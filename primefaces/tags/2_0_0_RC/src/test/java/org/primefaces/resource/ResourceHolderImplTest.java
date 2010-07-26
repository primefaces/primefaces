/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.resource;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourceHolderImplTest {

	private ResourceHolderImpl resourceHolder;
	
	@Before
	public void setup() {
		resourceHolder = new ResourceHolderImpl();
	}
	
	@After
	public void teardown() {
		resourceHolder = null;
	}
	
	@Test
	public void shouldContainAddedResources() {
		resourceHolder.addResource("/yui/resource1.css");
		resourceHolder.addResource("/pf/resource2.js");
		
		assertEquals(2, resourceHolder.getResources().size());
		assertTrue(resourceHolder.getResources().contains("/yui/resource1.css"));
		assertTrue(resourceHolder.getResources().contains("/pf/resource2.js"));
	}
	
	@Test
	public void shouldNotAddSameResourceMoreThanOnce() {
		resourceHolder.addResource("/yui/resource1.css");
		resourceHolder.addResource("/pf/resource2.js");
		resourceHolder.addResource("/yui/resource1.css");
		
		assertEquals(2, resourceHolder.getResources().size());
		assertTrue(resourceHolder.getResources().contains("/yui/resource1.css"));
		assertTrue(resourceHolder.getResources().contains("/pf/resource2.js"));
	}
}
