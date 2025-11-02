package deus.painscale.mixin.mobs.mob_monster;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.nbt.tags.CompoundTag;
import deus.painscale.PainScale;
import deus.painscale.api.IPainScaleMob;
import deus.painscale.api.IPainScaleMobMonster;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobMonster.class, remap = false)
public class MobMonsterMixin extends MobPathfinder implements IPainScaleMobMonster {

	@Shadow(remap = false) protected int attackStrength;

	@Unique double attackPowerMultiplier = 1.0;


	@Unique int dfLevel = 0;

	@Unique
	double healthMultiplier = 1.0;

	public MobMonsterMixin(@Nullable World world) {
		super(world);
	}

	@Override
	public void onDeath(Entity entityKilledBy) {
		super.onDeath(entityKilledBy);
		/*if (entityKilledBy instanceof Player player) {
			IPainScalePlayer p = (IPainScalePlayer) player;
			IPainScaleMob mob = (IPainScaleMob) (Object) this;
			int points = (int) Math.max(PainScale.CFG.getInt("Points.base_points_per_monster"), mob.ps$getPointsMultiplier() * dfLevel);
			p.ps$addPoints(points);
		}*/
	}

	@Override
	public void ps$setAttackPowerMultiplier(double multiplier) {
		this.attackPowerMultiplier = multiplier;
	}

	@Override
	public double ps$getAttackPower() {
		return attackStrength * attackPowerMultiplier;
	}

	@Override
	public double ps$getAttackMultiplier() {
		return attackPowerMultiplier;
	}

	@Override
	public void ps$setHealthMultiplier(double multiplier) {
		this.healthMultiplier = multiplier;
	}

	@Override
	public int ps$getBaseAttackPower() {
		return this.attackStrength;
	}

	@Override
	public int ps$getDfLevel() {
		return dfLevel;
	}

	@Override
	public void ps$setDfLevel(int level) {
		this.dfLevel = level;
	}

	@Override
	public double ps$getHealthMultiplier() {
		return healthMultiplier;
	}

//	@Inject(method = "hurt", at = @At("TAIL"), remap = false)
//	public void test(Entity attacker, int i, DamageType type, CallbackInfoReturnable<Boolean> cir) {
//		if (attacker instanceof Player player) {
//			((IPainScalePlayer)player).ps$addPoints(PainScaleMod.CFG.getInt());
//		}
//	}

	@Redirect(
		method = "attackEntity(Lnet/minecraft/core/entity/Entity;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/entity/Entity;hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z"
		), remap = false
	)
	private boolean redirectAttackEntity(Entity instance, Entity attacker, int baseDamage, DamageType type) {
		Entity e = (Entity) (Object) this;
		if (PainScale.CFG.getBoolean("Enemies.scale_monster_strength")) {
			float newDamage = (float)(this.attackStrength * attackPowerMultiplier);
			return instance.hurt(attacker, (int) newDamage, type);
		} else {
			return instance.hurt(attacker, baseDamage, type);
		}
	}

	@ModifyReturnValue(
		method = "getMaxHealth", at = @At("RETURN"), remap = false)
    private int modifyGetMaxHealth(int original) {
		Entity e = (Entity) (Object) this;
		if (PainScale.CFG.getBoolean("Enemies.scale_monster_health")) {
			return (int) Math.max(Math.round(original * healthMultiplier), original);
		} else {
			return original;
		}
    }



	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("PsDfLevel", dfLevel);
		//tag.putInt("PsDfPoints", dfPoints);
		tag.putDouble("PsDfAttackPowerMultiplier", attackPowerMultiplier);
		tag.putDouble("PsHealthMultiplier", healthMultiplier);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		dfLevel = tag.getInteger("PsDfLevel");
		//dfPoints = tag.getInteger("PsDfPoints");
		attackPowerMultiplier = tag.getDouble("PsDfAttackPowerMultiplier");
		healthMultiplier = tag.getDouble("PsHealthMultiplier");
	}



}
