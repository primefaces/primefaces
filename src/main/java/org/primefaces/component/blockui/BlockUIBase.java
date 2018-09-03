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
package org.primefaces.component.blockui;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class BlockUIBase extends UIPanel implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.BlockUIRenderer";

    public enum PropertyKeys {

        widgetVar,
        trigger,
        block,
        blocked,
        animate,
        styleClass
    }

    public BlockUIBase() {
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

    public String getTrigger() {
        return (String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(String trigger) {
        getStateHelper().put(PropertyKeys.trigger, trigger);
    }

    public String getBlock() {
        return (String) getStateHelper().eval(PropertyKeys.block, null);
    }

    public void setBlock(String block) {
        getStateHelper().put(PropertyKeys.block, block);
    }

    public boolean isBlocked() {
        return (Boolean) getStateHelper().eval(PropertyKeys.blocked, false);
    }

    public void setBlocked(boolean blocked) {
        getStateHelper().put(PropertyKeys.blocked, blocked);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}