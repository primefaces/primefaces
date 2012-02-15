/**
 *  PrimeFaces Spinner Widget
 */
PrimeFaces.widget.Spinner = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = this.jq.children('.ui-spinner-input');
    this.upButton = this.jq.children('a.ui-spinner-up');
    this.downButton = this.jq.children('a.ui-spinner-down');
    this.decimalSeparator = this.findDecimalSeparator();
    this.decimalCount = this.findDecimalCount();
    var _self = this;
        
    //grab value from input
    this.refreshValue();
    
    //aria
    this.addARIA();

    if(this.input.prop('disabled')||this.input.prop('readonly')) {
        return;
    }

    //visuals
    this.jq.children('.ui-spinner-button')
        .mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover ui-state-active');
            
            if(_self.timer) {
                clearInterval(_self.timer);
            }
        }).mouseup(function() {
            clearInterval(_self.timer);
            $(this).removeClass('ui-state-active');
        }).mousedown(function() {
            var element = $(this),
            dir = element.hasClass('ui-spinner-up') ? 1 : -1;

            element.removeClass('ui-state-hover').addClass('ui-state-active');

            _self.repeat(null, dir);
        });

    //only allow numbers and decimal keys
    this.input.keypress(function (e) {
        var charCode = (e.which) ? e.which : e.keyCode,
        notNumber = charCode > 31 && (charCode < 48||charCode > 57),
        decimalKey = (_self.decimalSeparator != null) && (charCode == 44||charCode == 46);
         
       if(notNumber && !decimalKey) {
            return false;
        }
        else {
            return true;
        }
    });

    //refresh the value if user enters input manually
    this.input.keyup(function (e){
        _self.refreshValue();
    });

    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }

    PrimeFaces.skinInput(this.input);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Spinner, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Spinner.prototype.repeat = function(interval, dir) {
    var _self = this,
    i = interval || 500;

    clearTimeout(this.timer);
    this.timer = setTimeout(function() {
        _self.repeat(40, dir);
    }, i);

    this.spin(this.cfg.step * dir);
}

PrimeFaces.widget.Spinner.prototype.spin = function(step) {
    var newValue = this.value + step;

    if(this.cfg.min != undefined && newValue < this.cfg.min) {
        newValue = this.cfg.min;
    }

    if(this.cfg.max != undefined && newValue > this.cfg.max) {
        newValue = this.cfg.max;
    }

    this.input.val(this.format(newValue));
    this.value = newValue;
    this.input.attr('aria-valuenow', newValue);

    this.input.change();
}

PrimeFaces.widget.Spinner.prototype.refreshValue = function() {
    var value = this.input.val();
    
    if(value == '') {
        if(this.cfg.min)
            this.value = this.cfg.min;
        else
            this.value = 0;
    } 
    else {
        if(this.cfg.prefix)
            value = value.split(this.cfg.prefix)[1];

        if(this.cfg.suffix)
            value = value.split(this.cfg.suffix)[0];

        if(this.decimalSeparator)
            this.value =  parseFloat(value);
        else
            this.value = parseInt(value);
    }
}

PrimeFaces.widget.Spinner.prototype.findDecimalSeparator = function() {
    var step = this.cfg.step + '';

    if(step.indexOf('.') != -1) {
        return "."
    } else if(step.indexOf(',') != -1) {
        return ',';
    } else {
        return null;
    }
}

PrimeFaces.widget.Spinner.prototype.findDecimalCount = function() {
    var decimalSeparator = this.findDecimalSeparator(),
    step = this.cfg.step + '';

    if(decimalSeparator) {
        return step.split(decimalSeparator)[1].length;
    } else {
        return 0;
    }
}

PrimeFaces.widget.Spinner.prototype.format = function(value) {
    if(this.decimalSeparator) {
        value = value + '';

        var decimalCount = this.findDecimalCount(),
        valueDecimalCount = null;

        if(value.indexOf(this.decimalSeparator) != -1) {
            valueDecimalCount = value.split(this.decimalSeparator)[1].length;
        } else {
            valueDecimalCount = 0;
            value = value + this.decimalSeparator;
        }

        for(var i = valueDecimalCount ; i < decimalCount; i++) {
            value = value + '0';
        }
    }

    if(this.cfg.prefix)
        value = this.cfg.prefix + value;

    if(this.cfg.suffix)
        value = value + this.cfg.suffix;

    return value;
}

PrimeFaces.widget.Spinner.prototype.addARIA = function() {
    this.input.attr('role', 'spinner');
    this.input.attr('aria-multiline', false);
    this.input.attr('aria-valuenow', this.value);
    
    if(this.cfg.min != undefined) 
        this.input.attr('aria-valuemin', this.cfg.min);
    
    if(this.cfg.max != undefined) 
        this.input.attr('aria-valuemax', this.cfg.max);
    
    if(this.input.prop('disabled'))
        this.input.attr('aria-disabled', true);
    
    if(this.input.prop('readonly'))
        this.input.attr('aria-readonly', true);
}