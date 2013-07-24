/**
 * PrimeFaces BlockUI Widget
 */
PrimeFaces.widget.BlockUI = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.block = PrimeFaces.Expressions.resolveComponentsAsSelector(this.cfg.block);
        this.content = $(this.jqId);

        this.render();

        if(this.cfg.triggers) {
            this.bindTriggers();
        }
        
        if(this.cfg.blocked) {
            this.show();
        }

        //remove script tag
        $(this.jqId + '_s').remove();
    },
    
    bindTriggers: function() {
        var $this = this,
        triggers = PrimeFaces.Expressions.resolveComponents(this.cfg.triggers);
        
        //listen global ajax send and complete callbacks
        $(document).on('pfAjaxSend', function(e, xhr, settings) {
            if($.inArray(settings.source, triggers) !== -1) {
            	$this.show();
            }
        });

        $(document).on('pfAjaxComplete', function(e, xhr, settings) {
            if($.inArray(settings.source, triggers) !== -1) {
            	$this.hide();
            }
        });
    },
    
    show: function() {
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
    },
    
    hide: function() {
        this.blocker.fadeOut();

        if(this.hasContent()) {
            this.content.fadeOut();
        }
    },
    
    render: function() {   
        this.blocker = $('<div id="' + this.id + '_blocker" class="ui-blockui ui-widget-overlay ui-helper-hidden"></div>');

        if(this.block.hasClass('ui-corner-all')) {
            this.blocker.addClass('ui-corner-all');
        }

        this.block.css('position', 'relative').append(this.blocker).append(this.content);
    },
    
    hasContent: function() {
        return this.content.contents().length > 0;
    }
    
});