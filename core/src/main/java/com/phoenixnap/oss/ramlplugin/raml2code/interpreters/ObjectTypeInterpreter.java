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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.OverrideNamingLogicWith;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Object types.
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class ObjectTypeInterpreter extends BaseTypeInterpreter {

	protected static final Logger logger = LoggerFactory.getLogger(ObjectTypeInterpreter.class);

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(ObjectTypeDeclaration.class);
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, boolean property) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		typeCheck(type);

		ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
		String name;
		if (objectType.name().endsWith("." + objectType.type())) {
			// when type is e.g. libName.Product
			name = StringUtils.capitalize(objectType.type());
		} else {
			name = StringUtils.capitalize(objectType.name());
		}
		Map<String, RamlDataType> types = document.getTypes();
		String typeName = objectType.type();

		// When we have base arrays with type in the object they differ from
		// Type[] notated types. I'm not sure if this should be handled in the
		// Array or in the ObjectInterpreter...
		if (RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName)) {
			// lets enter type and use that.
			return interpret(document, type.parentTypes().get(0), builderModel, property);
		}

		// When we have base objects we need to use them as type not blindly
		// create them
		if (!RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName) && property) {
			name = typeName;
			if (types.get(name) == null) {
				throw new IllegalStateException("Data type " + name + " can't be found!");
			}
			typeName = types.get(name).getType().type();
		}

		// For mime types we need to take the type not the name
		try {
			MimeType.valueOf(name);
			name = typeName;
			typeName = types.get(name).getType().type();

		} catch (Exception ex) {
			// not a valid mimetype do nothing
			logger.debug("mime: " + name);
		}

		// check if there is a discriminator and child data types
		List<RamlDataType> childTypes = new ArrayList<>();
		if (!StringUtils.isEmpty(objectType.discriminator())) {
			for (RamlDataType ramlDataType : document.getTypes().values()) {
				if (name.equals(ramlDataType.getType().type())) {
					childTypes.add(ramlDataType);
				}
			}
		}

		// Lets check if we've already handled this class before.
		if (builderModel != null) {
			JClass searchedClass = builderModel._getClass(Config.getPojoPackage() + "." + NamingHelper.convertToClassName(name));
			if (searchedClass != null) {
				// we've already handled this pojo in the model, no need to
				// re-interpret
				result.setCodeModel(builderModel);
				result.setResolvedClass(searchedClass);
				return result;
			}
		} else {
			builderModel = new JCodeModel();
			result.setCodeModel(builderModel);
		}

		PojoBuilder builder = new PojoBuilder(builderModel, NamingHelper.convertToClassName(name));
		result.setBuilder(builder);
		TypeDeclaration parent = null;

		// lets handle extensions first
		if (!RamlTypeHelper.isBaseObject(typeName)) {
			parent = types.get(typeName).getType();
		} else if (objectType.parentTypes() != null && objectType.parentTypes().size() > 0) {
			TypeDeclaration tempParent = objectType.parentTypes().get(0); // java
																			// doesnt
																			// support
																			// multiple
																			// parents
																			// take
																			// first;
			if (!RamlTypeHelper.isBaseObject(tempParent.name())) {
				parent = types.get(tempParent.name()).getType();
			}
		} else {
			parent = null;
		}

		if (parent != null && !(parent.name().equalsIgnoreCase(name))) { // add
																			// cyclic
																			// dependency
																			// check
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(parent).interpret(document, parent,
					builderModel, false);
			String childType = childResult.getResolvedClassOrBuiltOrObject().name();
			builder.extendsClass(childType);
		}

		List<String> excludeFieldsFromToString = new ArrayList<>();
		for (TypeDeclaration objectProperty : objectType.properties()) {

			String fieldName = null;
			if (Config.getOverrideNamingLogicWith() == OverrideNamingLogicWith.DISPLAY_NAME && objectProperty.displayName() != null) {
				fieldName = NamingHelper.getParameterName(objectProperty.displayName().value());
			} else if (Config.getOverrideNamingLogicWith() == OverrideNamingLogicWith.ANNOTATION) {
				for (AnnotationRef annotation : objectProperty.annotations()) {
					if ("(javaName)".equals(annotation.name())) {
						fieldName = String.valueOf(annotation.structuredValue().value());
						break;
					}
				}
			}
			if (StringUtils.isEmpty(fieldName)) {
				fieldName = NamingHelper.getParameterName(objectProperty.name());
			}

			List<AnnotationRef> annotations = objectProperty.annotations();
			for (AnnotationRef annotation : annotations) {
				if ("(isSensitive)".equals(annotation.name()) && Boolean.TRUE.equals(annotation.structuredValue().value())) {
					excludeFieldsFromToString.add(fieldName);
				}
			}
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(objectProperty).interpret(document,
					objectProperty, builderModel, true);
			String childType = childResult.getResolvedClassOrBuiltOrObject().fullName();
			builder.withField(fieldName, childType, RamlTypeHelper.getDescription(objectProperty), childResult.getValidations(),
					objectProperty);

		}

		// Add a constructor with all fields
		builder.withCompleteConstructor();

		// Add overriden hashCode(), equals() and toString() methods
		builder.withOverridenMethods(excludeFieldsFromToString);

		if (!childTypes.isEmpty()) {
			// Add @JsonTypeInfo and @JsonSubTypes to support discriminator
			builder.withJsonDiscriminator(childTypes, objectType.discriminator());
		}

		return result;
	}

}
