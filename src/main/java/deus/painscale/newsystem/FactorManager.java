package deus.painscale.newsystem;


import java.util.*;

public class FactorManager {
	private final Map<String, Factor> factors = new HashMap<>();

	public void register(Factor f) {
		factors.put(f.getKey(), f);
	}

	public Factor get(String key) {
		return factors.get(key);
	}

	public void addPoints(String key, int amount) {
		Factor f = factors.get(key);
		if (f != null) f.addPoints(amount);
	}

	public void updateAll() {
		factors.values().forEach(Factor::recalculate);
	}
}
