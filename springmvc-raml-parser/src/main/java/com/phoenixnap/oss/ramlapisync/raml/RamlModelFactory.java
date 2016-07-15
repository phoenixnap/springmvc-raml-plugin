package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.Action;
import org.raml.model.Resource;

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

    // TODO #1 remove when obsolete or move to jrp.raml08v1 package
    Resource extractResource(RamlResource ramlResource);

    RamlAction createRamlAction(Object action);

    RamlAction createRamlAction();

    // TODO #1 remove when obsolete or move to jrp.raml08v1 package
    Action extractAction(RamlAction ramlAction);
}
