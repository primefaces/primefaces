/**
 * __PrimeFaces TagCloud Widget__
 * 
 * TagCloud displays a collection of tag with different strengths.
 * 
 * @interface {PrimeFaces.widget.TagCloudCfg} cfg The configuration for the {@link  TagCloud| TagCloud widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.TagCloud = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        var _self = this;

        this.jq.find('a').on("mouseover", function() {
            $(this).addClass('ui-state-hover');
        })
        .on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        })
        .on("click", function(e) {
            var link = $(this);

            if(link.attr('href') === '#') {
                _self.fireSelectEvent(link);
                e.preventDefault();
            }
        });
    },

    /**
     * Callback for when a tag was clicked. Invokes the appropriate behavior.
     * @private
     * @param {JQuery} link The link element that was clicked. 
     */
    fireSelectEvent: function(link) {
        if(this.hasBehavior('select')) {
            var ext = {
                params: [
                    {name: this.id + '_itemIndex', value: link.parent().index()}
                ]
            };

            this.callBehavior('select', ext);
        }
    }

});