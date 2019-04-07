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
package org.primefaces.context;

import org.primefaces.mock.CollectingPartialResponseWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("resource")
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
