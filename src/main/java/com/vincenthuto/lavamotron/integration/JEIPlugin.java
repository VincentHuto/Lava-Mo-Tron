package com.vincenthuto.lavamotron.integration;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.recipe.LavamotronRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

	private static final ResourceLocation ID = new ResourceLocation(Lavamotron.MOD_ID, "main");
	public static final RecipeType<LavamotronRecipe> lavamotron_recipe_type = RecipeType.create(Lavamotron.MOD_ID,
			"lavamotron", LavamotronRecipe.class);

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new LavamotronRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(Lavamotron.lavamotron_item_block.get()), lavamotron_recipe_type);
	}

	@Override
	public void registerRecipes(@Nonnull IRecipeRegistration registry) {
		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
		registry.addRecipes(lavamotron_recipe_type, LavamotronRecipe.getAllRecipes(world));
	}

}