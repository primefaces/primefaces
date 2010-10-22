/**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.tbody = this.jqId + '_data';

    //Paginator
    if(this.cfg.paginator) {
        this.setupPaginator();
    }

    //Sort events
    this.setupSortEvents();

    //Selection events
    if(this.cfg.selectionMode || this.cfg.columnSelectionMode) {
        this.selectionHolder = this.jqId + '_selection';

        var preselection = jQuery(this.selectionHolder).val();
        this.selection = preselection == "" ? [] : preselection.split(',');

        this.setupSelectionEvents();
    }

    if(this.cfg.expansion) {
        this.setupExpansionEvents();
    }

    if(this.cfg.scrollable) {
        this.setupScrolling();
    }
}

/**
 * Binds the change event listener and renders the paginator
 */
PrimeFaces.widget.DataTable.prototype.setupPaginator = function() {
    var paginator = this.getPaginator();

    paginator.subscribe('changeRequest', this.paginate, null, this);
    paginator.render();
}

/**
 * Applies events related to sorting in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSortEvents = function() {
    var _self = this;
    
    jQuery(this.jqId + ' th.ui-sortable-column').
        mouseover(function(){
            jQuery(this).toggleClass('ui-state-hover');
        })
        .mouseout(function(){
            jQuery(this).toggleClass('ui-state-hover');}
        )
        .click(function(event) {

            if(jQuery(event.target).is(':not(th,span)')) {
                return;
            }

            var columnId = jQuery(this).attr('id');
            //Reset previous sorted columns
            jQuery(this).siblings().removeClass('ui-state-active').
                children('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            //Update sort state
            jQuery(this).addClass('ui-state-active');
            var sortIcon = jQuery(this).children('.ui-sortable-column-icon');
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');

                _self.sort(columnId, false);
            }
            else if(sortIcon.hasClass('ui-icon-triangle-1-s')) {
                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, true);
            } else {
                sortIcon.addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, true);
            }
        });
}

/**
 * Applies events related to selection in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSelectionEvents = function() {
    var _self = this;

    //Row mouseover, mouseout, click
    if(this.cfg.selectionMode) {
        var selectEvent = this.cfg.dblclickSelect ? 'dblclick' : 'click';

        jQuery(this.jqId + ' .ui-datatable-data tr')
            .css('cursor', 'pointer')
            .die()
            .live('mouseover', function() {
                var row = jQuery(this);

                if(!row.hasClass('ui-selected')) {
                    row.addClass('ui-state-highlight');
                }

            })
            .live('mouseout', function() {
                var row = jQuery(this);

                if(!row.hasClass('ui-selected')) {
                    row.removeClass('ui-state-highlight');
                }

            })
            .live(selectEvent, function(event) {
                _self.onRowClick(event, this);
            });
            
    }
    //Radio-Checkbox based rowselection
    else if(this.cfg.columnSelectionMode) {
        
        if(this.cfg.columnSelectionMode == 'single') {
            jQuery(this.jqId + ' .ui-datatable-data td.ui-selection-column input:radio')
                .die()
                .live('click', function() {
                    _self.selectRowWithRadio(this);
                });
        }
        else {
            jQuery(this.jqId + ' .ui-datatable-data td.ui-selection-column input:checkbox')
                .die()
                .live('click', function() {
                    _self.selectRowWithCheckbox(this);
                });
        }
    }

    
}

/**
 * Applies events related to row expansion in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupExpansionEvents = function() {
    var _self = this;

    jQuery(this.jqId + ' tbody tr td span.ui-row-toggler')
            .die()
            .live('click', function() {
                _self.toggleExpansion(this);
            });
}

/**
 * Initialize data scrolling, for live scrolling listens scroll event to load data dynamically
 */
PrimeFaces.widget.DataTable.prototype.setupScrolling = function() {
    this.scrollOffset = this.cfg.scrollStep;
    this.shouldLiveScroll = true;
    jQuery(this.jqId + ' table').tableScroll({height:this.cfg.height});

    var _self = this;
    if(this.cfg.liveScroll) {
        
        jQuery(this.jqId + ' .ui-scrollable-datatable-container').scroll(function() {

            if(_self.shouldLiveScroll) {
                var viewport = jQuery(this);
                var scrollTop = viewport.attr('scrollTop'),
                scrollHeight = viewport.attr('scrollHeight'),
                viewportHeight = viewport.attr('clientHeight');

                if(scrollTop >= (scrollHeight - (viewportHeight))) {
                    _self.loadLiveRows();
                }
            }
            
        });
        
    }
}

