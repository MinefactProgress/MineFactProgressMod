package de.jannik0308.minefactprogressmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SettingsConfig {

    public static ForgeConfigSpec.BooleanValue enableAutoGamemode;
    public static ForgeConfigSpec.BooleanValue enableAutoSpeed;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.comment("MineFact Progress Settings Config");

        enableAutoGamemode = builder
                .comment("Enable whether the gamemode should be set to creative when joining the New York City Server")
                .define("city_server.enable_AutoGamemode", true);
        enableAutoSpeed = builder
                .comment("Enable whether the speed should be set to 10 when joining the New York City Server")
                .define("city_server.enable_AutoSpeed", true);
    }

}
