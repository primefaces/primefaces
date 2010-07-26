/*
 * Copyright 2009 Prime Technology.
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

import java.util.Comparator;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * Generic comparator using bean properties to compare two beans.
 */
public class BeanPropertyComparator implements Comparator {

	private ValueExpression sortByExpression;
	
	private String order;
	
	private String var;
	
	private Logger logger = Logger.getLogger(BeanPropertyComparator.class.getName());
	
	public BeanPropertyComparator(ValueExpression sortByExpression, String var, String order) {
		this.sortByExpression = sortByExpression;
		this.var = var;
		this.order = order;
	}

	@SuppressWarnings("unchecked")
	public int compare(Object obj1, Object obj2) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			
			facesContext.getExternalContext().getRequestMap().put(var, obj1);
			Object value1 = sortByExpression.getValue(facesContext.getELContext());
			facesContext.getExternalContext().getRequestMap().put(var, obj2);
			Object value2 = sortByExpression.getValue(facesContext.getELContext());
			
			int value = ((Comparable) value1).compareTo(value2);
			
			return order.equals("asc") ? value : -1 * value;
		} catch (Exception e) {
			logger.severe("Error in sorting");
			
			throw new RuntimeException(e);
		}
	}
}