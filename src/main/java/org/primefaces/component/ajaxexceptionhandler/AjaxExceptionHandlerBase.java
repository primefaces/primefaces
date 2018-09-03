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
package org.primefaces.component.ajaxexceptionhandler;

import javax.faces.component.UIComponentBase;


abstract class AjaxExceptionHandlerBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        onexception,
        update,
        type;
    }

    public AjaxExceptionHandlerBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getOnexception() {
        return (String) getStateHelper().eval(PropertyKeys.onexception, null);
    }

    public void setOnexception(String onexception) {
        getStateHelper().put(PropertyKeys.onexception, onexception);
    }

    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

}