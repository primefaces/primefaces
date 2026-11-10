/**
 * The configuration for the {@link Keyboard} widget.
 */
export interface KeyboardCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, JQueryKeypad.KeypadSettings {
    /**
     * The name of the built-in keyboard layout to use. Mutually exclusive with `layoutTemplate`.
     */
    layoutName: string;
    /**
     * An optional custom keyboard layout template specified by the user. The keyboard rows must be separated by a
     * comma. Each row contains the keys that should be displayed on that row. To specify a control button (space,
     * back, shift etc.), separate the name of the control key with a dash.
     */
    layoutTemplate: string;
}

/**
 * __PrimeFaces Keyboard Widget__
 *
 * Keyboard is an input component that uses a virtual keyboard to provide the input. Notable features are the
 * customizable layouts and skinning capabilities.
 */
export class Keyboard<Cfg extends KeyboardCfg = KeyboardCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        // the utilities are looked up in the widget scope, they are not a widget
        const keyboardUtils = (PrimeFaces.widget as PrimeFacesTypes.WidgetRegistry & { KeyboardUtils: PrimeFacesTypes.widget.KeyboardUtils }).KeyboardUtils;
        if (this.cfg.layoutTemplate)
            this.cfg.layout = keyboardUtils.createLayoutFromTemplate(this.cfg.layoutTemplate);
        else
            this.cfg.layout = keyboardUtils.getPresetLayout(this.cfg.layoutName!);

        // effectDuration is rendered as string, jQuery only accepts milliseconds as number
        if (typeof this.cfg.duration === 'string' && /^\d+$/.test(this.cfg.duration)) {
            this.cfg.duration = parseInt(this.cfg.duration, 10);
        }

        this.cfg.beforeShow = (div, inst) => {
            PrimeFaces.nextZindex($(div).addClass('p-input-overlay'));
            this.bindPanelEvents();
        };

        this.cfg.onClose = () => {
            this.unbindPanelEvents();
        };

        this.jq.keypad(this.cfg);

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        // beforeShow is also invoked when the keypad renders on init, so drop handlers from a previous call
        this.unbindPanelEvents();

        //Hide overlay on resize/scroll
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', null, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.jq.keypad('hide');
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.jq.keypad('hide');
            }
        });
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }
}
