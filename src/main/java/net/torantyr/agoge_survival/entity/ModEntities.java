package net.torantyr.agoge_survival.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torantyr.agoge_survival.agoge_survival;
import net.torantyr.agoge_survival.entity.custom.SlingProjectile;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, agoge_survival.MODID);

    public static final RegistryObject<EntityType<SlingProjectile>> SLING_PROJECTILE =
            ENTITY_TYPES.register("sling_projectile", () ->
                    EntityType.Builder.<SlingProjectile>of(SlingProjectile::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(new ResourceLocation(agoge_survival.MODID, "sling_projectile").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}