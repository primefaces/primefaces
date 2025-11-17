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

import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;

import jakarta.el.MethodExpression;

public abstract class AjaxBehaviorBase extends PrimeClientBehavior {

    @Property
    public abstract boolean isDisabled();

    @Property
    public abstract boolean isAsync();

    @Property(defaultValue = "true")
    public abstract boolean isGlobal();

    @Property
    public abstract String getOncomplete();

    @Property
    public abstract String getOnstart();

    @Property
    public abstract String getOnsuccess();

    @Property
    public abstract String getOnerror();

    @Property
    public abstract String getProcess();

    @Property
    public abstract String getUpdate();

    @Property
    public abstract String getDelay();

    @Property
    public abstract boolean isImmediate();

    @Property
    public abstract boolean isIgnoreAutoUpdate();

    @Property
    public abstract boolean isPartialSubmit();

    @Property
    public abstract boolean isResetValues();

    @Property
    public abstract MethodExpression getListener();

    @Property(defaultValue = "0")
    public abstract int getTimeout();

    @Property
    public abstract String getPartialSubmitFilter();

    @Property
    public abstract String getForm();

    @Property(defaultValue = "true")
    public abstract boolean isSkipChildren();

    @Property
    public abstract boolean isIgnoreComponentNotFound();
}
