import Coloris from "@melloware/coloris";

/**
 * The Coloris API, including the methods that exist at runtime but are missing in the Coloris typings.
 */
const ColorisApi = Coloris as typeof Coloris & {
    /**
     * Wraps the fields matching the selector, so that the color thumbnail is shown.
     * @param selector CSS selector of the fields to wrap.
     */
    wrap(selector: string): void;
    /**
     * Sets the color of a field and dispatches a `change` event when the color changed.
     * @param color The new color.
     * @param target The field to set the color on.
     */
    setColor(color: string, target: HTMLElement | undefined): void;
};

// Ensure Coloris is only initialized once
let colorisInitialized = false;
// Coloris binds its (not removable) document listeners for the popup fields on every call with `el`
let colorisFieldsBound = false;

/**
 * The settings passed to Coloris, `el` is `null` in inline mode.
 */
type ColorPickerSettings = Omit<Coloris.ColorisOptions, "el"> & {
    /** CSS selector or elements of the fields to bind, `null` in inline mode. */
    el?: Coloris.ColorisOptions["el"] | null;
};

/**
 * The configuration for the {@link ColorPicker} widget.
 */
export interface ColorPickerCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** The localized ARIA labels of the Coloris dialog. */
    a11y: Coloris.Accessibility;
    /** Whether to enable alpha support. */
    alpha: boolean;
    /** Whether to show the clear button. */
    clearButton: boolean;
    /** The label of the clear button. */
    clearLabel: string;
    /** Whether to show the close button. */
    closeButton: boolean;
    /** The label of the close button. */
    closeLabel: string;
    /** The default color, the value of the component. */
    defaultColor: string;
    /** The fields Coloris binds to, `null` in inline mode. */
    el: Coloris.ColorisOptions["el"] | null;
    /** Whether to focus the color value input when the color picker dialog is opened. */
    focusInput: boolean;
    /** Whether to always include the alpha value in the color value. */
    forceAlpha: boolean;
    /** The preferred color string format. */
    format: Coloris.ColorFormat;
    /** Whether to show the format toggle buttons. */
    formatToggle: boolean;
    /** Whether the color picker is rendered inline, set from {@link mode}. */
    inline: boolean;
    /** The instance of for configuring in popup mode. */
    instance: string;
    /** Locale used for the labels of the color picker. */
    locale: string;
    /** Whether the color picker is displayed inline or as a popup. */
    mode: PrimeFacesTypes.widget.ColorPicker.DisplayMode;
    /**
     * In popup mode the CSS selector of the parent dialog, set when the popup is used within a dialog. In inline mode
     * the CSS selector of this widget.
     */
    parent: string;
    /** Whether the color picker is displayed right to left. */
    rtl: boolean;
    /** Whether to select and focus the color value input when the color picker dialog is opened. */
    selectInput: boolean;
    /** The colors of the swatches. */
    swatches: string[];
    /** Whether to hide all the color picker widgets except the swatches. */
    swatchesOnly: boolean;
    /** The color theme of the color picker. */
    theme: Coloris.Theme;
    /** Light or dark mode of the color picker. */
    themeMode: Coloris.ThemeMode;
}

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
 */
