/* 
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
package org.primefaces.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private Map<String, List<FacesMessage>> messages = new HashMap<>();

    private Map<Object, Object> attributes;
    private ResponseWriter writer;
    private UIViewRoot viewRoot;

    public FacesContextMock() {
        this.attributes = new HashMap<Object, Object>();

        setCurrentInstance(this);
    }

    public FacesContextMock(ResponseWriter writer) {
        this();
        this.writer = writer;

    }

    public FacesContextMock(Map<Object, Object> attributes) {
        this();
        this.attributes = attributes;
    }

    public FacesContextMock(ResponseWriter writer, Map<Object, Object> attributes) {
        this.writer = writer;
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
    public void addMessage(String clientId, FacesMessage message) {
        if (!messages.containsKey(clientId)) {
            messages.put(clientId, new ArrayList<>());
        }
        
        messages.get(clientId).add(message);
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public Iterator<String> getClientIdsWithMessages() {
        return messages.keySet().iterator();
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
        List<FacesMessage> all = new ArrayList<>();
        for (List msgs : messages.values()) {
            all.addAll(msgs);
        }
        
        return all.iterator();
    }

    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        return messages.get(clientId).iterator();
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

    @Override
    public boolean isReleased() {
        return false;
    }

    @Override
    public boolean isPostback() {
        return false;
    }
}
