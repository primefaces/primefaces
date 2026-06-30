/**
 * __PrimeFaces InputText Widget__
 *
 * InputText is an extension to standard inputText with skinning capabilities.
 *
 * @prop {JQuery} counter The DOM element for the counter that informs the user about the number of characters they can
 * still enter before they reach the limit.
 *
 * @interface {PrimeFaces.widget.InputTextCfg} cfg The configuration for the {@link  InputText| InputText widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {string} cfg.counter ID of the label component to display remaining and entered characters.
 * @prop {string} cfg.counterTemplate Template text to display in counter, default value is `{0}`.
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinInput(this.jq);

        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();

            if(this.counter) {
                var $this = this;
                this.jq.on('input.inputtext-counter', function(e) {
                    $this.updateCounter();
                });
            }
        }
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq);
    },

    /**
     * Updates the counter value that keeps count of how many more characters the user can enter before they reach the
     * limit.
     * @private
     */
    updateCounter: function() {
        var value = this.normalizeNewlines(this.jq.val()),
        length = this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;

        if(this.counter && this.cfg.maxlength) {
            var remaining = this.cfg.maxlength - length;
            if(remaining < 0) {
                remaining = 0;
            }

            var counterText = this.cfg.counterTemplate
                    .replace('{0}', remaining)
                    .replace('{1}', length)
                    .replace('{2}', this.cfg.maxlength);

            this.counter.text(counterText);
        }
    },

    /**
     * Replaces all line breaks with a Window-style line break (carriage return + line feed).
     * @private
     * @param {string} text Text to normalize.
     * @return {string} The given text, with all line breaks replaced with carriage return + line feed.
     */
    normalizeNewlines: function(text) {
        return text.replace(/(\r\n|\r|\n)/g, '\r\n');
    }
});
