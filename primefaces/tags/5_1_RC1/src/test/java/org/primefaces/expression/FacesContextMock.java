package org.primefaces.expression;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

public class FacesContextMock extends FacesContext {

	private Map<Object, Object> attributes;
	private ResponseWriter writer;

	public FacesContextMock() {
    }
	
	public FacesContextMock(ResponseWriter writer) {
		this.writer = writer;

		setCurrentInstance(this);
	}
	
	public FacesContextMock(Map<Object, Object> attributes) {
		this.attributes = attributes;

		setCurrentInstance(this);
	}

	@Override
    public Map<Object, Object> getAttributes() {
		return attributes;
	}

	@Override
    public boolean isProjectStage(ProjectStage stage) {
		return true;
	}

	@Override
	public void addMessage(String arg0, FacesMessage arg1) {

	}

	@Override
	public Application getApplication() {
		return null;
	}

	@Override
	public Iterator<String> getClientIdsWithMessages() {
		return null;
	}

	@Override
	public ExternalContext getExternalContext() {
		return null;
	}

	@Override
	public Severity getMaximumSeverity() {
		return null;
	}

	@Override
	public Iterator<FacesMessage> getMessages() {
		return null;
	}

	@Override
	public Iterator<FacesMessage> getMessages(String arg0) {
		return null;
	}

	@Override
	public RenderKit getRenderKit() {
		return new RenderKitMock();
	}

	@Override
	public boolean getRenderResponse() {
		return false;
	}

	@Override
	public boolean getResponseComplete() {
		return false;
	}

	@Override
	public ResponseStream getResponseStream() {
		return null;
	}

	@Override
	public ResponseWriter getResponseWriter() {
		return writer;
	}

	@Override
	public UIViewRoot getViewRoot() {
		return null;
	}

	@Override
	public void release() {

	}

	@Override
	public void renderResponse() {

	}

	@Override
	public void responseComplete() {

	}

	@Override
	public void setResponseStream(ResponseStream arg0) {

	}

	@Override
	public void setResponseWriter(ResponseWriter arg0) {

	}

	@Override
	public void setViewRoot(UIViewRoot arg0) {

	}
}
