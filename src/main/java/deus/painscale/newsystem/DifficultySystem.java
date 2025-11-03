package deus.painscale.newsystem;

public class DifficultySystem {

	public static Factor WORLD_DIFFICULTY;

	public static void initialize() {
		WORLD_DIFFICULTY = new Factor("worldDifficulty", new Level(100,1,100,0), 1.0);
	}
}
