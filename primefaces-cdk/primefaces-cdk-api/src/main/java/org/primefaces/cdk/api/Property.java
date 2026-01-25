/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.cdk.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks abstract getter methods to generate component property implementations.
 *
 * <p>Used on abstract getter methods in component base classes. The CDK annotation
 * processor generates concrete implementations that use StateHelper for state management.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @FacesComponentBase
 * public abstract class DataTableBase extends UIData {
 *
 *     @Property(description = "Number of rows per page", defaultValue = "10")
 *     public abstract int getRows();
 *
 *     @Property(description = "Enable pagination", required = true)
 *     public abstract boolean isPaginator();
 *     public abstract void setPaginator(boolean paginator);
 * }
 * }</pre>
 *
 * @see FacesComponentBase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Property {

    /**
     * Description of the property for documentation and taglib generation.
     *
     * @return the property description
     */
    String description() default "";

    /**
     * Whether the property is required.
     *
     * @return true if required, false otherwise
     */
    boolean required() default false;

    /**
     * Default value for the property.
     *
     * <p>For primitives, a default is automatically provided if not specified
     * (e.g., false for boolean, 0 for int).</p>
     *
     * @return the default value as a string
     */
    String defaultValue() default "";

    /**
     * Implicit default value for the property.
     * {@link #defaultValue()} is used to generate the default value for the {@link jakarta.faces.component.StateHelper}, whereas
     * the implicit default value is just for documentation purpose, as the defaults are set "implicit" in javascript.
     *
     * @return the implicit default value as a string
     */
    String implicitDefaultValue() default "";

    /**
     * Whether the generated getter and setter should call super.getXXX() and super.setXXX()
     * instead of using StateHelper directly.
     *
     * @return true if methods should call super, false otherwise
     */
    boolean callSuper() default false;
}