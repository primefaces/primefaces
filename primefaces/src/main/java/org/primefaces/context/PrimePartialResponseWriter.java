/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.util.BeanUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.NamingContainer;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialResponseWriter;
import jakarta.faces.event.AbortProcessingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    private static final Map<String, String> CALLBACK_EXTENSION_PARAMS = Map.of("ln", "primefaces", "type", "args");
    private static final Map<String, String> CALLBACK_EXTENSION_SCRIPTS = Map.of("ln", "primefaces", "type", "script");

    private boolean metadataRendered;

    public PrimePartialResponseWriter(PartialResponseWriter wrapped) {
        super(wrapped);
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

        super.endDocument();
    }

    @Override
    public void startError(String errorName) throws IOException {
        startMetadataIfNecessary();

        super.startError(errorName);
    }

    @Override
    public void startEval() throws IOException {
        startMetadataIfNecessary();

        super.startEval();
    }

    @Override
    public void startExtension(Map<String, String> attributes) throws IOException {
        startMetadataIfNecessary();

        super.startExtension(attributes);
    }

    @Override
    public void startInsertAfter(String targetId) throws IOException {
        startMetadataIfNecessary();

        super.startInsertAfter(targetId);
    }

    @Override
    public void startInsertBefore(String targetId) throws IOException {
        startMetadataIfNecessary();

        super.startInsertBefore(targetId);
    }

    @Override
    public void startUpdate(String targetId) throws IOException {
        startMetadataIfNecessary();

        super.startUpdate(targetId);
    }

    @Override
    public void updateAttributes(String targetId, Map<String, String> attributes) throws IOException {
        startMetadataIfNecessary();

        super.updateAttributes(targetId, attributes);
    }

    @Override
    public void delete(String targetId) throws IOException {
        startMetadataIfNecessary();

        super.delete(targetId);
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

            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> entry = it.next();
                String paramName = entry.getKey();
                Object paramValue = entry.getValue();

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
        List<String> initScripts = requestContext.getInitScriptsToExecute();
        List<String> scripts = requestContext.getScriptsToExecute();

        if (!initScripts.isEmpty()) {
            startEval();

            for (String initScript : initScripts) {
                getWrapped().write(EscapeUtils.forCDATA(initScript));
                getWrapped().write(';');
            }

            endEval();
        }

        for (String script : scripts) {
            startExtension(CALLBACK_EXTENSION_SCRIPTS);
            getWrapped().startCDATA();
            getWrapped().write(EscapeUtils.forCDATA(script));
            getWrapped().endCDATA();
            endExtension();
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
                        if (LangUtils.isNotEmpty(parameterNamespace)) {

                            String parameterPrefix = parameterNamespace;
                            // since JSF 2.3 -> https://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-790
                            parameterPrefix += UINamingContainer.getSeparatorChar(context);

                            Map<String, Object> params = new HashMap<>();
                            params.put("parameterPrefix", parameterPrefix);
                            encodeCallbackParams(params);
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
