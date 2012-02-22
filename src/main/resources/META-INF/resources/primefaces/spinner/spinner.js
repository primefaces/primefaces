/**
 * PrimeFaces Spinner Widget
 */
PrimeFaces.widget.XXX = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('a.ui-spinner-up');
        this.downButton = this.jq.children('a.ui-spinner-down');
        this.decimalSeparator = this.findDecimalSeparator();
        this.decimalCount = this.findDecimalCount();

        //grab value from input
        this.refreshValue();

        //aria
        this.addARIA();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();

        PrimeFaces.skinInput(this.input);
    },
    
    bindEvents: function() {
        var _self = this;

        //visuals for spinner buttons
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

        /**
        * Key restrictions
        * - Only allow integers by default
        * - Allow decimal separators in step mode
        * - Allow prefix and suffix if defined
        * - Enable support for arrow keys
        * 
        * Note: e.keyCode is used for arrow key detection, rest uses e.which
        */
        this.input.keypress(function (e) {
            var keyCode = $.ui.keyCode,
            character = String.fromCharCode(e.which),
            number = (e.which >= 48&&e.which <= 57),
            decimalKey = (_self.decimalSeparator != null) && (e.which == 44||e.which == 46),
            boundary = (character == _self.cfg.prefix||character == _self.cfg.suffix);

            switch(e.keyCode) {            
                case keyCode.BACKSPACE:
                case keyCode.LEFT:
                case keyCode.RIGHT:
                    //allow backspace, left and right arrow keys
                break;

                case keyCode.UP:
                    _self.spin(_self.cfg.step);
                break;

                case keyCode.DOWN:
                    _self.spin(-1 * _self.cfg.step);
                break;

                default:
                    if(!number && !decimalKey && !boundary) {
                        e.preventDefault();
                    }
                break;
            }
        });

        //refresh the value if user enters input manually
        this.input.keyup(function (e) {      
            _self.refreshValue();
        });

        //client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }
    },
    
    repeat: function(interval, dir) {
        var _self = this,
        i = interval || 500;

        clearTimeout(this.timer);
        this.timer = setTimeout(function() {
            _self.repeat(40, dir);
        }, i);

        this.spin(this.cfg.step * dir);
    },
    
    spin: function(step) {
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
    },
    
    refreshValue: function() {
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
    },
    
    findDecimalSeparator: function() {
        var step = this.cfg.step + '';

        if(step.indexOf('.') != -1) {
            return "."
        } else if(step.indexOf(',') != -1) {
            return ',';
        } else {
            return null;
        }
    },
    
    findDecimalCount: function() {
        var decimalSeparator = this.findDecimalSeparator(),
        step = this.cfg.step + '';

        if(decimalSeparator) {
            return step.split(decimalSeparator)[1].length;
        } else {
            return 0;
        }
    },
    
    format: function(value) {
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
    },
    
    addARIA: function() {
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
    
});