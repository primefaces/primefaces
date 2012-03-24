/**
 * PrimeFaces Sheet Widget
 */
PrimeFaces.widget.Sheet = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.header = this.jq.children('.ui-sheet-header');
        this.body = this.jq.children('.ui-sheet-body');
        this.editor = $(this.jqId + ' .ui-sheet-editor-bar').find('.ui-sheet-editor');
        this.columnHeaders = this.header.find('thead th');
        this.cellInfoDisplay = $(this.jqId + ' .ui-sheet-editor-bar .ui-sheet-cell-info');
        var _self = this;

        //sync body scroll with header
        this.body.scroll(function() {
            _self.header.scrollLeft(_self.body.scrollLeft());
        });

        this.bindDynamicEvents();

        this.bindStaticEvents();

        this.setupSorting();

        this.setupResizableColumns();
    },
    
    setupResizableColumns: function() {
        //Add resizers and resizer helper
        this.header.find('th div.ui-sh-c').prepend('<div class="ui-column-resizer"></div>');
        this.jq.append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

        //Variables
        var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
        resizers = $(this.jqId + ' thead th div.ui-column-resizer'),
        frozenHeaderRow = this.header.find('tbody'),
        thead = $(this.jqId + ' thead'),
        _self = this;

        //Main resize events
        resizers.draggable({
            axis: 'x',
            start: function(event, ui) {
                //Set height of resizer helper
                resizerHelper.height(_self.body.height() + frozenHeaderRow.height());
                resizerHelper.show();

                _self.viewMode(_self.cells.filter('.ui-sheet-editing'));
            },
            drag: function(event, ui) {
                resizerHelper.offset(
                    {
                        left: ui.helper.offset().left + ui.helper.width() / 2, 
                        top: thead.offset().top + thead.height()
                    });  
            },
            stop: function(event, ui) {
                var columnHeaderWrapper = ui.helper.parent(),
                columnHeader = columnHeaderWrapper.parent(),
                oldPos = ui.originalPosition.left,
                newPos = ui.position.left,
                change = (newPos - oldPos),
                newWidth = columnHeaderWrapper.width() + change;

                ui.helper.css('left','');
                resizerHelper.hide();

                columnHeaderWrapper.width(newWidth);

                _self.header.find('tbody tr td:nth-child(' + (columnHeader.index() + 1) + ')').width(newWidth).children('div').width(newWidth);
                _self.body.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width(newWidth).children('div').width(newWidth);
                
                //Sync width change with server side state
                var options = {
                    source: _self.id,
                    process: _self.id,
                    params: [
                        {name: _self.id + '_colResize', value: true},
                        {name: _self.id + '_columnId', value: columnHeader.attr('id')},
                        {name: _self.id + '_width', value: newWidth},
                    ]
                }
                
                PrimeFaces.ajax.AjaxRequest(options);

            },
            containment: this.jq
        });
    },
    
    setupSorting: function() {
        var _self = this;

        this.header.find('.ui-sortable-column').mouseover(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover');
        })
        .click(function(e) {
            var column = $(this);

            //Reset previous sorted columns
            column.siblings().removeClass('ui-state-active').
                find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            //Update sort state
            column.addClass('ui-state-active');

            var columnId = column.attr('id'),
            sortIcon = column.find('.ui-sortable-column-icon');

            if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');

                _self.sort(columnId, "DESCENDING");
            }
            else if(sortIcon.hasClass('ui-icon-triangle-1-s')) {
                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            } else {
                sortIcon.addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            }
        });
    },
    
    sort: function(columnId, order) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.jq.parents('form:first').attr('id')
        };

        var _self = this;
        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    _self.body.children('table').children('tbody').html(content);
                    _self.bindDynamicEvents();
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.params = [
            {name: this.id + '_sorting', value: true},
            {name: this.id + '_sortKey', value: columnId},
            {name: this.id + '_sortDir', value: order}
        ];

        PrimeFaces.ajax.AjaxRequest(options); 
    },
        
    updateCellInfoDisplay: function(cell) {
        var rowIndex = cell.parent().siblings().eq(0).children('.ui-sheet-index-cell').html(),
        columnName = this.header.find('th').eq(cell.parent().index()).children('.ui-sh-c').text();

        this.cellInfoDisplay.html(rowIndex + columnName);
    },
    
    selectCell: function(cell) {
        cell.addClass('ui-state-highlight');

        this.editor.val(cell.find('input').val());
        this.updateCellInfoDisplay(cell);
        this.origin = cell;
        this.cursor = cell;
    },
    
    unselectCell: function(cell) {
        cell.removeClass('ui-state-highlight');
    },
    
    unselectCells: function(cells) {
        cells.removeClass('ui-state-highlight');
    },
    
    isSelected: function(cell) {
        return cell.hasClass('ui-state-highlight');
    },
    
    checkCellViewPort: function(cell) {
        var _self = this,
        bTable = _self.body.children('table:first'),
        yScrolled = _self.body.height() < bTable.height(),
        xScrolled = _self.body.width() < bTable.width();
        
        //check & align up/down overflow
        var diff = cell.offset().top + cell.outerHeight(true) - _self.body.offset().top;
        if( diff > _self.body.height() )
            _self.body.scrollTop(_self.body.scrollTop() + (diff - _self.body.height()) + (xScrolled ? 16 : 0));
        else if( (diff -= cell.outerHeight(true)*2 - cell.height()) < 0 )
            _self.body.scrollTop( _self.body.scrollTop() + diff);
        
        
        //check & align left/right overflow
        diff = cell.offset().left + cell.outerWidth(true) - _self.body.offset().left;
        if( diff > _self.body.width() )
            _self.body.scrollLeft(_self.body.scrollLeft() + (diff - _self.body.width()) + (yScrolled ? 16 : 0));
        else if( (diff -= cell.outerWidth(true)*2 - cell.width()) < 0 )
            _self.body.scrollLeft( _self.body.scrollLeft() + diff);
    },
    
    bindDynamicEvents: function() {
        var _self = this;
        this.cells = this.body.find('div.ui-sh-c:not(.ui-sheet-index-cell)'),
        this.rows = this.body.find('tr');

        //this.cells.disableSelection();

        //events for data cells
        this.cells.click(function(e) {
            var cell = $(this),
            metaKey = (e.metaKey||e.ctrlKey),
            shiftKey = e.shiftKey,
            selected = _self.isSelected(cell);

            if(metaKey) {
                if(selected)
                    _self.unselectCell(cell);
                else
                    _self.selectCell(cell);
            } 
            else if(shiftKey) {
                _self.selectCells(_self.origin, cell);
            } 
            else {
                _self.cells.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                _self.selectCell(cell);
            }

            //check cell in frame (with parent/td element)
            _self.checkCellViewPort(cell.parent());

        })
        .dblclick(function(e) {
            var cell = $(this);

            _self.editMode(cell);
        });

        //events for input controls in data cells
        this.cells.find('input').blur(function(e) {
            var cell = $(this).parents('.ui-sh-c:first');

            _self.viewMode(cell);
        }).keyup(function(e) {
            //switch to display mode when enter is pressed during editing
            var keyCode = $.ui.keyCode,
            key = e.which,
            input = $(this),
            cell = $(this).parents('.ui-sh-c:first');

            if(key == keyCode.ENTER || key == keyCode.NUMPAD_ENTER) {
                _self.viewMode(cell);
            } 
            else {
                _self.editor.val(input.val());
            }

            e.preventDefault();
        });
    },
    
    editMode: function(cell) {
        cell.addClass('ui-sheet-editing');

        //switch to edit mode    
        cell.children('.ui-sh-c-d').hide();
        cell.children('.ui-sh-c-e').show().children('input:first').focus();
    },
    
    viewMode: function(cell) {
        //switch to display mode if anything other than editor is clicked
        if(cell.length > 0) {    
            var input = cell.find('input'),
            editableContainer = cell.children('.ui-sh-c-e'),
            displayContainer = cell.children('.ui-sh-c-d'),
            editableValue = input.val();

            displayContainer.html(editableValue).show();
            editableContainer.hide();

            cell.removeClass('ui-sheet-editing');
        }
    },
    
    bindStaticEvents: function() {
        var _self = this;

        //events for global editor
        this.editor.keydown(function(e) {
            //update cell value on enter key
            var keyCode = $.ui.keyCode,
            key = e.which,
            editor = $(this),
            selectedCells = _self.body.find('div.ui-sh-c.ui-state-highlight');

            if(key == keyCode.ENTER || key == keyCode.NUMPAD_ENTER) {
                selectedCells.children('.ui-sh-c-d').html(editor.val());
                selectedCells.find('input:first').val(editor.val());
                editor.val('');
            }
        }).focus(function(e) {
            //highlight current cell
            _self.origin.addClass('ui-state-highlight');
        });

        //keyboard navigation for datacells
        $(document).bind('keydown.sheet', function(e) {
            var target = $(e.target);
            if(!target.is('html') && !target.is(document.body)) {
                return;
            }

            var selectedCells = _self.body.find('div.ui-sh-c.ui-state-highlight');

            if(selectedCells.length > 0) {
                var keyCode = $.ui.keyCode,
                key = e.which,
                origin = _self.origin,
                shift = e.shiftKey;

                _self.cursor = _self.cursor || origin;

                switch(e.which) {
                    case keyCode.RIGHT:
                        var cursor = _self.cursor.parent().next().children('div.ui-sh-c');

                        if(cursor.length > 0) {
                            _self.cursor = cursor;

                            if(shift)
                                _self.selectCells(origin, _self.cursor);
                            else
                                _self.cursor.click();
                        } 

                        e.preventDefault();
                    break;

                    case keyCode.LEFT:
                        var cursor = _self.cursor.parent().prev().children('div.ui-sh-c:not(.ui-sheet-index-cell)');

                        if(cursor.length > 0) {
                            _self.cursor = cursor;

                            if(shift)
                                _self.selectCells(origin, _self.cursor);
                            else
                                _self.cursor.click();
                        } 

                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                    case keyCode.DOWN:
                        var next = _self.cursor.parents('tr:first').next().children().eq(_self.cursor.parent().index()).children('div.ui-sh-c');
                        if(next && next.length){
                            _self.cursor = next;
                            if(shift)
                                _self.selectCells(origin, _self.cursor);
                            else
                                _self.cursor.click();
                        }

                        e.preventDefault();
                    break;

                    case keyCode.UP:
                        var prev = _self.cursor.parents('tr:first').prev().children().eq(_self.cursor.parent().index()).children('div.ui-sh-c');
                        if(prev && prev.length){
                            _self.cursor = prev;
                            if(shift)
                                _self.selectCells(origin, _self.cursor);
                            else
                                _self.cursor.click();
                        }  

                        e.preventDefault();
                    break;

                    case keyCode.BACKSPACE:
                        selectedCells.children('.ui-sh-c-d').html('&nbsp;');
                        selectedCells.find('input').val('');
                        selectedCells.removeClass('ui-state-highlight');
                        e.preventDefault();
                    break;
                }
            }

        });
    },
    
    selectCells: function(origin, cursor) {
        this.cells.filter('.ui-state-highlight').removeClass('ui-state-highlight');

        var startRowIndex = origin.parents('tr:first').index(),
        startColumnIndex = origin.parent().index(),
        endRowIndex = cursor.parents('tr:first').index(),
        endColumnIndex = cursor.parent().index(),
        containerRows = null;

        if(endRowIndex > startRowIndex)
            containerRows = this.rows.slice(startRowIndex, endRowIndex + 1);
        else
            containerRows = this.rows.slice(endRowIndex, startRowIndex + 1);

        containerRows.each(function(i, item) {
            var row = $(item);

            if(endColumnIndex > startColumnIndex)
                row.children().slice(startColumnIndex, endColumnIndex + 1).children('div.ui-sh-c').addClass('ui-state-highlight');
            else
                row.children().slice(endColumnIndex, startColumnIndex + 1).children('div.ui-sh-c').addClass('ui-state-highlight');
        });
    }
    
});
