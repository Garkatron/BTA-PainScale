package deus.painscale.api;

import deus.painscale.newsystem.Level;
import deus.painscale.newsystem.PlayerManager;

public interface IPainScalePlayer {

	Level ps$getDifficultyLevel();

	PlayerManager ps$getManager();

	void ps$setMaxHealth(int value);
	boolean ps$wasKilledByPlayer();
}
