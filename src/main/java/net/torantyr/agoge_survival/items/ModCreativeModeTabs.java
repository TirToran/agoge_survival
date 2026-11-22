package net.torantyr.agoge_survival.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.torantyr.agoge_survival.agoge_survival;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
         DeferredRegister.create(Registries.CREATIVE_MODE_TAB, agoge_survival.MODID);

    // The order these are added is the order that they will appear in the creative mode tab
    public static final RegistryObject<CreativeModeTab> AGOGE_TAB = CREATIVE_MODE_TABS.register("agoge_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SLING.get()))
                    .title(Component.translatable("creativetab.agoge_tab"))
                    .displayItems((pParameters, pOutput) -> {
                       pOutput.accept(ModItems.SLING.get());
                       pOutput.accept(ModItems.NETHERITE_BULLET.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
