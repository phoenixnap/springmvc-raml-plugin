package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;
import org.raml.model.Action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlAction implements RamlAction {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final Action action;

    private Map<String, RamlResponse> responses = new LinkedHashMap<>();

    private Map<String, RamlMimeType> body = new LinkedHashMap<>();

    private Map<String, RamlHeader> headers = new LinkedHashMap<>();

    private Map<String, RamlQueryParameter> queryParameters = new LinkedHashMap<>();

    public RJP08V1RamlAction(Action action) {
        this.action = action;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Action getAction() {
        return action;
    }

    @Override
    public RamlActionType getType() {
        return ramlModelFactory.createRamlActionType(action.getType());
    }

    @Override
    public Map<String, RamlQueryParameter> getQueryParameters() {
        return ramlModelFactory.transformToUnmodifiableMap(action.getQueryParameters(), queryParameters, ramlModelFactory::createRamlQueryParameter);
    }

    @Override
    public Map<String, RamlResponse> getResponses() {
        return ramlModelFactory.transformToUnmodifiableMap(action.getResponses(), responses, ramlModelFactory::createRamlResponse);
    }

    @Override
    public void addResponse(String httpStatus, RamlResponse response) {
        responses.put(httpStatus, response);
        action.getResponses().put(httpStatus, ramlModelFactory.extractResponse(response));
    }

    @Override
    public RamlResource getResource() {
        return ramlModelFactory.createRamlResource(action.getResource());
    }

    @Override
    public Map<String, RamlHeader> getHeaders() {
        return ramlModelFactory.transformToUnmodifiableMap(action.getHeaders(), headers, ramlModelFactory::createRamlHeader);
    }

    @Override
    public Map<String, RamlMimeType> getBody() {
        return ramlModelFactory.transformToUnmodifiableMap(action.getBody(), body, ramlModelFactory::createRamlMimeType);
    }

    @Override
    public void setBody(Map<String, RamlMimeType> body) {
        this.body = body;
        this.action.setBody(ramlModelFactory.extractBody(body));
    }

    @Override
    public boolean hasBody() {
        return action.hasBody();
    }

    @Override
    public String getDescription() {
        return action.getDescription();
    }

    @Override
    public void setDescription(String description) {
        action.setDescription(description);
    }

    @Override
    public void setResource(RamlResource resource) {
        action.setResource(ramlModelFactory.extractResource(resource));
    }

    @Override
    public void setType(RamlActionType actionType) {
        action.setType(ramlModelFactory.extractActionType(actionType));
    }

    @Override
    public List<RamlSecurityReference> getSecuredBy() {
        return ramlModelFactory.createRamlSecurityReferences(action.getSecuredBy());
    }

    @Override
    public void addQueryParameters(Map<String, RamlQueryParameter> queryParameters) {
        for(String key: queryParameters.keySet()) {
            addQueryParameter(key, queryParameters.get(key));
        }
    }

    private void addQueryParameter(String key, RamlQueryParameter ramlQueryParameter) {
        queryParameters.put(key, ramlQueryParameter);
        action.getQueryParameters().put(key, ramlModelFactory.extractQueryParameter(ramlQueryParameter));
    }
}
