package net.torantyr.groundwork_sling;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue FIRE_CHARGES_ALLOWED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        FIRE_CHARGES_ALLOWED = builder
                .comment("Allow fire charges to be used as sling ammo. Admins can also change this in game.")
                .define("fireChargesAllowed", false);
        SPEC = builder.build();
    }
}