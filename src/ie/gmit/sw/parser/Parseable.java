/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.gmit.sw.parser;


/**
 * Basic set of methods which a parsebale class should implement
 * @author Krisztian Nagy
 */
public interface Parseable{
    
    /**
     * Checks if the source is available
     * @return boolean
     */
    public boolean availableSource();
    
    /**
     * Returns the source error message
     * @return String
     */
    public String getErrorMessage();
   
    /*
     * Returns the name of the source
     * @return String
     */
    public String getSourceName();
      
    /**
     * Reads the content of the file.
     * Returns whether the file was read successfully or not
     * @return boolean
     */
    public boolean readContent();
    
    /**
     * Returns the content type of the file
     * @return String
     */
    public String getContentType();

}
