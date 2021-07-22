/**
 * __PrimeFaces InputSwitch Widget__
 * 
 * InputSwitch is used to select a boolean value.
 * 
 * @prop {JQuery} handle The DOM element for the handle that lets the user change the state of this input switch by 
 * dragging it.
 * @prop {JQuery} input The DOM element for the hidden input field storing the current value of this widget.
 * @prop {number} offset Offset to the left of all switch parts, depends on the handler width. 
 * @prop {JQuery} offContainer The DOM element for the container with the elements for displaying the off state.
 * @prop {JQuery} offLabel The DOM element for the label with the text for when this input switch is turned off.
 * @prop {JQuery} onContainer The DOM element for the container with the elements for displaying the on state.
 * @prop {JQuery} onLabel The DOM element for the label with the text for when this input switch is turned on.
 * 
 * @interface {PrimeFaces.widget.InputSwitchCfg} cfg The configuration for the {@link  InputSwitch| InputSwitch widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 */
PrimeFaces.widget.InputSwitch = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.onContainer = this.jq.children('.ui-inputswitch-on');
        this.onLabel = this.onContainer.children('span');
        this.offContainer = this.jq.children('.ui-inputswitch-off');
        this.offLabel = this.offContainer.children('span');
        this.handle = this.jq.children('.ui-inputswitch-handle');
        this.input = $(this.jqId + '_input');

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        var onContainerWidth = this.onContainer.width(),
        offContainerWidth = this.offContainer.width(),
        spanPadding	= this.offLabel.innerWidth() - this.offLabel.width(),
        handleMargins = this.handle.outerWidth() - this.handle.innerWidth();

        var containerWidth = (onContainerWidth > offContainerWidth) ? onContainerWidth : offContainerWidth,
        handleWidth = containerWidth;

        this.handle.css({'width':handleWidth + 'px'});
        handleWidth = this.handle.width();

        containerWidth = containerWidth + handleWidth + 6;

        var labelWidth = containerWidth - handleWidth - spanPadding - handleMargins;

        this.jq.css({'width': containerWidth + 'px' });
        this.onLabel.width(labelWidth);
        this.offLabel.width(labelWidth);

        //position
        this.offContainer.css({ width: (this.jq.width() - 5) + 'px' });
        this.offset = this.jq.width() - this.handle.outerWidth();

        if(this.input.prop('checked')) {
            this.handle.css({ 'left': this.offset + 'px'});
            this.onContainer.css({ 'width': this.offset + 'px'});
            this.offLabel.css({ 'margin-right': -this.offset + 'px'});
        }
        else {
            this.onContainer.css({ 'width': '0px' });
            this.onLabel.css({'margin-left': -this.offset + 'px'});
        }

        if(!this.input.prop('disabled')) {
            this._bindEvents();
        }
    },

    /**
     * Sets up all event listenters required by this widget.
     * @private
     */
    _bindEvents: function() {
        var $this = this;

        this.jq.on('click.inputSwitch', function(e) {
            $this.toggle();
            $this.input.trigger('focus');
        });

        this.input.on('focus.inputSwitch', function(e) {
            $this.handle.addClass('ui-state-focus');
        })
        .on('blur.inputSwitch', function(e) {
            $this.handle.removeClass('ui-state-focus');
        })
        .on('keydown.inputSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup.inputSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        })
        .on('change.inputSwitch', function(e) {
            if($this.input.prop('checked'))
                $this._checkUI();
            else
                $this._uncheckUI();
        });


        this.handle.on("mouseover", function() {
            if(!$this.jq.hasClass('ui-state-disabled')) {
                $(this).addClass('ui-state-hover');
            }
        }).on("mouseout", function() {
            if(!$this.jq.hasClass('ui-state-disabled')) {
                $(this).removeClass('ui-state-hover');
            }
        });
    },

    /**
     * Toggles this input switch, i.e. switch it from on to off or from off to on.
     */
    toggle: function() {
        if(this.input.prop('checked'))
            this.uncheck();
        else
            this.check();
    },

    /**
     * Turns this input switch on, if not already switched on.
     */
    check: function() {
        this.input.prop('checked', true).trigger('change');
        this.jq.addClass('ui-inputswitch-checked');
    },

    /**
     * Turns this input switch off, if not already switched off.
     */
    uncheck: function() {
        this.input.prop('checked', false).trigger('change');
        this.jq.removeClass('ui-inputswitch-checked');
    },

    /**
     * Performs the UI updates for when this input switch is turned on, such as animating the transition.
     * @private
     */
    _checkUI: function() {
        this.onContainer.animate({width:this.offset}, 200);
        this.onLabel.animate({marginLeft:0}, 200);
        this.offLabel.animate({marginRight:-this.offset}, 200);
        this.handle.animate({left:this.offset}, 200);
    },

    /**
     * Performs the UI updates for when this input switch is turned off, such as animating the transition.
     * @private
     */
    _uncheckUI: function() {
        this.onContainer.animate({width:0}, 200);
        this.onLabel.animate({marginLeft:-this.offset}, 200);
        this.offLabel.animate({marginRight:0}, 200);
        this.handle.animate({left:0}, 200);
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }

});