package ie.gmit.sw.parser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

import ie.gmit.sw.MapSingleton;
import ie.gmit.sw.QueueSingleton;
import ie.gmit.sw.Shingle;

public class FileParser extends Parser implements Runnable {
	// variable for the file
	private File file;
	// variable for the buffered reader
	private BufferedReader br;
	// file error message
	private String fileErrorMessage = "";
	// The file name hash
	private int fileNameHash;

	public FileParser(String fileName, String separatorRegex, boolean filterLines) {
		// Call the parent
		super(separatorRegex, filterLines);
		// Set the file name
		this.file = new File(fileName);
		// Generate the hash from the file name this is O(n) So better to do it here
		// then individually for very shingle
		this.fileNameHash = fileName.hashCode();
	}

	@Override
	public String getSourceName() {
		// Return the name of the file
		return this.file.getName();
	}

	@Override
	public String getErrorMessage() {
		return fileErrorMessage;
	}

	@Override
	public boolean availableSource() {
		// Check if the file exists and if it is actually a file
		if (this.file.exists() && this.file.canRead())
			return this.file.isFile();
		// Set error message
		this.fileErrorMessage = "The file does not exists or cannot read it.";
		// If not the return false;
		return false;
	}

	@Override
	public boolean readContent() {
		// Check if the file is available
		if (this.availableSource()) {
			try {
				// Try to decode the file into a buffered reader
				this.decodeFile(0);
				// Return false if the buffered reader is still null
				if (this.br == null)
					return false;
				// Store the lines
				this.processLines();
				return true;
			} catch (IOException ex) {
				// Do nothing the file will be found anyways. We check this before.
				// Log the errors with sl4j and logback
				org.slf4j.LoggerFactory.getLogger(this.getClass()).debug(ex.getMessage(), ex);

			}
		}
		return false;
	}

