/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.gmit.sw.parser;


/**
 *
 * @author Krisztian Nagy
 */
public abstract class Parser implements Parseable {
	// The posible encodings
	private String[] encoding = { "UTF-8", "UTF-16", "ISO-8859-1", "GB2312", "Windows-1251", "Windows-1252",
			"Shift JIS", "GBK", "Windows-1256", "ISO-8859-2", "EUC-JP", "ISO-8859-15", "ISO-8859-9", "Windows-1250",
			"Windows-1254", "EUC-KR", "Big5", "Windows-874", "US-ASCII", "TIS-620", "ISO-8859-7", "Windows-1255" };
	// The regex to separate the words by
	private String separatorRegex;
	// Whether to filter the lines or not
	private boolean filterLines;
	/**
	 * Constructor which initilizes the regex to separate the lines with and whether the lines should be filtered or not
	 * @param separatorRegex regex to separate the lines
	 * @param filterLines option t filter the lines or not
	 */
	public Parser(String separatorRegex, boolean filterLines) {
		this.separatorRegex = separatorRegex;
		this.filterLines = filterLines;
	}

	/**
	 * The function returns the character encodings
	 * 
	 * @return String array of encodings
	 */
	public String[] getEncodings() {
		return this.encoding;
	}

	// This method should be overwritten in one of the child classes to provide
	// specific messages

	@Override
	public String getErrorMessage() {
		return "The path is not avalibale or does not exist.";
	}

	/**
	 * Returns the regex which is used to separate the lines
	 * @return String
	 */
	public String getSeparatorRegex() {
		return separatorRegex;
	}

	/**
	 * Returns whether the lines should be filtered or not
	 * @return boolean
	 */
	public boolean isFilterLines() {
		return filterLines;
	}

}
