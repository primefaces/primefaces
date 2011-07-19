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