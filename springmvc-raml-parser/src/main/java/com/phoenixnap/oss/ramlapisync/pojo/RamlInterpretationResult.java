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

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * The result of an interpretation of a type declaration
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class RamlInterpretationResult {
	
	/**
	 * The Object responsible for any generated POJOs following this interpretation
	 */
	private PojoBuilder builder;
	
	/**
	 * The Code model used to store generated code
	 * 
	 */
	private JCodeModel codeModel;
	
	/**
	 * The class linked to in cases where generation was completed previously
	 */
	private JClass resolvedClass;
	
	public PojoBuilder getBuilder() {
		return builder;
	}
	public void setBuilder(PojoBuilder builder) {
		this.builder = builder;
	}
	public JCodeModel getCodeModel() {
		return codeModel;
	}
	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}
	public JClass getResolvedClass() {
		return resolvedClass;
	}
	public void setResolvedClass(JClass resolvedClass) {
		this.resolvedClass = resolvedClass;
	}
	
	public JClass getResolvedClassOrBuiltOrObject() {
		if (getResolvedClass() != null) {
			return getResolvedClass();
		} else if (getBuilder() != null) {
			return getBuilder().getPojo();
		} else {
			return codeModel.ref(Object.class);
		}
	}

}
