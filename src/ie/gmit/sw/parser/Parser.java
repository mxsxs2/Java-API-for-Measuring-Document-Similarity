/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.gmit.sw.parser;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author user
 */
public abstract class Parser implements Parseable,Cloneable{
	//Buffer the words taken in
	private LinkedList<String> wordBuffer =new LinkedList<String>();
	//The posible encodings
    private String[] encoding={
    		"UTF-8",
			"UTF-16",
			"ISO-8859-1",
			"GB2312",
			"Windows-1251",
			"Windows-1252",
			"Shift JIS",
			"GBK",
			"Windows-1256",
			"ISO-8859-2",
			"EUC-JP",
			"ISO-8859-15",
			"ISO-8859-9",
			"Windows-1250",
			"Windows-1254",
			"EUC-KR",
			"Big5",
			"Windows-874",
			"US-ASCII",
			"TIS-620",
			"ISO-8859-7",
			"Windows-1255"};
    //The regex to separate the words by
    private String separatorRegex;
    //Whether to filter the lines or not
    private boolean filterLines;
    
    public Parser(String separatorRegex, boolean filterLines) {
    	this.separatorRegex=separatorRegex;
    	this.filterLines=filterLines;
    }
    
    /**
     * The function returns the character encodings
     * @return String[]
     */
    public String[] getEncodings(){
    	return this.encoding;
    }

    //This method should be overwritten in one of the child classes to provide specific messages
    
    @Override
    public String getErrorMessage(){
        return "The path is not avalibale or does not exist.";
    }
    

    public List<String> getWordBuffer() {
        //Return a clone of our lines
    	return this.wordBuffer;
    }

      
    public String getSeparatorRegex() {
		return separatorRegex;
	}

	public boolean isFilterLines() {
		return filterLines;
	}

	//This method should be overridden by the child classes
    //I need it here as otherwise I get a runtime error because of casting the child classes
    @Override
    public String getContentType(){
        return "";
    }
    
    
    //Clones the current parser object
    @Override
    public Parser clone() throws CloneNotSupportedException{
        try {
            //Return the clone of this object
            return (Parser)super.clone();
        } catch (CloneNotSupportedException ex) {
        	//Log the errors with sl4j and logback
        	org.slf4j.LoggerFactory.getLogger(this.getClass()).debug(ex.getMessage(), ex);
        }
        return this.clone();
    }
    
    
}
