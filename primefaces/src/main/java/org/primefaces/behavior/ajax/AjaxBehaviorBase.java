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
import org.primefaces.component.api.AjaxSource;

import jakarta.el.MethodExpression;

@FacesBehaviorBase
public abstract class AjaxBehaviorBase extends PrimeClientBehavior implements AjaxSource {

    @Property(description = "Disables ajax behavior.")
    public abstract boolean isDisabled();

    @Property(description = "Boolean value that determines the phaseId, when true actions are processed at apply_request_values, "
            + "when false at invoke_application phase.")
    public abstract boolean isImmediate();

    @Property(description = "Method to process in partial request.")
    public abstract MethodExpression getListener();

    @Property(description = "Containers components like, datatable, panel, tabview skip their children if the request owner is them. "
            + "For example, sort, page event of a datatable. As children are skipped, input values get lost, assume a case with a datatable and inputs "
            + "components in a column. "
            + "Sorting the column discards the changes and data is sorted according to original value. "
            + "Setting skipChildren to false, enabled input values to update the value and sorting to be happened with user values.",
            defaultValue = "true")
    public abstract boolean isSkipChildren();
}
