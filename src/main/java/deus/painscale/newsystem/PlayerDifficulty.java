package deus.painscale.newsystem;

public class PlayerDifficulty {
	public Factor melee;
	public Factor distance;
	public Factor survival;
	public Factor playerGlobal;

	public PlayerDifficulty() {
		melee        = new Factor("melee", new Level(3, 1, 50, 0), 0.4);
		distance     = new Factor("distance", new Level(3, 1, 50, 0), 0.3);
		survival     = new Factor("survival", new Level(3, 1, 50, 0), 0.3);
		playerGlobal = new Factor("playerGlobal", new Level(100, 1, 100, 0), 1.0);

		playerGlobal.addDependency(melee);
		playerGlobal.addDependency(distance);
		playerGlobal.addDependency(survival);

		DifficultySystem.WORLD_DIFFICULTY.addDependency(playerGlobal);
	}
}
