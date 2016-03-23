package com.phoenixnap.oss.ramlapisync.generation;

import java.util.StringTokenizer;

import static org.raml.parser.utils.Inflector.capitalize;

/**
 * @author armin.weisser
 */
public class Inflector {

    /**
     * Generates a camel case version of a phrase from dash.
     *
     * @param dash          dash version of a word to converted to camel case.
     * @param capitalizeFirstChar set to true if first character needs to be capitalized, false if not.
     * @return camel case version of dash.
     */
    public static String camelize(String dash, boolean capitalizeFirstChar)
    {
        StringBuilder result = new StringBuilder("");
        StringTokenizer st = new StringTokenizer(dash, "-");
        while (st.hasMoreTokens())
        {
            result.append(capitalize(st.nextToken()));
        }
        return capitalizeFirstChar ? result.toString() : result.substring(0, 1).toLowerCase() + result.substring(1);
    }

}
