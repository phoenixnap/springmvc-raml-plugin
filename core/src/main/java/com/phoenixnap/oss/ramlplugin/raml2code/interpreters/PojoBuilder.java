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
package com.phoenixnap.oss.ramlplugin.raml2code.interpreters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * Builder pattern for POJO generation using jCodeModel. Provides basic utility
 * methods including extension and getter/setter generation
 *
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class PojoBuilder extends AbstractBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(PojoBuilder.class);

	/**
	 * Constructor allowing chaining of JCodeModels
	 *
	 * @param pojoModel
	 *            Existing Codemodel to append to
	 * @param className
	 *            Class to be created
	 *
	 */
	public PojoBuilder(JCodeModel pojoModel, String className) {
		super(pojoModel);
		withName(Config.getPojoPackage(), className);
	}

	@Override
	public PojoBuilder extendsClass(String className) {
		pojoCreationCheck();
		if (this.pojo.name().equals(className)) {
			throw new IllegalStateException("A class cannot extend itself");
		}
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
	 * @param pojoPackage
	 *            The Package used to create POJO
	 * @param className
	 *            Class to be created
	 * @return This instance
	 */
	public PojoBuilder withName(String pojoPackage, String className) {
		if (!NamingHelper.isValidJavaClassName(className)) {
			className = NamingHelper.cleanNameForJava(className);
		}

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
			logger.debug("Creating class " + fullyQualifiedClassName);
			this.pojo = this.pojoModel._class(fullyQualifiedClassName);

			// Always add default constructor
			withDefaultConstructor(className);

			// Handle Serialization
			implementsSerializable();

			// Add to shortcuts
			this.codeModels.put(fullyQualifiedClassName, this.pojo);
		} catch (JClassAlreadyExistsException e) {
			// class already exists - reuse it!
			logger.debug("Enum {} already exists. Reusing it!", fullyQualifiedClassName);
			this.pojo = this.pojoModel._getClass(fullyQualifiedClassName);
		}
		return this;
	}

	private void withDefaultConstructor(String className) {
		// Create default constructor
		JMethod constructor = this.pojo.constructor(JMod.PUBLIC);
		constructor.javadoc().add("Creates a new " + className + ".");
		JBlock defaultConstructorBody = constructor.body();
		defaultConstructorBody.invoke("super");
	}

	private JFieldVar parentContainsField(JClass pojo, String name) {
		if (pojo != null && !pojo.fullName().equals(Object.class.getName())) {
			JClass parent = pojo._extends();
			if (parent != null) {
				// Our parent has a parent, lets check if it has it
				JFieldVar parentField = parentContainsField(parent, name);
				if (parentField != null) {
					return parentField;
				} else {
					if (parent instanceof JDefinedClass) {
						return ((JDefinedClass) parent).fields().get(name);
					}
				}
			}
		}

		return null;
	}

	public PojoBuilder withField(String name, String type, String comment, RamlTypeValidations validations,
			TypeDeclaration typeDeclaration) {
		pojoCreationCheck();
		logger.debug("Adding field: " + name + " to " + this.pojo.name());

		JClass resolvedType = resolveType(type);

		try {
			// If this class is a collection (List)- lets add an import
			if (resolvedType.fullName().startsWith(List.class.getName() + "<")) {
				resolvedType = this.pojo.owner().ref(List.class).narrow(resolvedType.getTypeParameters().get(0));
			}
			// If this class is a collection (Set) - lets add an import
			if (resolvedType.fullName().startsWith(Set.class.getName() + "<")) {
				resolvedType = this.pojo.owner().ref(Set.class).narrow(resolvedType.getTypeParameters().get(0));
			}
		} catch (Exception ex) {
			// skip import
		}

		JExpression jExpression = null;
		if (StringUtils.hasText(typeDeclaration.defaultValue())) {
			if (resolvedType.name().equals(Integer.class.getSimpleName())) {
				jExpression = JExpr.lit(Integer.valueOf(typeDeclaration.defaultValue()));
			} else if (resolvedType.name().equals(Boolean.class.getSimpleName())) {
				jExpression = JExpr.lit(Boolean.valueOf(typeDeclaration.defaultValue()));
			} else if (resolvedType.name().equals(Double.class.getSimpleName())) {
				jExpression = JExpr.lit(Double.valueOf(typeDeclaration.defaultValue()));
			} else if (resolvedType.name().equals(Float.class.getSimpleName())) {
				jExpression = JExpr.lit(Float.valueOf(typeDeclaration.defaultValue()));
			} else if (resolvedType.name().equals(Long.class.getSimpleName())) {
				jExpression = JExpr.lit(Long.valueOf(typeDeclaration.defaultValue()));
			} else if (resolvedType.name().equals(BigDecimal.class.getSimpleName())) {
				jExpression = JExpr.direct("new BigDecimal(\"" + typeDeclaration.defaultValue() + "\")");
			} else if (resolvedType.name().equals(String.class.getSimpleName())) {
				jExpression = JExpr.lit(typeDeclaration.defaultValue());
			} else if (type.contains(".") && resolvedType instanceof JDefinedClass
					&& ((JDefinedClass) resolvedType).getClassType().equals(ClassType.ENUM)) {
				jExpression = JExpr.direct(resolvedType.name() + "." + NamingHelper.cleanNameForJavaEnum(typeDeclaration.defaultValue()));
			}
		} else if (resolvedType.fullName().startsWith(List.class.getName() + "<")) {
			JClass narrowedListClass = this.pojoModel.ref(ArrayList.class).narrow(resolvedType.getTypeParameters().get(0));
			jExpression = JExpr._new(narrowedListClass);
		}

		if (resolveType(Collection.class.getName()).isAssignableFrom(resolvedType) && !Config.getPojoConfig().isInitializeCollections()) {
			jExpression = null;
		}

		// lets ignore this if parent contains it and we will use parent's in
		// the constructor
		JFieldVar parentField = parentContainsField(this.pojo, name);
		if (parentField != null) {

			// Add get method
			JMethod getterMethod = generateGetterMethod(parentField, name, jExpression);

			// validation
			if (Config.getPojoConfig().isIncludeJsr303Annotations() && validations != null) {

				validations.annotateFieldJSR303(getterMethod, false);
			}

			return this;
		}

		// Add protected variable
		JFieldVar field = this.pojo.field(JMod.PROTECTED, resolvedType, toJavaName(name), jExpression);

		if (!Objects.equals(typeDeclaration.name(), toJavaName(name))) {
			field.annotate(JsonProperty.class).param("value", typeDeclaration.name());
		}

		if (resolvedType.name().equals(Date.class.getSimpleName())) {
			JAnnotationUse jAnnotationUse = field.annotate(JsonFormat.class);
			String format = null;
			if (typeDeclaration instanceof DateTimeTypeDeclaration) {
				format = ((DateTimeTypeDeclaration) typeDeclaration).format();
			}
			RamlTypeHelper.annotateDateWithPattern(jAnnotationUse, typeDeclaration.type(), format);
		}

		if (StringUtils.hasText(comment)) {
			field.javadoc().add(comment);
		}
		String fieldName = NamingHelper.convertToClassName(name);

		// Add get method
		JMethod getterMethod = generateGetterMethod(field, name, null);
		if (Config.getPojoConfig().isIncludeJsr303Annotations() && validations != null) {

			// check if field is complex object so we can mark it with @Valid
			boolean isPOJO = type.startsWith(this.pojo._package().name() + ".");
			if (!isPOJO && resolvedType.getClass().getName().equals("com.sun.codemodel.JNarrowedClass")
					&& resolvedType.getTypeParameters().size() == 1) {
				JClass typeClass = resolvedType.getTypeParameters().get(0);
				isPOJO = typeClass.fullName().startsWith(this.pojo._package().name() + ".");
			}

			validations.annotateFieldJSR303(getterMethod, isPOJO);
		}

		// Add set method
		JMethod setter = this.pojo.method(JMod.PUBLIC, this.pojoModel.VOID, "set" + fieldName);
		setter.param(field.type(), field.name());
		setter.body().assign(JExpr._this().ref(field.name()), JExpr.ref(field.name()));
		setter.javadoc().add("Set the " + name + ".");
		setter.javadoc().addParam(field.name()).add("the new " + field.name());

		// Add with method
		if (Config.getPojoConfig().isGenerateBuilders()) {
			JMethod wither = this.pojo.method(JMod.PUBLIC, this.pojo, "with" + fieldName);
			wither.param(field.type(), field.name());
			wither.body().assign(JExpr._this().ref(field.name()), JExpr.ref(field.name()))._return(JExpr._this());
			wither.javadoc().add("With the " + name + ".");
			wither.javadoc().addParam(field.name()).add("the new " + field.name());
		}

		return this;
	}

	private JMethod generateGetterMethod(JFieldVar field, String fieldName, JExpression defaultValue) {

		String javaName = NamingHelper.convertToClassName(fieldName);

		// Add get method
		JMethod getter = this.pojo.method(JMod.PUBLIC, field.type(), "get" + javaName);
		if (defaultValue != null) {
			JBlock body = getter.body();
			body._if(field.eq(JExpr._null()))._then()._return(defaultValue);
		}
		getter.body()._return(field);
		getter.javadoc().add("Returns the " + fieldName + ".");
		getter.javadoc().addReturn().add(field.name());

		return getter;
	}

	/**
	 * Adds a constructor with all the fields in the POJO. If no fields are
	 * present it will not create an empty constructor because default
	 * constructor (without fields) is already present.
	 * 
	 * @return This builder instance
	 */
	public PojoBuilder withCompleteConstructor() {
		pojoCreationCheck();

		// first we need to check if there are any fields to add to constructor
		// because default constructor (without fields) is already present
		Map<String, JFieldVar> nonTransientAndNonStaticFields = getNonTransientAndNonStaticFields();

		if (MapUtils.isNotEmpty(nonTransientAndNonStaticFields)) {
			// Create complete constructor
			JMethod constructor = this.pojo.constructor(JMod.PUBLIC);
			Map<String, JVar> superParametersToAdd = getSuperParametersToAdd(this.pojo);
			addSuperConstructorInvocation(constructor, superParametersToAdd);
			constructor.javadoc().add("Creates a new " + this.pojo.name() + ".");

			Iterator<Map.Entry<String, JFieldVar>> iterator = nonTransientAndNonStaticFields.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, JFieldVar> pair = iterator.next();

				constructor.param(pair.getValue().type(), pair.getKey());
				constructor.body().assign(JExpr._this().ref(pair.getKey()), JExpr.ref(pair.getKey()));
			}
		}

		return this;
	}

	private void addSuperConstructorInvocation(JMethod constructor, Map<String, JVar> superParametersToAdd) {
		JBlock constructorBody = constructor.body();
		JInvocation invocation = constructorBody.invoke("super");
		for (JVar arg : superParametersToAdd.values()) {
			JVar param = constructor.param(arg.type(), arg.name());
			invocation.arg(param);
		}

	}

	private Map<String, JVar> getSuperParametersToAdd(JClass pojo) {
		Map<String, JVar> tFields = new LinkedHashMap<>();
		JClass parent = pojo._extends();
		if (!parent.name().equals(Object.class.getSimpleName())) {
			parent = CodeModelHelper.findFirstClassBySimpleName(this.pojoModel, parent.name());
			if (parent instanceof JDefinedClass) {
				JDefinedClass jParent = (JDefinedClass) parent;
				JMethod constructor = null;
				Iterator<JMethod> constructors = jParent.constructors();
				while (constructors.hasNext()) {
					JMethod targetConstructor = constructors.next();
					if (constructor == null || constructor.params().size() < targetConstructor.params().size()) {
						constructor = targetConstructor;
					}
				}
				for (JVar var : constructor.params()) {
					tFields.put(var.name(), var);
				}
			}
		}
		return tFields;
	}

	private Map<String, JFieldVar> getNonTransientAndNonStaticFields() {
		Map<String, JFieldVar> nonStaticNonTransientFields = new LinkedHashMap<>();

		if (pojo instanceof JDefinedClass) {
			Map<String, JFieldVar> fields = ((JDefinedClass) pojo).fields();

			Iterator<Map.Entry<String, JFieldVar>> iterator = fields.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, JFieldVar> pair = iterator.next();

				// If a field is not static or transient
				if ((pair.getValue().mods().getValue() & (JMod.STATIC | JMod.TRANSIENT)) == 0) {
					nonStaticNonTransientFields.put(pair.getKey(), pair.getValue());
				}
			}
		}

		return nonStaticNonTransientFields;
	}

	/**
	 * Generates implementations for hashCode(), equals() and toString() methods
	 * if the plugin has been configured to do so.
	 * 
	 * @param excludeFieldsFromToString
	 *            list of parameters to exclude from toString() method
	 */
	public void withOverridenMethods(List<String> excludeFieldsFromToString) {
		if (Config.getPojoConfig().isIncludeHashcodeAndEquals()) {
			withHashCode();
			withEquals();
		}
		if (Config.getPojoConfig().isIncludeToString()) {
			withToString(excludeFieldsFromToString);
		}
	}

	public void withJsonDiscriminator(List<RamlDataType> childTypes, String discriminator) {
		this.pojo.annotate(JsonTypeInfo.class).param("property", discriminator).param("use", JsonTypeInfo.Id.NAME)
				.param("include", As.EXISTING_PROPERTY).param("visible", true);

		JAnnotationUse param = this.pojo.annotate(JsonSubTypes.class).param("value", discriminator);
		JAnnotationArrayMember jAnnotationArrayMember = param.paramArray("value");
		for (RamlDataType childType : childTypes) {
			String discriminatorValue = childType.getDiscriminatorValue();
			if (StringUtils.isEmpty(discriminatorValue)) {
				// default value for discriminator is the name of the type
				discriminatorValue = childType.getType().name();
			}
			jAnnotationArrayMember.annotate(JsonSubTypes.Type.class).param("value", resolveType(childType.getType().name())).param("name",
					discriminatorValue);
		}
	}

	private void withToString(List<String> excludeFieldsFromToString) {
		pojoCreationCheck();

		JMethod toString = this.pojo.method(JMod.PUBLIC, String.class, "toString");

		Class<?> toStringBuilderClass = org.apache.commons.lang3.builder.ToStringBuilder.class;
		if (!Config.getPojoConfig().isUseCommonsLang3()) {
			toStringBuilderClass = org.apache.commons.lang.builder.ToStringBuilder.class;
		}

		JClass toStringBuilderRef = this.pojo.owner().ref(toStringBuilderClass);

		Map<String, JFieldVar> nonTransientAndNonStaticFields = getNonTransientAndNonStaticFields();
		nonTransientAndNonStaticFields.keySet().removeAll(excludeFieldsFromToString);

		JInvocation toStringBuilderInvocation = appendFieldsToString(nonTransientAndNonStaticFields, toStringBuilderRef);

		toString.body()._return(toStringBuilderInvocation.invoke("toString"));
	}

	private JInvocation appendFieldsToString(Map<String, JFieldVar> nonTransientAndNonStaticFields, JClass toStringBuilderRef) {
		JInvocation invocation = JExpr._new(toStringBuilderRef).arg(JExpr._this());
		Iterator<Map.Entry<String, JFieldVar>> iterator = nonTransientAndNonStaticFields.entrySet().iterator();

		if (!this.pojo._extends().name().equals(Object.class.getSimpleName())) { // If
																					// this
																					// POJO
																					// has
																					// a
																					// superclass,
																					// append
																					// the
																					// superclass
																					// toString()
																					// method.
			invocation = invocation.invoke("appendSuper").arg(JExpr._super().invoke("toString"));
		}

		while (iterator.hasNext()) {
			Map.Entry<String, JFieldVar> pair = iterator.next();
			invocation = invocation.invoke("append").arg(JExpr.lit(pair.getKey())).arg(pair.getValue());
		}

		return invocation;
	}

	// Ada
	private void withHashCode() {
		JMethod hashCode = this.pojo.method(JMod.PUBLIC, int.class, "hashCode");

		Class<?> hashCodeBuilderClass = org.apache.commons.lang3.builder.HashCodeBuilder.class;
		if (!Config.getPojoConfig().isUseCommonsLang3()) {
			hashCodeBuilderClass = org.apache.commons.lang.builder.HashCodeBuilder.class;
		}

		JClass hashCodeBuilderRef = this.pojo.owner().ref(hashCodeBuilderClass);

		JInvocation hashCodeBuilderInvocation = appendFieldsToHashCode(getNonTransientAndNonStaticFields(), hashCodeBuilderRef);

		hashCode.body()._return(hashCodeBuilderInvocation.invoke("toHashCode"));
	}

	private JInvocation appendFieldsToHashCode(Map<String, JFieldVar> nonTransientAndNonStaticFields, JClass hashCodeBuilderRef) {
		JInvocation invocation = JExpr._new(hashCodeBuilderRef);
		Iterator<Map.Entry<String, JFieldVar>> iterator = nonTransientAndNonStaticFields.entrySet().iterator();

		if (!this.pojo._extends().name().equals(Object.class.getSimpleName())) { // If
																					// this
																					// POJO
																					// has
																					// a
																					// superclass,
																					// append
																					// the
																					// superclass
																					// hashCode()
																					// method.
			invocation = invocation.invoke("appendSuper").arg(JExpr._super().invoke("hashCode"));
		}

		while (iterator.hasNext()) {
			Map.Entry<String, JFieldVar> pair = iterator.next();
			invocation = invocation.invoke("append").arg(pair.getValue());
		}

		return invocation;
	}

	private void withEquals() {
		JMethod equals = this.pojo.method(JMod.PUBLIC, boolean.class, "equals");
		JVar otherObject = equals.param(Object.class, "other");

		Class<?> equalsBuilderClass = org.apache.commons.lang3.builder.EqualsBuilder.class;
		if (!Config.getPojoConfig().isUseCommonsLang3()) {
			equalsBuilderClass = org.apache.commons.lang.builder.EqualsBuilder.class;
		}

		JBlock body = equals.body();

		body._if(otherObject.eq(JExpr._null()))._then()._return(JExpr.FALSE);
		body._if(otherObject.eq(JExpr._this()))._then()._return(JExpr.TRUE);
		body._if(JExpr._this().invoke("getClass").ne(otherObject.invoke("getClass")))._then()._return(JExpr.FALSE);

		JVar otherObjectVar = body.decl(this.pojo, "otherObject").init(JExpr.cast(this.pojo, otherObject));

		JClass equalsBuilderRef = this.pojo.owner().ref(equalsBuilderClass);

		JInvocation equalsBuilderInvocation = appendFieldsToEquals(getNonTransientAndNonStaticFields(), otherObjectVar, equalsBuilderRef);

		body._return(equalsBuilderInvocation.invoke("isEquals"));
	}

	private JInvocation appendFieldsToEquals(Map<String, JFieldVar> nonTransientAndNonStaticFields, JVar otherObject,
			JClass equalsBuilderRef) {
		JInvocation invocation = JExpr._new(equalsBuilderRef);
		Iterator<Map.Entry<String, JFieldVar>> iterator = nonTransientAndNonStaticFields.entrySet().iterator();

		if (!this.pojo._extends().name().equals(Object.class.getSimpleName())) {// If
																				// this
																				// POJO
																				// has
																				// a
																				// superclass,
																				// append
																				// the
																				// superclass
																				// equals()
																				// method.
			invocation = invocation.invoke("appendSuper").arg(JExpr._super().invoke("equals").arg(otherObject));
		}

		while (iterator.hasNext()) {
			Map.Entry<String, JFieldVar> pair = iterator.next();
			invocation = invocation.invoke("append").arg(pair.getValue()).arg(otherObject.ref(pair.getKey()));
		}

		return invocation;
	}

}
