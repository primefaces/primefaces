/**
 * PrimeFaces IdleMonitor Widget
 */
PrimeFaces.widget.IdleMonitor = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        var $this = this;

        $(document).on("idle.idleTimer" + this.cfg.id, function(){

            if($this.cfg.onidle) {
                $this.cfg.onidle.call($this);
            }

            $this.callBehavior('idle');
        })
        .on("active.idleTimer" + this.cfg.id, function(){
            if($this.cfg.onactive) {
                $this.cfg.onactive.call(this);
            }

            $this.callBehavior('active');
        });

        $.idleTimer(this.cfg.timeout, document, this.cfg.id);


        if (cfg.multiWindowSupport) {
            var globalLastActiveKey = $this.cfg.contextPath + '_idleMonitor_lastActive' + this.cfg.id;

            // always reset with current time on init
            localStorage.setItem(globalLastActiveKey, $(document).data('idleTimerObj' + this.cfg.id).lastActive);

            $this.timer = setInterval(function() {

                var idleTimerObj = $(document).data('idleTimerObj' + $this.cfg.id);

                var globalLastActive = parseInt(localStorage.getItem(globalLastActiveKey));
                var localLastActive = idleTimerObj.lastActive;

                // reset local state
                if (globalLastActive > localLastActive) {
                    // pause timer
                    $.idleTimer('pause', document, $this.cfg.id);

                    // overwrite real state
                    idleTimerObj.idle = false;
                    idleTimerObj.olddate = globalLastActive;
                    idleTimerObj.lastActive = globalLastActive;
                    idleTimerObj.remaining = $this.cfg.timeout;

                    // resume timer
                    $.idleTimer('resume', document, $this.cfg.id);
                }
                // update global state
                else if (localLastActive > globalLastActive) {
                    localStorage.setItem(globalLastActiveKey, localLastActive);
                }

            }, 2000);
        }
    },

    //@Override
    destroy: function() {
        this._super();

        if (this.cfg.multiWindowSupport) {
            clearInterval(this.timer);
        }
    },

    pause: function() {
        $.idleTimer('pause', document, this.cfg.id);
    },

    resume: function() {
        $.idleTimer('resume', document, this.cfg.id);
    },

    reset: function() {
        $.idleTimer('reset', document, this.cfg.id);
    }

});
