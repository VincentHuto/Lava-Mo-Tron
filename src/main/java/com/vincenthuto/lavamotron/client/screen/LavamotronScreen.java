package com.vincenthuto.lavamotron.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.render.FluidInfoArea;
import com.vincenthuto.lavamotron.Lavamotron;
import com.vincenthuto.lavamotron.common.menu.LavamotronMenu;
import com.vincenthuto.lavamotron.common.objects.LavamotronBlockEntity;

import net.minecraft.client.gui.GuiGraphics;
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
		int l = this.menu.getBurnProgress();
		graphics.blit(texture, i + 79, j + 34, 176, 14, l + 1, 16);

	}

	@Override
	protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
		super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
	}

}
