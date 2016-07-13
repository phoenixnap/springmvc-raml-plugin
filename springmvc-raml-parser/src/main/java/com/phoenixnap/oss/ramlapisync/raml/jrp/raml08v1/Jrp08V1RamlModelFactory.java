package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
        return new Jrp08V1RamlModelEmitter();
    }

    @Override
    public RamlRoot buildRamlRoot(String ramlFileUrl) {
        return createRamlRoot(new RamlDocumentBuilder().build(ramlFileUrl));
    }

    @Override
    public RamlRoot createRamlRoot() {
        return createRamlRoot(new Raml());
    }

    @Override
    public RamlRoot createRamlRoot(Object root) {
        return new Jrp08V1RamlRoot((Raml)root);
    }
}
