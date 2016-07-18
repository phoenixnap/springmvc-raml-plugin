package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.ParamType;
import org.raml.model.parameter.AbstractParam;

/**
 * @author armin.weisser
 */
public abstract class RamlHeader extends AbstractParam {

    protected abstract String getDisplayNameIntern();

    protected abstract ParamType getTypeIntern();

    protected abstract boolean isRequiredIntern();

    @Override
    public String getDisplayName() {
        return getDisplayNameIntern();
    }

    @Override
    public ParamType getType() {
        return getTypeIntern();
    }

    @Override
    public boolean isRequired() {
        return isRequiredIntern();
    }
}
