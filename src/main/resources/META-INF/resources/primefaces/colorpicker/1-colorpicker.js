/**
 * __PrimeFaces Color Picker Widget__
 * 
 * ColorPicker is an input component with a color palette.
 * 
 * This uses a color picker plugin for jQuery. To interact with the color picker, you can use the following code.
 * 
 * ```javascript
 * // Assuming the widget variable of the color picker was set to "myColorPicker"
 * const colorPicker = PF("myColorPicker");
 * 
 * // Brings up the color picker (if "mode" was set to "popup")
 * colorPicker.jqEl.ColorPickerShow();
 * 
 * // Hides up the color picker (if "mode" was set to "popup")
 * colorPicker.jqEl.ColorPickerHide();
 * 
 * // Sets the currently selected color to "green"
 * colorPicker.jqEl.ColorPickerSetColor("00FF00");
 * ```
 * 
 * @typedef {"inline" | "popup"} PrimeFaces.widget.ColorPicker.DisplayMode Display mode of a color picker. `inline`
 * renders the color picker within the normal content flow, `popup` creates an overlay that is displayed when the user
 * clicks on the color.
 * 
 * @prop {JQuery} input DOM element of the INPUT element
 * @prop {JQuery} overlay DOM element of the OVERLAY container.
 * @prop {JQuery} livePreview DOM element of the live color preview.
 * @prop {JQuery} jqEl DOM element on which the JQuery ColorPicker plugin was initialized. You can use this element to
 * interact with the ColorPicker.
 * 
 * @interface {PrimeFaces.widget.ColorPickerCfg} cfg The configuration for the {@link  ColorPicker| ColorPicker widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.ColorPicker.DisplayMode} cfg.mode Whether the color picker is displayed inline or as a popup.
 * @prop {string} cfg.color Initial color to be displayed.
 * @prop {boolean} cfg.flat `true` if `mode` is not `popup`, `false` otherwise.
 * @prop {boolean} cfg.livePreview Whether the live preview of the selected color is enabled.
 */
 PrimeFaces.widget.ColorPicker = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        var $this = this;

        this.input = $(this.jqId + '_input');
        this.cfg.popup = this.cfg.mode == 'popup';
        this.jqEl = this.cfg.popup ? $(this.jqId + '_button') : $(this.jqId + '_inline');
        this.cfg.flat = !this.cfg.popup;
        this.cfg.livePreview = false;

        this.bindCallbacks();

        //ajax update check
        if(this.cfg.popup) {
            this.clearOrphanOverlay();
        }

        //create colorpicker
        this.jqEl.ColorPicker(this.cfg);

        //popup ui
        if (this.cfg.popup) {
            PrimeFaces.skinButton(this.jqEl);
            this.overlay = $(PrimeFaces.escapeClientId(this.jqEl.data('colorpickerId')));
            this.livePreview = $(this.jqId + '_livePreview');
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Sets up the event listeners required by this widget.
     * @private
     */
    bindCallbacks: function() {
        var $this = this;

        this.cfg.onChange = function(hsb, hex, rgb) {
            $this.input.val(hex);


            if($this.cfg.popup) {
                $this.livePreview.css('backgroundColor', '#' + hex);
            }

            $this.input.trigger('change');

            $this.callBehavior('change');
        };

        this.cfg.onShow = function() {
            if ($this.cfg.popup) {
                $this.overlay.css({
                    'z-index': PrimeFaces.nextZindex(),
                    'display':'block',
                    'opacity':'0',
                    'pointer-events': 'none'
                });

                $this.bindPanelEvents();
            }

            $this.setupDialogSupport();

            //position the overlay relative to the button
            $this.alignPanel();

            if ($this.cfg.popup) {
                $this.overlay.css({
                    'display':'none',
                    'opacity':'',
                    'pointer-events': ''
                });
            }
        };

        this.cfg.onHide = function(cp) {
            if ($this.cfg.popup) {
                $this.unbindPanelEvents();
            }

            $this.overlay.css('z-index', PrimeFaces.nextZindex());
            $(cp).fadeOut('fast');
            $this.callBehavior('close');
            return false;
        };
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        //popup ui
        if (this.cfg.popup && this.overlay) {
            this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jqEl, function() {
                $this.overlay.hide();
            });

            this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.overlay, function() {
                $this.overlay.hide();
            });
        }
    },

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents: function() {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    },

    /**
     * When a popup colorpicker is updated via AJAX, a new overlay is appended to body and the old overlay would be
     * orphaned. We need to remove the old overlay to prevent memory leaks.
     * @private
     */
    clearOrphanOverlay: function() {
        var $this = this;

        $(document.body).children('.ui-colorpicker-container').each(function(i, element) {
            var overlay = $(element),
            options = overlay.data('colorpicker');

            if(options.id == $this.id) {
                overlay.remove();
                return false;   //break;
            }
        });
    },

    /**
     * Sets up support for using the overlay color picker within an overlay dialog.
     * @private
     */
    setupDialogSupport: function() {
        var dialog = this.jqEl[0].closest('.ui-dialog');
        if (dialog) {
            var $dialog = $(dialog);

            if($dialog.length == 1 && $dialog.css('position') === 'fixed') {
                this.overlay.css('position', 'fixed');
            }
        }
    },

    /**
     * Aligns the overlay panel with the color picker according to the current configuration. It is usually positioned
     * next to or below the input field to which it is attached.
     */
    alignPanel: function() {
        this.overlay.css({
                left: '',
                top: ''
            })
            .position({
                my: 'left top',
                at: 'left bottom',
                of: this.jqEl,
                collision: 'flipfit'
            });
    }
});
