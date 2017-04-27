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
package com.phoenixnap.oss.ramlapisync.pojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsonschema2pojo.util.NameHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * Builder pattern for Enum generation using jCodeModel. 
 * 
 * @author kurtpa
 * @since 0.10.2
 *
 */
public class EnumBuilder extends AbstractBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(EnumBuilder.class);
	
	private JFieldVar valueField = null;
	
	private JFieldVar lookupMap = null;
	
	private transient Map<String, Boolean> ENUM_CACHE = new HashMap<>();

	public EnumBuilder() {
		super();
	}

	/**
	 * Constructor allowing chaining of JCodeModels
	 * 
	 * @param config The Configuration object which controls generation
	 * @param pojoModel Existing Codemodel to append to
	 * @param className Class to be created
	 * 
	 */
	public EnumBuilder(PojoGenerationConfig config, JCodeModel pojoModel, String className) {
		super(config, pojoModel);
		withName(config.getPojoPackage(), className);
	}

	
	/**
	 * Sets this Pojo's name
	 * 
	 * @param pojoPackage The Package used to create POJO
	 * @param className Class to be created
	 * @return This instance
	 */
	public EnumBuilder withName(String pojoPackage, String className) {
		className = NamingHelper.convertToClassName(className);
		
		final String fullyQualifiedClassName = pojoPackage + "." + className;
		// Initiate package if necessary
		if (this.pojoPackage == null) {
			withPackage(pojoPackage);
		}
		// Builders should only have 1 active pojo under their responsibility
		if (this.pojo != null) {
			throw new IllegalStateException("Enum already created");
		}

		try {
			// create the class
			logger.debug("Creating Enum " + fullyQualifiedClassName);
			this.pojo = this.pojoModel._class(fullyQualifiedClassName, ClassType.ENUM);
			
			// Handle Serialization
			// Do enums need to be serializable?
			//implementsSerializable(); 
		} catch (JClassAlreadyExistsException e) {
			// class already exists - reuse it!
			logger.debug("Enum {} already exists. Reusing it!", fullyQualifiedClassName);
			this.pojo = this.pojoModel._getClass(fullyQualifiedClassName);
		}

		// Add to shortcuts
		this.codeModels.put(fullyQualifiedClassName, this.pojo);
		return this;
	}
	
	public EnumBuilder withValueField(Class<?> type) {
		pojoCreationCheck();
		if (!this.pojo.fields().containsKey("value")) {
			//Create Field
			valueField = this.pojo.field(JMod.PRIVATE | JMod.FINAL, type, "value");
			
			//Create private Constructor
	        JMethod constructor =  this.pojo.constructor(JMod.PRIVATE);
	        JVar valueParam = constructor.param(type, "value");
	        constructor.body().assign(JExpr._this().ref(valueField), valueParam);
	        
	        //add values to map
	        JClass lookupType = this.pojo.owner().ref(Map.class).narrow(valueField.type().boxify(), this.pojo);
	        lookupMap = this.pojo.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, lookupType, "VALUE_CACHE");
	
	        JClass lookupImplType = this.pojo.owner().ref(HashMap.class).narrow(valueField.type().boxify(), this.pojo);
	        lookupMap.init(JExpr._new(lookupImplType));
	
	        JForEach forEach = this.pojo.init().forEach(this.pojo, "c", JExpr.invoke("values"));
	        JInvocation put = forEach.body().invoke(lookupMap, "put");
	        put.arg(forEach.var().ref("value"));
	        put.arg(forEach.var());
	        
	        //Add method to retrieve value
	        JMethod fromValue = this.pojo.method(JMod.PUBLIC, valueField.type(), "value");
	        fromValue.body()._return(JExpr._this().ref(valueField));
	        
	        addFromValueMethod();
	        addToStringMethod();
		}
        return this;
	}
	
	
	private void addFromValueMethod() {
        JMethod fromValue = this.pojo.method(JMod.PUBLIC | JMod.STATIC, this.pojo, "fromValue");
        JVar valueParam = fromValue.param(valueField.type(), "value");

        fromValue.body()._return(lookupMap.invoke("get").arg(valueParam));
    }
	
	private void addToStringMethod() {
		JMethod toString = this.pojo.method(JMod.PUBLIC, String.class, "toString");
		toString.annotate(Override.class);
		
	    JExpression toReturn = JExpr._this().ref(valueField);
	    if (!valueField.type().fullName().equals(String.class.getName())) {
	    	toReturn = toReturn.invoke("toString");
	    }
	    toString.body()._return(toReturn);	
	    
	}

	public <T> EnumBuilder withEnum(T name, Class<T> type) {
		pojoCreationCheck();
		String cleaned = NamingHelper.cleanNameForJavaEnum(name.toString());
		if (!doesEnumContainField(type, cleaned)) {
			withValueField(type);
			ENUM_CACHE.put(cleaned, true);
			logger.debug("Adding field: " + name + " to " + this.pojo.name());
			if (StringUtils.hasText(cleaned)) {
				JEnumConstant enumConstant = this.pojo.enumConstant(cleaned);
				if (type.equals(Integer.class)) {
					enumConstant.arg(JExpr.lit((Integer)name));
				} else if (type.equals(Boolean.class)) {
					enumConstant.arg(JExpr.lit((Boolean)name));
				} else if (type.equals(Double.class)) {
					enumConstant.arg(JExpr.lit((Double)name));
				} else if (type.equals(Float.class)) {
					enumConstant.arg(JExpr.lit((Float)name));
				} else if (type.equals(Long.class)) {
					enumConstant.arg(JExpr.lit((Long)name));
				} else {
					enumConstant.arg(JExpr.lit(name.toString()));
				}
			}
		}
		return this;
	}
	
	private boolean doesEnumContainField(Class type, String name) {
		if (ENUM_CACHE.containsKey(name)) {
			return true;
		} else {
			
			boolean contains = false;
			try {
				Field field = this.pojo.getClass().getDeclaredField("enumConstantsByName");
				field.setAccessible(true);
				Map value = (Map) field.get(this.pojo);
				contains = value.containsKey(name);
			} catch (Exception e) {
				// if above code fails for any reason - do it 'old' way
				String elementAsString = CodeModelHelper.getElementAsString(this.pojo);
				String toCheck = name + "(";
				if(type.equals(String.class)){
					toCheck += "\"";
				}
				contains = elementAsString.contains(toCheck);
			}
			
			if (contains) {
				ENUM_CACHE.put(name, true);
			}
			return contains;
		}
	}
	
	public <T> EnumBuilder withEnums(List<? extends T> names, Class<T> type) {
		pojoCreationCheck();
		//
		
		if (names != null && !names.isEmpty()) {
			for(T name : names) {
				withEnum(name, type);
			}
		}
		return this;
	}



}
