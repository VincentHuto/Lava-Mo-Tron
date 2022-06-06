package com.vincenthuto.lavamotron.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.recipe.LavamotronRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class LavamotronRecipeCategory implements IRecipeCategory<LavamotronRecipe> {

	public static final ResourceLocation UID = new ResourceLocation(Lavamotron.MOD_ID, "lavamotron");
	private final IDrawable background;
	@SuppressWarnings("unused")
	private final String localizedName = "Lava-mo-tron";
	private final IDrawable overlay;
	private final IDrawable icon;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	protected IDrawableStatic staticFlame;
	protected IDrawableAnimated animatedFlame;

	public LavamotronRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(150, 110);
		overlay = guiHelper.createDrawable(
				new ResourceLocation("lavamotron", "textures/container/lavamotron_gui_overlay.png"), 0, 0, 150, 110);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Lavamotron.lavamotron_block.get()));
		this.staticFlame = guiHelper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 82,
				114, 14, 14);
		this.animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP,
				true);
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<>() {
			@Override
			public IDrawableAnimated load(Integer cookTime) {
				return guiHelper
						.drawableBuilder(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 82, 128, 24, 17)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
	}

	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Nonnull
	@Override
	public Class<? extends LavamotronRecipe> getRecipeClass() {
		return LavamotronRecipe.class;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return new TextComponent("Lava-Mo-Tron");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

//	@Override
//	public void setIngredients(LavamotronRecipe recipe, IIngredients iIngredients) {
//		List<List<ItemStack>> list = new ArrayList<>();
//		for (Ingredient ingr : recipe.getIngredients()) {
//			list.add(Arrays.asList(ingr.getItems()));
//		}
//		iIngredients.setInputLists(VanillaTypes.ITEM, list);
//		iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
//	}

	protected void drawExperience(LavamotronRecipe recipe, PoseStack poseStack, int y) {
		float experience = recipe.getExperience();
		if (experience > 0) {
			TranslatableComponent experienceString = new TranslatableComponent("gui.jei.category.smelting.experience",
					experience);
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			int stringWidth = fontRenderer.width(experienceString);
			fontRenderer.draw(poseStack, experienceString, background.getWidth() - stringWidth, y, 0xFF808080);
		}
	}

	protected void drawCookTime(LavamotronRecipe recipe, PoseStack poseStack, int y) {
		int cookTime = recipe.getCookingTime();
		if (cookTime > 0) {
			int cookTimeSeconds = cookTime / 20;
			TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds",
					cookTimeSeconds);
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			int stringWidth = fontRenderer.width(timeString);
			fontRenderer.draw(poseStack, timeString, background.getWidth() - stringWidth, y, 0xFF808080);
		}
	}

	protected IDrawableAnimated getArrow(LavamotronRecipe recipe) {
		int cookTime = recipe.getCookingTime();
		if (cookTime <= 0) {
			cookTime = 200;
		}
		return this.cachedArrows.getUnchecked(cookTime);
	}

	@Override
	public void draw(LavamotronRecipe recipe, PoseStack PoseStack, double mouseX, double mouseY) {
		overlay.draw(PoseStack);
		animatedFlame.draw(PoseStack, 57, 37);

		IDrawableAnimated arrow = getArrow(recipe);
		arrow.draw(PoseStack, 80, 34);

		drawExperience(recipe, PoseStack, 0);
		drawCookTime(recipe, PoseStack, 60);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LavamotronRecipe recipe, IFocusGroup focuses) {
		List<List<ItemStack>> list = new ArrayList<>();
		for (Ingredient ingr : recipe.getIngredients()) {
			list.add(Arrays.asList(ingr.getItems()));
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 56, 17).addIngredients(VanillaTypes.ITEM, list.get(0));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addIngredient(VanillaTypes.ITEM, recipe.getResultItem());

	}

//	@Override
//	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull LavamotronRecipe recipe,
//			@Nonnull IIngredients ingredients) {
//		recipeLayout.getItemStacks().init(0, true, 117, 36);
//		recipeLayout.getItemStacks().set(0, recipe.getResultItem());
//		recipeLayout.getItemStacks().init(1, true, 55, 16);
//		recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
//	}

}