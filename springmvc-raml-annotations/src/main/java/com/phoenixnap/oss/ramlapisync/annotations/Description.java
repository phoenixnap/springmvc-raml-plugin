package com.phoenixnap.oss.ramlapisync.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.phoenixnap.oss.ramlapisync.annotations.data.PathDescription;

/**
 * Gives an description to this element. This will be reproduced as is in the generated raml spec
 * 
 * @author Kurt Paris
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
	
	/**
	 * Description string for parts of the URL
	 * 
	 */
	PathDescription[] pathDescriptions() default {};

}
