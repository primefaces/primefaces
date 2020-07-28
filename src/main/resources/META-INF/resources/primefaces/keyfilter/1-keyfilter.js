/**
 * __PrimeFaces KeyFilter Widget__
 * 
 * KeyFilter is used to filter keyboard input on specified input components.
 * 
 * @prop {JQuery} target The DOM element for the target input element to which this key filter is applied.
 * @prop {string} value The current value of the input field.
 * 
 * @interface {PrimeFaces.widget.KeyFilterCfg} cfg The configuration for the {@link  KeyFilter| KeyFilter widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {RegExp} cfg.inputRegEx Defines the regular expression which should be used to test the complete input text
 * content. The options `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 * @prop {keyof JQueryKeyfilter.DefaultMasks} cfg.mask Defines the predefined mask which should be used. The options
 * `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 * @prop {boolean} cfg.preventPaste Whether the component also should prevent paste.
 * @prop {RegExp} cfg.regEx Defines the regular expression which should be used for filtering the input. The options
 * `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 * @prop {string} cfg.target The target input expression, defaults to the parent of this component.
 * @prop {JQueryKeyfilter.TestFunction} cfg.testFunction An optional function which should be used for filtering. The
 * options `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 */
PrimeFaces.widget.KeyFilter = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init : function(cfg) {
        this._super(cfg);

        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);

        if (this.target.is(':input')) {
            this.applyKeyFilter(this.target, cfg);
        } else {
            var nestedInput = $(':not(:submit):not(:button):input:visible:enabled:first', this.target);
            this.applyKeyFilter(nestedInput, cfg);
        }
    },

    /**
     * Applies the key filter to the given input or textarea element.
     *
     * @param {JQuery} input A jQuery selector object.
     * @param {TCfg} cfg The widget configuration.
     * @private
     */
    applyKeyFilter : function(input, cfg) {
        if (this.cfg.regEx) {
            input.keyfilter(this.cfg.regEx);
        } else if(this.cfg.inputRegEx) {
            var inputRegEx = this.cfg.inputRegEx;
            var previousInputValue = '';
            input.on('input', function(e) {
                    var ok = inputRegEx.test(this.value);
                    if(ok) {
                            previousInputValue = this.value;
                    }
                    else {
                            this.value = previousInputValue;
                    }
            });
        } else if (this.cfg.testFunction) {
            input.keyfilter(this.cfg.testFunction);
        } else if (this.cfg.mask) {
            input.keyfilter($.fn.keyfilter.defaults.masks[this.cfg.mask]);
        }
        
        //disable drop
        input.on('drop', function(e) {
            e.preventDefault();
        });

        if (cfg.preventPaste) {
            //disable paste
            input.on('paste', function(e) {
                e.preventDefault();
            });
        }
    }
});
