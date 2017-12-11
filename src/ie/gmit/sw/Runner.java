package ie.gmit.sw;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ie.gmit.sw.parser.FileParser;

public class Runner {

	public static void main(String[] args) {
		
		new UI().showMenu();
		
		/*
		String file1= "pg10681.txt";
		//String file1= "warnp.txt";
		//String file2= "warnp.txt";
		String file2= "warnp_mixed.txt";
		
		
		long startTime = System.nanoTime();
		// Read a new file in a thread
		Thread t1 = new Thread(new FileParser(file1, " ", true), "Fileparser");
		// Start reading
		t1.start();
		// Read a new file in a thread
		Thread t2 = new Thread(new FileParser(file2, " ", true), "Fileparser2");
		// Start reading
		t2.start();
		
		// Create an executor service
		//ExecutorService ex = Executors.newSingleThreadExecutor();
		ExecutorService ex=Executors.newSingleThreadExecutor();
		// Add the minhasher
		Future<Boolean> f = ex.submit(new MinHasher(200));
		
		try {
			f.get();
			ex.shutdown(); // Shut down the thread executor

			ex.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			System.out.println("Finished Threads in "+(System.nanoTime() - startTime)/1000000+"ms");	
			// Get the list of the minhashes
			ArrayList<Integer> l = MapSingleton.getInstance().get(file1.hashCode());
			ArrayList<Integer> l2 = MapSingleton.getInstance().get(file2.hashCode());

			synchronized (l) {
				l.retainAll(l2);
				System.out.println("Mathces: "+l.size());
				System.out.println("Jackard Index: "+(float)((float)l.size()/(float)(l2.size()*2-l.size())));
				System.out.println("Finished in "+(System.nanoTime() - startTime)/1000000+"ms");
			}

		}*/

	}
	


}
