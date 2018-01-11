package ie.gmit.sw;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Collection of static directory reading methods
 * 
 * @author Krisztian Nagy
 *
 */
public class DirectoryReader {

	/**
	 * Private constructor to block instantiation
	 */
	private DirectoryReader() {
		throw new AssertionError();
	}
	
	/**
	 * Reads the file names from a directory. If the recursive flag is true, it
	 * reads the files from the sub directories.
	 * 
	 * @param dirPath
	 * @param recursive
	 * @return List<Path>
	 */
	public static List<Path> getDirectoryContent(Path dirPath, boolean recursive) {
		// Create a return list for the paths
		List<Path> pathList = new ArrayList<Path>();
		// If the paths id a directory
		if (Files.isDirectory(dirPath)) {
			// Open the stream for this directory
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
				// Get each item in the stream
				stream.forEach((fp) -> {
					// If the item is a directory and the recursive flag is on
					if (Files.isDirectory(fp)) {
						//If the dir has to be read recursively
						if (recursive)
							// Read the directory recursively
							pathList.addAll(DirectoryReader.getDirectoryContent(fp, true));
					} else {
						// Add the file path to the list
						pathList.add(fp);
					}
				});
			} catch (IOException e) {
			}
		}
		// Return the list
		return pathList;
	}

}
