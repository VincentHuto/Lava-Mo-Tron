package com.vincenthuto.eelcologicaldisasters.common.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGeneration {
	public static void generate(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeClient(),
				new EDItemModelProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new EDLanguageProvider(packOutput, "en_us"));
		generator.addProvider(event.includeServer(), new EDRecipeProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(event.includeServer(),
				new EDEntityTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
	}
}