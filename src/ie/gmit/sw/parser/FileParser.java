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

public class FileParser extends Parser {
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
				org.slf4j.LoggerFactory.getLogger(this.getClass()).trace(ex.getMessage(), ex);

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
			//Loop until the document is not udded to the map
			while (!added) {
				try {
					// Add the new document to the map O(1)
					if (MapSingleton.getInstance().putIfAbsent(this.fileNameHash,new ArrayList<Integer>(Collections.nCopies(200, null))) != null)
						//It is added
						added = true;
				} catch (Exception e) {
					
				}
			}
			
			//Buffer for the line left overs
			StringBuilder lsb =new StringBuilder();
			// Stream through the lines
			this.br.lines().forEach((line) -> {
				//Trim the line
				line = line.trim();
				if(line.length()>0) {
					if(lsb.length()>0) lsb.append(' ');
					//Add the line to the leftover
					lsb.append(line);
					//Split and filter the line
					StringBuilder sb = filterAndSplitToShingles(lsb.toString(),false);
					//Clear line buffer after the string is used
					lsb.setLength(0);
					//If there was any leftover add to the line
					if(sb.length()>0) {
						//Add to the line buffer
						lsb.append(sb.toString());
					}
				}
			});

			
			// Process the reminder of the lines
			filterAndSplitToShingles(lsb.toString(),true);
			// Set the producer to be stopped
			QueueSingleton.setProducerDone();
		}
		// Close the buffered reader
		this.br.close();
	}
	
	/**
	 * Filters a given string to only letters,digits and spaces. At the same time it counts the number of non consecutive spaces and cuts the string into a shingle and inserts the shingle into the Queue.
	 * If a given line does not have sufficient amount of words for a shingle or there are remainder words. The words are returned back by the method.
	 * If it is the last line, the shingle size is ignored.
	 * @param line String, line to filter and split
	 * @param lastline Boolean, whether this is the last line or not
	 * @return StringBuffer, the remainder of the line
	 */
	private StringBuilder filterAndSplitToShingles(String line, boolean lastline) {
		//Previous character
		char prev=0;
		//Space counter
		int spaceCounter=0;
		//Builder for the shingle
		StringBuilder sb =new StringBuilder();
		//Loop the characters
		for(int i=0; i<line.length(); i++) {
			//Get the character
			char c = line.charAt(i);
			//If the character is alphanumeric or space.
			if(Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
			//If the character is alphanumeric or space. This is not Unicode
			//if((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c==' ') {
				//If it is a trailing space or leading space
				if(Character.isSpaceChar(c) && (i==0 || i==line.length()-1)) {
					//Set previous
					prev=c;
					//Skip 
					continue;
				}
				
				//If it is not a duplicate space
				if(!Character.isSpaceChar(c) || (Character.isSpaceChar(c) && prev != c)) {
					//TODO: allow the shingle size to be changed
					//If the shingle size is reached or it is the last line and last character
					if((Character.isSpaceChar(c) && spaceCounter==2) || (lastline && i==line.length()-1)) {
						//Add the last character if it is not space 
						if(!Character.isSpaceChar(c)) {
							sb.append(c);
						}
						//Convert the string
						try {
							Shingle s = new Shingle(this.fileNameHash, sb.toString().hashCode());
							//Add the shingle to the Queue
							QueueSingleton.getInstance().put(s);
						} catch (InterruptedException e) {
							// Nothing we can do about it
							//e.printStackTrace();
						}
						//Reset the space counter
						spaceCounter=0;
						//Clear the buffer
						sb.setLength(0);
					}else if(Character.isSpaceChar(c)) {
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
			org.slf4j.LoggerFactory.getLogger(this.getClass()).trace(ex.getMessage(), ex);
		}
		return type;
	}

}
