package net.torantyr.agoge_survival.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torantyr.agoge_survival.agoge_survival;
import net.torantyr.agoge_survival.items.custom.SlingItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, agoge_survival.MODID);

    public static final RegistryObject<Item> SLING = ITEMS.register("sling",
            () -> new SlingItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
