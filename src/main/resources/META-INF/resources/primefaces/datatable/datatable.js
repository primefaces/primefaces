PrimeFaces.widget.DataTable = function(clientId, columnDef, dataSource, cfg) {
	this.clientId = clientId;
	this.selectParam = this.clientId + "_selected";
	this.pageParam = this.clientId + "_page";
	
	YAHOO.widget.DataTable.CLASS_DATATABLE = "ui-datatable ui-widget";
	YAHOO.widget.DataTable.CLASS_LINER = "ui-datatable-liner";
	YAHOO.widget.DataTable.CLASS_LABEL = "ui-datatable-label";
	YAHOO.widget.DataTable.CLASS_DATA = "ui-datatable-data";
	YAHOO.widget.DataTable.CLASS_DATA = "ui-datatable-data";
	YAHOO.widget.DataTable.CLASS_EVEN = "ui-datatable-even ui-widget-content";
	YAHOO.widget.DataTable.CLASS_ODD = "ui-datatable-odd ui-widget-content";
	YAHOO.widget.DataTable.CLASS_LAST = "ui-datatable-last";
	YAHOO.widget.DataTable.CLASS_FIRST = "ui-datatable-first";
	YAHOO.widget.DataTable.CLASS_SORTABLE = "ui-datatable-sortable";
	YAHOO.widget.DataTable.CLASS_ASC = "ui-datatable-asc";
	YAHOO.widget.DataTable.CLASS_DESC = "ui-datatable-desc";
	YAHOO.widget.DataTable.CLASS_HIDDEN = "ui-datatable-hidden";
	YAHOO.widget.DataTable.CLASS_HIGHLIGHTED = "ui-state-hover";
	YAHOO.widget.DataTable.CLASS_SELECTED = "ui-state-active";
	
	PrimeFaces.widget.DataTable.superclass.constructor.call(this, clientId + "_container", columnDef, dataSource, cfg);
	
	jQuery('.ui-datatable table').addClass('ui-widget-content');
	jQuery('.ui-datatable table thead tr th').addClass('ui-state-default');
	
	this.initialize(clientId);
}

