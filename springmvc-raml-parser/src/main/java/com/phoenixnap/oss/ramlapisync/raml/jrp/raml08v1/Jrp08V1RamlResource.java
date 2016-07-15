package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlResource implements RamlResource {

    private static RamlModelFactory ramlModelFactory = RamlModelFactoryOfFactories.createRamlModelFactory();

    private final Resource resource;
    private Map<String, RamlResource> resources = new LinkedHashMap<>();

    public Jrp08V1RamlResource(Resource resource) {
        this.resource = resource;
    }


    private void syncResources() {
        if(resources.size() != resource.getResources().size()) {
            resources.clear();
            Map<String, Resource> baseResources = resource.getResources();
            for (String key : baseResources.keySet()) {
                RamlResource ramlResource = ramlModelFactory.createRamlResource(baseResources.get(key));
                resources.put(key, ramlResource);
            }
        }
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
    public Map<ActionType, Action> getActions() {
        return resource.getActions();
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
        syncResources();
        return Collections.unmodifiableMap(resources);
    }

    @Override
    public RamlResource getResource(String path) {
        return ramlModelFactory.createRamlResource(resource.getResource(path));
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
    public Action getAction(ActionType actionType) {
        return resource.getAction(actionType);
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
