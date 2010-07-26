package org.primefaces.component.poll;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
public class PollRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		Poll poll = (Poll) component;
	
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(poll.getClientId(facesContext))) {
			poll.queueEvent(new ActionEvent(poll));
		}
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Poll poll = (Poll) component;
		String pollVar = createUniqueWidgetVar(facesContext, poll);
		String clientId = poll.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, poll);
		if(form == null) {
			throw new FacesException("Poll:" + clientId + " needs to be enclosed in a form component");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.addListener(window, 'load', function() {");
		
		writer.write(pollVar + "= new PrimeFaces.widget.Poll('" + clientId + "', {");
		writer.write("frequency:" + poll.getInterval());
		writer.write(",autoStart:" + poll.isAutoStart());
		writer.write(",fn: function() {");
		writer.write(buildAjaxRequest(facesContext, poll, form.getClientId(facesContext), clientId));
		writer.write("}");
				
		writer.write("});});");
		
		writer.endElement("script");
	}
}