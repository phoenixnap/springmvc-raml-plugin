package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author aweisser
 */
public enum RamlVersion {

    V08("#%RAML 0.8"),
    V10("#%RAML 1.0");

    private final String identifier;

    RamlVersion(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param raml content to parse the RAML version from
     * @return RamlVersion that fits to the raml content
     * @throws CannotGetRamlVersionException
     */
    public static RamlVersion forRaml(String raml) {
        if(raml.startsWith(V08.identifier)) {
            return V08;
        } else if(raml.startsWith(V10.identifier)) {
            return V10;
        }
        throw new CannotGetRamlVersionException(raml, V08, V10);
    }
}
