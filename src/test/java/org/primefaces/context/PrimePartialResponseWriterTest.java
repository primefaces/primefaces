/*
 * Copyright 2009-2015 PrimeTek.
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

import org.primefaces.mock.CollectingPartialResponseWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class PrimePartialResponseWriterTest {
    
    @Test
    public void testEncodeJSONArray() throws IOException, JSONException {
        CollectingPartialResponseWriter partialResponseWriter = new CollectingPartialResponseWriter();
        PrimePartialResponseWriter primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);
        
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("test");
        jsonArray.put(12);
        jsonArray.put(1);
        jsonArray.put("test123&");
        
        primePartialResponseWriter.encodeJSONArray("myArray", jsonArray);
        
        Assert.assertEquals("\"myArray\":[\"test\",12,1,\"test123&amp;\"]", partialResponseWriter.toString());
    }
    
    @Test
    public void testEncodeJSONObject() throws IOException, JSONException {
        CollectingPartialResponseWriter partialResponseWriter = new CollectingPartialResponseWriter();
        PrimePartialResponseWriter primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("myStrVal", "Hello<>World!");
        jsonObject.put("isThatTrue", false);

        primePartialResponseWriter.encodeJSONObject("myObj", jsonObject);
        
        Assert.assertEquals("\"myObj\":{\"myStrVal\":\"Hello&lt;&gt;World!\",\"isThatTrue\":false}", partialResponseWriter.toString());
    }
    
    @Test
    public void testEncodeJSONValue() throws IOException, JSONException {
        CollectingPartialResponseWriter partialResponseWriter = new CollectingPartialResponseWriter();
        PrimePartialResponseWriter primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);
        
        primePartialResponseWriter.encodeJSONValue("myVal", "test123>");
        Assert.assertEquals("\"myVal\":\"test123&gt;\"", partialResponseWriter.toString());
        
        
        partialResponseWriter = new CollectingPartialResponseWriter();
        primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);
        
        primePartialResponseWriter.encodeJSONValue("myVal2", 123);
        Assert.assertEquals("\"myVal2\":123", partialResponseWriter.toString());
    }
}
