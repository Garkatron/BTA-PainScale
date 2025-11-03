package deus.painscale.newsystem;

import com.mojang.nbt.tags.CompoundTag;
import deus.painscale.util.Signal;
import deus.painscale.util.Tuple;

public class Level {
	private int points;
	private int level;
	private int maxPoints;
	private int maxLevel;
	public static Signal<Tuple<Integer, Integer>> onChangeLevel = new Signal<>();
	public static Signal<Tuple<Integer, Integer>> onChangePoints = new Signal<>();

	// public final Signal<Integer> onLevelUp = new Signal<>();
	// public final Signal<Integer> onLevelDown = new Signal<>();
	// public final Signal<Integer> onChange = new Signal<>();

	public Level(int maxPoints, int maxLevel) {
		this.maxLevel = maxLevel;
		this.maxPoints = maxPoints;
		this.points = 1;
		this.level = 1;
	}

	public Level(int maxLevel, int level, int maxPoints, int points) {
		this.maxLevel = maxLevel;
		this.maxPoints = maxPoints;
		this.points = Math.max(1, points);
		this.level = Math.max(1, level);
	}


	public void reset() {
		this.resetPoints();
		this.resetLevel();
	}

	public void addPoints(int amount) {
		int last = points;
		points += amount;
		while (atMaxPoints()) {
			points -= maxPoints;
			if (level < maxLevel) level++;
			else {
				points = maxPoints;
				break;
			}
		}
		if (points!=last) onChangePoints.emit(new Tuple<>(last, points));
	}

	public void addLevel(int amount) {
		int last = level;
		for (int i = 0; i < amount; i++) {
			addPoints(getRemainingPoints());
		}
		if (level!=last) onChangeLevel.emit(new Tuple<>(last, points));
	}

	public void subLevel(int amount) {
		int last = level;
		for (int i = 0; i < amount; i++) {
			subPoints(getPoints());
		}
		if (level!=last) onChangeLevel.emit(new Tuple<>(last, points));
	}

	public void resetLevel() {
		this.level = 1;
	}
	public void resetPoints() {
		this.points = 0;
	}

	public void subPoints(int amount) {
		int last = points;
		points -= amount;
		if (points < 0) {
			if (level > 0) {
				level--;
				points = maxPoints + points;
			} else {
				points = 0;
			}
		}
		if (points!=last) onChangePoints.emit(new Tuple<>(last, points));

	}

	public double asPercentage() {
		if (maxLevel == 0 || maxPoints == 0) return 100.0;
		double progressInLevel = (double) points / maxPoints;
		double totalProgress = ((level + progressInLevel) / maxLevel) * 100.0;
		return Math.min(100.0, totalProgress);
	}

	public boolean atMaxLevel() {
		return level >= maxLevel;
	}

	public boolean atMaxPoints() {
		return points >= maxPoints;
	}

	public boolean isMaxed() {
		return atMaxLevel() && atMaxPoints();
	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("level", this.getLevel());
		tag.putInt("points", this.getPoints());
		tag.putInt("maxLevel", this.getMaxLevel());
		tag.putInt("maxPoints", this.getMaxPoints());
		return tag;
	}

	public void loadFromCompound(CompoundTag compoundTag) {
		this.level = Math.max(1,compoundTag.getInteger("level"));
		this.points = Math.max(1,  compoundTag.getInteger("points"));
		this.maxLevel = compoundTag.getInteger("maxLevel");
		this.maxPoints = compoundTag.getInteger("maxPoints");
	}

	public static Level fromCompound(CompoundTag compoundTag) {
		return new Level(compoundTag.getInteger("maxLevel"), compoundTag.getInteger("level"), compoundTag.getInteger("maxPoints"), compoundTag.getInteger("points"));
	}

	protected void setLevel(int level) {
		int last = this.level;
		this.level = level;
		if (level!=last) onChangeLevel.emit(new Tuple<>(last, points));

	}
	public void setPoints(int points) {
		int last = this.points;
		this.points = points;
		if (points!=last) onChangePoints.emit(new Tuple<>(last, points));	}

	protected void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	protected void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public int getPoints() { return points; }
	public int getRemainingPoints() { return Math.max(0, points - maxPoints); }
	public int getLevel() { return level; }
	public int getMaxPoints() { return maxPoints; }
	public int getMaxLevel() { return maxLevel; }

	@Override
	public String toString() {
		return "{level: "+ this.level + ", points:" + this.points + "}";
	}
}
