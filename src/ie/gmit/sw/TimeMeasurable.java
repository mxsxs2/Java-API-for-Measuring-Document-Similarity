package ie.gmit.sw;
/**
 * Provides a measuring interface
 * @author Krisztian Nagy
 *
 */
public interface TimeMeasurable {
	/**
	 * Returns the total runtime of a measured section
	 * @return The time it took to run an operation
	 */
	public long getRuntime();
}
