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
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Object types.
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class StringTypeInterpreter extends BaseTypeInterpreter {

	private static final String DEFAULT_ENUM_NAME = "DefaultEnum";

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(StringTypeDeclaration.class);
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, boolean property) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());

		if (type instanceof StringTypeDeclaration) {
			StringTypeDeclaration stringType = (StringTypeDeclaration) type;
			// do stringy stuff - enums and stuff.
			RamlTypeValidations validations = result.getValidations();
			validations.withPattern(stringType.pattern());
			validations.withLenghts(stringType.minLength(), stringType.maxLength());

			// Create and handle Enums here
			if (stringType.enumValues() != null && !stringType.enumValues().isEmpty()) {
				// We have an enum. we need to create it and set it
				String enumName = stringType.type();
				if ("string".equalsIgnoreCase(enumName)) {
					enumName = stringType.name();
				}
				if ("string".equalsIgnoreCase(enumName)) {
					enumName = DEFAULT_ENUM_NAME;
				}
				EnumBuilder builder = new EnumBuilder(builderModel, enumName);
				builder.withEnums(stringType.enumValues(), String.class);
				result.setBuilder(builder);
				result.setCodeModel(builderModel);
			}

		}
		if (result.getBuilder() == null) {
			result.setResolvedClass(CodeModelHelper.findFirstClassBySimpleName(builderModel, "java.lang.String"));
		}

		return result;
	}

}
