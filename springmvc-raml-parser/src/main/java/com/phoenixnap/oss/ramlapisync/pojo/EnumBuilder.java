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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

/**
 * Builder pattern for Enum generation using jCodeModel. 
 * 
 * @author kurtpa
 * @since 0.10.2
 *
 */
public class EnumBuilder extends AbstractBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(EnumBuilder.class);

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

			// Add to shortcuts
			this.codeModels.put(fullyQualifiedClassName, this.pojo);
			return this;
		} catch (JClassAlreadyExistsException e) {
			// this should never happen, however in this case lets throw the same error
			throw new IllegalStateException(e);
		}
	}


	public EnumBuilder withEnum(String name) {
		pojoCreationCheck();
		name = NamingHelper.cleanNameForJava(name);
		logger.debug("Adding field: " + name + " to " + this.pojo.name());
		if (StringUtils.hasText(name)) {
			this.pojo.enumConstant(name);
		}
		return this;
	}
	
	public EnumBuilder withEnums(List<String> names) {
		pojoCreationCheck();
		if (names != null && !names.isEmpty()) {
			for(String name : names) {
				withEnum(name);
			}
		}
		return this;
	}



}
