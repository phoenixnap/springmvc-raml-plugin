package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.emitter.RamlEmitter;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlModelEmitter implements RamlModelEmitter {

    private RamlEmitter ramlEmitter = new RamlEmitter();

    @Override
    public String dump(RamlRoot ramlRoot) {
        return ramlEmitter.dump(((Jrp08V1RamlRoot)ramlRoot).getRaml());
    }
}
