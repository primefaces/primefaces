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
import java.util.HashMap;
import javax.faces.context.FacesContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.mock.FacesContextMock;

public class PrimePartialResponseWriterTest {

    @Before
    public void init()
    {
        FacesContext context = new FacesContextMock(new HashMap<Object, Object>());
    }

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
        
        Assert.assertEquals("\"myArray\":[&#34;test&#34;,12,1" + ",&#34;test123&amp;&#34;]", partialResponseWriter.toString());
    }

    @Test
    public void testEncodeJSONObject() throws IOException, JSONException {
        CollectingPartialResponseWriter partialResponseWriter = new CollectingPartialResponseWriter();
        PrimePartialResponseWriter primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("myStrVal", "Hello<>World!");
        jsonObject.put("isThatTrue", false);

        primePartialResponseWriter.encodeJSONObject("myObj", jsonObject);
        
        Assert.assertEquals("\"myObj\":{&#34;myStrVal&#34;:&#34;Hello&lt;&gt;World!&#34;,&#34;isThatTrue&#34;:false}", partialResponseWriter.toString());
    }

    @Test
    public void testEncodeJSONValue() throws IOException, JSONException {
        CollectingPartialResponseWriter partialResponseWriter = new CollectingPartialResponseWriter();
        PrimePartialResponseWriter primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);

        primePartialResponseWriter.encodeJSONValue("myVal", "test123>");
        Assert.assertEquals("&#34;myVal&#34;:&#34;test123&gt;&#34;", partialResponseWriter.toString());
        
        
        partialResponseWriter = new CollectingPartialResponseWriter();
        primePartialResponseWriter = new PrimePartialResponseWriter(partialResponseWriter);

        primePartialResponseWriter.encodeJSONValue("myVal2", 123);
        Assert.assertEquals("&#34;myVal2&#34;:123", partialResponseWriter.toString());
    }
}
