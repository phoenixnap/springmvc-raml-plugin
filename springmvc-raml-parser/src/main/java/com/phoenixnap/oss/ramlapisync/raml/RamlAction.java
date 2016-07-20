package com.phoenixnap.oss.ramlapisync.raml;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlAction {

    RamlActionType getType();

    Map<String, RamlQueryParameter> getQueryParameters();

    Map<String, RamlResponse> getResponses();

    RamlResource getResource();

    Map<String, RamlHeader> getHeaders();

    Map<String, RamlMimeType> getBody();

    boolean hasBody();

    String getDescription();

    void setDescription(String description);

    void setBody(Map<String, RamlMimeType> body);

    void setResource(RamlResource resource);

    void setType(RamlActionType actionType);

    List<RamlSecurityReference> getSecuredBy();

    void addResponse(String httpStatus, RamlResponse response);

    void addQueryParameters(Map<String, RamlQueryParameter> queryParameters);
}
