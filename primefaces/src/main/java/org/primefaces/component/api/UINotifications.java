/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import javax.faces.component.UIMessages;

public abstract class UINotifications extends UIMessages implements UINotification {

    public enum PropertyKeys {
        escape,
        severity,
        forType,
        forIgnores,
        skipDetailIfEqualsSummary
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, null);
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
    }

    public String getForType() {
        return (String) getStateHelper().eval(PropertyKeys.forType, null);
    }

    public void setForType(String forType) {
        getStateHelper().put(PropertyKeys.forType, forType);
    }

    public String getForIgnores() {
        return (String) getStateHelper().eval(PropertyKeys.forIgnores, null);
    }

    public void setForIgnores(String forIgnores) {
        getStateHelper().put(PropertyKeys.forIgnores, forIgnores);
    }

    @Override
    public boolean isSkipDetailIfEqualsSummary() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipDetailIfEqualsSummary, false);
    }

    public void setSkipDetailIfEqualsSummary(boolean skipDetailIfEqualsSummary) {
        getStateHelper().put(PropertyKeys.skipDetailIfEqualsSummary, skipDetailIfEqualsSummary);
    }

}
