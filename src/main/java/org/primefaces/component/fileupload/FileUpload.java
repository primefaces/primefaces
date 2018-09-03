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
package org.primefaces.component.fileupload;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "fileupload/fileupload.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "fileupload/fileupload.js")
})
public class FileUpload extends FileUploadBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.FileUpload";
    public static final String CONTAINER_CLASS = "ui-fileupload ui-widget ui-fileupload-responsive";
    public static final String BUTTON_BAR_CLASS = "ui-fileupload-buttonbar ui-widget-header ui-corner-top";
    public static final String CONTENT_CLASS = "ui-fileupload-content ui-widget-content ui-corner-bottom";
    public static final String FILES_CLASS = "ui-fileupload-files";
    public static final String CHOOSE_BUTTON_CLASS = "ui-fileupload-choose";
    public static final String UPLOAD_BUTTON_CLASS = "ui-fileupload-upload";
    public static final String CANCEL_BUTTON_CLASS = "ui-fileupload-cancel";
    public static final String BUTTON_ICON_ONLY = "ui-fileupload-icon-only";

    public static final String CONTAINER_CLASS_SIMPLE = "ui-fileupload-simple ui-widget";
    public static final String FILENAME_CLASS = "ui-fileupload-filename";

    @Override
    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
        super.broadcast(event);

        FacesContext facesContext = getFacesContext();
        MethodExpression me = getFileUploadListener();

        if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
            me.invoke(facesContext.getELContext(), new Object[]{event});
        }
    }
}