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
package org.primefaces.component.ajaxstatus;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js")
})
public class AjaxStatus extends AjaxStatusBase implements org.primefaces.component.api.Widget {


    public static final String COMPONENT_TYPE = "org.primefaces.component.AjaxStatus";

    public final static String START = "start";
    public final static String SUCCESS = "success";
    public final static String COMPLETE = "complete";
    public final static String ERROR = "error";
    public final static String DEFAULT = "default";
    public final static String CALLBACK_SIGNATURE = "function()";

    public final static String[] EVENTS = {
            START, SUCCESS, COMPLETE, ERROR
    };
}