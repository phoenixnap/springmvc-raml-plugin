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

import java.awt.List;
import java.util.Collections;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
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
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, PojoGenerationConfig config) {
		RamlInterpretationResult result = new RamlInterpretationResult();
		
		typeCheck(type);		
		if (type instanceof ArrayTypeDeclaration) {
			ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) type;
			
			//Lets check if we've already handled this class before.
			if (builderModel != null) {
				JClass searchedClass = CodeModelHelper.findFirstClassBySimpleName(builderModel, arrayType.items().type());
				if (!searchedClass.getClass().getSimpleName().contains("JDirectClass")) { //WTF can't we use this dude pff
					//we've already handled this pojo in the model, no need to re-interpret
					result.setCodeModel(builderModel);
					result.setResolvedClass(searchedClass);
					return result;
				}
			} else {
				builderModel = new JCodeModel();
				result.setCodeModel(builderModel);
			}
			
			//Lets process the array base class first
			RamlInterpretationResult childResult = PojoBuilderFactory.getInterpreterForType(arrayType.items()).interpret(document, arrayType.items(), builderModel, config);
			Class<?> container = List.class;
			if (arrayType.uniqueItems() != null && arrayType.uniqueItems() ) {
				container = Set.class;
			}
			JClass collection = builderModel.ref(container).narrow(childResult.getResolvedClassOrBuiltOrObject());
			result.setResolvedClass(collection);
		}
		
		return result;
	}

}
