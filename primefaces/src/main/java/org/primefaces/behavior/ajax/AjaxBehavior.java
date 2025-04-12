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

import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.behavior.base.BehaviorAttribute;
import org.primefaces.component.api.AjaxSource;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import jakarta.el.MethodExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.behavior.ClientBehaviorHint;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.event.AjaxBehaviorListener;

@FacesBehavior(AjaxBehavior.BEHAVIOR_ID)
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
public class AjaxBehavior extends AbstractBehavior implements AjaxSource {

    public static final String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";

    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    public enum PropertyKeys implements BehaviorAttribute {
        update(String.class),
        process(String.class),
        global(Boolean.class),
        async(Boolean.class),
        oncomplete(String.class),
        onerror(String.class),
        onsuccess(String.class),
        onstart(String.class),
        listener(MethodExpression.class),
        immediate(Boolean.class),
        disabled(Boolean.class),
        partialSubmit(Boolean.class),
        resetValues(Boolean.class),
        ignoreAutoUpdate(Boolean.class),
        delay(String.class),
        timeout(Integer.class),
        partialSubmitFilter(String.class),
        form(String.class),
        skipChildren(Boolean.class),
        ignoreComponentNotFound(Boolean.class);

        private final Class<?> expectedType;

        PropertyKeys(Class<?> expectedType) {
            this.expectedType = expectedType;
        }

        @Override
        public Class<?> getExpectedType() {
            return expectedType;
        }
    }

    public AjaxBehavior() {
        super();
    }

    @Override
    public String getRendererType() {
        return "org.primefaces.component.AjaxBehaviorRenderer";
    }

    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }

    public boolean isDisabled() {
        return (boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    @Override
    public boolean isAsync() {
        return (boolean) getStateHelper().eval(PropertyKeys.async, false);
    }

    public void setAsync(boolean async) {
        getStateHelper().put(PropertyKeys.async, async);
    }

    @Override
    public boolean isGlobal() {
        return (boolean) getStateHelper().eval(PropertyKeys.global, true);
    }

    public void setGlobal(boolean global) {
        getStateHelper().put(PropertyKeys.global, global);
    }

    @Override
    public String getOncomplete() {
        return (String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, oncomplete);
    }

    @Override
    public String getOnstart() {
        return (String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        getStateHelper().put(PropertyKeys.onstart, onstart);
    }

    @Override
    public String getOnsuccess() {
        return (String) getStateHelper().eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(String onsuccess) {
        getStateHelper().put(PropertyKeys.onsuccess, onsuccess);
    }

    @Override
    public String getOnerror() {
        return (String) getStateHelper().eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
        getStateHelper().put(PropertyKeys.onerror, onerror);
    }

    @Override
    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    @Override
    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    @Override
    public String getDelay() {
        return (String) getStateHelper().eval(PropertyKeys.delay, null);
    }

    public void setDelay(String delay) {
        getStateHelper().put(PropertyKeys.delay, delay);
    }

    public boolean isImmediate() {
        return (boolean) getStateHelper().eval(PropertyKeys.immediate, Boolean.FALSE);
    }

    public void setImmediate(Boolean immediate) {
        getStateHelper().put(PropertyKeys.immediate, immediate);
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return (boolean) getStateHelper().eval(PropertyKeys.ignoreAutoUpdate, false);
    }

    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        getStateHelper().put(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
    }

    @Override
    public boolean isPartialSubmit() {
        return (boolean) getStateHelper().eval(PropertyKeys.partialSubmit, false);
    }

    public void setPartialSubmit(boolean partialSubmit) {
        getStateHelper().put(PropertyKeys.partialSubmit, partialSubmit);
    }

    @Override
    public boolean isResetValues() {
        return (boolean) getStateHelper().eval(PropertyKeys.resetValues, false);
    }

    public void setResetValues(boolean resetValues) {
        getStateHelper().put(PropertyKeys.resetValues, resetValues);
    }

    public MethodExpression getListener() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.listener, null);
    }

    public void setListener(MethodExpression listener) {
        getStateHelper().put(PropertyKeys.listener, listener);
    }

    @Override
    public int getTimeout() {
        return (int) getStateHelper().eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    @Override
    public String getPartialSubmitFilter() {
        return (String) getStateHelper().eval(PropertyKeys.partialSubmitFilter, null);
    }

    public void setPartialSubmitFilter(String partialSubmitFilter) {
        getStateHelper().put(PropertyKeys.partialSubmitFilter, partialSubmitFilter);
    }

    @Override
    public String getForm() {
        return (String) getStateHelper().eval(PropertyKeys.form, null);
    }

    public void setForm(String form) {
        getStateHelper().put(PropertyKeys.form, form);
    }

    public boolean isSkipChildren() {
        return (boolean) getStateHelper().eval(PropertyKeys.skipChildren, true);
    }

    public void setSkipChildren(Boolean skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isImmediateSet() {
        return getStateHelper().eval(PropertyKeys.immediate, null) != null;
    }

    @Override
    public boolean isPartialSubmitSet() {
        return getStateHelper().eval(PropertyKeys.partialSubmit, null) != null;
    }

    @Override
    public boolean isResetValuesSet() {
        return getStateHelper().eval(PropertyKeys.resetValues, null) != null;
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }

    @Override
    public boolean isIgnoreComponentNotFound() {
        return (boolean) getStateHelper().eval(PropertyKeys.ignoreComponentNotFound, false);
    }

    public void setIgnoreComponentNotFound(Boolean ignoreComponentNotFound) {
        getStateHelper().put(PropertyKeys.ignoreComponentNotFound, ignoreComponentNotFound);
    }

    @Override
    protected BehaviorAttribute[] getAllAttributes() {
        return PropertyKeys.values();
    }


    public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
        addBehaviorListener(listener);
    }

    public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
        removeBehaviorListener(listener);

    }
}
