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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;

/**
 * AjaxSource is the contract that needs to be implemented by components that fully implement all configuration options of PrimeFaces PPR
 */
public interface AjaxSource {

    @Property(description = "Client-side javascript callback to execute when ajax request is completed.")
    String getOncomplete();

    @Property(description = "Client-side javascript callback to execute before ajax request begins.")
    String getOnstart();

    @Property(description = "Client-side javascript callback to execute when ajax request succeeds.")
    String getOnsuccess();

    @Property(description = "Client-side javascript callback to execute when ajax request fails.")
    String getOnerror();

    @Property(description = "Component(s) to process in partial request.")
    String getProcess();

    @Property(description = "Component(s) to update with ajax.")
    String getUpdate();

    @Property(description = "Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.",
            defaultValue = "true")
    boolean isGlobal();

    @Property(description = "When set to true, ajax requests are not queued.")
    boolean isAsync();

    @Property(description = "Enables serialization of values belonging to the partially processed components only.")
    boolean isPartialSubmit();

    boolean isPartialSubmitSet();

    @Property(description = "Selector to use when partial submit is on, default is \":input\" to select all descendant inputs "
            + "of a partially processed components.")
    String getPartialSubmitFilter();

    @Property(description = "If true, local values of input components to be updated within the ajax request would be reset.")
    boolean isResetValues();

    boolean isResetValuesSet();

    @Property(description = "If true, components which autoUpdate=\"true\" will not be updated for this request. "
            + "If not specified, or the value is false, no such indication is made.")
    boolean isIgnoreAutoUpdate();

    boolean isAjaxified();

    @Property(description = "If true, unresolvable components (ComponentNotFoundException) referenced in the update/process attribute are ignored.")
    boolean isIgnoreComponentNotFound();

    @Property(description = "If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests "
            + "are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.")
    String getDelay();

    @Property(description = "Timeout in milliseconds for ajax request, whereas 0 means no timeout.", defaultValue = "0")
    int getTimeout();

    @Property(description = "Form to serialize for an ajax request.", implicitDefaultValue = "The enclosing form")
    String getForm();
}
