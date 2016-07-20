package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import org.raml.model.DocumentationItem;

/**
 * @author armin.weisser
 */
public class RJP08V1RamlDocumentationItem implements RamlDocumentationItem {

    private final DocumentationItem documentationItem;

    public RJP08V1RamlDocumentationItem(DocumentationItem documentationItem) {
        this.documentationItem = documentationItem;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    DocumentationItem getDocumentationItem() {
        return documentationItem;
    }

    @Override
    public void setContent(String content) {
        documentationItem.setContent(content);
    }

    @Override
    public void setTitle(String title) {
        documentationItem.setTitle(title);
    }
}
