/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.gmit.sw.parser;


/**
 *
 * @author user
 */
public interface Parseable{
    
    /**
     * Check if the source is available
     * @return boolean
     */
    public boolean availableSource();
    
    /**
     * Get source error message
     * @return String
     */
    public String getErrorMessage();
   
    /*
     * Get the name of the source
     * @return String
     */
    public String getSourceName();
      
    /**
     * Buffer the parsed content by a regex.
     * If the regex is an empty String then the line will not be separated
     * @param separatorRegex
     * @return boolean
     */
    public boolean readContent();
    
    /**
     * Get Content Type
     * @return String
     */
    public String getContentType();

}
