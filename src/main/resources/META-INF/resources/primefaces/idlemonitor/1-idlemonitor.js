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

            $this.callBehavior('idle');
        })
        .on("active.idleTimer", function(){
            if($this.cfg.onactive) {
                $this.cfg.onactive.call(this);
            }

            $this.callBehavior('active');
        });

        $.idleTimer(this.cfg.timeout);


        if (cfg.multiWindowSupport) {
            var globalLastActiveKey = $this.cfg.contextPath + '_idleMonitor_lastActive';

            // always reset with current time on init
            localStorage.setItem(globalLastActiveKey, $(document).data('idleTimerObj').lastActive);

            $this.timer = setInterval(function() {

                var idleTimerObj = $(document).data('idleTimerObj');

                var globalLastActive = parseInt(localStorage.getItem(globalLastActiveKey));
                var localLastActive = idleTimerObj.lastActive;

                // reset local state
                if (globalLastActive > localLastActive) {
                    // pause timer
                    $.idleTimer('pause');

                    // overwrite real state
                    idleTimerObj.idle = false;
                    idleTimerObj.olddate = globalLastActive;
                    idleTimerObj.lastActive = globalLastActive;
                    idleTimerObj.remaining = $this.cfg.timeout;

                    // resume timer
                    $.idleTimer('resume');
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
        $.idleTimer('pause');
    },

    resume: function() {
        $.idleTimer('resume');
    },

    reset: function() {
        $.idleTimer('reset');
    }

});
