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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Union types.
 *
 * @author rahul
 * @since 0.10.6
 */
public class UnionTypeInterpreter extends BaseTypeInterpreter {

	protected static final Logger logger = LoggerFactory.getLogger(UnionTypeInterpreter.class);

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(UnionTypeDeclaration.class);
	}

	private String getClassName(TypeDeclaration type) {
		UnionTypeDeclaration objectType = (UnionTypeDeclaration) type;
		String name = StringUtils.capitalize(objectType.name());

		// For mime types we need to take the type not the name
		try {
			MimeType.valueOf(name);
			name = objectType.type();
		} catch (Exception ex) {
			// not a valid mimetype do nothing
			logger.debug("mime: " + name);
		}
		return name;
	}

	private TypeDeclaration getParent(UnionTypeDeclaration objectType, String typeName, RamlRoot document) {
		Map<String, RamlDataType> types = document.getTypes();

		TypeDeclaration parent = null;

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
		return parent;
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, boolean property) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		typeCheck(type);

		String name = getClassName(type);

		UnionTypeDeclaration objectType = (UnionTypeDeclaration) type;
		String typeName = objectType.type();

		// Lets check if we've already handled this class before.
		if (builderModel != null) {
			JClass searchedClass = builderModel._getClass(Config.getPojoPackage() + "." + name);
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

		PojoBuilder builder = new PojoBuilder(builderModel, name);
		result.setBuilder(builder);

		TypeDeclaration parent = getParent(objectType, typeName, document);

		if (parent != null && !(parent.name().equals(name))) { // add cyclic
																// dependency
																// check
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(parent).interpret(document, parent,
					builderModel, false);
			String childType = childResult.getResolvedClassOrBuiltOrObject().name();

			builder.extendsClass(childType);
		}

		for (TypeDeclaration objectProperty : objectType.of()) {
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(objectProperty).interpret(document,
					objectProperty, builderModel, true);

			String childType = childResult.getResolvedClassOrBuiltOrObject().fullName();
			builder.withField(objectProperty.name(), childType, RamlTypeHelper.getDescription(objectProperty), childResult.getValidations(),
					objectProperty);
		}
		// Add a constructor with all fields
		builder.withCompleteConstructor();

		// Add overriden hashCode(), equals() and toString() methods
		builder.withOverridenMethods(Collections.emptyList());

		return result;

	}

}
