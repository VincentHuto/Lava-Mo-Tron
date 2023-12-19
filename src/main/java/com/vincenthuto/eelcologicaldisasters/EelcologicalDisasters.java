package com.vincenthuto.eelcologicaldisasters;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.vincenthuto.eelcologicaldisasters.common.data.DataGeneration;
import com.vincenthuto.eelcologicaldisasters.init.BlockInit;
import com.vincenthuto.eelcologicaldisasters.init.EntityInit;
import com.vincenthuto.eelcologicaldisasters.init.ItemInit;
import com.vincenthuto.eelcologicaldisasters.worldgen.EDBiomeModifiers;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(EelcologicalDisasters.MODID)
@Mod.EventBusSubscriber(modid = EelcologicalDisasters.MODID, bus = Bus.MOD)
public class EelcologicalDisasters {
	public static EelcologicalDisasters instance;

	public static final String MODID = "eelcologicaldisasters";
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final RegistryObject<CreativeModeTab> eelcologicaldisasterstab = CREATIVETABS.register(
			"eelcologicaldisasterstab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("item_group." + MODID + ".eelcologicaldisasterstab"))
					.icon(() -> new ItemStack(ItemInit.car_battery.get())).build());

	public EelcologicalDisasters() {
		instance = this;

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		MinecraftForge.EVENT_BUS.register(this);
		ItemInit.BASEITEMS.register(modEventBus);
		ItemInit.SPAWNEGGS.register(modEventBus);
		BlockInit.BASEBLOCKS.register(modEventBus);
		EntityInit.ENTITY_TYPES.register(modEventBus);
		CREATIVETABS.register(modEventBus);
		EDBiomeModifiers.BIOME_MODIFIER_SERIALIZERS_DEFERRED.register(modEventBus);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::buildContents);
		modEventBus.addListener(DataGeneration::generate);

		EDBiomeModifiers.BIOME_MODIFIER_SERIALIZERS_DEFERRED.register("fish_spawn",
				EDBiomeModifiers.FishSpawnBiomeModifier::makeCodec);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
	}

	public void buildContents(BuildCreativeModeTabContentsEvent populator) {
		if (populator.getTabKey() == eelcologicaldisasterstab.getKey()) {
			// Items
			ItemInit.BASEITEMS.getEntries().forEach(i -> populator.accept(i.get()));
			ItemInit.SPAWNEGGS.getEntries().forEach(i -> populator.accept(i.get()));

			// Blocks
			BlockInit.BASEBLOCKS.getEntries().forEach(i -> populator.accept(i.get()));
		}
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	public static ResourceLocation rloc(String path) {
		return new ResourceLocation(MODID, path);
	}
}
