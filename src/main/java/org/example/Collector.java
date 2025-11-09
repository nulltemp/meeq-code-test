package org.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public record Collector(BlockingQueue<Integer> queue, int bucketCount, long windowMillis) {
	public Map<String, Object> aggregateOnce() throws InterruptedException {
		final Map<Integer, Integer> counts = new LinkedHashMap<>();
		for (int i = 0; i < bucketCount; i++) {
			counts.put(i, 0);
		}

		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < windowMillis) {
			final Integer num = queue.poll(50, TimeUnit.MILLISECONDS);
			if (num != null && num >= 0 && num < bucketCount) {
				counts.put(num, counts.get(num) + 1);
			}
		}

		final long unixTime = System.currentTimeMillis() / 1000;
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put("timestamp", unixTime);
		result.put("counts", counts);
		return result;
	}
}
