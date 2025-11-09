package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CollectorProcess {
	private static final BlockingQueue<Integer> QUEUE = new LinkedBlockingQueue<>();

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
				final var collector = new Collector(QUEUE, 10, 1000);
				System.out.println(collector.aggregateOnce());
			}
		}
	}

	private record ClientHandler(Socket socket, BlockingQueue<Integer> queue) implements Runnable {
		@Override
		public void run() {
			try (var scanner = new Scanner(socket.getInputStream())) {
				while (scanner.hasNext()) {
					queue.put(scanner.nextInt());
				}
			} catch (Exception _) {
			}
		}
	}
}
