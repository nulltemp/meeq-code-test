package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

public class CollectorProcess {
	private static final Queue<Integer> QUEUE = new ArrayDeque<>();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final Object LOCK = new Object();

	static void main(String[] args) throws Exception {
		final int port = Integer.parseInt(args[0]);
		try (final var serverSocket = new ServerSocket(port)) {
			final Thread acceptor = new Thread(() -> {
				try {
					while (true) {
						final Socket client = serverSocket.accept();
						new Thread(new ClientHandler(client, QUEUE)).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			acceptor.start();

			while (true) {
				final var collector = new Collector(QUEUE, 10, 1000, LOCK);
				System.out.println(OBJECT_MAPPER.writeValueAsString(collector.aggregateOnce()));
			}
		}
	}

	private record ClientHandler(Socket socket, Queue<Integer> queue) implements Runnable {
		@Override
		public void run() {
			try (var scanner = new Scanner(socket.getInputStream())) {
				while (scanner.hasNext()) {
					synchronized (LOCK) {
						queue.add(scanner.nextInt());
					}
				}
			} catch (Exception _) {
			}
		}
	}
}
