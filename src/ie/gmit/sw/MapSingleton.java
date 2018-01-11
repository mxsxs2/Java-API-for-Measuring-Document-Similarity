package ie.gmit.sw;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Implements a thread safe Map as a singleton
 * @author Krisztian Nagy
 *
 */
public final class MapSingleton{
	// The map to use
	private static ConcurrentHashMap<Integer, ArrayList<Integer>> map;
	//private static ConcurrentHashMap<Integer, Integer[]> map;
	/**
	 * Private constructor to block instantiation
	 */
	private MapSingleton() {
		throw new AssertionError();
	}

	/**
	 * Returns the instance of a thread safe map. The map is loaded with lazy loading.
	 * 
	 * @return Instance of a concurrent HashMap
	 */
	public static ConcurrentHashMap<Integer, ArrayList<Integer>> getInstance() {
		// If the map is null
		if (map == null) {
			// Create a new one
			map = new ConcurrentHashMap<Integer, ArrayList<Integer>>();
		}
		
		// Return the queue
		return map;
	}
	
}
