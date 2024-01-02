package com.skoow.relics_vivid_light.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig  {
    public static ForgeConfigSpec.ConfigValue<Integer>
            FORCEFIELD_PARTICLE_COUNT;

    public ClientConfig (ForgeConfigSpec.Builder builder) {
        FORCEFIELD_PARTICLE_COUNT = builder.comment("Forcefield Particle Count (DEF:10)")
                .comment("Particle count when drawing the sphere of Forcefield")
                .define("VAL", 10);
    }

    public static final ClientConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }
}