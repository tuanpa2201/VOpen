package base.exception;

/**
 * <p>
 * Exception khi hai gia tri khong cung kieu
 * Jul 21, 2016
 * </p>
 * 
 * @author VuD
 *
 */

public class NoSameTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSameTypeException() {
		super();
	}

	public NoSameTypeException(String msg) {
		super(msg);
	}
}
