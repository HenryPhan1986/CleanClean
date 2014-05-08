package chapter2.problem50;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

public class Clock {

	public static double TIME_QUANTA = 51.2;
	public static int TOTAL_RUNS = 100;
	public static int[] HOST_COUNTS = {20,40,60,80,100};
	
	ArrayList<Process> hosts = new ArrayList<Process>();
	ArrayList<Process> completed = new ArrayList<Process>();
	
	public static void main(String[] args) throws IOException {
		File file = new File("chapter2problem50.csv");
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file);
		for (int j = 0; j < HOST_COUNTS.length; j++) {
			for (int i = 0; i < TOTAL_RUNS; i++) {
				Clock clock = new Clock(HOST_COUNTS[j]);
				clock.process();
				IOUtils.write(clock.toString(), stream);
			}
			IOUtils.write("\n\n", stream);
		}
	}
	
	public Clock(int numHosts) {
		for (int i = 0; i < numHosts; i++) {
			hosts.add(new Process(i));
		}
	}
	
	public void process() {
		ArrayList<Process> active;
		Iterator<Process> iter;
		Process target;
		
		while (!finished()) {
			
//			System.out.println("Time: " + (time * TIME_QUANTA));
			
			active = new ArrayList<Process>();
			iter = hosts.iterator();
			while (iter.hasNext()) {

				target = iter.next();
//				System.out.println(target.getId() + ": Complete? " + target.isComplete() + " TimeToRun? " + target.getTimeToRun());
				if (target.run()) {
					active.add(target);
				}
			}
			
			if (active.size() > 1) {
				for (Process p : active) {
					p.collision();
				}
			} else if (active.size() == 1) {
				completed.add(active.get(0));
			}
//			System.out.println();
		}
	}
	
	public boolean finished() {
		Iterator<Process> iter = hosts.iterator();
		while (iter.hasNext()) {
			if (!iter.next().isComplete())
				return false;
		}
		return true;
	}
	
	public String toString() {
		String result = "", comma = "";
		for (Process p : completed) {
			result += comma + p.getTotalCount();
			comma = ",";
		}
		return result + "\n";
	}
}
