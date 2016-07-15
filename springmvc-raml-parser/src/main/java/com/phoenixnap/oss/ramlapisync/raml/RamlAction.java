package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Response;
import org.raml.model.SecurityReference;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlAction {

    ActionType getType();

    Map<String,QueryParameter> getQueryParameters();

    Map<String, Response> getResponses();

    RamlResource getResource();

    Map<String, Header> getHeaders();

    Map<String, MimeType> getBody();

    boolean hasBody();

    String getDescription();

    void setDescription(String description);

    void setBody(Map<String, MimeType> body);

    void setResource(RamlResource resource);

    void setType(ActionType actionType);

    List<SecurityReference> getSecuredBy();
}
