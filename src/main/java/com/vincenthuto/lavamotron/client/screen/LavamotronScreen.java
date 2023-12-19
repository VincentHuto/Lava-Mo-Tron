package com.vincenthuto.lavamotron.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.render.FluidInfoArea;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.lavamotron.Lavamotron;
import com.vincenthuto.lavamotron.common.menu.LavamotronMenu;
import com.vincenthuto.lavamotron.common.network.PacketHandler;
import com.vincenthuto.lavamotron.common.network.PacketToggleMachineMode;
import com.vincenthuto.lavamotron.common.objects.LavamotronBlockEntity;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class LavamotronScreen extends AbstractContainerScreen<LavamotronMenu> {

	private final ResourceLocation texture;
	final LavamotronBlockEntity te;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Lavamotron.MOD_ID,
			"textures/container/lavamotron_gui.png");
	private static final int TOGGLEOFFID = 100;
	private static final int TOGGLEONID = 101;
	LavamotronButton toggleOffButton;
	LavamotronButton toggleOnButton;

	public LavamotronScreen(LavamotronMenu p_97825_, Inventory p_97827_, Component p_97828_) {
		super(p_97825_, p_97827_, p_97828_);
		this.texture = TEXTURE;
		this.te = p_97825_.getTe();

	}

	@Override
	public void init() {
		super.init();
		leftPos = width / 2 - imageWidth / 2;
		topPos = height / 2 - imageHeight / 2;
		renderables.clear();
		toggleOffButton = new LavamotronButton(TEXTURE, TOGGLEOFFID, leftPos + imageWidth - (imageWidth - 124),
				topPos + imageHeight - (104), 8, 18, 176, 31, (press) -> {
					if (press instanceof LavamotronButton button) {
						PacketHandler.MAINCHANNEL.sendToServer(new PacketToggleMachineMode());
					}
				});
		if (!te.liquidMode) {
			toggleOffButton.setState(true);
		}
		this.addRenderableWidget(toggleOffButton);

		toggleOnButton = new LavamotronButton(TEXTURE, TOGGLEONID, leftPos + imageWidth - (imageWidth - 124),
				topPos + imageHeight - (104), 8, 18, 184, 31, (press) -> {
					if (press instanceof LavamotronButton button) {
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
		FluidStack fluid = te.tank.getFluid();
		super.render(graphics, mouseX, mouseY, parTick);
		if (fluid != null) {
			Rect2i area = new Rect2i(leftPos + 145, topPos + 10, 20, 45);
			FluidInfoArea lavaArea = new FluidInfoArea(te.tank, area, 176, 30, 20, 51, TEXTURE);
			lavaArea.render(graphics, mouseX, mouseY, parTick);
			List<Component> tooltip = new ArrayList<>();
			lavaArea.fillTooltip(mouseX, mouseY, tooltip);
			if (lavaArea.isMouseOver(mouseX, mouseY)) {
				graphics.renderTooltip(font, Component.translatable("" + te.tank.getFluidAmount() + "mb"), mouseX,
						mouseY);
			}
		}
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

	@Override
	protected void renderBg(GuiGraphics graphics, float p_97854_, int p_97855_, int p_97856_) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.leftPos;
		int j = this.topPos;
		toggleOffButton.visible = false;
		toggleOnButton.visible = false;
		graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int k = this.menu.getLitProgress();
			graphics.blit(texture, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		int l = this.menu.getBurnProgress();
		if (te.liquidMode) {
			toggleOffButton.visible = false;
			toggleOnButton.visible = true;

		} else {
			toggleOnButton.visible = false;
			toggleOffButton.visible = true;
		}

		graphics.blit(texture, i + 79, j + 34, 176, 14, l + 1, 16);

	}

	@Override
	protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
		super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
	}

	public static class LavamotronButton extends HLButtonTextured {

		public LavamotronButton(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
				int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnPress actionIn) {
			super(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, stateIn, actionIn);
		}

		public LavamotronButton(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
				int buttonHeightIn, int uIn, int vIn, boolean stateIn, Component text, Button.OnPress actionIn) {
			super(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, stateIn, text, actionIn);
		}

		public LavamotronButton(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
				int buttonHeightIn, int uIn, int vIn, Button.OnPress actionIn) {
			this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, false, actionIn);

		}

		public LavamotronButton(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
				int buttonHeightIn, int uIn, int vIn, Component text, Button.OnPress actionIn) {
			super(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, text, actionIn);
		}

		@Override
		public void render(GuiGraphics graphics, int mouseX, int mouseY, float particks) {
			if (visible) {
				this.isHovered = (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width
						&& mouseY < this.getY() + this.height);
				if (isHovered) {
					v = newV;
					graphics.blit(texture, posX, posY, u, adjV, width, height);
				} else {
					this.isHovered = false;
					newV = v;
					graphics.blit(texture, posX, posY, u, v, width, height);
				}
			}

		}
	}

}
