package net.smackem.nutfx.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
public @interface NutParam {
    String value();
    boolean isRequired() default false;

    /**
     * A class that is searched for the following methods, in order:
     * <ul>
     *     <li>
     *         <code>
     *     static type-of-parameter parse(String)
     *         </code>
     *     </li>
     *     <li>
     *         <code>
     *     static type-of-parameter valueOf(String)
     *         </code>
     *     </li>
     * </ul>
     * @return A class object
     */
    Class<?> converterClass() default void.class;
}
