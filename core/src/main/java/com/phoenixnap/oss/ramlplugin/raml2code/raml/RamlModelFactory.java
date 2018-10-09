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
package com.phoenixnap.oss.ramlplugin.raml2code.raml;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.phoenixnap.oss.ramlplugin.raml2code.data.RamlFormParameter;

/**
 * Abstract Representation of a Raml Factory
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public interface RamlModelFactory {

	RamlRoot buildRamlRoot(String ramlFileUrl);

	RamlResource createRamlResource(Object resource);

	RamlAction createRamlAction(Object action);

	RamlDocumentationItem createRamlDocumentationItem(Object documentationItem);

	RamlResponse createRamlResponse(Object response);

	RamlMimeType createRamlMimeType(Object mimeType);

	RamlHeader createRamlHeader(Object haeder);

	RamlQueryParameter createRamlQueryParameter(Object queryParameter);

	RamlFormParameter createRamlFormParameter(Object formParameter);

	List<RamlFormParameter> createRamlFormParameters(List<? extends Object> formParameters);

	List<RamlSecurityReference> createRamlSecurityReferences(List<? extends Object> securityReferences);

	RamlSecurityReference createRamlSecurityReference(Object securityReference);

	RamlParamType createRamlParamType(Object paramType);

	default <SK, TK, SV, TV> Map<TK, TV> transformToUnmodifiableMap(Collection<SV> source, Map<TK, TV> target,
			Function<SV, TV> valueTransformer, Function<SV, TK> keyTransformer) {
		if (source == null) {
			target.clear();
		} else if (target.size() != source.size()) {
			target.clear();
			for (SV value : source) {
				TV targetValue = valueTransformer.apply(value);
				TK targetKey = keyTransformer.apply(value);
				target.put(targetKey, targetValue);
			}
		}
		return Collections.unmodifiableMap(target);
	}

	default <T> T identity(T object) {
		return object;
	}

	List<RamlSecurityScheme> createRamlSecuritySchemes(List<? extends Object> securitySchemes);

	RamlSecurityScheme createRamlSecurityScheme(Object securityReference);

}
