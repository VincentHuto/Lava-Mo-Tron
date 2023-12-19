package com.vincenthuto.lavamotron.common.menu;

import com.vincenthuto.lavamotron.common.objects.ThermalShardItem;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LavamotronFluidInputSlot extends Slot {

	public static boolean isBucket(ItemStack p_39530_) {
		return p_39530_.is(Items.BUCKET);
	}

	public LavamotronFluidInputSlot(LavamotronMenu p_39520_, Container p_39521_, int p_39522_, int p_39523_, int p_39524_) {
		super(p_39521_, p_39522_, p_39523_, p_39524_);
	}

	@Override
	public int getMaxStackSize(ItemStack p_39528_) {
		return 64;
	}

	@Override
	public boolean mayPlace(ItemStack p_39526_) {
		return isBucket(p_39526_) || p_39526_.getItem() instanceof ThermalShardItem;
	}
}