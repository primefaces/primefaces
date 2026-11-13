import { core } from "../core/core.js";
import { BaseComponent, type BaseComponentCfg } from "../core/core.component.js";

/**
 * The configuration for the {@link  Poll| Poll component}.
 * 
 * You can access this configuration via {@link Poll.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface PollCfg extends BaseComponentCfg {
    /**
     * In auto start mode, polling starts automatically on page load. To start polling on
     * demand set to false.
     */
    autoStart: boolean;
    /**
     * Callback that performs the polling action.
     */
    fn: PrimeFacesTypes.component.Poll.PollingAction;
    /**
     * Duration between two successive AJAX poll request, either in milliseconds or seconds,
     * depending on the configure `intervalType`.
     */
    frequency: number;
    /**
     * Time unit for the frequency.
     */
    intervalType: PrimeFacesTypes.component.Poll.IntervalType;
    /**
     * Optional callback invoked when polling starts.
     */
    onActivated: PrimeFacesTypes.component.Poll.OnActivatedCallback;
    /**
     * Optional callback invoked when polling stops.
     */
    onDeactivated: PrimeFacesTypes.component.Poll.OnDeactivatedCallback;
}

/**
 * __PrimeFaces Poll Component__
 * 
 * Poll is an ajax component that has the ability to send periodical ajax requests.
 */
export class Poll<Cfg extends PollCfg = PollCfg> extends BaseComponent<Cfg> {
    /**
     * Whether polling is currently active.
     */
    active: boolean = false;

    /**
     * The set-interval timer ID of the timer used for polling.
     */
    timer: number = 0;

    override init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.active = false;

        if (this.cfg.autoStart) {
            this.start();
        }
    }

    override refresh(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
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
    core.registerHook("killSwitch", {
        kill: () => {
            for (const component of core.getComponentsByType(Poll)) {
                core.warn("Stopping Poll");
                component.stop();
            }
        },
    });
}
