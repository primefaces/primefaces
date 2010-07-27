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
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import org.primefaces.json.JSONObject;

public class PrimePartialResponseWriter extends PartialResponseWriter {

    public PrimePartialResponseWriter(ResponseWriter writer) {
        super(writer);
    }

    @Override
    public void endDocument() throws IOException {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("validationFailed", FacesContext.getCurrentInstance().isValidationFailed());
        Map<String, Object> params = requestContext.getCallbackParams();

        try {
            for (String paramName : params.keySet()) {
                Map<String, String> callbackParamExtension = new HashMap<String, String>();
                callbackParamExtension.put("primefacesCallbackParam", paramName);

                startExtension(callbackParamExtension);

                Object paramValue = params.get(paramName);
                String json = isBean(paramValue) ? "{\"" + paramName + "\":" + new JSONObject(paramValue).toString() + "}" : new JSONObject().put(paramName, paramValue).toString();
                write(json);

                endExtension();
            }
        } catch (Exception exception) {
            throw new AbortProcessingException(exception);
        } finally {
            requestContext.release();
        }
            
        super.endDocument();
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
