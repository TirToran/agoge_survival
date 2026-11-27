package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SlingAnimationHandler {

    private static final RawAnimation FP_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.fp.spin_up").thenLoop("animation.sling.fp.spin_loop");
    private static final RawAnimation FP_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.fp.reset");
    private static final RawAnimation TP_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.tp.spin_up").thenLoop("animation.sling.tp.spin_loop");
    private static final RawAnimation TP_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.tp.reset");

    public static PlayState handle(AnimationState<SlingItem> state) {
        ItemDisplayContext view = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        ItemStack stack = state.getData(DataTickets.ITEMSTACK);
        if (view == null || stack == null) {
            return PlayState.STOP;
        }

        boolean firstPerson = view.firstPerson();
        boolean thirdPerson = view == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || view == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        if (!firstPerson && !thirdPerson) {
            return PlayState.STOP; // hotbar icon, dropped item, item frame: no animation
        }

        // being used right now: spin up, then loop
        if (isBeingUsed(stack, firstPerson)) {
            return state.setAndContinue(firstPerson ? FP_WHIRL : TP_WHIRL);
        }

        // not in use: if it was whirling (or is mid-reset), play the reset, then stop
        AnimationController<SlingItem> controller = state.getController();
        RawAnimation current = controller.getCurrentRawAnimation();
        boolean wasWhirling = current == FP_WHIRL || current == TP_WHIRL;
        boolean resetting = (current == FP_RESET || current == TP_RESET)
                && controller.getAnimationState() != AnimationController.State.STOPPED;
        if (wasWhirling || resetting) {
            return state.setAndContinue(firstPerson ? FP_RESET : TP_RESET);
        }
        return PlayState.STOP;
    }

    private static boolean isBeingUsed(ItemStack stack, boolean firstPerson) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        if (firstPerson) {
            Player me = mc.player; // first person is always your own hand
            return me != null && me.isUsingItem() && me.getUseItem().getItem() instanceof SlingItem;
        }
        for (Player p : mc.level.players()) { // third person: find who is holding this exact stack
            if (p.isUsingItem() && p.getUseItem() == stack) {
                return true;
            }
        }
        return false;
    }
}