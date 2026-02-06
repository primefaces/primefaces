/**
 * __PrimeFaces Color Picker Widget__
 * 
 * ColorPicker is an input component with a color palette.
 * 
 * This uses Coloris written in vanilla ES6. To interact with the color picker, you can use the following code.
 * 
 * ```javascript
 * // Assuming the widget variable of the color picker was set to "myColorPicker"
 * const colorPicker = PF("myColorPicker");
 * 
 * // Brings up the color picker (if "mode" was set to "popup")
 * colorPicker.show();
 * 
 * // Hides up the color picker (if "mode" was set to "popup")
 * colorPicker.hide();
 * 
 * // Sets the currently selected color to "green"
 * colorPicker.setColor("00FF00");
 * ```
 * 
 * @typedef {"inline" | "popup"} PrimeFaces.widget.ColorPicker.DisplayMode Display mode of a color picker. `inline`
 * renders the color picker within the normal content flow, `popup` creates an overlay that is displayed when the user
 * clicks on the color.
 * 
 * @prop {JQuery} input DOM element of the INPUT element
 * @prop {boolean} popup True if popup mode, else inline mode
 * @prop {boolean} hasFloatLabel Is this component wrapped in a float label.
 * 
 * @interface {PrimeFaces.widget.ColorPickerCfg} cfg The configuration for the {@link  Coloris}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.ColorPicker.DisplayMode} cfg.mode Whether the color picker is displayed inline or as a popup.
 * @prop {string} cfg.instance The instance of for configuring in popup mode
 */
