/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.commandlink;

import javax.faces.component.html.HtmlCommandLink;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Confirmable;
import org.primefaces.component.api.PrimeClientBehaviorHolder;


public abstract class CommandLinkBase extends HtmlCommandLink implements AjaxSource, Confirmable, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CommandLinkRenderer";

    public enum PropertyKeys {

        ajax,
        async,
        process,
        update,
        onstart,
        oncomplete,
        onerror,
        onsuccess,
        global,
        delay,
        timeout,
        partialSubmit,
        resetValues,
        ignoreAutoUpdate,
        validateClient,
        partialSubmitFilter,
        form,
        ariaLabel,
        ignoreComponentNotFound
    }

    public CommandLinkBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public boolean isAjax() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ajax, true);
    }

    public void setAjax(boolean ajax) {
        getStateHelper().put(PropertyKeys.ajax, ajax);
    }

    @Override
    public boolean isAsync() {
        return (Boolean) getStateHelper().eval(PropertyKeys.async, false);
    }

    public void setAsync(boolean async) {
        getStateHelper().put(PropertyKeys.async, async);
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

    public boolean isValidateClient() {
        return (Boolean) getStateHelper().eval(PropertyKeys.validateClient, false);
    }

    public void setValidateClient(boolean validateClient) {
        getStateHelper().put(PropertyKeys.validateClient, validateClient);
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

    public String getAriaLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaLabel, null);
    }

    public void setAriaLabel(String ariaLabel) {
        getStateHelper().put(PropertyKeys.ariaLabel, ariaLabel);
    }

    @Override
    public boolean isIgnoreComponentNotFound() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ignoreComponentNotFound, false);
    }

    public void setIgnoreComponentNotFound(boolean ignoreComponentNotFound) {
        getStateHelper().put(PropertyKeys.ignoreComponentNotFound, ignoreComponentNotFound);
    }
}