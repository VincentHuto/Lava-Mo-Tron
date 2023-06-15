package com.vincenthuto.lavamotron.objects;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ThermalShardItem extends Item {

	public ThermalShardItem() {
		super(new Item.Properties());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable("Right click to place a").withStyle(ChatFormatting.GOLD));
		tooltip.add(Component.translatable("block of lava in the world.").withStyle(ChatFormatting.GOLD));
	}

	@SuppressWarnings("resource")
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos blockpos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		world.playLocalSound(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D,
				SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.4F + 0.8F, false);
		ctx.getLevel().setBlock(blockpos1, Blocks.LAVA.defaultBlockState(), -999);
		ctx.getItemInHand().shrink(1);
		if (!ctx.getLevel().isClientSide) {
			for (int i = 0; i < 10; i++) {
				double d0 = blockpos1.getX() + 0.5;
				double d1 = blockpos1.getY();
				double d2 = blockpos1.getZ() + .5;
				ServerLevel sWorld = (ServerLevel) ctx.getLevel();
				(sWorld).sendParticles(ParticleTypes.SMALL_FLAME, d0, d1, d2, 2,
						(world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3,
						(world.random.nextFloat() * 1 - 0.5) / 3, 0.1f);
				(sWorld).sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 2, (world.random.nextFloat() * 1 - 0.5) / 3,
						(world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, 0.1f);
			}
		}
		return super.useOn(ctx);
	}

}