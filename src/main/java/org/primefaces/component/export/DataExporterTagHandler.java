/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.export;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class DataExporterTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute type;
    private final TagAttribute fileName;
    private final TagAttribute pageOnly;
    private final TagAttribute selectionOnly;
    private final TagAttribute preProcessor;
    private final TagAttribute postProcessor;
    private final TagAttribute encoding;
    private final TagAttribute options;
    private final TagAttribute onTableRender;
    private final TagAttribute customExporter;

    public DataExporterTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        target = getRequiredAttribute("target");
        type = getRequiredAttribute("type");
        fileName = getRequiredAttribute("fileName");
        pageOnly = getAttribute("pageOnly");
        selectionOnly = getAttribute("selectionOnly");
        encoding = getAttribute("encoding");
        preProcessor = getAttribute("preProcessor");
        postProcessor = getAttribute("postProcessor");
        options = getAttribute("options");
        onTableRender = getAttribute("onTableRender");
        customExporter = getAttribute("customExporter");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
        ValueExpression typeVE = type.getValueExpression(faceletContext, Object.class);
        ValueExpression fileNameVE = fileName.getValueExpression(faceletContext, Object.class);
        ValueExpression pageOnlyVE = null;
        ValueExpression selectionOnlyVE = null;
        ValueExpression encodingVE = null;
        MethodExpression preProcessorME = null;
        MethodExpression postProcessorME = null;
        ValueExpression repeatVE = null;
        ValueExpression optionsVE = null;
        MethodExpression onTableRenderME = null;
        ValueExpression customExporterVE = null;

        if (encoding != null) {
            encodingVE = encoding.getValueExpression(faceletContext, Object.class);
        }
        if (pageOnly != null) {
            pageOnlyVE = pageOnly.getValueExpression(faceletContext, Object.class);
        }
        if (selectionOnly != null) {
            selectionOnlyVE = selectionOnly.getValueExpression(faceletContext, Object.class);
        }
        if (preProcessor != null) {
            preProcessorME = preProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
        }
        if (postProcessor != null) {
            postProcessorME = postProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
        }
        if (options != null) {
            optionsVE = options.getValueExpression(faceletContext, Object.class);
        }
        if (onTableRender != null) {
            onTableRenderME = onTableRender.getMethodExpression(faceletContext, null, new Class[]{Object.class, Object.class});
        }
        if (customExporter != null) {
            customExporterVE = customExporter.getValueExpression(faceletContext, Object.class);
        }
        ActionSource actionSource = (ActionSource) parent;
        DataExporter dataExporter = new DataExporter(targetVE, typeVE, fileNameVE, pageOnlyVE, selectionOnlyVE,
                encodingVE, preProcessorME, postProcessorME, optionsVE, onTableRenderME);
        dataExporter.setCustomExporter(customExporterVE);
        actionSource.addActionListener(dataExporter);
    }

}
