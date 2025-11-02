package deus.painscale.newsystem;

import com.mojang.nbt.tags.CompoundTag;

public class Level {
	private int points;
	private int level;
	private int maxPoints;
	private int maxLevel;

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

	public void addPoints(int amount) {
		points += amount;
		while (atMaxPoints()) {
			points -= maxPoints;
			if (level < maxLevel) level++;
			else {
				points = maxPoints;
				break;
			}
		}
	}

	public void subPoints(int amount) {
		points -= amount;
		if (points < 0) {
			if (level > 0) {
				level--;
				points = maxPoints + points;
			} else {
				points = 0;
			}
		}
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
		this.level = level;
	}
	public void setPoints(int points) {
		this.points = points;
	}
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
