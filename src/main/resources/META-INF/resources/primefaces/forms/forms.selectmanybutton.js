/**
 * PrimeFaces SelecyManyButton Widget
 */
PrimeFaces.widget.SelectManyButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

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

    select: function(button) {
        button.children(':checkbox').prop('checked', true).change();
    },

    unselect: function(button) {
        button.children(':checkbox').prop('checked', false).change();
    }

});
