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

import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Factory that will map PojoBuilders for specific RAML types
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class PojoBuilderFactory {
	
    protected static final Logger logger = LoggerFactory.getLogger(PojoBuilderFactory.class);
	
	static RamlTypeInterpreter[] SUPPORTED_INTERPRETERS = { new ObjectTypeInterpreter() };
	static RamlTypeInterpreter DEFAULT_INTERPRETER = new StringTypeInterpreter() ;
	
	private static Map<String, RamlTypeInterpreter> interpreters = new LinkedHashMap<>();
	
	static {
		for (RamlTypeInterpreter interpreter : SUPPORTED_INTERPRETERS) {
			for (Class<? extends TypeDeclaration> type : interpreter.getSupportedTypes()) {
				String key = identifyByClass(type);
				if (interpreters.containsKey(key)) {
					logger.warn("Overwriting Interpreter " + interpreters.get(key) + " with " + identifyByClass(interpreter) + " for type " + key);
				}
						
				interpreters.put(key, interpreter);
				logger.info("Adding Interpreter " + identifyByClass(interpreter) + " for type " + key);
			}
		}
	}
	
	private static String identifyByClass(Object obj) {
		if (obj instanceof Class<?>) {
			return ((Class<?>)obj).getSimpleName();
		} else {
			return obj.getClass().getSimpleName();
		}
	}
	
	public static RamlTypeInterpreter getInterpreterForType (TypeDeclaration type) {
		RamlTypeInterpreter interpreter =  interpreters.get(identifyByClass(type));
		if (interpreter == null) {
			logger.error("Missing Interpreter for type " + identifyByClass(type));
		}
		return interpreter;
	}

}
