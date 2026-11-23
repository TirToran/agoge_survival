package net.torantyr.groundwork_sling.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torantyr.groundwork_sling.groundwork_sling;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, groundwork_sling.MODID);

    public static final RegistryObject<SoundEvent> SLING_SPINNING_UP = register("sling_spinning_up");
    public static final RegistryObject<SoundEvent> SLING_SPINNING_MAX = register("sling_spinning_max");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(groundwork_sling.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}