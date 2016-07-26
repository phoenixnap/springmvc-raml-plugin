package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import org.raml.model.parameter.Header;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlHeader extends RamlHeader {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final Header header;

    public RJP08V1RamlHeader(Header header) {
        this.header = header;
    }

    @Override
    public String getDisplayName() {
        return header.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        header.setDisplayName(displayName);
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(header.getType());
    }

    @Override
    public void setType(RamlParamType paramType) {
        header.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public boolean isRequired() {
        return header.isRequired();
    }

    @Override
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
