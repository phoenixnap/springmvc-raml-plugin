package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Map;

/**
 * @author aweisser
 */
public class RJP10V2RamlResource implements RamlResource {
    private final Resource delegate;

    public RJP10V2RamlResource(Resource resource) {
        this.delegate = resource;
    }

    @Override
    public Map<String, RamlResource> getResources() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource getResource(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResource(String firstResourcePart) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRelativeUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<RamlActionType, RamlAction> getActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, RamlUriParameter> getUriParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUriParameter(String name, RamlUriParameter uriParameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, RamlUriParameter> getResolvedUriParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource getParentResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentResource(RamlResource parentResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getParentUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentUri(String parentUri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRelativeUri(String relativeUri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDisplayName(String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlAction getAction(RamlActionType actionType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAction(RamlActionType apiAction, RamlAction action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addActions(Map<RamlActionType, RamlAction> actions) {
        throw new UnsupportedOperationException();
    }
}
