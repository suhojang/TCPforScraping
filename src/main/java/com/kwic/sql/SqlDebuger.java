package com.kwic.sql;

import project.kais.logger.Logger;
import project.kais.logger.LoggerFactory;

public class SqlDebuger {
	  protected static Logger logger = LoggerFactory.getLogger("com.kwic");
	  
	  public static void debug(String msg){
		  logger.debug(msg);
	  }
}
