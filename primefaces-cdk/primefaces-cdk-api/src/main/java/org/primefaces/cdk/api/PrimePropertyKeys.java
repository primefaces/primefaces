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

/**
 * Interface for property key enums.
 *
 * <p>Generated PropertyKeys enums for components with {@link Property} annotations
 * implement this interface to provide type-safe access to property names and types.</p>
 *
 * <p>Example generated enum:</p>
 * <pre>{@code
 * public enum PropertyKeys implements PrimePropertyKeys {
 *     rows(Integer.class),
 *     paginator(Boolean.class);
 *
 *     private final Class<?> type;
 *
 *     PropertyKeys(Class<?> type) {
 *         this.type = type;
 *     }
 *
 *     public Class<?> getType() {
 *         return type;
 *     }
 * }
 * }</pre>
 *
 * @see Property
 */
public interface PrimePropertyKeys {

    /**
     * Returns the type for this property.
     *
     * <p>This type should be used when converting tag attribute values via
     * {@link jakarta.faces.view.facelets.TagAttribute#getObject(jakarta.faces.view.facelets.FaceletContext, Class)}.</p>
     *
     * @return the object type for this property
     */
    Class<?> getType();

    /**
     * Returns the property name.
     *
     * <p>Default implementation returns the enum constant name via toString().</p>
     *
     * @return the property name
     */
    String getName();

    /**
     * Description of the property for documentation and taglib generation.
     *
     * @return the property description
     */
    String getDescription();

    /**
     * Whether the property is required.
     *
     * @return true if required, false otherwise
     */
    boolean isRequired();

    /**
     * Default value for the property.
     *
     * <p>For primitives, a default is automatically provided if not specified
     * (e.g., false for boolean, 0 for int).</p>
     *
     * @return the default value as a string
     */
    String getDefaultValue();

    /**
     * Implicit default value for the property.
     * {@link #getDefaultValue()} is used to generate the default value for the {@link jakarta.faces.component.StateHelper}, whereas
     * the implicit default value is just for documentation purpose, as the defaults are set "implicit" in javascript.
     *
     * @return the implicit default value as a string
     */
    String getImplicitDefaultValue();

    boolean isHidden();
}