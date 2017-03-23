package com.phoenixnap.oss.ramlapisync.pojo;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.util.StringUtils;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;

/*
 * Copyright 2002-2017 the original author or authors.
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
/**
 * 
 * Class containing the validation information that should be applied to a particular type
 * 
 * @author kurtpa
 * @since 0.10.2
 *
 */
public class RamlTypeValidations {
	
	Integer minLength;
	Integer maxLength;
	Double minimum;
	Double maximum;
	String pattern;
	Boolean required;
	
	public RamlTypeValidations (Boolean required) {
		if (required == null || required == true) {
			this.required = true;
		} else {
			this.required = false;
		}
	}
	
	public RamlTypeValidations withLenghts(Integer minLength, Integer maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		return this;
	}
	
	public RamlTypeValidations withPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public Integer getMinLength() {
		return minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public String getPattern() {
		return pattern;
	}

	public boolean isRequired() {
		return required;
	}

	/**
	 * Adds validation annotations to the supplied field
	 * 
	 * @param field
	 */
	public void annotateFieldJSR303(JFieldVar field) {
		if (isRequired()) {
			field.annotate(NotNull.class);
		}
		if (StringUtils.hasText(getPattern())) {
			JAnnotationUse annotation = field.annotate(Pattern.class);
			annotation.param("regexp", getPattern());
		}
		if (getMinLength() != null || getMaxLength() != null) {
			JAnnotationUse annotation = field.annotate(Size.class); 

            if (getMinLength() != null ) {
                annotation.param("min", getMinLength());
            }

            if (getMaxLength() != null ) {
                annotation.param("max", getMaxLength());
            }
		}

        if (minimum != null ) {
        	JAnnotationUse annotation = field.annotate(DecimalMin.class); 
            annotation.param("value", String.valueOf(minimum));
        }

        if (maximum != null ) {
        	 JAnnotationUse annotation = field.annotate(DecimalMax.class);
            annotation.param("value", String.valueOf(maximum));
        }
	}

	public RamlTypeValidations withMinMax(Double minimum, Double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
		return this;
	}
	

}
