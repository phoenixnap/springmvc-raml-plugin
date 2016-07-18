package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlResponse {

    void setBody(Map<String, RamlMimeType> body);

    Map<String, RamlMimeType> getBody();

    boolean hasBody();

    void setDescription(String description);

    String getDescription();

    void addToBody(String key, RamlMimeType value);
}