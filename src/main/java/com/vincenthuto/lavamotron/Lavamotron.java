package com.vincenthuto.lavamotron;

import java.util.function.ToIntFunction;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.lavamotron.client.screen.LavamotronScreen;
import com.vincenthuto.lavamotron.common.menu.LavamotronMenu;
import com.vincenthuto.lavamotron.common.network.PacketHandler;
import com.vincenthuto.lavamotron.common.objects.LavamotronBlock;
import com.vincenthuto.lavamotron.common.objects.LavamotronBlockEntity;
import com.vincenthuto.lavamotron.common.objects.LavamotronItemBlock;
import com.vincenthuto.lavamotron.common.objects.ThermalShardItem;
import com.vincenthuto.lavamotron.common.recipe.LavamotronRecipe;
import com.vincenthuto.lavamotron.common.recipe.LavamotronSerializer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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

	public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, HutosLib.MOD_ID);

	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(ForgeRegistries.RECIPE_TYPES, MOD_ID);

	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			MOD_ID);

	// Objects
	public static final RegistryObject<Block> lavamotron_block = BLOCKS.register("lavamotron",
			() -> new LavamotronBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
					.requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))));

	public static final RegistryObject<Item> lavamotron_item_block = ITEMS.register("lavamotron",
			() -> new LavamotronItemBlock(lavamotron_block.get(), new Item.Properties()));

	public static final RegistryObject<Item> thermal_shard = ITEMS.register("thermal_shard",
			() -> new ThermalShardItem());

	public static final RegistryObject<BlockEntityType<LavamotronBlockEntity>> lavamotron_tile = TILES.register(
			"lavamotron",
			() -> BlockEntityType.Builder.of(LavamotronBlockEntity::new, lavamotron_block.get()).build(null));

	public static final RegistryObject<CreativeModeTab> lavamotrontab = CREATIVETABS.register("lavamotrontab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group." + MOD_ID + ".lavamotrontab"))
					.icon(() -> new ItemStack(lavamotron_item_block.get())).build());

	public static final RegistryObject<MenuType<LavamotronMenu>> lavamotron_menu = CONTAINERS.register("lavamotron",
			() -> IForgeMenuType.create(LavamotronMenu::new));

	public static final RegistryObject<RecipeType<LavamotronRecipe>> lavamotron_recipe_type = RECIPE_TYPES
			.register("lavamotron_recipe_type", () -> new RecipeType<LavamotronRecipe>() {
				public String toString() {
					return lavamotron_recipe_type.toString();
				}
			});

	public static final RegistryObject<RecipeSerializer<?>> lavamotron_serializer = SERIALIZERS.register("lavamotron",
			LavamotronSerializer::new);

	private static ToIntFunction<BlockState> litBlockEmission(int p_50760_) {
		return (p_50763_) -> {
			return p_50763_.getValue(BlockStateProperties.LIT) ? p_50760_ : 0;
		};
	}

	public Lavamotron() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::buildContents);

		ITEMS.register(modEventBus);
		BLOCKS.register(modEventBus);
		CREATIVETABS.register(modEventBus);
		SERIALIZERS.register(modEventBus);
		RECIPE_TYPES.register(modEventBus);
		CONTAINERS.register(modEventBus);
		TILES.register(modEventBus);

	}

	public void buildContents(BuildCreativeModeTabContentsEvent output) {
		if (output.getTabKey() == lavamotrontab.getKey()) {
			output.accept(lavamotron_item_block.get());
			output.accept(thermal_shard.get());
		}
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(lavamotron_menu.get(), LavamotronScreen::new);
		});
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.registerChannels();
	}

}
