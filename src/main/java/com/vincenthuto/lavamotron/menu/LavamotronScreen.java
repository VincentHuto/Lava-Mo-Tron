package com.vincenthuto.lavamotron.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.objects.LavamotronBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class LavamotronScreen extends AbstractContainerScreen<LavamotronMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Lavamotron.MOD_ID,
			"textures/container/lavamotron_gui.png");
	private final ResourceLocation texture;
	final LavamotronBlockEntity te;

	public LavamotronScreen(LavamotronMenu p_97825_, Inventory p_97827_, Component p_97828_) {
		super(p_97825_, p_97827_, p_97828_);
		this.texture = TEXTURE;
		this.te = p_97825_.getTe();

	}

	public void init() {
		super.init();
	}

	protected void renderBg(PoseStack p_97853_, float p_97854_, int p_97855_, int p_97856_) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		int i = this.leftPos;
		int j = this.topPos;
		this.blit(p_97853_, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {

			int k = this.menu.getLitProgress();
			this.blit(p_97853_, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		int l = this.menu.getBurnProgress();
		this.blit(p_97853_, i + 79, j + 34, 176, 14, l + 1, 16);
	}

	protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
		super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
	}

	public static int density(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getDensity() : 0;
	}

	public void render(PoseStack pose, int p_97859_, int p_97860_, float p_97861_) {
		this.renderBackground(pose);
		this.renderBg(pose, p_97861_, p_97859_, p_97860_);
		int centerX = (width / 2) - imageWidth / 2;
		int centerY = (height / 2) - imageHeight / 2;

		FluidStack fluid = te.tank.getFluid();

		super.render(pose, p_97859_, p_97860_, p_97861_);
		if (fluid != null) {
			if (fluid.getFluid().getAttributes().getStillTexture(fluid) != null) {
				int resourceHeight = height - 2;
				int resourceWidth = width - 2;
				int amount = getScaled(resourceHeight, te.tank);

				RenderHelper.drawFluid(centerX + 145, -centerY + (resourceHeight - amount) - 110, te.tank.getFluid(),
						20, amount);

				// RenderSystem.setShader(GameRenderer::getPositionTexShader);
//				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//				RenderSystem.setShaderTexture(0, new ResourceLocation(
//						"textures/" + fluid.getFluid().getAttributes().getStillTexture(fluid).getPath() + ".png"));
//				pose.pushPose();
//				pose.mulPose(new Quaternion(Vector3f.ZP, 180, true));
//				blit(pose, centerX - 465, centerY - 170, 0, 20, amount / 10,
//						RenderHelper.getTexture(fluid.getFluid().getAttributes().getStillTexture(fluid)));
//				pose.popPose();
			}

		}
		HLGuiUtils.drawMaxWidthString(font, new TextComponent("" + te.tank.getFluidAmount() + "mb"), centerX + 100,
				centerY + 70, 165, 0xffffff, true);
		this.renderTooltip(pose, p_97859_, p_97860_);

	}

	protected int getScaled(int scale, FluidTank tank) {

		double fraction = (double) tank.getFluidAmount() / 6 * scale / tank.getCapacity();
		int amount = MathHelper.clamp(MathHelper.round(fraction), 0, scale);
		return fraction > 0 ? Math.max(1, amount) : amount;
	}

}
