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
package org.primefaces.mock;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

public class FacesContextMock extends FacesContext {

    private ExternalContext externalContext = new ExternalContextMock();
    private Application application = new ApplicationMock();
    private PartialViewContext partialViewContext = new PartialViewContextMock();
    
	private Map<Object, Object> attributes;
	private ResponseWriter writer;
    private UIViewRoot viewRoot;
    

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
		return application;
	}

	@Override
	public Iterator<String> getClientIdsWithMessages() {
		return null;
	}

	@Override
	public ExternalContext getExternalContext() {
		return externalContext;
	}

    @Override
    public PartialViewContext getPartialViewContext() {
        return partialViewContext;
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
		return viewRoot;
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
	public void setViewRoot(UIViewRoot viewRoot) {
        this.viewRoot = viewRoot;
	}
}
