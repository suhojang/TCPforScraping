package com.kwic.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.ContextLoader;

public class Transactions {
	private DataSourceTransactionManager txManager;
	private TransactionStatus status;
	
	public Transactions(String txName){
		beginTransaction(txName);
	}
	
	private void beginTransaction(String txName){
		txManager	= (DataSourceTransactionManager)ContextLoader.getCurrentWebApplicationContext().getBean(txName);		
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		status	= txManager.getTransaction(def);
	}
	
	public void commit(){
		txManager.commit(status);
	}

	public void rollback(){
		txManager.rollback(status);
	}
}
