/*
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved. 
 */
package com.phoenixnap.oss.ramlapisync.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;

public class JsonSchema2PojoConfigBuilder {

	private Map<String, Object> config = new HashMap<>();

	public JsonSchema2PojoConfigBuilder enable(String attribute) {
		set(attribute, Boolean.TRUE);
		return this;
	}

	public JsonSchema2PojoConfigBuilder disable(String attribute) {
		set(attribute, Boolean.FALSE);
		return this;
	}

	public JsonSchema2PojoConfigBuilder reset(String attribute) {
		assertParameterIsSupported(attribute, null);
		config.remove(attribute);
		return this;
	}

	public JsonSchema2PojoConfigBuilder set(String attribute, Object value) {
		assertParameterIsSupported(attribute, value);
		config.put(attribute, value);
		return this;
	}

	public GenerationConfig build() {
		return (GenerationConfig) Proxy.newProxyInstance(JsonSchema2PojoConfigBuilder.class.getClassLoader(), new Class[] { GenerationConfig.class },
				new MyInvocationHandler(config));
	}

	private final void assertParameterIsSupported(String attribute, Object value) {
		Method method = MethodUtils.getAccessibleMethod(DefaultGenerationConfig.class, attribute);
		if (method == null) {
			throw new IllegalArgumentException("The attribute '" + attribute + "' is unknown)");
		}
		if ((value != null) && (method.getReturnType().isAssignableFrom(value.getClass()))) {
			throw new IllegalArgumentException("The attribute '" + attribute + "' is not a boolean");
		}
	}

	private static final class MyInvocationHandler implements InvocationHandler {

		private final DefaultGenerationConfig defaultConfig = new DefaultGenerationConfig();
		private final Map<String, Object> config;

		MyInvocationHandler(Map<String, Object> config) {
			if (config != null) {
				this.config = config;
			} else {
				this.config = new HashMap<>();
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object value = config.get(method.getName());
			if (value != null) {
				return value;
			}
			return method.invoke(defaultConfig, args);
		}

	}

}
