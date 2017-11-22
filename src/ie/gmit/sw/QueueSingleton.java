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
		//Return the que
		return queue;
	}
}
