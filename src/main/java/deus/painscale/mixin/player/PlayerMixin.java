package deus.painscale.mixin.player;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.nbt.tags.CompoundTag;
import deus.painscale.api.IPainScalePlayer;
import deus.painscale.newsystem.Level;
import deus.painscale.newsystem.PlayerManager;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static deus.painscale.PainScale.*;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin extends Mob implements IPainScalePlayer {
	@Unique int survivedDayCount = 0;
	@Unique boolean isFirstTick = true;

	@Unique private int maxHealth = 20; // ! Unused if catalyst-effects is present!.
	@Unique int dayCountLastTick = -1; // Initialize to -1 to ensure first day is detected
	@Unique private boolean killedBy = false;

	@Unique private PlayerManager playerManager;

	@Shadow(remap = false) public abstract void readAdditionalSaveData(@NotNull CompoundTag tag);
	@Shadow public abstract void addAdditionalSaveData(@NotNull CompoundTag tag);
	@Shadow public abstract void sendMessage(String string);

	public PlayerMixin(@Nullable World world) {
		super(world);
	}


	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void init(World world, CallbackInfo ci) {
		playerManager = new PlayerManager();
	}


	@Override
	public boolean ps$wasKilledByPlayer() {
		return killedBy;
	}


	@Override
	public PlayerManager ps$getManager() {
		return playerManager;
	}

	@Inject(method = "tick", at = @At("TAIL"), remap = false)
	public void postTick(CallbackInfo ci) {
		if (world != null) {
			int currentDayCount = (int) (world.getLevelData().getWorldTime() / 24000L);
			if (isFirstTick) {
				dayCountLastTick = currentDayCount;
				isFirstTick = false;
			} else if (currentDayCount != dayCountLastTick) {
				dayCountLastTick = currentDayCount;
				survivedDayCount++;
				playerManager.survival.addPoints(20);
				if (world.getGameRuleValue(DAY_SURVIVED_MESSAGE)) {
					sendMessage("You have survived " + survivedDayCount + " days!");
				}
			}
		}
	}

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("TAIL"), remap = false)
	public void addPointsOnAttackTargetEntityWithCurrentItem(Entity entity, CallbackInfo ci) {
		playerManager.melee.addPoints(50);
		System.out.println(playerManager.melee.getLevel());
	}

	@Inject(method = "onDeath", at = @At("TAIL"), remap = false)
	public void losePointsWhenKilledBy(Entity entityKilledBy, CallbackInfo ci) {
		if (entityKilledBy instanceof Player) {
			killedBy = true;
		}
	}


	@Override
	public Level ps$getDifficultyLevel() {

		return playerManager.global.getLevel();
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("PsDfMaxHealth", Math.max(maxHealth, 20));
		tag.putInt("PsDfSurvivedDayCount", survivedDayCount);

		/*
		CompoundTag meleeTag = new CompoundTag();
		meleeTag.putCompound("melee", playerManager.melee.getLevel().toTag());
		tag.put("FactorMelee", meleeTag);

		CompoundTag distanceTag = new CompoundTag();
		distanceTag.putCompound("distance", playerManager.distance.getLevel().toTag());
		tag.put("FactorDistance", distanceTag);

		CompoundTag survivalTag = new CompoundTag();
		survivalTag.putCompound("survival", playerManager.survival.getLevel().toTag());
		tag.put("FactorSurvival", survivalTag);
		*/

	}


	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"), remap = false)
	public void modifiedReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		maxHealth = Math.max(tag.getInteger("PsDfMaxHealth"), 20);
		survivedDayCount = tag.getInteger("PsDfSurvivedDayCount");

		if (tag.containsKey("FactorMelee")) {
			CompoundTag meleeTag = tag.getCompound("FactorMelee");
			playerManager.melee.getLevel().loadFromCompound(meleeTag.getCompound("melee"));
		}

		if (tag.containsKey("FactorDistance")) {
			CompoundTag distanceTag = tag.getCompound("FactorDistance");
			playerManager.distance.getLevel().loadFromCompound(distanceTag.getCompound("distance"));
		}

		if (tag.containsKey("FactorSurvival")) {
			CompoundTag survivalTag = tag.getCompound("FactorSurvival");
			playerManager.survival.getLevel().loadFromCompound(survivalTag.getCompound("survival"));
		}
	}

	@ModifyReturnValue(
		method = "getMaxHealth", at = @At("RETURN"), remap = false)
	private int modifyGetMaxHealth(int original) {
		if (isCatalystPresent()) {
			return original;
		}
		if (world.getGameRuleValue(MORE_HEARTS)) {
			return Math.max(maxHealth, 20);
		} else {
			return 20;
		}
	}

	@Override
	public void ps$setMaxHealth(int value) {
		if (isCatalystPresent()) {
			LOGGER.warn("Don't use setMaxHealth if catalyst-effects is present!.");
			return;
		}
		if (world.getGameRuleValue(MORE_HEARTS)) {
			this.maxHealth = Math.max(value, 20);
			setHealthRaw(Math.max(value, 20));
		}
	}


}
