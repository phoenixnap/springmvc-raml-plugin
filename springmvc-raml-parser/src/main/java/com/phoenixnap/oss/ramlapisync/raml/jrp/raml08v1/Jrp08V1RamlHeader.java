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
    protected String getDisplayNameIntern() {
        return header.getDisplayName();
    }

    @Override
    protected ParamType getTypeIntern() {
        return header.getType();
    }

    @Override
    protected boolean isRequiredIntern() {
        return header.isRequired();
    }
}
