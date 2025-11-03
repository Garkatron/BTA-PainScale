package deus.painscale.api;

import deus.painscale.newsystem.Level;
import deus.painscale.newsystem.PlayerDifficulty;

public interface IPainScalePlayer {

	PlayerDifficulty ps$getPlayerDifficulty();


	void ps$setMaxHealth(int value);
	boolean ps$wasKilledByPlayer();
}
