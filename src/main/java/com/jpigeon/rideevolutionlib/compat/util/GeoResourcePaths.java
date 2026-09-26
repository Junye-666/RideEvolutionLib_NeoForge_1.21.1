package com.jpigeon.rideevolutionlib.compat.util;

import net.minecraft.resources.ResourceLocation;

/**
 * 一个 Geo 对象的三个资源路径（模型 / 纹理 / 动画）。
 * <p>用 record 记录，替代 4 个 Base 类里重复的 {@code getModelPath()/getTexturePath()/getAnimationPath()}。
 */
public record GeoResourcePaths(
        ResourceLocation model,
        ResourceLocation texture,
        ResourceLocation animation
) {
    private static ResourceLocation rl(String mod, String path) {
        return ResourceLocation.fromNamespaceAndPath(mod, path);
    }

    public static GeoResourcePaths armor(String mod, String rider, String form, boolean animated) {
        String r = rider.toLowerCase();
        String f = form.toLowerCase();
        return new GeoResourcePaths(
                rl(mod, "geo/" + r + "/armor/" + r + "_" + f + ".geo.json"),
                rl(mod, "textures/armor/" + r + "/" + r + "_" + f + ".png"),
                animated
                        ? rl(mod, "animations/" + r + "/" + r + "_" + f + ".animation.json")
                        : rl(mod, "animations/no_armor.animation.json")
        );
    }

    public static GeoResourcePaths item(String mod, String rider, String item, boolean animated) {
        String r = rider.toLowerCase();
        String i = item.toLowerCase();
        return new GeoResourcePaths(
                rl(mod, "geo/" + r + "/item/" + r + "_" + i + ".geo.json"),
                rl(mod, "textures/item/" + r + "/geo_item/" + r + "_" + i + ".png"),
                animated
                        ? rl(mod, "animations/" + r + "/item/" + r + "_" + i + ".animation.json")
                        : rl(mod, "animations/no_item.animation.json")
        );
    }

    public static GeoResourcePaths entity(String mod, String rider, String entity) {
        String r = rider.toLowerCase();
        String e = entity.toLowerCase();
        return new GeoResourcePaths(
                rl(mod, "geo/" + r + "/entity/" + e + ".geo.json"),
                rl(mod, "textures/entity/" + r + "/" + e + ".png"),
                rl(mod, "animations/" + r + "/entity/" + e + ".animation.json")
        );
    }

    public static GeoResourcePaths block(String mod, String rider, String block, boolean animated) {
        String r = rider.toLowerCase();
        String b = block.toLowerCase();
        return new GeoResourcePaths(
                rl(mod, "geo/" + r + "/block/" + b + ".geo.json"),
                rl(mod, "textures/block/" + r + "/" + b + ".png"),
                animated
                        ? rl(mod, "animations/" + r + "/block/" + b + ".animation.json")
                        : rl(mod, "animations/no_block.animation.json")
        );
    }
}
