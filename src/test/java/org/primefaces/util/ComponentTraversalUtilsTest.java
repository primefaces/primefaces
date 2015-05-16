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
package org.primefaces.util;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class ComponentTraversalUtilsTest {
    
	@Test
	public void closestForm() {
		UIForm outerForm = new UIForm();
		UIForm innerForm = new UIForm();
		UINamingContainer container = new UINamingContainer();
		UIComponent cmp = new UIOutput();

		innerForm.getChildren().add(cmp);
		container.getChildren().add(innerForm);
		outerForm.getChildren().add(container);

		UIComponent result = ComponentTraversalUtils.closestForm(null, cmp);
		assertSame("Expected closest surrounding UIForm", innerForm, result);
	}

	@Test
	public void closestNamingContainer() {
		UINamingContainer outerContainer = new UINamingContainer();
		UINamingContainer innerContainer = new UINamingContainer();
		UIForm form = new UIForm();
		UIComponent cmp = new UIOutput();

		innerContainer.getChildren().add(cmp);
		form.getChildren().add(innerContainer);
		outerContainer.getChildren().add(form);

		UIComponent result = ComponentTraversalUtils.closestNamingContainer(cmp);
		assertSame("Expected closest surrounding UIForm", innerContainer, result);
	}
    
}
