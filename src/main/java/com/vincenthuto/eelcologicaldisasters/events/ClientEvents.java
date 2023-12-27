package com.vincenthuto.eelcologicaldisasters.events;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelLargeModel;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelMidModel;
import com.vincenthuto.eelcologicaldisasters.client.model.mob.ElectricEelSmallModel;
import com.vincenthuto.eelcologicaldisasters.client.render.entity.mob.ElectricEelRenderer;
import com.vincenthuto.eelcologicaldisasters.init.EntityInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EelcologicalDisasters.MODID, bus = Bus.FORGE)
public class ClientEvents {

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {

	}

	@Mod.EventBusSubscriber(modid = EelcologicalDisasters.MODID, value = Dist.CLIENT, bus = Bus.MOD)
	public static class ClientModBusEvents {

		@SubscribeEvent
		public static void renderEntities(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(EntityInit.electric_eel.get(), ElectricEelRenderer::new);

		}

		@SubscribeEvent
		public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ElectricEelLargeModel.LAYER_LOCATION, ElectricEelLargeModel::createBodyLayer);
			event.registerLayerDefinition(ElectricEelMidModel.LAYER_LOCATION, ElectricEelMidModel::createBodyLayer);
			event.registerLayerDefinition(ElectricEelSmallModel.LAYER_LOCATION, ElectricEelSmallModel::createBodyLayer);

		}

		@SubscribeEvent
		public static void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
		}

	}
}
