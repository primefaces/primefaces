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
    
    show: function() {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideDown(this.cfg.effect);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeIn(this.cfg.effect);
        else if(this.cfg.effect === 'none')
            $(this.jq).show();
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