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
package org.primefaces.behavior.ajax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.event.AjaxBehaviorListener;

import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.behavior.base.BehaviorAttribute;
import org.primefaces.component.api.AjaxSource;

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
        return attributeHandler.eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        attributeHandler.put(PropertyKeys.disabled, disabled);
    }

    @Override
    public boolean isAsync() {
        return attributeHandler.eval(PropertyKeys.async, Boolean.FALSE);
    }

    public void setAsync(boolean async) {
        attributeHandler.put(PropertyKeys.async, async);
    }

    @Override
    public boolean isGlobal() {
        return attributeHandler.eval(PropertyKeys.global, Boolean.TRUE);
    }

    public void setGlobal(boolean global) {
        attributeHandler.put(PropertyKeys.global, global);
    }

    @Override
    public String getOncomplete() {
        return attributeHandler.eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
        attributeHandler.put(PropertyKeys.oncomplete, oncomplete);
    }

    @Override
    public String getOnstart() {
        return attributeHandler.eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        attributeHandler.put(PropertyKeys.onstart, onstart);
    }

    @Override
    public String getOnsuccess() {
        return attributeHandler.eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(String onsuccess) {
        attributeHandler.put(PropertyKeys.onsuccess, onsuccess);
    }

    @Override
    public String getOnerror() {
        return attributeHandler.eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
        attributeHandler.put(PropertyKeys.onerror, onerror);
    }

    @Override
    public String getProcess() {
        return attributeHandler.eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        attributeHandler.put(PropertyKeys.process, process);
    }

    @Override
    public String getUpdate() {
        return attributeHandler.eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        attributeHandler.put(PropertyKeys.update, update);
    }

    @Override
    public String getDelay() {
        return attributeHandler.eval(PropertyKeys.delay, null);
    }

    public void setDelay(String delay) {
        attributeHandler.put(PropertyKeys.delay, delay);
    }

    public boolean isImmediate() {
        return attributeHandler.eval(PropertyKeys.immediate, Boolean.FALSE);
    }

    public void setImmediate(Boolean immediate) {
        attributeHandler.put(PropertyKeys.immediate, immediate);
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return attributeHandler.eval(PropertyKeys.ignoreAutoUpdate, Boolean.FALSE);
    }

    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        attributeHandler.put(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
    }

    @Override
    public boolean isPartialSubmit() {
        return attributeHandler.eval(PropertyKeys.partialSubmit, Boolean.FALSE);
    }

    public void setPartialSubmit(boolean partialSubmit) {
        attributeHandler.put(PropertyKeys.partialSubmit, partialSubmit);
    }

    @Override
    public boolean isResetValues() {
        return attributeHandler.eval(PropertyKeys.resetValues, Boolean.FALSE);
    }

    public void setResetValues(boolean resetValues) {
        attributeHandler.put(PropertyKeys.resetValues, resetValues);
    }

    public MethodExpression getListener() {
        return attributeHandler.eval(PropertyKeys.listener, null);
    }

    public void setListener(MethodExpression listener) {
        attributeHandler.put(PropertyKeys.listener, listener);
    }

    @Override
    public int getTimeout() {
        return attributeHandler.eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int timeout) {
        attributeHandler.put(PropertyKeys.timeout, timeout);
    }

    @Override
    public String getPartialSubmitFilter() {
        return attributeHandler.eval(PropertyKeys.partialSubmitFilter, null);
    }

    public void setPartialSubmitFilter(String partialSubmitFilter) {
        attributeHandler.put(PropertyKeys.partialSubmitFilter, partialSubmitFilter);
    }

    @Override
    public String getForm() {
        return attributeHandler.eval(PropertyKeys.form, null);
    }

    public void setForm(String form) {
        attributeHandler.put(PropertyKeys.form, form);
    }

    public boolean isSkipChildren() {
        return attributeHandler.eval(PropertyKeys.skipChildren, Boolean.TRUE);
    }

    public void setSkipChildren(Boolean skipChildren) {
        attributeHandler.put(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isImmediateSet() {
        return attributeHandler.isAttributeSet(PropertyKeys.immediate);
    }

    @Override
    public boolean isPartialSubmitSet() {
        return attributeHandler.isAttributeSet(PropertyKeys.partialSubmit);
    }

    @Override
    public boolean isResetValuesSet() {
        return attributeHandler.isAttributeSet(PropertyKeys.resetValues);
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }

    @Override
    public boolean isIgnoreComponentNotFound() {
        return attributeHandler.eval(PropertyKeys.ignoreComponentNotFound, Boolean.FALSE);
    }

    public void setIgnoreComponentNotFound(Boolean ignoreComponentNotFound) {
        attributeHandler.put(PropertyKeys.ignoreComponentNotFound, ignoreComponentNotFound);
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
