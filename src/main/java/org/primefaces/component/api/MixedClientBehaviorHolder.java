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
package org.primefaces.component.api;

import java.util.Collection;

/**
 * MixedClientBehaviorHolder is implemented by input components that have both obstrusive and non-obstrusive
 * client behavior events.  Components such as DataTable are not input components and thus do not need this interface
 * as it will never support obtrusive events.
 * <p>
 * Obtrusive events are rendered directly in the HTML DOM such as onclick="alert('test');"
 * </p>
 * <p>
 * Unobtrusive events are fired by the JS widget and need to be rendered as a widget parameter. e.g. DataTable "sort".
 * </p>
 */
public interface MixedClientBehaviorHolder {

    /**
     * Gets the collection of unobtrusive event names.
     *
     * @return the collection of unobtrusive event names.
     */
    Collection<String> getUnobstrusiveEventNames();
}
