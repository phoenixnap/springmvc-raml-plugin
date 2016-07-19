package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import org.raml.model.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlResponse implements RamlResponse {

    private static Jrp08V1RamlModelFactory ramlModelFactory = new Jrp08V1RamlModelFactory();

    private final Response response;

    private Map<String, RamlMimeType> body = new LinkedHashMap<>();

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
    public void addToBody(String key, RamlMimeType value) {
        body.putIfAbsent(key, value);
        response.getBody().putIfAbsent(key, ramlModelFactory.extractMimeType(value));
    }

    @Override
    public Map<String, RamlMimeType> getBody() {
        return ramlModelFactory.transformToUnmodifiableMap(response.getBody(), body, ramlModelFactory::createRamlMimeType);
    }

    @Override
    public void setBody(Map<String, RamlMimeType> body) {
        this.body = body;
        this.response.setBody(ramlModelFactory.extractBody(body));
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
