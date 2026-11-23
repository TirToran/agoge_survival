package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torantyr.groundwork_sling.groundwork_sling;

@Mod.EventBusSubscriber(modid = groundwork_sling.MODID)
public class SlingAnvilEvents {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!(event.getLeft().getItem() instanceof SlingItem)) {
            return; // only care about slings
        }
        ItemStack right = event.getRight();
        if (EnchantmentHelper.getEnchantments(right).containsKey(Enchantments.INFINITY_ARROWS)) {
            event.setCanceled(true); // no output slot result, so the book can't be applied
        }
    }
}