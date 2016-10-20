package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author aweisser
 */
public class CannotGetRamlVersionException extends RuntimeException {
    public CannotGetRamlVersionException(String raml, RamlVersion... versionsSearchedFor) {
        super("Can't find one of the versions" +versionsSearchedFor+ " in the following RAML content:\n"+raml);
    }
}
