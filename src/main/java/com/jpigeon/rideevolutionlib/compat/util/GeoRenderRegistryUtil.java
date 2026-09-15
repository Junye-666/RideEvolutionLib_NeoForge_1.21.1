package com.jpigeon.rideevolutionlib.compat.util;

import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.GenericArmorModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.GenericArmorRenderer;
import com.jpigeon.rideevolutionlib.compat.geckoLib.block.GenericBlockEntityModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.block.GenericBlockEntityRenderer;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.BaseRiderEffectEntity;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.RiderEffectModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.RiderEffectRenderer;
import com.jpigeon.rideevolutionlib.compat.geckoLib.item.GenericItemModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.item.GenericItemRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeoRenderRegistryUtil {
    private static final Map<String, GenericArmorRenderer> ARMOR_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, GenericItemRenderer>  ITEM_CACHE  = new ConcurrentHashMap<>();

    /**
     * 创建效果实体的渲染器（支持透明度）
     * @param context 渲染器上下文
     * @param modId 你的模组ID
     * @param riderName 骑士名称（用于路径）
     * @param entityName 实体名称（用于路径）
     * @return 渲染器实例
     */
    public static RiderEffectRenderer<BaseRiderEffectEntity> createEffectRenderer(
            EntityRendererProvider.Context context,
            String modId,
            String riderName,
            String entityName) {

        return new RiderEffectRenderer<>(
                context,
                new RiderEffectModel<>(
                        entityModelPath(modId, riderName, entityName),
                        entityTexturePath(modId, riderName, entityName),
                        entityAnimationPath(modId, riderName, entityName)
                )
        );
    }

    /**
     * 创建方块实体渲染器
     */
    public static GenericBlockEntityRenderer createBlockRenderer(
            BlockEntityRendererProvider.Context context,
            String modId,
            String riderName,
            String blockName) {

        return new GenericBlockEntityRenderer(
                context,
                new GenericBlockEntityModel(
                        blockModelPath(modId, riderName, blockName),
                        blockTexturePath(modId, riderName, blockName),
                        blockAnimationPath(modId, riderName, blockName)
                )
        );
    }

    /**
     * 创建盔甲渲染器
     */
    public static GenericArmorRenderer createArmorRenderer(
            String modId,
            String riderName,
            String formName,
            boolean animated) {

        String key = modId + "/" + riderName + "/" + formName + "/" + animated;
        return ARMOR_CACHE.computeIfAbsent(key, k -> {
            GenericArmorModel model = new GenericArmorModel(
                    armorModelPath(modId, riderName, formName),
                    armorTexturePath(modId, riderName, formName),
                    armorAnimationPath(modId, riderName, formName, animated));
            return new GenericArmorRenderer(model);
        });
    }

    /**
     * 创建物品渲染器
     */
    public static GenericItemRenderer createItemRenderer(
            String modId,
            String riderName,
            String itemName,
            boolean animated) {

        String key = modId + "/" + riderName + "/" + itemName + "/" + animated;
        return ITEM_CACHE.computeIfAbsent(key, k -> {
            GenericItemModel model = new GenericItemModel(
                    itemModelPath(modId, riderName, itemName),
                    itemTexturePath(modId, riderName, itemName),
                    itemAnimationPath(modId, riderName, itemName, animated));
            return new GenericItemRenderer(model);
        });
    }

    // ========== 内部路径生成方法 ==========

    private static ResourceLocation entityModelPath(String modId, String riderName, String entityName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/entity/" + entityName.toLowerCase() + ".geo.json");
    }

    private static ResourceLocation entityTexturePath(String modId, String riderName, String entityName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/entity/" + riderName.toLowerCase() + "/" + entityName.toLowerCase() + ".png");
    }

    private static ResourceLocation entityAnimationPath(String modId, String riderName, String entityName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/" + riderName.toLowerCase() + "/entity/" + entityName.toLowerCase() + ".animation.json");
    }

    private static ResourceLocation blockModelPath(String modId, String riderName, String blockName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/block/" + blockName.toLowerCase() + ".geo.json");
    }

    private static ResourceLocation blockTexturePath(String modId, String riderName, String blockName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/block/" + riderName.toLowerCase() + "/" + blockName.toLowerCase() + ".png");
    }

    private static ResourceLocation blockAnimationPath(String modId, String riderName, String blockName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/" + riderName.toLowerCase() + "/block/" + blockName.toLowerCase() + ".animation.json");
    }

    private static ResourceLocation armorModelPath(String modId, String riderName, String formName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/armor/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".geo.json");
    }
    private static ResourceLocation armorTexturePath(String modId, String riderName, String formName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/armor/" + riderName.toLowerCase() + "/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".png");
    }
    private static ResourceLocation armorAnimationPath(String modId, String riderName, String formName, boolean animated) {
        if (animated) {
            return ResourceLocation.fromNamespaceAndPath(modId,
                    "animations/" + riderName.toLowerCase() + "/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".animation.json");
        }
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/no_armor.animation.json");
    }

    private static ResourceLocation itemModelPath(String modId, String riderName, String itemName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".geo.json");
    }
    private static ResourceLocation itemTexturePath(String modId, String riderName, String itemName) {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/item/" + riderName.toLowerCase() + "/geo_item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".png");
    }
    private static ResourceLocation itemAnimationPath(String modId, String riderName, String itemName, boolean animated) {
        if (animated) {
            return ResourceLocation.fromNamespaceAndPath(modId,
                    "animations/" + riderName.toLowerCase() + "/item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".animation.json");
        }
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/no_item.animation.json");
    }
}
