/**
 * __PrimeFaces Signature Widget__
 *
 * Signature is used to draw a signature as an input. Various options such as background color, foreground color,
 * thickness are available for customization. Signature also supports touch enabled devices and legacy browsers without
 * canvas support.
 *
 * @typedef PrimeFaces.widget.Signature.OnChangeCallback Callback that is invoked when the signature changes. See also
 * {@link SignatureCfg.onchange}.
 * @this {PrimeFaces.widget.Signature} PrimeFaces.widget.Signature.OnChangeCallback
 *
 * @prop {JQuery} canvas The canvas element where the signature is drawn.
 * @prop {JQuery} inputBase64 The DOM element for the hidden input element storing the base 64 value.
 * @prop {JQuery} inputJson The DOM element for the hidden input storing the value of this widget.
 * @prop {JQuery} inputText The DOM element for the hidden input storing the printed text value of this widget.
 *
 * @interface {PrimeFaces.widget.SignatureCfg} cfg The configuration for the {@link  Signature| Signature widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQuerySignature.SignatureSettings} cfg
 *
 * @prop {boolean} cfg.base64 Whether the signature data should be saved as a base 64 string.
 * @prop {PrimeFaces.widget.Signature.OnChangeCallback} cfg.onchange Callback that is invoked when the signature
 * changes.
 * @prop {boolean} cfg.readonly Whether the signature widget is readonly.
 */
PrimeFaces.widget.Signature = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.inputJson = this.jq.children(this.jqId + '_value');
        this.inputText = this.jq.children(this.jqId + '_text');
        this.cfg.notAvailable = '<p>Your browser does NOT support signing</p>';
        this.cfg.syncField = this.inputJson;
        this.cfg.tabindex = this.cfg.tabindex || '0';
        this.cfg.fontSize = this.cfg.fontSize || 40;
        this.cfg.fontFamily = this.cfg.fontFamily || 'Brush Script MT, cursive';

        this.render();
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function () {
        this._super();
        this.clear();
        this.jq.signature('destroy');
    },

    /**
     * Renders the client-side parts of this widget.
     * @private
     */
    render: function () {
        var $this = this;
        this.setupBase64();

        this.cfg.change = function () {
            $this.handleChange();
        };

        // create the signature 
        this.jq.signature(this.cfg);

        //disable the signature
        if (this.cfg.readonly) {
            this.disable();
        }

        // bind accessibility events
        this.setupCanvas();
        this.bindEvents();

        // attempt to draw the signature from the JSON value
        const json = this.inputJson.val();
        if (json) {
            this.draw(json);
            if (!this.jq.signature('isEmpty')) {
                return;
            }
        }

        // if the JSON load fails, attempt to draw the signature from the text value
        const textValue = this.inputText.val();
        if (textValue) {
            this.createSignatureFromText(textValue);
        }
    },

    /**
     * Sets up the base64 configuration for the signature widget.
     * @private
     */
    setupBase64: function () {
        if (this.cfg.base64) {
            this.cfg.svgStyles = true;
            this.inputBase64 = this.jq.children(this.jqId + '_base64');
        }
    },

    /**
     * Sets up the base64 configuration for the signature widget.
     * @private
     */
    setupCanvas: function () {
        this.canvas = this.jq.children('canvas');

        // accessibility
        this.canvas.attr({
            'aria-label': PrimeFaces.getAriaLabel('signatureLabel', this.cfg.ariaLabel || 'Sign here'),
            'aria-labelledby': this.cfg.ariaLabelledBy,
            'id': this.id + '_canvas',
            'role': 'img',
            'tabindex': this.cfg.readonly ? "-1" : (this.cfg.tabindex || '0'),
        });
    },

    /**
     * Binds event handlers to the signature canvas.
     * @private
     */
    bindEvents: function () {
        var $this = this;
        if (this.cfg.readonly) {
            return;
        }

        // focus from label
        if (this.cfg.ariaLabelledBy) {
            $(PrimeFaces.escapeClientId(this.cfg.ariaLabelledBy)).on('click', () => $this.canvas.trigger('focus'));
        }

        // events
        this.canvas.off('mouseenter mouseleave mousedown focus blur keydown')
            .on('mouseenter', () => $this.jq.addClass('ui-state-hover'))
            .on('mouseleave', () => $this.jq.removeClass('ui-state-hover'))
            .on('mousedown', () => $this.canvas.trigger('focus'))
            .on('focus', () => $this.jq.addClass('ui-state-focus'))
            .on('blur', () => {
                $this.jq.removeClass('ui-state-hover ui-state-focus');
                $this.updateBase64();
            })
            .on('keydown', (event) => {
                let printedText = $this.inputText.val() || '';
                switch (event.code) {
                    case 'Backspace':
                    case 'Delete':
                        if (printedText.length > 1) {
                            printedText = printedText.slice(0, -1);
                        } else {
                            printedText = '';
                            $this.clear();
                        }
                        break;
                    case 'Escape':
                        $this.clear();
                        break;
                    default:
                        if (PrimeFaces.utils.isPrintableKey(event)) {
                            printedText += event.key;
                        } else {
                            return;
                        }
                }

                event.preventDefault();
                if (printedText) {
                    $this.inputText.val(printedText);
                    $this.createSignatureFromText(printedText);
                }
            });
    },

    /**
     * Clears this signature widget, removing all drawn lines.
     */
    clear: function () {
        this.jq.signature('clear');
        this.inputJson.val('');
        this.inputText.val('');
        this.updateBase64(true);
    },

    /**
     * Draws the given line data to this signature widget viewport.
     * @param {string | JQuerySignature.SignatureJson} value The signatue data to draw.
     */
    draw: function (value) {
        if (value) {
            this.jq.signature('draw', value);
        }
    },

    /**
     * Callback for when the signature has changed.
     * @private
     */
    handleChange: function () {
        this.updateBase64();

        if (this.cfg.onchange) {
            this.cfg.onchange.call(this);
        }
    },

    /**
     * Updates the base64 value of the signature widget.
     * @param {boolean} clear - Whether to clear the base64 value.
     * @private
     */
    updateBase64: function (clear = false) {
        if (this.cfg.base64) {
            this.inputBase64.val(clear ? '' : this.canvas[0].toDataURL());
        }
    },

    /**
     * Creates a signature from the given text using SVG.
     * @param {string} text - The text to convert into a signature.
     */
    createSignatureFromText: function (text) {
        const canvas = this.canvas[0];
        const width = canvas.width;
        const height = canvas.height;
        const ctx = canvas.getContext("2d");
        ctx.save()
        ctx.clearRect(0, 0, width, height);
        ctx.fillStyle = this.cfg.color || 'black';
        ctx.font = `${this.cfg.fontSize}px ${this.cfg.fontFamily || 'Brush Script MT, cursive'}`;
        ctx.fillText(text, 10, (height + this.cfg.fontSize) / 2);
        this.handleChange();
        ctx.restore();
        this.draw(canvas.toDataURL());
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function () {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputJson);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function () {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputJson);
    }

});
