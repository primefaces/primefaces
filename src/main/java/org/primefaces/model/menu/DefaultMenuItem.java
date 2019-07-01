/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.util.SerializableFunction;

public class DefaultMenuItem implements MenuItem, UIOutcomeTarget, AjaxSource, Serializable {

    private static final long serialVersionUID = 1L;

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
    private SerializableFunction<MenuItem, String> function;
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

    /**
     * Creates a new menu item without value.
     */
    public DefaultMenuItem() {
        // NOOP
    }

    /**
     * Creates a new menu item with the specified
     * @param value the value of the item used as label
     */
    public DefaultMenuItem(Object value) {
        this.value = value;
    }

    /**
     * Creates a new menu item with the specified
     * @param value the value of the item used as label
     * @param icon the icon to be displayed next to the label
     */
    public DefaultMenuItem(Object value, String icon) {
        this.value = value;
        this.icon = icon;
    }

    /**
     * Creates a menu item with the specified
     * @param value the value of the item used as label
     * @param icon the icon to be displayed next to the label
     * @param url a URL to redirect to after the menu item has been clicked
     * (specifying a {@code url} which is not {@code null} causes
     * {@code command} to be ignored) (another form of redirection is provided
     * by the {@code outcome} property)
     */
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
            params = new LinkedHashMap<>();
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
    public SerializableFunction<MenuItem, String> getFunction() {
        return function;
    }

    public void setFunction(SerializableFunction<MenuItem, String> function) {
        this.function = function;
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

        private DefaultMenuItem menuItem;

        private Builder() {
            menuItem = new DefaultMenuItem();
        }

        public Builder id(String id) {
            menuItem.setId(id);
            return this;
        }

        public Builder icon(String icon) {
            menuItem.setIcon(icon);
            return this;
        }

        public Builder iconPos(String iconPos) {
            menuItem.setIconPos(iconPos);
            return this;
        }

        public Builder title(String title) {
            menuItem.setTitle(title);
            return this;
        }

        public Builder disabled(boolean disabled) {
            menuItem.setDisabled(disabled);
            return this;
        }

        public Builder onclick(String onclick) {
            menuItem.setOnclick(onclick);
            return this;
        }

        public Builder style(String style) {
            menuItem.setStyle(style);
            return this;
        }

        public Builder styleClass(String styleClass) {
            menuItem.setStyleClass(styleClass);
            return this;
        }

        public Builder url(String url) {
            menuItem.setUrl(url);
            return this;
        }

        public Builder target(String target) {
            menuItem.setTarget(target);
            return this;
        }

        public Builder ajax(boolean ajax) {
            menuItem.setAjax(ajax);
            return this;
        }

        public Builder value(Object value) {
            menuItem.setValue(value);
            return this;
        }

        public Builder outcome(String outcome) {
            menuItem.setOutcome(outcome);
            return this;
        }

        public Builder includeViewParams(boolean includeViewParams) {
            menuItem.setIncludeViewParams(includeViewParams);
            return this;
        }

        public Builder fragment(String fragment) {
            menuItem.setFragment(fragment);
            return this;
        }

        public Builder params(Map<String, List<String>> params) {
            menuItem.setParams(params);
            return this;
        }

        public Builder command(String command) {
            menuItem.setCommand(command);
            return this;
        }

        public Builder function(SerializableFunction<MenuItem, String> function) {
            menuItem.setFunction(function);
            return this;
        }

        public Builder rendered(boolean rendered) {
            menuItem.setRendered(rendered);
            return this;
        }

        public Builder onstart(String onstart) {
            menuItem.setOnstart(onstart);
            return this;
        }

        public Builder onerror(String onerror) {
            menuItem.setOnerror(onerror);
            return this;
        }

        public Builder onsuccess(String onsuccess) {
            menuItem.setOnsuccess(onsuccess);
            return this;
        }

        public Builder oncomplete(String oncomplete) {
            menuItem.setOncomplete(oncomplete);
            return this;
        }

        public Builder update(String update) {
            menuItem.setUpdate(update);
            return this;
        }

        public Builder process(String process) {
            menuItem.setProcess(process);
            return this;
        }

        public Builder partialSubmit(boolean partialSubmit) {
            menuItem.setPartialSubmit(partialSubmit);
            return this;
        }

        public Builder global(boolean global) {
            menuItem.setGlobal(global);
            return this;
        }

        public Builder async(boolean async) {
            menuItem.setAsync(async);
            return this;
        }

        public Builder resetValues(boolean resetValues) {
            menuItem.setResetValues(resetValues);
            return this;
        }

        public Builder ignoreAutoUpdate(boolean ignoreAutoUpdate) {
            menuItem.setIgnoreAutoUpdate(ignoreAutoUpdate);
            return this;
        }

        public Builder immediate(boolean immediate) {
            menuItem.setImmediate(immediate);
            return this;
        }

        public Builder delay(String delay) {
            menuItem.setDelay(delay);
            return this;
        }

        public Builder timeout(int timeout) {
            menuItem.setTimeout(timeout);
            return this;
        }

        public Builder disableClientWindow(boolean disableClientWindow) {
            menuItem.setDisableClientWindow(disableClientWindow);
            return this;
        }

        public Builder containerStyle(String containerStyle) {
            menuItem.setContainerStyle(containerStyle);
            return this;
        }

        public Builder containerStyleClass(String containerStyleClass) {
            menuItem.setContainerStyleClass(containerStyleClass);
            return this;
        }

        public Builder partialSubmitFilter(String partialSubmitFilter) {
            menuItem.setPartialSubmitFilter(partialSubmitFilter);
            return this;
        }

        public Builder confirmationScript(String confirmationScript) {
            menuItem.setConfirmationScript(confirmationScript);
            return this;
        }

        public Builder form(String form) {
            menuItem.setForm(form);
            return this;
        }

        public Builder escape(boolean escape) {
            menuItem.setEscape(escape);
            return this;
        }

        public Builder rel(String rel) {
            menuItem.setRel(rel);
            return this;
        }

        public DefaultMenuItem build() {
            return menuItem;
        }
    }
}
