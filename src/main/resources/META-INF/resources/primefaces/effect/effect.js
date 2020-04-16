/**
 * __PrimeFaces Effect Widget__
 * 
 * Effect component is based on the jQuery UI effects library.
 * 
 * @typedef {() => void} PrimeFaces.widget.Effect.EffectCallback Function that run the effect when invoked. See also
 * {@link EffectCfg.fn}.
 * 
 * @prop {JQuery} source The DOM element with the source for the effect.
 * @prop {number} timeoutId ID of the current `setTimeout` for scheduling the effect.
 * 
 * @interface {PrimeFaces.widget.EffectCfg} cfg The configuration for the {@link  Effect| Effect widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryUI.EffectOptions} cfg
 * 
 * @prop {string} cfg.source ID of the source element for the effect.
 * @prop {number} cfg.delay Delay between effect repetitions in milliseconds.
 * @prop {string} cfg.event Event that triggers the effect. Defaults to `load` (page load).
 * @prop {PrimeFaces.widget.Effect.EffectCallback} cfg.fn Function that runs the effect when invoked.
 */
PrimeFaces.widget.Effect = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.source = $(PrimeFaces.escapeClientId(this.cfg.source));
        var _self = this;

        this.runner = function() {
            //avoid queuing multiple runs
            if(_self.timeoutId) {
                clearTimeout(_self.timeoutId);
            }

            _self.timeoutId = setTimeout(_self.cfg.fn, _self.cfg.delay);
        };

        if(this.cfg.event == 'load') {
            this.runner.call();
        } 
        else {
            this.source.on(this.cfg.event, this.runner);
        }
    }
    
});