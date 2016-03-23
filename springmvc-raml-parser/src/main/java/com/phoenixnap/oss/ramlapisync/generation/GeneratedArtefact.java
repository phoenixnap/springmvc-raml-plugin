package com.phoenixnap.oss.ramlapisync.generation;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author armin.weisser
 */
public class GeneratedArtefact {
    private final JCodeModel model;
    private final JDefinedClass topLevelClass;

    public GeneratedArtefact(JCodeModel model, JDefinedClass topLevelClass) {
        this.model = model;
        this.topLevelClass = topLevelClass;
    }

    public JCodeModel getModel() {
        return model;
    }

    public JDefinedClass getTopLevelClass() {
        return topLevelClass;
    }
}
