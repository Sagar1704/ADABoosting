package sagar.machinelearning.exceptions;

/**
 * This exception is thrown when the algorithm is invoked without scanning the input file
 * 
 * @author Sagar
 * 
 */
public class FileNotScannedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8493930533888842361L;

	public FileNotScannedException(String message) {
		super(message);
	}
}
