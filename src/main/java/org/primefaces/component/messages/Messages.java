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
package org.primefaces.component.messages;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css")
})
public class Messages extends MessagesBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Messages";

    public static final String CONTAINER_CLASS = "ui-messages ui-widget";
    public static final String ICONLESS_CONTAINER_CLASS = "ui-messages ui-messages-noicon ui-widget";
    public static final String CLOSE_LINK_CLASS = "ui-messages-close";
    public static final String CLOSE_ICON_CLASS = "ui-icon ui-icon-close";
    public static final String SEVERITY_PREFIX_CLASS = "ui-messages-";
}