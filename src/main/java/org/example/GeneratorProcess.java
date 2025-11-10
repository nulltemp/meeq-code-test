package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeneratorProcess {
	static void main(String[] args) throws Exception {
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final int m = Integer.parseInt(args[2]);

		try (final var socket = new Socket(host, port);
			 final var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			 final var executor = Executors.newScheduledThreadPool(m)) {

			final var rand = new Random();
			for (int i = 0; i < m; i++) {
				executor.scheduleWithFixedDelay(() -> {
					final int value = rand.nextInt(10);
					try {
						writer.write(String.valueOf(value));
						writer.newLine();
						writer.flush();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}, 0, 10, TimeUnit.MILLISECONDS);
			}

			final var latch = new CountDownLatch(1);
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
