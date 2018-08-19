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
package org.primefaces.component.clock;

import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "clock/clock.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "raphael/raphael.js"),
        @ResourceDependency(library = "primefaces", name = "clock/clock.js")
})
public class Clock extends ClockBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Clock";

    public static final String STYLE_CLASS = "ui-clock ui-widget ui-widget-header ui-corner-all";
    public static final String ANALOG_STYLE_CLASS = "ui-analog-clock ui-widget";
    private java.util.TimeZone appropriateTimeZone;

    public boolean isSyncRequest() {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        return params.containsKey(getClientId(context) + "_sync");
    }

    public java.util.TimeZone calculateTimeZone() {
        if (appropriateTimeZone == null) {
            Object usertimeZone = getTimeZone();
            if (usertimeZone != null) {
                if (usertimeZone instanceof String) {
                    appropriateTimeZone = java.util.TimeZone.getTimeZone((String) usertimeZone);
                }
                else if (usertimeZone instanceof java.util.TimeZone) {
                    appropriateTimeZone = (java.util.TimeZone) usertimeZone;
                }
                else {
                    throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
                }
            }
            else {
                appropriateTimeZone = java.util.TimeZone.getDefault();
            }
        }

        return appropriateTimeZone;
    }


}