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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

	private final ResourceLocation texture;
	final LavamotronBlockEntity te;
	GuiButtonTextured toggleOffButton;
	GuiButtonTextured toggleOnButton;

	public LavamotronScreen(LavamotronMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		this.texture = TEXTURE;
		this.te = menu.getTe();
	}

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

	protected void renderBg(PoseStack pose, float partialTick, int mX, int mY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		int i = this.leftPos;
		int j = this.topPos;
		this.blit(pose, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int k = this.menu.getLitProgress();
			this.blit(pose, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		toggleOffButton.visible = false;
		toggleOnButton.visible = false;
		int l = this.menu.getBurnProgress();
		this.blit(pose, i + 79, j + 34, 176, 14, l + 1, 16);
		if (te.liquidMode) {
			toggleOffButton.visible = false;
			toggleOnButton.visible = true;

		} else {
			toggleOnButton.visible = false;
			toggleOffButton.visible = true;
		}
		toggleOnButton.render(pose, 0, 00, 10);
		toggleOffButton.render(pose, 0, 00, 10);

	}

	protected void slotClicked(Slot slot, int id, int mouseButton, ClickType clickType) {
		super.slotClicked(slot, id, mouseButton, clickType);
	}

	public static int density(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getDensity() : 0;
	}

	public static double mapOneRangeToAnother(double sourceNumber, double fromA, double fromB, double toA, double toB,
			int decimalPrecision) {
		double deltaA = fromB - fromA;
		double deltaB = toB - toA;
		double scale = deltaB / deltaA;
		double negA = -1 * fromA;
		double offset = (negA * scale) + toA;
		double finalNumber = (sourceNumber * scale) + offset;
		int calcScale = (int) Math.pow(10, decimalPrecision);
		return (double) Math.round(finalNumber * calcScale) / calcScale;
	}

	public void render(PoseStack pose, int mX, int mY, float partialTick) {
		this.renderBackground(pose);
		this.renderBg(pose, partialTick, mX, mY);
		leftPos = width / 2 - imageWidth / 2;
		topPos = height / 2 - imageHeight / 2;
		FluidStack fluid = te.tank.getFluid();
		super.render(pose, mX, mY, partialTick);

		
		
		if (fluid != null) {
			if (fluid.getFluid().getAttributes().getStillTexture(fluid) != null) {
				int lerped = (int) mapOneRangeToAnother(te.tank.getFluidAmount(), 0, 10000, 0, 45, 2);
				RenderHelper.drawFluid(leftPos + 145, -topPos + (height - lerped) - 110, te.tank.getFluid(), 20,
						lerped);
			}
		}
		HLGuiUtils.drawMaxWidthString(font, new TextComponent("" + te.tank.getFluidAmount() + "mb"), leftPos + 100,
				topPos + 71, 165, 0xffffff, true);
		this.renderTooltip(pose, mX, mY);
		if (this.toggleOffButton.isHoveredOrFocused() && toggleOffButton.visible) {
			renderTooltip(pose, new TranslatableComponent("Toggle Liquid Mode On"), this.toggleOffButton.x,
					this.toggleOffButton.y);
		}
		if (this.toggleOnButton.isHoveredOrFocused() && toggleOnButton.visible) {
			renderTooltip(pose, new TranslatableComponent("Toggle Liquid Mode Off"), this.toggleOnButton.x,
					this.toggleOnButton.y);
		}

	}

	protected int getScaled(int scale, FluidTank tank) {
		double fraction = (double) tank.getFluidAmount() / 6 * scale / tank.getCapacity();
		int amount = MathHelper.clamp(MathHelper.round(fraction), 0, scale);
		return fraction > 0 ? Math.max(1, amount) : amount;
	}

}
