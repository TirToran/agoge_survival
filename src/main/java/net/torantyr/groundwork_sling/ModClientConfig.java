package net.torantyr.groundwork_sling;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SLING_SOUNDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SLING_SOUNDS = builder.comment("Play the sling spinning sounds").define("slingSounds", true);
        SPEC = builder.build();
    }
}