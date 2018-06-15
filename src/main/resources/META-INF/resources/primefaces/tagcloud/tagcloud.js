/**
 * PrimeFaces TagCloud Widget
 */
PrimeFaces.widget.TagCloud = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        var _self = this;

        this.jq.find('a').mouseover(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover');
        })
        .click(function(e) {
            var link = $(this);

            if(link.attr('href') === '#') {
                _self.fireSelectEvent(link);
                e.preventDefault();
            }
        });
    },

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