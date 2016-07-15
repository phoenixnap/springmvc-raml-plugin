package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.Raml;
import org.raml.model.Resource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlRoot implements RamlRoot {

    private static Jrp08V1RamlModelFactory ramlModelFactory = new Jrp08V1RamlModelFactory();

    private final Raml raml;

    private Map<String, RamlResource> resources = new LinkedHashMap<>();

    public Jrp08V1RamlRoot(Raml raml) {
        if(raml == null) {
            throw new IllegalArgumentException("The Raml instance must not be null");
        }
        this.raml = raml;
    }

    private void syncResources() {
        if(resources.size() != raml.getResources().size()) {
            resources.clear();
            Map<String, Resource> baseResources = raml.getResources();
            for (String key : baseResources.keySet()) {
                RamlResource ramlResource = ramlModelFactory.createRamlResource(baseResources.get(key));
                this.resources.put(key, ramlResource);
            }
        }
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Raml getRaml() {
        return raml;
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        raml.getResources().put(path, ramlModelFactory.extractResource(childResource));
        resources.put(path, childResource);
    }

    @Override
    public Map<String, RamlResource> getResources() {
        syncResources();
        return Collections.unmodifiableMap(resources);
    }

    @Override
    public void removeResource(String firstResourcePart) {
        raml.getResources().remove(firstResourcePart);
        resources.remove(firstResourcePart);
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
        for(String key: resources.keySet()) {
            addResource(key, resources.get(key));
        }
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
    public void setDocumentation(List<RamlDocumentationItem> documentationItems) {
        raml.setDocumentation(ramlModelFactory.extractDocumentationItems(documentationItems));
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
