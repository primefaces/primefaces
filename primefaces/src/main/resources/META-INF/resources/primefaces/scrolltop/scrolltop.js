/**
 * __PrimeFaces ScrollTop Widget__
 *
 * ScrollTop gets displayed after a certain scroll position and used to navigates to the top of the page quickly.
 *
 * @prop {JQuery} scrollElement Window or parent element of the ScrollTop.
 *
 * @interface {PrimeFaces.widget.ScrollTopCfg} cfg The configuration for the {@link  ScrollTop| ScrollTop widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {string} cfg.behavior Scrolling behavior of the ScrollTop.
 * @prop {string} cfg.target Target element of the scroll top widget.
 * @prop {number} cfg.threshold Value of the vertical scroll position of the target to toggle the visibility.
 */
PrimeFaces.widget.ScrollTop = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.scrollElement = this.cfg.target === 'window' ? $(window) : this.jq.parent();
        
        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this,
        scrollNS = 'scroll.scrollTop' + this.id,
        zIndex = $this.jq.css('zIndex');
        
        this.jq.on('click.scrollTop', function(e) {
            $this.scrollElement.get(0).scroll({
                top: 0,
                behavior: $this.cfg.behavior
            });
            
            e.preventDefault();
        });

        this.scrollElement.off(scrollNS).on(scrollNS, function() {
            if ($this.cfg.threshold < $this.scrollElement.scrollTop()) {
                $this.jq.fadeIn({
                    duration: 150,
                    start: function() {
                        if (zIndex === 'auto' && $this.jq.css('zIndex') === 'auto') {
                            $this.jq.css('zIndex', PrimeFaces.nextZindex());
                        }
                    }
                });
            }
            else {
                $this.jq.fadeOut({
                    duration: 150,
                    start: function() {
                        if (zIndex === 'auto') {
                            $this.jq.css('zIndex', '');
                        }
                    }
                });
            }
        });
    }
});