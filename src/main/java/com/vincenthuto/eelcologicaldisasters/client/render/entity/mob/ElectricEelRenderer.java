package com.vincenthuto.eelcologicaldisasters.client.render.entity.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelLargeModel;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelMidModel;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelSmallModel;
import com.vincenthuto.eelcologicaldisasters.common.entity.mob.ElectricEelEntity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class ElectricEelRenderer extends MobRenderer<ElectricEelEntity, EntityModel<ElectricEelEntity>> {
	private static final ResourceLocation PUFFER_LOCATION = EelcologicalDisasters
			.rloc("textures/entity/electric_eel/model_electric_eel.png");
	private int puffStateO = 3;
	private final EntityModel<ElectricEelEntity> small;
	private final EntityModel<ElectricEelEntity> mid;
	private final EntityModel<ElectricEelEntity> big = this.getModel();

	public ElectricEelRenderer(EntityRendererProvider.Context p_174358_) {
		super(p_174358_, new ElectricEelLargeModel<>(p_174358_.bakeLayer(ElectricEelLargeModel.LAYER_LOCATION)), 0.2F);
		this.mid = new ElectricEelMidModel<>(p_174358_.bakeLayer(ElectricEelMidModel.LAYER_LOCATION));
		this.small = new ElectricEelSmallModel<>(p_174358_.bakeLayer(ElectricEelSmallModel.LAYER_LOCATION));
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(ElectricEelEntity pEntity) {
		return PUFFER_LOCATION;
	}

	public void render(ElectricEelEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
			MultiBufferSource pBuffer, int pPackedLight) {
		int i = pEntity.getPuffState();
		if (i != this.puffStateO) {
			if (i == 0) {

				this.model = this.small;
			} else if (i == 1) {

				this.model = this.mid;
			} else {

				this.model = this.big;
			}
		}

		this.puffStateO = i;
		this.shadowRadius = 0.1F + 0.1F * (float) i;
		super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
	}

	@Override
	protected void scale(ElectricEelEntity pLivingEntity, PoseStack pPoseStack, float pPartialTickTime) {
		float f = pLivingEntity.getSwelling(pPartialTickTime);
		float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
		f = Mth.clamp(f, 0.0F, 1.0F);
		f *= f;
		f *= f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		pPoseStack.scale(f2, f3, f2);
	}

	@Override
	protected float getWhiteOverlayProgress(ElectricEelEntity pLivingEntity, float pPartialTicks) {
		if (pLivingEntity.getPuffState() > 0) {
			float f = pLivingEntity.getPuffState() + pPartialTicks;
			return (int) (f * 10F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.25F, 0.5F);
		} else {
			return 0;
		}

	}

	protected void setupRotations(ElectricEelEntity pEntityLiving, PoseStack pPoseStack, float pAgeInTicks,
			float pRotationYaw, float pPartialTicks) {
		pPoseStack.translate(0.0F, Mth.cos(pAgeInTicks * 0.05F) * 0.08F, 0.0F);
		super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
	}
}