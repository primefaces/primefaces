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

    private DefaultMenuItem() {
        // NOOP
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
        url = href;
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
        partialSubmitSet = true;
    }

    @Override
    public boolean isResetValues() {
        return resetValues;
    }

    public void setResetValues(boolean resetValues) {
        this.resetValues = resetValues;
        resetValuesSet = true;
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
        return id;
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
        return confirmationScript;
    }

    @Override
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }

    @Override
    public boolean requiresConfirmation() {
        return confirmationScript != null;
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private DefaultMenuItem defaultMenuItem;

        private Builder() {
            defaultMenuItem = new DefaultMenuItem();
        }

        public Builder withId(String id) {
            defaultMenuItem.setId(id);
            return this;
        }

        public Builder withIcon(String icon) {
            defaultMenuItem.setIcon(icon);
            return this;
        }

        public Builder withIconPos(String iconPos) {
            defaultMenuItem.setIconPos(iconPos);
            return this;
        }

        public Builder withTitle(String title) {
            defaultMenuItem.setTitle(title);
            return this;
        }

        public Builder withDisabled(boolean disabled) {
            defaultMenuItem.setDisabled(disabled);
            return this;
        }

        public Builder withOnclick(String onclick) {
            defaultMenuItem.setOnclick(onclick);
            return this;
        }

        public Builder withStyle(String style) {
            defaultMenuItem.setStyle(style);
            return this;
        }

        public Builder withStyleClass(String styleClass) {
            defaultMenuItem.setStyleClass(styleClass);
            return this;
        }

        public Builder withUrl(String url) {
            defaultMenuItem.setUrl(url);
            return this;
        }

        public Builder withTarget(String target) {
            defaultMenuItem.setTarget(target);
            return this;
        }

        public Builder withAjax(boolean ajax) {
            defaultMenuItem.setAjax(ajax);
            return this;
        }

        public Builder withValue(Object value) {
            defaultMenuItem.setValue(value);
            return this;
        }

        public Builder withOutcome(String outcome) {
            defaultMenuItem.setOutcome(outcome);
            return this;
        }

        public Builder withIncludeViewParams(boolean includeViewParams) {
            defaultMenuItem.setIncludeViewParams(includeViewParams);
            return this;
        }

        public Builder withFragment(String fragment) {
            defaultMenuItem.setFragment(fragment);
            return this;
        }

        public Builder withParam(String key, Object value) {
            defaultMenuItem.setParam(key, value);
            return this;
        }

        public Builder withParams(Map<String, List<String>> params) {
            defaultMenuItem.setParams(params);
            return this;
        }

        public Builder withCommand(String command) {
            defaultMenuItem.setCommand(command);
            return this;
        }

        public Builder withRendered(boolean rendered) {
            defaultMenuItem.setRendered(rendered);
            return this;
        }

        public Builder withOnstart(String onstart) {
            defaultMenuItem.setOnstart(onstart);
            return this;
        }

        public Builder withOnerror(String onerror) {
            defaultMenuItem.setOnerror(onerror);
            return this;
        }

        public Builder withOnsuccess(String onsuccess) {
            defaultMenuItem.setOnsuccess(onsuccess);
            return this;
        }

        public Builder withOncomplete(String oncomplete) {
            defaultMenuItem.setOncomplete(oncomplete);
            return this;
        }

        public Builder withUpdate(String update) {
            defaultMenuItem.setUpdate(update);
            return this;
        }

        public Builder withProcess(String process) {
            defaultMenuItem.setProcess(process);
            return this;
        }

        public Builder withPartialSubmit(boolean partialSubmit) {
            defaultMenuItem.setPartialSubmit(partialSubmit);
            return this;
        }

        public Builder withGlobal(boolean global) {
            defaultMenuItem.setGlobal(global);
            return this;
        }

        public Builder withAsync(boolean async) {
            defaultMenuItem.setAsync(async);
            return this;
        }

        public Builder withResetValues(boolean resetValues) {
            defaultMenuItem.setResetValues(resetValues);
            return this;
        }

        public Builder withIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
            defaultMenuItem.setIgnoreAutoUpdate(ignoreAutoUpdate);
            return this;
        }

        public Builder withImmediate(boolean immediate) {
            defaultMenuItem.setImmediate(immediate);
            return this;
        }

        public Builder withDelay(String delay) {
            defaultMenuItem.setDelay(delay);
            return this;
        }

        public Builder withTimeout(int timeout) {
            defaultMenuItem.setTimeout(timeout);
            return this;
        }

        public Builder withDisableClientWindow(boolean disableClientWindow) {
            defaultMenuItem.setDisableClientWindow(disableClientWindow);
            return this;
        }

        public Builder withContainerStyle(String containerStyle) {
            defaultMenuItem.setContainerStyle(containerStyle);
            return this;
        }

        public Builder withContainerStyleClass(String containerStyleClass) {
            defaultMenuItem.setContainerStyleClass(containerStyleClass);
            return this;
        }

        public Builder withPartialSubmitFilter(String partialSubmitFilter) {
            defaultMenuItem.setPartialSubmitFilter(partialSubmitFilter);
            return this;
        }

        public Builder withConfirmationScript(String confirmationScript) {
            defaultMenuItem.setConfirmationScript(confirmationScript);
            return this;
        }

        public Builder withForm(String form) {
            defaultMenuItem.setForm(form);
            return this;
        }

        public Builder withEscape(boolean escape) {
            defaultMenuItem.setEscape(escape);
            return this;
        }

        public Builder withRel(String rel) {
            defaultMenuItem.setRel(rel);
            return this;
        }

        public DefaultMenuItem build() {
            return defaultMenuItem;
        }
    }
}
