/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.filedownload;

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Property;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.ActionSource;
import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.FaceletException;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;

@FacesTagHandler("Traditionally, dynamic binary data was presented to the client by writing a servlet or filter to stream the data."
        + " FileDownload simplifies this process.")
public class FileDownloadTagHandler extends TagHandler {

    @Property(description = "A streamed content instance.", required = true)
    private final TagAttribute value;

    @Property(description = "Specifies display mode (non-ajax), valid values are \"attachment\" and \"inline\".",
            defaultValue = "attachment",
            type = String.class)
    private final TagAttribute contentDisposition;

    @Property(description = "Defines setting cookie key for monitorDownload method on client side.",
            type = String.class)
    private final TagAttribute monitorKey;

    @Property(description = "Controls the 'no-store' attribute on the cache control header. Default false is to include 'no-store'.",
            type = Boolean.class)
    private final TagAttribute store;

    public FileDownloadTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        value = getRequiredAttribute("value");
        contentDisposition = getAttribute("contentDisposition");
        monitorKey = getAttribute("monitorKey");
        store = getAttribute("store");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        ValueExpression valueVE = value.getValueExpression(faceletContext, Object.class);
        ValueExpression contentDispositionVE = null;
        ValueExpression monitorKeyVE = null;
        ValueExpression storeVE = null;

        if (contentDisposition != null) {
            contentDispositionVE = contentDisposition.getValueExpression(faceletContext, String.class);
        }
        if (monitorKey != null) {
            monitorKeyVE = monitorKey.getValueExpression(faceletContext, String.class);
        }
        if (store != null) {
            storeVE = store.getValueExpression(faceletContext, Boolean.class);
        }

        ActionSource actionSource = (ActionSource) parent;
        actionSource.addActionListener(new FileDownloadActionListener(valueVE, contentDispositionVE, monitorKeyVE, storeVE));
    }
}
