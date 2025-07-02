package deus.painscale.api;

public interface IPainScalePlayer {

	int ps$getDifficultyLevel();
	int ps$getDifficultyPoints();
	double ps$getPointsMultiplier();

	void ps$addPoints(int amount);
	void ps$subPoints(int amount);

	void ps$addLevels(int amount);
	void ps$subLevels(int amount);

	void ps$resetMultiplier();

	void ps$resetLevels();

	void ps$resetPoints();

	int ps$getRemainingPoints();

	void ps$setDifficultyPoints(int points);
	void ps$setDifficultyLevels(int levels);
	void ps$setRemainingPoints(int points);
	void ps$setPointsMultiplier(double points);

	void ps$setMaxHealth(int value);
	void ps$setDifficultyLevel(int level);
	boolean ps$wasKilledByPlayer();
}
