/**
 * The configuration for the {@link  IdleMonitor} widget.
 * 
 * You can access this configuration via {@link IdleMonitor.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface IdleMonitorCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * When set to true, the lastAccessed state will be shared between all browser
     * windows for the same servlet context.
     */
    multiWindowSupport: boolean;

    /**
     * Client side callback to execute when the user
     * comes back.
     */
    onactive: PrimeType.widget.IdleMonitor.OnActiveCallback;

    /**
     * Client side callback to execute when the user goes
     * idle.
     */
    onidle: PrimeType.widget.IdleMonitor.OnIdleCallback;

    /**
     * Time to wait in milliseconds until deciding if the user is idle. Default is 5 minutes.
     */
    timeout: number;
}

/**
 * __PrimeFaces IdleMonitor Widget__
 * 
 * IdleMonitor watches user actions on a page and notify callbacks in case they go idle or active again.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class IdleMonitor<Cfg extends IdleMonitorCfg = IdleMonitorCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The set-interval ID used for the timer.
     */
    timer: number = 0;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        $(document).on("idle.idleTimer" + this.cfg.id, () => {

            if (this.cfg.onidle) {
                this.cfg.onidle.call(this);
            }

            this.callBehavior('idle', undefined, false);
        })
            .on("active.idleTimer" + this.cfg.id, () => {
                if (this.cfg.onactive) {
                    this.cfg.onactive.call(this);
                }

                this.callBehavior('active', undefined, false);
            });

        $.idleTimer(this.cfg.timeout ?? 300_000, document, this.getId());


        if (cfg.multiWindowSupport) {
            var globalLastActiveKey = PrimeFaces.createStorageKey(this.getId(), 'IdleMonitor_lastActive', true);

            // always reset with current time on init
            localStorage.setItem(globalLastActiveKey, $(document).data('idleTimerObj' + this.cfg.id).lastActive);

            this.timer = setInterval(() => {

                var idleTimerObj = $(document).data('idleTimerObj' + this.cfg.id);

                var globalLastActive = parseInt(localStorage.getItem(globalLastActiveKey) ?? "0");
                var localLastActive = idleTimerObj.lastActive;

                // reset local state
                if (globalLastActive > localLastActive) {
                    // pause timer
                    $.idleTimer('pause', document, this.getId());

                    // overwrite real state
                    idleTimerObj.idle = false;
                    idleTimerObj.olddate = globalLastActive;
                    idleTimerObj.lastActive = globalLastActive;
                    idleTimerObj.remaining = this.cfg.timeout;

                    // resume timer
                    $.idleTimer('resume', document, this.getId());
                }
                // update global state
                else if (localLastActive > globalLastActive) {
                    localStorage.setItem(globalLastActiveKey, localLastActive);
                }

            }, 2000);
        }
    }

    override destroy(): void {
        if (this.cfg.multiWindowSupport) {
            clearInterval(this.timer);
        }
        $(document).off("idle.idleTimer" + this.getId());
        $(document).off("active.idleTimer" + this.getId());
        $.idleTimer('destroy', document, this.getId());

        super.destroy();
    }

    /**
     * Pauses the monitor, saving the remaining time.
     */
    pause(): void {
        $.idleTimer('pause', document, this.getId());
    }

    /**
     * Resumes the monitor, with the remaining time as it was saved when the monitor was paused.
     */
    resume(): void {
        $.idleTimer('resume', document, this.getId());
    }

    /**
     * Resets the monitor and restarts the timer.
     */
    reset(): void {
        $.idleTimer('reset', document, this.getId());
    }
}

/**
 * Registers an implementation for the kill switch feature with the PrimeFaces
 * core. Stops all idle monitors when a kill signal is received.
 */
export function registerKillSwitchHookForIdleMonitor(): void {
    PrimeFaces.registerHook("killSwitch", {
        kill: () => {
            for (const widget of PrimeFaces.getWidgetsByType(IdleMonitor)) {
                PrimeFaces.warn("Stopping IdleMonitor");
                widget.pause();
            }
        },
    });
}
