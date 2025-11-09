package org.example;

import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public record Collector(BlockingQueue<Integer> queue, int bucketCount, long windowMillis) {
	public CountRecord aggregateOnce() throws InterruptedException {
		final var counts = new LinkedHashMap<Integer, Integer>();
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
		return new CountRecord(unixTime, counts);
	}
}
