package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest {
	@Test
	void aggregateOnce_countsCorrect() throws Exception {
		var queue = new LinkedBlockingQueue<Integer>();
		// 準備: 値をキューに入れる
		queue.put(1);
		queue.put(1);
		queue.put(1);
		queue.put(3);
		queue.put(3);
		queue.put(10); // 範囲外（bucketCount=10）なので無視される想定

		Collector collector = new Collector(queue, 10, 200);
		Map<String, Object> result = collector.aggregateOnce();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> counts = (Map<Integer, Integer>) result.get("counts");

		assertEquals(0, counts.get(0));
		assertEquals(3, counts.get(1));
		assertEquals(0, counts.get(2));
		assertEquals(2, counts.get(3));
	}
}
