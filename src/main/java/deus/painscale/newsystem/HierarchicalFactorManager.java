package deus.painscale.newsystem;

import com.mojang.nbt.tags.CompoundTag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchicalFactorManager {

	private final HierarchicalFactorManager parent;

	private final Map<String, Factor> factors = new HashMap<>();

	public HierarchicalFactorManager(HierarchicalFactorManager parent) {
		this.parent = parent;
	}

	public void register(Factor f) {
		factors.put(f.getKey(), f);
	}

	public Factor get(String key) {
		Factor f = factors.get(key);
		if (f == null && parent != null) {
			return parent.get(key);
		}
		return f;
	}
	public List<Factor> getAllFactors() {
		return factors.values().stream().toList();
	}

	public void addPoints(String key, int amount) {
		Factor f = get(key);
		if (f != null) f.addPoints(amount);
	}

	public void updateAll() {
		factors.values().forEach(Factor::recalculate);
	}

	public Factor createFactor(String id, int maxLevel, int level, int maxPoints, int points) {
		Factor factor = new Factor(id, new Level(maxLevel, level, maxPoints, points));
		this.register(factor);
		return factor;
	}



}
