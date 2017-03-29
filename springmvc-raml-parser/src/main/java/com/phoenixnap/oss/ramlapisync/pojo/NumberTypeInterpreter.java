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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Object types.
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public class NumberTypeInterpreter extends BaseTypeInterpreter {

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		return Collections.singleton(NumberTypeDeclaration.class);
	}


	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, PojoGenerationConfig config) {
		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		String resolvedType = String.class.getSimpleName();
		typeCheck(type);
		if (type instanceof NumberTypeDeclaration) {
			NumberTypeDeclaration numberType = (NumberTypeDeclaration) type;
			String format = numberType.format();
			RamlTypeValidations validations = result.getValidations();
			validations.withMinMax(numberType.minimum(), numberType.maximum());
			resolvedType = SchemaHelper.mapSimpleType(RamlParamType.NUMBER, format).getSimpleName();
			
			if (!StringUtils.hasText(format)) {
				//format not supplied. Defaulting to long if it's integer since it's safer
				if (type instanceof IntegerTypeDeclaration) {
					resolvedType = Long.class.getSimpleName();
				} else {
					resolvedType = Double.class.getSimpleName();
				}
				 
			} else {
				resolvedType = SchemaHelper.mapSimpleType(RamlParamType.NUMBER, format).getSimpleName();
			}
		}
		
		if (resolvedType.equals(Double.class.getSimpleName()) && config.isUseBigDecimals()) {
			resolvedType = BigDecimal.class.getName();
		}
		if (resolvedType.equals(Long.class.getSimpleName()) && config.isUseBigIntegers()) {
			resolvedType = BigInteger.class.getName();
		}
		
		result.setResolvedClass(CodeModelHelper.findFirstClassBySimpleName(builderModel, resolvedType));
		return result;
	}

}
