package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.raml.model.parameter.UriParameter;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlUriParameter extends RamlUriParameter {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final UriParameter uriParameter;

    public RJP08V1RamlUriParameter(UriParameter uriParameter) {
        this.uriParameter = uriParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    UriParameter getUriParameter() {
        return uriParameter;
    }

    @Override
    public String getDisplayName() {
        return uriParameter.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        uriParameter.setDisplayName(displayName);
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(uriParameter.getType());
    }

    @Override
    public void setType(RamlParamType paramType) {
        uriParameter.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public boolean isRequired() {
        return uriParameter.isRequired();
    }

    @Override
    public void setRequired(boolean required) {
        uriParameter.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        uriParameter.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        uriParameter.setDescription(description);
    }

    @Override
    public String getExample() {
        return uriParameter.getExample();
    }

    @Override
    public String getDescription() {
        return uriParameter.getDescription();
    }
}
