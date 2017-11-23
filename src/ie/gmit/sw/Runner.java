package ie.gmit.sw;


import java.util.Timer;
import java.util.concurrent.TimeUnit;

import ie.gmit.sw.parser.FileParser;

public class Runner {
	
	public static void main(String[] args) {
		
		
		
		// Read a new file in a thread
		Thread t1 = new Thread(new FileParser("warnp.txt", " ", true));
		// Start reading
		t1.start();
		// Create a consumer thread for testing the file parser
		Thread t2 = new Thread(() -> {
			//Run until the producers are done
			while (!QueueSingleton.isProducersDone()) {
				try {
					// Get the next from the queue
					Shingle s=QueueSingleton.getInstance().take();
					
					System.out.println("C1:"+ s.getDocId()+" "+s.getHashCode());
				} catch (InterruptedException e) {
				}
			}
		},"C-1");
		t2.start();
		try {
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
