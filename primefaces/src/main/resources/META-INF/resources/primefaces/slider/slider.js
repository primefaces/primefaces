/**
 * __PrimeFaces Slider Widget__
 * 
 * 
 * Slider is used to provide input with various customization options like orientation, display modes and skinning.
 *
 * @typedef PrimeFaces.widget.Slider.SliderCallback
 * A callback function that is invoked when a slider handle is moved or starts or ends moving.
 * @this {PrimeFaces.widget.Slider} PrimeFaces.widget.Slider.SliderCallback
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Slider.SliderCallback.event The event that triggered the slider event, as
 * given by jQuery.
 * @param {JQueryUI.SliderUIParams} PrimeFaces.widget.Slider.SliderCallback.ui Details about the slider, as given by
 * the jQueryUI slider widget.
 * 
 * @prop {boolean} decimalStep `true` if the  {@link SliderCfg.step} has a fractional part, or `false` it is is an
 * integer.
 * @prop {JQuery} input The DOM elements for the hidden input fields storing the value of each slider handle.
 * @prop {JQuery} output The DOM element displaying the current value of the slider.
 *  
 * @interface {PrimeFaces.widget.SliderCfg} cfg The configuration for the {@link  Slider| Slider widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryUI.SliderOptions} cfg
 * 
 * @prop {string} cfg.display ID of the component to display the slider value.
 * @prop {string} cfg.displayTemplate String template to use when updating the display. Valid placeholders are
 * `{value}`, `{min}` and `{max}`.
 * @prop {string} cfg.input IDs of the hidden {@link Slider.input} fields storing the values of each slider handle,
 * separated by a comma.
 * @prop {PrimeFaces.widget.Slider.SliderCallback} cfg.onSlide Client side callback that is invoked when a slider handle
 * is moved.
 * @prop {PrimeFaces.widget.Slider.SliderCallback} cfg.onSlideEnd Client side callback that is invoked when a slider
 * handle stops moving.
 * @prop {PrimeFaces.widget.Slider.SliderCallback} cfg.onSlideStart Client side callback that is invoked when a slider
 * handle starts moving.
 */
