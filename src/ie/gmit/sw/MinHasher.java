package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * A callable which takes one Shingle from the QueueSingleton until the producers are done. 
 * Each Shingle is passed to a child thread to calculate the min hash on them and to be added to the MapSingleton
 * @author Krisztian Nagy
 *
 */
public class MinHasher implements Callable<Boolean> {
	// The random has codes
	private int[] randomHashCodes;
	// Size of the hash array
	private int hashArraySize;
	/**
	 * Constructor which initialises the random hash codes array
	 * @param int size of the hash array
	 */
	public MinHasher(int hashArraySize) {
		this.hashArraySize = hashArraySize;
		// Generate the hashes
		this.generateRandomHasNumbers();
	}
	/**
	 * Creates a fixed size of thread pool and keep filling it up until the QueueSingleton filling is stopped by the producer threads.
	 * Each thread is joined back to the stack which the MinHasher is on.
	 * When every thread is finished and the producer is finished the pool is shutdown and the method returns.
	 * @return return true once it is finished
	 */
	private Boolean runConsumerThreads() {
		// Create a pool of 100 threads
		ExecutorService ex = Executors.newFixedThreadPool(100);
		// Run while the producer threads works
		while (!QueueSingleton.isProducersDone()) {
			try {
				// Get the next shingle from the queue
				Shingle next = QueueSingleton.getInstance().take();
				//Create a new thread to the pool
				Thread t = new Thread(() -> {
					// Get the minhash of this shingle
					this.findMinHashOfString(next);
				});

				// Execute the new task in the thread pool and add the future to the list
				ex.submit(t);
				// Join back to the main
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			// Shut down the thread executor as it is not needed it anymore
			ex.shutdown();
		} catch (Exception e) {

		}
		// All tasks are finished
		return true;
	}
	/**
	 * Initialises the random hash code array by a given amount of int values.
	 * The size of the array is specified this classe's constructor
	 */
	private void generateRandomHasNumbers() {
		// Create a new random
		Random r = new Random(System.nanoTime());
		// Initialise random hash codes array with random integers
		// This is nice and short how ever, if the hashArraySize is large, should
		// consider to change this to an ordinary for loop
		this.randomHashCodes = r.ints().limit(this.hashArraySize).toArray();
	}
	/**
	 * Finds the smallest has for a given shingle at a given index.
	 * Fills the document's(which specified in the shingle) list in the MapSingleton
	 * @param Shingle to find the lowest hash
	 */
	private void findMinHashOfString(Shingle s) {
		// Loop k times O(n)
		for (int j = 0; j < this.hashArraySize; j++) {
			// Generate a new hashcode O(1)
			int newHashCode = s.getHashCode() ^ this.randomHashCodes[j];
			// Get the list from the map
			ArrayList<Integer> l = MapSingleton.getInstance().get(s.getDocId());
			synchronized (l) {
				// Get the current minhash O(1)
				Integer current = l.get(j);
				// Check if the current hash is higher than the generated one O(1)
				if (current > newHashCode) {
					// Replace the hash with the generated one O(1)
					l.set(j, newHashCode);
				}
			}
		}
	}

	@Override
	public Boolean call() throws Exception {
		// Start the consumers
		return this.runConsumerThreads();
	}

}
