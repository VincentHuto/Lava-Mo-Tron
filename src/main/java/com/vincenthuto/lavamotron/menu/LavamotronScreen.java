package com.vincenthuto.lavamotron.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.network.PacketHandler;
import com.vincenthuto.lavamotron.network.PacketToggleMachineMode;
import com.vincenthuto.lavamotron.objects.LavamotronBlockEntity;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class LavamotronScreen extends AbstractContainerScreen<LavamotronMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Lavamotron.MOD_ID,
			"textures/container/lavamotron_gui.png");
	private static final int TOGGLEOFFID = 100;
	private static final int TOGGLEONID = 101;

	public static int density(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? RenderHelper.density(stack) : 0;
	}
	private final ResourceLocation texture;
	final LavamotronBlockEntity te;
	GuiButtonTextured toggleOffButton;

	GuiButtonTextured toggleOnButton;

	public LavamotronScreen(LavamotronMenu p_97825_, Inventory p_97827_, Component p_97828_) {
		super(p_97825_, p_97827_, p_97828_);
		this.texture = TEXTURE;
		this.te = p_97825_.getTe();

	}

	protected int getScaled(int scale, FluidTank tank) {
		double fraction = (double) tank.getFluidAmount() / 6 * scale / tank.getCapacity();
		int amount = MathHelper.clamp(MathHelper.round(fraction), 0, scale);
		return fraction > 0 ? Math.max(1, amount) : amount;
	}

	@Override
	public void init() {
		super.init();
		leftPos = width / 2 - imageWidth / 2;
		topPos = height / 2 - imageHeight / 2;
		renderables.clear();
		toggleOffButton = new GuiButtonTextured(TEXTURE, TOGGLEOFFID, leftPos + imageWidth - (imageWidth - 112),
				topPos + imageHeight - (104), 24, 8, 176, 31, (press) -> {
					if (press instanceof GuiButtonTextured button) {
						PacketHandler.MAINCHANNEL.sendToServer(new PacketToggleMachineMode());
					}
				});
		if (!te.liquidMode) {
			toggleOffButton.setState(true);
		}
		this.addRenderableWidget(toggleOffButton);

		toggleOnButton = new GuiButtonTextured(TEXTURE, TOGGLEONID, leftPos + imageWidth - (imageWidth - 112),
				topPos + imageHeight - (104), 24, 8, 200, 31, (press) -> {
					if (press instanceof GuiButtonTextured button) {
						PacketHandler.MAINCHANNEL.sendToServer(new PacketToggleMachineMode());
					}
				});
		if (te.liquidMode) {
			toggleOnButton.setState(true);
		}
		this.addRenderableWidget(toggleOnButton);

	}

	@Override
	public void render(PoseStack pose, int p_97859_, int p_97860_, float p_97861_) {
		this.renderBackground(pose);
		this.renderBg(pose, p_97861_, p_97859_, p_97860_);
		int centerX = (width / 2) - imageWidth / 2;
		int centerY = (height / 2) - imageHeight / 2;
		FluidStack fluid = te.tank.getFluid();
		super.render(pose, p_97859_, p_97860_, p_97861_);
		if (fluid != null) {
			int resourceHeight = height - 2;
			int amount = getScaled(resourceHeight, te.tank);
			RenderHelper.drawFluid(centerX + 145, -centerY + (resourceHeight - amount) - 109, te.tank.getFluid(), 20,
					amount);
		}
		HLGuiUtils.drawMaxWidthString(font, Component.translatable("" + te.tank.getFluidAmount() + "mb"), centerX + 100,
				centerY + 71, 165, 0xffffff, true);
		this.renderTooltip(pose, p_97859_, p_97860_);
		if (this.toggleOffButton.isHoveredOrFocused() && toggleOffButton.visible) {
			renderTooltip(pose, Component.translatable("Toggle Liquid Mode On"), this.toggleOffButton.getX(),
					this.toggleOffButton.getY());
		}
		if (this.toggleOnButton.isHoveredOrFocused() && toggleOnButton.visible) {
			renderTooltip(pose, Component.translatable("Toggle Liquid Mode Off"), this.toggleOnButton.getX(),
					this.toggleOnButton.getY());
		}

	}

	@Override
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
		toggleOffButton.visible = false;
		toggleOnButton.visible = false;
		int l = this.menu.getBurnProgress();
		this.blit(p_97853_, i + 79, j + 34, 176, 14, l + 1, 16);
		if (te.liquidMode) {
			toggleOffButton.visible = false;
			toggleOnButton.visible = true;

		} else {
			toggleOnButton.visible = false;
			toggleOffButton.visible = true;
		}
		toggleOnButton.render(p_97853_, 0, 00, 10);
		toggleOffButton.render(p_97853_, 0, 00, 10);

	}

	@Override
	protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
		super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
	}

}
