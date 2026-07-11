package com.jpigeon.rideevolutionlib.compat.geckoLib;

import com.jpigeon.ridebattlelib.common.api.RideBattleAPI;
import com.jpigeon.rideevolutionlib.Config;
import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.BaseKamenRiderArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Arrays;

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
        GeoArmorRenderer<BaseKamenRiderArmorItem> renderer = getArmorRenderer(player, chestStack);
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


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        boolean hasInvisible = player.getActiveEffects().stream().anyMatch(mobEffectInstance -> mobEffectInstance.is(MobEffects.INVISIBILITY));
        if (hasInvisible) return;
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        if (isFirstPerson) {
            player.setInvisible(false);
            return;
        }
        if (RideBattleAPI.isTransformed(player) && !player.isInvisible()) {
            player.setInvisible(true);
        } else if (!RideBattleAPI.isTransformed(player) && player.isInvisible()) {
            player.setInvisible(false);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (!Config.FIRST_PERSON_ARM_RENDER.get()) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) {
            return;
        }

        if (!RideBattleAPI.isTransformed(player) || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof BaseKamenRiderArmorItem armorItem)) {
            return;
        }

        GeoArmorRenderer<BaseKamenRiderArmorItem> renderer = getArmorRenderer(player, chestStack);
        if (renderer == null) {
            return;
        }

        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();
        int packedLight = 15728880; // 全亮
        float partialTick = event.getRenderTick();

        GeoModel<BaseKamenRiderArmorItem> geoModel = renderer.getGeoModel();
        BakedGeoModel bakedModel = geoModel.getBakedModel(geoModel.getModelResource(armorItem, renderer));
        ResourceLocation texture = renderer.getTextureLocation(armorItem);
        if (texture == null) {
            return;
        }

        // --- 构建相机空间的 PoseStack ---
        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();
        try {
            Camera camera = mc.gameRenderer.getMainCamera();
            // 应用相机旋转（使手臂跟随视角）
            poseStack.mulPose(camera.rotation());

            // 应用第一人称手臂变换
            HumanoidArm mainArm = player.getMainArm();
            applyFirstPersonTransform(poseStack, player, mainArm);

            // 隐藏非手臂骨骼
            hideAllBonesExceptArms(renderer);

            // 设置手臂骨骼可见性（只显示主手对应的手臂）
            GeoBone rightArmBone = renderer.getRightArmBone(geoModel);
            GeoBone leftArmBone = renderer.getLeftArmBone(geoModel);
            if (rightArmBone != null) {
                rightArmBone.setHidden(mainArm != HumanoidArm.RIGHT);
                rightArmBone.updateRotation(0, 0, 0);
                rightArmBone.updatePosition(0, 0, 0);
            }
            if (leftArmBone != null) {
                leftArmBone.setHidden(mainArm != HumanoidArm.LEFT);
                leftArmBone.updateRotation(0, 0, 0);
                leftArmBone.updatePosition(0, 0, 0);
            }

            // 渲染
            RenderType renderType = RenderType.entityTranslucent(texture);
            VertexConsumer buffer = bufferSource.getBuffer(renderType);
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
        } finally {
            poseStack.popPose();
        }

    }

    // ========== 辅助方法 ==========

    private static void applyFirstPersonTransform(PoseStack poseStack, AbstractClientPlayer player, HumanoidArm arm) {
        poseStack.translate(0.0F, 0.0F, 10F);
    }

    // ========== 辅助方法 ==========
    private static GeoArmorRenderer<BaseKamenRiderArmorItem> getArmorRenderer(
            AbstractClientPlayer player,
            ItemStack stack) {
        GeoRenderProvider provider = GeoRenderProvider.of(stack);
        HumanoidModel<AbstractClientPlayer> model = (HumanoidModel<AbstractClientPlayer>) provider.getGeoArmorRenderer(player, stack, EquipmentSlot.CHEST, null);
        if (model instanceof GeoArmorRenderer<?> genericRenderer) {
            @SuppressWarnings("unchecked")
            GeoArmorRenderer<BaseKamenRiderArmorItem> renderer = (GeoArmorRenderer<BaseKamenRiderArmorItem>) genericRenderer;
            return renderer;
        }
        return null;
    }

    private static void hideAllBonesExceptArms(GeoArmorRenderer<BaseKamenRiderArmorItem> renderer) {
        GeoModel<BaseKamenRiderArmorItem> model = renderer.getGeoModel();
        GeoBone head = renderer.getHeadBone(model);
        GeoBone body = renderer.getBodyBone(model);
        GeoBone rightLeg = renderer.getRightLegBone(model);
        GeoBone rightBoot = renderer.getRightBootBone(model);
        GeoBone leftLeg = renderer.getLeftLegBone(model);
        GeoBone leftBoot = renderer.getLeftBootBone(model);

        for (GeoBone bone : Arrays.asList(head, body, rightLeg, rightBoot, leftLeg, leftBoot)) {
            if (bone != null) {
                bone.setHidden(true);
            }
        }
        model.getBone("armorBody").ifPresent(bone -> bone.setHidden(true));
    }
}
