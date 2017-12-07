package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A callable which takes one Shingle from the QueueSingleton until the
 * producers are done. Each Shingle is passed to a child thread to calculate the
 * min hash on them and to be added to the MapSingleton
 * 
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
	 * 
	 * @param hashArraySize size of the hash array
	 */
	public MinHasher(int hashArraySize) {
		this.hashArraySize = hashArraySize;
		// Generate the hashes
		this.generateRandomHashNumbers();
	}

	/**
	 * Creates a fixed size of thread pool and keep filling it up until the
	 * QueueSingleton filling is stopped by the producer threads. Each thread is
	 * joined back to the stack which the MinHasher is on. When every thread is
	 * finished and the producer is finished the pool is shutdown and the method
	 * returns.
	 * 
	 * @return return true once it is finished
	 */
	private Boolean runHasherThreads() {
		// Keep taking from the queue flag
		boolean consume = true;
		// Create a pool of 100 threads
		ExecutorService ex = Executors.newFixedThreadPool(100);
		// Run while the producer threads works
		while (consume) {
			try {
				// Get the next shingle from the queue
				Shingle next = QueueSingleton.getInstance().take();
				// If not a poison pill was encountered
				if (next != QueueSingleton.POISON_PILL) {
					// Execute the new task in the thread pool
					ex.submit(() -> {
						// Get the minhash of this shingle
						this.findMinHashOfString(next);
					});
				} else {
					// Stop the loop
					consume = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// Shut down the thread executor as it is not needed anymore
			ex.shutdown();
			ex.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {

		}

		// All tasks are finished
		return true;
	}

	/**
	 * Initialises the random hash code array by a given amount of int values. The
	 * size of the array is specified this classe's constructor
	 */
	private void generateRandomHashNumbers() {
		// Create a new random
		Random r = new Random(System.nanoTime());
		// Initialise random hash codes array with random integers
		// This is nice and short how ever, if the hashArraySize is large, should
		// consider to change this to an ordinary for loop
		this.randomHashCodes = r.ints().limit(this.hashArraySize).toArray();
	}

	/**
	 * Finds the smallest has for a given shingle at a given index. Fills the
	 * document's(which specified in the shingle) list in the MapSingleton
	 * 
	 * @param Shingle to find the lowest hash
	 */
	private void findMinHashOfString(Shingle s) {
		// Get the list from the map O(1)
		ArrayList<Integer> l = MapSingleton.getInstance().get(s.getDocId());
		//Synchronise the list
		synchronized (l) {
			// Loop k times O(n)
			for (int j = 0; j < this.hashArraySize; j++) {
				// Generate a new hashcode O(1)
				int newHashCode = s.getHashCode() ^ this.randomHashCodes[j];
					// Get the current minhash O(1)
					Integer current = l.get(j);
					// Check if the current hash is higher than the generated one O(1)
					if (current== null || current > newHashCode) {
						// Replace the hash with the generated one O(1)
						l.set(j, newHashCode);
					}
			}
		}
	}

	@Override
	public Boolean call() throws Exception {
		//Run the threads to consume the Queue elements
		//It will only return after the threads are done
		return this.runHasherThreads();
		
	}

}
