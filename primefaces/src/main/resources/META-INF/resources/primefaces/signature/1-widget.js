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
 * @prop {JQuery} base64Input The DOM element for the hidden input element storing the base 64 value.
 * @prop {HTMLCanvasElement} canvasEL The canvas element where the signature is drawn.
 * @prop {JQuery} input The DOM element for the hidden input storing the value of this widget.
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
    init: function(cfg) {
        this._super(cfg);
        this.input = this.jq.children(this.jqId + '_value');
        this.base64Input = this.jq.children(this.jqId + '_base64');
        this.cfg.syncField = this.input;

        var $this = this;
        this.cfg.change = function() {
            $this.handleChange();
        };

        this.render();

        //disable the signature
        if(this.cfg.readonly){
            this.jq.signature("disable");
        }
    },

    /**
     * Clears this signature widget, removing all drawn lines.
     */
    clear: function() {
        this.jq.signature('clear');
        this.input.val('');
        this.base64Input.val('');
    },

    /**
     * Draws the given line data to this signature widget viewport.
     * @param {string | JQuerySignature.SignatureJson} value The signatue data to draw.
     */
    draw: function(value) {
        this.jq.signature('draw', value);
        if(this.cfg.base64) {
            this.base64Input.val(this.canvasEL.toDataURL());
        }
    },

    /**
     * Renders the client-side parts of this widget.
     * @private
     */
    render: function() {
        this.jq.signature(this.cfg);

        this.canvasEL = this.jq.children('canvas').get(0);

        var value = this.input.val();
        if(value) {
            this.draw(value);
        }
    },

    /**
     * Callback for when the signature has changed.
     * @private
     */
    handleChange: function() {
        if(this.cfg.base64) {
            this.base64Input.val(this.canvasEL.toDataURL());
        }

        if(this.cfg.onchange) {
            this.cfg.onchange.call(this);
        }
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }

});
