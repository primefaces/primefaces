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
package org.primefaces.context;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.event.AbortProcessingException;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.util.ComponentUtils;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    private static final Map<String, String> CALLBACK_EXTENSION_PARAMS = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("ln", "primefaces");
        put("type", "args");
    }});

    private final PartialResponseWriter wrapped;

    public PrimePartialResponseWriter(PartialResponseWriter wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    public void encodeJSONObject(String paramName, JSONObject jsonObject) throws IOException, JSONException {
        String json = jsonObject.toString();
        json = ComponentUtils.escapeXml(json);

        getWrapped().write("\"");
        getWrapped().write(paramName);
        getWrapped().write("\":");
        getWrapped().write(json);
    }
    
    public void encodeJSONArray(String paramName, JSONArray jsonArray) throws IOException, JSONException {
        String json = jsonArray.toString();
        json = ComponentUtils.escapeXml(json);

        getWrapped().write("\"");
        getWrapped().write(paramName);
        getWrapped().write("\":");
        getWrapped().write(json);
    }
    
    public void encodeJSONValue(String paramName, Object paramValue) throws IOException, JSONException {
        String json = new JSONObject().put(paramName, paramValue).toString();
        json = ComponentUtils.escapeXml(json);

        getWrapped().write(json.substring(1, json.length() - 1));
    }

    public void encodeCallbackParams(Map<String, Object> params) throws IOException, JSONException {

        if (params != null && !params.isEmpty()) {

            startExtension(CALLBACK_EXTENSION_PARAMS);
            getWrapped().write("{");

            for(Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String paramName = it.next();
                Object paramValue = params.get(paramName);

                if (paramValue instanceof JSONObject) {
                    encodeJSONObject(paramName, (JSONObject) paramValue);
                }
                else if (paramValue instanceof JSONArray) {
                    encodeJSONArray(paramName, (JSONArray) paramValue);
                }
                else if (isBean(paramValue)) {
                    encodeJSONObject(paramName, (JSONObject) paramValue);
                }
                else {
                    encodeJSONValue(paramName, paramValue);
                }

                if (it.hasNext()) {
                    getWrapped().write(",");
                }
            }

            getWrapped().write("}");
            endExtension();
        }
    }

    public void encodeScripts(RequestContext requestContext) throws IOException {
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

    @Override
    public void delete(String targetId) throws IOException {
        wrapped.delete(targetId);
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
    public void redirect(String url) throws IOException {
        wrapped.redirect(url);
    }

    @Override
    public void startDocument() throws IOException {
        wrapped.startDocument();


        RequestContext requestContext = RequestContext.getCurrentInstance();

        if (requestContext != null) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context.getViewRoot() instanceof NamingContainer) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("parameterNamespace", context.getViewRoot().getContainerClientId(context));
                    encodeCallbackParams(params);
                }
            }
            catch (Exception e) {
                throw new AbortProcessingException(e);
            }
        }
    }

    @Override
    public void endDocument() throws IOException {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if (requestContext != null) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context.isValidationFailed()) {
                    requestContext.addCallbackParam("validationFailed", true);
                }

                encodeCallbackParams(requestContext.getCallbackParams());
                encodeScripts(requestContext);
            }
            catch (Exception exception) {
                throw new AbortProcessingException(exception);
            }
        }

        wrapped.endDocument();
    }

    @Override
    public void startError(String errorName) throws IOException {
        wrapped.startError(errorName);
    }

    @Override
    public void startEval() throws IOException {
        wrapped.startEval();
    }

    @Override
    public void startExtension(Map<String, String> attributes) throws IOException {
        wrapped.startExtension(attributes);
    }

    @Override
    public void startInsertAfter(String targetId) throws IOException {
        wrapped.startInsertAfter(targetId);
    }

    @Override
    public void startInsertBefore(String targetId) throws IOException {
        wrapped.startInsertBefore(targetId);
    }

    @Override
    public void startUpdate(String targetId) throws IOException {
        wrapped.startUpdate(targetId);
    }

    @Override
    public void updateAttributes(String targetId, Map<String, String> attributes) throws IOException {
        wrapped.updateAttributes(targetId, attributes);
    }

    private boolean isBean(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean || value instanceof String || value instanceof Number) {
            return false;
        }

        return true;
    }
}
