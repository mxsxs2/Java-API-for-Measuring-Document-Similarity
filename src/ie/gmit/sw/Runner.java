package ie.gmit.sw;


public class Runner {

	public static void main(String[] args) {
		//Open and show the ui
		new UI().showMenu();
		
		/*//String file1 = "pg10681.txt";
		String file1= "warnp_mixed.txt";
		// String file1= "warnp.txt";
		String file2= "warnp.txt";
		//String file2 = "warnp_mixed.txt";

		long startTime = System.nanoTime();
		// Read a new file in a thread
		Thread t1 = new Thread(new FileParser(file1, " ", true), "Fileparser");
		// Start reading
		t1.start();
		// Read a new file in a thread
		Thread t2 = new Thread(new FileParser(file2, " ", true), "Fileparser2");
		// Start reading
		t2.start();

		// Create the minhaser
		MinHasher mh = new MinHasher(200);
		// Run the hasher. This is a blocking method
		mh.run();

		System.out.println("Finished Threads in " + (System.nanoTime() - startTime) / 1000000 + "ms");
		// Get the list of the minhashes
		ArrayList<Integer> l = MapSingleton.getInstance().get(file1.hashCode());
		ArrayList<Integer> l2 = MapSingleton.getInstance().get(file2.hashCode());

		synchronized (l) {
			l.retainAll(l2);
			System.out.println("Mathces: " + l.size());
			System.out.println("Jackard Index: " + (float) ((float) l.size() / (float) (l2.size() * 2 - l.size())));
			System.out.println("Finished in " + (System.nanoTime() - startTime) / 1000000 + "ms");
		}*/
		
	}

}
