package project.kais.logger;

public class LoggerFactory {
	
	public static Logger getLogger(String name){
		return new Logger(org.slf4j.LoggerFactory.getLogger(name));
	}
	public static Logger getLogger(Class<?> claz){
		return new Logger(org.slf4j.LoggerFactory.getLogger(claz));
	}
}
