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
package org.primefaces.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONObjectTest {

	@Test
	public void testBooleanToJSON() {
		try {
			String json = new JSONObject().put("valid", true).toString();
			assertEquals("{\"valid\":true}", json);
		} catch (JSONException e) {
			fail();
		}
	}

	@Test
	public void testPojoToJSON() throws JSONException {
		JSONObject json = new JSONObject(new Person("Cagatay", "Civici"));
        assertNotNull(json.get("firstname"));
        assertEquals("Cagatay", json.get("firstname"));
        assertNotNull(json.get("lastname"));
		assertEquals("Civici", json.get("lastname"));
        assertEquals(2, json.length());
	}

    @Test
    public void testListToJSONArray() throws JSONException {
        List<String> myList = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            myList.add("item" + i);
        }

        assertEquals(5, new JSONArray(myList).length());
    }

    @Test
    public void testMapToJSONObject() throws JSONException {
        Map<String, String> myMap = new HashMap<String, String>();
        for (int j = 0; j < 5; j++) {
            myMap.put("key" + j, "value" + j);
        }

        assertEquals(5, new JSONObject(myMap).length());
    }

	static public class Person {

		private String firstname;
		private String lastname;

		public Person() {}

		public Person(String firstname, String lastname) {
			this.firstname = firstname;
			this.lastname = lastname;
		}
		public String getFirstname() {
			return firstname;
		}
		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}
		public String getLastname() {
			return lastname;
		}
		public void setLastname(String lastname) {
			this.lastname = lastname;
		}
	}
}
