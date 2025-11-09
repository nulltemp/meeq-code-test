package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest {
	@Test
	void aggregateOnce_countsCorrect() {
		var queue = new ArrayDeque<Integer>();
		queue.add(1);
		queue.add(1);
		queue.add(1);
		queue.add(3);
		queue.add(3);

		Collector collector = new Collector(queue, 10, 200, new Object());
		var result = collector.aggregateOnce();

		Map<Integer, Integer> counts = result.counts();

		assertEquals(0, counts.get(0));
		assertEquals(3, counts.get(1));
		assertEquals(0, counts.get(2));
		assertEquals(2, counts.get(3));
	}
}
