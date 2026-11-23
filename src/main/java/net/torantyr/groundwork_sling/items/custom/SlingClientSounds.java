package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.torantyr.groundwork_sling.ModClientConfig;
import net.torantyr.groundwork_sling.sound.ModSounds;

public class SlingClientSounds {
    public static void onUseTick(Player player, int ticksHeld) {
        if (!ModClientConfig.SLING_SOUNDS.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (ticksHeld == 0) {
            mc.getSoundManager().play(
                    new SlingSoundInstance(player, ModSounds.SLING_SPINNING_UP.get(), false));
        } else if (ticksHeld == SlingItem.CRIT_TICKS) {
            mc.getSoundManager().play(
                    new SlingSoundInstance(player, ModSounds.SLING_SPINNING_MAX.get(), true));
        }
    }
}