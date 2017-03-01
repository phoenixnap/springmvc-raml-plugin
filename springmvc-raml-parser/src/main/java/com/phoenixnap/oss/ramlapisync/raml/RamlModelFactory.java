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
package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;

/**
 * Abstract Representation of a Raml Factory
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public interface RamlModelFactory {

    RamlModelEmitter createRamlModelEmitter();

    RamlRoot buildRamlRoot(String ramlFileUrl) throws InvalidRamlResourceException;

    RamlRoot createRamlRoot();

    RamlRoot createRamlRoot(String ramlFileUrl);

    RamlResource createRamlResource();

    RamlResource createRamlResource(Object resource);

    RamlAction createRamlAction(Object action);

    RamlAction createRamlAction();

    RamlDocumentationItem createRamlDocumentationItem();

    RamlDocumentationItem createRamlDocumentationItem(Object documentationItem);

    RamlActionType createRamlActionType(Object type);

    RamlResponse createRamlResponse();

    RamlResponse createRamlResponse(Object response);

    RamlMimeType createRamlMimeType();

    RamlMimeType createRamlMimeType(Object mimeType);

    RamlMimeType createRamlMimeTypeWithMime(String mime);

    RamlHeader createRamlHeader(Object haeder);

    RamlUriParameter createRamlUriParameter(Object o);

    RamlUriParameter createRamlUriParameterWithName(String name);

    RamlQueryParameter createRamlQueryParameter();

    RamlQueryParameter createRamlQueryParameter(Object queryParameter);

    RamlFormParameter createRamlFormParameter();

    RamlFormParameter createRamlFormParameter(Object formParameter);

    List<RamlFormParameter> createRamlFormParameters(List<? extends Object> formParameters);

    List<RamlSecurityReference> createRamlSecurityReferences(List<? extends Object> securityReferences);

    RamlSecurityReference createRamlSecurityReference(Object securityReference);

    RamlParamType createRamlParamType(Object paramType);

    default <K, SV, TV> Map<K, TV> transformToUnmodifiableMap(Map<K, SV> source, Map<K, TV> target, Function<SV, TV> valueTransformer) {
        return transformToUnmodifiableMap(source, target, valueTransformer, this::identity);
    }

    default <SK, TK, SV, TV> Map<TK, TV> transformToUnmodifiableMap(Map<SK, SV> source, Map<TK, TV> target, Function<SV, TV> valueTransformer, Function<SK, TK> keyTransformer) {
        if (source == null) {
            target.clear();
        } else if (target.size() != source.size()) {
            target.clear();
            for (SK key : source.keySet()) {
                TV targetValue = valueTransformer.apply(source.get(key));
                TK targetKey = keyTransformer.apply(key);
                target.put(targetKey, targetValue);
            }
        }
        return Collections.unmodifiableMap(target);
    }

    default <SK, TK, SV, TV> Map<TK, TV> transformToUnmodifiableMap(Collection<SV> source, Map<TK, TV> target, Function<SV, TV> valueTransformer, Function<SV, TK> keyTransformer) {
        if (source == null) {
            target.clear();
        } else if (target.size() != source.size()) {
            target.clear();
            for (SV value: source) {
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

}
