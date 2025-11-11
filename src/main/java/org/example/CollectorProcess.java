package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CollectorProcess {
	private static final int BUCKET_COUNT = 10;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static void main(String[] args) throws Exception {
		final int port = Integer.parseInt(args[0]);
		try (final var serverSocket = new ServerSocket(port);
			 final var acceptor = Executors.newSingleThreadExecutor();
			 final var socketHandler = Executors.newCachedThreadPool();
			 final var collectorExecutor = Executors.newSingleThreadScheduledExecutor()) {

			final var collector = new Collector();
			acceptor.submit(() -> {
				while (true) {
					final Socket client = serverSocket.accept();
					socketHandler.submit(new ClientHandler(client, collector));
				}
			});

			collectorExecutor.scheduleWithFixedDelay(() -> {
				final var result = collector.getAndResetCounts();

				final var output = new LinkedHashMap<Integer, Integer>();
				for (int i = 0; i < BUCKET_COUNT; i++) {
					output.put(i, result.getOrDefault(i, 0));
				}

				final long unixTime = System.currentTimeMillis() / 1000;

				try {
					System.out.println(OBJECT_MAPPER.writeValueAsString(new CountRecord(unixTime, output)));
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}, 0, 1, TimeUnit.SECONDS);

			final var latch = new CountDownLatch(1);
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private record ClientHandler(Socket socket, Collector collector) implements Runnable {
		@Override
		public void run() {
			try (final var scanner = new Scanner(socket.getInputStream())) {
				while (scanner.hasNext()) {
					collector.add(scanner.nextInt());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
