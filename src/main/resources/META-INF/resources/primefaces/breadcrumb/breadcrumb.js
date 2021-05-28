/**
 * __PrimeFaces BreadCrumb Widget__
 *
 * BreadCrumb is an component used to output a navigation trail.
 *
 * @interface {PrimeFaces.widget.BreadCrumbCfg} cfg The configuration for the {@link BreadCrumb| BreadCrumb widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.responsive When `true`, enables responsive mode.
 */
PrimeFaces.widget.BreadCrumb = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        if (this.cfg.responsive === true) {
            this.limit();
        }
    },

    /**
     * Limit bread crumb to the items on the first line.
     */
    limit: function() {
        var items = $('li', this.jq);
        if (items.length <= 2) {
            return;
        }
        var end = items.length - 1,
                firstY = this.getY(items.get(0)),
                showExpander = false;
        for (i = 1; i < end; i++) {
            if (this.getY(items.get(end)) > firstY) {
                $(items.get(i)).hide();
                showExpander = true;
            }
            else {
                break;
            }
        }
        if (showExpander) {
            this.addExpander(0);
        }
    },

    /**
     * Get Y position of the provided HTML node.
     * @param {HTMLElement} node to get the Y position of.
     * @returns {number} Y position.
     */
    getY: function(node) {
        return node.getBoundingClientRect().y;
    },

    /**
     * Inserts expander before the provided menu item index.
     * @param {number} index of the menu item to insert the expander before.
     */
    addExpander: function(index) {
        var $this = this;
        this.expander = $('<li class=expander><a tabindex=0 class="ui-menuitem-link ui-corner-all" href="#"><span class=ui-menuitem-text>…</span></a></li>');
        this.expander.click(function(){
           $('li', $this.jq).show();
           $this.expander.remove();
           $('li:visible a', $this.jq).get(0).focus();
        });
        this.expander.insertBefore($('li:visible', this.jq).eq(index));
    },

});
