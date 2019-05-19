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
