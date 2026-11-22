package net.torantyr.agoge_survival;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> SLING_AMMO = mod("sling_ammo");
        public static final TagKey<Item> COPPER_NUGGETS = forge("nuggets/copper");
        public static final TagKey<Item> TIN_NUGGETS = forge("nuggets/tin");
        public static final TagKey<Item> STEEL_NUGGETS = forge("nuggets/steel");

        private static TagKey<Item> mod(String name) {
            return ItemTags.create(new ResourceLocation(agoge_survival.MODID, name));
        }

        private static TagKey<Item> forge(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }
}