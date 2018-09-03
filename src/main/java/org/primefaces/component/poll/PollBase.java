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
package org.primefaces.component.poll;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class PollBase extends UIComponentBase implements AjaxSource, Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PollRenderer";

    public enum PropertyKeys {

        widgetVar,
        interval,
        update,
        listener,
        immediate,
        onstart,
        oncomplete,
        process,
        onerror,
        onsuccess,
        global,
        delay,
        timeout,
        async,
        autoStart,
        stop,
        partialSubmit,
        resetValues,
        ignoreAutoUpdate,
        partialSubmitFilter,
        form
    }

    public PollBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public int getInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.interval, 2);
    }

    public void setInterval(int interval) {
        getStateHelper().put(PropertyKeys.interval, interval);
    }

    @Override
    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public javax.el.MethodExpression getListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.listener, null);
    }

    public void setListener(javax.el.MethodExpression listener) {
        getStateHelper().put(PropertyKeys.listener, listener);
    }

    public boolean isImmediate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.immediate, false);
    }

    public void setImmediate(boolean immediate) {
        getStateHelper().put(PropertyKeys.immediate, immediate);
    }

    @Override
    public String getOnstart() {
        return (String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        getStateHelper().put(PropertyKeys.onstart, onstart);
    }

    @Override
    public String getOncomplete() {
        return (String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, oncomplete);
    }

    @Override
    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    @Override
    public String getOnerror() {
        return (String) getStateHelper().eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
        getStateHelper().put(PropertyKeys.onerror, onerror);
    }

    @Override
    public String getOnsuccess() {
        return (String) getStateHelper().eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(String onsuccess) {
        getStateHelper().put(PropertyKeys.onsuccess, onsuccess);
    }

    @Override
    public boolean isGlobal() {
        return (Boolean) getStateHelper().eval(PropertyKeys.global, true);
    }

    public void setGlobal(boolean global) {
        getStateHelper().put(PropertyKeys.global, global);
    }

    @Override
    public String getDelay() {
        return (String) getStateHelper().eval(PropertyKeys.delay, null);
    }

    public void setDelay(String delay) {
        getStateHelper().put(PropertyKeys.delay, delay);
    }

    @Override
    public int getTimeout() {
        return (Integer) getStateHelper().eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    @Override
    public boolean isAsync() {
        return (Boolean) getStateHelper().eval(PropertyKeys.async, false);
    }

    public void setAsync(boolean async) {
        getStateHelper().put(PropertyKeys.async, async);
    }

    public boolean isAutoStart() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoStart, true);
    }

    public void setAutoStart(boolean autoStart) {
        getStateHelper().put(PropertyKeys.autoStart, autoStart);
    }

    public boolean isStop() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stop, false);
    }

    public void setStop(boolean stop) {
        getStateHelper().put(PropertyKeys.stop, stop);
    }

    @Override
    public boolean isPartialSubmit() {
        return (Boolean) getStateHelper().eval(PropertyKeys.partialSubmit, false);
    }

    public void setPartialSubmit(boolean partialSubmit) {
        getStateHelper().put(PropertyKeys.partialSubmit, partialSubmit);
    }

    @Override
    public boolean isResetValues() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resetValues, false);
    }

    public void setResetValues(boolean resetValues) {
        getStateHelper().put(PropertyKeys.resetValues, resetValues);
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ignoreAutoUpdate, false);
    }

    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        getStateHelper().put(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}