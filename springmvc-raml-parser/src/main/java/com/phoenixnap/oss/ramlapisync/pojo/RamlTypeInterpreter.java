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

import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Builder pattern for POJO generation using jCodeModel. Provides basic utility methods including extension and
 * getter/setter generation
 * 
 * @author kurtpa
 * @since 0.10.0
 *
 */
public interface RamlTypeInterpreter {
	
	/**
	 * The supported TypeDeclarations which this Interpreter can process
	 * 
	 * @return Set of TypeDeclarations
	 */
	Set<Class<? extends TypeDeclaration>> getSupportedTypes();
	
	/**
	 * Interprets the type declaration and returns a RamlInterpretationResult
	 * 
	 * @param document The RAML document being parsed
	 * @param type The particular node/type being interpreted
	 * @param builderModel The JCodeModel where code is being stored
	 * @param config Configuration relating to the generation of code
	 * @param property When parsing properties inside a type this should be flagged to true
	 * @return a {@link RamlInterpretationResult}
	 */
	RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, PojoGenerationConfig config, boolean property);

}