PrimeFaces.widget.DataTableExtensions = {

	initialize : function(clientId) {
		//Row selection
		if(this.isRowSelectionEnabled()) {
			this.subscribe("rowMouseoverEvent", this.onEventHighlightRow);
	        this.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow);
	        this.subscribe('rowSelectEvent', this.onRowSelect);
	        this.subscribe('rowUnselectEvent', this.onRowUnselect);
	        this.subscribe('unselectAllRowsEvent', this.onUnselectAllRows);
	        
	        if(this.configs.dblClickSelect)
	        	this.subscribe("rowDblclickEvent", this.handleRowClickEvent);
	        else
	        	this.subscribe("rowClickEvent", this.handleRowClickEvent);
		}
		
		//Cell selection
		if(this.isCellSelectionEnabled()) {
			this.subscribe("cellClickEvent", this.onEventSelectCell);
			this.subscribe("cellMouseoverEvent", this.onEventHighlightCell);
			this.subscribe("cellMouseoutEvent", this.onEventUnhighlightCell);
			this.subscribe("cellSelectEvent", this.onCellSelect);
			this.subscribe("cellUnselectEvent", this.onCellUnselect);
			this.subscribe("unselectAllCellsEvent", this.onUnselectAllCells);
		}
		
		//Initialize filters
		this.filters = {};
		if(!this.isDynamic()) {
			this.initialData = this.getDataSource().parseHTMLTableData(null, this.getDataSource().liveData).results;
		}
	},
	
	onPaginatorChangeRequest : function(paginatorState) {
		if(this.isDynamic()) {
			this.showTableMessage(this.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
			 
			// Update paginator state
			var state = this.getState();
			state.pagination = paginatorState;
			state.ajaxPage = true;
			
			if(this.isSelectionEnabled()) {
				this.clearSelections();
			}
			
            //Send ajax sort request
            this.loadDynamicData(state, false);
            
            //Clear state
			state.ajaxPage = false;
		} else {
			document.getElementById(this.pageParam).value = paginatorState.page;
			
			paginatorState.paginator.setStartIndex(paginatorState.recordOffset, true);
			paginatorState.paginator.setRowsPerPage(paginatorState.rowsPerPage, true);
    
            // Update the UI
            this.render();
		}
	},
	
	sortColumn : function(sortColumn, sDir) {
		// Get sort direction
        var sortDir = sDir || this.getColumnSortDir(sortColumn),
        desc = (sortDir == YAHOO.widget.DataTable.CLASS_DESC) ? true : false,
        iconDir = desc ? 's' : 'n',
        th = jQuery(sortColumn.getThEl());

		jQuery(this.getTheadEl()).find('tr th span.ui-icon').remove();
		
		if(th.children('span.ui-icon').size() == 0)
			th.append('<span class="ui-icon ui-icon-triangle-1-' + iconDir + '" style="float:right"></span>');
		else
			th.children('span.ui-icon').replaceWith('<span class="ui-icon ui-icon-triangle-1-' + iconDir + '" style="float:right"></span>');
        			
        // Save for generic sort
        PrimeFaces.widget.DataTableUtils.sortColumn = sortColumn;
                    
        // Ajax sorting
        if(this.isDynamic()) {
        	this.showTableMessage(this.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
        	
            // Update state
            var state = this.getState();
            
            if(state.pagination) {
            	state.pagination.recordOffset = 0;
            }
            
            state.sortedBy = {
                key: sortColumn.key,
                dir: sortDir
            };
            
            state.ajaxSort = true;
            
            if(this.isSelectionEnabled()) {
				this.clearSelections();
			}

            //Send ajax sort request
            this.loadDynamicData(state, false);
            
            //Clear state
			state.ajaxSort = false;
        }
        // Client-side sort
        else {
        	//Generic client sort function
            var sortFunction = sortColumn.sortOptions.sortFunction ? sortColumn.sortOptions.sortFunction : PrimeFaces.widget.DataTableUtils.genericSort;
            
            // Field to sort
            var sortField = sortColumn.sortOptions.field;

            // Sort the records        
            this._oRecordSet.sortRecords(sortFunction, desc, sortField);

            // Reset to paginator if paginated
            var paginator = this.get('paginator');
            if(paginator) {
            	this.resetPageState();
            }
            
            // Update UI
            this.render();
            this.set('sortedBy', {key:sortColumn.key, dir:sortDir, column:sortColumn}); 
        }
	},
	
	filter : function(value, field) {
		
		if(value === "")
			this.filters[field] = null;
		else 
			this.filters[field] = value.toLowerCase();

		if(this.isDynamic()) {
			this.showTableMessage(this.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);
			
			 // Update state
            var state = this.getState();            
            state.ajaxFilter = true;
            
            // Reset paginator meta
            if(state.pagination) {
            	state.pagination.recordOffset = 0;
            }
            
            if(this.isSelectionEnabled()) {
				this.clearSelections();
			}
            
            //Send ajax filter request
            this.loadDynamicData(state, true);
            
            //Clear state
			state.ajaxFilter = false;
			
		} else {
			var filtered = [];
			
			for(var i = 0; i < this.initialData.length; i++) {
				var shouldAdd = true,
				data = this.initialData[i];
				
				for(var field in this.filters) {
					if(this.filters[field]) {
						var filterValue = this.filters[field],
						columnValue = PrimeFaces.widget.DataTableUtils.stripHtml(data[field].toLowerCase());
						
						// Check if value starts with filter value
						if(columnValue.indexOf(filterValue) != 0) {
							shouldAdd = false;
							break;
						}
					}
				}
				
				if(shouldAdd)
					filtered.push(data);
			}
			
			var data = {
				results : filtered
			};
			
			// Reset paginator if paginated
            var paginator = this.get('paginator');
            if(paginator) {
            	this.resetPageState();
            }
            
            this.resetPageState();
			
			//Update UI
			this.onDataReturnInitializeTable(value, data, this.getState());
		}
	},
	
	loadDynamicData : function(state, isFilter) {
		 var requestParams = this.getDynamicDataRequestParams(state);
		 
		jQuery.ajax({
    		url: this.configs.url,
    		type: "POST",
    		cache: false,
    		dataType: "xml",
    		data: requestParams,
    		context: this,
    		global: false,
    		success : function(data, status, xhr) {
        		//Assign parsed xml response elements
    			var xmlDoc = data.documentElement,
    			tableContent = xmlDoc.getElementsByTagName("table")[0].firstChild.data,
    			viewstate = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
    			rowCount = xmlDoc.getElementsByTagName("row-count")[0].firstChild.data;
    			
    			//Update state
    			PrimeFaces.ajax.AjaxUtils.updateState(viewstate);
    			
    			//Parse table content to retrieve data
    			var el = document.createElement('div');
    			el.innerHTML = tableContent;
    			var table = el.getElementsByTagName('table')[0],
    			parsedResponse = this.getDataSource().parseHTMLTableData(requestParams, table);
    			
    			//Update UI
    			if(isFilter) {
    				state.totalRecords = parseInt(rowCount);
    				this.onDataReturnInitializeTable(requestParams, parsedResponse, state);
    			}
    			else
    				this.onDataReturnSetRows(requestParams, parsedResponse, state);
    		},
    		error: function() {
    			this.showTableMessage(this.get("MSG_ERROR"), YAHOO.widget.DataTable.CLASS_ERROR);
    		}
    	});
	},
		
	getDynamicDataRequestParams : function(state) {
		var requestParams = jQuery(PrimeFaces.escapeClientId(this.configs.formId)).serialize(),
		params = {};
		params[this.clientId + "_ajaxData"] = true;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.clientId;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
		
		if(state.ajaxSort) {
			var sortDir = state.sortedBy.dir.split("-")[2];
			params[this.clientId + "_ajaxSort"] = true;
			params[this.clientId + '_sortKey'] = state.sortedBy.key; 
			params[this.clientId + '_sortDir'] = sortDir;
		}
		
		if(state.ajaxPage) {
			params[this.clientId + "_ajaxPage"] = true;
			params[this.clientId + "_first"] = state.pagination.recordOffset;
			params[this.clientId + "_rows"] = state.pagination.rowsPerPage;
			params[this.clientId + "_page"] = state.pagination.page;
		}

		if(state.ajaxFilter) {
			params[this.clientId + "_ajaxFilter"] = true;
		}
		
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params);

		return requestParams;
	},
	
	onRowSelect : function(args) {
		var rowIndex = args.record.getData('rowIndex');
		
		if(this.isSelectionModeSingle())
			this.selected[0] = rowIndex;
		else if(this.selected.length == 0 || this.getSelectedIndexPosition(rowIndex) == -1)
			this.selected.push(rowIndex);		
		
		document.getElementById(this.selectParam).value = this.selected.join(',');
		
		if(this.configs.update) {
			this.doInstantRowSelectionRequest();
		}
	},
	
	onRowUnselect : function(args) {
		var rowIndex = args.record.getData('rowIndex'),
		position = this.getSelectedIndexPosition(rowIndex);
		
		this.selected.splice(position, 1);

		document.getElementById(this.selectParam).value = this.selected.join(',');
	},
	
	onUnselectAllRows : function() {
		this.clearSelections();
	},
	
	getSelectedIndexPosition : function(data) {
		for(var i=0; i < this.selected.length; i++) {
			if(data == this.selected) {
				return i;
			}
		}
		
		return -1;
	},
	
	handleRowClickEvent : function(args) {
		var event = args.event,
		eventTarget = null;
		
		//ie
		if(event.srcElement) {
			eventTarget = event.srcElement;
		} else {
			eventTarget = event.target;
		}
		
		if(eventTarget.className === "ui-datatable-liner") {
			this.onEventSelectRow(args);
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
	
	onCellSelect : function(args) {
		var rowIndex = args.record.getData('rowIndex'),
		columnKey = args.column.getKey(),
		cellInfo = rowIndex + '#' + columnKey;
		
		if(columnKey != 'rowIndex') {
			if(this.isSelectionModeSingle())
				this.selected[0] = cellInfo;
			else
				this.selected.push(cellInfo);
				
			document.getElementById(this.selectParam).value = this.selected.join(',');
		}
	},
	
	onCellUnselect : function(args) {
		var rowIndex = args.record.getData('rowIndex'),
		columnKey = args.column.getKey(),
		cellInfo = rowIndex + '#' + columnKey,
		position = this.getSelectedIndexPosition(cellInfo);
		
		this.selected.splice(position, 1);

		document.getElementById(this.selectParam).value = this.selected.join(',');
	},
	
	onUnselectAllCells : function() {
		this.clearSelections();
	},
	
	hasFilter : function() {
		for(var key in this.filters) {
			if(this.filters[key])
				return true;
		}
			
		return false;
	},
	
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
	
	/**
	 * Restores selection UI after client side paging, sorting and initial page load for preselection
	 */
	_setSelections : function() {
		if(!this.isSelectionEnabled()) {
			return;
		}
		
		//Initialize preselection on page load
		if(!this.selected) {
			var selectedState = document.getElementById(this.selectParam).value;
			this.selected = selectedState === '' ? [] : selectedState.split(',');
		}
	
		var recordSet = this.getRecordSet();
		
		for(var i=0; i < this.selected.length; i++) {
			var selected = this.selected[i];
			
			for(var j=0; j < recordSet.getLength(); j++) {
				var rec = recordSet.getRecord(j);
				
				if(this.isCellSelectionEnabled()) {
					var cellInfo = selected.split('#');
					
					if(rec && cellInfo[0] == rec.getData('rowIndex')) {
						var col = this.getColumn(cellInfo[1]),
						tdEl = this.getTdEl({record:rec, column:col});
						
						YAHOO.util.Dom.addClass(tdEl, YAHOO.widget.DataTable.CLASS_SELECTED);
					}
					
				} else {
					if(rec && selected == rec.getData('rowIndex')) {
						var el = YAHOO.util.Dom.get(rec.getId());
						if(el) {
							YAHOO.util.Dom.addClass(el, YAHOO.widget.DataTable.CLASS_SELECTED);
			            }
					}
				}
				
			}
		}
	},

	formatTheadCell : function(elCellLabel, oColumn, oSortedBy) {
	    var sKey = oColumn.getKey();
	    var sLabel = YAHOO.lang.isValue(oColumn.label) ? oColumn.label : sKey;

	    //Add accessibility link for sortable Columns
	    if(oColumn.sortable) {
	        //Calculate the direction
	        var sSortClass = this.getColumnSortDir(oColumn, oSortedBy);
	        var bDesc = (sSortClass === YAHOO.widget.DataTable.CLASS_DESC);

	        //This is the sorted Column
	        if(oSortedBy && (oColumn.key === oSortedBy.key)) {
	            bDesc = !(oSortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC);
	        }

	        //Generate a unique HREF for visited status
	        var sHref = this.getId() + "-href-" + oColumn.getSanitizedKey();
	        
	        //Generate a dynamic TITLE for sort status
	        var sTitle = (bDesc) ? this.get("MSG_SORTDESC") : this.get("MSG_SORTASC");
	        
	        //Format the element
	        elCellLabel.innerHTML = "<a href=\"" + sHref + "\" title=\"" + sTitle + "\" class=\"" + YAHOO.widget.DataTable.CLASS_SORTABLE + "\">" + sLabel + "</a>";
	    }
	    // Just display the label for non-sortable Columns
	    else {
	        elCellLabel.innerHTML = sLabel;
	    }
	    
	    // Restore filter value if column has a filter as sorting formats column header
		for(var filterKey in this.filters) {
			if(this.filters[filterKey])
				document.getElementById(filterKey+ "_filter").value = this.filters[filterKey];
		}
	},
	
	clearSelections : function() {
		this.selected = [];
		document.getElementById(this.selectParam).value = "";
	},
	
	isSelectionEnabled : function() {
		return this.configs.selectionMode ? true : false;
	},
	
	isRowSelectionEnabled : function() {
		return this.configs.selectionMode && this.configs.selectionMode.indexOf('cell') == -1;
	},
	
	isCellSelectionEnabled : function() {
		return this.configs.selectionMode && this.configs.selectionMode.indexOf('cell') != -1;
	},
	
	isSelectionModeSingle : function() {
		return (this.configs.selectionMode == 'single' || this.configs.selectionMode == 'singlecell');
	},
	
	isDynamic : function() {
		return this.configs.dynamicData;
	},
	
	resetPageState : function() {
		document.getElementById(this.pageParam).value = 1;
		this.get('paginator').setPage(1, true);
	},
	
	_getTrTemplateEl : function (oRecord, index) {
	    // Template is already available
	    if(this._elTrTemplate) {
	        return this._elTrTemplate;
	    }
	    // Template needs to be created
	    else {
	        var d   = document,
	            tr  = d.createElement('tr'),
	            td  = d.createElement('td'),
	            div = d.createElement('div');
	    
	        // Append the liner element
	        td.appendChild(div);

	        // Create TD elements into DOCUMENT FRAGMENT
	        var df = document.createDocumentFragment(),
	            allKeys = this._oColumnSet.keys,
	            elTd;

	        // Set state for each TD;
	        var aAddClasses;
	        for(var i=0, keysLen=allKeys.length; i<keysLen; i++) {
	            // Clone the TD template
	            elTd = td.cloneNode(true);

	            // Format the base TD
	            elTd = this._formatTdEl(allKeys[i], elTd, i, (i===keysLen-2));
	                        
	            df.appendChild(elTd);
	        }
	        tr.appendChild(df);
	        this._elTrTemplate = tr;
	        return tr;
	    }   
	}
};

PrimeFaces.widget.DataTableUtils = {

	genericSort : function(a, b, desc, field) {
		var parser = PrimeFaces.widget.DataTableUtils.sortColumn.parser;
		var val1 = PrimeFaces.widget.DataTableUtils.stripHtml(a.getData(field));
		var val2 = PrimeFaces.widget.DataTableUtils.stripHtml(b.getData(field));
		        		
		if(parser === "number") {
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