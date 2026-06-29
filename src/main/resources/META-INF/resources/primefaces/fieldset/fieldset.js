/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.legend = this.jq.children('.ui-fieldset-legend');

        var $this = this;

        if(this.cfg.toggleable) {
            this.content = this.jq.children('.ui-fieldset-content');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.on('click', function(e) {
                $this.toggle(e);
            })
            .on('mouseover', function() {
                $this.legend.toggleClass('ui-state-hover');
            })
            .on('mouseout', function() {
                $this.legend.toggleClass('ui-state-hover');
            })
            .on('mousedown', function() {
                $this.legend.toggleClass('ui-state-active');
            })
            .on('mouseup', function() {
                $this.legend.toggleClass('ui-state-active');
            })
            .on('focus', function() {
                $this.legend.toggleClass('ui-state-focus');
            })
            .on('blur', function() {
                $this.legend.toggleClass('ui-state-focus');
            })
            .on('keydown', function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER)) {
                    $this.toggle(e);
                    e.preventDefault();
                }
            });
        }
    },

    /**
     * Toggles the content
     */
    toggle: function(e) {
        this.updateToggleState(this.cfg.collapsed);

        var $this = this;

        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.callBehavior('toggle');
        });

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    /**
     * Updates the visual toggler state and saves state
     */
    updateToggleState: function(collapsed) {
        if(collapsed)
            this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        else
            this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

        this.cfg.collapsed = !collapsed;

        this.stateHolder.val(!collapsed);
    }

});