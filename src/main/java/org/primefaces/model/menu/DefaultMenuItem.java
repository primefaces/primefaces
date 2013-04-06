/*
 * Copyright 2009-2012 PrimeTek.
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

import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import org.primefaces.component.api.UIOutcomeTarget;

public class DefaultMenuItem implements MenuItem, UIOutcomeTarget {
    
    private String icon;
    private String title;
    private boolean disabled;
    private String onclick;
    private String style;
    private String styleClass;
    private String url;
    private String target;
    private boolean ajax;
    private Object value;
    private String outcome;
    private String href;
    private boolean includeViewParams;
    private String fragment;
    private Map<String, List<String>> params;
    private boolean rendered = true;
    
    public DefaultMenuItem() {}
    
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
    }
    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getOnclick() {
        return onclick;
    }
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getHref() {
        return url;
    }
    public void setHref(String href) {
        this.url = href;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getOutcome() {
        return outcome;
    }
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public boolean isAjax() {
        return ajax;
    }
    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isIncludeViewParams() {
        return includeViewParams;
    }
    public void setIncludeViewParams(boolean includeViewParams) {
        this.includeViewParams = includeViewParams;
    }

    public String getFragment() {
        return fragment;
    }
    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }
    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public boolean shouldRenderChildren() {
        return false;
    }
    
    public List<UIComponent> getChildren() {
        return null;
    }

    public boolean isDynamic() {
        return true;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
}
