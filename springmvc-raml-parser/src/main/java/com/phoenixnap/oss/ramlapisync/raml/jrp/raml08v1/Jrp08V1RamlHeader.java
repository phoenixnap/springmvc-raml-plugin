package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import org.raml.model.ParamType;
import org.raml.model.parameter.Header;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlHeader extends RamlHeader {

    private final Header header;

    public Jrp08V1RamlHeader(Header header) {
        this.header = header;
    }

    @Override
    public String getDisplayName() {
        return header.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        header.setDisplayName(displayName);
    }

    public ParamType getType() {
        return header.getType();
    }

    public void setType(ParamType type) {
        header.setType(type);
    }

    public boolean isRequired() {
        return header.isRequired();
    }

    public void setRequired(boolean required) {
        header.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        header.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        header.setDescription(description);
    }

    @Override
    public String getExample() {
        return header.getExample();
    }

    @Override
    public String getDescription() {
        return header.getDescription();
    }
}
