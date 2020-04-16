/**
 * PrimeFaces Draggable Widget
 */
PrimeFaces.widget.Draggable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.cfg.cancel = this.cfg.cancel || "input,textarea,button,select,option";

        if(this.cfg.appendTo) {
            this.cfg.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo);
        }
        
        var $this = this;

        this.cfg.start = function(event, ui) {
            if($this.cfg.onStart) {
                $this.cfg.onStart.call($this, event, ui);
            }
        };
        
        this.cfg.stop = function(event, ui) {
            if($this.cfg.onStop) {
                $this.cfg.onStop.call($this, event, ui);
            }
        };
        
        this.jqTarget.draggable(this.cfg);
    }
    
});

/**
 * PrimeFaces Droppable Widget
 */
PrimeFaces.widget.Droppable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));

        this.bindDropListener();

        this.jqTarget.droppable(this.cfg);
    },
    
    bindDropListener: function() {
        var _self = this;

        this.cfg.drop = function(event, ui) {
            if(_self.cfg.onDrop) {
                _self.cfg.onDrop.call(_self, event, ui);
            }
            if(_self.cfg.behaviors) {
                var dropBehavior = _self.cfg.behaviors['drop'];

                if(dropBehavior) {
                    var ext = {
                        params: [
                            {name: _self.id + '_dragId', value: ui.draggable.attr('id')},
                            {name: _self.id + '_dropId', value: _self.cfg.target}
                        ]
                    };

                    dropBehavior.call(_self, ext);
                }
            }
        };
    }
    
});