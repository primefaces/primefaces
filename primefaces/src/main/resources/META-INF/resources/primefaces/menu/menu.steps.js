/**
 * __PrimeFaces Steps Widget__
 * 
 * Steps is a menu component that displays steps of a workflow.
 *
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Steps = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init : function(cfg) {
        this._super(cfg);

        this.items = this.jq.find('.ui-steps-item');
        this.enabledItems = this.items.filter(".ui-state-default:not(.ui-state-disabled)");

        this.bindEvents();
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        this.enabledItems.on('mouseover', function(e) {
            var element = $(this);
            element.addClass('ui-state-highlight');
        })
        .on('mouseout', function(e) {
            var element = $(this);
            element.removeClass('ui-state-highlight');
        });
    },
});
