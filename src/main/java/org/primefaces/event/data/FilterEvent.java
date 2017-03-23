/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.event.data;

import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class FilterEvent extends AbstractAjaxBehaviorEvent {

	private List<?> data;
    	
	public FilterEvent(UIComponent component, Behavior behavior, List<?> data) {
		super(component, behavior);
		this.data = data;
	}

    @Deprecated
    public List<?> getData() {
        return this.data;
    }

    public Map<String, Object> getFilters() {
        return ((DataTable) this.source).getFilters();
    }
}