/**
 * Loads rows on-the-fly when scrolling live
 */
PrimeFaces.widget.DataTable.prototype.loadLiveRows = function() {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.jqId + ' table.ui-scrollable-datatable-body tr:last').after(content);

                _self.scrollOffset += _self.cfg.scrollStep;

                //Disable scroll if there is no more data left
                if(_self.scrollOffset == _self.cfg.scrollLimit) {
                    _self.shouldLiveScroll = false;
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_scrolling"] = true;
    params[this.id + "_scrollOffset"] = this.scrollOffset;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Ajax pagination
 */
PrimeFaces.widget.DataTable.prototype.paginate = function(newState) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.tbody).replaceWith(content);

                _self.getPaginator().setState(newState);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_paging"] = true;
    params[this.id + "_first"] = newState.recordOffset;
    params[this.id + "_rows"] = newState.rowsPerPage;
    params[this.id + "_page"] = newState.page;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Ajax sort
 */
PrimeFaces.widget.DataTable.prototype.sort = function(columnId, asc) {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }
    
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.tbody).replaceWith(content);

                //reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(1, true);
                }

            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_sorting"] = true;
    params[this.id + "_sortKey"] = columnId;
    params[this.id + "_sortDir"] = asc;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Ajax filter
 */
PrimeFaces.widget.DataTable.prototype.filter = function() {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }
    
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        var paginator = _self.getPaginator();
        if(paginator) {
            var extensions = xmlDoc.getElementsByTagName("extension"),
            totalRecords = _self.getPaginator().getTotalRecords();

            for(var i=0; i < extensions.length; i++) {
                if(extensions[i].attributes.getNamedItem("primefacesCallbackParam").nodeValue == 'totalRecords') {
                    totalRecords = jQuery.parseJSON(extensions[i].firstChild.data).totalRecords;

                    //Reset paginator state
                    paginator.setPage(1);
                    paginator.setTotalRecords(totalRecords, true);
                }
            }
        }

        for(i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.tbody).replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_filtering"] = true;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);

}

/**
 * Row select handler
 *
 * - Unselects a row if it's already selected
 * - For single selection, clears previous selection
 */
PrimeFaces.widget.DataTable.prototype.onRowClick = function(event, rowElement) {
    
    //Check if rowclick triggered this event not an element in row content
    if(jQuery(event.target).is('td,span')) {
        
        var row = jQuery(rowElement);

        if(row.hasClass('ui-selected'))
            this.unselectRow(row);
        else
           this.selectRow(row);
       
    }
    
}

PrimeFaces.widget.DataTable.prototype.selectRow = function(row) {
    var rowId = row.attr('id').split('_row_')[1];

    //unselect previous selection
    if(this.isSingleSelection()) {
        row.siblings('.ui-selected').removeClass('ui-selected ui-state-highlight'); 
        this.selection = [];
    }

    //add to selection
    row.addClass('ui-state-highlight ui-selected');
    this.selection.push(rowId);

    //save state
    this.writeSelections();

    if(this.cfg.instantSelect) {
        this.fireRowSelectEvent(rowId);
    }
}

PrimeFaces.widget.DataTable.prototype.unselectRow = function(row) {
    var rowId = row.attr('id').split('_row_')[1];

    //remove visual style
    row.removeClass('ui-selected ui-state-highlight');

    //remove from selection
    this.selection = jQuery.grep(this.selection, function(r) {
        return r != rowId;
    });

    //save state
    this.writeSelections();

    if(this.cfg.instantUnselect) {
        this.fireRowUnselectEvent(rowId);
    }
}

/**
 *  Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowSelectEvent = function(rowId) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onRowSelectUpdate) {
        options.update = this.cfg.onRowSelectUpdate;
    }
    
    if(this.cfg.onRowSelectStart) 
        options.onstart = this.cfg.onRowSelectStart;
    if(this.cfg.onRowSelectComplete)
        options.oncomplete = this.cfg.onRowSelectComplete;
    
    var params = {};
    params[this.id + '_instantSelectedRowIndex'] = rowId;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 *  Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowUnselectEvent = function(rowId) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onRowUnselectUpdate) {
        options.update = this.cfg.onRowUnselectUpdate;
    }

    var params = {};
    params[this.id + '_instantUnselectedRowIndex'] = rowId;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 *  Selects the corresping row of a radio based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithRadio = function(radio) {
    var row = jQuery(radio).parent().parent(),
    rowId = row.attr('id').split('_row_')[1];

    this.selection = [];
    this.selection.push(rowId);

    //save state
    this.writeSelections();
}

/**
 *  Selects the corresping row of a checkbox based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithCheckbox = function(element) {
    var checkbox = jQuery(element),
    row = checkbox.parent().parent(),
    rowId = row.attr('id').split('_row_')[1],
    checked = checkbox.attr('checked');

    if(checked) {
        //add to selection
        this.selection.push(rowId);

    } else {
        //remove from selection
        this.selection = jQuery.grep(this.selection, function(r) {
            return r != rowId;
        });
        
    }

    //save state
    this.writeSelections();
}

/**
 * Selects all rows with checkbox
 */
