package org.example;

import java.util.LinkedHashMap;

public record Aggregator(Collector collector) {
	private static final int BUCKET_COUNT = 10;

	public CountRecord aggregate() {
		final var result = collector.getAndResetCounts();

		final var output = new LinkedHashMap<Integer, Integer>();
		for (int i = 0; i < BUCKET_COUNT; i++) {
			output.put(i, result.getOrDefault(i, 0));
		}

		final long unixTime = System.currentTimeMillis() / 1000;

		return new CountRecord(unixTime, output);
	}
}
