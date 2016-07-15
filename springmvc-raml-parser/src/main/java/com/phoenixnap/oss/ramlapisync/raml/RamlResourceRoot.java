package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlResourceRoot {

    Map<String, RamlResource> getResources();

    void addResource(String path, RamlResource childResource);

    RamlResource getResource(String path);

    void removeResource(String firstResourcePart);

    void addResources(Map<String, RamlResource> resources);
}
