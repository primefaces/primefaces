/**
 * PrimeFaces Rating Widget
 */
PrimeFaces.widget.Rating = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jqInput = $(this.jqId + ' input');
        this.cfg.formId = this.jq.parents('form:first').attr('id');
        this.value = this.getValue();
        var _self = this;

        this.cfg.callback = function(value) {
            if(_self.value == value)
                return;

            _self.value = value;

            if(_self.cfg.onRate) {
                _self.cfg.onRate.call(_self, value);
            }

            if(_self.cfg.behaviors) {
                var rateBehavior = _self.cfg.behaviors['rate'];
                if(rateBehavior) {
                    rateBehavior.call(_self);
                }
            }
        };

    },
    
    getValue: function() {
        return $(this.jq).find('input:radio:checked').val();
    },
    
    setValue: function(value) {
        this.jqInput.rating('select', value);
    },
    
    enable: function() {
        this.jqInput.rating('enable');
    },
    
    disable: function() {
        this.jqInput.rating('disable');
    }

    
});