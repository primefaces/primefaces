/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.visit;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

public class UIDataContextCallback implements ContextCallback {

	private String dragId;
	private Object data;

	public UIDataContextCallback(String dragId) {
		this.dragId = dragId;
	}

	public void invokeContextCallback(FacesContext fc, UIComponent component) {
		UIData uiData = (UIData) component;
		String[] idTokens = dragId.split(String.valueOf(UINamingContainer.getSeparatorChar(fc)));
		int rowIndex = Integer.parseInt(idTokens[idTokens.length - 2]);
		uiData.setRowIndex(rowIndex);
		data = uiData.getRowData();
		uiData.setRowIndex(-1);
	}

	public Object getData() {
		return data;
	}
}
