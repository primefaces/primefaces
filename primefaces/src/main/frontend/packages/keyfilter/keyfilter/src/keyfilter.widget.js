/**
 * __PrimeFaces KeyFilter Widget__
 * 
 * KeyFilter is used to filter keyboard input on specified input components.
 * 
 * @prop {JQuery} target The DOM element for the target input element to which this key filter is applied.
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
PrimeFaces.widget.KeyFilter = class KeyFilter extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);

        if (this.target.is(':input')) {
            this.applyKeyFilter(this.target, cfg);
        } else {
            var nestedInput = $(':not(:submit):not(:button):not([readonly]):input:visible:enabled:first', this.target);
            this.applyKeyFilter(nestedInput, cfg);
        }
    }

    /**
     * Applies the key filter to the given input or textarea element.
     *
     * @param {JQuery} input A jQuery selector object.
     * @param {TCfg} cfg The widget configuration.
     * @private
     */
    applyKeyFilter(input, cfg) {
        // the input is not replaced when only the keyFilter itself is updated, so remove the handlers of the
        // previous init - only the own ones, as other keyFilters could be applied to the same input
        var namespace = '.keyFilter' + this.id;
        input.off(namespace);
        this.addDestroyListener(function() {
            input.off(namespace);
        });

        if (this.cfg.regEx) {
            input.keyfilter(this.cfg.regEx, namespace);
        } else if(this.cfg.inputRegEx) {
            var inputRegEx = this.cfg.inputRegEx;
            var previousInputValue = '';
            input.on('input' + namespace, function(e) {
                    var ok = inputRegEx.test(this.value);
                    if(ok) {
                            previousInputValue = this.value;
                    }
                    else {
                            this.value = previousInputValue;
                    }
            });
        } else if (this.cfg.testFunction) {
            input.keyfilter(this.cfg.testFunction, namespace);
        } else if (this.cfg.mask) {
            input.keyfilter($.fn.keyfilter.defaults.masks[this.cfg.mask], namespace);
        }

        //disable drop
        input.on('drop' + namespace, function(e) {
            e.preventDefault();
        });

        if (cfg.preventPaste) {
            //disable paste
            input.on('paste' + namespace, function(e) {
                e.preventDefault();
            });
        }
    }
}
