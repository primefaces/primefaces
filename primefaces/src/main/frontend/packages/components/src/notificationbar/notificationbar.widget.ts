/**
 * The configuration for the {@link NotificationBar} widget.
 */
export interface NotificationBarCfg extends PrimeFacesTypes.widget.DynamicOverlayFeatureWidgetCfg {
    /** Whether the notification bar is shown by default on page load. */
    autoDisplay: boolean;
    /** Effect applied when the notification bar is shown or hidden. */
    effect: PrimeFacesTypes.widget.NotificationBar.Effect;
    /** Speed of the effect when the notification bar is shown or hidden. */
    effectSpeed: PrimeFacesTypes.widget.NotificationBar.EffectSpeed;
    /** Position of the bar, either top or bottom. */
    position: PrimeFacesTypes.widget.NotificationBar.Position;
}

/**
 * __PrimeFaces NotificationBar Widget__
 *
 * NotificationBar displays a multipurpose fixed positioned panel for notification.
 */
export class NotificationBar<Cfg extends NotificationBarCfg = NotificationBarCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        //relocate
        this.jq.css(this.cfg.position as string, '0px');
        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.getId());

        //display initially
        if (this.cfg.autoDisplay) {
            this.jq.css('display', 'block');
        }

        //bind events
        this.jq.children('.p-notificationbar-close').on("click", () => {
            this.hide();
        });
    }

    /**
     * Shows the notification bar.
     *
     * The up-to-three arguments will be routed to jQuery as-is.
     *
     * @param a1 First parameter passed through to jQuery.
     * @param a2 Second parameter passed through to jQuery.
     * @param a3 Third parameter passed through to jQuery.
     *
     * @see http://api.jquery.com/slidedown/
     * @see http://api.jquery.com/fadein/
     * @see http://api.jquery.com/show/
     */
    show(a1?: JQuery.Duration, a2?: string, a3?: (this: HTMLElement) => void): void {
        // the arguments are passed through as-is, jQuery handles missing ones; the duration defaults to effectSpeed
        const duration = a1 ?? (this.cfg.effectSpeed as JQuery.Duration);
        if (this.cfg.effect === 'slide') {
            this.jq.slideDown(duration, a2 as string, a3 as (this: HTMLElement) => void);
        }
        else if (this.cfg.effect === 'fade') {
            this.jq.fadeIn(duration, a2 as string, a3 as (this: HTMLElement) => void);
        }
        else if (this.cfg.effect === 'none') {
            this.jq.show(a1 as JQuery.Duration, a2 as string, a3 as (this: HTMLElement) => void);
        }
    }

    /**
     * Hides the notification bar.
     */
    hide(): void {
        if (this.cfg.effect === 'slide') {
            this.jq.slideUp(this.cfg.effectSpeed as JQuery.Duration);
        }
        else if (this.cfg.effect === 'fade') {
            this.jq.fadeOut(this.cfg.effectSpeed as JQuery.Duration);
        }
        else if (this.cfg.effect === 'none') {
            this.jq.hide();
        }
    }

    /**
     * Checks whether the notification bar is currently displayed.
     * @returns `true` if the notification bar is currently visible, `false` otherwise.
     */
    isVisible(): boolean {
        return this.jq.is(':visible');
    }

    /**
     * Shows the notification bar it is currently hidden, or hides it if it is currently displayed.
     */
    toggle(): void {
        if (this.isVisible()) {
            this.hide();
        }
        else {
            this.show();
        }
    }
}
