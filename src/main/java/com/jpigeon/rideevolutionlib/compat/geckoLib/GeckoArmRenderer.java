package com.jpigeon.rideevolutionlib.compat.geckoLib;

import com.jpigeon.ridebattlelib.common.api.RideBattleAPI;
import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.BaseKamenRiderArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@EventBusSubscriber(modid = RideEvolutionLib.MODID, value = Dist.CLIENT)
public class GeckoArmRenderer {

    // ========== 第一人称手臂渲染 ==========
    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent event) {
        if (!ModList.get().isLoaded("geckolib")) {
            return;
        }

        AbstractClientPlayer player = event.getPlayer();

        // 未变身 -> 不处理，使用原版手臂
        if (!RideBattleAPI.isTransformed(player)) {
            return;
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof BaseKamenRiderArmorItem armorItem)) {
            return;
        }

        // 获取 Gecko 盔甲渲染器（直接使用具体类型）
        GeoArmorRenderer<BaseKamenRiderArmorItem> renderer = getArmorRenderer(armorItem, player, chestStack);
        if (renderer == null) {
            return;
        }

        // 准备渲染参数
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        // 获取模型数据
        GeoModel<BaseKamenRiderArmorItem> geoModel = renderer.getGeoModel();
        BakedGeoModel bakedModel = geoModel.getBakedModel(geoModel.getModelResource(armorItem, renderer));
        net.minecraft.resources.ResourceLocation texture = renderer.getTextureLocation(armorItem);
        if (texture == null) {
            return;
        }

        // 开始渲染
        poseStack.pushPose();
        try {
            // 1. 隐藏除手臂外的所有骨骼
            hideAllBonesExceptArms(renderer);

            // 2. 根据当前渲染的手臂显隐对应骨骼
            HumanoidArm arm = event.getArm();
            GeoBone rightArmBone = geoModel.getBone("rightArm").orElse(null);
            GeoBone leftArmBone = geoModel.getBone("leftArm").orElse(null);

            if (rightArmBone != null) {
                rightArmBone.setHidden(arm != HumanoidArm.RIGHT);
            }
            if (leftArmBone != null) {
                leftArmBone.setHidden(arm != HumanoidArm.LEFT);
            }

            // 3. 如果安装了 PlayerAnimator 且不是第三人称模式，锁定手臂骨骼变换
            if (shouldLockArmBones(player)) {
                if (rightArmBone != null) {
                    rightArmBone.updateRotation(0, 0, 0);
                    rightArmBone.updatePosition(0, 0, 0);
                }
                if (leftArmBone != null) {
                    leftArmBone.updateRotation(0, 0, 0);
                    leftArmBone.updatePosition(0, 0, 0);
                }
            }

            // 4. 获取 VertexConsumer 和 RenderType
            RenderType renderType = RenderType.entityTranslucent(texture);
            VertexConsumer buffer = bufferSource.getBuffer(renderType);

            // 5. 执行 Gecko 渲染
            renderer.actuallyRender(
                    poseStack,
                    armorItem,
                    bakedModel,
                    renderType,
                    bufferSource,
                    buffer,
                    true,
                    partialTick,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    -1
            );

            // 6. 取消原版手臂渲染
            event.setCanceled(true);
        } finally {
            poseStack.popPose();
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 获取盔甲渲染器（直接从 BaseKamenRiderArmorItem 获取，类型安全）
     */
    private static GeoArmorRenderer<BaseKamenRiderArmorItem> getArmorRenderer(
            BaseKamenRiderArmorItem armorItem,
            AbstractClientPlayer player,
            ItemStack stack) {
        // 由于 BaseKamenRiderArmorItem 的 createGeoRenderer 已经注册了 GenericArmorRenderer
        // 我们可以通过 GeoRenderProvider 获取，然后强制转换
        var provider = software.bernie.geckolib.animatable.client.GeoRenderProvider.of(stack);
        HumanoidModel<?> model = provider.getGeoArmorRenderer(player, stack, EquipmentSlot.CHEST, null);
        if (model instanceof GeoArmorRenderer<?> genericRenderer) {
            // 安全强制转换：因为所有骑士盔甲都用 GenericArmorRenderer<BaseKamenRiderArmorItem>
            @SuppressWarnings("unchecked")
            GeoArmorRenderer<BaseKamenRiderArmorItem> renderer = (GeoArmorRenderer<BaseKamenRiderArmorItem>) genericRenderer;
            return renderer;
        }
        return null;
    }

    /**
     * 隐藏除手臂外的所有骨骼（用于 RenderArmEvent）
     */
    private static void hideAllBonesExceptArms(GeoArmorRenderer<?> renderer) {
        var model = renderer.getGeoModel();
        // 标准 Gecko 盔甲骨骼名称
        String[] toHide = {"head", "body", "armorBody", "rightLeg", "leftLeg", "rightBoot", "leftBoot"};
        for (String name : toHide) {
            model.getBone(name).ifPresent(bone -> bone.setHidden(true));
        }
    }

    /**
     * 判断是否需要锁定手臂骨骼变换（避免 PlayerAnimator 干扰）
     */
    private static boolean shouldLockArmBones(AbstractClientPlayer player) {
        if (!ModList.get().isLoaded("playeranimator")) return true;
        try {
            if (player instanceof IAnimatedPlayer animated) {
                var mode = animated.playerAnimLib$getAnimManager().getFirstPersonMode();
                return mode != FirstPersonMode.THIRD_PERSON_MODEL;
            }
        } catch (Exception ignored) {}
        return true;
    }
}
