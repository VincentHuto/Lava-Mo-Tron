package com.vincenthuto.lavamotron.objects;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.vincenthuto.lavamotron.core.Lavamotron;
import com.vincenthuto.lavamotron.menu.LavamotronMenu;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class LavamotronBlockEntity extends BaseContainerBlockEntity
		implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
	protected static final int SLOT_INPUT = 0;
	protected static final int SLOT_FUEL = 1;
	protected static final int SLOT_RESULT = 2;
	public static final int DATA_LIT_TIME = 0;
	private static final int[] SLOTS_FOR_UP = new int[] { 0 };
	private static final int[] SLOTS_FOR_DOWN = new int[] { 2 };
	private static final int[] SLOTS_FOR_SIDES = new int[] { 1 };
	private static final int[] SLOTS_FOR_EAST = new int[] { 3 };
	private static final int[] SLOTS_FOR_SOUTH = new int[] { 1 };
	public static final int DATA_LIT_DURATION = 1;
	public static final int DATA_COOKING_PROGRESS = 2;
	public static final int DATA_COOKING_TOTAL_TIME = 3;
	public static final int NUM_DATA_VALUES = 4;
	public static final int BURN_TIME_STANDARD = 200;
	public static final int BURN_COOL_SPEED = 2;
	public NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
	public int litTime;
	public int litDuration;
	public int cookingProgress;
	public int cookingTotalTime;
	public boolean liquidMode;

	// Fluid Stuff
	public FluidTank tank = new FluidTank(10000);
	public final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
	protected LazyOptional<?> outputFluidCap = LazyOptional.empty();

	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	private final RecipeType<? extends AbstractCookingRecipe> recipeType = Lavamotron.lavamotron_recipe_type;

	public LavamotronBlockEntity(BlockPos p_154992_, BlockState p_154993_) {
		super(Lavamotron.lavamotron_tile.get(), p_154992_, p_154993_);
	}

	private boolean isLit() {
		return this.litTime > 0;
	}

	@SuppressWarnings("unchecked")
	public static void serverTick(Level level, BlockPos pos, BlockState state, LavamotronBlockEntity te) {
		boolean flag = te.isLit();
		boolean flag1 = false;
		if (te.isLit()) {
			--te.litTime;
		}
		ItemStack itemstack = te.items.get(1);
		ItemStack fillStack = te.items.get(4);
		if (!fillStack.isEmpty()) {
			if (fillStack.getItem() == Items.BUCKET && te.tank.getFluidAmount() >= 1000) {
				te.items.set(4, new ItemStack(Items.LAVA_BUCKET));
				te.tank.drain(new FluidStack(te.tank.getFluid(), 1000), FluidAction.EXECUTE);
			}
		}
		if (te.isLit() || !itemstack.isEmpty() && !te.items.get(0).isEmpty()) {
			Recipe<?> recipe = level.getRecipeManager()
					.getRecipeFor((RecipeType<AbstractCookingRecipe>) te.recipeType, te, level).orElse(null);
			int i = te.getMaxStackSize();
			if (!te.isLit() && te.canBurn(recipe, te.items, i)) {
				te.litTime = te.getBurnDuration(itemstack);
				te.litDuration = te.litTime;
				if (te.isLit()) {
					flag1 = true;
					if (itemstack.hasContainerItem())
						te.items.set(1, itemstack.getContainerItem());
					else if (!itemstack.isEmpty()) {
						itemstack.getItem();
						itemstack.shrink(1);
						if (itemstack.isEmpty()) {
							te.items.set(1, itemstack.getContainerItem());
						}
					}
				}
			}
			if (te.isLit() && te.canBurn(recipe, te.items, i)) {
				++te.cookingProgress;
				if (te.cookingProgress == te.cookingTotalTime) {
					te.cookingProgress = 0;
					te.cookingTotalTime = getTotalCookTime(level, te.recipeType, te);
					if (te.burn(recipe, te.items, i)) {
						te.setRecipeUsed(recipe);
					}
					flag1 = true;
				}
			} else {
				te.cookingProgress = 0;
			}
		} else if (!te.isLit() && te.cookingProgress > 0) {
			te.cookingProgress = Mth.clamp(te.cookingProgress - 2, 0, te.cookingTotalTime);
		}

		if (flag != te.isLit()) {
			flag1 = true;
			state = state.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(te.isLit()));
			level.setBlock(pos, state, 3);
		}

		if (flag1) {
			setChanged(level, pos, state);

		}
		te.sendUpdates();

	}

	@SuppressWarnings("unchecked")
	private boolean canBurn(@Nullable Recipe<?> p_155006_, NonNullList<ItemStack> p_155007_, int p_155008_) {
		if (!p_155007_.get(0).isEmpty() && p_155006_ != null) {
			ItemStack itemstack = ((Recipe<WorldlyContainer>) p_155006_).assemble(this);
			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack resultStack = p_155007_.get(2);
				if (liquidMode) {
					if (tank.getFluidAmount() < tank.getCapacity()) {
						return true;
					} else {
						if (resultStack.isEmpty()) {
							return true;
						} else if (!resultStack.sameItem(itemstack)) {
							return false;
						} else if (resultStack.getCount() + itemstack.getCount() <= p_155008_
								&& resultStack.getCount() + itemstack.getCount() <= resultStack.getMaxStackSize()) { // fix:
							return true;
						} else {
							return resultStack.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge
						}
					}
				} else {
					if (resultStack.isEmpty()) {
						return true;
					} else if (!resultStack.sameItem(itemstack)) {
						return false;
					} else if (resultStack.getCount() + itemstack.getCount() <= p_155008_
							&& resultStack.getCount() + itemstack.getCount() <= resultStack.getMaxStackSize()) { // fix:
						return true;
					} else {
						return resultStack.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge
						// fix:
					}
				}
			}
		} else {

			return false;
		}
	}

	private boolean burn(@Nullable Recipe<?> p_155027_, NonNullList<ItemStack> p_155028_, int p_155029_) {
		if (p_155027_ != null && this.canBurn(p_155027_, p_155028_, p_155029_)) {
			ItemStack itemstack = p_155028_.get(0);
			@SuppressWarnings("unchecked")
			ItemStack resultStack = ((Recipe<WorldlyContainer>) p_155027_).assemble(this);
			ItemStack currentResultStack = p_155028_.get(2);
			if (!liquidMode) {
				if (p_155028_.get(3).isEmpty()) {
					if (currentResultStack.isEmpty()) {
						p_155028_.set(2, resultStack.copy());
					} else if (currentResultStack.is(resultStack.getItem())) {
						currentResultStack.grow(resultStack.getCount());
					}
				} else {
					if (currentResultStack.isEmpty()) {
						if (!liquidMode) {
							p_155028_.set(2, new ItemStack(Items.LAVA_BUCKET));
							p_155028_.get(3).shrink(1);
						}
					} else {
						if (!liquidMode) {
							currentResultStack.grow(currentResultStack.getCount());
							this.cookingProgress = 0;
						}
					}
				}
			} else {
				if (tank.getFluidAmount() < tank.getCapacity()) {
					tank.fill(new FluidStack(Fluids.LAVA, 1000), FluidAction.EXECUTE);
				} else {
					if (p_155028_.get(3).isEmpty()) {
						if (currentResultStack.isEmpty()) {
							p_155028_.set(2, resultStack.copy());
						} else if (currentResultStack.is(resultStack.getItem())) {
							currentResultStack.grow(resultStack.getCount());
						}
					} else {
						if (currentResultStack.isEmpty()) {
							p_155028_.set(2, new ItemStack(Items.LAVA_BUCKET));
							p_155028_.get(3).shrink(1);
						} else {
							currentResultStack.grow(currentResultStack.getCount());
							this.cookingProgress = 0;
						}
					}
				}
			}
			itemstack.shrink(1);
			return true;
		} else {
			return false;
		}
	}

	protected int getBurnDuration(ItemStack p_58343_) {
		if (p_58343_.isEmpty()) {
			return litDuration;
		} else {
			p_58343_.getItem();
			return net.minecraftforge.common.ForgeHooks.getBurnTime(p_58343_, this.recipeType);
		}
	}

	@SuppressWarnings("unchecked")
	private static int getTotalCookTime(Level p_155010_, RecipeType<? extends AbstractCookingRecipe> p_155011_,
			Container p_155012_) {
		return p_155010_.getRecipeManager()
				.getRecipeFor((RecipeType<AbstractCookingRecipe>) p_155011_, p_155012_, p_155010_)
				.map(AbstractCookingRecipe::getCookingTime).orElse(200);
	}

	public static boolean isFuel(ItemStack p_58400_) {
		return net.minecraftforge.common.ForgeHooks.getBurnTime(p_58400_, null) > 0;
	}

	public int[] getSlotsForFace(Direction direction) {
		switch (direction) {
		case DOWN:
			return SLOTS_FOR_DOWN;
		case EAST:
			return SLOTS_FOR_EAST;
		case SOUTH:
			return SLOTS_FOR_SOUTH;
		case UP:
			return SLOTS_FOR_UP;
		default:
			return SLOTS_FOR_SIDES;
		}
	}

	public boolean canPlaceItemThroughFace(int p_58336_, ItemStack p_58337_, @Nullable Direction p_58338_) {
		return this.canPlaceItem(p_58336_, p_58337_);
	}

	public boolean canTakeItemThroughFace(int p_58392_, ItemStack p_58393_, Direction p_58394_) {
		return (p_58394_ == Direction.DOWN && p_58392_ == 1)
				? p_58393_.is(Items.WATER_BUCKET) || p_58393_.is(Items.BUCKET)
				: true;
	}

	public int getContainerSize() {
		return this.items.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getItem(int p_58328_) {
		return this.items.get(p_58328_);
	}

	public ItemStack removeItem(int p_58330_, int p_58331_) {
		return ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
	}

	public ItemStack removeItemNoUpdate(int p_58387_) {
		return ContainerHelper.takeItem(this.items, p_58387_);
	}

	public void setItem(int p_58333_, ItemStack p_58334_) {
		ItemStack itemstack = this.items.get(p_58333_);
		boolean flag = !p_58334_.isEmpty() && p_58334_.sameItem(itemstack) && ItemStack.tagMatches(p_58334_, itemstack);
		this.items.set(p_58333_, p_58334_);
		if (p_58334_.getCount() > this.getMaxStackSize()) {
			p_58334_.setCount(this.getMaxStackSize());
		}
		if (p_58333_ == 0 && !flag) {
			this.cookingTotalTime = getTotalCookTime(this.level, this.recipeType, this);
			this.cookingProgress = 0;
			this.setChanged();
		}

	}

	public boolean stillValid(Player player) {
		return (this.level.getBlockEntity(this.worldPosition) != this) ? false
				: player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
						(double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	public boolean canPlaceItem(int p_58389_, ItemStack p_58390_) {
		return super.canPlaceItem(p_58389_, p_58390_);

	}

	public void clearContent() {
		this.items.clear();
	}

	public void setRecipeUsed(@Nullable Recipe<?> p_58345_) {
		if (p_58345_ != null) {
			ResourceLocation resourcelocation = p_58345_.getId();
			this.recipesUsed.addTo(resourcelocation, 1);
		}
	}

	@Nullable
	public Recipe<?> getRecipeUsed() {
		return null;
	}

	public void awardUsedRecipesAndPopExperience(ServerPlayer p_155004_) {
		List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(p_155004_.getLevel(), p_155004_.position());
		p_155004_.awardRecipes(list);
		this.recipesUsed.clear();
	}

	public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel p_154996_, Vec3 p_154997_) {
		List<Recipe<?>> list = Lists.newArrayList();
		for (Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			p_154996_.getRecipeManager().byKey(entry.getKey()).ifPresent((p_155023_) -> {
				list.add(p_155023_);
				createExperience(p_154996_, p_154997_, entry.getIntValue(),
						((AbstractCookingRecipe) p_155023_).getExperience());
			});
		}

		return list;
	}

	private static void createExperience(ServerLevel p_154999_, Vec3 p_155000_, int p_155001_, float p_155002_) {
		int i = Mth.floor((float) p_155001_ * p_155002_);
		float f = Mth.frac((float) p_155001_ * p_155002_);
		if (f != 0.0F && Math.random() < (double) f) {
			++i;
		}

		ExperienceOrb.award(p_154999_, p_155000_, i);
	}

	public void fillStackedContents(StackedContents p_58342_) {
		for (ItemStack itemstack : this.items) {
			p_58342_.accountStack(itemstack);
		}

	}

	LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers = SidedInvWrapper.create(this,
			Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH);

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(
			net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			switch (facing) {
			case UP:
				return handlers[0].cast();
			case DOWN:
				return handlers[1].cast();
			case EAST:
				return handlers[3].cast();
			case SOUTH:
				return handlers[4].cast();
			default:
				return handlers[2].cast();
			}
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return holder.cast();

		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (int x = 0; x < handlers.length; x++) {
			handlers[x].invalidate();
		}
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN,
				Direction.NORTH, Direction.EAST, Direction.SOUTH);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.lavamotron");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return new LavamotronMenu(id, player, this);
	}

	public ItemStack handleContainerInteraction(ItemStack stack, Player player) {
		PlayerInvWrapper inv = new PlayerInvWrapper(player.getInventory());

		FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, tank, inv, Integer.MAX_VALUE, player,
				!getLevel().isClientSide());
		if (result.isSuccess()) {
			return result.getResult();
		}

		result = FluidUtil.tryFillContainerAndStow(stack, tank, inv, Integer.MAX_VALUE, player,
				!getLevel().isClientSide());
		return result.getResult();
	}

