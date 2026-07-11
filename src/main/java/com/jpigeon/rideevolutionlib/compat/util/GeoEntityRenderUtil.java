package com.jpigeon.rideevolutionlib.compat.util;

import com.jpigeon.rideevolutionlib.compat.geckoLib.block.GenericBlockEntityModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.block.GenericBlockEntityRenderer;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.BaseKamenRiderEffectEntity;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.RiderEffectModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.RiderEffectRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GeoEntityRenderUtil {

    /**
     * 创建效果实体的渲染器（支持透明度）
     * @param context 渲染器上下文
     * @param modId 你的模组ID
     * @param riderName 骑士名称（用于路径）
     * @param entityName 实体名称（用于路径）
     * @return 渲染器实例
     */
    public static RiderEffectRenderer<BaseKamenRiderEffectEntity> createEffectRenderer(
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
}
