package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.DocumentationItem;
import org.raml.model.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlRoot {

    Map<String, Resource> getResources();

    Resource getResource(String path);

    String getMediaType();

    List<Map<String, String>> getSchemas();

    void setBaseUri(String baseUri);

    void setVersion(String version);

    void setTitle(String title);

    void setDocumentation(List<DocumentationItem> documentationItems);

    void setMediaType(String mediaType);

    String getBaseUri();
}
