
package com.kwic.security.base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Util {
	
    public Base64Util() {}

    /**
     *	Base64Encoding을 수행한다. binany in ascii out
     *
     *	@param encodeBytes encoding할 byte array
     *	@return encoding 된 String
     */
    public static String encode(byte[] encodeBytes) {
        
        
        ByteArrayInputStream bin = new ByteArrayInputStream(encodeBytes);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        Base64Encoder base64Encoder = new Base64Encoder(bin, bout);
        byte[] buf = null;

        try{
            base64Encoder.process();
        } catch(Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        buf = bout.toByteArray();
        return new String(buf).trim();
    }

    /**
     *	Base64Decoding 수행한다. binany out ascii in
     *
     *	@param strDecode decoding할 String
     *	@return decoding 된 byte array
     */
    public static byte[] decode(String strDecode) {
        
    	ByteArrayInputStream bin = new ByteArrayInputStream(strDecode.getBytes());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        Base64Decoder base64Decoder = new Base64Decoder(bin, bout);
        byte[] buf = null;

        try {		
            base64Decoder.process();
        } catch(Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }

        buf = bout.toByteArray();

        return buf;

    }
}
