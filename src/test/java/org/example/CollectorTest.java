package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectorTest {

	@Test
	void add_and_getAndResetCounts_singleThread() {
		// ...existing code...
		// Collector がデフォルトコンストラクタで作れる前提
		Collector collector = new Collector();

		collector.add(1);
		collector.add(1);
		collector.add(2);

		Map<Integer, Integer> counts = collector.getAndResetCounts();
		assertNotNull(counts);
		assertEquals(2, counts.get(1));
		assertEquals(1, counts.get(2));

		// リセットされていること
		Map<Integer, Integer> afterReset = collector.getAndResetCounts();
		assertTrue(afterReset.isEmpty());
	}
}

