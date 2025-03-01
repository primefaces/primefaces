import { core } from "../core/core.js";
import { BaseWidget, type BaseWidgetCfg } from "../core/core.widget.js";

/**
 * The configuration for the {@link  Poll| Poll widget}.
 * 
 * You can access this configuration via {@link Poll.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface PollCfg extends BaseWidgetCfg {
    /**
     * In auto start mode, polling starts automatically on page load. To start polling on
     * demand set to false.
     */
    autoStart: boolean;
    /**
     * Callback that performs the polling action.
     */
    fn: PrimeType.widget.Poll.PollingAction;
    /**
     * Duration between two successive AJAX poll request, either in milliseconds or seconds,
     * depending on the configure `intervalType`.
     */
    frequency: number;
    /**
     * Time unit for the frequency.
     */
    intervalType: PrimeType.widget.Poll.IntervalType;
    /**
     * Optional callback invoked when polling starts.
     */
    onActivated: PrimeType.widget.Poll.OnActivatedCallback;
    /**
     * Optional callback invoked when polling stops.
     */
    onDeactivated: PrimeType.widget.Poll.OnDeactivatedCallback;
}

/**
 * __PrimeFaces Poll Widget__
 * 
 * Poll is an ajax component that has the ability to send periodical ajax requests.
 */
export class Poll<Cfg extends PollCfg = PollCfg> extends BaseWidget<Cfg> {
    /**
     * Whether polling is currently active.
     */
    active: boolean = false;

    /**
     * The set-interval timer ID of the timer used for polling.
     */
    timer: number = 0;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.active = false;

        if (this.cfg.autoStart) {
            this.start();
        }
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.stop();

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        this.stop();
    }

    /**
     * Starts the polling, sending AJAX requests in periodic intervals.
     * @return `true` if polling was started, or `false` otherwise.
     */
    start(): boolean {
        if (!this.active) {
            //Call user onactivated callback and block if they return false
            if (this.cfg.onActivated && this.cfg.onActivated.call(this) === false) {
                return false;
            }

            var frequency = this.cfg.intervalType == 'millisecond' ? this.cfg.frequency : ((this.cfg.frequency ?? 1) * 1000);
            this.timer = setInterval(this.cfg.fn ?? $.noop, frequency);
            this.active = true;
        }
        return true;
    }

    /**
     * Stops the polling so that no more AJAX requests are made.
     * @return `true` if polling wsa stopped, or `false` otherwise.
     */
    stop(): boolean {
        if (this.active) {
            //Call user ondeactivated callback and block if they return false
            if (this.cfg.onDeactivated && this.cfg.onDeactivated.call(this) === false) {
                return false;
            }

            clearInterval(this.timer);
            this.active = false;
        }
        return true;
    }

    /**
     * Checks whether polling is active or whether it was stopped.
     * @return `true` if polling is currently active, or `false` otherwise.
     */
    isActive(): boolean {
        return this.active;
    }
}

/**
 * Registers an implementation for the kill switch feature with the PrimeFaces
 * core. Stops all pollers when a kill signal is received.
 */
export function registerKillSwitchHookForPoll(): void {
    PrimeFaces.registerHook("killSwitch", {
        kill: () => {
            for (const widget of PrimeFaces.getWidgetsByType(Poll)) {
                core.warn("Stopping Poll");
                widget.stop();
            }
        },
    });
}
