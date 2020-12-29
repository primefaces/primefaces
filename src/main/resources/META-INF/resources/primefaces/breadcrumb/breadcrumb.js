/**
 * __PrimeFaces BreadCrumb Widget__
 *
 * BreadCrumb is an component used to output a navigation trail.
 *
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.BreadCrumb = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        if (cfg.limit > 1) {
            this.limit(cfg.limit);
        }
    },

    /**
     * Limit bread crumb to the provided amount of menu items.
     * @param {number} limit to this amount of menu items.
     * @returns {undefined}
     */
    limit: function(limit) {
        var items = $('li[role=menuitem]', this.jq);
        var start = (this.cfg.permaRoot ? 1 : 0);
        if (items.length <= limit - start) {
            return;
        }
        for (i = start; i < items.length - limit; i++) {
            var index = 2 * i;
            $('li', this.jq).slice(index, index + 2).hide();
            return false;
        }
        this.addExpander(start);
    },

    /**
     * Inserts expander before the provided menu item index.
     * @param {number} index of the menu item to insert the expander before.
     */
    addExpander: function(index) {
        var chevron = $('li', this.jq).eq(1).clone();
        var expander = $('<li class=expander><a tabindex=0 class="ui-menuitem-link ui-corner-all" href="#"><span class=ui-menuitem-text>…</span></a></li>');
        var before = $('li', this.jq).eq(index * 2);
        expander.click(function(){
           chevron.remove();
           expander.remove();
           $('li', this.jq).show();
        });
        expander.insertBefore(before);
        chevron.insertBefore(before);
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();
    },

});