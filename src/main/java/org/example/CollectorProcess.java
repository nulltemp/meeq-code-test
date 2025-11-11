package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CollectorProcess {
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

			final var aggregator = new Aggregator(collector);
			collectorExecutor.scheduleWithFixedDelay(() -> {
				try {
					System.out.println(OBJECT_MAPPER.writeValueAsString(aggregator.aggregate()));
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
}
