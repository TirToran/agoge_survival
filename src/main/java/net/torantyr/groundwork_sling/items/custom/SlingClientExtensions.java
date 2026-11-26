package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SlingClientExtensions implements IClientItemExtensions {

    // =====================================================================
    // GECKOLIB: the sling's own spin, tilt and reset now come from the
    // animations in sling.animation.json. This class only hands GeckoLib
    // the renderer.
    // =====================================================================
    private SlingRenderer renderer;

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (this.renderer == null) {
            this.renderer = new SlingRenderer(); // created late, once the game is ready
        }
        return this.renderer;
    }


    // =====================================================================
    // THIRD PERSON ARM POSE: what OTHER players see, and what you see in F5.
    // The game asks getArmPose when it draws a player's model. This moves the
    // player's ARM, not the sling (the sling's own motion is GeckoLib's).
    // =====================================================================
    private static final float ACCEL = 1.6F;      // degrees per tick, per tick (arm sway speed-up)
    private static final float MAX_SPEED = 60.0F; // degrees per tick (full arm sway)

    private static final float TP_TILT_DEGREES = 0.0F;       // unused for now
    private static final float TP_ARM_RAISE = 3.6F;          // radians up from hanging: 1.57 is forward, 3.14 is straight up
    private static final float TP_ARM_OUT_DEGREES = -45.0F;  // arm swings out to the side, like a jumping jack
    private static final float TP_RADIUS = 0.2F;             // radians: how wide the hand circles

    private static final HumanoidModel.ArmPose SLING_POSE =
            HumanoidModel.ArmPose.create("GROUNDWORK_SLING", false, (model, entity, arm) -> {
                int side = arm == HumanoidArm.RIGHT ? 1 : -1;
                ModelPart part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;

                // how long the sling has been in use, with partial ticks for smoothness
                float t = entity.getTicksUsingItem() + Minecraft.getInstance().getFrameTime();

                // speeds up, then holds steady
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
            // return null;
        }
        return null; // not whirling, so use the normal hold pose
    }
}