export class ColorPicker<Cfg extends ColorPickerCfg = ColorPickerCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** DOM element of the INPUT element, only in popup mode. */
    input?: JQuery;
    /** True if popup mode, else inline mode. */
    popup: boolean = false;
    /** Is this component wrapped in a float label. */
    hasFloatLabel: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.setupGlobalDefaults();
        this.setupPopup();
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this._cleanup();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this._cleanup();
    }

    /**
     * Clean up this widget and remove events from the DOM.
     */
    private _cleanup(): void {
        if (this.input) {
            this.input.removeClass('p-colorpicker');
            this.input.parent().find('button').remove();
            Coloris.removeInstance('.' + this.cfg.instance);
        }
        else {
            // remove the inline from the DOM
            this.jq.empty();
        }
    }

    /**
     * Only one instance of Coloris is allowed so ensure it only loads defaults once.
     */
    private setupGlobalDefaults(): void {
        this.popup = this.cfg.mode === 'popup';
        if (colorisInitialized) {
            return;
        }

        this.configureLocale();
        this.cfg.inline = !this.popup;
        this.cfg.themeMode = this.cfg.themeMode || PrimeFaces.env.getThemeContrast(this.jq[0]);
        let settings: ColorPickerSettings = this.cfg;
        let wrapFields = false;
        if (this.popup) {
            colorisInitialized = true;
            settings = {
                inline: this.cfg.inline,
                a11y: this.cfg.a11y,
                clearLabel: this.cfg.clearLabel,
                closeLabel: this.cfg.closeLabel
            };
            if (colorisFieldsBound) {
                // the delegated document listeners already handle new fields, they only need to be wrapped
                wrapFields = true;
            }
            else {
                colorisFieldsBound = true;
                settings.el = '.p-colorpicker';
            }
        }
        else {
            colorisInitialized = false;
            settings.el = null;
            settings.parent = this.jqId;
            this.bindInlineCallbacks();
        }
        $(document).ready(() => {
            Coloris.init();
            // Coloris ignores the `el` setting when it is null
            Coloris(settings as Coloris.ColorisOptions);
            if (wrapFields) {
                ColorisApi.wrap('.p-colorpicker');
            }
            if (this.cfg.inline) {
                Coloris.updatePosition();
            }
            colorisInitialized = false;
        });
    }

    /**
     * Localizes the ARIA accessibility labels for the color picker.
     */
    private configureLocale(): void {
        const lang = PrimeFaces.getLocaleSettings(this.cfg.locale);
        if (!lang) {
            return;
        }
        if (lang.aria && lang.aria.close) { this.cfg.closeLabel = lang.aria.close; }
        if (lang.clear) { this.cfg.clearLabel = lang.clear; }
        if (lang.isRTL) { this.cfg.rtl = true; }
        if (lang.aria) {
            // the private cache of the global locale, read by getAriaLabel below
            PrimeFaces['localeSettings'] = lang;
            const a11y: Partial<Coloris.Accessibility> = {};
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
            // Coloris uses its default for each label that is not set
            this.cfg.a11y = a11y as Coloris.Accessibility;
        }
    }

    /**
     * Configures a single ARIA label from PF locale to Coloris a11y.
     * @param label the PF label to lookup in locale.js
     * @param a11y the a11y JSON object for Coloris
     * @param property the JSON property to set in a11y
     */
    private configureAriaLabel(label: keyof PrimeFacesTypes.LocaleAria, a11y: Partial<Coloris.Accessibility>, property: keyof Coloris.Accessibility): void {
        const ariaLabel = this.getAriaLabel(label);
        if (ariaLabel) {
            a11y[property] = ariaLabel;
        }
    }

    /**
     * Configure the color picker for popup mode.
     */
    private setupPopup(): void {
        if (!this.popup) {
            return;
        }

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
        Coloris.setInstance('.' + this.cfg.instance, this.cfg as Partial<Coloris.ColorisVirtualInstanceOptions>);

        // add error styling to thumbnail button if necessary
        $(document).ready(() => {
            const triggerButton = this.input!.prev();
            if (this.input!.hasClass('p-state-error')) {
                triggerButton.addClass('p-state-error');
            }
        });
    }

    /**
     * Sets up the event listeners required by this widget for inline mode.
     */
    private bindInlineCallbacks(): void {
        if (this.hasBehavior('change')) {
            const pickNS = 'coloris:pick.' + this.id;
            $(document).off(pickNS).on(pickNS, (e) => {
                const ext = {
                    params: [
                        { name: this.id + '_color', value: (e.detail as unknown as Coloris.PickEventData).color }
                    ]
                };
                this.callBehavior('change', ext);
            });
            this.addDestroyListener(function () {
                $(document).off(pickNS);
            });
        }
    }

    /**
     * Sets up the event listeners required by this widget.
     */
    private bindInputCallbacks(): void {
        const input = this.input!;
        if (this.hasBehavior('change')) {
            input.on('coloris:pick', () => {
                this.callBehavior('change');
            });
        }
        if (this.hasBehavior('open') || this.cfg.parent) {
            input.on('open.colorpicker', () => {
                this.callBehavior('open', undefined, false);

                if (this.cfg.parent) {
                    // #11076 dialog support of input
                    const dialog = $(this.cfg.parent);
                    const colorInput = dialog.find('#clr-color-value');
                    const newZIndex = PrimeFaces.nextZindex();
                    colorInput.zIndex(newZIndex);
                    colorInput.parent().zIndex(newZIndex);
                }
            });
        }

        input.on('close.colorpicker', () => {
            if (this.hasBehavior('close')) {
                this.callBehavior('close');
            }
            if (this.hasFloatLabel) {
                const container = input.parent();
                PrimeFaces.queueTask(() => {
                    container.removeClass('p-inputwrapper-focus');
                    PrimeFaces.utils.updateFloatLabel(container, input, this.hasFloatLabel);
                });
            }
        });

        if (this.hasFloatLabel) {
            input.on('focus.colorpicker', () => {
                input.parent().addClass('p-inputwrapper-focus');
            });
        }
    }

    /**
     * Sets up support for using the overlay color picker within an overlay dialog.
     */
    private setupDialogSupport(): void {
        const dialog = this.input![0]!.closest('.p-dialog, .p-sidebar');
        if (dialog) {
            this.cfg.parent = PrimeFaces.escapeClientId(dialog.id);
        }
    }

    /**
     * Gets the current color
     * @return the current color
     */
    getColor(): string {
        const input = this.popup ? this.input! : this.jq.find('#clr-color-value');
        return input.val() as string;
    }

    /**
     * Sets the current color
     * @param color the color to set
     */
    setColor(color: string): void {
        if (!color) {
            return;
        }
        const newColor = color.toLowerCase();
        const input = this.popup ? this.input! : this.jq.find('#clr-color-value');
        ColorisApi.setColor(newColor, input[0]);
    }

    /**
     * Shows the popup panel.
     */
    show(): void {
        if (this.input) {
            this.input.trigger('click');
        }
    }

    /**
     * Close the dialog and revert the color to its original value.
     * @param revert true to revert the color to its original value
     */
    hide(revert?: boolean): void {
        if (this.input) {
            Coloris.close(revert);
        }
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }
}
