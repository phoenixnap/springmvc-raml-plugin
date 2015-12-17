package com.phoenixnap.oss.ramlapisync.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gives an Example usage of this element. This will be reproduced as is in the generated raml spec
 * 
 * @author Kurt Paris
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Example {
	
	/**
	 * The String that will be displayed. currently only static strings are supported - this will be enhanced to support spring boot property file integration
	 * 
	 * @return The String example that will be displayed.
	 */
	String value() default "";

}
