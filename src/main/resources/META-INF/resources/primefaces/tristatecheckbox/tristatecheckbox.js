
PrimeFaces.widget.TriStateCheckbox = PrimeFaces.widget.BaseWidget.extend({

    init:function (cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');
        this.fixedMod = function(number, mod){
            return ((number % mod) + mod) % mod;
        };

        var $this = this;

        //bind events if not disabled
        if (!this.disabled) {
            this.box.mouseover(function() {
                $this.box.addClass('ui-state-hover');
            }).mouseout(function() {
                $this.box.removeClass('ui-state-hover');
            }).click(function(event) {
                $this.toggle(1);
                if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
            });

            //toggle state on label click
            this.itemLabel.click(function(event) {
                $this.toggle(1);
                if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
            });

            //adding accesibility
            this.box.on('keydown', function(event) {
                switch (event.keyCode) {
                    case 38:
                        $this.toggle(1);
                        if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
                        break;
                    case 40:
                        $this.toggle(-1);
                        if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
                        break;
                    case 39:
                        $this.toggle(1);
                        if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
                        break;
                    case 37:
                        $this.toggle(-1);
                        if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
                        break;
                    case 32:
                        $this.toggle(1);
                        if (event.preventDefault) { event.preventDefault(); } else { event.returnValue = false; }
                        break;
                }
            });

            // client behaviors
            if (this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
            }
        }

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle:function (direction) {
        if (!this.disabled) {
            // default to switch to next state
            if (isNaN(direction)) {
                direction = 1;
            }

            var oldValue = parseInt(this.input.val());
            var newValue = this.fixedMod((oldValue + direction), 3);
            this.input.val(newValue);

            // remove / add def. icon and active classes
            if (newValue == 0) {
                this.box.removeClass('ui-state-active');
            } else {
                this.box.addClass('ui-state-active');
            }

            // remove old icon and add the new one
            var iconsClasses = this.box.data('iconstates');
            this.icon.removeClass(iconsClasses[oldValue]).addClass(iconsClasses[newValue]);

            // change title to the new one
            var iconTitles = this.box.data('titlestates');
            if (iconTitles != null && iconTitles.titles != null && iconTitles.titles.length > 0) {
                this.box.attr('title', iconTitles.titles[newValue]);
            }

            // fire change event
            this.input.change();
        }
    }
});

