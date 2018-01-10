package ie.gmit.sw;
/**
 * Prediefined methods which has to be implemented by every file comparing class
 * @author Krisztian Nagy
 *
 */
public interface FileComparator extends TimeMeasurable{
	/**
	 * Adds a file path to the internal list of files to be compared
	 * 
	 * @param filePath
	 */
	public void addFile(String filePath);

	/**
	 * Returns the number of files which are added for comparison
	 * 
	 * @return
	 */
	public int getNumberOfFiles();

	/**
	 * Compares the predefined files and writes out the jaccard index of them.
	 */
	public java.util.List<Result> compare();
}