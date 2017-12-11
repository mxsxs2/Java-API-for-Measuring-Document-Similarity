package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ie.gmit.sw.parser.FileParser;

public class UI {
	//The scanner object
	private Scanner scanner;
	//List of files to compare
	private ArrayList<String> files;
	
	public UI() {
		//Initialise the file list
		this.files=new ArrayList<String>();
	}
	
	
	/***
	 * Function used to read string from the console.
	 * @return String
	 */
	private String readString(){
			System.out.println("\nPlease enter the filename:");
			//Reset the scanner
			this.scanner=new Scanner(System.in);
			//Get the next line
			return this.scanner.nextLine();
	}
	
	
	/**
	 * Function used to draw the menu and return the user input
	 * @return int - menu
	 */
	//O(n)<-same guess as previously
	private int drawMainMenu(){
		//The menu option
		int option=0;
		
			//Show the options
			System.out.println("\n\n1. Add file(Files added:"+this.files.size()+")");
			//System.out.println("2. Add second file");
			System.out.println("3. Calculate Jaccard Index");
			System.out.println("-1. Exit");
			
			
			//Keep asking until a valid input is given
			while(option==0){
				//Keep asking until the value is valid
				System.out.println("Please input the menu number:");
				try{
					//Set the scanner
					this.scanner=new Scanner(System.in);
					//Read the int
					option=this.scanner.nextInt();
					//Return if it is valid
					if((option>0 || option==-1) && option<4) return option;
				}catch(InputMismatchException e){
					
				}
				//Reset if it is invalid
				option=0;
			}
		//Return exit
		return option;
	}
	
	/**
	 * Show the menu and handles the menu choices
	 */
	public void showMenu() {
		//Run the application
				boolean alive=true;

				//Show the menu until its needed
				while(alive){
					//Draw the menu
					switch(this.drawMainMenu()){
						case 1:
							//Read the file name from the console and add to the list
							this.files.add(this.readString());
							break;
						case 2:
							//Read the file name from the console and add to the list
							this.files.add(this.readString());
							break;
						case 3:
							//Compare the files
							this.compareFiles2();
							break;
						case -1:
							//Exit the application
							System.exit(0);
							break;
					}
				}
		
	}
	
	
	/**
	 * Compares the predefined files and writes out the jaccard index of them.
	 */
	private void compareFiles2() {
		//Let the user know the comparison started
		System.out.println("The file comparisons started. Please wait...");
		//Start the timer
		long startTime = System.nanoTime();
		
		//Empty the Queue
		QueueSingleton.getInstance().clear();
		//Empty the Map
		MapSingleton.getInstance().clear();
		
		//Loop the file names
		this.files.forEach((name)->{
			// Read a new file in a thread
			Thread t1 = new Thread(new FileParser(name, " ", true), "Fileparser");
			// Start reading
			t1.start();
		});
		
		
		// Create an executor service
		//ExecutorService ex = Executors.newSingleThreadExecutor();
		ExecutorService ex=Executors.newSingleThreadExecutor();
		// Add the minhasher
		Future<Boolean> f = ex.submit(new MinHasher(200));
		
		try {
			f.get();
			ex.shutdown(); // Shut down the thread executor
			ex.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//The time took to read the files and create the shingles
			long read = (System.nanoTime() - startTime)/1000000;
				
			//This is quadratic. How ever it does not give huge overhead unless there is a large amount of files beeing compared
			//Loop the files list 
			for (int i = 0; i < this.files.size(); i++) {
				//Loop the files list
				  for (int j = i+1; j < this.files.size(); j++) {
					  //Get the teo list to compare
					  ArrayList<Integer> l = MapSingleton.getInstance().get(this.files.get(i).hashCode());
					  ArrayList<Integer> l2 = MapSingleton.getInstance().get(this.files.get(j).hashCode());
					  //Synchronise the list
						synchronized (l) {
							//Get intersection of the two lists
							l.retainAll(l2);
							System.out.println("\n");
							System.out.println("Comparison of \""+this.files.get(i)+"\" and \""+this.files.get(j)+"\"");
							System.out.println("Mathces: "+l.size());
							//Calculate the Jaccard index and output
							System.out.println("Jackard Index: "+(float)((float)l.size()/(float)(l2.size()*2-l.size())));
						}
				  }
			}
			//Out put time measures
			System.out.println("\nRun time of file reading and Shingle creation  "+read+"ms");
			System.out.println("Total run time: "+(System.nanoTime() - startTime)/1000000+"ms");

		}
	}

	
	
	
	
	
	/**
	 * Compares the predefined files and writes out the jaccard index of them.
	 */
	private void compareFiles() {
		String file1= this.files.get(0);
		//String file1= "warnp.txt";
		//String file2= "warnp.txt";
		String file2= this.files.get(1);
		
		
		long startTime = System.nanoTime();
		// Read a new file in a thread
		Thread t1 = new Thread(new FileParser(file1, " ", true), "Fileparser");
		// Start reading
		t1.start();
		// Read a new file in a thread
		Thread t2 = new Thread(new FileParser(file2, " ", true), "Fileparser2");
		// Start reading
		t2.start();
		
		// Create an executor service
		//ExecutorService ex = Executors.newSingleThreadExecutor();
		ExecutorService ex=Executors.newSingleThreadExecutor();
		// Add the minhasher
		Future<Boolean> f = ex.submit(new MinHasher(200));
		
		try {
			f.get();
			ex.shutdown(); // Shut down the thread executor

			ex.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			System.out.println("Finished Threads in "+(System.nanoTime() - startTime)/1000000+"ms");	
			// Get the list of the minhashes
			ArrayList<Integer> l = MapSingleton.getInstance().get(file1.hashCode());
			ArrayList<Integer> l2 = MapSingleton.getInstance().get(file2.hashCode());

			synchronized (l) {
				l.retainAll(l2);
				System.out.println("Mathces: "+l.size());
				System.out.println("Jackard Index: "+(float)((float)l.size()/(float)(l2.size()*2-l.size())));
				System.out.println("Finished in "+(System.nanoTime() - startTime)/1000000+"ms");
			}

		}
	}
	
}
