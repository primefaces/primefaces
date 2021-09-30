/**
 * __PrimeFaces Dock Widget__
 * 
 * Dock component mimics the well known dock interface of Mac OS X.
 * 
 * @prop {JQuery} items The DOM elements for the dock items.
 * @prop {JQuery} links The DOM elements for the clickable anchors of the dock items.
 * 
 * @interface {PrimeFaces.widget.DockCfg} cfg The configuration for the {@link  Dock| Dock widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.animate Whether opening and closing the dock should be animated.
 * @prop {string} cfg.animationDuration The duration for the opening and closing animation in milliseconds.
 * @prop {boolean} cfg.blockScroll Whether to block scrolling of the document. 
 */
PrimeFaces.widget.Dock = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);

        this.items = $('.ui-dock ul li');
        this.links = $(".ui-dock li a");

        if (this.cfg.blockScroll) {
            PrimeFaces.utils.preventScrolling();
        }

        this.bindAnimations();
    },

    /**
     * Sets up the hover and click event listeners required for the dock.
     * @private
     */
    bindAnimations: function () {
        var $this = this;
        this.items.on('mouseenter.dock',
            function () {
                var item = $(this);
                item.addClass('active');
                item.prev().addClass('prev').prev().addClass('prev-anchor');
                item.next().addClass('next').next().addClass('next-anchor');
            }
        ).on('mouseleave.dock', 
            function () {
                $this.items.removeClass('active prev next next-anchor prev-anchor');
            }
        );

        // add bounce effect 
        if (this.cfg.animate) {
            this.links.on("click.dock", function (e) {
                var item = $(this);
                item.addClass('ui-dock-bounce').delay($this.cfg.animationDuration).queue(function () {
                    item.removeClass('ui-dock-bounce');
                    item.dequeue();
                });
            });
        }

    }

});