package com.phoenixnap.oss.ramlapisync.generation;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

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

}
