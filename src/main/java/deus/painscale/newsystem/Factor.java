package deus.painscale.newsystem;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import deus.painscale.util.Signal;
import deus.painscale.util.Tuple;

import java.util.*;
import java.util.*;

public class Factor {

	private final String key;
	private Level level;
	private final double weight;
	private final List<Factor> dependencies = new ArrayList<>();

	public Factor(String key, Level level, double weight) {
		this.key = key;
		this.level = level;
		this.weight = weight;
	}

	public void addDependency(Factor f) {
		dependencies.add(f);
	}

	public void addPoints(int amount) {
		level.addPoints(amount);
	}

	public double getPercentage() {
		double depPct = 0;
		for (Factor dep : dependencies) {
			depPct += dep.getPercentage();
		}

		double avgDep = dependencies.isEmpty() ? 0 : depPct / dependencies.size();
		double localPct = level.asPercentage() * weight;

		if (!dependencies.isEmpty()) {
			return (localPct + avgDep) / 2.0;
		} else {
			return localPct;
		}
	}

	public Level getCompositeLevel() {
		double totalWeight = weight;
		double weightedPoints = level.getPoints() * weight;
		double weightedMaxPoints = level.getMaxPoints() * weight;

		for (Factor dep : dependencies) {
			Level depLevel = dep.getCompositeLevel();
			double depWeight = dep.weight;
			weightedPoints += depLevel.getPoints() * depWeight;
			weightedMaxPoints += depLevel.getMaxPoints() * depWeight;
			totalWeight += depWeight;
		}

		int finalPoints = (int)Math.round(weightedPoints / totalWeight);
		int finalMaxPoints = (int)Math.round(weightedMaxPoints / totalWeight);

		return new Level(finalMaxPoints, 0, finalMaxPoints, finalPoints);
	}


	public boolean isComposite() {
		return !dependencies.isEmpty();
	}

	public Level getLevel() {
		return level;
	}

	public int getLevelNumber() {
		return level.getLevel();
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "Factor{" +
			"key='" + key + '\'' +
			", level=" + level.toString() +
			(isComposite() ? ", composite=" + dependencies.size() + " deps" : "") +
			'}';
	}

	public void loadLevel(CompoundTag tag) {
		this.level.loadFromCompound(tag.getCompound("level"));
	}

}