	/**
	 * Goes through each line of the file, splits the lines up to shingles and adds
	 * the shingles to the QueueSingleton Adds the document key to the MapSingleton
	 * and fills up the fixed size list in it with Integer.MAX_VALUE
	 * 
	 * @throws IOException
	 */
	private void processLines() throws IOException {
		// Check if the buffer is empty
		if (this.br.ready()) {
			// Add the producer to the QueueSingleton
			QueueSingleton.addProducer();
			boolean added = false;
			while (!added) {
				try {
					//TODO: Allow the compare size to be changed
					// Add the new document to the map O(1)
					//if (MapSingleton.getInstance().putIfAbsent(this.fileNameHash,new ArrayList<Integer>()) != null)
					if (MapSingleton.getInstance().putIfAbsent(this.fileNameHash,new ArrayList<Integer>(Collections.nCopies(200, null))) != null)
						added = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*// Get the word buffer
			LinkedList<String> wb = (LinkedList<String>) this.getWordBuffer();
			// Create filter pattern. It is less overhead than calling replaceAll on every
			// line and compile the pattern every time
			Pattern p = Pattern.compile("[^\\p{IsAlphabetic}^\\p{IsDigit}\'\\s]"); // Remove not alphanumeric characters
			// Create filter pattern for multiple white spaces
			Pattern wsp = Pattern.compile("\\s{2,}");*/
			
			//Buffer for the line left overs
			StringBuffer lsb =new StringBuffer();
			// Stream through the lines
			this.br.lines().forEach((line) -> {
				//Trim the line
				line = line.trim();
				if(line.length()>0) {
					if(lsb.length()>0) lsb.append(' ');
					//Add the line to the leftover
					lsb.append(line);
					//Split and filter the line
					StringBuffer sb = filetrAndSplitToShingles(lsb.toString(),false);
					//Clear line buffer after the string is used
					lsb.setLength(0);
					//If there was any leftover add to the line
					if(sb.length()>0) {
						//Add to the line buffer
						lsb.append(sb.toString());
					}
				}
				
				/*// Trim the line
				line = line.trim();
				// Skip empty lines
				if (line.length() != 0) {
					// Remove extra characters like '.,!-?' but leave the ' as that can make
					// different words. Also leave space for splitting
					if (this.isFilterLines()) {
						// Remove non alphanumeric
						line = p.matcher(line).replaceAll("");
						// Remove multiple white spaces with one
						line = wsp.matcher(line).replaceAll(" ");
						// System.out.println(line);
					}
					// Separate the line by the given regex then convert the result to a list and
					// finally add to our word buffer
					wb.addAll(Arrays.asList(line.split(this.getSeparatorRegex())));
					// Cut the line and add the shingles to the blocking queue
					this.addToQue(wb, false);
				}*/
			});

			
			// Process the reminder of the lines
			filetrAndSplitToShingles(lsb.toString(),true);
			//this.addToQue(wb, true);
			// Set the producer to be stopped
			QueueSingleton.setProducerDone();
		}
		// Close the buffered reader
		this.br.close();
	}
	
	/**
	 * Filters a given string to only letters,digits and spaces. At the same time it counts the number of non consecutive spaces and cuts the string into a shingle and inserts the shingle into the Queue.
	 * If a given line does not have sufficient amount of words for a shingle or there are remainder words. The words are returned back by the method.
	 * The method does work with Unicode 
	 * If it is the last line, the shingle size is ignored.
	 * @param line String, line to filter and split
	 * @param lastline Boolean, whether this is the last line or not
	 * @return StringBuffer, the remainder of the line
	 */
	private StringBuffer filetrAndSplitToShingles(String line, boolean lastline) {
		//Previous character
		char prev=0;
		//Space counter
		int spaceCounter=0;
		//Buffer for the shingle
		StringBuffer sb =new StringBuffer();
		//Loop the characters
		for(int i=0; i<line.length(); i++) {
			//Get the character
			char c = line.charAt(i);
			//If the character is alphanumeric or space. This is Unicode
			if(Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
			//If the character is alphanumeric or space. This is not Unicode
			//if((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c==' ') {
				//If it is a trailing space or leading space
				if(c==' ' && (i==0 || i==line.length()-1)) {
					//Set previous
					prev=c;
					//Skip 
					continue;
				}
				
				//If it is not a duplicate space
				if(c!=' ' || (c==' ' && prev != c)) {
					//TODO: allow the shingle size to be changed
					//If the shingle size is reached or it is the last line and last character
					if((c==' ' && spaceCounter==2) || (lastline && i==line.length()-1)) {
						//Add the last character if it is not space 
						if(c!=' ') {
							sb.append(c);
						}
						//Convert the string
						try {
							Shingle s = new Shingle(this.fileNameHash, sb.toString().hashCode());
							//Add the shingle to the Queue
							QueueSingleton.getInstance().put(s);
						} catch (InterruptedException e) {
							// Nothing we can do about it
							e.printStackTrace();
						}
						//Reset the space counter
						spaceCounter=0;
						//Clear the buffer
						sb.setLength(0);
					}else if(c==' ') {
						//Add to the buffer
						sb.append(c);
						//Increment counter
						spaceCounter++;
						//Set previous
						prev=c;
					}else {
						//Add to the buffer
						sb.append(c);
						//Set previous
						prev=c;
					}
				}
			}
		}
		//Return the string buffer
		return sb;
	}

	/**
	 * Creates shingles from the line, calculates the hash code on them and adds
	 * them to the QueueSingleton.
	 * 
	 * @param LinkedList<String> the line to be converted to shingles
	 * @param boolean Whether this is the last line in the file or not
	 */

	/*private void addToQue(LinkedList<String> line, boolean lastLine) {
		// Loop the words if there is at least 3 left or it is the last line
		while (line.size() >= 3 || (lastLine && line.size() > 0)) {
			// Create a string buffer for the shingle
			StringBuffer sb = new StringBuffer();
			// TODO: allow the shingle size to be changed
			for (int i = 1; i <= 3; i++) {
				// get the next from the word buffer
				String next = line.poll();
				// Check if there is an item
				if (next != null) {
					// Add a space as a separator if it is not the first word
					if (i > 1)
						sb.append(" ");
					// Add the next to the buffer
					sb.append(next);
				} else {
					// Break as there is no more words
					break;
				}
			}

			// Create a new shingle with the document id and the hascode of the shingle
			//System.out.println(this.fileNameHash + ": " + sb.toString());
			Shingle s = new Shingle(this.fileNameHash, sb.toString().hashCode());
			// Add the shingle to the blocking queue
			try {
				QueueSingleton.getInstance().put(s);
				//System.out.println("Add :"+s.getDocId()+" "+s.getHashCode());
			} catch (InterruptedException e) {
				// Nothing we can do about it
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * Auto detect the encoding of the file
	 * 
	 * @param charsetIndex of the charset array to be used
	 */
	private void decodeFile(int charsetIndex) throws IOException {
		// Declare the encodings to try
		String[] enc = this.getEncodings();
		try {
			// Create a buffered reader with a given charset
			BufferedReader br = Files.newBufferedReader(this.file.toPath(), Charset.forName(enc[charsetIndex]));
			// Temporary line holder
			@SuppressWarnings("unused")
			String line;
			// Try to read the first line
			if ((line = br.readLine()) != null) {
				// Close the stream
				br.close();
				// If the line could be read then "rewind" the buffered reader and return it
				this.br = Files.newBufferedReader(this.file.toPath(), Charset.forName(enc[charsetIndex]));
			}
		} catch (MalformedInputException ex) {
			// If an exception was thrown (for example because of the wrong charset)
			// Check if there is any more charset is available for testing
			if (enc.length > ++charsetIndex) {
				// Try to decode again with a different encoding
				this.decodeFile(charsetIndex);
			}
		}
	}

	@Override
	public String getContentType() {
		// Set the base type
		String type = "Not determinable";
		try {
			// Try to get the content type
			String t = java.nio.file.Files.probeContentType(this.file.toPath());
			// If we have a content type the override the type variable
			if (t != null)
				type = t;
		} catch (IOException ex) {
			// Do nothing the file will be found anyways. We check this before.
			// Log the errors with sl4j and logback
			org.slf4j.LoggerFactory.getLogger(this.getClass()).debug(ex.getMessage(), ex);
		}
		return type;
	}

	@Override
	public void run() {
		// Read the content and process the lines into shingles and add them to the blocking queue
		this.readContent();

	}

}
