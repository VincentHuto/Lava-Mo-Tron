package com.vincenthuto.eelcologicaldisasters.common.entity.mob;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EelSwellGoal extends Goal {
	private final ElectricEelEntity electricEelEntity;
	@Nullable
	private LivingEntity target;

	public EelSwellGoal(ElectricEelEntity pElectricEelEntity) {
	      this.electricEelEntity = pElectricEelEntity;
	      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	   }

	/**
	 * Returns whether execution should begin. You can also read and cache any state
	 * necessary for execution in this method as well.
	 */
	public boolean canUse() {
		LivingEntity livingentity = this.electricEelEntity.getTarget();
		return this.electricEelEntity.getSwellDir() > 0
				|| livingentity != null && this.electricEelEntity.distanceToSqr(livingentity) < 9.0D;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		this.electricEelEntity.getNavigation().stop();
		this.target = this.electricEelEntity.getTarget();
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void stop() {
		this.target = null;
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		if (this.target == null) {
			this.electricEelEntity.setSwellDir(-1);
		} else if (this.electricEelEntity.distanceToSqr(this.target) > 49.0D) {
			this.electricEelEntity.setSwellDir(-1);
		} else if (!this.electricEelEntity.getSensing().hasLineOfSight(this.target)) {
			this.electricEelEntity.setSwellDir(-1);
		} else {
			this.electricEelEntity.setSwellDir(1);
		}
	}
}
