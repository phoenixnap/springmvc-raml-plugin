package com.phoenixnap.oss.ramlapisync.raml;

import java.math.BigDecimal;

/**
 * @author armin.weisser
 */
public abstract class RamlQueryParameter extends RamlAbstractParam {

    public abstract void setRepeat(boolean repeat);

    public abstract Integer getMinLength();

    public abstract Integer getMaxLength();

    public abstract BigDecimal getMinimum();

    public abstract BigDecimal getMaximum();

    public abstract String getPattern();

    public abstract boolean isRepeat();
}
