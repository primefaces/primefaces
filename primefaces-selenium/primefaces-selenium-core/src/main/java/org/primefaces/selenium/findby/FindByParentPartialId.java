/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.selenium.findby;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.support.FindBy;

/**
 * Alternative {@link FindBy} annotation, which should only be used in page fragments. It searches by id, starting from the parent element, based on the id of
 * the parent concatenated with {@link #value()}. Sometimes, for example with <code>appendTo="..."</code>, the child element is moved to somewhere else in the
 * DOM. In this case you have to set {@link #searchFromRoot()} to <code>true</code>.
 * <p>
 * NOTE: If a sub-element under the parent does not have an ID but only a name you can use the name attribute to specify it and it will look for the name="xx"
 * instead of ID.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindByParentPartialId {

    /**
     * ID of the component under the parent to find.
     *
     * @return the ID
     */
    String value();

    /**
     * Name attribute to find instead of using the ID of the component.
     *
     * @return the name attribute or default "".
     */
    String name() default "";

    boolean searchFromRoot() default false;
}
