package deus.painscale.mixin.player;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.nbt.tags.CompoundTag;
import deus.painscale.PainScaleMod;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static deus.painscale.PainScaleMod.DAY_SURVIVED_MESSAGE;
import static deus.painscale.PainScaleMod.MORE_HEARTS;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin extends Mob implements IPainScalePlayer {

	@Unique protected int dfLevel = 0;
	@Unique protected int dfPoints = 0;
	@Unique protected int dfPointsToGrow = 10;
	@Unique protected double dfPointsToGrowMultiplier = 0;
	@Unique int survivedDayCount = 0;
	@Unique boolean isFirstTick = true;
	@Unique private int maxHealth = 20;
	@Unique int dayCountLastTick = -1; // Initialize to -1 to ensure first day is detected
	@Unique private boolean killedBy = false;

	@Shadow(remap = false) @Final private int damageRemainder;
	@Shadow(remap = false) public float cameraVelocity;
	@Shadow(remap = false) public abstract void readAdditionalSaveData(@NotNull CompoundTag tag);
	@Shadow public abstract void addAdditionalSaveData(@NotNull CompoundTag tag);
	@Shadow public int portalID;
	@Shadow public abstract void sendMessage(String string);

	public PlayerMixin(@Nullable World world) {
		super(world);
	}

	@Override public int ps$getDifficultyLevel() {
		return dfLevel;
	}

	@Override public int ps$getDifficultyPoints() {
		return dfPoints;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("PsDfLevel", dfLevel);
		tag.putInt("PsDfPoints", dfPoints);
		tag.putInt("PsDfPointsRemaining", dfPointsToGrow);
		tag.putDouble("PsDfPointsToGrowMultiplier", dfPointsToGrowMultiplier);
		tag.putInt("PsDfMaxHealth", Math.max(maxHealth, 20));
		tag.putInt("PsDfSurvivedDayCount", survivedDayCount);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		dfLevel = tag.getInteger("PsDfLevel");
		dfPoints = tag.getInteger("PsDfPoints");
		maxHealth = Math.max(tag.getInteger("PsDfMaxHealth"), 20);
		dfPointsToGrow = tag.getInteger("PsDfPointsRemaining");
		dfPointsToGrowMultiplier = tag.getDouble("PsDfPointsToGrowMultiplier");
		survivedDayCount = tag.getInteger("PsDfSurvivedDayCount");
	}

	@ModifyReturnValue(
		method = "getMaxHealth", at = @At("RETURN"), remap = false)
	private int modifyGetMaxHealth(int original) {
		if (world.getGameRuleValue(MORE_HEARTS)) {
			return Math.max(maxHealth, 20);
		} else {
			return 20;
		}
	}

	@Override
	public boolean ps$wasKilledByPlayer() {
		return killedBy;
	}

	// Linear scaling | Level * Multiplier = Result
	@Override
	public void ps$addPoints(int amount) {
		while (amount > 0) {
			dfPoints += amount;
			int remainingToLevelUp = dfPointsToGrow;

			if (amount >= remainingToLevelUp) {
				dfLevel = Math.min(dfLevel + 1, PainScaleMod.CFG.getInt("Levels.max_df_level"));
				amount -= remainingToLevelUp;

				dfPointsToGrowMultiplier += PainScaleMod.CFG.getDouble("Levels.min_df_points_multiplier");
				dfPointsToGrow = (int) Math.round(
					PainScaleMod.CFG.getInt("Levels.min_df_points_to_grow_up") * dfPointsToGrowMultiplier
				);
			} else {
				dfPointsToGrow -= amount;
				amount = 0;
			}
		}
	}

	@Inject(method = "tick", at = @At("TAIL"), remap = false)
	public void postTick(CallbackInfo ci) {
		if (world != null) {
			int currentDayCount = (int) (world.getLevelData().getWorldTime() / 24000L);
			if (isFirstTick) {
				dayCountLastTick = currentDayCount; // Set to current day on first tick
				isFirstTick = false;
			} else if (currentDayCount != dayCountLastTick) {
				dayCountLastTick = currentDayCount;
				survivedDayCount++;
				int points = PainScaleMod.CFG.getInt("Points.gain_per_survived_day_per_level") * dfLevel;
				ps$addPoints(points);
				if (world.getGameRuleValue(DAY_SURVIVED_MESSAGE)) {
					sendMessage("You have survived " + survivedDayCount + " days!");
					sendMessage("You have earned " + points + " points!");
				}

			}
		}
	}

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("TAIL"), remap = false)
	public void addPointsOnAttackTargetEntityWithCurrentItem(Entity entity, CallbackInfo ci) {
		ps$addPoints(PainScaleMod.CFG.getInt("Points.gain_on_monster_attack"));
	}

	@Inject(method = "onDeath", at = @At("TAIL"), remap = false)
	public void losePointsWhenKilledBy(Entity entityKilledBy, CallbackInfo ci) {
		if (entityKilledBy instanceof Player) {
			killedBy = true;
		}
	}

	@Override
	public void ps$subPoints(int amount) {
		if (dfPoints >= amount) {
			dfPoints -= amount;
			dfPointsToGrow += amount;
		} else {
			int spent = dfPoints;
			dfPoints = 0;

			dfPointsToGrow += spent;

			if (dfLevel > PainScaleMod.CFG.getInt("Levels.min_df_level")) {
				dfLevel--;
			}
		}
	}

	@Override
	public void ps$addLevels(int amount) {
		for (int i = 0; i < amount; i++) {
			ps$addPoints(dfPointsToGrow);
		}
	}

	@Override
	public void ps$subLevels(int amount) {
		int minLevel = PainScaleMod.CFG.getInt("Levels.min_df_level");
		for (int i = 0; i < amount; i++) {
			if (dfLevel > minLevel) {
				dfLevel--;

			} else {
				break;
			}
		}
	}


	@Override
	public void ps$resetMultiplier() {
		dfPointsToGrowMultiplier = PainScaleMod.CFG.getDouble("Levels.min_df_points_multiplier");
	}

	@Override
	public void ps$resetPoints() {
		dfPointsToGrow = (int) Math.round(PainScaleMod.CFG.getInt("Levels.min_df_points_to_grow_up") * dfPointsToGrowMultiplier);
		dfPoints = 0;
	}

	@Override
	public void ps$resetLevels() {
		dfLevel = Math.min(dfLevel + 1, PainScaleMod.CFG.getInt("Levels.min_df_level"));
		ps$resetPoints();
	}

	@Override
	public int ps$getRemainingPoints() {
		return dfPointsToGrow;
	}

	@Override
	public void ps$setPointsMultiplier(double points) {
		dfPointsToGrowMultiplier = points;
	}

	@Override
	public void ps$setDifficultyLevels(int levels) {
		dfLevel = levels;
	}

	@Override
	public void ps$setDifficultyPoints(int points) {
		dfPoints = points;
	}

	@Override
	public void ps$setRemainingPoints(int points) {
		dfPointsToGrow = points;
	}

	@Override
	public double ps$getPointsMultiplier() {
		return dfPointsToGrowMultiplier;
	}

	@Override
	public void ps$setMaxHealth(int value) {
		if (world.getGameRuleValue(MORE_HEARTS)) {
			this.maxHealth = Math.max(value, 20);
			setHealthRaw(Math.max(value, 20));
		}
	}

	@Override
	public void ps$setDifficultyLevel(int level) {
		dfLevel = level;
	}
}
