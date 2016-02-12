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

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.AbstractAjaxBehaviorEvent;
import org.primefaces.model.SortOrder;

public class SortEvent extends AbstractAjaxBehaviorEvent {

	private UIColumn sortColumn;
    
    private boolean ascending;

    private int sortColumnIndex;
	
	public SortEvent(UIComponent component, Behavior behavior, UIColumn sortColumn, SortOrder order, int sortColumnIndex) {
		super(component, behavior);
		this.sortColumn = sortColumn;
        this.ascending = order.equals(SortOrder.ASCENDING);
        this.sortColumnIndex = sortColumnIndex;
	}

    public boolean isAscending() {
        return ascending;
    }

    public UIColumn getSortColumn() {
        return sortColumn;
    }

    public int getSortColumnIndex() {
        return sortColumnIndex;
    }
}