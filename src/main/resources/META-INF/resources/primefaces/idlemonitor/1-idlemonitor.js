/**
 * PrimeFaces IdleMonitor Widget
 */
PrimeFaces.widget.IdleMonitor = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        var $this = this;

        $(document).on("idle.idleTimer", function(){

            if($this.cfg.onidle) {
                $this.cfg.onidle.call($this);
            }

            if($this.cfg.behaviors) {
                var idleBehavior = $this.cfg.behaviors['idle'];
                if(idleBehavior) {
                    idleBehavior.call($this);
                }
            }
        })
        .on("active.idleTimer", function(){
            if($this.cfg.onactive) {
                $this.cfg.onactive.call(this);
            }

            if($this.cfg.behaviors) {
                var activeBehavior = $this.cfg.behaviors['active'];
                if(activeBehavior) {
                    activeBehavior.call($this);
                }
            }
        });

        $.idleTimer(this.cfg.timeout);
    },
    
    pause: function() {
        $.idleTimer('pause');
    },
    
    resume: function() {
        $.idleTimer('resume');
    },
    
    reset: function() {
        $.idleTimer('reset');
    }
    
});
