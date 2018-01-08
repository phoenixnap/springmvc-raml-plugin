package com.phoenixnap.oss.ramlplugin.raml2code.data;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAbstractParam;

/**
 * @author armin.weisser
 */
public abstract class RamlFormParameter extends RamlAbstractParam {

	public abstract boolean isRepeat();
}
