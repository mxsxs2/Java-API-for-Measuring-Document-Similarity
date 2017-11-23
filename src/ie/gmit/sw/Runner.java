package ie.gmit.sw;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ie.gmit.sw.parser.FileParser;

public class Runner {
	
	public static void main(String[] args) {
		
		
		
		// Read a new file in a thread
		Thread t1 = new Thread(new FileParser("warnp.txt", " ", true),"Fileparser");
		// Start reading
		t1.start();

		//Create an executor service
		ExecutorService ex = Executors.newSingleThreadExecutor();
		//Add the minhasher
		Future<Boolean> f = ex.submit(new MinHasher(200));
		
		
		try {
			f.get();
			ex.shutdown(); //Shut down the thread executor
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//Print the min hashes
			ArrayList<Integer> l = MapSingleton.getInstance().get("warnp.txt".hashCode());
			System.out.println(l.size());
			synchronized (l) {
				//l.forEach(System.out::println);
			}
			
		}
		
		
	}

}
