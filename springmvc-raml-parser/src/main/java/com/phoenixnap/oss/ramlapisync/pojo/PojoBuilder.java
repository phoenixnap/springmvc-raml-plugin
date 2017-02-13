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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * Builder pattern for POJO generation using jCodeModel. Provides basic utility methods including extension and
 * getter/setter generation
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class PojoBuilder {

	private transient LinkedHashMap<String, JDefinedClass> codeModels = new LinkedHashMap<>();
	private JCodeModel pojoModel = null;
	private JDefinedClass pojo = null;
	private JPackage pojoPackage = null;

	public PojoBuilder() {
		// default constructor
	}

	/**
	 * Constructor
	 * 
	 * @param pojoPackage The Package used to create POJOs
	 * @param className Class to be created
	 */

	public PojoBuilder(String pojoPackage, String className) {
		this(null, pojoPackage, className);
	}

	/**
	 * Constructor allowing chaining of JCodeModels
	 * 
	 * @param pojoModel Existing Codemodel to append to
	 * @param pojoPackage The Package used to create POJOs
	 * @param className Class to be created
	 */
	public PojoBuilder(JCodeModel pojoModel, String pojoPackage, String className) {
		this.pojoModel = pojoModel;
		withName(pojoPackage, className);
	}

	public PojoBuilder extendsClass(String className) {
		pojoCreationCheck();
		this.pojo._extends(CodeModelHelper.findFirstClassBySimpleName(pojoModel, className));
		return this;
	}

	public PojoBuilder extendsClass(PojoBuilder classBuilder) {
		if (classBuilder.pojo == null) {
			throw new IllegalStateException("Supplied builder does not contain a class");
		}
		return extendsClass(classBuilder.pojo.name());
	}

	/**
	 * Sets this Pojo's name
	 * 
	 * @param pojoPackage The Package used to create POJO
	 * @param className Class to be created
	 * @return
	 */
	public PojoBuilder withName(String pojoPackage, String className) {
		final String fullyQualifiedClassName = pojoPackage + "." + className;
		// Initiate package if necessary
		if (this.pojoPackage == null) {
			withPackage(pojoPackage);
		}
		// Builders should only have 1 active pojo under their responsibility
		if (this.pojo != null) {
			throw new IllegalStateException("Class already created");
		}

		try {
			// create the class
			this.pojo = this.pojoModel._class(fullyQualifiedClassName);

			// Create default constructor
			this.pojo.constructor(JMod.PUBLIC).javadoc().add("Creates a new " + className + ".");

			// Implement Serializable
			this.pojo._implements(Serializable.class);

			// Add constant serializable id
			this.pojo.field(JMod.STATIC | JMod.FINAL, Long.class, "serialVersionUID",
					JExpr.lit(new Random(System.currentTimeMillis()).nextLong()));

			// Add to shortcuts
			this.codeModels.put(fullyQualifiedClassName, this.pojo);
			return this;
		} catch (JClassAlreadyExistsException e) {
			// this should never happen, however in this case lets throw the same error
			throw new IllegalStateException(e);
		}
	}

	public PojoBuilder withPackage(String pojoPackage) {
		if (this.pojoPackage != null) {
			throw new IllegalStateException("Pojo Package already created");
		}
		if (this.pojoModel == null) {
			this.pojoModel = new JCodeModel();
		}

		this.pojoPackage = this.pojoModel._package(pojoPackage);

		return this;
	}

	public PojoBuilder withClassComment(String classComment) {
		pojoCreationCheck();
		JDocComment javadoc = this.pojo.javadoc();
		// javadoc.add
		javadoc.add(toJavaComment(classComment));
		javadoc.add("\n\nGenerated using springmvc-raml-plugin on " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
		return this;
	}

	public PojoBuilder withField(String name, String type, String comment) {
		pojoCreationCheck();
		
		JType resolvedType = resolveType(type);

		// Add private variable
		JFieldVar field = this.pojo.field(JMod.PRIVATE, resolvedType, toJavaName(name));
		if (StringUtils.hasText(comment)) {
			field.javadoc().add(comment);
		}
		String fieldName = NamingHelper.convertToClassName(name);

		// Add get method
		JMethod getter = this.pojo.method(JMod.PUBLIC, field.type(), "get" + fieldName);
		getter.body()._return(field);
		getter.javadoc().add("Returns the " + name + ".");
		getter.javadoc().addReturn().add(field.name());

		// Add set method
		JMethod setter = this.pojo.method(JMod.PUBLIC, this.pojoModel.VOID, "set" + fieldName);
		setter.param(field.type(), field.name());
		setter.body().assign(JExpr._this().ref(field.name()), JExpr.ref(field.name()));
		setter.javadoc().add("Set the " + name + ".");
		setter.javadoc().addParam(field.name()).add("the new " + field.name());

		return this;
	}

	public JCodeModel getCodeModel() {
		return this.pojoModel;
	}
	
	public JClass getPojo() {
		return this.pojo;
	}

	/**
	 * Adds a constructor with all the fields in the POJO
	 * 
	 * @return
	 */
	public PojoBuilder withCompleteConstructor() {
		pojoCreationCheck();
		
		// Create default constructor
		JMethod constructor = this.pojo.constructor(JMod.PUBLIC);
		Collection<JFieldVar> fieldsToAdd = getFieldsToAdd(null, this.pojo);
		for (JFieldVar field : fieldsToAdd) {
			addFieldToConstructor(field, constructor);
		}
		constructor.javadoc().add("Creates a new " + this.pojo.name() + ".");

		return this;
	}
	
	private Collection<JFieldVar> getFieldsToAdd(Collection<JFieldVar> fields, JClass pojo) {
		Collection<JFieldVar> tFields = fields;
		if (fields == null) {
			tFields = new LinkedHashSet<>();
		}
		
		JClass parent = pojo._extends();
		if (!parent.name().equals(Object.class.getSimpleName())) {
			CodeModelHelper.findFirstClassBySimpleName(this.pojoModel, parent.name());
			tFields.addAll(getFieldsToAdd(tFields, parent));
		}
		
		if (pojo instanceof JDefinedClass) {
			tFields.addAll(((JDefinedClass)pojo).fields().values());
		}
		
		return tFields;		
	}
	private void addFieldToConstructor(JFieldVar field, JMethod constructor) {
		if (((field.mods().getValue() & (JMod.STATIC | JMod.TRANSIENT)) == 0)) { //Ignore static variables
			constructor.param(field.type(), field.name());
			constructor.body().assign(JExpr._this().ref(field.name()), JExpr.ref(field.name()));
		}
	}

	/**
	 * Convenience method to check if the pojo has been created before applying any operators to it
	 */
	private void pojoCreationCheck() {
		if (this.pojo == null) {
			throw new IllegalStateException("Class not created");
		}
	}

	private String toJavaComment(String comment) {
		return comment;
	}

	private String toJavaName(String name) {
		return name;
	}

	private JType resolveType(String type) {
		return CodeModelHelper.findFirstClassBySimpleName(pojoModel, type);
	}

	public static void main(String[] args) throws Exception {
		ConsoleAppender console = new ConsoleAppender();
		 //configure the appender
		  String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		  console.setLayout(new PatternLayout(PATTERN)); 
		  console.setThreshold(Level.DEBUG);
		  console.activateOptions();
		Logger.getRootLogger().addAppender(console);
		PojoBuilderFactory factory = new PojoBuilderFactory();
	 	PojoBuilder pojoBuilder = new PojoBuilder("com.gen.foo", "TestClass")
	 	.withClassComment("Hi there")
	 	.withField("aField", "Object", null)
	 	.withField("oohAnother", "String", "No Comment :p");
	 	
	 	
	 	PojoBuilder anotherBuilder = new PojoBuilder(pojoBuilder.getCodeModel(), "com.gen.foo", "AnotherTestClass")
	 	.withClassComment("Hi there again")
	 	.withField("zomg", "Integer", null)
	 	.withField("zomomg", "String", "No Comment :p");
	 	
	 	PojoBuilder yetAnotherBuilder = new PojoBuilder(pojoBuilder.getCodeModel(), "com.gen.foo", "ChildTestClass")
	 	.extendsClass(pojoBuilder)
	 	.withClassComment("Hi there oh")
	 	.withField("bob", "java.util.List<Integer>", null)
	 	.withField("boblet", "String", "No Comment :p")
	 	.withCompleteConstructor();
	 	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			anotherBuilder.getCodeModel().build(new SingleStreamCodeWriter(bos));
		} catch (IOException e) {
			//do nothing
		}
		System.out.println(bos.toString());

	}
	

}
