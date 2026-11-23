package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class SlingSoundInstance extends AbstractTickableSoundInstance {
    private final Player player;

    public SlingSoundInstance(Player player, SoundEvent sound, boolean loop) {
        super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = loop;
        this.delay = 0;
        this.volume = 0.7F;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
        if (player.isRemoved() || !player.isUsingItem()
                || !(player.getUseItem().getItem() instanceof SlingItem)) {
            this.stop(); // released, died, or switched items
            return;
        }
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}