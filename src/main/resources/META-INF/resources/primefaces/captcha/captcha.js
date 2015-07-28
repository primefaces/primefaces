PrimeFaces.widget.Captcha = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        var $this = this;

        $.getScript( "http://www.google.com/recaptcha/api/js/recaptcha_ajax.js", function() {
            grecaptcha.render($this.id, $this.cfg.render);
        });
    }
}); 