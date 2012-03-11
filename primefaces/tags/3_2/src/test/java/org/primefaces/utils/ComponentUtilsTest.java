/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.util.ComponentUtils;

public class ComponentUtilsTest {

	@Test
	public void shouldDecorateAttribute() {
		CommandLink link = new CommandLink();
		ComponentUtils.decorateAttribute(link, "onclick", "alert('barca');");
		
		assertEquals("alert('barca');", link.getAttributes().get("onclick"));
	}
	
	@Test
	public void shouldNotDecorateAttributeForSameValue() {
		CommandLink link = new CommandLink();
		ComponentUtils.decorateAttribute(link, "onclick", "alert('barca');");
		ComponentUtils.decorateAttribute(link, "onclick", "alert('barca');");
		ComponentUtils.decorateAttribute(link, "onclick", "alert('barca');");
		
		assertEquals("alert('barca');", link.getAttributes().get("onclick"));
	}
	
	@Test
	public void shouldEscapeJQueryId() {
		String id = "test";
		
		assertEquals("#test", ComponentUtils.escapeJQueryId(id));
		
		id="form:test";
		assertEquals("#form\\\\:test", ComponentUtils.escapeJQueryId(id));
	}
}
