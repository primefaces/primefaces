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
package org.primefaces.component.export;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.util.ComponentUtils;

public class DataExporter implements ActionListener, StateHolder {

	private ValueExpression target;
	
	private ValueExpression type;
	
	private ValueExpression fileName;
	
	private ValueExpression excludeColumns;
	
	private MethodExpression preProcessor;
	
	private MethodExpression postProcessor;
	
	public DataExporter() {}

	public DataExporter(ValueExpression target, ValueExpression type, ValueExpression fileName, ValueExpression exludeColumns, MethodExpression preProcessor, MethodExpression postProcessor) {
		this.target = target;
		this.type = type;
		this.fileName = fileName;
		this.excludeColumns = exludeColumns;
		this.preProcessor = preProcessor;
		this.postProcessor = postProcessor;
	}

	public void processAction(ActionEvent event){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
		
		String tableId = (String) target.getValue(elContext);
		String exportAs = (String) type.getValue(elContext);
		String outputFileName = (String) fileName.getValue(elContext);
		int[] excludedColumnIndexes = null;
		
		if(excludeColumns != null) {
			excludedColumnIndexes = resolveExcludedColumnIndexes((String) excludeColumns.getValue(elContext));
		}
		
		try {
			Exporter exporter = ExporterFactory.getExporterForType(exportAs);
			UIData table = (UIData) ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), tableId);
			
			exporter.export(facesContext, table, outputFileName, excludedColumnIndexes, preProcessor, postProcessor);
			
			facesContext.responseComplete();
		} catch (IOException e) {
			throw new FacesException(e);
		}
	}

	private int[] resolveExcludedColumnIndexes(String columnsToExclude) {
		String[] columnIndexesAsString = columnsToExclude.split(",");
		int[] indexes = new int[columnIndexesAsString.length];
		
		for(int i=0; i < indexes.length; i++) 
			indexes[i] = Integer.parseInt(columnIndexesAsString[i].trim());
		
		return indexes;
	}

	public boolean isTransient() {
		return false;
	}

	public void setTransient(boolean value) {
		//NoOp
	}
	
	 public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;

		target = (ValueExpression) values[0];
		type = (ValueExpression) values[1];
		fileName = (ValueExpression) values[2];
		excludeColumns = (ValueExpression) values[3];
		preProcessor = (MethodExpression) values[4];
		postProcessor = (MethodExpression) values[5];
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[6];

		values[0] = target;
		values[1] = type;
		values[2] = fileName;
		values[3] = excludeColumns;
		values[4] = preProcessor;
		values[5] = postProcessor;
		
		return ((Object[]) values);
	}
}
