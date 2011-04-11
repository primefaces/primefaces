/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.mobile.util;

import javax.faces.context.FacesContext;

public class MobileUtils {

    public static final String MOBILE_REQUEST_MARKER = "PRIMEFACES_MOBILE_REQUEST";

    public static void setMobileRequest() {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(MOBILE_REQUEST_MARKER, MOBILE_REQUEST_MARKER);
    }

    public static boolean isMobileRequest() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(MOBILE_REQUEST_MARKER) != null;
    }
}
