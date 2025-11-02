package deus.painscale.newsystem;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import deus.painscale.util.Signal;
import java.util.*;

public class Factor {

	private final String key;
	private Level level;

	private final Map<String, Signal<?>> events = new HashMap<>();
	private final List<Factor> dependencies = new ArrayList<>();
	private boolean autoRecalculate = true;

	public Factor(String key, Level level) {
		this.key = key;
		this.level = level;
		registerEvent("changed", new Signal<>());
	}

	public void addDependency(Factor factor) {
		dependencies.add(factor);
		if (autoRecalculate) {
			Signal<Level> signal = factor.getEvent("changed");
			if (signal != null) {
				signal.connect((s, f) -> recalculate());
			}
		}
	}

	public void addPoints(int amount) {
		if (isComposite()) return;
		level.addPoints(amount);
		emit("changed", level);
	}


	public boolean isComposite() {
		return !dependencies.isEmpty();
	}

	public void recalculate() {
		if (!isComposite()) return;

		double avg = dependencies.stream()
			.mapToDouble(f -> f.getLevel().asPercentage())
			.average()
			.orElse(0.0);

		int newPoints = (int) (avg * level.getMaxPoints() / 100.0);
		int delta = newPoints - level.getPoints();
		level.addPoints(delta);
		emit("changed", level);
	}

	public <T> void registerEvent(String id, Signal<T> signal) {
		events.put(id, signal);
	}

	@SuppressWarnings("unchecked")
	public <T> Signal<T> getEvent(String id) {
		return (Signal<T>) events.get(id);
	}

	@SuppressWarnings("unchecked")
	private <T> void emit(String id, T data) {
		Signal<T> signal = (Signal<T>) events.get(id);
		if (signal != null) signal.emit(data);
	}

	public String getKey() { return key; }
	public Level getLevel() { return level; }
	public List<Factor> getDependencies() { return Collections.unmodifiableList(dependencies); }

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
