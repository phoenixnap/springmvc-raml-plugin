package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import org.raml.model.MimeType;
import org.raml.model.Response;

import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlResponse implements RamlResponse {
    private final Response response;

    public Jrp08V1RamlResponse(Response response) {
        this.response = response;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Response getResponse() {
        return response;
    }

    @Override
    public void setBody(Map<String, MimeType> body) {
        response.setBody(body);
    }

    @Override
    public Map<String, MimeType> getBody() {
        return response.getBody();
    }

    @Override
    public boolean hasBody() {
        return response.hasBody();
    }

    @Override
    public void setDescription(String description) {
        response.setDescription(description);
    }

    @Override
    public String getDescription() {
        return response.getDescription();
    }
}
