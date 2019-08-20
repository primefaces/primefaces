/**
 * PrimeFaces BlockUI Widget
 */
PrimeFaces.widget.BlockUI = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.block = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.block);
        this.content = $(this.jqId);
        this.cfg.animate = (this.cfg.animate === false)? false : true;
        this.cfg.blocked = (this.cfg.blocked === true)? true : false;
        
        this.render();

        if(this.cfg.triggers) {
            this.bindTriggers();
        }
        
        if(this.cfg.blocked) {
            this.show();
        }

        this.removeScriptElement(this.id);
    },
            
    refresh: function(cfg) {
        this.blocker.remove();
        this.block.children('.ui-blockui-content').remove();
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);
        
        this._super(cfg);
    },
    
    bindTriggers: function() {
        var $this = this;
        
        //listen global ajax send and complete callbacks
        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            var sourceId = $.type(settings.source) === 'string' ? settings.source : settings.source.name;
            // we must evaluate it each time as the DOM might has been changed
            var triggers = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents($this.cfg.triggers);
            
            if($.inArray(sourceId, triggers) !== -1 && !$this.cfg.blocked) {
                $this.show();
            }
        });

        $(document).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
            var sourceId = $.type(settings.source) === 'string' ? settings.source : settings.source.name;
            // we must evaluate it each time as the DOM might has been changed
            var triggers = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents($this.cfg.triggers);
            
            if($.inArray(sourceId, triggers) !== -1 && !$this.cfg.blocked) {
                $this.hide();
            }
        });
    },
    
    /**
     * Show the component with optional duration animation.
     * 
     * @param [duration] Durations are given in milliseconds; higher values indicate slower animations, not faster ones. 
     *                 The strings 'fast' and 'slow' can be supplied to indicate durations of 200 and 600 milliseconds, 
     *                 respectively.
     */
    show: function(duration) {
        this.blocker.css('z-index', ++PrimeFaces.zindex);
        
        //center position of content
        for(var i = 0; i < this.block.length; i++) {
            var blocker = $(this.blocker[i]),
                content = $(this.content[i]);
           
            content.css({
                'left': (blocker.width() - content.outerWidth()) / 2,
                'top': (blocker.height() - content.outerHeight()) / 2,
                'z-index': ++PrimeFaces.zindex
            });
        }

        var animated = this.cfg.animate;
        if(animated)
            this.blocker.fadeIn(duration);    
        else
            this.blocker.show(duration);

        if(this.hasContent()) {
            if(animated)
                this.content.fadeIn(duration);
            else
                this.content.show(duration);
        }
        
        this.block.attr('aria-busy', true);
    },
    
    /**
     * Hide the component with optional duration animation.
     * 
     * @param [duration] Durations are given in milliseconds; higher values indicate slower animations, not faster ones. 
     *                 The strings 'fast' and 'slow' can be supplied to indicate durations of 200 and 600 milliseconds, 
     *                 respectively.
     */
    hide: function(duration) {
        var animated = this.cfg.animate;

        if(animated)
            this.blocker.fadeOut(duration);
        else
            this.blocker.hide(duration);

        if(this.hasContent()) {
            if(animated)
                this.content.fadeOut(duration);
            else
                this.content.hide(duration);
        }
        
        this.block.attr('aria-busy', false);
    },
    
    render: function() {   
        this.blocker = $('<div id="' + this.id + '_blocker" class="ui-blockui ui-widget-overlay ui-helper-hidden"></div>');

        if(this.cfg.styleClass) {
            this.blocker.addClass(this.cfg.styleClass);
        }

        if(this.block.hasClass('ui-corner-all')) {
            this.blocker.addClass('ui-corner-all');
        }
        
        if(this.block.length > 1) {
            this.content = this.content.clone();
        }
        
        this.block.css('position', 'relative').attr('aria-busy', this.cfg.blocked).append(this.blocker).append(this.content);
    
        if(this.block.length > 1) {
            this.blocker = $(PrimeFaces.escapeClientId(this.id + '_blocker'));
            this.content = this.block.children('.ui-blockui-content');
        }
    },
    
    hasContent: function() {
        return this.content.contents().length > 0;
    }
    
});