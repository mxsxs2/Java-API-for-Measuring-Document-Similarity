package ie.gmit.sw.parser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;

import ie.gmit.sw.QueueSingleton;
import ie.gmit.sw.Shingle;

public class FileParser extends Parser implements Runnable {
	// variable for the file
	private File file;
	// variable for the buffered reader
	private BufferedReader br;
	// file error message
	private String fileErrorMessage = "";

	public FileParser(String fileName,String separatorRegex, boolean filterLines) {
		//Call the parent
		super(separatorRegex,filterLines);
		//Set the file name
		this.file = new File(fileName);
		
	}

	@Override
    public String getSourceName(){
        //Return the name of the file
        return this.file.getName();
    }
	
	public String getFileErrorMessage() {
		return fileErrorMessage;
	}

	public boolean availableSource() {
		// Check if the file exists and if it is actually a file
		if (this.file.exists() && this.file.canRead())
			return this.file.isFile();
		// Set error message
		this.fileErrorMessage="The file does not exists or cannot read it.";
		// If not the return false;
		return false;
	}

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

	private void processLines() throws IOException {
		// Stream through the lines returns 579944(filter after split) words or
		// 579781(filter before split)
		// Check if the buffer is empty
		if (this.br.ready()) {
			//Add the producer to the QueueSingleton
			QueueSingleton.addProducer();
			// Get the word buffer
			LinkedList<String> wb = (LinkedList<String>) this.getWordBuffer();
			//Stream through the lines
			this.br.lines().forEach((line) -> {
				// Remove extra characters like '.,!-?' but leave the ' as that can make
				// different words. Also leave space for splitting
				if (this.isFilterLines())
					line = line.replaceAll("[^a-zA-Z0-9\' ]", "");
				// Separate the line by the given regex then convert the result to a list and
				// finally add to our word buffer
				wb.addAll(Arrays.asList(line.split(this.getSeparatorRegex())));
				//Cut the line and add the shingles to the blocking que
				this.addToQue(wb, false);
			});
			//Process the reminder of the lines
			this.addToQue(wb, true);
			//Set the producer to be stopped
			QueueSingleton.setProducerDone();
			
		}
		// Close the buffered reader
		this.br.close();
	}

	private void addToQue(LinkedList<String> line,boolean lastLine) {
		// Loop the words
		while (line.size() >= 3) {
			// Buffer for this shingle
			LinkedList<String> shingleBuffer = new LinkedList<String>();
			// TODO: allow the shingle size to be changed
			for (int i = 1; i <= 3; i++) {
				// get the next from the word buffer
				String next = line.poll();
				// If there is a word
				if (next != null) {
					// Add the word to the shingle buffer
					shingleBuffer.add(next);
				} else {
					// Break as there is no more words
					break;
				}
			}

			// Check if there is enough words for a shingle or if it is the last line
			if (shingleBuffer.size() == 3 || lastLine) {
				// Add the words to the builder
				String shingleString = String.join(" ", shingleBuffer);
				// Create a new shingle with the document id and the hascode of the shingle
				Shingle s = new Shingle(this.file.getName().hashCode(), shingleString.hashCode());
				// Add the shingle to the blocking queue
				try {
					QueueSingleton.getInstance().put(s);
				} catch (InterruptedException e) {
					//Nothing we can do about it
					e.printStackTrace();
				}
			} else {
				// Put back the words to the word buffer
				shingleBuffer.forEach((word) -> {
					// Back to the end of the word
					line.add(word);
				});
			}

		}
	}

	/**
	 * Auto detect the encoding of the file
	 * 
	 * @param charsetIndex
	 */
	private void decodeFile(int charsetIndex) throws IOException {
		// Declare the encodings to try
		String[] enc = this.getEncodings();
		try {
			// Create a buffered reader with a given charset
			BufferedReader br = Files.newBufferedReader(this.file.toPath(), Charset.forName(enc[charsetIndex]));
			// Temporary line holder
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
		//Read the content and process the lines into shingles and add them to the blocking que
		this.readContent();

	}


}
