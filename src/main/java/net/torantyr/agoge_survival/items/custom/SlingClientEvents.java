package net.torantyr.agoge_survival.items.custom;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torantyr.agoge_survival.agoge_survival;

@Mod.EventBusSubscriber(modid = agoge_survival.MODID, value = Dist.CLIENT)
public class SlingClientEvents {
    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        SlingClientExtensions.whirling = false; // the hand has already been drawn this frame
    }
}