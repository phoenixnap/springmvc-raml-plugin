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

    RamlDocumentationItem createDocumentationItem();

    RamlDocumentationItem createDocumentationItem(Object documentationItem);

    RamlActionType createActionType(Object type);
}
