package de.jannik0308.minefactprogressmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SettingsConfig {

    public static ForgeConfigSpec.BooleanValue enableAutoGamemode;
    public static ForgeConfigSpec.IntValue autoSpeedValue;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.comment("MineFact Progress Settings Config");

        enableAutoGamemode = builder
                .comment("Enable whether the gamemode should be set to creative when joining the New York City Server")
                .define("city_server.enable_AutoGamemode", true);
        autoSpeedValue = builder
                .comment("Set the value the speed should be set to when joining the New York City Server")
                .defineInRange("city_server.value_AutoSpeed", 10, 1, 10);
    }

}
