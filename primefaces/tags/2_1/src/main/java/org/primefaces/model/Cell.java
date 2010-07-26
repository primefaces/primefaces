/*
 * Copyright 2010 Prime Technology.
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

public class Cell {
	
	private Object rowData;
	
	private String columnId;
	
	private Object value;
	
	public Cell(Object rowData, String columnId) {
		this.rowData = rowData;
		this.columnId = columnId;
	}
	
	public Cell(Object rowData, String columnId, Object value) {
		this.rowData = rowData;
		this.columnId = columnId;
		this.value = value;
	}

	public Object getRowData() {
		return rowData;
	}

	public void setRowData(Object rowData) {
		this.rowData = rowData;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
