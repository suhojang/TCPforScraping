package com.kwic.service;

import java.util.Hashtable;
import java.util.Map;

import com.kwic.datasource.Transactions;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

public class ServiceIMPL extends EgovAbstractServiceImpl{
	private long statusId;
	private Map<String,Transactions> txStatus	= new Hashtable<String,Transactions>();
	
	public synchronized String beginTransaction(String txName){
		if(statusId>9999999999L)
			statusId	= 0;
		String id	= String.valueOf(statusId++);
		txStatus.put(id, new Transactions(txName));
		return id;
	}
	
	public void commit(String id){
		txStatus.get(id).commit();
		txStatus.remove(id);
	}
	public void rollback(String id){
		txStatus.get(id).rollback();
		txStatus.remove(id);
	}
	
}
