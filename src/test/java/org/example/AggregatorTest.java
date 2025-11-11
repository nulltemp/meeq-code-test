package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AggregatorTest {

	@Test
	void aggregate_returnsAllBucketsAndUnixTime() {
		// Collector スタブを作成
		Collector stubCollector = new Collector() {
			@Override
			public Map<Integer, Integer> getAndResetCounts() {
				Map<Integer, Integer> m = new HashMap<>();
				m.put(0, 2);
				m.put(7, 9);
				return m;
			}
		};

		Aggregator aggregator = new Aggregator(stubCollector);
		CountRecord record = aggregator.aggregate();

		// バケットが0..9すべて存在し、未設定のものは0で補完されていること
		Map<Integer, Integer> counts = record.counts();
		assertNotNull(counts);
		for (int i = 0; i < 10; i++) {
			assertTrue(counts.containsKey(i), "bucket " + i + " が存在しません");
		}
		assertEquals(2, counts.get(0));
		assertEquals(9, counts.get(7));
		// 他のバケットは 0
		for (int i = 0; i < 10; i++) {
			if (i == 0 || i == 7) continue;
			assertEquals(0, counts.get(i));
		}

		// unixTime が現在時刻に近いこと（秒単位で差が小さい）
		long now = System.currentTimeMillis() / 1000;
		long unixTime = record.time();
		assertTrue(Math.abs(now - unixTime) <= 5, "unixTime が現在時刻から遠すぎます: " + unixTime);
	}
}

