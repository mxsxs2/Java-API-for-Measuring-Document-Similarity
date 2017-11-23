package ie.gmit.sw;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Implements a thread safe Mapas a singleton
 * @author Krisztian Nagy
 *
 */
public final class MapSingleton {
	// The map to use
	private static ConcurrentHashMap<Integer, ArrayList<Integer>> map;

	/**
	 * Private constructor to block instantiation
	 */
	private MapSingleton() {
	}

	/**
	 * Returns the instance of the Map which is thread safe
	 * 
	 * @return Map<Integer, ArrayList<Integer>>
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
