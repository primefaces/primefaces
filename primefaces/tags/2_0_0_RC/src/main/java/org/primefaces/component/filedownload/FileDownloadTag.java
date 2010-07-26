package org.primefaces.component.filedownload;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FileDownloadTag extends TagSupport {

	private ValueExpression value;
	
	public int doStartTag() throws JspException {

		ActionListener actionListener = new FileDownloadActionListener(value);

		UIComponentClassicTagBase tag = UIComponentClassicTagBase.getParentUIComponentClassicTagBase(pageContext);

		if(tag == null)
			throw new JspException("FileDownload component needs to be enclosed in a UICommand component");

		if(tag.getCreated()) {
			UIComponent component = tag.getComponentInstance();
			((ActionSource) component).addActionListener(actionListener);
		}

		return SKIP_BODY;
	}

	public ValueExpression getValue() {
		return value;
	}

	public void setValue(ValueExpression value) {
		this.value = value;
	}
	
	public void release() {
		value = null;
	}
}
