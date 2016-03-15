package com.phoenixnap.oss.ramlapisync.generation.serialize;

/**
 * @author armin.weisser
 */
public interface ApiControllerMetadataSerializer {

    /**
     *
     * @return a serialized represantation of the generated API controller code.
     */
    String serialize();

    /**
     *
     * @return the name of the generated API controller.
     */
    String getName();
}
