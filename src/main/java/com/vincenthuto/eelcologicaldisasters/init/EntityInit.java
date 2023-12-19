package com.vincenthuto.eelcologicaldisasters.init;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;
import com.vincenthuto.eelcologicaldisasters.common.entity.mob.ElectricEelEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = EelcologicalDisasters.MODID, bus = Bus.MOD)
public class EntityInit {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.ENTITY_TYPES, EelcologicalDisasters.MODID);

	public static final TagKey<EntityType<?>> EEL_TAG = createTag("eel");

	public static final RegistryObject<EntityType<ElectricEelEntity>> electric_eel = ENTITY_TYPES.register(
			"electric_eel",
			() -> EntityType.Builder.of(ElectricEelEntity::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F)
					.clientTrackingRange(4).build(EelcologicalDisasters.rloc("electric_eel").toString()));

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(EntityInit.electric_eel.get(), ElectricEelEntity.setAttributes().build());

	}

	public static TagKey<EntityType<?>> createTag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(EelcologicalDisasters.MODID, name));
	}

}
