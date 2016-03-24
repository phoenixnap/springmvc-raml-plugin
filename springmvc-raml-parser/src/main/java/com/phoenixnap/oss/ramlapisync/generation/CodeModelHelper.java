package com.phoenixnap.oss.ramlapisync.generation;

import com.sun.codemodel.*;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public abstract class CodeModelHelper {

    /**
     *
     * @param codeModel
     * @param simpleClassName
     * @return the first class in any package that matches the simple class name.
     */
    public static JClass findFirstClassBySimpleName(JCodeModel codeModel, String simpleClassName) {
        while(codeModel.packages().hasNext()) {
            JPackage jPackage = codeModel.packages().next();
            while(jPackage.classes().hasNext()) {
                JDefinedClass aClass = jPackage.classes().next();
                if(aClass.name().equals(simpleClassName)) {
                    return aClass;
                }
            }
        }
        throw new InvalidModelException("No unique class found for simple class name " + simpleClassName);
    }

    public static JExtMethod ext(JMethod jMethod, JCodeModel jCodeModel) {
        return new JExtMethod(jMethod, jCodeModel);
    }

    /**
     * Helper class because JMethod does not expose it's JCodeModel.
     */
    public static class JExtMethod {

        private final JMethod jMethod;
        private final JCodeModel owner;

        public JExtMethod(JMethod jMethod, JCodeModel jCodeModel) {
            this.jMethod = jMethod;
            this.owner = jCodeModel;
        }

        public JMethod get() {
            return jMethod;
        }

        public JCodeModel owner() {
            return owner;
        }
    }
}