PrimeFaces.widget.Slider = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.displayTemplate = this.cfg.displayTemplate||(this.cfg.range === true ? '{min} - {max}' : '{value}');

        if(this.cfg.range === true) {
            var inputIds = this.cfg.input.split(',');
            this.input = $(PrimeFaces.escapeClientId(inputIds[0]) + ',' + PrimeFaces.escapeClientId(inputIds[1]));
        }
        else {
            this.input = $(PrimeFaces.escapeClientId(this.cfg.input));
        }

        if(this.cfg.display) {
            this.output = $(PrimeFaces.escapeClientId(this.cfg.display));
        }

        this.jq.slider(this.cfg);

        this.decimalStep = !(this.cfg.step % 1 === 0);

        this.bindEvents();

        if (PrimeFaces.env.isTouchable(this.cfg)) {
            this.bindTouchEvents();
        }
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.jq.on('slide', function(event, ui) {
            $this.onSlide(event, ui);
        });

        if(this.cfg.onSlideStart) {
            this.jq.on('slidestart', function(event, ui) {
                $this.cfg.onSlideStart.call(this, event, ui);
            });
        }

        this.jq.on('slidestop', function(event, ui) {
            $this.onSlideEnd(event, ui);
        });

        if (this.input.parent().hasClass('ui-inputnumber')) {
            this.input.parent().find('input:hidden').off('change.slider').on('change.slider', function () {
                $this.setValue($(this).val());
            });
        }
        else {
            this.input.on('keydown.slider', function (e) {
                var keyCode = $.ui.keyCode,
                key = e.which;

                switch(key) {
                    case keyCode.UP:
                    case keyCode.DOWN:
                    case keyCode.LEFT:
                    case keyCode.RIGHT:
                    case keyCode.BACKSPACE:
                    case keyCode.DELETE:
                    case keyCode.END:
                    case keyCode.HOME:
                    case keyCode.TAB:
                    break;

                    default:
                        if (key < 32) return true; // Control chars (charcode)
                        var character = e.key;
                        var current = $(this).val();

                         // don't allow duplicate decimal separators
                        var separatorRegex ='';
                        if($this.decimalStep) {
                            if (character === ',') {
                                if(current.indexOf(',') !== -1) {
                                    return false;
                                }
                                else {
                                    separatorRegex = ',';
                                }
                            } 
                            if (character === '.') {
                                if(current.indexOf('.') !== -1) {
                                    return false;
                                }
                                else {
                                    separatorRegex = '\\.';
                                }
                            } 
                        }

                        // #6319 only allow negative once and if min < 0
                        var negativeRegex = '';
                        if ($this.cfg.min < 0) {
                            if (character === '-' && current.indexOf('-') !== -1) return false;
                            negativeRegex = '-';
                        }
                        var regex = new RegExp('[^0-9' + separatorRegex + negativeRegex + ']', 'g');
                        return !character.match(regex);
                    break;
                }
            }).on('keyup.slider', function (e) {
                $this.setValue($this.input.val());
            });
        }
    },

    /**
     * Sets up all touch and mouse related event listeners that are required by this widget.
     * @private
     */
    bindTouchEvents: function() {
        var eventMapping = {
            touchstart: 'mousedown',
            touchmove: 'mousemove',
            touchend: 'mouseup'
        };

        this.jq.children('.ui-slider-handle').on('touchstart touchmove touchend', function(e) {
            var touch = e.originalEvent.changedTouches[0];
            var targetEvent = document.createEvent('MouseEvent');

            targetEvent.initMouseEvent(
                    eventMapping[e.originalEvent.type],
                    true, // canBubble
                    true, // cancelable
                    window, // view
                    1, // detail
                    touch.screenX,
                    touch.screenY,
                    touch.clientX,
                    touch.clientY,
                    false, // ctrlKey
                    false, // altKey
                    false, // shiftKey
                    false, // metaKey
                    0, // button
                    null // relatedTarget
                );

            touch.target.dispatchEvent(targetEvent);
            e.preventDefault();
        });
    },

    /**
     * Callback that is invoked when the user moves a slider handle.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that triggered the slider handle to move.
     * @param {JQueryUI.SliderUIParams} ui Details about the slider.
     */
    onSlide: function(event, ui) {
        if(this.cfg.onSlide) {
            this.cfg.onSlide.call(this, event, ui);
        }

        if(this.cfg.range === true) {
            this.setInputValue(this.input.eq(0), ui.values[0]);
            this.setInputValue(this.input.eq(1), ui.values[1]);

            if(this.output) {
                this.output.text(this.cfg.displayTemplate.replace('{min}', ui.values[0]).replace('{max}', ui.values[1]));
            }
        }
        else {
            this.setInputValue(this.input, ui.value);

            if(this.output) {
                this.output.text(this.cfg.displayTemplate.replace('{value}', ui.value));
            }
        }
    },

    /**
     * Stores the given slider handle value in the given hidden input field.
     * @private
     * @param {JQuery} input A hidden input field that should store the value.
     * @param {number} inputValue A value of a slider handle to store.
     */
    setInputValue: function(input, inputValue) {
        if (input.parent().hasClass('ui-inputnumber')) {
            var inputNumberId = input.closest('.ui-inputnumber').attr('id');
            var inputNumberWidget = PrimeFaces.getWidgetById(inputNumberId);
            inputNumberWidget.autonumeric.set(inputValue);
        }
        else if (input.hasClass('ui-spinner-input')) {
            var spinnerId = input.closest('.ui-spinner').attr('id');
            var spinnerWidget = PrimeFaces.getWidgetById(spinnerId);
            spinnerWidget.setValue(inputValue);
        }
        else {
            input.val(inputValue);
        }
    },

    /**
     * Triggers the change event on the hidden input.
     * @private
     * @param {JQuery} input The slider input element.
     */
    triggerOnchange: function(input) {
        if (input.parent().hasClass('ui-inputnumber')) {
            input.trigger('change');
        }
        else if (input.hasClass('ui-spinner-input')) {
            input.trigger('change');
        }
    },

    /**
     * Callback that is invoked when the user is done moving a slider handle.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that triggered the slide to end.
     * @param {JQueryUI.SliderUIParams} ui Details about the slider.
     */
    onSlideEnd: function(event, ui) {
        if(this.cfg.onSlideEnd) {
            this.cfg.onSlideEnd.call(this, event, ui);
        }

        if(this.cfg.range === true) {
            this.triggerOnchange(this.input.eq(0));
            this.triggerOnchange(this.input.eq(1));
        }
        else {
            this.triggerOnchange(this.input);
        }

        if(this.hasBehavior('slideEnd')) {
            var ext = {
                params: [
                    {name: this.id + '_slideValue', value: ui.value}
                ]
            };

            this.callBehavior('slideEnd', ext);
        }
    },

    /**
     * Determines the value of the slider, if there is only one handle. If there is more than one handle, determines the
     * value of the first handle.
     * @return {number} value The value of the first slider handler. 
     */
    getValue: function() {
        return this.jq.slider('value');
    },

    /**
     * Sets the value of the slider, if there is only one handle. If there is more than one handle, sets the
     * value of the first handle.
     * @param {number} value The value for the first slider handler. 
     */
    setValue: function(value) {
        this.jq.slider('value', value);
    },

    /**
     * Finds the values of all slider handles.
     * @return {number[]} A list with the values of all handles.
     */
    getValues: function() {
        return this.jq.slider('values');
    },

    /**
     * Sets the values of all slider handlers.
     * @param {number[]} values The new values for the handles.
     */
    setValues: function(values) {
        this.jq.slider('values', values);
    },

    /**
     * Enables this slider widget so that the user can move the slider.
     */
    enable: function() {
        this.jq.slider('enable');
    },

    /**
     * Disables this slider widget so that the user cannot move the slider anymore.
     */
    disable: function() {
        this.jq.slider('disable');
    }
});