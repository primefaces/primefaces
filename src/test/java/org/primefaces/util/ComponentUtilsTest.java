/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

import org.junit.Test;

public class ComponentUtilsTest {

    @Test
    public void escapeSelector() {
        String id = "test";

        assertEquals("test", ComponentUtils.escapeSelector(id));

        id = "form:test";
        assertEquals("form\\\\:test", ComponentUtils.escapeSelector(id));
    }

    @Test
    public void createContentDisposition() {
        assertEquals("attachment;filename=\"Test%20Spaces.txt\"; filename*=UTF-8''Test%20Spaces.txt", ComponentUtils.createContentDisposition("attachment", "Test Spaces.txt"));
    }
    
    @Test
    public void getHrefURLWithExternalLinks() {
        Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
                
        params.put("param1", Arrays.asList("value1"));
        params.put("param2", Arrays.asList("enc?de&"));//URLEncoded: enc%3Fde%26
        params.put("param3", Arrays.asList("two", "v@lues"));//v@lues URLEncoded: v%40lues
        
        Map<String, String> testCases = new LinkedHashMap<String, String>();
        
        //No query string nor fragment
        testCases.put("https://foo.bar/some/path", 
                "https://foo.bar/some/path?param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues");
        testCases.put("/internal/other/path", 
                "/internal/other/path?param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues");
        
        //Just Fragment
        testCases.put("https://foo.bar/some/path#frg1", 
                "https://foo.bar/some/path?param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues#frg1");
        testCases.put("/internal/other/path#frg1", 
                "/internal/other/path?param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues#frg1");
        
        //Just query string
        testCases.put("https://foo.bar/some/path?q1=123&q2=456",
                "https://foo.bar/some/path?q1=123&q2=456&param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues");
        testCases.put("/internal/other/path?q1=123&q2=456", 
                "/internal/other/path?q1=123&q2=456&param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues");
        
        //Query string and fragment
        testCases.put("https://foo.bar/some/path?q1=123&q2=456#frg1",
                "https://foo.bar/some/path?q1=123&q2=456&param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues#frg1");
        testCases.put("/internal/other/path?q1=123&q2=456#frg1", 
                "/internal/other/path?q1=123&q2=456&param1=value1&param2=enc%3Fde%26&param3=two&param3=v%40lues#frg1");
        
        //Testing
        for (Map.Entry<String, String> test : testCases.entrySet()) {
            assertEquals(ComponentUtils.getHrefURL(test.getKey(), params), test.getValue());
        }
    }
}
