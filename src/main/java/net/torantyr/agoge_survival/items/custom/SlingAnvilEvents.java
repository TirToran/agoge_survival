package net.torantyr.agoge_survival.items.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torantyr.agoge_survival.agoge_survival;

@Mod.EventBusSubscriber(modid = agoge_survival.MODID)
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