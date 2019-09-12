/**
 * PrimeFaces Spinner Widget
 */
PrimeFaces.widget.Spinner = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('a.ui-spinner-up');
        this.downButton = this.jq.children('a.ui-spinner-down');
        this.cfg.step = this.cfg.step || 1;
        if (this.cfg.thousandSeparator == undefined) {
          this.cfg.thousandSeparator = '';
        }
        if (!this.cfg.decimalSeparator) {
          this.cfg.decimalSeparator = '.';
        }
        this.cursorOffset = this.cfg.prefix ? this.cfg.prefix.length: 0;

        var inputValue = this.input.val();

        if(this.cfg.decimalPlaces > 0) {
            this.cfg.precision = this.cfg.decimalPlaces;
        }
        else if(!(typeof this.cfg.step === 'number' && this.cfg.step % 1 === 0)) {
            this.cfg.precision = this.cfg.step.toString().split(/[,]|[.]/)[1].length;
        }

        var maxlength = this.input.attr('maxlength');
        if(maxlength) {
            this.cfg.maxlength = parseInt(maxlength);
        }

        this.value = this.parseValue(inputValue);

        this.format();

        this.addARIA();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();

        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
    },

    bindEvents: function() {
        var $this = this;

        this.jq.children('.ui-spinner-button')
            .on('mouseover.spinner', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.spinner', function() {
                $(this).removeClass('ui-state-hover ui-state-active');

                if($this.timer) {
                    clearInterval($this.timer);
                }
            })
            .on('mouseup.spinner', function() {
                clearInterval($this.timer);
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
                $this.input.trigger('change');
            })
            .on('mousedown.spinner', function(e) {
                var element = $(this),
                dir = element.hasClass('ui-spinner-up') ? 1 : -1;

                element.removeClass('ui-state-hover').addClass('ui-state-active');

                if($this.input.is(':not(:focus)')) {
                    $this.input.focus();
                }

                $this.repeat(null, dir);

                //keep focused
                e.preventDefault();
        });

        this.input.on('keydown.spinner', function (e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    $this.spin(1);
                break;

                case keyCode.DOWN:
                    $this.spin(-1);
                break;

                case keyCode.ENTER:
                    $this.updateValue();
                    $this.format();
                break;

                default:
                    //do nothing
                break;
            }

            /* Github #1964 do not allow minus */
            if ($this.cfg.min >= 0 && event.key === "-") {
                e.preventDefault();
            }
        })
        .on('keyup.spinner', function (e) {
            $this.updateValue();

            var keyCode = $.ui.keyCode;

            /* Github #2636 */
            var checkForIE = (PrimeFaces.env.isIE(11) || PrimeFaces.env.isLtIE(11)) && (e.which === keyCode.ENTER);

            if(e.which === keyCode.UP||e.which === keyCode.DOWN||checkForIE) {
                $this.input.trigger('change');
                $this.format();
            }
        })
        .on('blur.spinner', function(e) {
            $this.format();
        })
        .on('mousewheel.spinner', function(event, delta) {
            if($this.input.is(':focus')) {
                if(delta > 0)
                    $this.spin(1);
                else
                    $this.spin(-1);
                
                $this.input.trigger('change');

                return false;
            }
        });
    },

    repeat: function(interval, dir) {
        var $this = this,
        i = interval||500;

        clearTimeout(this.timer);
        this.timer = setTimeout(function() {
            $this.repeat(40, dir);
        }, i);

        this.spin(dir);
    },

    spin: function(dir) {
        var step = this.cfg.step * dir,
        currentValue = this.value ? this.value : 0,
        newValue = this.parseValue(currentValue + step);

        if(this.cfg.maxlength !== undefined && newValue.toString().length > this.cfg.maxlength) {
            newValue = currentValue;
        }

        this.value = newValue;
        this.format();
        this.input.attr('aria-valuenow', this.getValue());
    },

    updateValue: function() {
        var value = this.input.val();

        if(this.cfg.prefix && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }  else {
            var ix = value.indexOf(this.cfg.suffix);
            if(this.cfg.suffix && ix > -1 && ix === (value.length - this.cfg.suffix.length)) {
                value = value.substring(0, value.length - this.cfg.suffix.length);
            }
        }

        value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.thousandSeparator), 'g'), '');
        value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.decimalSeparator), 'g'), '\.');
        this.value = this.parseValue(value);
    },

    parseValue: function(value) {
        var parsedValue;
        if(this.cfg.precision) {
            parsedValue = parseFloat(value);
        } else {
            parsedValue = parseInt(value);
        }
        if(isNaN(parsedValue)) {
            if($.trim(value) === '' && this.cfg.min !== undefined && this.cfg.required) {
                parsedValue = this.cfg.min;
            } else {
                parsedValue = null;
            }
        } else {
            var minimum = this.cfg.min;
            var maximum = this.cfg.max;

            if (this.cfg.rotate) {
                if(parsedValue < minimum) {
                    parsedValue = maximum;
                }
                if(parsedValue > maximum) {
                    parsedValue = minimum;
                }
            } else {
                if(parsedValue > maximum) {
                    parsedValue = maximum;
                }
                if(parsedValue < minimum) {
                    parsedValue = minimum;
                }
            }
        }
        return parsedValue;
    },

    format: function() {
        if(this.value !== null) {
            var value = this.getValue();
            var numAndFract = value.toString().split('.');
            value = numAndFract[0].replace(/(\d)(?=(?:\d{3})+\b)/g, '$1' + this.cfg.thousandSeparator);
            if (numAndFract.length === 2) {
              value += this.cfg.decimalSeparator + numAndFract[1];
            }
            if(this.cfg.prefix)
                value = this.cfg.prefix + value;

            if(this.cfg.suffix)
                value = value + this.cfg.suffix;

            this.input.val(value);
        }
    },


    addARIA: function() {
        this.input.attr('role', 'spinbutton');
        this.input.attr('aria-multiline', false);
        this.input.attr('aria-valuenow', this.getValue());

        if(this.cfg.min !== undefined)
            this.input.attr('aria-valuemin', this.cfg.min);

        if(this.cfg.max !== undefined)
            this.input.attr('aria-valuemax', this.cfg.max);

        if(this.input.prop('disabled'))
            this.input.attr('aria-disabled', true);

        if(this.input.prop('readonly'))
            this.input.attr('aria-readonly', true);
    },

    getValue: function() {
        if(this.cfg.precision) {
            return parseFloat(this.value).toFixed(this.cfg.precision);
        }
        else {
            return this.value;
        }
    },

    setValue: function(value) {
        this.value = value;
        this.format();
    }
});
