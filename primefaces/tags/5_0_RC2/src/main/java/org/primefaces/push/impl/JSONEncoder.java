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
package org.primefaces.push.impl;

import java.util.Map;
import org.atmosphere.config.managed.Encoder;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.util.BeanUtils;

public class JSONEncoder implements Encoder<Object, String> {

    private final static String PRIMITIVE_DATA = "pfpd";
    
    public String encode(Object object) {
        if(object == null) {
            return null;
        }
        
        try {
            String json;
            
            if(object instanceof Map) {
                json = new JSONObject((Map) object).toString();
            }
            else if (BeanUtils.isBean(object.getClass())) {
                json = new JSONObject(object).toString();
            }  else {
                json = new JSONObject().put(PRIMITIVE_DATA, object).toString();
            }

            return json;
        } 
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
