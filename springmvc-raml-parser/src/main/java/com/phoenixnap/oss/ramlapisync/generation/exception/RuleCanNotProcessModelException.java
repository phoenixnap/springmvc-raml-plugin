package com.phoenixnap.oss.ramlapisync.generation.exception;

/**
 * An exception that tells the user that a rule can't process the given RAML model.
 * @author armin.weiss
 * er
 * @since 0.4.1
 */
public class RuleCanNotProcessModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuleCanNotProcessModelException(String message) {
        super(message);
    }

    public RuleCanNotProcessModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
