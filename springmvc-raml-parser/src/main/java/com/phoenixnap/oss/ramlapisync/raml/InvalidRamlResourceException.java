package com.phoenixnap.oss.ramlapisync.raml;

import java.util.List;

/**
 * @author aweisser
 */
public class InvalidRamlResourceException extends Exception {
    private final List<String> errors;
    private final String ramlFileUrl;

    public InvalidRamlResourceException(String ramlFileUrl, List<String> errors) {
        this.errors = errors;
        this.ramlFileUrl = ramlFileUrl;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "InvalidRamlResourceException on raml file " +ramlFileUrl+" {" +
                "errors=" + errors +
                '}';
    }
}
