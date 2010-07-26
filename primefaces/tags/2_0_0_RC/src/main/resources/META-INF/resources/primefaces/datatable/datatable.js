if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.DataTable = function(clientId, columnDef, dataSource, cfg) {
	PrimeFaces.widget.DataTable.superclass.constructor.call(this, clientId + "_container", columnDef, dataSource, cfg);
	this.clientId = clientId;
	this.cfg = cfg;
	this.rowSelectParam = this.clientId + "_selectedRows";
	this.firstRowParam = this.clientId + "_firstRow";
	
	if(cfg.selectionMode) {
		this.subscribe("rowMouseoverEvent", this.onEventHighlightRow);
        this.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow);
        this.subscribe("rowClickEvent", this.handleRowClickEvent);
	}
	
	if(cfg.columnSelectionMode) {
		this.selectedRows = new Array();

		if(cfg.columnSelectionMode == 'single')
			this.subscribe("radioClickEvent", this.handleRadioClickEvent);
		else if(cfg.columnSelectionMode == 'multiple')
				this.subscribe("checkboxClickEvent", this.handleCheckboxClickEvent);
	}
	
	 for(var i=0; i < columnDef.length; i++) {
		 if(columnDef[i].filter) {
			 var columnKey = dataSource.responseSchema.fields[i].key;
			 
			 var columnHeader = '#' + this.getId() + "-th-" + columnKey;
			 var columnSelector = this.getId() + "-col-" + columnKey;
			
			 jQuery(columnHeader).append('<input type="text" onkeyup="PrimeFaces.widget.DataTableUtils.filterColumn(\'' + this.clientId + '\', this.value, \'' + columnSelector + '\')"/>');
		 }
	 }
	 
	 if(cfg.dynamicData) {
		 this.getDataSource().subscribe('responseEvent', this.handleDynamicDataResponse);
	 }
}

YAHOO.lang.extend(PrimeFaces.widget.DataTable, YAHOO.widget.DataTable,
{
	handleRowClickEvent : function(event, target) {
		this.onEventSelectRow(event, target);
		
		var selectedRows = this.getSelectedRows().join(',');
		document.getElementById(this.rowSelectParam).value = selectedRows;
		document.getElementById(this.firstRowParam).value = this.getRecordSet().getRecord(0).getId();
		
		if(this.cfg.update) {
			var requestConfig = {
					partialSubmit:true,
					formClientId:this.cfg.formId
			};
			
			if(this.cfg.onselectStart)
				requestConfig.onstart = this.cfg.onselectStart;
			if(this.cfg.onselectComplete)
				requestConfig.oncomplete = this.cfg.onselectComplete;
			
			PrimeFaces.ajax.AjaxRequest(this.cfg.url, requestConfig, "update=" + this.cfg.update + "&" 
						+ this.rowSelectParam + "=" + selectedRows + "&" + this.firstRowParam + "=" + this.getRecordSet().getRecord(0).getId());
		}
	},
	
	handleRadioClickEvent : function(args) {
		 var radio = args.target,
		 record = this.getRecord(radio);
		 
		 document.getElementById(this.rowSelectParam).value = record.getId();
		 document.getElementById(this.firstRowParam).value = this.getRecordSet().getRecord(0).getId();
	},
	
	handleCheckboxClickEvent : function(args) {
		var checkbox = args.target,
		record = this.getRecord(checkbox)

       	if(checkbox.checked)
       		this.selectedRows.push(record.getId());
       	else {
       		var index = jQuery.inArray(record.getId(), this.selectedRows);
       		
       		this.selectedRows.splice(index,1);
       	}
       	
       	document.getElementById(this.rowSelectParam).value = this.selectedRows.join(',');
       	document.getElementById(this.firstRowParam).value = this.getRecordSet().getRecord(0).getId();
	},
	
	filter : function(text) {
		var jqClientId = PrimeFaces.escapeClientId(this.clientId);
		
		jQuery(jqClientId + " table .yui-dt-data").find('tr').hide();
		jQuery(jqClientId + " table .yui-dt-data").find('td:contains("' + text + '")').parents('tr').show();
	},

	handleDynamicDataResponse: function(args) {
		var el = document.createElement('div');
        el.innerHTML = args.response.responseText;
        jsfstate = el.getElementsByTagName('input')[0];
        jQuery("input[name=" + this.VIEW_STATE_PARAM + "]").val(jsfstate.value);
	}
});

PrimeFaces.widget.DataTableUtils = {
		
	filterColumn : function(id, text, column) {
		var jqClientId = PrimeFaces.escapeClientId(id);
		
		jQuery(jqClientId + " table .yui-dt-data").find('tr').hide();
		jQuery(jqClientId + " table .yui-dt-data").find('td.' + column + ':contains("' + text + '")').parents('tr').show();
	},
	
	loadDynamicData : function(state, dt) {
		var params = "ajaxSource=" + dt.clientId,
		viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(dt.cfg.formId);
		if(state.pagination) {
			params = params + "&first=" + state.pagination.recordOffset;
		}
		if(state.sortedBy) {
			var sortDir = state.sortedBy.dir.split("-")[2];
			params = params + "&sortKey=" + state.sortedBy.key; 
			params = params + "&sortDir=" + sortDir;
		}
		
		return params;
	},
	
	loadInitialData : function(clientId, formId) {
		var params = "ajaxSource=" + clientId,
		viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(formId);
		
		return params;
	}
};