package com.vincenthuto.eelcologicaldisasters.init;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = EelcologicalDisasters.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ItemInit {
	public static final DeferredRegister<Item> BASEITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			EelcologicalDisasters.MODID);
	public static final DeferredRegister<Item> SPAWNEGGS = DeferredRegister.create(ForgeRegistries.ITEMS,
			EelcologicalDisasters.MODID);

	public static final RegistryObject<Item> car_battery = BASEITEMS.register("car_battery",
			() -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> raw_eel = BASEITEMS.register("raw_eel", () -> new Item(
			new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).build())));

	public static final RegistryObject<Item> cooked_eel = BASEITEMS.register("cooked_eel", () -> new Item(
			new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).build())));

	public static final RegistryObject<ForgeSpawnEggItem> spawn_egg_electric_eel = SPAWNEGGS.register(
			"spawn_egg_electric_eel",
			() -> new ForgeSpawnEggItem(EntityInit.electric_eel, 4926731, 15124009, new Item.Properties()));

	// Item Property Override
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void itemPropOverrideClient(final FMLClientSetupEvent event) {

	}

}
