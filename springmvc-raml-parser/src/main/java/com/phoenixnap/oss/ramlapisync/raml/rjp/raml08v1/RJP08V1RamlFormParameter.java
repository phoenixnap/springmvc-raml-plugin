package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import org.raml.model.parameter.FormParameter;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlFormParameter extends RamlFormParameter {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final FormParameter formParameter;

    public RJP08V1RamlFormParameter(FormParameter formParameter) {
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
    public void setType(RamlParamType paramType) {
        formParameter.setType(ramlModelFactory.extractRamlParam(paramType));
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
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(formParameter.getType());
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
