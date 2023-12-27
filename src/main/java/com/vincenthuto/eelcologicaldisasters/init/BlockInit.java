package com.vincenthuto.eelcologicaldisasters.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = EelcologicalDisasters.MODID, bus = Bus.MOD, value = Dist.CLIENT)

public class BlockInit {
	public static final DeferredRegister<Block> BASEBLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			EelcologicalDisasters.MODID);

	public static List<Block> getAllBlockEntries() {
		List<Block> blocks = new ArrayList<>();
		BASEBLOCKS.getEntries().stream().map(RegistryObject::get).forEach(b -> blocks.add(b));
		return blocks;
	}

	public static Stream<RegistryObject<Block>> getAllBlockEntriesAsStream() {
		Stream<RegistryObject<Block>> combinedStream = Stream.of(BASEBLOCKS.getEntries()).flatMap(Collection::stream);
		return combinedStream;
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void registerBlocks(FMLClientSetupEvent event) {
//		ItemBlockRenderTypes.setRenderLayer(BlockInit.smouldering_ash_trail.get(), RenderType.cutoutMipped());

	}

	@SubscribeEvent
	public static void registerBlocks(FMLCommonSetupEvent event) {
//		event.enqueueWork(() -> {
//			((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(BlockInit.bleeding_heart.getId(),
//					BlockInit.potted_bleeding_heart);
//		});
	}

}
