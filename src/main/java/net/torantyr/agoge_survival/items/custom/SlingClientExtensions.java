package net.torantyr.agoge_survival.items.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SlingClientExtensions implements IClientItemExtensions {
    private static final float ACCEL = 1.6F;      // degrees per tick, per tick (spin-up speed)
    private static final float MAX_SPEED = 60.0F; // degrees per tick (full spin)
    private static final float SETTLE = 0.2F;     // how fast the reset eases to rest

    private float angle = 0.0F; // current spin angle in degrees
    private float speed = 0.0F; // current spin speed

    public static boolean whirling = false;

    private static final float TILT_DEGREES = -90.0F; // how far the spin plane leans
    private float tilt = 0.0F;                       // 0 = flat, 1 = full tilt

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                           ItemStack itemInHand, float partialTick,
                                           float equipProcess, float swingProcess) {
        float dt = Minecraft.getInstance().getDeltaFrameTime(); // time since last frame, in ticks
        boolean spinning = player.isUsingItem() && player.getUseItem().is(itemInHand.getItem());

        if (spinning) {
            speed = Math.min(speed + ACCEL * dt, MAX_SPEED); // spin-up, then steady spin
            angle += speed * dt;
            tilt += ((spinning ? 1.0F : 0.0F) - tilt) * Math.min(1.0F, 0.15F * dt);
        } else if (angle != 0.0F) {
            speed = 0.0F;
            float target = (float) Math.ceil(angle / 360.0F) * 360.0F; // finish the current turn
            angle += (target - angle) * Math.min(1.0F, SETTLE * dt);
            if (target - angle < 0.5F) {
                angle = 0.0F; // back at rest
            }
        }

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F); // normal hand position
        poseStack.translate(0.0F, 0.2F, 0.0F);                                   // move to the spin pivot
        poseStack.mulPose(Axis.YP.rotationDegrees(TILT_DEGREES * tilt));  // lean the spin plane
        poseStack.mulPose(Axis.ZP.rotationDegrees(-angle * side));                // the whirl
        poseStack.translate(0.0F, -0.2F, 0.0F);                                  // move back
        whirling = spinning || angle != 0.0F;
        return true; // we handled positioning, so skip the default transform
    }
}