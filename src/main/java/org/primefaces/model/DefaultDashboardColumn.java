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
package org.primefaces.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DefaultDashboardColumn implements DashboardColumn, Serializable {

	private List<String> widgets;
	
	public DefaultDashboardColumn() {
		widgets = new LinkedList<String>();
	}
	
	public void removeWidget(String widgetId) {
		widgets.remove(widgetId);
	}

	public List<String> getWidgets() {
		return widgets;
	}
	
	public int getWidgetCount() {
		return widgets.size();
	}
	
	public String getWidget(int index) {
		return widgets.get(index);
	}
	
	public void addWidget(int index, String widgetId) {
        widgets.add(index, widgetId);
	}

	public void reorderWidget(int index, String widgetId) {
		widgets.remove(widgetId);
		widgets.add(index, widgetId);
	}

	public void addWidget(String widgetId) {
		widgets.add(widgetId);
	}
}