PrimeFaces.widget.DataTable.prototype.toggleCheckAll = function(element) {
    var checkbox = jQuery(element),
    checked = checkbox.attr('checked');

    if(checked) {
        jQuery(this.jqId + ' .ui-datatable-data td.ui-selection-column input:checkbox').attr('checked', true);

        if(this.getPaginator() != null) {
            for(var i=0; i < this.getPaginator().getTotalRecords(); i++) {
                this.selection.push(i);
            }

        } else {
            jQuery(this.jqId + ' .ui-datatable-data tr').each(function() {
                this.selection.push(jQuery(this).attr('id').split('_row_')[1]);
            });
        }

        //save state
        this.writeSelections();

    }
    else {
        jQuery(this.jqId + ' .ui-datatable-data td.ui-selection-column input:checkbox').attr('checked', false);

        this.clearSelection();
    }
}

/**
 * Expands a row to display detail content
 */
PrimeFaces.widget.DataTable.prototype.toggleExpansion = function(expanderElement) {
    var expander = jQuery(expanderElement),
    row = expander.parent().parent().parent(),
    expanded = row.hasClass('ui-expanded-row');

    if(expanded) {
        expander.removeClass('ui-icon-circle-triangle-s');
        row.removeClass('ui-expanded-row');
        row.next().fadeOut(function() {
           jQuery(this).remove();
        });
    }
    else {
        expander.addClass('ui-icon-circle-triangle-s');
        row.addClass('ui-expanded-row');

        this.loadExpandedRowContent(row);
    }
    
}

PrimeFaces.widget.DataTable.prototype.loadExpandedRowContent = function(row) {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    rowId = row.attr('id').split('_row_')[1],
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                row.after(content);
                row.next().fadeIn();
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + '_rowExpansion'] = true;
    params[this.id + '_expandedRowId'] = rowId;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Displays in-cell editors for given row
 */
PrimeFaces.widget.DataTable.prototype.showEditors = function(element) {
    jQuery(element).parents('tr').addClass('ui-state-highlight').children('td.ui-editable-cell').each(function() {
       var column = jQuery(this);

       column.children().hide();
       column.children('span.ui-cell-editor').show();

       jQuery(element).hide();
       jQuery(element).siblings().show();
    });
}

/**
 * Saves the edited row
 */
PrimeFaces.widget.DataTable.prototype.saveRowEdit = function(element) {
    this.doRowEditRequest(element, 'save');
}

/**
 * Cancels row editing
 */
PrimeFaces.widget.DataTable.prototype.cancelRowEdit = function(element) {
    this.doRowEditRequest(element, 'cancel');
}

/**
 * Sends an ajax request to handle row save or edit
 */
PrimeFaces.widget.DataTable.prototype.doRowEditRequest = function(element, action) {
    var row = jQuery(element).parents('tr').get(0),
    options = {
        source: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    _self = this;

    if(action === 'save') {
        options.process = this.id;
    }

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(row).replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + '_rowEdit'] = true;
    params[this.id + '_editedRowId'] = row.id.split('_row_')[1];

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}


/**
 * Returns the paginator instance if any defined
 */
PrimeFaces.widget.DataTable.prototype.getPaginator = function() {
    return this.cfg.paginator;
}

/**
 * Writes selected row ids to state holder
 */
PrimeFaces.widget.DataTable.prototype.writeSelections = function() {
    jQuery(this.selectionHolder).val(this.selection.join(','));
}

/**
 * Returns type of selection
 */
PrimeFaces.widget.DataTable.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

/**
 * Clears the selection state
 */
PrimeFaces.widget.DataTable.prototype.clearSelection = function() {
    this.selection = [];
    
    jQuery(this.selectionHolder).val('');
}

/**
 * Returns true|false if selection is enabled|disabled
 */
PrimeFaces.widget.DataTable.prototype.isSelectionEnabled = function() {
    return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
}

