/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import org.primefaces.cdk.api.Property;

/**
 * AjaxSource is the contract that needs to be implemented by components that fully implement all configuration options of PrimeFaces PPR,
 * as their request is built by the core (<code>PrimeFaces.ab</code>). Components that build the request on their own
 * only implement {@link AjaxAware}.
 */
public interface AjaxSource extends AjaxAware {

    @Property(description = "When set to true, ajax requests are not queued.")
    boolean isAsync();

    @Property(description = "Enables serialization of values belonging to the partially processed components only.")
    boolean isPartialSubmit();

    boolean isPartialSubmitSet();

    @Property(description = "Selector to use when partial submit is on, default is \":input\" to select all descendant inputs "
            + "of a partially processed components.")
    String getPartialSubmitFilter();

    boolean isAjaxified();

    @Property(description = "If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests "
            + "are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.")
    String getDelay();
}
