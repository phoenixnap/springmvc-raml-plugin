package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.raml.model.ParamType;
import org.raml.model.parameter.UriParameter;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlUriParameter extends RamlUriParameter {

    private final UriParameter uriParameter;

    public Jrp08V1RamlUriParameter(UriParameter uriParameter) {
        this.uriParameter = uriParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    UriParameter getUriParameter() {
        return uriParameter;
    }

    public String getDisplayName() {
        return uriParameter.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        uriParameter.setDisplayName(displayName);
    }

    public ParamType getType() {
        return uriParameter.getType();
    }

    public void setType(ParamType type) {
        uriParameter.setType(type);
    }

    public boolean isRequired() {
        return uriParameter.isRequired();
    }

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
