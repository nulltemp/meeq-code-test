package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Main {
	void main(String[] args) throws IOException, InterruptedException {
		final int n = Integer.parseInt(args[0]);
		final String m = args[1];
		final String port = args[2];

		final String classpath = System.getProperty("java.class.path");

		final Process collector = new ProcessBuilder(
				"java", "-cp", classpath, "org.example.CollectorProcess", port
		).inheritIO().start();

		Thread.sleep(1000);

		final var generators = new ArrayList<Process>();
		try {
			for (int i = 0; i < n; i++) {
				final Process p = new ProcessBuilder(
						"java", "-cp", classpath, "org.example.GeneratorProcess", "localhost", port, m
				).inheritIO().start();
				generators.add(p);
			}

			final var latch = new CountDownLatch(1);
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} finally {
			for (Process p : generators) {
				p.destroy();
			}
			collector.destroy();
		}
	}
}