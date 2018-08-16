/*
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
package org.primefaces.component.log;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="components.css"),
	@ResourceDependency(library="primefaces", name="log/log.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="core.js"),
	@ResourceDependency(library="primefaces", name="components.js"),
	@ResourceDependency(library="primefaces", name="log/log.js")
})
public class Log extends LogBase implements org.primefaces.component.api.Widget {



    public final static String CONTAINER_CLASS = "ui-log ui-widget ui-widget-content ui-corner-all";
    public final static String HEADER_CLASS = "ui-log-header ui-widget-header ui-helper-clearfix";
    public final static String CONTENT_CLASS = "ui-log-content";
    public final static String ITEMS_CLASS = "ui-log-items";
    public final static String CLEAR_BUTTON_CLASS = "ui-log-button ui-log-clear ui-corner-all";
    public final static String ALL_BUTTON_CLASS = "ui-log-button ui-log-all ui-corner-all";
    public final static String INFO_BUTTON_CLASS = "ui-log-button ui-log-info ui-corner-all";
    public final static String DEBUG_BUTTON_CLASS = "ui-log-button ui-log-debug ui-corner-all";
    public final static String WARN_BUTTON_CLASS = "ui-log-button ui-log-warn ui-corner-all";
    public final static String ERROR_BUTTON_CLASS = "ui-log-button ui-log-error ui-corner-all";
}