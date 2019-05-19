/**
 * PrimeFaces Poll Widget
 */
PrimeFaces.widget.Poll = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.active = false;

        if(this.cfg.autoStart) {
            this.start();
        }
    },

    //@Override
    refresh: function(cfg) {
        this.stop();

        this._super(cfg);
    },

    //@Override
    destroy: function() {
        this._super();

        this.stop();
    },

    start: function() {
        if (!this.active) {
            this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
            this.active = true;
        }
    },

    stop: function() {
        if (this.active) {
            clearInterval(this.timer);
            this.active = false;
        }
    },

    isActive: function() {
        return this.active;
    }
});