//NBT STUFF
	public void load(CompoundTag p_155025_) {
		super.load(p_155025_);
		tank.readFromNBT(p_155025_);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(p_155025_, this.items);
		this.litTime = p_155025_.getInt("BurnTime");
		this.cookingProgress = p_155025_.getInt("CookTime");
		this.cookingTotalTime = p_155025_.getInt("CookTimeTotal");
		this.litDuration = this.getBurnDuration(this.items.get(1));
		CompoundTag compoundtag = p_155025_.getCompound("RecipesUsed");
		for (String s : compoundtag.getAllKeys()) {
			this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
		}
		this.liquidMode = p_155025_.getBoolean("LiquidMode");
	}

	@Override
	public void saveAdditional(CompoundTag p_58379_) {
		super.saveAdditional(p_58379_);
		tank.writeToNBT(p_58379_);
		p_58379_.putInt("BurnTime", this.litTime);
		p_58379_.putInt("CookTime", this.cookingProgress);
		p_58379_.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(p_58379_, this.items);
		CompoundTag compoundtag = new CompoundTag();
		this.recipesUsed.forEach((p_58382_, p_58383_) -> {
			compoundtag.putInt(p_58382_.toString(), p_58383_);
		});
		p_58379_.put("RecipesUsed", compoundtag);
		p_58379_.putBoolean("LiquidMode", this.liquidMode);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		tank.readFromNBT(tag);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items);
		this.litTime = tag.getInt("BurnTime");
		this.cookingProgress = tag.getInt("CookTime");
		this.cookingTotalTime = tag.getInt("CookTimeTotal");
		this.litDuration = this.getBurnDuration(this.items.get(1));
		CompoundTag compoundtag = tag.getCompound("RecipesUsed");
		for (String s : compoundtag.getAllKeys()) {
			this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
		}
		this.liquidMode = tag.getBoolean("LiquidMode");
	}

	@Override
	public final CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tank.writeToNBT(tag);
		tag.putInt("BurnTime", this.litTime);
		tag.putInt("CookTime", this.cookingProgress);
		tag.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(tag, this.items);
		CompoundTag compoundtag = new CompoundTag();
		this.recipesUsed.forEach((p_58382_, p_58383_) -> {
			compoundtag.putInt(p_58382_.toString(), p_58383_);
		});
		tag.put("RecipesUsed", compoundtag);
		tag.putBoolean("LiquidMode", this.liquidMode);
		return tag;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag p_155025_ = pkt.getTag();
		tank.readFromNBT(p_155025_);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(p_155025_, this.items);
		this.litTime = p_155025_.getInt("BurnTime");

		this.cookingProgress = p_155025_.getInt("CookTime");
		this.cookingTotalTime = p_155025_.getInt("CookTimeTotal");
		this.litDuration = this.getBurnDuration(this.items.get(1));
		CompoundTag compoundtag = p_155025_.getCompound("RecipesUsed");
		for (String s : compoundtag.getAllKeys()) {
			this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
		}
		this.liquidMode = p_155025_.getBoolean("LiquidMode");

	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public boolean getLiquidMode() {
		return liquidMode;
	}

	public void setLiquidMode(boolean liquidModeIn) {
		liquidMode = liquidModeIn;
		this.sendUpdates();
	}

	public void sendUpdates() {
		level.setBlocksDirty(worldPosition, getBlockState(), getBlockState());
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		setChanged();
	}
}
