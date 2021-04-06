
package com.kwic.security.base64;

/**
 * Exception for invalid BASE64 streams.
 */

public class Base64FormatException extends Exception {
	private static final long serialVersionUID = 1L;

/**
   * Create that kind of exception
   * @param msg The associated error message 
   */
  
  public Base64FormatException(String msg) {
	super(msg) ;
  }

}
