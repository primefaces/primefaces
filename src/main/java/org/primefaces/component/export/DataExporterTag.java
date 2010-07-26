/*
 * Copyright 2009 Prime Technology.
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class DataExporterTag extends TagSupport {

	private ValueExpression target;

	private ValueExpression type;
	
	private ValueExpression fileName;
	
	private ValueExpression pageOnly;
	
	private ValueExpression encoding;
	
	private ValueExpression excludeColumns;
	
	private MethodExpression preProcessor;

	private MethodExpression postProcessor;

	public int doStartTag() throws JspException {
		ActionListener actionListener = new DataExporter(target, type, fileName, pageOnly, excludeColumns, encoding, preProcessor, postProcessor);

		UIComponentClassicTagBase tag = UIComponentClassicTagBase.getParentUIComponentClassicTagBase(pageContext);

		if (tag == null)
			throw new JspException("DataExporter component needs to be enclosed in a UICommand component");

		if (tag.getCreated()) {

			UIComponent component = tag.getComponentInstance();

			((ActionSource) component).addActionListener(actionListener);
		}

		return SKIP_BODY;
	}
	
	public ValueExpression getTarget() {
		return target;
	}

	public void setTarget(ValueExpression target) {
		this.target = target;
	}
	
	public ValueExpression getType() {
		return type;
	}

	public void setType(ValueExpression type) {
		this.type = type;
	}
	
	public ValueExpression getFileName() {
		return fileName;
	}

	public void setFileName(ValueExpression fileName) {
		this.fileName = fileName;
	}
	
	public ValueExpression getPageOnly() {
		return pageOnly;
	}

	public void setPageOnly(ValueExpression pageOnly) {
		this.pageOnly = pageOnly;
	}

	public ValueExpression getExcludeColumns() {
		return excludeColumns;
	}

	public void setExcludeColumns(ValueExpression excludeColumns) {
		this.excludeColumns = excludeColumns;
	}
	
	public MethodExpression getPreProcessor() {
		return preProcessor;
	}

	public void setPreProcessor(MethodExpression preProcessor) {
		this.preProcessor = preProcessor;
	}
	
	public MethodExpression getPostProcessor() {
		return postProcessor;
	}

	public void setPostProcessor(MethodExpression postProcessor) {
		this.postProcessor = postProcessor;
	}

	public ValueExpression getEncoding() {
		return encoding;
	}

	public void setEncoding(ValueExpression encoding) {
		this.encoding = encoding;
	}

	public void release() {
		target = null;
		type = null;
		fileName = null;
		pageOnly = null;
		excludeColumns = null;
		preProcessor = null;
		postProcessor = null;
		encoding = null;
	}
}
