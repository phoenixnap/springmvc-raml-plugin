package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import org.raml.model.MimeType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlMimeType implements RamlMimeType {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final MimeType mimeType;

    private Map<String, List<RamlFormParameter>> formParameters = new LinkedHashMap<>();

    public RJP08V1RamlMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public Map<String, List<RamlFormParameter>> getFormParameters() {
        return ramlModelFactory.transformToUnmodifiableMap(mimeType.getFormParameters(), formParameters, ramlModelFactory::createRamlFormParameters);
    }

    @Override
    public void setFormParameters(Map<String, List<RamlFormParameter>> formParameters) {
        this.formParameters = formParameters;
        mimeType.setFormParameters(ramlModelFactory.extractFormParameters(formParameters));
    }

    @Override
    public String getSchema() {
        return mimeType.getSchema();
    }

    @Override
    public void setSchema(String schema) {
        mimeType.setSchema(schema);
    }

    @Override
    public void setExample(String example) {
        mimeType.setExample(example);
    }

    @Override
    public void addFormParameters(String name, List<RamlFormParameter> ramlFormParameters) {
        this.formParameters.put(name, ramlFormParameters);
        if(this.mimeType.getFormParameters() == null) {
            this.mimeType.setFormParameters(new LinkedHashMap<>());
        }
        this.mimeType.getFormParameters().put(name, ramlModelFactory.extractFormParameters(ramlFormParameters));
    }

}
