package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public void main() throws IOException, InterruptedException {
		int N = 3; // プロセス数
		int M = 5; // 各プロセスのスレッド数
		int port = 5000;

		String classpath = System.getProperty("java.class.path");

		// 集計プロセス起動
		Process collector = new ProcessBuilder(
				"java", "-cp", classpath, "org.example.CollectorProcess", String.valueOf(port)
		).inheritIO().start();

		Thread.sleep(1000); // 起動待ち

		// N個の生成プロセス起動
		List<Process> generators = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			Process p = new ProcessBuilder(
					"java", "-cp", classpath, "org.example.GeneratorProcess", "localhost", String.valueOf(port), String.valueOf(M)
			).inheritIO().start();
			generators.add(p);
		}

		// 10秒実行
		Thread.sleep(10_000);

		// プロセス終了
		for (Process p : generators) p.destroy();
		collector.destroy();
	}
}