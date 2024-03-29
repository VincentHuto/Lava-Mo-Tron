package com.vincenthuto.lavamotron.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class LavamotronSerializer implements RecipeSerializer<LavamotronRecipe> {

	protected LavamotronRecipe createRecipe(ResourceLocation recipeId, String group, Ingredient ingredient,
			ItemStack result, float experience, int cookingTime) {
		return new LavamotronRecipe(recipeId, group, ingredient, result, experience, cookingTime);
	}

	@SuppressWarnings("deprecation")
	@Override
	public LavamotronRecipe fromJson(ResourceLocation location, JsonObject object) {
		String s = GsonHelper.getAsString(object, "group", "");
		JsonElement jsonelement = GsonHelper.isArrayNode(object, "ingredient")
				? GsonHelper.getAsJsonArray(object, "ingredient")
				: GsonHelper.getAsJsonObject(object, "ingredient");
		Ingredient ingredient = Ingredient.fromJson(jsonelement);
		if (!object.has("result"))
			throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
		ItemStack itemstack;
		if (object.get("result").isJsonObject())
			itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(object, "result"));
		else {
			int c = GsonHelper.getAsInt(object, "count");
			String s1 = GsonHelper.getAsString(object, "result");
			ResourceLocation resourcelocation = new ResourceLocation(s1);
			ForgeRegistries.ITEMS.getValue(resourcelocation);

			itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation), c);
		}

		float f = GsonHelper.getAsFloat(object, "experience", 0.0F);
		int i = GsonHelper.getAsInt(object, "cookingtime", 100);
		return new LavamotronRecipe(location, s, ingredient, itemstack, f, i);
	}

	@Override
	public LavamotronRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		ResourceLocation id = buffer.readResourceLocation();
		String group = buffer.readUtf();
		ItemStack result = buffer.readItem();
		Ingredient input = Ingredient.of(buffer.readItem());
		float xp = buffer.readFloat();
		int time = buffer.readInt();
		return createRecipe(id, group, input, result, xp, time);
	}


	@Override
	public void toNetwork(FriendlyByteBuf buffer, LavamotronRecipe recipe) {
		buffer.writeResourceLocation(recipe.getId());
		buffer.writeUtf(recipe.getGroup());
		buffer.writeItem(recipe.getIngredients().get(0).getItems()[0]);
		buffer.writeItem(recipe.getResult());
		buffer.writeFloat(recipe.getExperience());
		buffer.writeInt(recipe.getCookingTime());
	}
}