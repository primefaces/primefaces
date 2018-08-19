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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getTrigger() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(java.lang.String _trigger) {
        getStateHelper().put(PropertyKeys.trigger, _trigger);
    }

    public java.lang.String getBlock() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.block, null);
    }

    public void setBlock(java.lang.String _block) {
        getStateHelper().put(PropertyKeys.block, _block);
    }

    public boolean isBlocked() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.blocked, false);
    }

    public void setBlocked(boolean _blocked) {
        getStateHelper().put(PropertyKeys.blocked, _blocked);
    }

    public boolean isAnimate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean _animate) {
        getStateHelper().put(PropertyKeys.animate, _animate);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}