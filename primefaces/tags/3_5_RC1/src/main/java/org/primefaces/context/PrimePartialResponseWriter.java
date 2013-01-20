/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.event.AbortProcessingException;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    private PartialResponseWriter wrapped;

    public PrimePartialResponseWriter(PartialResponseWriter writer) {
        super(writer);
        wrapped = (PartialResponseWriter) writer;
    }
    
    private void encodeCallbackParams(RequestContext requestContext) throws IOException, JSONException {
        //callback params
        Map<String, Object> params = requestContext.getCallbackParams();

        boolean validationFailed = FacesContext.getCurrentInstance().isValidationFailed();
        if(validationFailed) {
            requestContext.addCallbackParam("validationFailed", true);
        }

        if(!params.isEmpty()) {
            StringBuilder jsonBuilder = new StringBuilder();
            Map<String, String> callbackParamExtension = new HashMap<String, String>();
            callbackParamExtension.put("ln", "primefaces");
            callbackParamExtension.put("type", "args");

            startExtension(callbackParamExtension);

            jsonBuilder.append("{");

            for(Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String paramName = it.next();
                Object paramValue = params.get(paramName);

                if(isBean(paramValue)) {
                    jsonBuilder.append("\"").append(paramName).append("\":").append(new JSONObject(paramValue).toString());
                } 
                else {
                    String json = new JSONObject().put(paramName, paramValue).toString();
                    jsonBuilder.append(json.substring(1, json.length() - 1));
                }

                if(it.hasNext()) {
                    jsonBuilder.append(",");
                }
            }

            jsonBuilder.append("}");

            write(jsonBuilder.toString());
            jsonBuilder.setLength(0);

            endExtension();
        }
    }
    
    private void encodeScripts(RequestContext requestContext) throws IOException {
        List<String> scripts = requestContext.getScriptsToExecute();
        if(!scripts.isEmpty()) {
            startEval();

            for(String script : scripts) {
                write(script + ";");
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
    }
    
    @Override
    public void endDocument() throws IOException {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(requestContext != null) {
            try {
                encodeCallbackParams(requestContext);
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
        if(value == null) {
            return false;
        }

        if(value instanceof Boolean || value instanceof String || value instanceof Number) {
            return false;
        }

        return true;
    }
}
