package com.vincenthuto.lavamotron.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.network.PacketHandler;
import com.vincenthuto.lavamotron.network.PacketToggleMachineMode;
import com.vincenthuto.lavamotron.objects.LavamotronBlockEntity;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float parTick) {
		this.renderBackground(graphics);
		this.renderBg(graphics, parTick, mouseX, mouseY);
		int centerX = (width / 2) - imageWidth / 2;
		int centerY = (height / 2) - imageHeight / 2;
		FluidStack fluid = te.tank.getFluid();
		super.render(graphics, mouseX, mouseY, parTick);
		if (fluid != null) {
			int resourceHeight = height - 2;
			int amount = getScaled(resourceHeight, te.tank);
			RenderHelper.drawFluid(centerX + 145, -centerY + (resourceHeight - amount) - 109, te.tank.getFluid(), 20,
					amount);

			if (mouseOverFluid(mouseX, mouseY, centerX + 145, -centerY + (resourceHeight - amount) - 109, 20, amount)) {
				graphics.renderTooltip(font, Component.translatable("" + te.tank.getFluidAmount() + "mb"), mouseX,
						mouseY);
			}
		}
		HLGuiUtils.drawMaxWidthString(font, Component.translatable("" + te.tank.getFluidAmount() + "mb"), centerX + 100,
				centerY + 71, 165, 0xffffff, true);

		this.renderTooltip(graphics, mouseX, mouseY);

		if (this.toggleOffButton.isMouseOver(mouseX, mouseY) && toggleOffButton.visible) {
			graphics.renderTooltip(font, Component.translatable("Toggle Liquid Mode On"), this.toggleOffButton.getX(),
					this.toggleOffButton.getY());
		}
		if (this.toggleOnButton.isMouseOver(mouseX, mouseY) && toggleOnButton.visible) {
			graphics.renderTooltip(font, Component.translatable("Toggle Liquid Mode Off"), this.toggleOffButton.getX(),
					this.toggleOffButton.getY());
		}

	}

	public boolean mouseOverFluid(int mouseX, int mouseY, int tankX, int tankY, int tankWidth, int tankHeight) {
		if (mouseX >= tankX && mouseY >= tankY && mouseX < tankX + tankWidth && mouseY < tankY + tankHeight) {
			return true;

		} else {
			return false;

		}

	}

	@Override
	protected void renderBg(GuiGraphics graphics, float p_97854_, int p_97855_, int p_97856_) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.leftPos;
		int j = this.topPos;
		graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int k = this.menu.getLitProgress();
			graphics.blit(texture, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		toggleOffButton.visible = false;
		toggleOnButton.visible = false;
		int l = this.menu.getBurnProgress();
		graphics.blit(texture, i + 79, j + 34, 176, 14, l + 1, 16);
		if (te.liquidMode) {
			toggleOffButton.visible = false;
			toggleOnButton.visible = true;

		} else {
			toggleOnButton.visible = false;
			toggleOffButton.visible = true;
		}
		toggleOnButton.render(graphics, 0, 00, 10);
		toggleOffButton.render(graphics, 0, 00, 10);

	}

	@Override
	protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
		super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
	}

}
