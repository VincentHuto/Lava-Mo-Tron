package com.vincenthuto.eelcologicaldisasters.common.data;

import java.util.concurrent.CompletableFuture;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;
import com.vincenthuto.eelcologicaldisasters.init.EntityInit;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class EDEntityTagProvider extends TagsProvider<EntityType<?>> {

	public EDEntityTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
			ExistingFileHelper existingFileHelper) {
		super(output, Registries.ENTITY_TYPE, lookupProvider, EelcologicalDisasters.MODID, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Entity Type Tags";
	}

	@Override
	protected void addTags(Provider p_256380_) {
		tag(EntityInit.EEL_TAG).add(ResourceKey.create(Registries.ENTITY_TYPE,
				ForgeRegistries.ENTITY_TYPES.getKey(EntityInit.electric_eel.get())));
	}

}
