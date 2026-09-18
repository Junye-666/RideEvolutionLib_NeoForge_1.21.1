package com.jpigeon.rideevolutionlib;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue FIRST_PERSON_ARM_RENDER;
    public static final ModConfigSpec.BooleanValue FALLBACK_HENSHIN_RENDER_MODE;

    static {
        FIRST_PERSON_ARM_RENDER = BUILDER
                .comment("第一人称下用 GeoArmorRenderer 重定向渲染手臂盔甲")
                .define("firstPersonArmRender", true);

        FALLBACK_HENSHIN_RENDER_MODE = BUILDER
                .comment("后备变身渲染模式（玩家隐身）")
                .define("fallbackHenshinRenderMode", false);
    }
    static final ModConfigSpec SPEC = BUILDER.build();

}
