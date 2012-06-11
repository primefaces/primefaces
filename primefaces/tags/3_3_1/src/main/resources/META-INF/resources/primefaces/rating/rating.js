/**
 * PrimeFaces Rating Widget
 */
PrimeFaces.widget.Rating = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.jqInput = $(this.jqId + '_input');
        this.value = this.getValue();
        this.stars = this.jq.children('.ui-rating-star');
        this.cancel = this.jq.children('.ui-rating-cancel');
        
        if(!this.cfg.disabled && !this.cfg.readonly) {
            this.bindEvents();
        }
        
        if(this.cfg.readonly) {
            this.jq.children().css('cursor', 'default');
        }
    },
    
    bindEvents: function() {
        var _self = this;
        
        this.stars.click(function() {
            var value = _self.stars.index(this) + 1;   //index starts from zero
            
            _self.setValue(value);
        });
        
        this.cancel.hover(function() {
            $(this).toggleClass('ui-rating-cancel-hover');
        })
        .click(function() {
            _self.reset();
        });
    },
    
    unbindEvents: function() {        
        this.stars.unbind('click');
        
        this.cancel.unbind('hover click');
    },
    
    getValue: function() {
        var inputVal = this.jqInput.val();
        
        return inputVal == '' ? null : parseInt(inputVal);
    },
    
    setValue: function(value) {
        //set hidden value
        this.jqInput.val(value);
        
        //update visuals
        this.stars.removeClass('ui-rating-star-on');
        for(var i = 0; i < value; i++) {
            this.stars.eq(i).addClass('ui-rating-star-on');
        }
        
        //invoke callback
        if(this.cfg.onRate) {
            this.cfg.onRate.call(this, value);
        }

        //invoke ajax rate behavior
        if(this.cfg.behaviors) {
            var rateBehavior = this.cfg.behaviors['rate'];
            if(rateBehavior) {
                rateBehavior.call(this);
            }
        }
    },
    
    enable: function() {
        this.cfg.disabled = false;
        
        this.bindEvents();
        
        this.jq.removeClass('ui-state-disabled');
    },
    
    disable: function() {
        this.cfg.disabled = true;
        
        this.unbindEvents();
        
        this.jq.addClass('ui-state-disabled');
    },
    
    reset: function() {
        this.jqInput.val('');
        
        this.stars.filter('.ui-rating-star-on').removeClass('ui-rating-star-on');
        
        //invoke ajax cancel behavior
        if(this.cfg.behaviors) {
            var cancelBehavior = this.cfg.behaviors['cancel'];
            if(cancelBehavior) {
                cancelBehavior.call(this);
            }
        }
    }
});