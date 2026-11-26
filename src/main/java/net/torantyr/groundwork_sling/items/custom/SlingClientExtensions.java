package net.torantyr.groundwork_sling.items.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SlingClientExtensions implements IClientItemExtensions {

    // =====================================================================
    // SHARED: used by both first person and third person
    // =====================================================================
    private static final float ACCEL = 1.6F;      // degrees per tick, per tick (spin-up speed)
    private static final float MAX_SPEED = 60.0F; // degrees per tick (full spin)


    // =====================================================================
    // FIRST PERSON: what YOU see of your own sling.
    // The game calls applyForgeHandTransform every frame for your own hand.
    // =====================================================================
    private static final float SETTLE = 0.2F;         // how fast the reset eases to rest
    private static final float TILT_DEGREES = -90.0F; // how far the spin plane leans

    private float angle = 0.0F; // current spin angle in degrees
    private float speed = 0.0F; // current spin speed
    private float tilt = 0.0F;  // 0 = flat, 1 = full tilt

    // Read by the item model property (the texture swap) and by SlingClientEvents
    public static boolean whirling = false;

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                           ItemStack itemInHand, float partialTick,
                                           float equipProcess, float swingProcess) {
        float dt = Minecraft.getInstance().getDeltaFrameTime(); // time since last frame, in ticks
        boolean spinning = player.isUsingItem() && player.getUseItem().is(itemInHand.getItem());

        if (spinning) {
            speed = Math.min(speed + ACCEL * dt, MAX_SPEED); // spin-up, then steady spin
            angle += speed * dt;
        } else if (angle != 0.0F) {
            speed = 0.0F;
            float target = (float) Math.ceil(angle / 360.0F) * 360.0F; // finish the current turn
            angle += (target - angle) * Math.min(1.0F, SETTLE * dt);
            if (target - angle < 0.5F) {
                angle = 0.0F; // back at rest
            }
        }

        // ease the tilt in while spinning and back out afterwards
        tilt += ((spinning ? 1.0F : 0.0F) - tilt) * Math.min(1.0F, 0.15F * dt);
        if (!spinning && tilt < 0.02F) {
            tilt = 0.0F; // fully flat once it's close, so it doesn't linger
        }

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F); // normal hand position

        // the vanilla left-click swing, only when not whirling
        if (!spinning && swingProcess > 0.0F) {
            float f = Mth.sin(swingProcess * swingProcess * (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + f * -20.0F)));
            float f1 = Mth.sin(Mth.sqrt(swingProcess) * (float) Math.PI);
         //   poseStack.mulPose(Axis.ZP.rotationDegrees(side * f1 * -20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(f1 * -80.0F));
         //   poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));
        }

        poseStack.translate(0.0F, 0.2F, 0.0F);                           // move to the spin pivot
       // poseStack.mulPose(Axis.YP.rotationDegrees(TILT_DEGREES * tilt)); // lean the spin plane
       // poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));       // the whirl
        poseStack.translate(0.0F, -0.2F, 0.0F);                          // move back

        whirling = spinning || angle != 0.0F || tilt > 0.0F;
        return true; // we handled positioning, so skip the default transform
    }


    // =====================================================================
    // THIRD PERSON: what OTHER players see, and what you see in F5 view.
    // The game asks getArmPose when it draws a player's model.
    // =====================================================================
    private static final float TP_TILT_DEGREES = 0.0F; // lean of the circle, 0 for now
    private static final float TP_ARM_RAISE = 3.6F;    // radians up from hanging: 1.57 is forward, 3.14 is straight up
    private static final float TP_ARM_OUT_DEGREES = -45.0F; // arm swings out to the side, like a jumping jack
    private static final float TP_RADIUS = 0.2F;       // radians: how wide the hand circles

    private static final HumanoidModel.ArmPose SLING_POSE =
            HumanoidModel.ArmPose.create("GROUNDWORK_SLING", false, (model, entity, arm) -> {
                int side = arm == HumanoidArm.RIGHT ? 1 : -1;
                ModelPart part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;

                // how long the sling has been in use, with partial ticks for smoothness
                float t = entity.getTicksUsingItem() + Minecraft.getInstance().getFrameTime();

                // same spin-up as first person: speeds up, then holds steady
                float rampTicks = MAX_SPEED / ACCEL;
                float degrees = t < rampTicks
                        ? 0.5F * ACCEL * t * t
                        : 0.5F * ACCEL * rampTicks * rampTicks + MAX_SPEED * (t - rampTicks);
                float phi = (float) Math.toRadians(degrees);

                part.xRot = -TP_ARM_RAISE + TP_RADIUS * Mth.cos(phi);
                part.zRot = side * ((float) Math.toRadians(TP_ARM_OUT_DEGREES) + TP_RADIUS * Mth.sin(phi));
                part.yRot = 0.0F;
            });

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (!stack.isEmpty() && entity.isUsingItem()
                && entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
            return SLING_POSE;
        }
        return null; // not whirling, so use the normal hold pose
    }

    // THIS IS FOR THE GECKOLIB Animations to hand over the renderer
    private SlingRenderer renderer;

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (this.renderer == null) {
            this.renderer = new SlingRenderer(); // created late, once the game is ready
        }
        return this.renderer;
    }
}