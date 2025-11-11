package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientHandlerTest {

	@Test
	void run_readsIntsAndCallsCollector() throws Exception {
		// 入力データを用意
		String data = "10 20 30\n";
		InputStream in = new ByteArrayInputStream(data.getBytes());

		// getInputStream をオーバーライドしたダミー Socket
		Socket dummySocket = new Socket() {
			@Override
			public InputStream getInputStream() {
				return in;
			}

			@Override
			public synchronized void close() {
				// do nothing to avoid closing underlying stream unexpectedly in tests
			}
		};

		// Collector スタブ：add 呼び出しを記録する
		List<Integer> received = new ArrayList<>();
		Collector stubCollector = new Collector() {
			@Override
			public void add(int value) {
				received.add(value);
			}

			@Override
			public Map<Integer, Integer> getAndResetCounts() {
				return Map.of(); // 他のメソッドはテストで不要
			}
		};

		// 実行
		ClientHandler handler = new ClientHandler(dummySocket, stubCollector);
		handler.run();

		// 検証
		assertEquals(List.of(10, 20, 30), received);
	}
}

