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
package org.primefaces.model.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.UIOutcomeTarget;

public class DefaultMenuItem implements MenuItem, UIOutcomeTarget, AjaxSource, Serializable {

    private String id;
    private String icon;
    private String iconPos;
    private String title;
    private boolean disabled;
    private String onclick;
    private String style;
    private String styleClass;
    private String url;
    private String target;
    private boolean ajax = true;
    private Object value;
    private String outcome;
    private boolean includeViewParams;
    private String fragment;
    private Map<String, List<String>> params;
    private String command;
    private boolean rendered = true;
    private String onstart;
    private String onerror;
    private String onsuccess;
    private String oncomplete;
    private String update;
    private String process;
    private boolean partialSubmit;
    private boolean global;
    private boolean async;
    private boolean partialSubmitSet;
    private boolean resetValues;
    private boolean resetValuesSet;
    private boolean ignoreAutoUpdate;
    private boolean immediate;
    private String delay;
    private int timeout;
    private boolean disableClientWindow;
    private String containerStyle;
    private String containerStyleClass;
    private String partialSubmitFilter;
    private String confirmationScript;
    private String form;
    private boolean escape = true;
    private String rel;

    public DefaultMenuItem() {
    }

    public DefaultMenuItem(Object value) {
        this.value = value;
    }

    public DefaultMenuItem(Object value, String icon) {
        this.value = value;
        this.icon = icon;
    }

    public DefaultMenuItem(Object value, String icon, String url) {
        this.value = value;
        this.icon = icon;
        this.url = url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getIconPos() {
        return iconPos;
    }

    public void setIconPos(String iconPos) {
        this.iconPos = iconPos;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @Override
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public String getHref() {
        return url;
    }

    public void setHref(String href) {
        this.url = href;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public boolean isAjax() {
        return ajax;
    }

    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isIncludeViewParams() {
        return includeViewParams;
    }

    public void setIncludeViewParams(boolean includeViewParams) {
        this.includeViewParams = includeViewParams;
    }

    @Override
    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    @Override
    public Map<String, List<String>> getParams() {
        return params;
    }

    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    @Override
    public void setParam(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (params == null) {
            params = new LinkedHashMap<String, List<String>>();
        }

        if (!params.containsKey(key)) {
            params.put(key, new ArrayList<String>());
        }

        params.get(key).add(value.toString());
    }

    @Override
    public boolean shouldRenderChildren() {
        return false;
    }

    @Override
    public List<UIComponent> getChildren() {
        return null;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    @Override
    public String getOnstart() {
        return onstart;
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    @Override
    public String getOnerror() {
        return onerror;
    }

    public void setOnerror(String onerror) {
        this.onerror = onerror;
    }

    @Override
    public String getOnsuccess() {
        return onsuccess;
    }

    public void setOnsuccess(String onsuccess) {
        this.onsuccess = onsuccess;
    }

    @Override
    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    @Override
    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    @Override
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    @Override
    public boolean isPartialSubmit() {
        return partialSubmit;
    }

    public void setPartialSubmit(boolean partialSubmit) {
        this.partialSubmit = partialSubmit;
        this.partialSubmitSet = true;
    }

    @Override
    public boolean isResetValues() {
        return resetValues;
    }

    public void setResetValues(boolean resetValues) {
        this.resetValues = resetValues;
        this.resetValuesSet = true;
    }

    @Override
    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public boolean isPartialSubmitSet() {
        return partialSubmitSet;
    }

    @Override
    public boolean isResetValuesSet() {
        return resetValuesSet;
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        return ignoreAutoUpdate;
    }

    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        this.ignoreAutoUpdate = ignoreAutoUpdate;
    }

    @Override
    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @Override
    public boolean isAjaxified() {
        return getUrl() == null && isAjax();
    }

    @Override
    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    @Override
    public boolean isDisableClientWindow() {
        return disableClientWindow;
    }

    public void setDisableClientWindow(boolean disableClientWindow) {
        this.disableClientWindow = disableClientWindow;
    }

    @Override
    public String getContainerStyle() {
        return containerStyle;
    }

    public void setContainerStyle(String containerStyle) {
        this.containerStyle = containerStyle;
    }

    @Override
    public String getContainerStyleClass() {
        return containerStyleClass;
    }

    public void setContainerStyleClass(String containerStyleClass) {
        this.containerStyleClass = containerStyleClass;
    }

    @Override
    public String getClientId() {
        return this.id;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getPartialSubmitFilter() {
        return partialSubmitFilter;
    }

    public void setPartialSubmitFilter(String partialSubmitFilter) {
        this.partialSubmitFilter = partialSubmitFilter;
    }

    @Override
    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String getConfirmationScript() {
        return this.confirmationScript;
    }

    @Override
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }

    @Override
    public boolean requiresConfirmation() {
        return this.confirmationScript != null;
    }

    @Override
    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    @Override
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

}
