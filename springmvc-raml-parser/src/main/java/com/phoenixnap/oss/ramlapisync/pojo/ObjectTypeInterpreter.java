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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
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
public class ObjectTypeInterpreter extends BaseTypeInterpreter {

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(ObjectTypeDeclaration.class);
	}


	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, PojoGenerationConfig config) {
		RamlInterpretationResult result = new RamlInterpretationResult();
		
		typeCheck(type);
		ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
		String name = objectType.type();
		//For top level classes that extend object, we need to take the name not the type
		if (name.equals("object")) {
			name = objectType.name();
		}
		//Lets check if we've already handled this class before.
		if (builderModel != null) {
			JClass searchedClass = CodeModelHelper.findFirstClassBySimpleName(builderModel, name);
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
		
		Map<String, RamlDataType> types = document.getTypes();
		PojoBuilder builder = new PojoBuilder(builderModel, config.getPojoPackage(), name);
		result.setBuilder(builder);
		TypeDeclaration parent = null; 
		//lets handle extensions first
		if (objectType.parentTypes() != null && objectType.parentTypes().size() > 0) {
			parent = objectType.parentTypes().get(0); //java doesnt support multiple parents take first;
			if (parent.type() != null && !parent.type().equalsIgnoreCase(Object.class.getSimpleName())) {
				TypeDeclaration ramlDataType = types.get(parent.type()).getType();
				RamlInterpretationResult childResult = PojoBuilderFactory.getInterpreterForType(ramlDataType).interpret(document, ramlDataType, builderModel, config);
				String childType = childResult.getResolvedClassOrBuiltOrObject().name();
				builder.extendsClass(childType);
			} else {
				parent = null;
			}
		}
		
		
		
		
		for (TypeDeclaration property : objectType.properties()) {
			RamlInterpretationResult childResult = PojoBuilderFactory.getInterpreterForType(property).interpret(document, property, builderModel, config);
			String childType = childResult.getResolvedClassOrBuiltOrObject().name();
			builder.withField(property.name(), childType, RamlTypeHelper.getDescription(property));
		}
		
		//Add a constructor with all fields 
		builder.withCompleteConstructor();
		
		return result;
	}

}
