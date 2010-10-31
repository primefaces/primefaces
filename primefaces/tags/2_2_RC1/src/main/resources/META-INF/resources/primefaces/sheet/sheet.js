PrimeFaces.widget.Spreadsheet = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	
	this.cfg.buildSheet = true;
	
	this.spreadsheet = jQuery(this.jqId + '_datasources').sheet(this.cfg);
	
	jQuery('.jSheetTabContainer span:last-child').hide();
}

PrimeFaces.widget.Spreadsheet.prototype.save = function() {
	var tables = this.spreadsheet.sheetInstance.exportSheet.html();
	
	for(var i = 0 ; i < tables.length; i++) {
		var dataCells = jQuery(tables[i]).find('tbody tr td');
		
		var transportCells = jQuery(this.jqId + '_datatransports').children().eq(i).find('tbody tr td');

		dataCells.each(function(j) {
			var cellValue = jQuery(this).html();
		
			jQuery(transportCells[j]).children('input').val(cellValue);
		});
	}	
}