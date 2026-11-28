package net.torantyr.groundwork_sling.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torantyr.groundwork_sling.SlingRules;
import net.torantyr.groundwork_sling.groundwork_sling;

@Mod.EventBusSubscriber(modid = groundwork_sling.MODID, value = Dist.CLIENT)
public class SlingClientNetworkEvents {
    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        SlingRules.setSyncedFireCharges(null);
    }
}