package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.Resource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlRoot implements RamlRoot {

    private static RamlModelFactory ramlModelFactory = RamlModelFactoryOfFactories.createRamlModelFactory();

    private final Raml raml;

    private Map<String, RamlResource> resources;

    public Jrp08V1RamlRoot(Raml raml) {
        if(raml == null) {
            throw new IllegalArgumentException("The Raml instance must not be null");
        }
        this.raml = raml;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Raml getRaml() {
        return raml;
    }

    @Override
    public Map<String, RamlResource> getResources() {
        if(resources == null) {
            Map<String, Resource> baseResources = raml.getResources();
            resources = new LinkedHashMap<>(baseResources.size());
            baseResources.keySet().stream().forEach(path -> {
                Resource baseResource = baseResources.get(path);
                RamlResource resource = ramlModelFactory.createRamlResource(baseResource);
                resources.put(path, resource);
            });

        }
        return resources;
    }

    @Override
    public RamlResource getResource(String path) {
        return ramlModelFactory.createRamlResource(raml.getResource(path));
    }

    @Override
    public String getMediaType() {
        return raml.getMediaType();
    }

    @Override
    public List<Map<String, String>> getSchemas() {
        return raml.getSchemas();
    }

    @Override
    public void setBaseUri(String baseUri) {
        raml.setBaseUri(baseUri);
    }

    @Override
    public void setVersion(String version) {
        raml.setVersion(version);
    }

    @Override
    public void setTitle(String title) {
        raml.setTitle(title);
    }

    @Override
    public void setDocumentation(List<DocumentationItem> documentationItems) {
        raml.setDocumentation(documentationItems);
    }

    @Override
    public void setMediaType(String mediaType) {
        raml.setMediaType(mediaType);
    }

    @Override
    public String getBaseUri() {
        return raml.getBaseUri();
    }
}
