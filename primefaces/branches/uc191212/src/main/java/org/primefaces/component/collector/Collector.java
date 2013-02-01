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
package org.primefaces.component.collector;

import java.util.Collection;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

public class Collector implements ActionListener, StateHolder {

	private ValueExpression addTo;

	private ValueExpression removeFrom;
	
	private ValueExpression value;

	private boolean _transient;

	public Collector() {}

	public Collector(ValueExpression addTo, ValueExpression removeFrom, ValueExpression value) {
		this.addTo = addTo;
		this.removeFrom = removeFrom;
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		if (value == null)
			throw new AbortProcessingException("@for has not been set");

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();

		Object val = (Object) value.getValue(elContext);
		
		if(addTo != null) {
			Collection collection = (Collection) addTo.getValue(elContext);
			
			if(!collection.contains(val))
				collection.add(val);
		}
		else if(removeFrom != null){
			Collection collection = (Collection) removeFrom.getValue(elContext);
			collection.remove(val);
		} else
			throw new IllegalArgumentException("Specify either addTo or removeFrom as collection reference");
	}

	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = addTo;
		state[1] = removeFrom;
		state[2] = value;
		
		return state;
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		addTo = (ValueExpression) values[0];
		removeFrom = (ValueExpression) values[1];
		value = (ValueExpression) values[2];
	}

	public boolean isTransient() {
		return _transient;
	}

	public void setTransient(boolean _transient) {
		this._transient = _transient;
	}
	
	public ValueExpression getAddTo() {
		return addTo;
	}

	public void setAddTo(ValueExpression addTo) {
		this.addTo = addTo;
	}

	public ValueExpression getRemoveFrom() {
		return removeFrom;
	}

	public void setRemoveFrom(ValueExpression removeFrom) {
		this.removeFrom = removeFrom;
	}

	public ValueExpression getValue() {
		return value;
	}

	public void setValue(ValueExpression value) {
		this.value = value;
	}
}