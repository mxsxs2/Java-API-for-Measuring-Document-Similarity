package ie.gmit.sw;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implements a thread safe Queue as a singleton
 * 
 * @author Krisztian Nagy
 *
 */
public final class QueueSingleton {
	// The queue to use
	private static LinkedBlockingQueue<Shingle> queue;
	// Flag if the producer done writing
	private static volatile int[] noOfproducersDone = new int[] { 0 };
	// The number of the producers
	private static volatile int[] noOfProducers = new int[] { 0 };
	// The poison pill
	public static final Shingle POISON_PILL = new Shingle(Integer.MIN_VALUE, Integer.MAX_VALUE);

	/**
	 * Private constructor to block instantiation
	 */
	private QueueSingleton() {
		throw new AssertionError();
	}

	/**
	 * Returns the instance of the LinkedBlockingQueue
	 * 
	 * @return Instance of a LinkedBlockingQueue
	 */
	public static LinkedBlockingQueue<Shingle> getInstance() {
		// If the queue is null
		if (queue == null) {
			// TODO: get the size from settings or something
			// Create a new one
			queue = new LinkedBlockingQueue<Shingle>(1000);
		}
		// Return the queue
		return queue;
	}

	/**
	 * Set a producer to be done adding to this queue.
	 * When all the producers are done, a poison pill is added to the queue
	 */
	public static void setProducerDone() {
		noOfproducersDone[0]++;

		// If there is at least one producer and the number of producers are the same as
		// the stopped producers the producing is done
		if (noOfProducers[0] != 0 && noOfproducersDone[0] == noOfProducers[0]) {
			try {
				//Pass the posion pill to the singleton
				QueueSingleton.getInstance().put(QueueSingleton.POISON_PILL);
			} catch (InterruptedException e) {
			}
		}
	}
	/**
	 * Registers a new producer to this Queue
	 */
	public static void addProducer() {
		//Increase the number of producers
		noOfProducers[0]++;
	}
}
