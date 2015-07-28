/**
 * PrimeFaces Captcha Widget
 */
PrimeFaces.widget.Captcha = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.cfg.language = this.cfg.language||'en';
        var $this = this;
        
        window[this.cfg.widgetVar + '_initCallback'] = function() {
            $this.render();
        };
        
        $(document.body).append('<script src="https://www.google.com/recaptcha/api.js?onload=' + this.cfg.widgetVar + '_initCallback&render=explicit&hl=' 
                            + this.cfg.language +'" async defer>');
    },
    
    render: function() {
        grecaptcha.render(this.jq.get(0), {
            'sitekey' : this.cfg.sitekey,
            'tabindex': this.cfg.tabindex,
            'theme': this.cfg.theme
        });
        
        window[this.cfg.widgetVar + '_initCallback'] = undefined;
    }
    
}); 