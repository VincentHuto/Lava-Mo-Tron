package com.vincenthuto.lavamotron.common.objects;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class LavamotronItemBlock extends BlockItem {

	public LavamotronItemBlock(Block p_40565_, Properties p_40566_) {
		super(p_40565_, p_40566_);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable(
				"'I just wish there was a simpler machine that like,\nexclusively let me turn cobblestone into lava...'\nP.J. 2k21")
				.withStyle(ChatFormatting.GOLD));

	}

}
