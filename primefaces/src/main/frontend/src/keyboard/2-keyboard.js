/**
 * __PrimeFaces Keyboard Widget__
 * 
 * Keyboard is an input component that uses a virtual keyboard to provide the input. Notable features are the
 * customizable layouts and skinning capabilities.
 * 
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 *
 * @interface {PrimeFaces.widget.KeyboardCfg} cfg The configuration for the {@link  Keyboard| Keyboard widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryKeypad.KeypadSettings} cfg
 * 
 * @prop {JQueryKeypad.BeforeShowListener} cfg.beforeShow Callback that is invoked by the keyboard JQuery plugin before
 * the keyboard is brought up.
 * @prop {string[]} cfg.layout The resolved and parsed keyboard layout to be used. Contains on item for each row, each
 * keyboard row contains the character codes of the keys shown on that row.
 * @prop {string} cfg.layoutName The name of the built-in keyboard layout to use. Mutually exclusive with
 * `layoutTemplate`.
 * @prop {string} cfg.layoutTemplate An optional custom keyboard layout template specified by the user. The keyboard rows
 * must be separated by a comma. Each row contains the keys that should be displayed on that row. To specify a control
 * button (space, back, shift etc.), separate the name of the control key with a dash.   
 * @prop {JQueryKeypad.CloseListener} cfg.onClose Callback that is invoked by the keyboard JQuery plugin before
 * the keyboard is closed.
 */
 PrimeFaces.widget.Keyboard = class Keyboard extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        var $this = this;   
        if (this.cfg.layoutTemplate)
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.createLayoutFromTemplate(this.cfg.layoutTemplate);
        else
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.getPresetLayout(this.cfg.layoutName);

        this.cfg.beforeShow = function(div, inst) {
            $(div).addClass('ui-input-overlay').css('z-index', PrimeFaces.nextZindex());
            $this.bindPanelEvents();
        };

        this.cfg.onClose = function() {
            $this.unbindPanelEvents();
        };

        this.jq.keypad(this.cfg);

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents() {
        var $this = this;

        //Hide overlay on resize/scroll
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', null, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.jq.keypad('hide');
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.jq.keypad('hide');
            }
        });
    }

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents() {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }
}
