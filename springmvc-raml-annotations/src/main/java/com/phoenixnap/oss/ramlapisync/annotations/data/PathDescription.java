package com.phoenixnap.oss.ramlapisync.annotations.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A description that will be applied to part of the Path/ URL of an API
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathDescription {
	
	/**
	 * The Partial url this will applied to
	 * 
	 * @return the partial url that the description with be applied to
	 */
	String key();
	
	/**
	 * Description that will be applied to the resource
	 * 
	 * @return String description that will be applied to the resource
	 */
	String value();
	
	
}
