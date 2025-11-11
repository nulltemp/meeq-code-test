package org.example;

import java.util.HashMap;
import java.util.Map;

public class Collector {
	private static final Object LOCK = new Object();

	private Map<Integer, Integer> counts = new HashMap<>();

	public void add(int number) {
		synchronized (LOCK) {
			counts.put(number, counts.getOrDefault(number, 0) + 1);
		}
	}

	public Map<Integer, Integer> getAndResetCounts() {
		final Map<Integer, Integer> result;
		synchronized (LOCK) {
			result = counts;
			counts = new HashMap<>();
		}
		return result;
	}
}
