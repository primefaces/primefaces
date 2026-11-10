/**
 * The configuration for the {@link Sticky} widget.
 */
export interface StickyCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Margin to the top of the page while fixed, recalculated from `stickyTopAt`. */
    margin: number;
    /** Selector of elements fixed at the top of the page, their height is used as margin. */
    stickyTopAt: string | null;
    /** The client ID of the component to make sticky. */
    target: string;
}

/**
 * __PrimeFaces Sticky Widget__
 *
 * Sticky positions other components as fixed so that they stay in the window viewport during scrolling.
 */
export class Sticky<Cfg extends StickyCfg = StickyCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The component to make sticky. */
    target: JQuery = $();
    /** Whether the target is currently fixed to the top of the page. */
    fixed: boolean = false;
    /** The placeholder at the original position of the target while it is fixed. */
    ghost?: JQuery;
    /** The position and height of the target before it was fixed. */
    initialState: PrimeFacesTypes.widget.Sticky.InitialState = { top: 0, height: 0 };

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target ?? ''));
        this.cfg.margin = this.cfg.margin || 0;
        this.cfg.stickyTopAt = this.cfg.stickyTopAt || null;

        this.initialState = {
            top: this.target.offset()?.top ?? 0,
            height: this.target.height() ?? 0
        };

        this.calculateDynamicMargin();

        this.bindEvents();
    }

    // no super.refresh / init on purpose (#660): initialState must stay the one measured before the target was fixed,
    // and the scroll/resize handlers stay bound, so only the (updated) target is resolved again and fixed if needed
    override refresh(_cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target ?? ''));

        if (this.fixed) {
            this.ghost?.remove();
            this.fix(true);
        }
    }

    private bindEvents(): void {
        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.getId() + '_align', () => {
            if (($(window).scrollTop() ?? 0) > this.initialState.top - (this.cfg.margin ?? 0)) {
                this.fix();
            }
            else {
                this.restore();
            }
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.getId() + '_align', null, () => {
            const marginChanged = this.calculateDynamicMargin();
            const ghostTop = this.ghost?.offset()?.top;
            const targetTop = this.target.offset()?.top;
            this.initialState.top = (this.ghost && this.ghost.length && ghostTop !== undefined && ghostTop > 0)
                    ? ghostTop
                    : (targetTop !== undefined ? targetTop : this.initialState.top);
            if (($(window).scrollTop() ?? 0) > this.initialState.top - (this.cfg.margin ?? 0)) {
                if (marginChanged && this.fixed) {
                    this.ghost?.remove();
                    this.fix(true);
                }
                else {
                    this.fix();
                }
            }
            else {
                this.restore();
            }
            if (this.fixed) {
                this.target.width((this.ghost?.outerWidth() ?? 0) - ((this.target.outerWidth() ?? 0) - (this.target.width() ?? 0)));
            }
        });
    }

    /**
     * Pins the target to the top of the page.
     * @param force `true` to pin the target even if it is pinned already.
     */
    fix(force?: boolean): void {
        if (!this.fixed || force) {
            const winScrollTop = $(window).scrollTop() ?? 0;

            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin + 'px',
                'z-index': PrimeFaces.utils.nextStickyZindex()
            })
            .addClass('p-sticky');

            this.ghost = $('<div class="p-sticky-ghost"></div>').height(this.target.outerHeight() ?? 0).insertBefore(this.target);
            this.target.width((this.ghost?.outerWidth() ?? 0) - ((this.target.outerWidth() ?? 0) - (this.target.width() ?? 0)));
            this.fixed = true;
            $(window).scrollTop(winScrollTop);
        }
    }

    /**
     * Unpins the target and returns it to its normal position.
     */
    restore(): void {
        if (this.fixed) {
            this.target.css({
                position: 'static',
                top: 'auto',
                width: 'auto'
            })
            .removeClass('p-sticky');

            this.ghost?.remove();
            this.fixed = false;
        }
    }

    /**
     * Sets the margin to the height of the elements matching `stickyTopAt`.
     * @returns `true` if the margin has changed.
     */
    calculateDynamicMargin(): boolean {
        const fixedElementsOnTop = this.cfg.stickyTopAt ? $(this.cfg.stickyTopAt) : null;
        const previousMargin = this.cfg.margin;
        if (fixedElementsOnTop && fixedElementsOnTop.length) {
            this.cfg.margin = fixedElementsOnTop.toArray().reduce((height: number, elem) => height + ($(elem).outerHeight() ?? 0), 0);
        }
        return previousMargin !== this.cfg.margin;
    }
}
