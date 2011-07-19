/**
 * PrimeFaces Sheet Widget
 */
PrimeFaces.widget.Sheet = function(id, cfg) {
    this.id = id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.header = this.jq.children('.ui-sheet-header');
    this.body = this.jq.children('.ui-sheet-body');
    this.cfg = cfg;
    var _self = this;

    //sync body scroll with header
    this.body.scroll(function() {
        _self.header.scrollLeft(_self.body.scrollLeft());
    });
    
    this.bindEvents();
    
    this.setupResizableColumns();
}

PrimeFaces.widget.Sheet.prototype.bindEvents = function() {
    var _self = this,
    cells = this.body.find('div.ui-sh-c');
        
    cells.click(function(e) {
        cells.filter('.ui-state-highlight').removeClass('ui-state-highlight');
        $(this).addClass('ui-state-highlight');
    })
    .dblclick(function(e) {
        var cell = $(this),
        oldWidth = cell.width(),
        padding = cell.innerWidth() - cell.width(),
        newWidth = oldWidth + padding;
                    
        _self.current = cell;
                    
        //change cell structure to allocate all the space
        cell.data('oldWidth', oldWidth)
        .removeClass('ui-state-highlight')
        .css('padding', '0px')
        .width(newWidth);
                        
        //switch to edit mode    
        cell.children('.ui-sh-c-d').hide();
        cell.children('.ui-sh-c-e').show().children('input:first').focus();
    });
                
    cells.find('input').blur(function() {
        //switch to display mode
        var editableContainer = _self.current.children('.ui-sh-c-e'),
        editableValue = editableContainer.children('input:first').val();
                    
        _self.current.children('.ui-sh-c-d').html(editableValue).show();
        editableContainer.hide();
                    
        //restore cell structure
        _self.current.css('padding', '').width(_self.current.data('oldWidth')).removeData('oldWidth');
        
    }).keydown(function(e) {
        //switch to display mode when enter is pressed during editing
        var keyCode = $.ui.keyCode,
        key = e.which,
        input = $(this);

        if(key == keyCode.ENTER || key == keyCode.NUMPAD_ENTER) {
            input.blur();
        }
    });
}

PrimeFaces.widget.Sheet.prototype.setupResizableColumns = function() {
    //Add resizers and resizer helper
    this.header.find('th div.ui-sh-c').prepend('<div class="ui-column-resizer"></div>');
    this.jq.append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

    //Variables
    var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
    resizers = $(this.jqId + ' thead th div.ui-column-resizer'),
    columnHeaders = $(this.jqId + ' thead th'),
    thead = $(this.jqId + ' thead'),  
    _self = this;
    
    //State cookie
    this.columnWidthsCookie = this.id + '_columnWidths';
    
    //Main resize events
    resizers.draggable({
        axis: 'x',
        start: function(event, ui) {
            //Set height of resizer helper
            resizerHelper.height(_self.body.height());
            resizerHelper.show();
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
                        
            _self.body.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);
            
            //Save state
            var columnWidths = [];
            columnHeaders.each(function(i, item) {
                columnWidths.push($(item).children('div').width());
            });
            PrimeFaces.setCookie(_self.columnWidthsCookie, columnWidths.join(','));
        },
        containment: this.jq
    });
    
    //Restore widths on postback
    var widths = PrimeFaces.getCookie(this.columnWidthsCookie);
    if(widths) {
        widths = widths.split(',');
        for(var i = 0; i < widths.length; i++) {
            var width = widths[i];
            
            columnHeaders.eq(i).children('div').width(width);
            _self.body.find('tr td:nth-child(' + (i + 1) + ')').children('div').width(width);
        }
    }

}