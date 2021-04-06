// Base64Decoder.java
// $Id: Base64Decoder.java,v 1.1 1980/01/03 17:02:38 jjh Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package com.kwic.security.base64;

import java.io.* ;

/**
 * Decode a BASE64 encoded input stream to some output stream.
 * This class implements BASE64 decoding, as specified in the
 * <a href="http://ds.internic.net/rfc/rfc1521.txt">MIME specification</a>.
 * @see org.w3c.tools.codec.Base64Encoder
 */

public class Base64Decoder {
    private static final int BUFFER_SIZE = 800000 ;

    InputStream in       = null ;
    OutputStream out     = null ;
    boolean      stringp = false ;

    private final int get1 (byte buf[], int off) {
		return ((buf[off] & 0x3f) << 2) | ((buf[off+1] & 0x30) >>> 4) ;
    }

    private final int get2 (byte buf[], int off) {
		return ((buf[off+1] & 0x0f) << 4) | ((buf[off+2] &0x3c) >>> 2) ;
    }

    private final int get3 (byte buf[], int off) {
		return ((buf[off+2] & 0x03) << 6) | (buf[off+3] & 0x3f) ;
    }

    private final int check (int ch) 
    {
		if ((ch >= 'A') && (ch <= 'Z')) 
		{
	    	return ch - 'A' ;
		} 
		else if ((ch >= 'a') && (ch <= 'z')) 
		{
	    	return ch - 'a' + 26 ;
		} 
		else if ((ch >= '0') && (ch <= '9')) 
		{
	    	return ch - '0' + 52 ;
		} 
		else 
		{
	    	switch (ch) 
	    	{
				case '=':
					return 65 ;
	    		case '+':
					return 62 ;
				case '/':
					return 63 ;
				default:
					return -1 ;
			}
		}
    }

    /**
     * Do the actual decoding.
     * Process the input stream by decoding it and emiting the resulting bytes
     * into the output stream.
     * @exception IOException If the input or output stream accesses failed.
     * @exception Base64FormatException If the input stream is not compliant
     *    with the BASE64 specification.
     */

    public void process () 
	throws IOException, Base64FormatException
    {
		byte buffer[] = new byte[BUFFER_SIZE] ;
		byte chunk[]  = new byte[4] ;
		int  got      = -1 ;
		int  ready    = 0 ;
	
		fill:
		while ((got = in.read(buffer)) > 0) 
		{
			int skiped = 0 ;
			while ( skiped < got ) 
	    	{
				// Check for un-understood characters:
				while ( ready < 4 ) 
				{
			    	if ( skiped >= got )
						continue fill ;
			    	int ch = check (buffer[skiped++]) ;
			    	if ( ch >= 0 )
						chunk[ready++] = (byte) ch ;
				}
			
				if ( chunk[2] == 65 ) 
				{
		    		out.write(get1(chunk, 0));
		    		return ;
				} 
				else if ( chunk[3] == 65 ) 
				{
		    		out.write(get1(chunk, 0)) ;
		    		out.write(get2(chunk, 0)) ;
		    		return ;
				} 
				else 
				{
		    		out.write(get1(chunk, 0)) ;
		    		out.write(get2(chunk, 0)) ;
		    		out.write(get3(chunk, 0)) ;
				}
				ready = 0 ;
	    	} 
		}
		
		if ( ready != 0 )
	    	throw new Base64FormatException ("Invalid length.") ;
		out.flush() ;
		out.close();
    }
    
    /**
     * Do the decoding, and return a String.
     * This methods should be called when the decoder is used in
     * <em>String</em> mode. It decodes the input string to an output string
     * that is returned.
     * @exception RuntimeException If the object wasn't constructed to
     *    decode a String.
     * @exception Base64FormatException If the input string is not compliant 
     *     with the BASE64 specification.
     */

    public String processString () 
	throws Base64FormatException
    {
		if ( ! stringp )
	    	throw new RuntimeException (this.getClass().getName()
						+ "[processString]"
						+ "invalid call (not a String)");
		try 
		{
	    	process() ;
		} 
		catch (IOException e) 
		{
		}
		return ((ByteArrayOutputStream) out).toString() ;
    }

    /**
     * Create a decoder to decode a String.
     * @param input The string to be decoded.
     */

    public Base64Decoder (String input) 
    {
		byte bytes[] = input.getBytes () ;
		this.stringp = true ;
		this.in      = new ByteArrayInputStream(bytes) ;
		this.out     = new ByteArrayOutputStream () ;
    }

    /**
     * Create a decoder to decode a stream.
     * @param in The input stream (to be decoded).
     * @param out The output stream, to write decoded data to.
     */

    public Base64Decoder (InputStream in, OutputStream out) 
    {
		this.in = in ;
		this.out = out ;
		this.stringp = false ;
    }
    
    public Base64Decoder (String input, OutputStream out) 
    {
		byte bytes[] = input.getBytes () ;
		this.stringp = true ;
		this.in      = new ByteArrayInputStream(bytes) ;
		this.out     = out ;
    }
}
