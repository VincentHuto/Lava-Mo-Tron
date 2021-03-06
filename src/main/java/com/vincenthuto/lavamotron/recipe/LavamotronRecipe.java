package com.vincenthuto.lavamotron.recipe;

import java.util.Collection;

import com.vincenthuto.lavamotron.core.Lavamotron;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LavamotronRecipe extends AbstractCookingRecipe {
	public LavamotronRecipe(ResourceLocation resourceLocation, String group, Ingredient ingredient, ItemStack result,
			float experience, int cookingTime) {
		super(Lavamotron.lavamotron_recipe_type, resourceLocation, group, ingredient, result, experience, cookingTime);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(Lavamotron.lavamotron_block.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Lavamotron.lavamotron_serializer.get();
	}

	public static Collection<LavamotronRecipe> getAllRecipes(Level world) {
		return world.getRecipeManager().getAllRecipesFor(Lavamotron.lavamotron_recipe_type);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> allIngredients = NonNullList.create();
		allIngredients.add(this.ingredient);
		return allIngredients;
	}

	@Override
	public ItemStack getResultItem() {
		return super.getResultItem();
	}
}