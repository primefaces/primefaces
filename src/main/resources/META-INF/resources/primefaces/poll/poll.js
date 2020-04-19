/**
 * __PrimeFaces Poll Widget__
 * 
 * Poll is an ajax component that has the ability to send periodical ajax requests.
 * 
 * @typedef {"millisecond" | "second"} PrimeFaces.widget.Poll.IntervalType Time unit for the polling interval.
 * 
 * @typedef {() => void} PrimeFaces.widget.Poll.PollingAction Callback that performs the polling action. See also
 * {@link PollCfg.fn}.
 * 
 * @prop {boolean} active Whether polling is currently active.
 * @prop {number} timer The set-interval timer ID of the timer used for polling.
 * 
 * @interface {PrimeFaces.widget.PollCfg} cfg The configuration for the {@link  Poll| Poll widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.autoStart In auto start mode, polling starts automatically on page load. To start polling on
 * demand set to false.
 * @prop {PrimeFaces.widget.Poll.IntervalType} cfg.intervalType Time unit for the frequency.
 * @prop {number} cfg.frequency Duration between two successive AJAX poll request, either in milliseconds or seconds,
 * depending on the configure `intervalType`.
 * @prop {PrimeFaces.widget.Poll.PollingAction} cfg.fn Callback that performs the polling action.
 */
PrimeFaces.widget.Poll = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.active = false;

        if(this.cfg.autoStart) {
            this.start();
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.stop();

        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        this.stop();
    },

    /**
     * Starts the polling, sending AJAX requests in periodic intervals.
     */
    start: function() {
        if (!this.active) {
            var frequency = this.cfg.intervalType == 'millisecond' ? this.cfg.frequency : (this.cfg.frequency * 1000);
            this.timer = setInterval(this.cfg.fn, frequency);
            this.active = true;
        }
    },

    /**
     * Stops the polling so that no more AJAX requests are made.
     */
    stop: function() {
        if (this.active) {
            clearInterval(this.timer);
            this.active = false;
        }
    },

    /**
     * Checks whether polling is active or whether it was stopped.
     * @return {boolean} `true` if polling is currently active, or `false` otherwise.
     */
    isActive: function() {
        return this.active;
    }
});