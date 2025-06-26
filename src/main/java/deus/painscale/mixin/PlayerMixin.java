package deus.painscale.mixin;

import com.mojang.nbt.tags.CompoundTag;
import deus.painscale.PainScaleMod;
import deus.painscale.api.IPlayer;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Player.class)
public abstract class PlayerMixin implements IPlayer {

	@Shadow
	@Final
	private int damageRemainder;
	@Shadow
	public float cameraVelocity;

	@Shadow
	public abstract void readAdditionalSaveData(@NotNull CompoundTag tag);

	@Unique protected int dfLevel = 0;
	@Unique protected int dfPoints = 0;
	@Unique protected int dfPointsToGrow = 0;
	@Unique protected int dfPointsToGrowMultiplier = 0;

	@Override
	public int ps$getDifficultyLevel() {
		return dfLevel;
	}

	@Override
	public int ps$getDifficultyPoints() {
		return dfPoints;
	}

	@Override
	public void ps$addPoints(int amount) {
		while (amount > 0) {
			dfPoints += amount;
			int remainingToLevelUp = dfPointsToGrow;

			if (amount >= remainingToLevelUp) {
				//  Grow Up
				dfLevel = Math.min(dfLevel + 1, PainScaleMod.CFG.getInt("max_df_level"));
				amount -= remainingToLevelUp;

				// Re-Calc next points to grow up
				dfPointsToGrowMultiplier += PainScaleMod.CFG.getInt("min_df_points_multiplier");
				dfPointsToGrow = PainScaleMod.CFG.getInt("min_df_points_to_grow_up") * dfPointsToGrowMultiplier;
			} else {
				dfPointsToGrow -= amount;
				amount = 0;
			}
		}
	}

	@Override
	public void ps$subPoints(int amount) {
		dfPoints -= amount;
		dfPointsToGrow += amount;
	}

	@Override
	public void ps$addLevels(int amount) {
		for (int i = 0; i < amount; i++) {
			ps$addPoints(dfPointsToGrow);
		}
	}

	@Override
	public void ps$subLevels(int amount) {
		dfLevel = Math.min(dfLevel + 1, PainScaleMod.CFG.getInt("min_df_level"));
	}

	@Override
	public void ps$resetMultiplier() {
		dfPointsToGrowMultiplier = PainScaleMod.CFG.getInt("min_df_points_multiplier");
		dfPointsToGrow = PainScaleMod.CFG.getInt("min_df_points_to_grow_up") * dfPointsToGrowMultiplier;
	}
}
