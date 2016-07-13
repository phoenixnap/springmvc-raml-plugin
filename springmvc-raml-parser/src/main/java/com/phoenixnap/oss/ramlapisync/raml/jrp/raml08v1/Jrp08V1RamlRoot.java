package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlRoot implements RamlRoot {

    private Raml raml;

    public Jrp08V1RamlRoot(Raml raml) {
        if(raml == null) {
            throw new IllegalArgumentException("The Raml instance must not be null");
        }
        this.raml = raml;
    }

    @Override
    public Map<String, Resource> getResources() {
        return raml.getResources();
    }

    @Override
    public Resource getResource(String path) {
        return raml.getResource(path);
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

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Raml getRaml() {
        return raml;
    }
}
