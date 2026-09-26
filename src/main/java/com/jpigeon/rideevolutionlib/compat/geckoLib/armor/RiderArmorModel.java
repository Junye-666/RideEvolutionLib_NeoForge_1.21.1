package com.jpigeon.rideevolutionlib.compat.geckoLib.armor;

import com.jpigeon.rideevolutionlib.compat.geckoLib.SimpleGeoModel;
import com.jpigeon.rideevolutionlib.compat.util.GeoResourcePaths;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;

public class RiderArmorModel extends SimpleGeoModel<BaseRiderArmorItem> {
    private GeoBone driver;
    private GeoBone body;
    private boolean resolved;

    public RiderArmorModel(GeoResourcePaths paths) {
        super(paths);
    }

    @Override
    public void setCustomAnimations(BaseRiderArmorItem animatable, long instanceId,
                                    AnimationState<BaseRiderArmorItem> state) {
        super.setCustomAnimations(animatable, instanceId, state);

        if (!resolved) {
            driver = getBone("driver").orElse(null);
            body = getBone("armorBody").orElse(null);
            resolved = true;
        }
        if (driver == null || body == null) return;

        Entity entity = state.getData(DataTickets.ENTITY);
        EquipmentSlot slot = state.getData(DataTickets.EQUIPMENT_SLOT);
        if (entity instanceof Player player && slot == EquipmentSlot.LEGS) {
            driver.setRotY(body.getRotY());
            driver.setRotZ(body.getRotZ());
            driver.setPosX(body.getPosX());
            driver.setRotX(body.getRotX());
            if (player.isCrouching()) {
                driver.setPosY(body.getPosY() + 1F);
                driver.setPosZ(body.getPosZ() - 0.5F);
            } else {
                driver.setPosY(body.getPosY());
                driver.setPosZ(body.getPosZ());
            }
        }
    }
}
