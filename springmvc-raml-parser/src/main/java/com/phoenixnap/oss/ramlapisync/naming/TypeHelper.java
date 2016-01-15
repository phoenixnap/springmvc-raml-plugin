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
package com.phoenixnap.oss.ramlapisync.naming;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Simple class containing utility methods for dealing with Java Generic and generic Types
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class TypeHelper {

	/**
	 * Attempts to infer the type in the generic part of the declaration of the type
	 * @param param The parameter to inspect
	 * @return The Class in the generic portrion of the typ
	 */
	public static Type getGenericType(Parameter param) {
		Type parameterizedType = param.getParameterizedType();
		if (parameterizedType instanceof ParameterizedType) {
			parameterizedType = ((ParameterizedType) parameterizedType).getRawType();
		}
		return parameterizedType;
	}

	/**
	 * Attempts to infer the type in the generic part of the declaration of the type
	 * @param clazz The type to inspect
	 * @return The Class in the generic portrion of the type or null if unavailable
	 */
	public static Type inferGenericType(Type clazz) {
		if (clazz != null && clazz instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) clazz;
			return pType.getActualTypeArguments()[0];
		}
		return null;
	}

}
