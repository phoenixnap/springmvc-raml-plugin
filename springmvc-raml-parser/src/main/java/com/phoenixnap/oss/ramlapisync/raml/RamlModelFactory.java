package com.phoenixnap.oss.ramlapisync.raml;

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
}
