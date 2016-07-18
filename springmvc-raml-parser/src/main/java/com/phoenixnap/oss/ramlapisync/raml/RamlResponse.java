package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.MimeType;

import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlResponse {

    void setBody(Map<String, MimeType> body);

    Map<String, MimeType> getBody();

    boolean hasBody();

    void setDescription(String description);

    String getDescription();

}