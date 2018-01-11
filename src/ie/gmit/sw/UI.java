package ie.gmit.sw;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Creates a console User Interface for adding files, changing sample size for comparison and to start the comparison.
 * @author Krisztian Nagy
 *
 */
public class UI {
	// The scanner object
	private Scanner scanner;
	// Comparator to use
	private JaccardIndexCalculator comparator;

	public UI() {
		// Initialize the comparator
		this.comparator = new JaccardIndexCalculator();
	}

	/***
	 * Function used to read string from the console.
	 * 
	 * @return String
	 */
	private String readString() {
		// Reset the scanner
		this.scanner = new Scanner(System.in);
		// Get the next line
		return this.scanner.nextLine();
	}

	/***
	 * Function used to read int from the console.
	 * 
	 * @return int
	 */
	private int readInt(String message) {
		// Loop until a number is given
		while (true) {
			// Reset the scanner
			this.scanner = new Scanner(System.in);
			this.scanner.reset();
			try {
				// Read the next line
				int n = this.scanner.nextInt();
				// If its higher than 0
				if (n > 0) {
					// Return the number
					return n;
				}
			} catch (Exception e) {
				org.slf4j.LoggerFactory.getLogger(this.getClass()).debug(e.getMessage(), e);
				System.out.println(message);
			}

		}
	}

	/**
	 * Function used to draw the menu and return the user input
	 * 
	 * @return int - menu option
	 */
	private int drawMainMenu() {
		// The menu option
		int option = 0;

		// Show the options
		System.out.println("\n\n1. Add file(Files added:" + this.comparator.getNumberOfFiles() + ")");
		System.out.println("2. Read in files from directory");
		System.out.println("3. Set the sample size("+(this.comparator.getNumberOfSamples()==200 ?"default 200": "set to "+this.comparator.getNumberOfSamples())+")");
		System.out.println("4. Set number of consumer threads("+(this.comparator.getPoolSize()==100 ?"default 100": "set to "+this.comparator.getPoolSize())+")");
		System.out.println("5. Calculate Jaccard Index");
		System.out.println("-1. Exit");

		// Keep asking until a valid input is given
		while (option == 0) {
			// Keep asking until the value is valid
			System.out.println("Please input the menu number:");
			try {
				// Set the scanner
				this.scanner = new Scanner(System.in);
				// Read the int
				option = this.scanner.nextInt();
				// Return if it is valid
				if ((option > 0 || option == -1) && option < 6)
					return option;
			} catch (InputMismatchException e) {
				org.slf4j.LoggerFactory.getLogger(this.getClass()).debug(e.getMessage(), e);
			}
			// Reset if it is invalid
			option = 0;
		}
		// Return exit
		return option;
	}

	/**
	 * Show the menu and handles the menu choices
	 */
	public void showMenu() {
		// Run the application
		boolean alive = true;

		// Show the menu until its needed
		while (alive) {
			// Draw the menu
			switch (this.drawMainMenu()) {
			case 1:
				System.out.println("\nPlease enter the file path:");
				// Read the file name from the console and add to the list
				this.comparator.addFile(this.readString());
				break;
			case 2:
				System.out.println("\nPlease enter the direcotry path:");
				//Get path from user
				Path dirPath=Paths.get(this.readString());
				//Get if it is recursive
				boolean req=this.getRecursiveOption();
				System.out.println("Reading directory content. Please wait...");
				//Read the directory from the given path
				List<Path> fileList = DirectoryReader.getDirectoryContent(dirPath, req);
				//Check if the list is empty
				if(fileList.size()>0) {
					//Stream the file list
					fileList.forEach((fp)->{
						//Add the file to the comparators list
						this.comparator.addFile(fp.toString());
					});
					
					System.out.println(fileList.size()+" files has been added for file comparison");
				}else {
					System.out.println("\""+dirPath+"\" is not a directory or it is empty!");
				}
				break;
			case 3:
				System.out.println("\nPlease enter the sample size:");
				//Read the sample size
				int i=this.readInt("The sample size has to be higher than 0. Please input again:");
				// Set the sample size 
				this.comparator.setNumberOfSamples(i);
				System.out.println("\nThe sample size is now set to "+i);
				break;
			case 4:
				System.out.println("\nPlease enter the number of consumer threads:");
				//Read the pool size
				int p=this.readInt("The number of consumer threads has to be more than 1. Please input again:");
				// Set the pool size 
				this.comparator.setPoolSize(p);
				System.out.println("\nThe number of consumer threads is now set to "+p);
				break;
			case 5:
				//Check if there is at least two files
				if(this.comparator.getNumberOfFiles()>1) {
					// Let the user know the comparison started
					System.out.println("The file comparisons started. Please wait...");
					// Compare the files
					this.comparator.compare().forEach(r -> {
						//Check if the result is not -999 e.g. error
						if(!r.getResult().equals("-999")) {
							// Write out the result
							System.out.println("Comparison of \"" + r.getFileName1() + "\" and \"" + r.getFileName2()
									+ " resulted a(n) " + r.getComparisonType() + " of " + r.getResult());
						}else{
							//Write out error message
							System.out.println(r.getComparisonType());
						}
					});
					;
	
					System.out.println("Total run time: " + this.comparator.getRuntime() + "ms");
				}else {
					System.out.println("At least two files has to be added in order to compare.");
				}
				break;
			case -1:
				// Exit the application
				System.exit(0);
				break;
			}
		}

	}
	
	/**
	 * Reads in from the console the users choice to read the directory recursively or not 
	 * @return boolean
	 */
	private boolean getRecursiveOption() {
		int option=-1;
		System.out.println("Would you like to read the directory recursively?\n"+
						   "Press 1 for yes\n"+
						   "Press 2 for no");
		while(option<1 || option>2) {
			option=this.readInt("Please input the menu number:");
			if(option>2) {
				System.out.println("Please input the menu number:");
			}
		}
		//Return true on yes, return false on no
		return option==1;
	}

}
