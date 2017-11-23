package ie.gmit.sw;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implements LinkedBlockingQueue and its methods as a singleton(Returns always the exact same LinkedBlockingQueue
 * @author Krisztian Nagy
 *
 */
public final class QueueSingleton {
	//The que to use
	private static LinkedBlockingQueue<Shingle> queue;
	//Flag if the producer done writing
	private static volatile int[] noOfproducersDone=new int[]{0};
	//The number of the producers 
	private static volatile  int[] noOfProducers=new int[]{0};
	
	/**
	 * Private constructor to block instantiation
	 */
	private QueueSingleton() {	
	}
	
	/**
	 * Returns the instance of the LinkedBlockingQueue
	 * @return LinkedBlockingQueue<Shingle>
	 */
	public static LinkedBlockingQueue<Shingle> getInstance() {
		//If the que is null
		if(queue==null) {
			//TODO: get the size from settings or something
			//Create a new one
			queue=new LinkedBlockingQueue<Shingle>(100);
		}
		//Return the queue
		return queue;
	}
	
	public static boolean isProducersDone() {
		//If there is at least one producer and the number of producers are the same as the stopped producers
		return  noOfProducers[0]!=0 && noOfproducersDone[0]==noOfProducers[0];
	}

	public static void setProducerDone() {
		noOfproducersDone[0]++;
	}
	
	public static void addProducer() {
		noOfProducers[0]++;
	}
}
