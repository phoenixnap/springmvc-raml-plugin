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

import org.jsonschema2pojo.GenerationConfig;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;

/**
 * Builder pattern for POJO generation using jCodeModel. Provides basic utility methods including extension and
 * getter/setter generation
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class PojoGenerationConfig {
	
	private String basePackage;
	private String pojoPackage;
	
	//If set to true, Integers will be generated as longs
	private boolean useLongIntegers = true;
	
	private boolean generateJSR303Annotations = false;
	
	private boolean generateHashcodeEqualsToString = true;
	
	private boolean useCommonsLang3 = false;
	
	//If set to true, doubles will be generated as BigDecimals
	private boolean useBigDecimals = false;
	
	//If set to true, longs will be generated as BigIntegers
	private boolean useBigIntegers = false;
		
		
	

	public String getPojoPackage() {
		return pojoPackage;
	}
	public String getBasePackage() {
		return basePackage;
	}

	public boolean isUseLongIntegers() {
		return useLongIntegers;
	}

	public boolean isGenerateHashcodeEqualsToString() {
		return generateHashcodeEqualsToString;
	}

	public boolean isUseCommonsLang3() {
		return useCommonsLang3;
	}
	
	public boolean isGenerateJSR303Annotations() {
		return generateJSR303Annotations;
	}
	
	public boolean isUseBigDecimals() {
		return useBigDecimals;
	}
	public boolean isUseBigIntegers() {
		return useBigIntegers;
	}
	
	public PojoGenerationConfig withPackage(String basePackage, String pojoSubPackage) {
		this.basePackage = basePackage;
		if (pojoSubPackage != null) {
			this.pojoPackage = basePackage + pojoSubPackage;
		} else {
			this.pojoPackage = basePackage + NamingHelper.getDefaultModelPackage();
		}
		
		return this;
	}
	
	public PojoGenerationConfig withLongIntegers(boolean longIntegers) {
		this.useLongIntegers = longIntegers;
		return this;
	}
	
	public PojoGenerationConfig withCommonsLang3(boolean useCommonsLang3) {
		this.useCommonsLang3 = useCommonsLang3;
		return this;
	}
	
	public PojoGenerationConfig withHashcodeEqualsToString(boolean generateHashcodeEqualsToString) {
		this.generateHashcodeEqualsToString = generateHashcodeEqualsToString;
		return this;
	}
	
	public PojoGenerationConfig withJSR303Annotations(boolean generateJSR303Annotations) {
		this.generateJSR303Annotations = generateJSR303Annotations;
		return this;
	}
	
	public PojoGenerationConfig withBigDecimals(boolean useBigDecimals) {
		this.useBigDecimals = useBigDecimals;
		return this;
	}
	
	public PojoGenerationConfig withBigIntegers(boolean useBigIntegers) {
		this.useBigIntegers = useBigIntegers;
		return this;
	}
	
	/**
	 * Applies a JSONSchema2Pojo config to this config
	 * 
	 * @param generationConfig the config from which values will be pulled
	 */
	public void apply(GenerationConfig generationConfig) {
		this
		.withLongIntegers(generationConfig.isUseLongIntegers())
		.withCommonsLang3(generationConfig.isUseCommonsLang3())
		.withBigDecimals(generationConfig.isUseBigDecimals())
		.withBigIntegers(generationConfig.isUseBigIntegers())
		.withJSR303Annotations(generationConfig.isIncludeJsr303Annotations())
		.withHashcodeEqualsToString((generationConfig.isIncludeHashcodeAndEquals() && generationConfig.isIncludeToString()));
	}
	

}
