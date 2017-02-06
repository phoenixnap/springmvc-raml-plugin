package com.phoenixnap.oss.ramlapisync.data;

import com.phoenixnap.oss.ramlapisync.raml.RamlAbstractParam;

/**
 * @author armin.weisser
 */
public abstract class RamlFormParameter extends RamlAbstractParam {
	
    
    public abstract boolean isRepeat();
    public abstract void setRepeat(boolean repeat);
}
