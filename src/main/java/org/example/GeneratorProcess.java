package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneratorProcess {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.err.println("Usage: java GeneratorProcess <collectorHost> <collectorPort> <threads>");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int m = Integer.parseInt(args[2]);

		Socket socket = new Socket(host, port);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		AtomicBoolean running = new AtomicBoolean(true);

		// M個のスレッドを生成
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < m; i++) {
			Thread t = new Thread(() -> {
				Random rand = new Random();
				while (running.get()) {
					try {
						int value = rand.nextInt(10);
						out.println(value);
						Thread.sleep(10); // 0.01秒間隔
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});
			t.start();
			threads.add(t);
		}

		// Ctrl+C で終了
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			running.set(false);
			out.close();
			try { socket.close(); } catch (IOException ignored) {}
		}));

		// 終了待ち
		for (Thread t : threads) t.join();
	}
}
