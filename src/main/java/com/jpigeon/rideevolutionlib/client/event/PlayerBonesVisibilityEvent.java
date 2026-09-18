package com.jpigeon.rideevolutionlib.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import software.bernie.geckolib.animatable.GeoItem;

/**
 * 在玩家变身状态下触发
 * <p>
 * 仅在客户端触发
 * <p>
 * 可用于外部修改玩家变身状态下某个骨骼可见性 (比如骑士人的脸部)
 */
@OnlyIn(Dist.CLIENT)
public class PlayerBonesVisibilityEvent extends Event {
    private final ResourceLocation riderId;
    private boolean headVisible = true;
    private boolean hatVisible = true;
    private boolean bodyVisible = true;
    private boolean rightArmVisible = true;
    private boolean leftArmVisible = true;
    private boolean rightLegVisible = true;
    private boolean leftLegVisible = true;
    private boolean leftSleeveVisible = true;
    private boolean rightSleeveVisible = true;
    private boolean leftPantsVisible = true;
    private boolean rightPantsVisible = true;
    private boolean jacketVisible = true;

    public PlayerBonesVisibilityEvent(Player player, ResourceLocation riderId) {
        this.riderId = riderId;
    }

    public static PlayerBonesVisibilityEvent fromPlayer(Player player, ResourceLocation riderId) {
        PlayerBonesVisibilityEvent event = new PlayerBonesVisibilityEvent(player, riderId);

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack boot = player.getItemBySlot(EquipmentSlot.FEET);

        boolean headGeo = head.getItem() instanceof GeoItem;
        boolean chestGeo = chest.getItem() instanceof GeoItem;
        boolean bootGeo = boot.getItem() instanceof GeoItem;

        event.headVisible = !headGeo;
        event.hatVisible = !headGeo;
        event.bodyVisible = !chestGeo;
        event.rightArmVisible = !chestGeo;
        event.leftArmVisible = !chestGeo;
        event.leftSleeveVisible = !chestGeo;
        event.rightSleeveVisible = !chestGeo;
        event.jacketVisible = !chestGeo;
        event.rightLegVisible = !bootGeo;
        event.leftLegVisible = !bootGeo;
        event.leftPantsVisible = !bootGeo;
        event.rightPantsVisible = !bootGeo;

        return event;
    }

    public boolean isHeadVisible() {
        return headVisible;
    }

    public void setHeadVisible(boolean headVisible) {
        this.headVisible = headVisible;
    }

    public boolean isHatVisible() {
        return hatVisible;
    }

    public void setHatVisible(boolean hatVisible) {
        this.hatVisible = hatVisible;
    }

    public boolean isBodyVisible() {
        return bodyVisible;
    }

    public void setBodyVisible(boolean bodyVisible) {
        this.bodyVisible = bodyVisible;
    }

    public boolean isRightArmVisible() {
        return rightArmVisible;
    }

    public void setRightArmVisible(boolean rightArmVisible) {
        this.rightArmVisible = rightArmVisible;
    }

    public boolean isLeftArmVisible() {
        return leftArmVisible;
    }

    public void setLeftArmVisible(boolean leftArmVisible) {
        this.leftArmVisible = leftArmVisible;
    }

    public boolean isRightLegVisible() {
        return rightLegVisible;
    }

    public void setRightLegVisible(boolean rightLegVisible) {
        this.rightLegVisible = rightLegVisible;
    }

    public boolean isLeftLegVisible() {
        return leftLegVisible;
    }

    public void setLeftLegVisible(boolean leftLegVisible) {
        this.leftLegVisible = leftLegVisible;
    }

    public boolean isLeftSleeveVisible() {
        return leftSleeveVisible;
    }

    public void setLeftSleeveVisible(boolean leftSleeveVisible) {
        this.leftSleeveVisible = leftSleeveVisible;
    }

    public boolean isRightSleeveVisible() {
        return rightSleeveVisible;
    }

    public void setRightSleeveVisible(boolean rightSleeveVisible) {
        this.rightSleeveVisible = rightSleeveVisible;
    }

    public boolean isLeftPantsVisible() {
        return leftPantsVisible;
    }

    public void setLeftPantsVisible(boolean leftPantsVisible) {
        this.leftPantsVisible = leftPantsVisible;
    }

    public boolean isRightPantsVisible() {
        return rightPantsVisible;
    }

    public void setRightPantsVisible(boolean rightPantsVisible) {
        this.rightPantsVisible = rightPantsVisible;
    }

    public boolean isJacketVisible() {
        return jacketVisible;
    }

    public void setJacketVisible(boolean jacketVisible) {
        this.jacketVisible = jacketVisible;
    }

    public ResourceLocation getRiderId() {
        return riderId;
    }
}
