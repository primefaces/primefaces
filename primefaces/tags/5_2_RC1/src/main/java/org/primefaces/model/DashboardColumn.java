/*
 * Copyright 2009,2010 Prime Technology.
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

import java.util.List;

public interface DashboardColumn {
		
	public void removeWidget(String widgetId);
	
	public List<String> getWidgets();
	
	public int getWidgetCount();
	
	public String getWidget(int index);
	
	public void addWidget(String widgetId);
	
	public void addWidget(int index, String widgetId);
	
	public void reorderWidget(int index, String widgetId);
}