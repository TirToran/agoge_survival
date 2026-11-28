package net.torantyr.groundwork_sling;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SLING_SOUNDS;
    public static final ForgeConfigSpec.DoubleValue SOUND_VOLUME;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SLING_SOUNDS = builder.comment("Play the sling spinning sounds").define("slingSounds", true);
        SOUND_VOLUME = builder.comment("Volume of the sling spinning sounds, 0.0 to 1.0")
                .defineInRange("slingVolume", 1.0, 0.0, 1.0);
        SPEC = builder.build();
    }
}