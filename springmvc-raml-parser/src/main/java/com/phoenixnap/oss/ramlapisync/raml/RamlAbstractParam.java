package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author armin.weisser
 */
public abstract class RamlAbstractParam { //extends AbstractParam {

    public abstract void setType(RamlParamType paramType);

    public abstract void setRequired(boolean required);

    public abstract void setExample(String example);

    public abstract void setDescription(String description);

    public abstract boolean isRequired();

    public abstract RamlParamType getType();

    public abstract String getExample();

    public abstract void setDisplayName(String displayName);

    public abstract String getDescription();

    public abstract String getDisplayName();
}
