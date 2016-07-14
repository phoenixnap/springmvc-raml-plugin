package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlResourceRoot {

    Map<String, RamlResource> getResources();

    RamlResource getResource(String path);

}
