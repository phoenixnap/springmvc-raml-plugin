package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlResource implements RamlResource {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final Resource resource;
    private Map<String, RamlResource> resources = new LinkedHashMap<>();
    private Map<RamlActionType, RamlAction> actions = new LinkedHashMap<>();
    private Map<String, RamlUriParameter> uriParameters = new LinkedHashMap<>();
    private Map<String, RamlUriParameter> resolvedUriParameters = new LinkedHashMap<>();

    public RJP08V1RamlResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Resource getResource() {
        return resource;
    }

    @Override
    public String getRelativeUri() {
        return resource.getRelativeUri();
    }

    @Override
    public void setRelativeUri(String relativeUri) {
        resource.setRelativeUri(relativeUri);
    }

    @Override
    public Map<RamlActionType, RamlAction> getActions() {
        return ramlModelFactory.transformToUnmodifiableMap(resource.getActions(), actions, ramlModelFactory::createRamlAction, ramlModelFactory::createRamlActionType);
    }


    @Override
    public void addActions(Map<RamlActionType, RamlAction> newActions) {
        for(RamlActionType key: newActions.keySet()) {
            addAction(key, newActions.get(key));
        }
    }

    @Override
    public void addAction(RamlActionType actionType, RamlAction action) {
        resource.getActions().put(ramlModelFactory.extractActionType(actionType), ramlModelFactory.extractAction(action));
        actions.put(actionType, action);
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        resource.getResources().put(path, ramlModelFactory.extractResource(childResource));
        resources.put(path, childResource);
    }

    @Override
    public void removeResource(String firstResourcePart) {
        resource.getResources().remove(firstResourcePart);
        resources.remove(firstResourcePart);
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
        for(String key: resources.keySet()) {
            addResource(key, resources.get(key));
        }
    }

    @Override
    public Map<String, RamlResource> getResources() {
        return ramlModelFactory.transformToUnmodifiableMap(resource.getResources(), resources, ramlModelFactory::createRamlResource);
    }

    @Override
    public RamlResource getResource(String path) {
        return ramlModelFactory.createRamlResource(resource.getResource(path));
    }

    @Override
    public Map<String, RamlUriParameter> getUriParameters() {
        return ramlModelFactory.transformToUnmodifiableMap(resource.getUriParameters(), uriParameters, ramlModelFactory::createRamlUriParameter);
    }

    @Override
    public void addUriParameter(String name, RamlUriParameter uriParameter) {
        uriParameters.put(name, uriParameter);
        resource.getUriParameters().put(name, ramlModelFactory.extractUriParameter(uriParameter));
    }

    @Override
    public Map<String, RamlUriParameter> getResolvedUriParameters() {
        return ramlModelFactory.transformToUnmodifiableMap(resource.getResolvedUriParameters(), resolvedUriParameters, ramlModelFactory::createRamlUriParameter);
    }

    @Override
    public String getUri() {
        return resource.getUri();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public RamlResource getParentResource() {
        return ramlModelFactory.createRamlResource(resource.getParentResource());
    }

    @Override
    public String getParentUri() {
        return resource.getParentUri();
    }

    @Override
    public void setParentUri(String parentUri) {
        resource.setParentUri(parentUri);
    }

    @Override
    public void setParentResource(RamlResource parentResource) {
        resource.setParentResource(ramlModelFactory.extractResource(parentResource));
    }

    @Override
    public void setDisplayName(String displayName) {
        resource.setDisplayName(displayName);
    }

    @Override
    public void setDescription(String description) {
        resource.setDescription(description);
    }

    @Override
    public RamlAction getAction(RamlActionType actionType) {
        ActionType name = ramlModelFactory.extractActionType(actionType);
        Action action = resource.getAction(name);
        return ramlModelFactory.createRamlAction(action);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
        {
            return true;
        }
        if (!(o instanceof RamlResource))
        {
            return false;
        }

        RamlResource resource = (RamlResource) o;

        return getParentUri().equals(resource.getParentUri()) && getRelativeUri().equals(resource.getRelativeUri());

    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }
}