/*

Copyright (c) 2009 Dimas Begunoff, http://www.farinspace.com

Licensed under the MIT license
http://en.wikipedia.org/wiki/MIT_License

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

;(function($){

	var scrollbarWidth = 0;

	// http://jdsharp.us/jQuery/minute/calculate-scrollbar-width.php
	function getScrollbarWidth()
	{
		if (scrollbarWidth) return scrollbarWidth;
		var div = $('<div style="width:50px;height:50px;overflow:hidden;position:absolute;top:-200px;left:-200px;"><div style="height:100px;"></div></div>');
		$('body').append(div);
		var w1 = $('div', div).innerWidth();
		div.css('overflow-y', 'auto');
		var w2 = $('div', div).innerWidth();
		$(div).remove();
		scrollbarWidth = (w1 - w2);
		return scrollbarWidth;
	}

	$.fn.tableScroll = function(options)
	{
		if (options == 'undo')
		{
			var container = $(this).parent().parent();
			container.find('.ui-scrollable-datatable-header thead').prependTo(this);
			container.find('.ui-scrollable-datatable-footer tfoot').appendTo(this);
			container.before(this);
			container.empty();
			return;
		}

		var settings = $.extend({},$.fn.tableScroll.defaults,options);

		settings.scrollbarWidth = getScrollbarWidth();

		this.each(function()
		{
			var flush = settings.flush;

			var tb = $(this).addClass('ui-scrollable-datatable-body');

			var wrapper = $('<div class="ui-scrollable-datatable-container"></div>').insertBefore(tb).append(tb);

			// check for a predefined container
			if (!wrapper.parent('div').hasClass(settings.containerClass))
			{
				$('<div></div>').addClass(settings.containerClass).insertBefore(wrapper).append(wrapper);
			}

			var width = settings.width ? settings.width : tb.outerWidth();

			wrapper.css
			({
				'width': width+'px',
				'height': settings.height+'px',
				'overflow': 'auto'
			});

			tb.css('width',width+'px');

			// with border difference
			var wrapper_width = wrapper.outerWidth();
			var diff = wrapper_width-width;

			// assume table will scroll
			wrapper.css({width:((width-diff)+settings.scrollbarWidth)+'px'});
			tb.css('width',(width-diff)+'px');

			if (tb.outerHeight() <= settings.height)
			{
				wrapper.css({height:'auto',width:(width-diff)+'px'});
				flush = false;
			}

			// using wrap does not put wrapper in the DOM right
			// away making it unavailable for use during runtime
			// tb.wrap(wrapper);

			// possible speed enhancements
			var has_thead = $('thead',tb).length ? true : false ;
			var has_tfoot = $('tfoot',tb).length ? true : false ;
			var thead_tr_first = $('thead tr:first',tb);
			var tbody_tr_first = $('tbody tr:first',tb);
			var tfoot_tr_first = $('tfoot tr:first',tb);

			// remember width of last cell
			var w = 0;

			$('th, td',thead_tr_first).each(function(i)
			{
				w = $(this).width();

				$('th:eq('+i+'), td:eq('+i+')',thead_tr_first).css('width',w+'px');
				$('th:eq('+i+'), td:eq('+i+')',tbody_tr_first).css('width',w+'px');
				if (has_tfoot) $('th:eq('+i+'), td:eq('+i+')',tfoot_tr_first).css('width',w+'px');
			});

			if (has_thead)
			{
				var tbh = $('<table class="ui-scrollable-datatable-header" cellspacing="0"></table>').insertBefore(wrapper).prepend($('thead',tb));
			}

			if (has_tfoot)
			{
				var tbf = $('<table class="ui-scrollable-datatable-footer" cellspacing="0"></table>').insertAfter(wrapper).prepend($('tfoot',tb));
			}

			if (tbh != undefined)
			{
				tbh.css('width',width+'px');

				if (flush)
				{
					$('tr:first th:last, tr:first td:last',tbh).css('width',(w+settings.scrollbarWidth)+'px');
					tbh.css('width',wrapper.outerWidth() + 'px');
				}
			}

			if (tbf != undefined)
			{
				tbf.css('width',width+'px');

				if (flush)
				{
					$('tr:first th:last, tr:first td:last',tbf).css('width',(w+settings.scrollbarWidth)+'px');
					tbf.css('width',wrapper.outerWidth() + 'px');
				}
			}
		});

		return this;
	};

	// public
	$.fn.tableScroll.defaults =
	{
		flush: true, // makes the last thead and tbody column flush with the scrollbar
		width: null, // width of the table (head, body and foot), null defaults to the tables natural width
		height: 100, // height of the scrollable area
		containerClass: 'ui-scrollable-datatable' // the plugin wraps the table in a div with this css class
	};

})(jQuery);
