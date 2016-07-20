package com.phoenixnap.oss.ramlapisync.raml;


import com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1.RJP08V1RamlModelFactory;

/**
 * @author armin.weisser
 */
public interface RamlModelFactoryOfFactories {
    static RamlModelFactory createRamlModelFactory() {
        // Currently we only have java-raml-parser v1 for raml 0.8
        return new RJP08V1RamlModelFactory();
    }
}
