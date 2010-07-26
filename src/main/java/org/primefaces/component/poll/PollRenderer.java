package org.primefaces.component.poll;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class PollRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		Poll poll = (Poll) component;
		
		String clientId = poll.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId)) {
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
			throw new FacesException("Tree:" + clientId + " needs to be enclosed in a form");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.addListener(window, 'load', function() {\n");
		
		writer.write(pollVar + "= new PrimeFaces.widget.Poll('" + clientId + "', {");
		writer.write("formId:'" + form.getClientId(facesContext) + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		writer.write(",frequency:" + poll.getInterval() + "");
				
		if(poll.isAsync()) writer.write(",async:true");

		//Callbacks
		if(poll.getOnstart() != null) writer.write(",onstart:function(xhr){" + poll.getOnstart() + ";}");
		if(poll.getOnerror() != null) writer.write(",onerror:function(xhr, status, error){" + poll.getOnerror() + ";}");
		if(poll.getOnsuccess() != null) writer.write(",onsuccess:function(data, status, xhr, args){" + poll.getOnsuccess() + ";}"); 
		if(poll.getOncomplete() != null) writer.write(",oncomplete:function(xhr, status, args){" + poll.getOncomplete() + ";}");

		writer.write(",global:" + poll.isGlobal());
		
		writer.write("},{");
		
		writer.write("'" + clientId + "'");
		writer.write(":");
		writer.write("'" + clientId + "'");
		
		if(poll.getUpdate() != null) {
			writer.write(",'" + Constants.PARTIAL_UPDATE_PARAM + "':");
			writer.write("'" + ComponentUtils.findClientIds(facesContext, poll, poll.getUpdate()) + "'");
		}
		
		if(poll.getProcess() != null) {
			writer.write(",'" + Constants.PARTIAL_PROCESS_PARAM + "':");
			writer.write("'" + ComponentUtils.findClientIds(facesContext, poll, poll.getProcess()) + "'");
		}
	
		writer.write("});});");
		
		writer.endElement("script");
	}
}