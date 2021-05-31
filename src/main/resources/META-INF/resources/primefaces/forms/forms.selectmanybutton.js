/**
 * __PrimeFaces SelecyManyButton Widget__
 * 
 * SelectManyButton is a multi select component using button UI.
 * 
 * @prop {JQuery} buttons The DOM elements for the selectable buttons.
 * @prop {JQuery} inputs The DOM elements for the hidden input fields of type checkbox storing which buttons are
 * selected.
 * 
 * @interface {PrimeFaces.widget.SelectManyButtonCfg} cfg The configuration for the {@link  SelectManyButton| SelectManyButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.SelectManyButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
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
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function(e) {
            var button = $(this),
            input = button.children(':checkbox');

            if(button.hasClass('ui-state-active'))
                button.addClass('ui-state-hover');
            else
                button.removeClass('ui-state-hover');
            
            input.trigger('focus').trigger('click');
        });

        /* Keyboard support */
        this.inputs.on('focus', function() {
            var input = $(this),
            button = input.parent();

            button.addClass('ui-state-focus');
        })
        .on('blur', function() {
            var input = $(this),
            button = input.parent();

            button.removeClass('ui-state-focus');
        })
        .on('change', function() {
            var input = $(this),
            button = input.parent();

            if(input.prop('checked'))
                button.addClass('ui-state-active');
            else
                button.removeClass('ui-state-active');
        })
        .on('click', function(e) {
            e.stopPropagation();
        });
    },

    /**
     * Selects the given button option.
     * @param {JQuery} button A button of this widget to select.
     */
    select: function(button) {
        button.children(':checkbox').prop('checked', true).trigger('change');
    },

    /**
     * Unselects the given button option.
     * @param {JQuery} button A button of this widget to unselect.
     */
    unselect: function(button) {
        button.children(':checkbox').prop('checked', false).trigger('change');
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
        this.disabled = false;
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
        this.disabled = true;
    }

});
