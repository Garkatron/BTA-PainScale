package deus.painscale.api;

import deus.painscale.newsystem.Level;

public interface IPainScalePlayer {

	Level ps$getDifficultyLevel();

	void ps$addMeleePoints(int amount);
	void ps$addDistanceAttackPoints(int amount);

	void ps$setMaxHealth(int value);
	boolean ps$wasKilledByPlayer();
}
