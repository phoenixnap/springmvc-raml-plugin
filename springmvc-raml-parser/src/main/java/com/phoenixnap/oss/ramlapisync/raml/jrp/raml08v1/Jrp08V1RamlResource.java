package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlResource implements RamlResource {

    private static RamlModelFactory ramlModelFactory = RamlModelFactoryOfFactories.createRamlModelFactory();

    private final Resource resource;
    private Map<String, RamlResource> resources;

    public Jrp08V1RamlResource(Resource resource) {
        this.resource = resource;
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
    public Map<ActionType, Action> getActions() {
        return resource.getActions();
    }

    @Override
    public Map<String, RamlResource> getResources() {
        if(resources == null) {
            Map<String, Resource> baseResources = resource.getResources();
            resources = new LinkedHashMap<>(baseResources.size());
            baseResources.keySet().stream().forEach(path -> resources.put(path, ramlModelFactory.createRamlResource(baseResources.get(path))));
        }
        return resources;
    }

    @Override
    public RamlResource getResource(String path) {
        Resource baseResource = this.resource.getResource(path);
        if(baseResource == null) {
            return null;
        }
        return ramlModelFactory.createRamlResource(baseResource);
    }

    @Override
    public Map<String, UriParameter> getUriParameters() {
        return resource.getUriParameters();
    }

    @Override
    public Map<String, UriParameter> getResolvedUriParameters() {
        return resource.getResolvedUriParameters();
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
        resource.setParentResource(ramlModelFactory.createResource(parentResource));
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
    public Action getAction(ActionType actionType) {
        return resource.getAction(actionType);
    }
}
