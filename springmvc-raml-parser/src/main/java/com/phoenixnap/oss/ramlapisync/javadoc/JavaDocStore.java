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
package com.phoenixnap.oss.ramlapisync.javadoc;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple wrapper around the map providing uniform saving and retrieval keys structures
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class JavaDocStore {

	private Map<String, JavaDocEntry> javaDocStore = new LinkedHashMap<>();

	public JavaDocStore() {
	}

	/**
	 * Merges a Java Doc Store with this one. Only keys with higher semantic content will be absorbed into the store
	 * @param javaDocStore The JavaDocStore that will be used to extract data from and merge with ours. Preference will be given to current in case of equal semantic value
	 */
	public void merge(JavaDocStore javaDocStore) {
		if (javaDocStore != null) {
			for (Entry<String, JavaDocEntry> entry : javaDocStore.javaDocStore.entrySet()) {
				putValue(entry.getKey(), entry.getValue());
			}
		}
	}

	private String getStringConvention(Class<?> clazz) {
		return "@class";
	}

	public void setJavaDoc(Class<?> clazz, String javaDoc) {
		setClassJavaDoc(javaDoc);
	}

	public void setClassJavaDoc(String javaDoc) {
		putValue(getStringConvention(null), javaDoc);
	}

	public void setJavaDoc(Method method, String javaDoc) {
		setJavaDoc(method.getName(), method.getParameterCount(), javaDoc);
	}

	public void setJavaDoc(String fieldName, String javaDoc) {
		putValue("@field" + fieldName, javaDoc);
	}

	public void setJavaDoc(String methodName, int methodParameterCount, String javaDoc) {

		putValue(methodName + methodParameterCount, javaDoc);
	}

	public JavaDocEntry getJavaDoc(Method method) {
		return getJavaDoc(method.getName(), method.getParameterCount());
	}

	public JavaDocEntry getJavaDoc(String methodName, int parameterCount) {
		return javaDocStore.get(methodName + parameterCount);
	}

	public JavaDocEntry getJavaDoc(String fieldName) {
		return javaDocStore.get("@field" + fieldName);
	}

	private void putValue(String key, JavaDocEntry entry) {
		JavaDocEntry existingEntry = javaDocStore.get(key);
		if (existingEntry == null) {
			javaDocStore.put(key, entry);
		} else {
			existingEntry.merge(entry);
		}
	}

	private void putValue(String key, String rawEntry) {
		putValue(key, new JavaDocEntry(rawEntry));
	}

	public String getJavaDocComment(Class<?> clazz) {
		JavaDocEntry docEntry = javaDocStore.get(getStringConvention(clazz));
		if (docEntry != null) {
			return docEntry.getComment();
		} else {
			return null;
		}
	}

	public String toString() {
		String out = "";
		for (Entry<String, JavaDocEntry> entry : javaDocStore.entrySet()) {
			out += "-" + entry.getKey() + " : " + entry.getValue() + "\n";
		}
		return out;
	}

}
