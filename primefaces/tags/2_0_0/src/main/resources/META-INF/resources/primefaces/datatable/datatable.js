PrimeFaces.widget.DataTable = function(clientId, columnDef, dataSource, cfg) {
	PrimeFaces.widget.DataTable.superclass.constructor.call(this, clientId + "_container", columnDef, dataSource, cfg);
	
	this.initialize(clientId);
}

PrimeFaces.widget.ScrollingDataTable = function(clientId, columnDef, dataSource, cfg) {
	PrimeFaces.widget.ScrollingDataTable.superclass.constructor.call(this, clientId + "_container", columnDef, dataSource, cfg);
	
	this.initialize(clientId);
}

PrimeFaces.widget.DataTableExtensions = {

	initialize : function(clientId) {
		this.clientId = clientId;
		this.rowSelectParam = this.clientId + "_selectedRows";
		this.pageParam = this.clientId + "_currentPage";
		this.selectedRowsState = [];
		
		//Row selection handlers
		if(this.configs.selectionMode) {
			this.subscribe("rowMouseoverEvent", this.onEventHighlightRow);
	        this.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow);
	        
	        if(this.configs.dblClickSelect)
	        	this.subscribe("rowDblclickEvent", this.handleRowClickEvent);
	        else
	        	this.subscribe("rowClickEvent", this.handleRowClickEvent);
		}
		
		//Column selection handlers
		if(this.configs.columnSelectionMode) {
			if(this.configs.columnSelectionMode == 'single')
				this.subscribe("radioClickEvent", this.handleRadioClickEvent);
			else if(this.configs.columnSelectionMode == 'multiple')
				this.subscribe("checkboxClickEvent", this.handleCheckboxClickEvent);
		}
		
		//Preselection
		if(this.isSelectionEnabled()) {
			var preSelectedRowIndexesValue = document.getElementById(this.rowSelectParam).value;
			
			if(preSelectedRowIndexesValue != "") {
				this.selectedRowsState = preSelectedRowIndexesValue.split(',');
				this.restoreSelectedRows();
			}
		}
		
		//Syncs state and total records meta
		if(this.isDynamic()) {
			this.getDataSource().subscribe('responseEvent', this.handleDynamicDataResponse, this, true);
		}
 
		//Sets page index
		if(this.configs.paginator) {
			this.doBeforePaginatorChange = function(state) {
				this.showTableMessage(this.get("MSG_LOADING"), this.CLASS_LOADING);
				document.getElementById(this.pageParam).value = state.page;
				
				if(this.isDynamic() && this.isSelectionEnabled()) {
					this.keepSelectedRowsState = true;
				}
				
				return true;
			}
		}
		
		//Configurations before sorting
		this.doBeforeSortColumn = function(col, order) {
			this.showTableMessage(this.get("MSG_LOADING"), this.CLASS_LOADING);
			if(!this.isDynamic()) {
				PrimeFaces.widget.DataTableUtils.sortColumn = col;
			}
			
			if(this.isDynamic() && this.isSelectionEnabled()) {
				this.keepSelectedRowsState = true;
			}
			
			return true;
		}
 
		//Initializes client side filters
		if(this.configs.filter) {
			if(!this.isDynamic()) {
				this.filters = {};
				this.setupLocalDataFilter();
			}
		}

		//Configuration to make selections stateful among dynamic requests
		if(this.isDynamic() && this.isSelectionEnabled()) {
			this.handleDataReturnPayload = this.restoreSelectedRows;
			this.subscribe("rowSelectEvent", this.handleRowSelect);
			this.subscribe("rowUnselectEvent", this.handleRowUnselect);
			this.subscribe("unselectAllRowsEvent", this.handleAllRowsUnselect);
		}
	},
		
	handleRowClickEvent : function(args) {
		this.onEventSelectRow(args);
	
		if(this.isDynamic()) {
			document.getElementById(this.rowSelectParam).value = this.selectedRowsState.join(',');
		} else {
			var selectedRows = this.getSelectedRows(),
			selectedRowIndexes = [];
			
			for(var i=0; i < selectedRows.length; i++) {
				selectedRowIndexes[i] = this.getRecord(selectedRows[i]).getData('rowIndex');
			}
			
			document.getElementById(this.rowSelectParam).value = selectedRowIndexes.join(',');
		}
		
		if(this.configs.update) {
			this.doInstantRowSelectionRequest();
		}
	},
	
	handleRowSelect : function(args) {
		if(this.isSelectionModeSingle()) {
			this.selectedRowsState = [];
		}
		
		var rowIndex = args.record.getData('rowIndex');
		
		if(jQuery.inArray(rowIndex, this.selectedRowsState) == -1)
			this.selectedRowsState.push(rowIndex);
	},
	
	handleAllRowsUnselect : function() {
		if(this.keepSelectedRowsState)
			this.keepSelectedRowsState = false;
		else
			this.selectedRowsState = [];
	},

	handleRowUnselect : function(args) {
		for(var i = 0; i < this.selectedRowsState.length; i++) {
			if(this.selectedRowsState[i] == args.record.getData('rowIndex')) {
				this.selectedRowsState.splice(i, 1);
				break;
			}
		}
	},
	
	doInstantRowSelectionRequest : function() {
		var requestConfig = {formId:this.configs.formId};
		
		var params = {};
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.configs.update;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
		
		if(this.configs.onselectStart)
			requestConfig.onstart = this.configs.onselectStart;
		if(this.configs.onselectComplete)
			requestConfig.oncomplete = this.configs.onselectComplete;
		
		PrimeFaces.ajax.AjaxRequest(this.configs.url, requestConfig, params);
	},
	
	handleRadioClickEvent : function(args) {
		var record = this.getRecord(args.target),
		selectedRows = this.getSelectedRows();
		
		if(selectedRows.length === 1) {
			 var oldRecord = this.getRecord(selectedRows[0]);
			 oldRecord.setData('pf_column_select', false);
			 this.unselectRow(oldRecord);
		 }
		 
		 this.selectRow(record);
		 record.setData('pf_column_select', true);
		 
		 document.getElementById(this.rowSelectParam).value = record.getData('rowIndex');
	},
	
	handleCheckboxClickEvent : function(args) {
		var checkbox = args.target,
		record = this.getRecord(checkbox);
	
	   	if(checkbox.checked) {
	   		record.setData('pf_column_select', true);
	   		this.selectRow(record);
	   	}
	   	else {
	   		record.setData('pf_column_select', false);
	   		this.unselectRow(record);
	   	}
	   	
	   	if(this.isDynamic()) {
	   		document.getElementById(this.rowSelectParam).value = this.selectedRowsState.join(',');
	   	} else {
	   		var selectedRows = this.getSelectedRows(),
		   	selectedRowIndexes = [];
		   	for(var i=0; i < selectedRows.length; i++) {
		   		selectedRowIndexes[i] = this.getRecord(selectedRows[i]).getData('rowIndex');
		   	}
		   	document.getElementById(this.rowSelectParam).value = selectedRowIndexes.join(',');
	   	}
	},
	
	handleDynamicDataResponse : function(args) {
		var el = document.createElement('div');
	    el.innerHTML = args.response.responseText,
	    doc = el.childNodes[0];
	    
	    for(var i=0; i < doc.childNodes.length; i++) {
	    	var node = doc.childNodes[i];
	    	
	    	if(node.tagName === 'INPUT') {
	    		jQuery("input[name=javax.faces.ViewState]").val(node.value);
	    	}
	    	else if(node.tagName === 'SPAN') {
	    		args.callback.argument.totalRecords = node.innerHTML;
	    	}
	    }
	},
	
	filter : function(value, field) {
		if(this.isDynamic()) {
			this.showTableMessage(this.get("MSG_LOADING"), this.CLASS_LOADING);
			
			var state = this.getState(),
			request = this.get("generateRequest")(state, this);
			
	        this.getDataSource().sendRequest(request,  {
        		success : function(sRequest, oResponse, oPayload) {
 							var paginator = this.get('paginator');
 							this.onDataReturnInitializeTable(sRequest, oResponse, oPayload);
 							paginator.set('totalRecords', oPayload.totalRecords);
 				},
                failure : this.onDataReturnInitializeTable,
                argument : state, 
                scope : this
	        });
		} else {
			if(value === "")
	       		this.filters[field] = null;
	       	else 
	       		this.filters[field] = value.toLowerCase();
	       	
			 this.getDataSource().sendRequest(value, {
	         	success : function(sRequest, oResponse, oPayload) {
	         					var paginator = this.get('paginator');
	         					this.onDataReturnInitializeTable(sRequest, oResponse, oPayload);
	         					paginator.set('totalRecords', oResponse.results.length);
	         	},
	         	failure : this.onDataReturnInitializeTable,
	         	scope   : this,
	         	argument: [this]
	     	});
		}
	},
	
	hasFilter : function() {
		for(var key in this.filters) {
			if(this.filters[key])
				return true;
		}
			
		return false;
	},
	
	setupLocalDataFilter : function() {
		this.getDataSource().doBeforeCallback = function(req, raw, res, cb) {
			var table = cb.argument[0];
			
			if(table.hasFilter()) {
				var data = res.results, 
				filtered = [], 
				req = req.toLowerCase();

				for(var i = 0; i < data.length; i++) {
					var shouldAdd = false;

					for(var filter in table.filters) {
						if(table.filters[filter]) {
							var filterValue = table.filters[filter],
							columnValue = PrimeFaces.widget.DataTableUtils.stripHtml(data[i][filter]).toLowerCase();
	
							if(!columnValue.indexOf(filterValue)) {
								shouldAdd = true;
							} else {
								shouldAdd = false;
								break;
							}
						}
					}

					if(shouldAdd) {
						filtered.push(data[i]);
					}
				}

				res.results = filtered;
				res.meta = { totalRecords : res.results.length }; 
			}

			return res;
		}
	},
	
	restoreSelectedRows : function(oRequest, oResponse, oPayload) {
		if(this.selectedRowsState) {
			for(var i=0; i < this.selectedRowsState.length; i++) {
				
				for(var iter in this.getRecordSet().getRecords()) {
					var rec = this.getRecordSet().getRecords()[iter];
					
					if(rec.getData('rowIndex') == this.selectedRowsState[i]) {
						this.selectRow(rec);
						
						if(this.configs.columnSelectionMode) {
							rec.setData('pf_column_select', true);
							
							if(!this.isDynamic()) {
								try {
									var tdEl = this.getTdLinerEl({record:rec, column:this.getColumn('pf_column_select')});
									if(tdEl != null)
										tdEl.childNodes[0].checked = true;
								}catch(exception) {
									//td does not exist
								}
							}
						}
						
						break;
					}
				}
			}
		}
		
	    return oPayload;
	},
	
	//prevent sort if it is a filtering event
	onEventSortColumn : function(args) {
		var event = args.event,
		eventTarget = null;
		
		//ie
		if(event.srcElement) {
			eventTarget = event.srcElement;
		} else {
			eventTarget = event.target;
		}
		
		if(eventTarget.tagName.toLowerCase() != 'input') {
			var el = this.getThEl(args.target) || this.getTdEl(args.target);
			
			if(el) {
				var oColumn = this.getColumn(el);
				if(oColumn.sortable) {
					YAHOO.util.Event.stopEvent(event);
					this.sortColumn(oColumn);
				}
		 	}
		} else {
			YAHOO.util.Event.stopEvent(event);
		}
	},
	
	isSelectionEnabled : function() {
		return (this.configs.selectionMode || this.configs.columnSelectionMode) ? true : false;
	},
	
	isSelectionModeSingle : function() {
		return (this.configs.selectionMode == 'single' || this.configs.columnSelectionMode == 'single');
	},
	
	isDynamic : function() {
		return this.configs.dynamicData;
	}
};

