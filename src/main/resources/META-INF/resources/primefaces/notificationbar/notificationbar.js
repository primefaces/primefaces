/**
 * PrimeFaces NotificationBar Widget
 */
PrimeFaces.widget.NotificationBar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var _self = this;
	
        //relocate
        this.jq.css(this.cfg.position, '0').appendTo($('body'));

        //display initially
        if(this.cfg.autoDisplay) {
            $(this.jq).css('display','block')
        }

        //bind events
        this.jq.children('.ui-notificationbar-close').click(function() {
            _self.hide();
        });
    },
    
    /**
     * The up-to-three arguments will be routed to jQuery as it is.
     * @see: http://api.jquery.com/slidedown/
     * @see: http://api.jquery.com/fadein/
     * @see: http://api.jquery.com/show/
     */
    show: function(a1, a2, a3) {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideDown(a1, a2, a3);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeIn(a1, a2, a3);
        else if(this.cfg.effect === 'none')
            $(this.jq).show(a1, a2, a3);
    },
    
    hide: function() {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideUp(this.cfg.effect);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeOut(this.cfg.effect);
        else if(this.cfg.effect === 'none')
            $(this.jq).hide();
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    },

    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    }
    
});
