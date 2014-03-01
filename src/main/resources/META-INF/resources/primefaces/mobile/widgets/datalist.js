/**
 * PrimeFaces Mobile DataList Widget
 */
PrimeFaces.widget.DataList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.listview();
        this.items = this.jq.children('li');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.behaviors) {
            var $this = this;
            
            $.each(this.cfg.behaviors, function(eventName, fn) {
                $this.items.on(eventName, function() {
                    var ext = {
                        params: [{name: $this.id + '_item', value: $(this).index()}]
                    };
                            
                    fn.call($this, ext);
                });
            });
        }
    }
});