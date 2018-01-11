package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ie.gmit.sw.parser.FileParser;

/**
 * A Mediator class for the queue, map file parser and the min hasher
 * 
 * @author Krisztian Nagy
 *
 */
public class JaccardIndexCalculator implements FileComparator {
	// List of files to compare
	private ArrayList<String> files;
	// Total runtime of comparison
	private long runtime;
	// Number of samples
	private int numberOfSamples;
	// Size of the pool
	private int poolSize;
	
	public JaccardIndexCalculator() {
		super();
		this.files = new ArrayList<String>();
		this.runtime = 0;
		this.numberOfSamples = 200;
		this.poolSize=100;
	}
	

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}
	public int getPoolSize() {
		return this.poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize=poolSize;
	}

	@Override
	public void addFile(String filePath) {
		// Add the file to the list
		this.files.add(filePath);
	}

	/**
	 * Reads each file into QueueSingleton. Reading happens in separate threads
	 */
	private void readFiles() {
		// Loop the file names
		this.files.forEach((name) -> {
			// Read a new file in a thread
			Thread t1 = new Thread(()->{
				//Start reading the content of the file
				new FileParser(name, " ", true).readContent();
				
			}, "Fileparser - " + name);
			// Start reading
			t1.start();
		});
	}

	/**
	 * Compares the predefined files and writes out the Jaccard index of them.
	 * 
	 * @return List of comparison results
	 */
	public List<Result> compare() {
		// Start the timer
		long startTime = System.nanoTime();

		// Empty the Queue
		QueueSingleton.getInstance().clear();
		// Empty the Map
		MapSingleton.getInstance().clear();

		// Read in the files
		this.readFiles();

		// Create the minhaser
		MinHasher mh = new MinHasher(this.numberOfSamples,this.poolSize);
		// Run the hasher. This is a blocking method
		mh.run();
		// The time took to read the files and create the shingles
		// long read = (System.nanoTime() - startTime) / 1000000;

		// ArrayList for the results
		ArrayList<Result> results = new ArrayList<Result>(this.files.size());
		// This is quadratic. How ever it does not give huge overhead unless there is a
		// large amount of files being compared
		// Loop the files list
		for (int i = 0; i < this.files.size(); i++) {
			// Loop the files list
			for (int j = i + 1; j < this.files.size(); j++) {
				// Get the the list to compare
				ArrayList<Integer> l = MapSingleton.getInstance().get(this.files.get(i).hashCode());
				ArrayList<Integer> l2 = MapSingleton.getInstance().get(this.files.get(j).hashCode());
				// Number of matching elements in the lists
				AtomicInteger intersectionSize = new AtomicInteger(0);
				// If the list has be created e.g the file was read and the minhashes were
				// created
				if (l != null && l2 != null) {
					// Stream the items of list 1
					l.parallelStream().forEach(li -> {
						// If list2 contains this item
						if (l2.contains(li))
							// Increment the value
							intersectionSize.incrementAndGet();
					});

					// Add to the results list
					results.add(new Result(this.files.get(i), this.files.get(j), Float.toString(
							((float) intersectionSize.get() / (float) (l2.size() * 2 - intersectionSize.get()))),
							"Jaccard Index"));
				}else {
					//If the first file could not be opened
					if(l==null)
						// Add error "Result"
						results.add(new Result("", "", "-999",this.files.get(i)+" could not be opened."));
					//If the second file could not be opened
					if(l2==null)
						// Add error "Result"
						results.add(new Result("", "", "-999","Error: "+this.files.get(j)+" could not be opened."));
					
				}

			}
		}
		// Out put time measures
		// Sstem.out.println("\nRun time of file reading and Shingle creation " + read +
		// "ms");
		// Sstem.out.println("Total run time: " + (System.nanoTime() - startTime) /
		// 1000000 + "ms");

		this.runtime = (System.nanoTime() - startTime) / 1000000;
		// Return the results
		return results;
	}

	@Override
	public int getNumberOfFiles() {
		return this.files.size();
	}

	/**
	 * Returns the total runtime of the comparison.
	 * 
	 * @return The time it took to run the file reading and comparisons
	 */
	public long getRuntime() {
		return this.runtime;
	}

}
