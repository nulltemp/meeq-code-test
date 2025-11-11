package org.example;

import java.net.Socket;
import java.util.Scanner;

public record ClientHandler(Socket socket, Collector collector) implements Runnable {
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
