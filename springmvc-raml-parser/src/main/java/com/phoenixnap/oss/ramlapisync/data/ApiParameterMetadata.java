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
package com.phoenixnap.oss.ramlapisync.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.phoenixnap.oss.ramlapisync.annotations.Example;
import com.phoenixnap.oss.ramlapisync.naming.JavaTypeHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlAbstractParam;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * Data object containing information kept at runtime for Api Call Parameters. This class is used by the RAML generator
 * and serves as a middle layer to store information extracted from the source in a way that makes sense to the
 * generator
 * 
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class ApiParameterMetadata {

	/**
	 * The name of the parameter
	 */
	private String name;

	/**
	 * Java Parameter data relating to this parameter
	 */
	private Parameter param;
	
	/**
	 * RAML Parameter data relating to this parameter
	 */
	private RamlAbstractParam ramlParam;

	/**
	 * The Java Type of the parameter
	 */
	private Class<?> type;
	
	/**
	 * A format narrowing the allowed data for this parameter
	 */
	private String format;

	/**
	 * If the type contains generics, this is the type of the generic as defined in the code.
	 */
	private Type genericType;

	/**
	 * Can this parameter be passed in as null
	 */
	private boolean nullable;

	/**
	 * Is this parameter the identifying parameter to the rest resource
	 */
	private boolean resourceId;

	/**
	 * An example of valid data for this parameter
	 */
	private String example;

	/**
	 * Simple method to prefer a string (which usually is specified by annotation)
	 * @param preferredOption The first choice for the parameter name
	 * @param fallback if the preferred sources is empty use this string
	 * @return the better choice between these two strings 
	 */
	private String resolveParameterName(String preferredOption, Parameter fallback) {
		if (StringUtils.hasText(preferredOption)) {
			return preferredOption;
		} else {
			return fallback != null ? fallback.getName() : null;
		}
	}
	
	/**
	 * Default constructor that creates a metadata object from a Raml parameter
	 * 
	 * @param name	The name of this parameter if different in annotation
	 * @param param Java Parameter representation
	 */
	public ApiParameterMetadata(String name, RamlAbstractParam param) {
		super();

		if (param == null) {
			throw new NullArgumentException("param");
		}


		if (param instanceof RamlUriParameter) {
			resourceId = true;
			nullable = false;
		} else {
			resourceId = false;
			nullable = !param.isRequired();
		}


		this.name = name;
		this.param = null; //we wont have this.
		
		this.format = param.getFormat();
		this.type = SchemaHelper.mapSimpleType(param.getType(), this.format);
		
		//If it's a repeatable parameter simply convert to an array of type
		if(param.isRepeat()) {
			this.type = Array.newInstance(this.type, 0).getClass();
		}
		
		this.genericType = null;

		this.example = StringUtils.hasText(param.getExample()) ? param.getExample() : null;
		this.setRamlParam(param);
	}

	/**
	 * Default constructor that creates a metadata object from a Java parameter
	 * 
	 * @param param Java Parameter representation
	 */
	public ApiParameterMetadata(Parameter param) {
		super();
		this.resourceId = false;
		this.nullable = false;

		String annotatedName = null;
		if (param == null) {
			throw new NullArgumentException("param");
		}

		RequestParam requestParam = param.getAnnotation(RequestParam.class);
		if (requestParam != null) {
			annotatedName = requestParam.value();
			nullable = !requestParam.required();
		}

		PathVariable pathVariable = param.getAnnotation(PathVariable.class);
		if (pathVariable != null) {
			resourceId = true;
			annotatedName = pathVariable.value();
		}

		RequestBody requestBody = param.getAnnotation(RequestBody.class);
		if (requestBody != null) {
			nullable = !requestBody.required();

		}

		this.name = resolveParameterName(annotatedName, param);
		this.param = param;
		if (param != null) {
			this.type = param.getType();
			this.genericType = JavaTypeHelper.inferGenericType(param.getParameterizedType());
		}

		Example parameterExample = param.getAnnotation(Example.class);
		if (parameterExample != null && StringUtils.hasText(parameterExample.value())) {
			this.example = parameterExample.value();
		}
	}

	/**
	 * The Java Type of the parameter
	 * 
	 * @return The Java Type of the parameter
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * The Java Type of the generic portion of the parameter
	 * 
	 * @return The Java Type of the generic portion of the parameter
	 */
	public Type getGenericType() {
		return genericType;
	}

	/**
	 * Can this parameter be passed in as null
	 * 
	 * @return boolean which if false means this parameter cannot be null
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Is this parameter the identifying parameter to the rest resource. If true then this parameter should end up as
	 * part of the URL
	 * 
	 * @return a boolean which if true implies that this parameter is part of the url/id of the resource
	 */
	public boolean isResourceId() {
		return resourceId;
	}

	/**
	 * The Parameter Name in RAML
	 * 
	 * @return String with the parameter name
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the name of the parameter as specified in Java (ignores any annotations)
	 * 
	 * @return string with the variable name of this parameter
	 */
	public String getJavaName() {
		return param.getName();
	}

	/**
	 * Checks if the param is annotated by a particular annotation
	 * 
	 * @param annotationClass The type of the Annotation required
	 * @return true if this parameter has the annotation required 
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return param.isAnnotationPresent(annotationClass);
	}

	/**
	 * The example which is set with this parameter
	 * 
	 * @return String showing example usage of this parameter
	 */
	public String getExample() {
		return example;
	}

	@Override
	public String toString() {
		return "ApiParameterMetadata [name=" + name + ", type=" + type + "]";
	}

	public RamlAbstractParam getRamlParam() {
		return ramlParam;
	}

	private void setRamlParam(RamlAbstractParam ramlParam) {
		this.ramlParam = ramlParam;
	}
	

	/**
	 * Quick check to see if this is an array type or not
	 * @return true if this is an array/list or false if it's a single object
	 */
	public boolean isArray() {
		if (type == null) {
			return false;
		}
		return type.isArray() || List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type);
	}

}
