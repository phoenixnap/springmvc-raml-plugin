package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.Resource;

/**
 * @author armin.weisser
 */
public interface RamlModelFactory {

    RamlModelEmitter createRamlModelEmitter();

    RamlRoot buildRamlRoot(String ramlFileUrl);

    RamlRoot createRamlRoot();

    RamlRoot createRamlRoot(Object root);

    RamlResource createRamlResource();

    RamlResource createRamlResource(Object resource);

    // TODO #1 remove when obsolete
    Resource createResource(RamlResource ramlResource);
}
