/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.behavior.ajax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.event.AjaxBehaviorListener;

import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.component.api.AjaxSource;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js")
    })
public class AjaxBehavior extends AbstractBehavior implements AjaxSource {

    public final static String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";

    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    public enum PropertyKeys {
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
        skipChildren(Boolean.class);

        private final Class<?> expectedType;

        PropertyKeys(Class<?> expectedType) {
            this.expectedType = expectedType;
        }

        /**
         * Holds the type which ought to be passed to
         * {@link javax.faces.view.facelets.TagAttribute#getObject(javax.faces.view.facelets.FaceletContext, java.lang.Class) }
         * when creating the behavior.
         * @return the expectedType the expected object type
         */
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
        return eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        setLiteral(PropertyKeys.disabled, disabled);
    }

    @Override
    public boolean isAsync() {
        return eval(PropertyKeys.async, Boolean.FALSE);
    }

    public void setAsync(boolean async) {
        setLiteral(PropertyKeys.async, async);
    }

    @Override
    public boolean isGlobal() {
        return eval(PropertyKeys.global, Boolean.TRUE);
    }

    public void setGlobal(boolean global) {
        setLiteral(PropertyKeys.global, global);
    }

    @Override
    public String getOncomplete() {
        return eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
        setLiteral(PropertyKeys.oncomplete, oncomplete);
    }

    @Override
    public String getOnstart() {
        return eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        setLiteral(PropertyKeys.onstart, onstart);
    }

    @Override
    public String getOnsuccess() {
        return eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(String onsuccess) {
        setLiteral(PropertyKeys.onsuccess, onsuccess);
    }

    @Override
    public String getOnerror() {
        return eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
        setLiteral(PropertyKeys.onerror, onerror);
    }

    @Override
    public String getProcess() {
        return eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        setLiteral(PropertyKeys.process, process);
    }

    @Override
    public String getUpdate() {
        return eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        setLiteral(PropertyKeys.update, update);
    }

    @Override
    public String getDelay() {
        return eval(PropertyKeys.delay, null);
    }

    public void setDelay(String delay) {
        setLiteral(PropertyKeys.delay, delay);
    }

    public boolean isImmediate() {
        return eval(PropertyKeys.immediate, Boolean.FALSE);
    }

    public void setImmediate(Boolean immediate) {
        setLiteral(PropertyKeys.immediate, immediate);
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return eval(PropertyKeys.ignoreAutoUpdate, Boolean.FALSE);
    }

    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        setLiteral(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
    }

    @Override
    public boolean isPartialSubmit() {
        return eval(PropertyKeys.partialSubmit, Boolean.FALSE);
    }

    public void setPartialSubmit(boolean partialSubmit) {
        setLiteral(PropertyKeys.partialSubmit, partialSubmit);
    }

    @Override
    public boolean isResetValues() {
        return eval(PropertyKeys.resetValues, Boolean.FALSE);
    }

    public void setResetValues(boolean resetValues) {
        setLiteral(PropertyKeys.resetValues, resetValues);
    }

    public MethodExpression getListener() {
        return eval(PropertyKeys.listener, null);
    }

    public void setListener(MethodExpression listener) {
        setLiteral(PropertyKeys.listener, listener);
    }

    @Override
    public int getTimeout() {
        return eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int timeout) {
        setLiteral(PropertyKeys.timeout, timeout);
    }

    @Override
    public String getPartialSubmitFilter() {
        return eval(PropertyKeys.partialSubmitFilter, null);
    }

    public void setPartialSubmitFilter(String partialSubmitFilter) {
        setLiteral(PropertyKeys.partialSubmitFilter, partialSubmitFilter);
    }

    @Override
    public String getForm() {
        return eval(PropertyKeys.form, null);
    }

    public void setForm(String form) {
        setLiteral(PropertyKeys.form, form);
    }

    public boolean isSkipChildren() {
        return eval(PropertyKeys.skipChildren, Boolean.TRUE);
    }

    public void setSkipChildren(Boolean skipChildren) {
        setLiteral(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isImmediateSet() {
        return isAttributeSet(PropertyKeys.immediate);
    }

    @Override
    public boolean isPartialSubmitSet() {
        return isAttributeSet(PropertyKeys.partialSubmit);
    }

    @Override
    public boolean isResetValuesSet() {
        return isAttributeSet(PropertyKeys.resetValues);
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }


    @Override
    protected Enum<?>[] getAllProperties() {
        return PropertyKeys.values();
    }


    public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
        addBehaviorListener(listener);
    }

    public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
        removeBehaviorListener(listener);

    }
}
