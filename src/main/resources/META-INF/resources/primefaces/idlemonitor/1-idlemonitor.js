/**
 * __PrimeFaces IdleMonitor Widget__
 * 
 * IdleMonitor watches user actions on a page and notify callbacks in case they go idle or active again.
 * 
 * @typedef PrimeFaces.widget.IdleMonitor.OnActiveCallback Client side callback to execute when the user comes back. See
 * also {@link IdleMonitorCfg.onactive}.
 * @this {PrimeFaces.widget.IdleMonitor} PrimeFaces.widget.IdleMonitor.OnActiveCallback 
 * 
 * @typedef PrimeFaces.widget.IdleMonitor.OnIdleCallback Client side callback to execute when the user goes idle. See
 * also {@link IdleMonitorCfg.onidle}.
 * @this {PrimeFaces.widget.IdleMonitor} PrimeFaces.widget.IdleMonitor.OnIdleCallback 
 * 
 * @prop {number} timer The set-interval ID used for the timer.
 * 
 * @interface {PrimeFaces.widget.IdleMonitorCfg} cfg The configuration for the {@link  IdleMonitor| IdleMonitor widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.multiWindowSupport When set to true, the lastAccessed state will be shared between all browser
 * windows for the same servlet context.
 * @prop {PrimeFaces.widget.IdleMonitor.OnActiveCallback} cfg.onactive Client side callback to execute when the user
 * comes back.
 * @prop {PrimeFaces.widget.IdleMonitor.OnIdleCallback} cfg.onidle Client side callback to execute when the user goes
 * idle.
 * @prop {number} cfg.timeout Time to wait in milliseconds until deciding if the user is idle. Default is 5 minutes.
 */
PrimeFaces.widget.IdleMonitor = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
                $this.cfg.onactive.call($this);
            }

            $this.callBehavior('active');
        });

        $.idleTimer(this.cfg.timeout, document, this.cfg.id);


        if (cfg.multiWindowSupport) {
            var globalLastActiveKey = PrimeFaces.createStorageKey(this.cfg.id, 'IdleMonitor_lastActive');

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

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        if (this.cfg.multiWindowSupport) {
            clearInterval(this.timer);
        }
    },

    /**
     * Pauses the monitor, saving the remaining time.
     */
    pause: function() {
        $.idleTimer('pause', document, this.cfg.id);
    },

    /**
     * Resumes the monitor, with the remaining time as it was saved when the monitor was paused.
     */
    resume: function() {
        $.idleTimer('resume', document, this.cfg.id);
    },

    /**
     * Resets the monitor and restarts the timer.
     */
    reset: function() {
        $.idleTimer('reset', document, this.cfg.id);
    }

});
