package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneratorProcess {
	static void main(String[] args) throws Exception {
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final int m = Integer.parseInt(args[2]);

		try (final Socket socket = new Socket(host, port)) {
			final var out = new PrintWriter(socket.getOutputStream(), true);
			final var running = new AtomicBoolean(true);

			final var threads = new ArrayList<Thread>();
			for (int i = 0; i < m; i++) {
				final Thread t = new Thread(() -> {
					final Random rand = new Random();
					while (running.get()) {
						try {
							final int value = rand.nextInt(10);
							out.println(value);
							Thread.sleep(10);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				});
				t.start();
				threads.add(t);
			}

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				running.set(false);
				out.close();
				try {
					socket.close();
				} catch (IOException ignored) {
				}
			}));

			for (Thread t : threads) {
				t.join();
			}
		}
	}
}
