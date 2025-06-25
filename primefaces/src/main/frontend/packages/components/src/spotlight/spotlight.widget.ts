/**
 * The configuration for the {@link  Spotlight} widget.
 * 
 * You can access this configuration via {@link Spotlight.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface SpotlightCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Whether the spotlight is initially turned on.
     */
    active: boolean;

    /**
     * The search expression for the target component to highlight.
     */
    target: string;
}

/**
 * __PrimeFaces Spotlight Widget__
 * 
 * Spotlight highlights a certain component on page, drawing the user's attention to it.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Spotlight<Cfg extends SpotlightCfg = SpotlightCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The DOM element for the target component to highlight.
     */
    target: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);

        if (!$(document.body).children('.ui-spotlight').length) {
            this.createMasks();
        }

        if (this.cfg.active) {
            this.show();
        }
    }

    /**
     * Creates the mask overlay element for the spotlight effect and adds it to the DOM.
     */
    private createMasks(): void {
        $(document.body).append('<div class="ui-widget-overlay ui-spotlight ui-spotlight-top ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-bottom ui-helper-hidden"></div>' +
            '<div class="ui-widget-overlay ui-spotlight ui-spotlight-left ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-right ui-helper-hidden"></div>');
    }

    /**
     * Turns the spotlight on so that a certain part of the page is highlighted.
     */
    show(): void {
        this.calculatePositions();

        this.target.attr({
            'role': 'dialog'
            , 'aria-modal': true
        });
        $(document.body).children('div.ui-spotlight').show();

        this.bindEvents();
    }

    /**
     * Computes and applies the rectangular position of the spotlight.
     */
    private calculatePositions(): void {
        const doc = $(document);
        const documentBody = $(document.body);
        const offset = PrimeFaces.utils.calculateRelativeOffset(this.target);
        const zIndex = PrimeFaces.nextZindex();

        documentBody.children('div.ui-spotlight-top').css({
            'left': '0px',
            'top': '0px',
            'width': documentBody.width() + 'px',
            'height': offset.top + 'px',
            'z-index': zIndex
        });

        const bottomTop = offset.top + (this.target.outerHeight() ?? 0);
        documentBody.children('div.ui-spotlight-bottom').css({
            'left': '0px',
            'top': bottomTop + 'px',
            'width': documentBody.width() + 'px',
            'height': ((doc.height() ?? 0) - bottomTop) + 'px',
            'z-index': zIndex
        });

        documentBody.children('div.ui-spotlight-left').css({
            'left': '0px',
            'top': offset.top + 'px',
            'width': offset.left + 'px',
            'height': this.target.outerHeight() + 'px',
            'z-index': zIndex
        });

        const rightLeft = offset.left + (this.target.outerWidth() ?? 0);
        documentBody.children('div.ui-spotlight-right').css({
            'left': rightLeft + 'px',
            'top': offset.top + 'px',
            'width': ((documentBody.width() ?? 0) - rightLeft) + 'px',
            'height': this.target.outerHeight() + 'px',
            'z-index': zIndex
        });
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.target.data('zindex', this.target.zIndex()).css('z-index', PrimeFaces.nextZindex());

        if (this.cfg.blockScroll) {
            PrimeFaces.utils.preventScrolling();
        }

        PrimeFaces.utils.preventTabbing(this, this.getId(), this.target.zIndex(), () => {
            return this.target.find(':tabbable');
        });

        const namespace = '.spotlight' + this.id;
        $(window).on('resize' + namespace + ' scroll' + namespace, () => {
            this.calculatePositions();
        });
    }

    /**
     * Removes the event listeners that were added when the spotlight was turned on.
     */
    private unbindEvents(): void {
        PrimeFaces.utils.enableTabbing(this, this.getId());
        if (this.cfg.blockScroll) {
            PrimeFaces.utils.enableScrolling();
        }
        var namespace = '.spotlight' + this.id;
        $(window).off(namespace);
    }

    /**
     * Turns of the spotlight so that the entire page is visible normally again.
     */
    hide(): void {
        $(document.body).children('.ui-spotlight').hide();
        this.unbindEvents();
        this.target.css('z-index', String(this.target.zIndex()));
        this.target.attr({
            'role': ''
            , 'aria-modal': false
        });
    }
}