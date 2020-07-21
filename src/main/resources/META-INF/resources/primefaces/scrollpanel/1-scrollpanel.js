/** 
 * __PrimeFaces ScrollPanel Widget__
 * 
 * ScrollPanel is used to display scrollable content with theme aware scrollbars instead of native browser scrollbars.
 * 
 * @prop {JQueryJScrollPane.JScrollPaneInstance} jsp The current jQuery Scroll Pane instance.
 * 
 * @interface {PrimeFaces.widget.ScrollPanelCfg} cfg The configuration for the {@link  ScrollPanel| ScrollPanel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {JQueryJScrollPane.JScrollPaneSettings} cfg
 */
PrimeFaces.widget.ScrollPanel = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.jsp = this.jq.jScrollPane(this.cfg).data('jsp');
    },

    /**
     * Scrolls to the given scroll position.
     * @param {number} x Horizontal coordinate of the new scroll position.
     * @param {number} y Vertical coordinate of the new scroll position.
     */
    scrollTo: function(x, y) {
        this.jsp.scrollTo(x, y);
    },

    /**
     * Scroll horizontally to the given scroll position.
     * @param {number} x The new horizontal scroll position.
     */
    scrollX: function(x) {
        this.jsp.scrollToX(x);
    },

    /**
     * Scroll vertically to the given scroll position.
     * @param {number} y The new vertical scroll position.
     */
    scrollY: function(y) {
        this.jsp.scrollToY(y);
    },

    /**
     * Redraws the scrollbars.
     */
    redraw: function() {
        this.jsp.reinitialise();
    }

});