/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

/**
 * Adds the {@literal @}Controller or {@literal @}RestController (depending on Spring Version - 4 by default) annotation to the given JDefinedClass
 *
 * @author kurt paris
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringRestControllerAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {
	
	/**
	 * Major version of spring to support
	 */
	private int springVersion;
	
	protected SpringRestControllerAnnotationRule () {
		this(4);
	}
	
	public SpringRestControllerAnnotationRule (int springVersion) {
		this.springVersion = springVersion;
	}
	
    @Override
    public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
    	Class<? extends Annotation> annotationType;
    	switch (springVersion) {
	    	case 3 :	annotationType = Controller.class;
	    				break;
	    	case 4 :	annotationType = RestController.class;
					    break;
		    default: 	throw new IllegalStateException("Spring Version not set");
    	}
    	
        return generatableType.annotate(annotationType);
    }
}
