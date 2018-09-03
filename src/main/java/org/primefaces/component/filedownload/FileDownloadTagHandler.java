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
package org.primefaces.component.filedownload;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class FileDownloadTagHandler extends TagHandler {

    private final TagAttribute value;
    private final TagAttribute contentDisposition;
    private final TagAttribute monitorKey;

    public FileDownloadTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        value = getRequiredAttribute("value");
        contentDisposition = getAttribute("contentDisposition");
        monitorKey = getAttribute("monitorKey");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression valueVE = value.getValueExpression(faceletContext, Object.class);
            ValueExpression contentDispositionVE = null;
            ValueExpression monitorKeyVE = null;

            if (contentDisposition != null) {
                contentDispositionVE = contentDisposition.getValueExpression(faceletContext, String.class);
            }
            if (monitorKey != null) {
                monitorKeyVE = monitorKey.getValueExpression(faceletContext, String.class);
            }

            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new FileDownloadActionListener(valueVE, contentDispositionVE, monitorKeyVE));
        }
    }
}
