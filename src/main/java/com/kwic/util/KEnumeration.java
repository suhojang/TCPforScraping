package com.kwic.util;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class KEnumeration implements Enumeration<Object>{
	private List<Object> keys	= new Vector<Object>();
	private int iter	= -1;
	
	public void add(Object obj){
		keys.add(obj);
	}
	
	@Override
	public boolean hasMoreElements() {
		iter++;
		return keys.size()>iter?true:false;
	}

	@Override
	public Object nextElement() {
		return keys.get(iter);
	}

}
