/**
 * __PrimeFaces ScrollTop Widget__
 *
 * ScrollTop gets displayed after a certain scroll position and used to navigates to the top of the page quickly.
 *
 * @prop {JQuery} scrollTop DOM element of the scrollTop.
 * @prop {JQuery} visible Whether the ScrollTop element is visible or not.
 * @prop {JQuery} threshold The scroll threshold for displaying the ScrollTop element.
 * @prop {string} target Target element of the ScrollTop.
 * @prop {string} scrollElement Window or parent element of the ScrollTop.
 *
 * @interface {PrimeFaces.widget.ScrollTopCfg} cfg The configuration for the {@link  ScrollTop| ScrollTop widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number} cfg.threshold Value of the vertical scroll position of the target to toggle the visibility.
 * @prop {string} cfg.target Target of the ScrollTop.
 * @prop {string} cfg.behavior Scrolling behavior of the ScrollTop.
 */
PrimeFaces.widget.ScrollTop = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.scrollTop = $(this.jqId);

        var $this = this;
        this.visible = false;
        this.threshold = $this.cfg.threshold;
        this.target = $this.cfg.target;
        this.behavior = $this.cfg.behavior;
        this.scrollElement = this.target === 'window' ? $(window) : this.scrollTop.parent();

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        //Hide scrollTop on first initialization.
        this.scrollTop.hide();

        $this.checkVisibility();
        $this.bindScrollEvent();
    },

    /**
     * Checks visibility of the ScrollTop element.
     * @private
     */
    checkVisibility: function () {
        var $this = this;

        $this.scrollElement.scroll(function (e) {
            var checkVisibility = $this.threshold < $this.scrollElement.scrollTop();

            if (checkVisibility !== $this.visible) {
                if (!$this.visible) {
                    $this.scrollTop.fadeIn("slow");
                    $this.scrollTop.css("zIndex", PrimeFaces.nextZindex());
                    $this.visible = true;
                }
                else {
                    $this.scrollTop.fadeOut("slow");
                    $this.visible = false;
                }
            }
        });
    },

    /**
     * Sets up scroll to top event to the ScrollTop element.
     * @private
     */
    bindScrollEvent: function () {
        var $this = this;

        this.scrollTop.on("click", function(e) {
            e.preventDefault();
            $this.scrollElement.get(0).scroll({
                top: 0,
                behavior: $this.behavior
            });
        })
    },
});