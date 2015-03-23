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

import static org.junit.Assert.*;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;

import org.junit.Test;
import org.primefaces.component.commandlink.CommandLink;

public class ComponentUtilsTest {
	
	@Test
	public void shouldEscapeJQueryId() {
		String id = "test";
		
		assertEquals("#test", ComponentUtils.escapeJQueryId(id));
		
		id="form:test";
		assertEquals("#form\\\\:test", ComponentUtils.escapeJQueryId(id));
	}

	@Test
	public void shouldFindParentForm() {
		UIForm outerForm = new UIForm();
		UIForm innerForm = new UIForm();
		UINamingContainer container = new UINamingContainer();
		UIComponent cmp = new UIOutput();

		innerForm.getChildren().add(cmp);
		container.getChildren().add(innerForm);
		outerForm.getChildren().add(container);

		UIComponent result = ComponentUtils.findParentForm(null, cmp);
		assertSame("Expected closest surrounding UIForm", innerForm, result);
	}

	@Test
	public void shouldFindParentContainer() {
		UINamingContainer outerContainer = new UINamingContainer();
		UINamingContainer innerContainer = new UINamingContainer();
		UIForm form = new UIForm();
		UIComponent cmp = new UIOutput();

		innerContainer.getChildren().add(cmp);
		form.getChildren().add(innerContainer);
		outerContainer.getChildren().add(form);

		UIComponent result = ComponentUtils.findParentNamingContainer(cmp);
		assertSame("Expected closest surrounding UIForm", innerContainer, result);
	}
}
