package deus.painscale.api;

public interface IPainScaleMobMonster {
	void ps$setAttackPowerMultiplier(double multiplier);
	double ps$getAttackPower();
	double ps$getAttackMultiplier();
	void ps$setHealthMultiplier(double multiplier);
	int ps$getBaseAttackPower();
	int ps$getDfLevel();
	void ps$setDfLevel(int level);
	double ps$getHealthMultiplier();
	void ps$setDfPoints(int points);
}
