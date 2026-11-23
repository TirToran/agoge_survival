package net.torantyr.groundwork_sling.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torantyr.groundwork_sling.groundwork_sling;
import net.torantyr.groundwork_sling.items.custom.SlingItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, groundwork_sling.MODID);

    public static final RegistryObject<Item> SLING = ITEMS.register("sling",
            () -> new SlingItem(new Item.Properties().durability(479)));

    public static final RegistryObject<Item> NETHERITE_BULLET =
            ITEMS.register("netherite_bullet", () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
