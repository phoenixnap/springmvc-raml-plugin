package com.phoenixnap.oss.ramlapisync.generation.exception;

/**
 * A runtime exception telling the caller that there is a problem with the CodeModel.
 * @author armin.weisser
 * @since 0.4.1
 */
public class InvalidCodeModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCodeModelException(String message) {
        super(message);
    }

    public InvalidCodeModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
