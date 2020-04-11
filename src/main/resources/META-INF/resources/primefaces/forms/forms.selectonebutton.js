/**
 * __PrimeFaces SelectOneButton Widget__
 * 
 * SelectOneButton is an input component to do a single select.
 * 
 * @typedef PrimeFaces.widget.SelectOneButton.ChangeCallback Callback that is invoked when the value of this widget has
 * changed. See also {@link SelectOneButtonCfg.change}.
 * @this {PrimeFaces.widget.SelectOneButton} PrimeFaces.widget.SelectOneButton.ChangeCallback 
 * 
 * @prop {JQuery} buttons The DOM element for the button optios the user can select.
 * @prop {JQuery} inputs The DOM element for the hidden input fields storing the value of this widget. 
 * 
 * @interface {PrimeFaces.widget.SelectOneButtonCfg} cfg The configuration for the {@link  SelectOneButton| SelectOneButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.unselectable Whether selection can be cleared.
 * @prop {PrimeFaces.widget.SelectOneButton.ChangeCallback} cfg.change Callback that is invoked when the value of this
 * widget has changed.
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
        this.cfg.unselectable = this.cfg.unselectable === false ? false : true;

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Sets up all event listenters required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.buttons.on('mouseover', function() {
            var button = $(this);
            button.addClass('ui-state-hover');
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var button = $(this),
            radio = button.children(':radio');

            if(button.hasClass('ui-state-active') || radio.prop('checked')) {
                $this.unselect(button);
            }
            else {
                $this.select(button);
            }
        });

        /* For keyboard accessibility */
        this.buttons.on('focus.selectOneButton', function(){
            var button = $(this);
            button.addClass('ui-state-focus');
        })
        .on('blur.selectOneButton', function(){
            var button = $(this);
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                var button = $(this),
                radio = button.children(':radio');

                if(radio.prop('checked')) {
                    $this.unselect(button);
                }
                else {
                    $this.select(button);
                }
                e.preventDefault();
            }
        });
    },

    /**
     * Selects the given button option. If another button option is selected already, it will be unselected.
     * @param {JQuery} button A button of this widget to select.
     */
    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true);

        this.triggerChange();
    },

    /**
     * Unselects the given button option.
     * @param {JQuery} button A button of this widget to unselect.
     */
    unselect: function(button) {
        if(this.cfg.unselectable) {
            button.removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false).change();

            this.triggerChange();
        }
    },

    /**
     * Trigger the change behavior when the value of this widget has changed.
     * @private
     */
    triggerChange: function() {
        if(this.cfg.change) {
            this.cfg.change.call(this);
        }

        this.callBehavior('change');
    },

    /**
     * Disables this input components so that the user cannot select an option anymore.
     */
    disable: function() {
        this.buttons.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    /**
     * Enables this input components so that the user can select an option.
     */
    enable: function() {
        this.buttons.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});
