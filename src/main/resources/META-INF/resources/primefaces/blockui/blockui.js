/**
 * PrimeFaces BlockUI Widget
 */
PrimeFaces.widget.BlockUI = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.block = $(PrimeFaces.escapeClientId(this.cfg.block));
    this.content = $(this.jqId);

    this.render();

    if(this.cfg.triggers) {
        this.bindTriggers();
    }
    
    //remove script tag
    $(this.jqId + '_s').remove();
}

PrimeFaces.widget.BlockUI.prototype.bindTriggers = function() {
    var _self = this,
    triggers = this.cfg.triggers.split(',');
    
    $.each(triggers, function() {
        $(PrimeFaces.escapeClientId(this)).unbind('click.blockui').bind('click.blockui', function() {
            _self.show();
        });
    });
    
    $(document).bind('ajaxComplete', function(e, xhr, settings) {
        if($.inArray(settings.source, triggers) != -1) {
            _self.hide();
        }
    });
}

PrimeFaces.widget.BlockUI.prototype.show = function() {
    var blockWidth = this.block.outerWidth(),
    blockHeight = this.block.outerHeight();
    
    //set dimensions of blocker to span the content
    this.blocker.width(blockWidth).height(blockHeight);
    
    //center position of content
    this.content.css({
        'left': (blockWidth - this.content.outerWidth()) / 2,
        'top': (blockHeight - this.content.outerHeight()) / 2
    });
    
    this.blocker.fadeIn();
    
    if(this.hasContent()) {
        this.content.fadeIn();
    }
}

PrimeFaces.widget.BlockUI.prototype.hide = function() {
    this.blocker.fadeOut();
    
    if(this.hasContent()) {
        this.content.fadeOut();
    }
}

PrimeFaces.widget.BlockUI.prototype.render = function() {   
    this.blocker = $('<div id="' + this.id + '_blocker" class="ui-blockui ui-widget-overlay ui-helper-hidden"></div>');
    
    if(this.block.hasClass('ui-corner-all')) {
        this.blocker.addClass('ui-corner-all');
    }
    
    this.block.css('position', 'relative').append(this.blocker).append(this.content);
}

PrimeFaces.widget.BlockUI.prototype.hasContent = function() {
    return this.content.contents().length > 0;
}