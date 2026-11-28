package net.torantyr.groundwork_sling.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.torantyr.groundwork_sling.ModCommonConfig;
import net.torantyr.groundwork_sling.groundwork_sling;

@Mod.EventBusSubscriber(modid = groundwork_sling.MODID)
public class SlingServerEvents {
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new FireChargesSyncPacket(ModCommonConfig.FIRE_CHARGES_ALLOWED.get()));
        }
    }
}