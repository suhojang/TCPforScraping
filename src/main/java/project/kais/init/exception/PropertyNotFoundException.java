package project.kais.init.exception;

public class PropertyNotFoundException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PropertyNotFoundException(String propertyName){
		super(String.format("The value of property %s is not found.", propertyName));
	}

}
