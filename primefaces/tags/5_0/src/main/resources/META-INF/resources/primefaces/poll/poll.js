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
    
    refresh: function(cfg) {
        if(this.isActive()) {
            this.stop();
        }
        
        this.init(cfg);
    },

    start: function() {
        this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
        this.active = true;
    },

    stop: function() {
        clearInterval(this.timer);
        this.active = false;
    },

    handleComplete: function(xhr, status, args) {
        if(args.stop) {
            this.stop();
        }
    },

    isActive: function() {
        return this.active;
    }
});