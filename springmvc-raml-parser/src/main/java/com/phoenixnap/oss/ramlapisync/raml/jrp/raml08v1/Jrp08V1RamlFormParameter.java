package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import org.raml.model.ParamType;
import org.raml.model.parameter.FormParameter;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlFormParameter extends RamlFormParameter {

    private final FormParameter formParameter;

    public Jrp08V1RamlFormParameter(FormParameter formParameter) {
        this.formParameter = formParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    FormParameter getFormParameter() {
        return formParameter;
    }

    @Override
    public void setType(ParamType paramType) {
        formParameter.setType(paramType);
    }

    @Override
    public void setRequired(boolean required) {
        formParameter.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        formParameter.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        formParameter.setDescription(description);
    }

    @Override
    public boolean isRequired() {
        return formParameter.isRequired();
    }

    @Override
    public ParamType getType() {
        return formParameter.getType();
    }

    @Override
    public String getExample() {
        return formParameter.getExample();
    }

    @Override
    public void setDisplayName(String displayName) {
        formParameter.setDisplayName(displayName);
    }

    @Override
    public String getDescription() {
        return formParameter.getDescription();
    }

    @Override
    public String getDisplayName() {
        return formParameter.getDisplayName();
    }
}
