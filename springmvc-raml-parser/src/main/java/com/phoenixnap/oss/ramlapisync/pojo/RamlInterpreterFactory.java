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
public class RamlInterpreterFactory {

	protected static final Logger logger = LoggerFactory.getLogger(RamlInterpreterFactory.class);
	
	static RamlTypeInterpreter DEFAULT_INTERPRETER = new StringTypeInterpreter();
	static RamlTypeInterpreter[] SUPPORTED_INTERPRETERS = { new ObjectTypeInterpreter(), new BooleanTypeInterpreter(),
			new NullTypeInterpreter(), new NumberTypeInterpreter(), new ArrayTypeInterpreter(),
			new AnyTypeInterpreter(), DEFAULT_INTERPRETER };

	private static Map<Class<? extends TypeDeclaration>, RamlTypeInterpreter> interpreters = new LinkedHashMap<>();
	private static Map<Class<? extends TypeDeclaration>, RamlTypeInterpreter> interpreterCache = new LinkedHashMap<>();

	static {
		for (RamlTypeInterpreter interpreter : SUPPORTED_INTERPRETERS) {
			for (Class<? extends TypeDeclaration> type : interpreter.getSupportedTypes()) {
				if (interpreters.containsKey(type)) {
					logger.warn("Overwriting Interpreter " + interpreters.get(type) + " with "
							+ identifyByClass(interpreter) + " for type " + type.getSimpleName());
				}

				interpreters.put(type, interpreter);
				logger.info("Adding Interpreter " + identifyByClass(interpreter) + " for type " + type.getSimpleName());
			}
		}
	}

	private static String identifyByClass(Object obj) {
		if (obj instanceof Class<?>) {
			return ((Class<?>) obj).getSimpleName();
		} else {
			return obj.getClass().getSimpleName();
		}
	}

	public static RamlTypeInterpreter getInterpreterForType(TypeDeclaration type) {
		RamlTypeInterpreter interpreter = interpreterCache.get(identifyByClass(type));
		if (interpreter != null) {
			return interpreter;
		}

		for (Class<? extends TypeDeclaration> key : interpreters.keySet()) {
			if (key.isAssignableFrom(type.getClass())) {
				interpreter = interpreters.get(key);
				interpreterCache.put(type.getClass(), interpreter);
				break;
			}
		}
		if (interpreter == null) {
			logger.error("Missing Interpreter for type " + identifyByClass(type) + ":" + type.type());
			interpreter = DEFAULT_INTERPRETER;
		}
		return interpreter;
	}

}
