package com.phoenixnap.oss.ramlapisync.generation;

/**
 * @author armin.weisser
 */
public interface GeneratorRecipe<C, M> {
    C apply(M metadata);
}
