package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CollectorProcess {
	private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: java CollectorProcess <port>");
			System.exit(1);
		}

		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = new ServerSocket(port);

		System.out.println("Collector listening on port " + port);

		// クライアント受け付けスレッド
		Thread acceptor = new Thread(() -> {
			try {
				while (true) {
					Socket client = serverSocket.accept();
					new Thread(new ClientHandler(client, queue)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		acceptor.start();

		// 集計スレッド
		while (true) {
			Map<Integer, Integer> counts = new HashMap<>();
			for (int i = 0; i < 10; i++) counts.put(i, 0);

			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 1000) {
				Integer num = queue.poll(50, TimeUnit.MILLISECONDS);
				if (num != null) {
					counts.put(num, counts.get(num) + 1);
				}
			}

			long unixTime = System.currentTimeMillis() / 1000;
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("timestamp", unixTime);
			result.put("counts", counts);

			System.out.println(result);
		}
	}

	static class ClientHandler implements Runnable {
		private final Socket socket;
		private final BlockingQueue<Integer> queue;

		ClientHandler(Socket socket, BlockingQueue<Integer> queue) {
			this.socket = socket;
			this.queue = queue;
		}

		@Override
		public void run() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null) {
					try {
						int num = Integer.parseInt(line.trim());
						queue.put(num);
					} catch (NumberFormatException ignored) {}
				}
			} catch (Exception e) {
				// クライアント切断時は無視
			}
		}
	}
}
