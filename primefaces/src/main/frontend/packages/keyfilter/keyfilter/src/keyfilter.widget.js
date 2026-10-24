/**
 * __PrimeFaces KeyFilter Widget__
 *
 * KeyFilter is used to filter keyboard input on specified input components.
 *
 * @typedef {(this: HTMLInputElement | HTMLTextAreaElement, character: string) => boolean} PrimeFaces.widget.KeyFilter.TestFunction
 * A test function that checks whether an entered character is allowed. The this context is the DOM element of the input
 * field.
 * @param {string} PrimeFaces.widget.KeyFilter.TestFunction.character A newly entered character to check.
 * @return {boolean} PrimeFaces.widget.KeyFilter.TestFunction `true` to allow the entered character, or `false` to block
 * it.
 *
 * @typedef {"pint" | "int" | "pnum" | "money" | "num" | "hex" | "email" | "alpha" | "alphanum"} PrimeFaces.widget.KeyFilter.Mask
 * The name of a predefined mask, see {@link KeyFilter.masks}.
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
 * @prop {PrimeFaces.widget.KeyFilter.Mask} cfg.mask Defines the predefined mask which should be used. The options
 * `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 * @prop {boolean} cfg.preventPaste Whether the component also should prevent paste.
 * @prop {RegExp} cfg.regEx Defines the regular expression which should be used for filtering the input. The options
 * `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 * @prop {string} cfg.target The target input expression, defaults to the parent of this component.
 * @prop {PrimeFaces.widget.KeyFilter.TestFunction} cfg.testFunction An optional function which should be used for
 * filtering. The options `testFunction`, `regEx`, `inputRegEx`, and `mask` are mutually exclusive.
 */
PrimeFaces.widget.KeyFilter = class KeyFilter extends PrimeFaces.widget.BaseWidget {

    /**
     * The predefined masks, each a regular expression an entered character must match. Can be extended with own masks,
     * e.g. `PrimeFaces.widget.KeyFilter.masks.upper = /[A-Z]/` (formerly `$.fn.keyfilter.defaults.masks`).
     * @type {Record<PrimeFaces.widget.KeyFilter.Mask | string, RegExp>}
     */
    static masks = {
        pint: /[\d]/,
        'int': /[\d\-]/,
        pnum: /[\d\.]/,
        money: /[\d\.\s,]/,
        num: /[\d\-\.]/,
        hex: /[0-9a-f]/i,
        email: /[a-z0-9_\.\-@]/i,
        alpha: /[a-z_]/i,
        alphanum: /[a-z0-9_]/i
    };

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

        if(this.cfg.inputRegEx) {
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
        } else {
            var test = this.createCharacterTest();
            if (test) {
                this.bindCharacterFilter(input, test, namespace);
            }
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

    /**
     * Creates the test for a single entered character from the `regEx`, `testFunction` or `mask` option.
     *
     * @return {PrimeFaces.widget.KeyFilter.TestFunction | null} The test, or `null` if none of these options is set.
     * @private
     */
    createCharacterTest() {
        var regEx = this.cfg.regEx || (this.cfg.mask ? KeyFilter.masks[this.cfg.mask] : null);
        if (regEx) {
            return function(character) {
                return regEx.test(character);
            };
        }
        return this.cfg.testFunction || null;
    }

    /**
     * Removes entered characters that do not pass the test. Typed text is checked in `beforeinput`, which also works with
     * the virtual keyboards of mobile devices (unlike the deprecated `keypress` event); of a text with several
     * characters (e.g. an autocompletion of the keyboard) only the allowed ones are inserted. Text composed with an input
     * method editor (IME) can't be changed there, so its disallowed characters are removed when the composition ends.
     * Shortcuts like Ctrl+C, pasted and deleted text are not filtered.
     *
     * @param {JQuery} input The input or textarea element.
     * @param {PrimeFaces.widget.KeyFilter.TestFunction} test The test for a single character.
     * @param {string} namespace The event namespace of this widget.
     * @private
     */
    bindCharacterFilter(input, test, namespace) {
        var composition = null;
        var filter = function(element, text) {
            var result = '';
            for (var character of text) {
                if (test.call(element, character)) {
                    result += character;
                }
            }
            return result;
        };

        input.on('beforeinput' + namespace, function(e) {
            var event = e.originalEvent;
            if (event.inputType !== 'insertText' || !event.data) {
                return;
            }
            var filtered = filter(this, event.data);
            if (filtered !== event.data) {
                e.preventDefault();
                if (filtered) {
                    PrimeFaces.utils.replaceTextSelection(this, filtered);
                    $(this).trigger('input');
                }
            }
        })
        .on('compositionstart' + namespace, function() {
            var selection = PrimeFaces.utils.getTextSelection(this);
            composition = selection ? { value: this.value, start: selection.start, end: selection.end } : null;
        })
        .on('compositionend' + namespace, function(e) {
            var element = this,
                text = e.originalEvent.data,
                before = composition;
            composition = null;
            if (!before || !text) {
                return;
            }
            var filtered = filter(element, text);
            if (filtered !== text) {
                // after the browser inserted the composed text, some do that after compositionend
                setTimeout(function() {
                    element.value = before.value.slice(0, before.start) + filtered + before.value.slice(before.end);
                    PrimeFaces.utils.setTextSelection(element, before.start + filtered.length);
                    $(element).trigger('input');
                });
            }
        });
    }
}
