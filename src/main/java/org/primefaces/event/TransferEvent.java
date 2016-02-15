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
package org.primefaces.event;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

public class TransferEvent extends AbstractAjaxBehaviorEvent {

    private List<?> items;
    private boolean add;

	public TransferEvent(UIComponent component, Behavior behavior, List<?> items, boolean add) {
		super(component, behavior);
        this.items = items;
        this.add = add;
	}

    public boolean isAdd() {
        return add;
    }
    
    public boolean isRemove() {
        return !add;
    }

    public List<?> getItems() {
        return items;
    }
}