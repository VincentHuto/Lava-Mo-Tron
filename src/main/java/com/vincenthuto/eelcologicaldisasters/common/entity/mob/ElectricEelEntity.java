package com.vincenthuto.eelcologicaldisasters.common.entity.mob;

import java.util.List;
import java.util.function.Predicate;

import com.vincenthuto.eelcologicaldisasters.init.ItemInit;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ElectricEelEntity extends AbstractFish {
	private static final EntityDataAccessor<Integer> PUFF_STATE = SynchedEntityData.defineId(ElectricEelEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData
			.defineId(ElectricEelEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData
			.defineId(ElectricEelEntity.class, EntityDataSerializers.INT);

	public final AnimationState idleAnimationState = new AnimationState();
	private int oldSwell;
	private int swell;
	private int maxSwell = 30;

	int inflateCounter;
	int deflateTimer;
	private static final Predicate<LivingEntity> SCARY_MOB = (p_289442_) -> {
		return p_289442_.getType() == EntityType.AXOLOTL || p_289442_.getMobType() != MobType.WATER;
	};
	static final TargetingConditions targetingConditions = TargetingConditions.forNonCombat()
			.ignoreInvisibilityTesting().ignoreLineOfSight().selector(SCARY_MOB);
	public static final int STATE_SMALL = 0;
	public static final int STATE_MID = 1;
	public static final int STATE_FULL = 2;

	public ElectricEelEntity(EntityType<? extends ElectricEelEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.refreshDimensions();
	}

	public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		Item item = itemstack.getItem();

		float healthNeeded = this.getMaxHealth() - this.getHealth();
		if (!this.level().isClientSide && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
			this.heal(healthNeeded);
			if (!pPlayer.getAbilities().instabuild) {
				itemstack.shrink(1);
				this.level().broadcastEntityEvent(this, (byte) 7);
			}

			this.gameEvent(GameEvent.EAT, this);
			return InteractionResult.SUCCESS;
		} else {

			return super.mobInteract(pPlayer, pHand);
		}
	}

	public void handleEntityEvent(byte pId) {
		if (pId == 7) {
			this.spawnTamingParticles(true);
		} else if (pId == 6) {
			this.spawnTamingParticles(false);
		} else {
			super.handleEntityEvent(pId);
		}

	}

	protected void spawnTamingParticles(boolean pTamed) {
		ParticleOptions particleoptions = ParticleTypes.HEART;
		if (!pTamed) {
			particleoptions = ParticleTypes.SMOKE;
		}

		for (int i = 0; i < 7; ++i) {
			double d0 = this.random.nextGaussian() * 0.02D;
			double d1 = this.random.nextGaussian() * 0.02D;
			double d2 = this.random.nextGaussian() * 0.02D;
			this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D,
					this.getRandomZ(1.0D), d0, d1, d2);
		}

	}

	private boolean isFood(ItemStack itemstack) {
		return itemstack.getItem() == ItemInit.car_battery.get();
	}

	public static AttributeSupplier.Builder setAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 7.0D).add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.FOLLOW_RANGE, 5.0D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SWELL_DIR, -1);
		this.entityData.define(PUFF_STATE, 0);
		this.entityData.define(DATA_IS_IGNITED, false);
	}

	public int getSwellDir() {
		return this.entityData.get(DATA_SWELL_DIR);
	}

	public boolean isIgnited() {
		return this.entityData.get(DATA_IS_IGNITED);
	}

	public void ignite() {
		this.entityData.set(DATA_IS_IGNITED, true);
	}

	public void setSwellDir(int pState) {
		this.entityData.set(DATA_SWELL_DIR, pState);
	}

	public int getPuffState() {
		return this.entityData.get(PUFF_STATE);
	}

	public void setPuffState(int pPuffState) {
		this.entityData.set(PUFF_STATE, pPuffState);
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
		if (PUFF_STATE.equals(pKey)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(pKey);
	}

	public void addAdditionalSaveData(CompoundTag pCompound) {
		super.addAdditionalSaveData(pCompound);
		pCompound.putInt("PuffState", this.getPuffState());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
		this.setPuffState(Math.min(pCompound.getInt("PuffState"), 2));
		if (pCompound.contains("Fuse", 99)) {
			this.maxSwell = pCompound.getShort("Fuse");
		}

		if (pCompound.getBoolean("ignited")) {
			this.ignite();
		}
	}

	public ItemStack getBucketItemStack() {
		return new ItemStack(Items.PUFFERFISH_BUCKET);
	}

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new ElectricEelEntity.ElectricEelEntityPuffGoal(this));
		this.goalSelector.addGoal(2, new EelSwellGoal(this));
		this.targetSelector.addGoal(1, new EelAttackableTargetGoal<>(this, Player.class, false));
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {

		if (this.level().isClientSide()) {
			this.idleAnimationState.startIfStopped(this.tickCount);

		}

		if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
			if (this.inflateCounter > 0) {
				if (this.getPuffState() == 0) {
					this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
					this.setPuffState(1);
				} else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
					this.setPuffState(2);
				}

				++this.inflateCounter;
			} else if (this.getPuffState() != 0) {
				if (this.deflateTimer > 60 && this.getPuffState() == 2) {
					this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
					this.setPuffState(1);
				} else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
					this.setPuffState(0);
				}

				++this.deflateTimer;
			}
		}

		if (this.isAlive()) {
			this.oldSwell = this.swell;
			if (this.isIgnited()) {
				this.setSwellDir(1);
			}

			int i = this.getSwellDir();
			if (i > 0 && this.swell == 0) {
				this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
				this.gameEvent(GameEvent.PRIME_FUSE);
			}

			this.swell += i;
			if (this.swell < 0) {
				this.swell = 0;
			}

			if (this.swell >= this.maxSwell) {
				this.swell = this.maxSwell;

			}
		}

		super.tick();
	}

	/**
	 * Params: (Float)Render tick. Returns the intensity of the creeper's flash when
	 * it is ignited.
	 */
	public float getSwelling(float pPartialTicks) {
		return Mth.lerp(pPartialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
	}

	public void aiStep() {
		super.aiStep();
		if (this.isAlive() && this.swell <= this.maxSwell) {
			for (Mob mob : this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(2.3D),
					(p_149013_) -> {
						return targetingConditions.test(this, p_149013_);
					})) {
				if (mob.isAlive()) {
					this.touch(mob);
				}
			}
		}

	}

	private void touch(Mob pMob) {
		int i = this.getPuffState();
		if (pMob.hurt(this.damageSources().mobAttack(this), (float) (1 + i))) {
			pMob.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), this);
			this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
		}

	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void playerTouch(Player pEntity) {
		int i = this.getPuffState();
		if (pEntity instanceof ServerPlayer && this.swell <= this.maxSwell
				&& pEntity.hurt(this.damageSources().mobAttack(this), (float) (1 + i))) {
			if (!this.isSilent()) {
				((ServerPlayer) pEntity).connection
						.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
			}

			pEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), this);
		}

	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.PUFFER_FISH_AMBIENT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.PUFFER_FISH_DEATH;
	}

	protected SoundEvent getHurtSound(DamageSource pDamageSource) {
		return SoundEvents.PUFFER_FISH_HURT;
	}

	protected SoundEvent getFlopSound() {
		return SoundEvents.PUFFER_FISH_FLOP;
	}

	public EntityDimensions getDimensions(Pose pPose) {
		return super.getDimensions(pPose).scale(getScale(this.getPuffState()));
	}

	private static float getScale(int pPuffState) {
		switch (pPuffState) {
		case 0:
			return 0.5F;
		case 1:
			return 0.7F;
		default:
			return 1.0F;
		}
	}

	static class ElectricEelEntityPuffGoal extends Goal {
		private final ElectricEelEntity fish;

		public ElectricEelEntityPuffGoal(ElectricEelEntity pFish) {
			this.fish = pFish;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			List<LivingEntity> list = this.fish.level().getEntitiesOfClass(LivingEntity.class,
					this.fish.getBoundingBox().inflate(2.0D), (p_149015_) -> {
						return ElectricEelEntity.targetingConditions.test(this.fish, p_149015_);
					});
			return !list.isEmpty();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			this.fish.inflateCounter = 1;
			this.fish.deflateTimer = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			this.fish.inflateCounter = 0;
		}
	}
}