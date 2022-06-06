package com.vincenthuto.lavamotron.core;

import java.util.function.ToIntFunction;

import com.vincenthuto.lavamotron.menu.LavamotronMenu;
import com.vincenthuto.lavamotron.menu.LavamotronScreen;
import com.vincenthuto.lavamotron.objects.LavamotronBlock;
import com.vincenthuto.lavamotron.objects.LavamotronBlockEntity;
import com.vincenthuto.lavamotron.objects.LavamotronItemBlock;
import com.vincenthuto.lavamotron.objects.ThermalShardItem;
import com.vincenthuto.lavamotron.recipe.LavamotronRecipe;
import com.vincenthuto.lavamotron.recipe.LavamotronSerializer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("lavamotron")
@Mod.EventBusSubscriber(modid = Lavamotron.MOD_ID, bus = Bus.MOD)
public class Lavamotron {
	public static final String MOD_ID = "lavamotron";

	// Registries
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);

	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
	// Objects
	public static final RegistryObject<Block> lavamotron_block = BLOCKS.register("lavamotron",
			() -> new LavamotronBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
					.requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(litBlockEmission(13))));

	public static final RegistryObject<Item> lavamotron_item_block = ITEMS.register("lavamotron",
			() -> new LavamotronItemBlock(lavamotron_block.get(),
					new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

	public static final RegistryObject<Item> thermal_shard = ITEMS.register("thermal_shard",
			() -> new ThermalShardItem());

	public static final RegistryObject<BlockEntityType<LavamotronBlockEntity>> lavamotron_tile = TILES.register(
			"lavamotron",
			() -> BlockEntityType.Builder.of(LavamotronBlockEntity::new, lavamotron_block.get()).build(null));

	// Recipes
	public static RecipeType<LavamotronRecipe> lavamotron_recipe_type;
	public static final RegistryObject<RecipeSerializer<?>> lavamotron_serializer = SERIALIZERS.register("lavamotron",
			LavamotronSerializer::new);

	public Lavamotron() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		modEventBus.addGenericListener(MenuType.class, this::registerContainers);
		MinecraftForge.EVENT_BUS.register(this);
		ITEMS.register(modEventBus);
		BLOCKS.register(modEventBus);
		SERIALIZERS.register(modEventBus);
		TILES.register(modEventBus);

	}

	private static ToIntFunction<BlockState> litBlockEmission(int p_50760_) {
		return (p_50763_) -> {
			return p_50763_.getValue(BlockStateProperties.LIT) ? p_50760_ : 0;
		};
	}

	private void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
		event.getRegistry().registerAll(new MenuType<>(LavamotronMenu::new).setRegistryName("lavamotron"));
	}

	private void setupClient(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(LavamotronMenu.TYPE, LavamotronScreen::new);
		});
	}

	private void setupCommon(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			initRecipeTypes();
		});
	}

	private static void initRecipeTypes() {
		final ResourceLocation lavamotron = new ResourceLocation(MOD_ID, "lavamotron");
		lavamotron_recipe_type = (RecipeType<LavamotronRecipe>) Registry.register(Registry.RECIPE_TYPE, lavamotron,
				new RecipeType<LavamotronRecipe>() {
					public String toString() {
						return lavamotron.toString();
					}
				});
	}

}
