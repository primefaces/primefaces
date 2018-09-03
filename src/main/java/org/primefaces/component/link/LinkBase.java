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
package org.primefaces.component.link;

import javax.faces.component.html.HtmlOutcomeTargetLink;

import org.primefaces.component.api.UIOutcomeTarget;


abstract class LinkBase extends HtmlOutcomeTargetLink implements UIOutcomeTarget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LinkRenderer";

    public enum PropertyKeys {

        fragment,
        disableClientWindow,
        href,
        escape
    }

    public LinkBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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

    @Override
    public void setDisableClientWindow(boolean disableClientWindow) {
        getStateHelper().put(PropertyKeys.disableClientWindow, disableClientWindow);
    }

    @Override
    public String getHref() {
        return (String) getStateHelper().eval(PropertyKeys.href, null);
    }

    public void setHref(String href) {
        getStateHelper().put(PropertyKeys.href, href);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

}