package com.jpigeon.rideevolutionlib;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue FIRST_PERSON_ARM_RENDER;

    static {
        FIRST_PERSON_ARM_RENDER = BUILDER
                .comment("渲染第一人称盔甲")
                .define("firstPersonArmRender", true);
    }
    static final ModConfigSpec SPEC = BUILDER.build();

}
