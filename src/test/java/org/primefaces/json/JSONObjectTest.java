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
package org.primefaces.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
