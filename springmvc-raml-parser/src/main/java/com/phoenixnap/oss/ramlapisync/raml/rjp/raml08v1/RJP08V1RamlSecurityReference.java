package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;
import org.raml.model.SecurityReference;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlSecurityReference implements RamlSecurityReference {

    private final SecurityReference securityReference;

    public RJP08V1RamlSecurityReference(SecurityReference securityReference) {
        this.securityReference = securityReference;
    }

    @Override
    public String getName() {
        return securityReference.getName();
    }
}
