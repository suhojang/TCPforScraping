package com.kwic.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtil {
	public static final byte APPEND_CHARACTER_ZERO  = 0x30;
	public static final byte APPEND_CHARACTER_SPACE = 0x20;

	public static byte[] addByte(byte[] bytes, byte addByte, int size, boolean addLeft) {
		if (bytes.length == size) {
			return bytes;
		}
		if (bytes.length > size) {
			byte[] cpBytes = new byte[size];
			System.arraycopy(bytes, 0, cpBytes, 0, size);
			return cpBytes;
		}
		ByteBuffer bf = ByteBuffer.allocate(size);
		if (!addLeft){
			bf.put(bytes);
		}
		for (int i = 0; i < size - bytes.length; i++) {
			bf.put(addByte);
		}
		if (addLeft){
			bf.put(bytes);
		}
		return bf.array();
	}
	
	public static byte[] addByte(byte[] bytes, byte addByte, int size, boolean addLeft, String encoding) throws UnsupportedEncodingException {
		if (bytes.length == size) {
			return bytes;
		}
		if (bytes.length > size) {
			StringBuffer sb	= new StringBuffer();
			
			int len	= 0;
			String param	= new String(bytes, encoding);
			for (int i = 0; i < param.length(); i++) {
				String c	= Character.toString(param.charAt(i));
				int c_Len	= c.getBytes(encoding).length;
				if (c_Len == 2) {
					if (len + 2	== size) {
						sb.append(c);
						break;
					} else if (len + 1 == size) {
						sb.append(new String(new byte[]{APPEND_CHARACTER_SPACE}, encoding));
						break;
					} else if (len == size) {
						break;
					}
				}
				
				len += c_Len;
				if (len > size) {
					break;
				}
				sb.append(c);
			}
			
			byte[] cpBytes	= sb.toString().getBytes(encoding);
			return cpBytes;
		}
		ByteBuffer bf = ByteBuffer.allocate(size);
		if (!addLeft){
			bf.put(bytes);
		}
		for (int i = 0; i < size - bytes.length; i++) {
			bf.put(addByte);
		}
		if (addLeft){
			bf.put(bytes);
		}
		return bf.array();
	}

	public static byte[] append(byte[] bytes, byte[] append) {
		ByteBuffer bf = ByteBuffer.allocate(bytes.length + append.length);
		bf.put(bytes);
		bf.put(append);
		return bf.array();
	}
}
