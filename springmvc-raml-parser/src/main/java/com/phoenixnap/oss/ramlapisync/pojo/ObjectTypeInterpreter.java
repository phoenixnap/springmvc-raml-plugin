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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

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

	protected static final Logger logger = LoggerFactory.getLogger(ObjectTypeInterpreter.class);

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(ObjectTypeDeclaration.class);
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel,
			PojoGenerationConfig config, boolean property) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		typeCheck(type);

		ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
		String name = StringUtils.capitalize(objectType.name());
		Map<String, RamlDataType> types = document.getTypes();
		String typeName = objectType.type();
		
		//When we have base arrays with type in the object they differ from Type[] notated types. I'm not sure if this should be handled in the Array or in the ObjectInterpreter...
		if(RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName)) {
			//lets enter type and use that.
			return interpret(document, type.parentTypes().get(0), builderModel, config, property);
		}
		
		//When we have base objects we need to use them as type not blindly create them
		if(!RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName) && property) {
			name = typeName;
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
		// Lets check if we've already handled this class before.
		if (builderModel != null) {
			JClass searchedClass = builderModel._getClass(config.getPojoPackage() + "." + name);
			if (searchedClass != null) {
				// we've already handled this pojo in the model, no need to re-interpret
				result.setCodeModel(builderModel);
				result.setResolvedClass(searchedClass);
				return result;
			}
		} else {
			builderModel = new JCodeModel();
			result.setCodeModel(builderModel);
		}

		PojoBuilder builder = new PojoBuilder(config, builderModel, name);
		result.setBuilder(builder);
		TypeDeclaration parent = null;
		
		// lets handle extensions first
		if (!RamlTypeHelper.isBaseObject(typeName)) {
			parent = types.get(typeName).getType();
		} else if (objectType.parentTypes() != null && objectType.parentTypes().size() > 0) {
			TypeDeclaration tempParent = objectType.parentTypes().get(0); // java doesnt support multiple parents take first;
			if (!RamlTypeHelper.isBaseObject(tempParent.name())) {
				parent = types.get(tempParent.name()).getType();
			}
		} else {
			parent = null;
		}
		
		if (parent != null && !(parent.name().equals(name))) { //add cyclic dependency check
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(parent).interpret(document, parent, builderModel, config, false);
			String childType = childResult.getResolvedClassOrBuiltOrObject().name();
			builder.extendsClass(childType);
		}

		for (TypeDeclaration objectProperty : objectType.properties()) {
			RamlInterpretationResult childResult = RamlInterpreterFactory.getInterpreterForType(objectProperty).interpret(
					document, objectProperty, builderModel, config, true);
			String childType = childResult.getResolvedClassOrBuiltOrObject().fullName();
			builder.withField(objectProperty.name(), childType, RamlTypeHelper.getDescription(objectProperty), childResult.getValidations(), objectProperty.defaultValue());
			
		}

		// Add a constructor with all fields
		builder.withCompleteConstructor();

		return result;
	}

}
