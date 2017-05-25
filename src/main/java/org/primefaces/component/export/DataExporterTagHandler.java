/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.export;

import java.io.IOException;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class DataExporterTagHandler extends TagHandler {

	private final TagAttribute target;
	private final TagAttribute type;
	private final TagAttribute fileName;
	private final TagAttribute pageOnly;
	private final TagAttribute selectionOnly;
	private final TagAttribute preProcessor;
	private final TagAttribute postProcessor;
	private final TagAttribute encoding;
    private final TagAttribute repeat;
    private final TagAttribute options;

	public DataExporterTagHandler(TagConfig tagConfig) {
		super(tagConfig);
		this.target = getRequiredAttribute("target");
		this.type = getRequiredAttribute("type");
		this.fileName = getRequiredAttribute("fileName");
		this.pageOnly = getAttribute("pageOnly");
		this.selectionOnly = getAttribute("selectionOnly");
		this.encoding = getAttribute("encoding");
		this.preProcessor = getAttribute("preProcessor");
		this.postProcessor = getAttribute("postProcessor");
        this.repeat = getAttribute("repeat");
        this.options = getAttribute("options");
	}

	public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
		if (ComponentHandler.isNew(parent)) {
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
			
			if(encoding != null) {
				encodingVE = encoding.getValueExpression(faceletContext, Object.class);
			}
			if(pageOnly != null) {
				pageOnlyVE = pageOnly.getValueExpression(faceletContext, Object.class);
			}
			if(selectionOnly != null) {
				selectionOnlyVE = selectionOnly.getValueExpression(faceletContext, Object.class);
			}
			if(preProcessor != null) {
				preProcessorME = preProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
			}
			if(postProcessor != null) {
				postProcessorME = postProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
			}
            if(repeat != null) {
				repeatVE = repeat.getValueExpression(faceletContext, Object.class);
			}
			if(options != null) {
				optionsVE = options.getValueExpression(faceletContext, Object.class);
			}
			
			ActionSource actionSource = (ActionSource) parent;
            DataExporter dataExporter = new DataExporter(targetVE, typeVE, fileNameVE, pageOnlyVE, selectionOnlyVE, encodingVE, preProcessorME, postProcessorME, optionsVE);
            dataExporter.setRepeat(repeatVE);
			actionSource.addActionListener(dataExporter);
		}
	}

}