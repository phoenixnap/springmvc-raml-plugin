package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Map;

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

    Map<String,RamlResponse> createRamlResponses(Map<String, ? extends Object> responses);

    RamlResponse createRamlResponse();

    RamlResponse createRamlResponse(Object response);
}
