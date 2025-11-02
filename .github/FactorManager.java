package deus.painscale.newsystem;

import java.util.*;

public class FactorManager {

	private final Map<String, Factor> factors = new HashMap<>();

	public void register(Factor factor) {
		factors.put(factor.getKey(), factor);
	}

	public Factor get(String key) {
		return factors.get(key);
	}

	public void addPoints(String key, int amount) {
		Factor f = factors.get(key);
		if (f != null) f.addPoints(amount);
	}

	public void subPoints(String key, int amount) {
		Factor f = factors.get(key);
		if (f != null) f.subPoints(amount);
	}

	public void updateAll() {
		for (Factor f : factors.values()) {
			if (f instanceof CompositeFactor cf) cf.recalculate();
		}
	}

	public void printAll() {
		factors.forEach((k, v) -> System.out.println(k + " → " + v.asPercentage() + "%"));
	}
}
