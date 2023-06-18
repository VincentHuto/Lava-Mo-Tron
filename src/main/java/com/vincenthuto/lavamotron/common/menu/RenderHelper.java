package com.vincenthuto.lavamotron.common.menu;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class RenderHelper {
	public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");

	public static int color(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack)
				: 0;
	}

	public static int density(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getDensity(stack) : 0;
	}

	public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {
		if (fluid.isEmpty()) {
			return;
		}
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		int color = color(fluid);
		setPosTexShader();
		setBlockTextureSheet();
		setSahderColorFromInt(color);
		drawTiledTexture(x, y, getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid)), width,
				height);
	}

	public static void drawScaledTexturedModalRectFromSprite(int x, int y, TextureAtlasSprite icon, int width,
			int height) {

		if (icon == null) {
			return;
		}
		float minU = icon.getU0();
		float maxU = icon.getU1();
		float minV = icon.getV0();
		float maxV = icon.getV1();

		float u = minU + (maxU - minU) * width / 16F;
		float v = minV + (maxV - minV) * height / 16F;

		BufferBuilder buffer = tesselator().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x, y + height, 0).uv(minU, v).endVertex();
		buffer.vertex(x + width, y + height, 0).uv(u, v).endVertex();
		buffer.vertex(x + width, y, 0).uv(u, minV).endVertex();
		buffer.vertex(x, y, 0).uv(minU, minV).endVertex();

		tesselator().end();
	}

	public static void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {
		int drawHeight;
		int drawWidth;
		for (int i = 0; i < width; i += 16) {
			for (int j = 0; j < height; j += 16) {
				drawWidth = Math.min(width - i, 16);
				drawHeight = Math.min(height - j, 16);
				drawScaledTexturedModalRectFromSprite(x + i, y + j, icon, drawWidth, drawHeight);
			}
		}
		resetShaderColor();
	}

	public static TextureAtlasSprite getFluidTexture(Fluid fluid) {

		return getTexture(IClientFluidTypeExtensions.of(fluid).getStillTexture());
	}

	public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {

		return getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));
	}

	public static TextureAtlasSprite getTexture(ResourceLocation location) {

		return textureMap().getSprite(location);
	}

	public static TextureAtlasSprite getTexture(String location) {

		return textureMap().getSprite(new ResourceLocation(location));
	}

	public static void resetShaderColor() {

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void setBlockTextureSheet() {

		setShaderTexture0(MC_BLOCK_SHEET);
	}

	public static void setPosTexShader() {

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	public static void setSahderColorFromInt(int color) {

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		RenderSystem.setShaderColor(red, green, blue, 1.0F);
	}

	private static void setShaderTexture0(ResourceLocation mcBlockSheet) {
		RenderSystem.setShaderTexture(0, mcBlockSheet);
	}

	public static Tesselator tesselator() {

		return Tesselator.getInstance();
	}

	public static TextureAtlas textureMap() {

		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
	}
}
