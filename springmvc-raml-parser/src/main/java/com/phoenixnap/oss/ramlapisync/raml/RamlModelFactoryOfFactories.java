package com.phoenixnap.oss.ramlapisync.raml;


import com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1.Jrp08V1RamlModelFactory;

/**
 * @author armin.weisser
 */
public interface RamlModelFactoryOfFactories {
    static RamlModelFactory createRamlModelFactory() {
        // Currently we only have java-raml-parser v1 for raml 0.8
        return new Jrp08V1RamlModelFactory();
    }
}
