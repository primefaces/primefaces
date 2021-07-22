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
package org.primefaces.model.menu;

public interface MenuElement {

    /**
     * Allows access the id property which is managed and auto-assigned by
     * callers like menu model implementations and used to identify the element.
     *
     * The property is unrelated to the {@code id} attribute in the generated
     * XHTML.
     *
     * @return the id property
     */
    String getId();

    /**
     * Sets the id property which is described in {@link #getId() }. You should
     * have a good reason to call this if you're not extending, but only using
     * PrimeFaces.
     *
     * The property is unrelated to the {@code id} attribute in the generated
     * XHTML.
     *
     * @param id the id value
     */
    void setId(String id);

    boolean isRendered();
}
