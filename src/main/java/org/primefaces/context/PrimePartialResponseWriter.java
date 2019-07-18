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
package org.primefaces.context;

import org.primefaces.application.resource.DynamicResourcesPhaseListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.util.BeanUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.ResourceUtils;

import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.event.AbortProcessingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    private static final Map<String, String> CALLBACK_EXTENSION_PARAMS;

    static {
        Map<String, String> callbackExtensionParams = new HashMap<>();
        callbackExtensionParams.put("ln", "primefaces");
        callbackExtensionParams.put("type", "args");

        CALLBACK_EXTENSION_PARAMS = Collections.unmodifiableMap(callbackExtensionParams);
    }

    private final PartialResponseWriter wrapped;
    private boolean metadataRendered = false;

    public PrimePartialResponseWriter(PartialResponseWriter wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void endError() throws IOException {
        wrapped.endError();
    }

    @Override
    public void endEval() throws IOException {
        wrapped.endEval();
    }

    @Override
    public void endExtension() throws IOException {
        wrapped.endExtension();
    }

    @Override
    public void endInsert() throws IOException {
        wrapped.endInsert();
    }

    @Override
    public void endUpdate() throws IOException {
        wrapped.endUpdate();
    }

    @Override
    public void endDocument() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);

        if (requestContext != null) {
            try {
                if (context.isValidationFailed()) {
                    requestContext.getCallbackParams().put("validationFailed", true);
                }

                encodeCallbackParams(requestContext.getCallbackParams());
                encodeScripts(requestContext);
            }
            catch (Exception e) {
                throw new AbortProcessingException(e);
            }
        }

        wrapped.endDocument();
    }

    @Override
    public void startDocument() throws IOException {
        wrapped.startDocument();
    }

    @Override
    public void startError(String errorName) throws IOException {
        startMetadataIfNecessary();

        wrapped.startError(errorName);
    }

    @Override
    public void startEval() throws IOException {
        startMetadataIfNecessary();

        wrapped.startEval();
    }

    @Override
    public void startExtension(Map<String, String> attributes) throws IOException {
        startMetadataIfNecessary();

        wrapped.startExtension(attributes);
    }

    @Override
    public void startInsertAfter(String targetId) throws IOException {
        startMetadataIfNecessary();

        wrapped.startInsertAfter(targetId);
    }

    @Override
    public void startInsertBefore(String targetId) throws IOException {
        startMetadataIfNecessary();

        wrapped.startInsertBefore(targetId);
    }

    @Override
    public void startUpdate(String targetId) throws IOException {
        startMetadataIfNecessary();

        wrapped.startUpdate(targetId);
    }

    @Override
    public void updateAttributes(String targetId, Map<String, String> attributes) throws IOException {
        startMetadataIfNecessary();

        wrapped.updateAttributes(targetId, attributes);
    }

    @Override
    public void redirect(String url) throws IOException {
        wrapped.redirect(url);
    }

    @Override
    public void delete(String targetId) throws IOException {
        startMetadataIfNecessary();

        wrapped.delete(targetId);
    }

    public void encodeJSONObject(String paramName, JSONObject jsonObject) throws IOException, JSONException {
        String json = jsonObject.toString();
        json = EscapeUtils.forXml(json);

        getWrapped().write("\"");
        getWrapped().write(paramName);
        getWrapped().write("\":");
        getWrapped().write(json);
    }

    public void encodeJSONArray(String paramName, JSONArray jsonArray) throws IOException, JSONException {
        String json = jsonArray.toString();
        json = EscapeUtils.forXml(json);

        getWrapped().write("\"");
        getWrapped().write(paramName);
        getWrapped().write("\":");
        getWrapped().write(json);
    }

    public void encodeJSONValue(String paramName, Object paramValue) throws IOException, JSONException {
        String json = new JSONObject().put(paramName, paramValue).toString();
        json = EscapeUtils.forXml(json);

        getWrapped().write(json.substring(1, json.length() - 1));
    }

    public void encodeCallbackParams(Map<String, Object> params) throws IOException, JSONException {

        if (params != null && !params.isEmpty()) {

            startExtension(CALLBACK_EXTENSION_PARAMS);
            getWrapped().write("{");

            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String paramName = it.next();
                Object paramValue = params.get(paramName);

                if (paramValue == null) {
                    encodeJSONValue(paramName, null);
                }
                else {
                    if (paramValue instanceof JSONObject) {
                        encodeJSONObject(paramName, (JSONObject) paramValue);
                    }
                    else if (paramValue instanceof JSONArray) {
                        encodeJSONArray(paramName, (JSONArray) paramValue);
                    }
                    else if (BeanUtils.isBean(paramValue)) {
                        encodeJSONObject(paramName, new JSONObject(paramValue));
                    }
                    else {
                        encodeJSONValue(paramName, paramValue);
                    }
                }

                if (it.hasNext()) {
                    getWrapped().write(",");
                }
            }

            getWrapped().write("}");
            endExtension();
        }
    }

    protected void encodeScripts(PrimeRequestContext requestContext) throws IOException {
        List<String> scripts = requestContext.getScriptsToExecute();
        if (!scripts.isEmpty()) {
            startEval();

            for (int i = 0; i < scripts.size(); i++) {
                getWrapped().write(scripts.get(i));
                getWrapped().write(';');
            }

            endEval();
        }
    }

    protected void startMetadataIfNecessary() throws IOException {

        if (metadataRendered) {
            return;
        }

        metadataRendered = true;

        FacesContext context = FacesContext.getCurrentInstance();
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

        if (applicationContext != null) {
            try {
                // catch possible ViewExpired
                UIViewRoot viewRoot = context.getViewRoot();
                if (viewRoot != null) {
                    // portlet parameter namespacing
                    if (viewRoot instanceof NamingContainer) {

                        String parameterNamespace = viewRoot.getContainerClientId(context);
                        if ((parameterNamespace != null) && (parameterNamespace.length() > 0)) {

                            String parameterPrefix = parameterNamespace;

                            if (applicationContext.getEnvironment().isAtLeastJsf23()) {

                                // https://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-790
                                parameterPrefix += UINamingContainer.getSeparatorChar(context);
                            }

                            Map<String, Object> params = new HashMap<>();
                            params.put("parameterPrefix", parameterPrefix);
                            encodeCallbackParams(params);
                        }
                    }

                    // dynamic resource loading
                    // we just do it for postbacks, otherwise ajax requests without a form would reload all resources
                    // we also skip update=@all, as the head with all resources, will already be rendered
                    if (context.isPostback()
                            && !context.getPartialViewContext().isRenderAll()
                            && !applicationContext.getEnvironment().isAtLeastJsf23()) {
                        List<ResourceUtils.ResourceInfo> initialResources = DynamicResourcesPhaseListener.getInitialResources(context);
                        List<ResourceUtils.ResourceInfo> currentResources = ResourceUtils.getComponentResources(context);
                        if (initialResources != null && currentResources != null && currentResources.size() > initialResources.size()) {

                            List<ResourceUtils.ResourceInfo> newResources = new ArrayList<>(currentResources);
                            newResources.removeAll(initialResources);

                            boolean updateStarted = false;
                            for (int i = 0; i < newResources.size(); i++) {
                                ResourceUtils.ResourceInfo resourceInfo = newResources.get(i);
                                if (!updateStarted) {
                                    ((PartialResponseWriter) getWrapped()).startUpdate("javax.faces.Resource");
                                    updateStarted = true;
                                }
                                resourceInfo.getResource().encodeAll(context);
                            }

                            if (updateStarted) {
                                ((PartialResponseWriter) getWrapped()).endUpdate();
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new AbortProcessingException(e);
            }
        }
    }

}
