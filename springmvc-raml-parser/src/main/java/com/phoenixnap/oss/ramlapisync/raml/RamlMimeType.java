package com.phoenixnap.oss.ramlapisync.raml;

import org.raml.model.parameter.FormParameter;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlMimeType {

    Map<String, List<FormParameter>> getFormParameters();

    void setFormParameters(Map<String, List<FormParameter>> formParameters);

    String getSchema();

    void setSchema(String schema);

    void setExample(String example);
}
