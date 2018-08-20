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
package org.primefaces.component.menuitem;

import javax.faces.component.UICommand;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Confirmable;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.model.menu.MenuItem;


abstract class UIMenuItemBase extends UICommand implements AjaxSource, UIOutcomeTarget, MenuItem, Confirmable, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        url,
        target,
        style,
        styleClass,
        onclick,
        update,
        process,
        onstart,
        disabled,
        oncomplete,
        onerror,
        onsuccess,
        global,
        delay,
        timeout,
        async,
        ajax,
        icon,
        iconPos,
        partialSubmit,
        resetValues,
        ignoreAutoUpdate,
        title,
        outcome,
        includeViewParams,
        fragment,
        disableClientWindow,
        containerStyle,
        containerStyleClass,
        partialSubmitFilter,
        form,
        escape,
        rel
    }

    public UIMenuItemBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String getUrl() {
        return (String) getStateHelper().eval(PropertyKeys.url, null);
    }

    public void setUrl(String url) {
        getStateHelper().put(PropertyKeys.url, url);
    }

    @Override
    public String getTarget() {
        return (String) getStateHelper().eval(PropertyKeys.target, null);
    }

    public void setTarget(String target) {
        getStateHelper().put(PropertyKeys.target, target);
    }

    @Override
    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    @Override
    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    @Override
    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public String getOnclick() {
        return (String) getStateHelper().eval(PropertyKeys.onclick, null);
    }

    public void setOnclick(String onclick) {
        getStateHelper().put(PropertyKeys.onclick, onclick);
    }

    @Override
    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    @Override
    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    @Override
    public String getOnstart() {
        return (String) getStateHelper().eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
        getStateHelper().put(PropertyKeys.onstart, onstart);
    }

    @Override
    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
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
    public boolean isAsync() {
        return (Boolean) getStateHelper().eval(PropertyKeys.async, false);
    }

    public void setAsync(boolean async) {
        getStateHelper().put(PropertyKeys.async, async);
    }

    @Override
    public boolean isAjax() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ajax, true);
    }

    public void setAjax(boolean ajax) {
        getStateHelper().put(PropertyKeys.ajax, ajax);
    }

    @Override
    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    @Override
    public String getIconPos() {
        return (String) getStateHelper().eval(PropertyKeys.iconPos, "right");
    }

    public void setIconPos(String iconPos) {
        getStateHelper().put(PropertyKeys.iconPos, iconPos);
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
    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    @Override
    public String getOutcome() {
        return (String) getStateHelper().eval(PropertyKeys.outcome, null);
    }

    public void setOutcome(String outcome) {
        getStateHelper().put(PropertyKeys.outcome, outcome);
    }

    @Override
    public boolean isIncludeViewParams() {
        return (Boolean) getStateHelper().eval(PropertyKeys.includeViewParams, false);
    }

    public void setIncludeViewParams(boolean includeViewParams) {
        getStateHelper().put(PropertyKeys.includeViewParams, includeViewParams);
    }

    @Override
    public String getFragment() {
        return (String) getStateHelper().eval(PropertyKeys.fragment, null);
    }

    public void setFragment(String fragment) {
        getStateHelper().put(PropertyKeys.fragment, fragment);
    }

    @Override
    public boolean isDisableClientWindow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableClientWindow, false);
    }

    public void setDisableClientWindow(boolean disableClientWindow) {
        getStateHelper().put(PropertyKeys.disableClientWindow, disableClientWindow);
    }

    @Override
    public String getContainerStyle() {
        return (String) getStateHelper().eval(PropertyKeys.containerStyle, null);
    }

    public void setContainerStyle(String containerStyle) {
        getStateHelper().put(PropertyKeys.containerStyle, containerStyle);
    }

    @Override
    public String getContainerStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.containerStyleClass, null);
    }

    public void setContainerStyleClass(String containerStyleClass) {
        getStateHelper().put(PropertyKeys.containerStyleClass, containerStyleClass);
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
    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getRel() {
        return (String) getStateHelper().eval(PropertyKeys.rel, null);
    }

    public void setRel(String rel) {
        getStateHelper().put(PropertyKeys.rel, rel);
    }

}