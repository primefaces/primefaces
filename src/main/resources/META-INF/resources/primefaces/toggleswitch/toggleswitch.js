 /**
 * PrimeFaces ToggleSwitch Widget
 */
PrimeFaces.widget.ToggleSwitch = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.slider = this.jq.children('.ui-toggleswitch-slider');
        this.input = $(this.jqId + '_input');

        if(!this.input.prop('disabled')) {
            this._bindEvents();
        }
    },

    _bindEvents: function() {
        var $this = this;

        this.jq.on('click.toggleSwitch', function(e) {
            $this.toggle();
            $this.input.trigger('focus');
        });

        this.input.on('focus.toggleSwitch', function(e) {
            $this.jq.addClass('ui-toggleswitch-focus');
        })
        .on('blur.toggleSwitch', function(e) {
            $this.jq.removeClass('ui-toggleswitch-focus');
        })
        .on('keydown.toggleSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup.toggleSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        });
    },

    toggle: function() {
        if(this.input.prop('checked'))
            this.uncheck();
        else
            this.check();
    },

    check: function() {
        this.input.prop('checked', true).trigger('change');
        this.jq.attr('aria-checked', true).addClass('ui-toggleswitch-checked');
    },

    uncheck: function() {
        this.input.prop('checked', false).trigger('change');
        this.jq.attr('aria-checked', false).removeClass('ui-toggleswitch-checked');
    }
});
