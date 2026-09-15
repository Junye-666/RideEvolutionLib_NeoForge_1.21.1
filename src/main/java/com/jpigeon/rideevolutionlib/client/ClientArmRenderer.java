package com.jpigeon.rideevolutionlib.client;

import com.jpigeon.ridebattlelib.common.api.RideBattleAPI;
import com.jpigeon.ridebattlelib.common.config.RiderConfig;
import com.jpigeon.rideevolutionlib.Config;
import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import com.jpigeon.rideevolutionlib.client.event.PlayerBonesVisibilityEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Arrays;

@EventBusSubscriber(modid = RideEvolutionLib.MODID, value = Dist.CLIENT)
public class ClientArmRenderer {
    @SubscribeEvent
    public static void onRenderHand(RenderArmEvent event) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) return;
        if (!Config.FIRST_PERSON_ARM_RENDER.get() || !RideBattleAPI.isTransformed(player)) return;
        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        float partialTick = mc.getTimer().getRealtimeDeltaTicks();
        HumanoidArm arm = event.getArm();

        renderArm(player, EquipmentSlot.CHEST, poseStack, bufferSource, partialTick, packedLight, arm);
    }

    @SuppressWarnings("unchecked")
    // 参考的艾雅酱！
    public static void renderArm(AbstractClientPlayer player, EquipmentSlot slot, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, HumanoidArm arm) {
        ItemStack chest = player.getItemBySlot(slot);
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<?> entityrenderer = entityrenderdispatcher.getRenderer(player);
        if (!(entityrenderer instanceof PlayerRenderer playerRenderer)) return;
        PlayerModel<AbstractClientPlayer> playermodel = playerRenderer.getModel();
        poseStack.pushPose();

        if (chest.getItem() instanceof GeoItem geoItem) {
            GeoArmorRenderer geoArmorRender = (GeoArmorRenderer) GeoRenderProvider.of(chest).getGeoArmorRenderer(player, chest, slot, playermodel);

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(geoArmorRender.getTextureLocation(geoItem)));
            RenderType renderType = RenderType.entityTranslucent(geoArmorRender.getTextureLocation(geoItem));

            GeoModel geoModel = geoArmorRender.getGeoModel();
            BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(geoItem, geoArmorRender));

            hideAllBonesExceptArms(geoArmorRender);

            GeoBone right = geoArmorRender.getRightArmBone(geoModel);
            if (right != null) {
                right.setHidden(arm != HumanoidArm.RIGHT);
                right.updateRotation(0, 0, 0);
                right.updatePosition(0, 0, 0);
            }

            GeoBone left = geoArmorRender.getLeftArmBone(geoModel);
            if (left != null) {
                left.setHidden(arm != HumanoidArm.LEFT);
                left.updateRotation(0, 0, 0);
                left.updatePosition(0, 0, 0);
            }
            geoArmorRender.actuallyRender(poseStack, (Item) geoItem, model, renderType, bufferSource, buffer, true, partialTick, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }

        poseStack.popPose();
    }

    public static <T extends Item & GeoAnimatable & GeoItem> void hideAllBonesExceptArms(GeoArmorRenderer<T> render) {
        GeoModel<T> model = render.getGeoModel();
        try {
            for (GeoBone geoBone : Arrays.asList(
                    render.getHeadBone(model),
                    render.getBodyBone(model),
                    render.getRightLegBone(model),
                    render.getRightBootBone(model),
                    render.getLeftLegBone(model),
                    render.getLeftBootBone(model)
            )) {
                if (geoBone != null) {
                    geoBone.setHidden(true);
                }
            }
        } catch (Exception ignored) {
        }
    }


    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.hasEffect(MobEffects.INVISIBILITY)) return;
        if (!RideBattleAPI.isTransformed(player)) {
            player.setInvisible(false);
            return;
        }
        if (Config.FALLBACK_HENSHIN_RENDER_MODE.get()) {
            player.setInvisible(true);
            return;
        }

        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<?> entityrenderer = entityrenderdispatcher.getRenderer(player);
        PlayerModel<AbstractClientPlayer> model = ((PlayerRenderer) entityrenderer).getModel();

        RiderConfig config = RiderConfig.findActiveDriverConfig(player);
        if (config == null) return;
        PlayerBonesVisibilityEvent visibilityEvent = new PlayerBonesVisibilityEvent(player, config.getRiderId());
        NeoForge.EVENT_BUS.post(visibilityEvent);

        model.head.visible = visibilityEvent.isHeadVisible();
        model.hat.visible = visibilityEvent.isHatVisible();
        model.body.visible = visibilityEvent.isBodyVisible();
        model.rightArm.visible = visibilityEvent.isRightArmVisible();
        model.leftArm.visible = visibilityEvent.isLeftArmVisible();
        model.rightLeg.visible = visibilityEvent.isRightLegVisible();
        model.leftLeg.visible = visibilityEvent.isLeftLegVisible();
        model.leftSleeve.visible = visibilityEvent.isLeftSleeveVisible();
        model.rightSleeve.visible = visibilityEvent.isRightSleeveVisible();
        model.leftPants.visible = visibilityEvent.isLeftPantsVisible();
        model.rightPants.visible = visibilityEvent.isRightPantsVisible();
        model.jacket.visible = visibilityEvent.isJacketVisible();
    }
}