PrimeFaces.widget.ColorPicker = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.setupGlobalDefaults();
        this.setupPopup();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._cleanup();
        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();
        this._cleanup();
    },

    /**
    * Clean up this widget and remove events from the DOM.
    * @private
    */
    _cleanup: function() {
        if (this.input) {
            this.input.removeClass('ui-colorpicker');
            this.input.parent().find('button').remove();
            Coloris.removeInstance('.' + this.cfg.instance);
        }
        else {
            // remove the inline from the DOM
            this.jq.empty();
        }
    },

    /**
     * Only one instance of Coloris is allowed so ensure it only loads defaults once.
     * @private
     */
    setupGlobalDefaults: function() {
        this.popup = this.cfg.mode === 'popup';
        if (colorisInitialized) {
            return;
        }

        var $this = this;
        this.configureLocale();
        this.cfg.inline = !this.popup;
        this.cfg.themeMode = this.cfg.themeMode || PrimeFaces.env.getThemeContrast();
        var settings = this.cfg;
        if (this.popup) {
            colorisInitialized = true;
            settings = {
                el: '.ui-colorpicker',
                inline: this.cfg.inline,
                a11y: this.cfg.a11y,
                clearLabel: this.cfg.clearLabel,
                closeLabel: this.cfg.closeLabel
            };
        }
        else {
            colorisInitialized = false;
            settings.el = null;
            settings.parent = this.jqId;
            this.bindInlineCallbacks();
        }
        $(document).ready(function() {
            Coloris.init();
            Coloris(settings);
            if ($this.cfg.inline) {
                Coloris.updatePosition();
            }
            colorisInitialized = false;
        });
    },

    /**
     * Localizes the ARIA accessibility labels for the color picker.
     * @private
     */
    configureLocale: function() {
        var lang = PrimeFaces.getLocaleSettings(this.cfg.locale);
        if (!lang) {
            return;
        }
        if (lang.aria && lang.aria.close) { this.cfg.closeLabel = lang.aria.close; }
        if (lang.clear) { this.cfg.clearLabel = lang.clear; }
        if (lang.isRTL) { this.cfg.rtl = true; }
        if (lang.aria) {
            PrimeFaces.localeSettings = lang;
            var a11y = {};
            this.configureAriaLabel('colorpicker.OPEN', a11y, 'open');
            this.configureAriaLabel('colorpicker.CLOSE', a11y, 'close');
            this.configureAriaLabel('colorpicker.CLEAR', a11y, 'clear');
            this.configureAriaLabel('colorpicker.MARKER', a11y, 'marker');
            this.configureAriaLabel('colorpicker.HUESLIDER', a11y, 'hueSlider');
            this.configureAriaLabel('colorpicker.ALPHASLIDER', a11y, 'alphaSlider');
            this.configureAriaLabel('colorpicker.INPUT', a11y, 'input');
            this.configureAriaLabel('colorpicker.FORMAT', a11y, 'format');
            this.configureAriaLabel('colorpicker.SWATCH', a11y, 'swatch');
            this.configureAriaLabel('colorpicker.INSTRUCTION', a11y, 'instruction');
            this.cfg.a11y = a11y;
        }
    },
    
    /**
     * Configures a single ARIA label from PF locale to Coloris a11y.
     * @param {string} label the PF label to lookup in locale.js
     * @param {{key: string}} a11y the a11y JSON object for Coloris
     * @param {string} property the JSON property to set in a11y
     * @private
     */
    configureAriaLabel: function(label, a11y, property) {
        var ariaLabel = this.getAriaLabel(label);
        if (ariaLabel) {
            a11y[property] = ariaLabel;
        }
    },

    /**
     * Configure the color picker for popup mode.
     * @private
     */
    setupPopup: function() {
        if (!this.popup) {
            return;
        }
        var $this = this;

        // input and pfs metadata
        this.input = this.jq;
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        PrimeFaces.skinInput(this.input);
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.input);

        // check if being used in dialog and set the parent
        this.setupDialogSupport();

        // setup event callbacks
        this.bindInputCallbacks();

        // configure Coloris for this instance
        Coloris.setInstance('.' + this.cfg.instance, this.cfg);

        // add error styling to thumbnail button if necessary
        $(document).ready(function() {
            var triggerButton = $this.input.prev();
            if ($this.input.hasClass('ui-state-error')) {
                triggerButton.addClass('ui-inputfield ui-state-error');
            }
        });
    },

    /**
     * Sets up the event listeners required by this widget for inline mode.
     * @private
     */
    bindInlineCallbacks: function() {
        var $this = this;
        if ($this.hasBehavior('change')) {
            var pickNS = 'coloris:pick';
            $(document).on(pickNS, function(e) {
                var ext = {
                    params: [
                        { name: $this.id + '_color', value: e.detail.color }
                    ]
                };
                $this.callBehavior('change', ext);
            });
            this.addDestroyListener(function() {
                $(document).off(pickNS);
            });
        }
    },

    /**
     * Sets up the event listeners required by this widget.
     * @private
     */
    bindInputCallbacks: function() {
        var $this = this;
        if ($this.hasBehavior('change')) {
            $this.input.on('coloris:pick', function(e) {
                $this.callBehavior('change');
            });
        }
        if ($this.hasBehavior('open') || $this.cfg.parent) {
            $this.input.on('open.colorpicker', function(e) {
                $this.callBehavior('open', undefined, false);
                
                if ($this.cfg.parent) {
                   // #11076 dialog support of input
                   var dialog = $($this.cfg.parent);
                   var colorInput = dialog.find('#clr-color-value');
                   var newZIndex = PrimeFaces.nextZindex();
                   colorInput.zIndex(newZIndex);
                   colorInput.parent().zIndex(newZIndex);
                }
            });
        }

        $this.input.on('close.colorpicker', function(e) {
            if ($this.hasBehavior('close')) {
                $this.callBehavior('close');
            }
            if ($this.hasFloatLabel) {
                var container = $this.input.parent();
                PrimeFaces.queueTask(function() {
                    container.removeClass('ui-inputwrapper-focus');
                    PrimeFaces.utils.updateFloatLabel(container, $this.input, $this.hasFloatLabel);
                });
            }
        });

        if ($this.hasFloatLabel) {
            $this.input.on('focus.colorpicker', function() {
                $this.input.parent().addClass('ui-inputwrapper-focus');
            });
        }
    },

    /**
     * Sets up support for using the overlay color picker within an overlay dialog.
     * @private
     */
    setupDialogSupport: function() {
        var dialog = this.input[0].closest('.ui-dialog, .ui-sidebar');
        if (dialog) {
            this.cfg.parent = PrimeFaces.escapeClientId(dialog.id);
        }
    },

    /**
      * Gets the current color
      * @return {string} the current color
      */
    getColor: function() {
        var input = this.popup ? this.input : this.jq.find('#clr-color-value');
        return input.val();
    },

    /**
      * Sets the current color
      * @param {string} color the color to set
      */
    setColor: function(color) {
        if (!color) {
            return;
        }
        var newColor = color.toLowerCase();
        var input = this.popup ? this.input : this.jq.find('#clr-color-value');
        Coloris.setColor(newColor, input[0]);
    },

    /**
     * Shows the popup panel.
     */
    show: function() {
        if (this.input) {
            this.input.trigger('click');
        }
    },

    /**
     * Close the dialog and revert the color to its original value.
     * @param {boolean | undefined} revert true to revert the color to its original value
     */
    hide: function(revert) {
        if (this.input) {
            Coloris.close(revert);
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

// Global variable so Coloris is only initialized once
var colorisInitialized = false;

