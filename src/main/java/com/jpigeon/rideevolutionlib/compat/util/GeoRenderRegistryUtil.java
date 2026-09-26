package com.jpigeon.rideevolutionlib.compat.util;

import com.jpigeon.rideevolutionlib.compat.geckoLib.SimpleGeoModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.RiderArmorModel;
import com.jpigeon.rideevolutionlib.compat.geckoLib.block.BaseRiderGeoBlockEntity;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.BaseRiderEffectEntity;
import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.RiderEffectRenderer;
import com.jpigeon.rideevolutionlib.compat.geckoLib.item.BaseRiderGeoItem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeoRenderRegistryUtil {
    private static final Map<String, GeoArmorRenderer<?>> ARMOR_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, GeoItemRenderer<?>> ITEM_CACHE = new ConcurrentHashMap<>();

    // ==== 效果实体 ====
    public static <T extends BaseRiderEffectEntity> RiderEffectRenderer<T> createEffectRenderer(
            EntityRendererProvider.Context ctx,
            String modId, String riderName, String entityName) {
        return new RiderEffectRenderer<>(ctx,
                new SimpleGeoModel<T>(GeoResourcePaths.entity(modId, riderName, entityName)));
    }

    // ==== 方块实体 ====
    public static GeoBlockRenderer<BaseRiderGeoBlockEntity> createBlockRenderer(
            String modId, String riderName, String blockName) {
        return new GeoBlockRenderer<BaseRiderGeoBlockEntity>(
                new SimpleGeoModel<>(
                        GeoResourcePaths.block(modId, riderName, blockName, true)));
    }

    // ==== 盔甲：直接用 GeoArmorRenderer + RiderArmorModel ====
    public static GeoArmorRenderer<?> createArmorRenderer(String modId, String riderName, String formName, boolean animated) {
        String key = modId + "/" + riderName + "/" + formName + "/" + animated;
        return ARMOR_CACHE.computeIfAbsent(key, k -> {
            RiderArmorModel model = new RiderArmorModel(
                    GeoResourcePaths.armor(modId, riderName, formName, animated));
            return new GeoArmorRenderer<>(model);
        });
    }

    // ==== 物品：直接用 GeoItemRenderer ====
    public static GeoItemRenderer<?> createItemRenderer(String modId, String riderName, String itemName, boolean animated) {
        String key = modId + "/" + riderName + "/" + itemName + "/" + animated;
        return ITEM_CACHE.computeIfAbsent(key, k -> {
            SimpleGeoModel<BaseRiderGeoItem> model = new SimpleGeoModel<>(
                    GeoResourcePaths.item(modId, riderName, itemName, animated));
            return new GeoItemRenderer<>(model);
        });
    }
}
