/**
 * PrimeFaces Spinner Widget
 */
PrimeFaces.widget.Spinner = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('a.ui-spinner-up');
        this.downButton = this.jq.children('a.ui-spinner-down');
        this.decimalSeparator = this.findDecimalSeparator();
        this.decimalCount = this.findDecimalCount();

        //init value from input
        this.initValue();

        //aria
        this.addARIA();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

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
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
            }).mousedown(function(e) {
                var element = $(this),
                dir = element.hasClass('ui-spinner-up') ? 1 : -1;

                element.removeClass('ui-state-hover').addClass('ui-state-active');
                
                if(_self.input.is(':not(:focus)')) {
                    _self.input.focus();
                }

                _self.repeat(null, dir);

                //keep focused
                e.preventDefault();
            });

        /**
        * Key restrictions
        * - Only allow integers by default
        * - Allow decimal separators in step mode
        * - Allow prefix and suffix if defined
        * - Enable support for arrow keys
        */
        this.input.keydown(function (e) {        
            var keyCode = $.ui.keyCode,
            number = (e.which >= 48&&e.which <= 57),
            decimalKey = (_self.decimalSeparator != null) && (e.which == 188||e.which == 190);
            
            switch(e.which) {            
                case keyCode.BACKSPACE:
                case keyCode.LEFT:
                case keyCode.RIGHT:
                case keyCode.TAB:
                case keyCode.DELETE:
                    //allow keys above
                break;

                case keyCode.UP:
                    _self.spin(_self.cfg.step);
                break;

                case keyCode.DOWN:
                    _self.spin(-1 * _self.cfg.step);
                break;

                default:
                    //block key if not a number and decimal key
                    if(!number && !decimalKey) {
                        e.preventDefault();
                    }
                break;
            }
        });

        
        this.input.keyup(function () { 
            //update value from manual user input
            _self.updateValue();
        })
        .blur(function () { 
            //format value onblur
            _self.format();
        })
        .focus(function () {
            //remove formatting
            _self.input.val(_self.value);
        });
        
        //mousewheel
        this.input.bind('mousewheel', function(event, delta) {
            if(_self.input.is(':focus')) {
                if(delta > 0)
                    _self.spin(_self.cfg.step);
                else
                    _self.spin(-1 * _self.cfg.step);
                
                return false;
            }
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

        this.input.val(newValue);
        this.value = newValue;
        this.input.attr('aria-valuenow', newValue);

        this.input.change();
    },
    
    /**
     * Parses value on keyup
     */
    updateValue: function() {
        var value = this.input.val();

        if(value == '') {
            if(this.cfg.min != undefined)
                this.value = this.cfg.min;
            else
                this.value = 0;
        }
        else {
            if(this.decimalSeparator)
                value = parseFloat(value);
            else
                value = parseInt(value);
            
            if(!isNaN(value)) {
                this.value = value;
            }
        }
    },
    
    /**
     * Parses value on initial load
     */
    initValue: function() {
        var value = this.input.val();

        if(value == '') {
            if(this.cfg.min != undefined)
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
                this.value = parseFloat(value);
            else
                this.value = parseInt(value);
        }
    },
     
    format: function() {
        var value = this.value;
        
        if(this.decimalSeparator) {
            //convert to string
            value = value + '';

            var decimalCount = this.findDecimalCount(),
            valueDecimalCount = null;

            if(value.indexOf(this.decimalSeparator) != -1) {
                valueDecimalCount = value.split(this.decimalSeparator)[1].length;
            } 
            else {
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
        
        this.input.val(value);
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