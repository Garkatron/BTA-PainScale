package deus.painscale.api;

public interface IPlayer {

	int ps$getDifficultyLevel();
	int ps$getDifficultyPoints();

	void ps$addPoints(int amount);
	void ps$subPoints(int amount);

	void ps$addLevels(int amount);
	void ps$subLevels(int amount);

	void ps$resetMultiplier();

}
