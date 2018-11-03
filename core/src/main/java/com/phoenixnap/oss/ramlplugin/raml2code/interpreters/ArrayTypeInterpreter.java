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
import java.util.List;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
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
public class ArrayTypeInterpreter extends BaseTypeInterpreter {

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(ArrayTypeDeclaration.class);
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, boolean property) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());

		typeCheck(type);
		if (type instanceof ArrayTypeDeclaration) {
			ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) type;

			RamlTypeValidations validations = result.getValidations();
			validations.withLenghts(arrayType.minItems(), arrayType.maxItems());

			// Lets check if we've already handled this class before.
			if (builderModel != null) {
				String arrayItem = arrayType.items().name();
				if (!StringUtils.isEmpty(arrayItem) && arrayItem.endsWith("[]")) {
					arrayItem = arrayItem.substring(0, arrayItem.length() - 2);
				}
				JClass searchedClass = CodeModelHelper.findFirstClassBySimpleName(builderModel, arrayItem);
				if (!searchedClass.getClass().getSimpleName().contains("JDirectClass")) { // WTF
																							// can't
																							// we
																							// use
																							// this
																							// dude
																							// pff
					// we've already handled this pojo in the model, no need to
					// re-interpret
					JClass collection = resolveCollectionClass(arrayType, searchedClass, builderModel);
					result.setCodeModel(builderModel);
					result.setResolvedClass(collection);
					return result;
				}
			} else {
				builderModel = new JCodeModel();
				result.setCodeModel(builderModel);
			}
			TypeDeclaration arrayContentsType = arrayType.items();

			// Lets process the array base class first
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(arrayContentsType).interpret(document,
					arrayContentsType, builderModel, false);
			JClass collection = resolveCollectionClass(arrayType, childResult.getResolvedClassOrBuiltOrObject(), builderModel);
			result.setResolvedClass(collection);
		}

		return result;
	}

	private JClass resolveCollectionClass(ArrayTypeDeclaration arrayType, JClass resolvedClass, JCodeModel builderModel) {
		Class<?> container = List.class;
		if (arrayType.uniqueItems() != null && arrayType.uniqueItems()) {
			container = Set.class;
		}
		return builderModel.ref(container).narrow(resolvedClass);
	}

}
