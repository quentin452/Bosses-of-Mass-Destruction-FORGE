package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Random;

public class ObsidilithArmorRenderer implements IRendererWithModel, IRenderer<ObsidilithEntity> {
    private final AnimatedGeoModel<ObsidilithEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final ResourceLocation armorTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private ObsidilithEntity obsidilithEntity;
    private RenderType type;

    public ObsidilithArmorRenderer(AnimatedGeoModel<ObsidilithEntity> geoModel, EntityRendererProvider.Context context){
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(ObsidilithEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * new Random().nextFloat();

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(geoModel, context);

        this.obsidilithEntity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(GeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer energyBuffer = buffer.getBuffer(type);
        if(obsidilithEntity == null) return;
        if (type == null) return;

        if (obsidilithEntity.isShielded()){
            Vec3 color = getColor().add(VecUtils.unit).normalize().scale(0.6);

            if (geoModelProvider == null) return;
            geoModelProvider.render(
                    model,
                    obsidilithEntity,
                    partialTicks,
                    type,
                    poseStack,
                    buffer,
                    energyBuffer,
                    packedLightIn,
                    packedOverlayIn,
                    (float) color.x, (float) color.y, (float) color.z, 1.0f
            );
        }
    }

    private Vec3 getColor() {
        Vec3 color;
        switch (obsidilithEntity.currentAttack){
            case ObsidilithUtils.burstAttackStatus -> color = BMDColors.ORANGE;
            case ObsidilithUtils.waveAttackStatus -> color = BMDColors.RED;
            case ObsidilithUtils.spikeAttackStatus -> color = BMDColors.COMET_BLUE;
            case ObsidilithUtils.anvilAttackStatus -> color = BMDColors.ENDER_PURPLE;
            case ObsidilithUtils.pillarDefenseStatus -> color = BMDColors.WHITE;
            default -> color = BMDColors.WHITE;
        }
        return color;
    }

    private static class RenderHelper extends GeoEntityRenderer<ObsidilithEntity> {

        public RenderHelper(AnimatedGeoModel<ObsidilithEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
        }

        @Override
        public void renderCube(GeoCube cube, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.scale(1.08f, 1.05f, 1.08f);
            super.renderCube(cube, poseStack, buffer, IBoneLight.fullbright, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }
}
