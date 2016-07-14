package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
        return new Jrp08V1RamlModelEmitter();
    }

    @Override
    public RamlRoot buildRamlRoot(String ramlFileUrl) {
        return createRamlRoot(new RamlDocumentBuilder().build(ramlFileUrl));
    }

    @Override
    public RamlRoot createRamlRoot() {
        return createRamlRoot(new Raml());
    }

    @Override
    public RamlRoot createRamlRoot(Object root) {
        return new Jrp08V1RamlRoot((Raml)root);
    }

    @Override
    public RamlResource createRamlResource() {
        return createRamlResource(new Resource());
    }

    @Override
    public RamlResource createRamlResource(Object resource) {
        if(resource == null) {
            return null;
        }
        return new Jrp08V1RamlResource((Resource)resource);
    }

    @Override
    public Resource createResource(RamlResource ramlResource) {
        if(ramlResource == null) return null;
        Resource resource = new Resource();
        resource.setDescription(ramlResource.getDescription());
        resource.setParentResource(createResource(ramlResource.getParentResource()));
        resource.setActions(createActions(ramlResource.getActions()));
        resource.setResources(createResources(ramlResource.getResources()));
        resource.setRelativeUri(ramlResource.getRelativeUri());
        resource.setUriParameters(ramlResource.getUriParameters());
        return resource;
    }

    private Map<String, Resource> createResources(Map<String, RamlResource> ramlResources) {
        Map<String, Resource> resources = new LinkedHashMap<>(ramlResources.size());
        ramlResources.keySet().stream().forEach(key -> resources.put(key, createResource(ramlResources.get(key))));
        return resources;
    }

    private Map<ActionType, Action> createActions(Map<ActionType, Action> actions) {
        return actions;
    }
}
