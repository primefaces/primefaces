/**
 * PrimeFaces SelectOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
        this.cfg.unselectable = this.cfg.unselectable === false ? false : true;

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

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
            var button = $(this),
            radio = button.children(':radio');

            if(radio.prop('checked')) {
                button.addClass('ui-state-active');
            }

            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
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

    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true);

        this.triggerChange();
    },

    unselect: function(button) {
        if(this.cfg.unselectable) {
            button.removeClass('ui-state-active ui-state-hover ui-state-focus').children(':radio').prop('checked', false).change();

            this.triggerChange();
        }
    },

    triggerChange: function() {
        if(this.cfg.change) {
            this.cfg.change.call(this);
        }

        if(this.hasBehavior('change')) {
            var changeBehavior = this.cfg.behaviors['change'];
            if(changeBehavior) {
                changeBehavior.call(this);
            }
        }
    }

});
