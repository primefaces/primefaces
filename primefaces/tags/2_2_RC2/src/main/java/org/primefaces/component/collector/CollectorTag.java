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
package org.primefaces.component.collector;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class CollectorTag extends TagSupport {

	private ValueExpression addTo;

	private ValueExpression removeFrom;
	
	private ValueExpression value;

	public int doStartTag() throws JspException {

		ActionListener actionListener = new Collector(addTo, removeFrom, value);

		UIComponentClassicTagBase tag = UIComponentClassicTagBase.getParentUIComponentClassicTagBase(pageContext);

		if (tag == null)
			throw new JspException("Could not find a "
					+ "parent UIComponentClassicTagBase ... is this "
					+ "tag in a child of a UIComponentClassicTagBase?");

		if (tag.getCreated()) {

			UIComponent component = tag.getComponentInstance();

			((ActionSource) component).addActionListener(actionListener);
		}

		return SKIP_BODY;
	}
	
	public ValueExpression getAddTo() {
		return addTo;
	}

	public void setAddTo(ValueExpression addTo) {
		this.addTo = addTo;
	}
	
	public ValueExpression getRemoveFrom() {
		return removeFrom;
	}
	
	public void setRemoveFrom(ValueExpression removeFrom) {
		this.removeFrom = removeFrom;
	}
	
	public ValueExpression getValue() {
		return value;
	}

	public void setValue(ValueExpression value) {
		this.value = value;
	}

	public void release() {
		addTo = null;
		removeFrom = null;
		value = null;
	}
}
