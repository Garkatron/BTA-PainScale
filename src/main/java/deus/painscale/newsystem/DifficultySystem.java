package deus.painscale.newsystem;

public class DifficultySystem {

	public static final HierarchicalFactorManager worldManager = new HierarchicalFactorManager(null);
	public static final Factor worldDifficulty = new Factor("world.difficulty", new Level(10000, 100));

	public static void initialize() {
		worldManager.register(worldDifficulty);
	}
}
