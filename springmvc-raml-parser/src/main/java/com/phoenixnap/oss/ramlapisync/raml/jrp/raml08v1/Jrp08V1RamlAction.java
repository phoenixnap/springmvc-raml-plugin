package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.SecurityReference;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlAction implements RamlAction {

    private static Jrp08V1RamlModelFactory ramlModelFactory = new Jrp08V1RamlModelFactory();

    private final Action action;

    public Jrp08V1RamlAction(Action action) {
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
    public Map<String, QueryParameter> getQueryParameters() {
        return action.getQueryParameters();
    }

    @Override
    public Map<String, RamlResponse> getResponses() {
        return ramlModelFactory.createRamlResponses(action.getResponses());
    }

    @Override
    public RamlResource getResource() {
        return ramlModelFactory.createRamlResource(action.getResource());
    }

    @Override
    public Map<String, Header> getHeaders() {
        return action.getHeaders();
    }

    @Override
    public Map<String, MimeType> getBody() {
        return action.getBody();
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
    public void setBody(Map<String, MimeType> body) {
        action.setBody(body);
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
    public List<SecurityReference> getSecuredBy() {
        return action.getSecuredBy();
    }

}
