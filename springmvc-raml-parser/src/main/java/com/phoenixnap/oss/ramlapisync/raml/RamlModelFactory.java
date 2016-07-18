package com.phoenixnap.oss.ramlapisync.raml;

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

    default <K, SV, TV> void syncFromTo(Map<K, SV> source, Map<K, TV> target, Function<SV, TV> valueTransformer) {
        syncFromTo(source, target, valueTransformer, this::identity);
    }

    default <SK, TK,  SV, TV> void syncFromTo(Map<SK, SV> source, Map<TK, TV> target, Function<SV, TV> valueTransformer, Function<SK, TK> keyTransformer) {
        if(source == null) {
            target.clear();
        }
        else if(target.size() != source.size()) {
            target.clear();
            for (SK key : source.keySet()) {
                TV targetValue = valueTransformer.apply(source.get(key));
                TK targetKey = keyTransformer.apply(key);
                target.put(targetKey, targetValue);
            }
        }
    }

    default <T> T identity(T object) {
        return object;
    }
}
