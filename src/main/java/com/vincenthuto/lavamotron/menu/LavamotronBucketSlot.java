package com.vincenthuto.lavamotron.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LavamotronBucketSlot extends Slot {

	public LavamotronBucketSlot(LavamotronMenu p_39520_, Container p_39521_, int p_39522_, int p_39523_, int p_39524_) {
		super(p_39521_, p_39522_, p_39523_, p_39524_);
	}

	public boolean mayPlace(ItemStack p_39526_) {
		return isBucket(p_39526_);
	}

	public int getMaxStackSize(ItemStack p_39528_) {
		return 1;
	}

	public static boolean isBucket(ItemStack p_39530_) {
		return p_39530_.is(Items.BUCKET);
	}
}