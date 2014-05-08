package chapter2.problem52;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

public class Clock {

	public static double TIME_QUANTA = 51.2;
	public static int TOTAL_RUNS = 50;
	public static int HOST_COUNTS = 20;
	public static int[] LAMBDA = {20, 18, 16, 14, 12, 10, 8, 6, 4};
	
	ArrayList<Process> hosts = new ArrayList<Process>();
	ArrayList<Process> completed = new ArrayList<Process>();
	
	public static void main(String[] args) throws IOException {
		File file = new File("chapter2problem52.csv");
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file);
		for (int j = 0; j < LAMBDA.length; j++) {
			IOUtils.write(LAMBDA[j] + "\n", stream);
			for (int i = 0; i < TOTAL_RUNS; i++) {
				Clock clock = new Clock(HOST_COUNTS, LAMBDA[j]); 
				clock.process();
				IOUtils.write(clock.toString(), stream);
			}
			IOUtils.write("\n\n", stream);
		}
	}
	
	public Clock(int numHosts, int lambda) {
		for (int i = 0; i < numHosts; i++) {
			hosts.add(new Process(lambda));
		}	
	}
	
	public void process() {
		Process current, next;
		while (hosts.size() > 1) {
			
			Collections.sort(hosts, Process.totalCountComparator());
			current = hosts.remove(0);
			next = hosts.get(0);
			if (next.getTotalCount() - current.getTotalCount() < 1) {
				hosts.add(current.collision());
				next.setCollision(true);
			} else {
				if (current.isCollision()) {
					current.setCollision(false);
					hosts.add(current.collision());
				}
				else
					completed.add(current);
			}
		}
		if (hosts.size() > 0)
			completed.add(hosts.remove(0));
	}
	
	public String toString() {
		String result = "", comma = "";
		for (Process p : completed) {
			result += comma + String.format("%.03f", p.getTotalCount());
			comma = ",";
		}
		return result + "\n";
	}
}
