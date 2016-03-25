package com.phoenixnap.oss.ramlapisync.generation.rules;

/**
 * Represents a generic code generation rule (e.g. production of JCodeModel from RAML schema).
 * The Rule can be executed or 'applied' to perform the code generation step.
 *
 * @param <T>
 *            The type of the source code item on which this rule can operate. E.g. JClass.
 * @param <R>
 *            The type of the source code item generated by this rule. E.g. JMethod.
 * @param <M>
 *            The type of the meta data that this rule should operate on. E.g ApiControllerMetadata.
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public interface Rule<T, R, M> {

    /**
     * Add whatever Java source is required to the given generatable to
     * represent this rule.
     *
     * @param metadata
     *            The meta data from which the code is build.
     * @param generatableType
     *            A code generation construct to which this rule should be applied
     *
     * @return The newly generated source code item that was added/created as a result of applying this rule
     */
    R apply(M metadata, T generatableType);

}