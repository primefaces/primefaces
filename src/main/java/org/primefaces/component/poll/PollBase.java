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

import org.primefaces.util.ComponentUtils;


abstract class PollBase extends UIComponentBase implements org.primefaces.component.api.AjaxSource, org.primefaces.component.api.Widget {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public int getInterval() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.interval, 2);
    }

    public void setInterval(int _interval) {
        getStateHelper().put(PropertyKeys.interval, _interval);
    }

    @Override
    public java.lang.String getUpdate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(java.lang.String _update) {
        getStateHelper().put(PropertyKeys.update, _update);
    }

    public javax.el.MethodExpression getListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.listener, null);
    }

    public void setListener(javax.el.MethodExpression _listener) {
        getStateHelper().put(PropertyKeys.listener, _listener);
    }

    public boolean isImmediate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.immediate, false);
    }

    public void setImmediate(boolean _immediate) {
        getStateHelper().put(PropertyKeys.immediate, _immediate);
    }

    @Override
    public java.lang.String getOnstart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(java.lang.String _onstart) {
        getStateHelper().put(PropertyKeys.onstart, _onstart);
    }

    @Override
    public java.lang.String getOncomplete() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(java.lang.String _oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
    }

    @Override
    public java.lang.String getProcess() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(java.lang.String _process) {
        getStateHelper().put(PropertyKeys.process, _process);
    }

    @Override
    public java.lang.String getOnerror() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(java.lang.String _onerror) {
        getStateHelper().put(PropertyKeys.onerror, _onerror);
    }

    @Override
    public java.lang.String getOnsuccess() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(java.lang.String _onsuccess) {
        getStateHelper().put(PropertyKeys.onsuccess, _onsuccess);
    }

    @Override
    public boolean isGlobal() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.global, true);
    }

    public void setGlobal(boolean _global) {
        getStateHelper().put(PropertyKeys.global, _global);
    }

    @Override
    public java.lang.String getDelay() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.delay, null);
    }

    public void setDelay(java.lang.String _delay) {
        getStateHelper().put(PropertyKeys.delay, _delay);
    }

    @Override
    public int getTimeout() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int _timeout) {
        getStateHelper().put(PropertyKeys.timeout, _timeout);
    }

    @Override
    public boolean isAsync() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.async, false);
    }

    public void setAsync(boolean _async) {
        getStateHelper().put(PropertyKeys.async, _async);
    }

    public boolean isAutoStart() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoStart, true);
    }

    public void setAutoStart(boolean _autoStart) {
        getStateHelper().put(PropertyKeys.autoStart, _autoStart);
    }

    public boolean isStop() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stop, false);
    }

    public void setStop(boolean _stop) {
        getStateHelper().put(PropertyKeys.stop, _stop);
    }

    @Override
    public boolean isPartialSubmit() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.partialSubmit, false);
    }

    public void setPartialSubmit(boolean _partialSubmit) {
        getStateHelper().put(PropertyKeys.partialSubmit, _partialSubmit);
    }

    @Override
    public boolean isResetValues() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resetValues, false);
    }

    public void setResetValues(boolean _resetValues) {
        getStateHelper().put(PropertyKeys.resetValues, _resetValues);
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ignoreAutoUpdate, false);
    }

    public void setIgnoreAutoUpdate(boolean _ignoreAutoUpdate) {
        getStateHelper().put(PropertyKeys.ignoreAutoUpdate, _ignoreAutoUpdate);
    }

    @Override
    public java.lang.String getPartialSubmitFilter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.partialSubmitFilter, null);
    }

    public void setPartialSubmitFilter(java.lang.String _partialSubmitFilter) {
        getStateHelper().put(PropertyKeys.partialSubmitFilter, _partialSubmitFilter);
    }

    @Override
    public java.lang.String getForm() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.form, null);
    }

    public void setForm(java.lang.String _form) {
        getStateHelper().put(PropertyKeys.form, _form);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}