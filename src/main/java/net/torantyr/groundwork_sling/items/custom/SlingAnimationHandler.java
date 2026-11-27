package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SlingAnimationHandler {

    // first person
    private static final RawAnimation FP_R_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.fp.spin_up").thenLoop("animation.sling.fp.spin_loop");
    private static final RawAnimation FP_R_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.fp.reset");
    private static final RawAnimation FP_L_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.fp_left.spin_up").thenLoop("animation.sling.fp_left.spin_loop");
    private static final RawAnimation FP_L_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.fp_left.reset");

    // third person
    private static final RawAnimation TP_R_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.tp.spin_up").thenLoop("animation.sling.tp.spin_loop");
    private static final RawAnimation TP_R_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.tp.reset");
    private static final RawAnimation TP_L_WHIRL = RawAnimation.begin()
            .thenPlay("animation.sling.tp_left.spin_up").thenLoop("animation.sling.tp_left.spin_loop");
    private static final RawAnimation TP_L_RESET = RawAnimation.begin()
            .thenPlay("animation.sling.tp_left.reset");

    public static PlayState handle(AnimationState<SlingItem> state) {
        ItemDisplayContext view = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        ItemStack stack = state.getData(DataTickets.ITEMSTACK);
        if (view == null || stack == null) {
            return PlayState.STOP;
        }

        RawAnimation whirl = whirlFor(view);
        RawAnimation reset = resetFor(view);
        if (whirl == null || reset == null) {
            return PlayState.STOP; // hotbar icon, dropped item, item frame: no animation
        }

        // being used right now: spin up, then loop
        if (isBeingUsed(stack, view.firstPerson())) {
            return state.setAndContinue(whirl);
        }

        // not in use: if it was whirling (or is mid-reset), play the reset, then stop
        AnimationController<SlingItem> controller = state.getController();
        RawAnimation current = controller.getCurrentRawAnimation();
        boolean wasWhirling = current == FP_R_WHIRL || current == FP_L_WHIRL
                || current == TP_R_WHIRL || current == TP_L_WHIRL;
        boolean resetting = (current == FP_R_RESET || current == FP_L_RESET
                || current == TP_R_RESET || current == TP_L_RESET)
                && controller.getAnimationState() != AnimationController.State.STOPPED;
        if (wasWhirling || resetting) {
            return state.setAndContinue(reset);
        }
        return PlayState.STOP;
    }

    // which spin-up and loop set to use for this view
    private static RawAnimation whirlFor(ItemDisplayContext view) {
        return switch (view) {
            case FIRST_PERSON_RIGHT_HAND -> FP_R_WHIRL;
            case FIRST_PERSON_LEFT_HAND -> FP_L_WHIRL;
            case THIRD_PERSON_RIGHT_HAND -> TP_R_WHIRL;
            case THIRD_PERSON_LEFT_HAND -> TP_L_WHIRL;
            default -> null;
        };
    }

    // which reset to use for this view
    private static RawAnimation resetFor(ItemDisplayContext view) {
        return switch (view) {
            case FIRST_PERSON_RIGHT_HAND -> FP_R_RESET;
            case FIRST_PERSON_LEFT_HAND -> FP_L_RESET;
            case THIRD_PERSON_RIGHT_HAND -> TP_R_RESET;
            case THIRD_PERSON_LEFT_HAND -> TP_L_RESET;
            default -> null;
        };
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