package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.ActionType;
import org.raml.model.parameter.UriParameter;

import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlResource extends RamlResourceRoot {

    String getRelativeUri();

    Map<ActionType, RamlAction> getActions();

    Map<String, UriParameter> getUriParameters();

    Map<String, UriParameter> getResolvedUriParameters();

    String getUri();

    String getDescription();

    RamlResource getParentResource();

    void setParentResource(RamlResource parentResource);

    String getParentUri();

    void setParentUri(String parentUri);

    void setRelativeUri(String relativeUri);

    void setDisplayName(String displayName);

    void setDescription(String description);

    RamlAction getAction(ActionType actionType);

    void addAction(ActionType apiAction, RamlAction action);

    void addActions(Map<ActionType, RamlAction> actions);
}
