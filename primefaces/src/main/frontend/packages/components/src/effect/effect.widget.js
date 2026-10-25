/**
 * __PrimeFaces Effect Widget__
 * 
 * Effect component runs an effect of {@link PrimeFaces.effects} on an element.
 * 
 * @typedef {() => void} PrimeFaces.widget.Effect.EffectCallback Function that runs the effect when invoked. See also
 * {@link EffectCfg.fn}.
 * 
 * @typedef {() => void} PrimeFaces.widget.Effect.EffectRunner Internal function that runs the effect after a given
 * delay (via `setTimeout`) when invoked. See also {@link PrimeFaces.widget.Effect.runner}.
 * 
 * @prop {JQuery} source The DOM element with the source for the effect.
 * @prop {PrimeFaces.widget.Effect.EffectRunner} runner Internal function that runs the effect after a given delay (via
 * `setTimeout`) when invoked.
 * @prop {number | undefined} timeoutId ID of the current `setTimeout` for scheduling the effect.
 * 
 * @interface {PrimeFaces.widget.EffectCfg} cfg The configuration for the {@link  Effect| Effect widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.source ID of the source element for the effect.
 * @prop {number} cfg.delay Delay between effect repetitions in milliseconds.
 * @prop {string} cfg.event Event that triggers the effect. Defaults to `load` (page load).
 * @prop {PrimeFaces.widget.Effect.EffectCallback} cfg.fn Function that runs the effect when invoked.
 */
PrimeFaces.widget.Effect = class Effect extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.source = $(PrimeFaces.escapeClientId(this.cfg.source));
        // destroyed with the source
        this.bindLifecycleTo(this.source);
        var _self = this;

        this.runner = function() {
            //avoid queuing multiple runs
            if(_self.timeoutId) {
                clearTimeout(_self.timeoutId);
            }

            _self.timeoutId = PrimeFaces.queueTask(_self.cfg.fn, _self.cfg.delay);
        };

        var namespace = '.effect' + this.id;
        if(this.cfg.event == 'load') {
            this.runner.call();
        }
        else {
            var events = this.cfg.event.split(' ').map(function(e) { return e + namespace; }).join(' ');
            this.source.off(namespace).on(events, this.runner);
        }

        // the source may stay when the effect is removed
        this.addDestroyListener(function() {
            if(_self.timeoutId) {
                clearTimeout(_self.timeoutId);
            }
            _self.source.off(namespace);
        });
    }
    
}