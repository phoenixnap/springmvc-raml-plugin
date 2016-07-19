package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.ParamType;

/**
 * @author armin.weisser
 */
public abstract class RamlAbstractParam { //extends AbstractParam {

    public abstract void setType(ParamType paramType);

    public abstract void setRequired(boolean required);

    public abstract void setExample(String example);

    public abstract void setDescription(String description);

    public abstract boolean isRequired();

    public abstract ParamType getType();

    public abstract String getExample();

    public abstract void setDisplayName(String displayName);

    public abstract String getDescription();

    public abstract String getDisplayName();
}
