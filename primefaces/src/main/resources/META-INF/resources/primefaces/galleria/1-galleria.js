/**
 * __PrimeFaces Galleria Widget__
 *
 * Galleria is a content gallery component.
 *
 * @prop {JQuery} primeGalleriaWidget The DOM element for the caption below the image.
 *
 * @interface {PrimeFaces.widget.GalleriaCfg} cfg The configuration for the {@link  Galleria| Galleria widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {number} cfg.activeIndex Index of the first item.
 * @prop {boolean} cfg.fullScreen Whether to display the component on fullscreen.
 * @prop {string} cfg.closeIcon Close icon on fullscreen mode.
 * @prop {number} cfg.numVisible Number of items per page.
 * @prop {boolean} cfg.showThumbnails Whether to display thumbnail container.
 * @prop {boolean} cfg.showIndicators Whether to display indicator container.
 * @prop {boolean} cfg.showIndicatorsOnItem When enabled, indicator container is displayed on item container.
 * @prop {boolean} cfg.showCaption Whether to display caption container.
 * @prop {boolean} cfg.showItemNavigators Whether to display navigation buttons in item container.
 * @prop {boolean} cfg.showThumbnailNavigators Whether to display navigation buttons in thumbnail container.
 * @prop {boolean} cfg.showItemNavigatorsOnHover Whether to display navigation buttons on item container's hover.
 * @prop {boolean} cfg.changeItemOnIndicatorHover When enabled, item is changed on indicator item's hover.
 * @prop {boolean} cfg.circular Defines if scrolling would be infinite.
 * @prop {boolean} cfg.autoPlay Items are displayed with a slideshow in autoPlay mode.
 * @prop {number} cfg.transitionInterval Time in milliseconds to scroll items.
 * @prop {string} cfg.thumbnailsPosition Position of thumbnails. Valid values are "bottom", "top", "left" and "right".
 * @prop {string} cfg.verticalViewPortHeight Height of the viewport in vertical layout.
 * @prop {string} cfg.indicatorsPosition Position of indicators. Valid values are "bottom", "top", "left" and "right".
 * @prop {{breakpoint:string, numVisible:number}[]} cfg.responsiveOptions A model of options for responsive design.
 */
PrimeFaces.widget.Galleria = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.selector = this.jqId;

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.primeGalleriaWidget = this.jq.galleria(this.cfg).data('prime-galleria');
    },
    
    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if(this.primeGalleriaWidget) {
            this.primeGalleriaWidget.destroy();
        }

        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        if (this.primeGalleriaWidget) {
            this.primeGalleriaWidget.destroy();
        }
    },
    
    /**
     * Shows content on fullscreen mode.
     */
    show: function() {
        this.primeGalleriaWidget.show();
    },

    /**
     * Hides content on fullscreen mode.
     */
    hide: function() {
        this.primeGalleriaWidget.hide();
    },

    /**
     * Moves to the next content that comes after the currently shown content.
     */
    next: function() {
        this.primeGalleriaWidget.next();
    },

    /**
     * Moves to the previous content that comes before the currently shown content.
     */
    prev: function() {
        this.primeGalleriaWidget.prev();
    }
});