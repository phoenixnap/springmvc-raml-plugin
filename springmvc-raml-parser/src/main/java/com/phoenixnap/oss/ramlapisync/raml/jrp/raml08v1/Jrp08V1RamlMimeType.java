package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import org.raml.model.MimeType;
import org.raml.model.parameter.FormParameter;

import java.util.List;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlMimeType implements RamlMimeType {

    private final MimeType mimeType;

    public Jrp08V1RamlMimeType(MimeType mimeType) {
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
    public Map<String, List<FormParameter>> getFormParameters() {
        return mimeType.getFormParameters();
    }


    @Override
    public void setFormParameters(Map<String, List<FormParameter>> formParameters) {
        mimeType.setFormParameters(formParameters);
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
}
