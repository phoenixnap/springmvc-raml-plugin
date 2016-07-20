package com.phoenixnap.oss.ramlapisync.raml;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author armin.weisser
 */
public interface RamlModelFactory {

    RamlModelEmitter createRamlModelEmitter();

    RamlRoot buildRamlRoot(String ramlFileUrl);

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

    default <T> T identity(T object) {
        return object;
    }
}
