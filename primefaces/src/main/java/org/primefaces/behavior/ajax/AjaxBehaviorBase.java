/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.behavior.ajax;

import org.primefaces.cdk.api.FacesBehaviorBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;

import jakarta.el.MethodExpression;

@FacesBehaviorBase
public abstract class AjaxBehaviorBase extends PrimeClientBehavior {

    @Property(description = "Disables ajax behavior.")
    public abstract boolean isDisabled();

    @Property(description = "When set to true, ajax requests are not queued.")
    public abstract boolean isAsync();

    @Property(description = "Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus.",
            defaultValue = "true")
    public abstract boolean isGlobal();

    @Property(description = "Client-side javascript callback to execute when ajax request is completed.")
    public abstract String getOncomplete();

    @Property(description = "Client-side javascript callback to execute before ajax request begins.")
    public abstract String getOnstart();

    @Property(description = "Client-side javascript callback to execute when ajax request succeeds.")
    public abstract String getOnsuccess();

    @Property(description = "Client-side javascript callback to execute when ajax request fails.")
    public abstract String getOnerror();

    @Property(description = "Component(s) to process in partial request.")
    public abstract String getProcess();

    @Property(description = "Component(s) to update with ajax.")
    public abstract String getUpdate();

    @Property(description = "If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests "
            + "are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used.")
    public abstract String getDelay();

    @Property(description = "Boolean value that determines the phaseId, when true actions are processed at apply_request_values, "
            + "when false at invoke_application phase.")
    public abstract boolean isImmediate();

    @Property(description = "If true, components which autoUpdate=\"true\" will not be updated for this request. "
            + "If not specified, or the value is false, no such indication is made.")
    public abstract boolean isIgnoreAutoUpdate();

    @Property(description = "Enables serialization of values belonging to the partially processed components only.")
    public abstract boolean isPartialSubmit();

    @Property(description = "If true, local values of input components to be updated within the ajax request would be reset.")
    public abstract boolean isResetValues();

    @Property(description = "Method to process in partial request.")
    public abstract MethodExpression getListener();

    @Property(description = "Timeout in milliseconds for ajax request, whereas 0 means no timeout.", defaultValue = "0")
    public abstract int getTimeout();

    @Property(description = "Selector to use when partial submit is on, default is \":input\" to select all descendant inputs "
            + "of a partially processed components.")
    public abstract String getPartialSubmitFilter();

    @Property(description = "Form to serialize for an ajax request.", implicitDefaultValue = "The enclosing form")
    public abstract String getForm();

    @Property(description = "Containers components like, datatable, panel, tabview skip their children if the request owner is them. "
            + "For example, sort, page event of a datatable. As children are skipped, input values get lost, assume a case with a datatable and inputs "
            + "components in a column. "
            + "Sorting the column discards the changes and data is sorted according to original value. "
            + "Setting skipChildren to false, enabled input values to update the value and sorting to be happened with user values.",
            defaultValue = "true")
    public abstract boolean isSkipChildren();

    @Property(description = "If true, unresolvable components (ComponentNotFoundException) referenced in the update/process attribute are ignored.")
    public abstract boolean isIgnoreComponentNotFound();
}
