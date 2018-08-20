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
package org.primefaces.component.gmap;

import javax.faces.component.UIComponentBase;


abstract class GMapInfoWindowBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        maxWidth
    }

    public GMapInfoWindowBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public int getMaxWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxWidth, Integer.MIN_VALUE);
    }

    public void setMaxWidth(int maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, maxWidth);
    }

}