PrimeFaces.widget.DataTableUtils = {
		
	loadDynamicData : function(state, dt) {
		var requestParams = jQuery(PrimeFaces.escapeClientId(dt.configs.formId)).serialize(),
		params = {};
		
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = dt.clientId;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = dt.clientId;
		
		if(state.pagination) {
			params['first']= state.pagination.recordOffset;
		}
		if(state.sortedBy) {
			var sortDir = state.sortedBy.dir.split("-")[2];
			params['sortKey'] = state.sortedBy.key; 
			params['sortDir'] = sortDir;
		}
		
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
		
		return requestParams;
	},
	
	loadInitialData : function(clientId, formId) {
		var requestParams = jQuery(PrimeFaces.escapeClientId(formId)).serialize(),
		params = {};
		
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = clientId;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = clientId;
		
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
		
		return requestParams;
	},
	
	genericSort : function(a, b, desc, field) {
		var parser = PrimeFaces.widget.DataTableUtils.sortColumn.parser;
		var val1 = PrimeFaces.widget.DataTableUtils.stripHtml(a.getData(field));
		var val2 = PrimeFaces.widget.DataTableUtils.stripHtml(b.getData(field));
		        		
		if(parser == "number") {
			val1 = YAHOO.util.DataSource.parseNumber(val1);
			val2 = YAHOO.util.DataSource.parseNumber(val2);
			
			if(val1 == val2)
				return 0;
			else if(val1 > val2)
				return desc ? -1 : 1;
			else
				return desc ? 1 : -1;
		} else if(parser === "date") {
			val1 = YAHOO.util.DataSource.parseDate(val1);
			val2 = YAHOO.util.DataSource.parseDate(val2);
			
			if(val1 == val2)
				return 0;
			else if(val1 > val2)
				return desc ? -1 : 1;
			else
				return desc ? 1 : -1;
		}	
		else {
			return YAHOO.util.Sort.compare(val1, val2, desc);
		}
	},
	
	stripHtml : function(value) {
		return  value.replace(/(<([^>]+)>)/ig,""); 
	}
};

YAHOO.lang.extend(PrimeFaces.widget.DataTable, YAHOO.widget.DataTable, PrimeFaces.widget.DataTableExtensions);

YAHOO.lang.extend(PrimeFaces.widget.ScrollingDataTable, YAHOO.widget.ScrollingDataTable, PrimeFaces.widget.DataTableExtensions);

