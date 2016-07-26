package com.phoenixnap.oss.ramlapisync.raml;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public interface RamlMimeType {

    Map<String, List<RamlFormParameter>> getFormParameters();

    void setFormParameters(Map<String, List<RamlFormParameter>> formParameters);

    void addFormParameters(String name, List<RamlFormParameter> formParameters);

    String getSchema();

    void setSchema(String schema);

    void setExample(String example);
}
