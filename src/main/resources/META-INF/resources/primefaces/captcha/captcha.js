/**
 * PrimeFaces Captcha Widget
 */
PrimeFaces.widget.Captcha = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.cfg.language = this.cfg.language||'en';
        var $this = this;

        window[this.getInitCallbackName()] = function() {
            $this.render();
        };

        $(document.body).append('<script src="https://www.google.com/recaptcha/api.js?onload=' + this.getInitCallbackName() + '&render=explicit&hl='
                            + this.cfg.language +'" async defer>');

    },

    getInitCallbackName : function() {
        return this.cfg.widgetVar + '_initCallback';
    },

    //@Override
    render: function() {
        $this = this;
        grecaptcha.render(this.jq.get(0), {
            'sitekey' : this.cfg.sitekey,
            'tabindex': this.cfg.tabindex,
            'theme': this.cfg.theme,
            'callback': $this.cfg.callback,
            'expired-callback': $this.cfg.expired,
            'size': this.cfg.size
        });

        if (this.cfg.size === 'invisible') {
            grecaptcha.execute();
        }

        window[this.getInitCallbackName()] = null;
    },

    //@Override
    destroy: function() {
        this._super();

        window[this.getInitCallbackName()] = null;
    }
});