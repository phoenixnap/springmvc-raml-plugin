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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * Builder pattern for POJO generation using jCodeModel. Provides basic utility methods including extension and
 * getter/setter generation
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class AbstractBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractBuilder.class);
	
	protected PojoGenerationConfig config;

	protected transient LinkedHashMap<String, JDefinedClass> codeModels = new LinkedHashMap<>();
	protected JCodeModel pojoModel = null;
	protected JDefinedClass pojo = null;
	protected JPackage pojoPackage = null;
	

	public AbstractBuilder() {
		// default constructor
		this.config = new PojoGenerationConfig();
	}

	/**
	 * Constructor allowing chaining of JCodeModels
	 * 
	 * @param config The Configuration object which controls generation
	 * @param pojoModel Existing Codemodel to append to
	 * 
	 */
	public AbstractBuilder(PojoGenerationConfig config, JCodeModel pojoModel) {
		this.config = config;
		this.pojoModel = pojoModel;
	}

	public AbstractBuilder extendsClass(String className) {
		pojoCreationCheck();
		if (this.pojo.name().equals(className)) {
			throw new IllegalStateException("A class cannot extend itself");
		}
		this.pojo._extends(CodeModelHelper.findFirstClassBySimpleName(pojoModel, className));
		return this;
	}

	public AbstractBuilder extendsClass(AbstractBuilder classBuilder) {
		if (classBuilder.pojo == null) {
			throw new IllegalStateException("Supplied builder does not contain a class");
		}
		return extendsClass(classBuilder.pojo.name());
	}

	protected void implementsSerializable() {
		// Implement Serializable
		this.pojo._implements(Serializable.class);

		// Add constant serializable id
		this.pojo.field(JMod.STATIC | JMod.FINAL, this.pojoModel.LONG, "serialVersionUID",
				JExpr.lit(new Random(System.currentTimeMillis()).nextLong()));
	}

	public AbstractBuilder withPackage(String pojoPackage) {
		if (this.pojoPackage != null) {
			throw new IllegalStateException("Pojo Package already created");
		}
		if (this.pojoModel == null) {
			this.pojoModel = new JCodeModel();
		}

		this.pojoPackage = this.pojoModel._package(pojoPackage);

		return this;
	}

	public AbstractBuilder withClassComment(String classComment) {
		pojoCreationCheck();
		JDocComment javadoc = this.pojo.javadoc();
		// javadoc.add
		javadoc.add(toJavaComment(classComment));
		javadoc.add("\n\nGenerated using springmvc-raml-plugin on "
				+ new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
		return this;
	}
	
	public JCodeModel getCodeModel() {
		return this.pojoModel;
	}

	public JClass getPojo() {
		return this.pojo;
	}


	/**
	 * Convenience method to check if the pojo has been created before applying any operators to it
	 */
	protected void pojoCreationCheck() {
		if (this.pojo == null) {
			throw new IllegalStateException("Class not created");
		}
	}

	protected String toJavaComment(String comment) {
		return NamingHelper.cleanForJavadoc(comment);
	}

	protected String toJavaName(String name) {
		return NamingHelper.cleanNameForJava(name);
	}

	protected JClass resolveType(String type) {
		//first try exact match
		JDefinedClass jDefinedClass = pojoModel._getClass(type);
		if (jDefinedClass != null) {
			return jDefinedClass;
		}
		if (type.contains(".")) {
			int lastIndexOf = type.lastIndexOf(".");
			type.substring(lastIndexOf+1);
		}
		return CodeModelHelper.findFirstClassBySimpleName(pojoModel, type);
	}
	

    

}
