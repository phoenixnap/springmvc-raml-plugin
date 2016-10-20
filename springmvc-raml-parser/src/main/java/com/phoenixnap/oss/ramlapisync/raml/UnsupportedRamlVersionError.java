package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author aweisser
 */
public class UnsupportedRamlVersionError extends Error {
    public UnsupportedRamlVersionError(RamlVersion ramlVersion, RamlVersion... supportedVersions) {
        super("RAML Version "+ramlVersion+ " is not supported. Supported versions are "+supportedVersions);
    }
}
