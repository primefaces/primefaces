/*
 * Copyright 2010 Prime Technology.
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
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.event.AbortProcessingException;
import org.primefaces.json.JSONObject;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    private PartialResponseWriter wrapped;

    public PrimePartialResponseWriter(PartialResponseWriter writer) {
        super(writer);
        wrapped = (PartialResponseWriter) writer;
    }

    @Override
    public void endDocument() throws IOException {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(requestContext != null) {

            try {
                //callback params
                boolean validationFailed = FacesContext.getCurrentInstance().isValidationFailed();
                
                if(validationFailed) {
                    requestContext.addCallbackParam("validationFailed", true);
                }
                
                Map<String, Object> params = requestContext.getCallbackParams();

                for(String paramName : params.keySet()) {
                    Map<String, String> callbackParamExtension = new HashMap<String, String>();
                    callbackParamExtension.put("primefacesCallbackParam", paramName);

                    startExtension(callbackParamExtension);

                    Object paramValue = params.get(paramName);
                    String json = isBean(paramValue) ? "{\"" + paramName + "\":" + new JSONObject(paramValue).toString() + "}" : new JSONObject().put(paramName, paramValue).toString();
                    write(json);

                    endExtension();
                }

                //scripts
                List<String> scripts = requestContext.getScriptsToExecute();
                if(!scripts.isEmpty()) {
                    startEval();
                    for(String script : scripts) {
                        write(script + ";");
                    }
                    endEval();
                }
                
            } catch (Exception exception) {
                throw new AbortProcessingException(exception);
            } finally {
                requestContext.release();
            }
        }

            
        wrapped.endDocument();
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
