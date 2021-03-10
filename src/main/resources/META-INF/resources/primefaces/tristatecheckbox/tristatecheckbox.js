/**
 * __PrimeFaces TriStateCheckbox Widget__
 * 
 * TriStateCheckbox adds a new state to a checkbox value.
 * 
 * @typedef PrimeFaces.widget.TriStateCheckbox.FixedMod A modulo operation with the result being in the range `[0,mod)`.
 * @param {number} PrimeFaces.widget.TriStateCheckbox.FixedMod.number The divisor for the modulo operation.
 * @param {number} PrimeFaces.widget.TriStateCheckbox.FixedMod.mod The dividend for the modulo operation.
 * @return {number} PrimeFaces.widget.TriStateCheckbox.FixedMod The result of the module operation, in the range
 * `[0,mod)]`.
 * 
 * @prop {JQuery} box The DOM element for the container with the label and the icon.
 * @prop {boolean} disabled Whether this widget is initially disabled.
 * @prop {PrimeFaces.widget.TriStateCheckbox.FixedMod} fixedMod A modulo operation with the result being in the range
 * `[0,mod)`.
 * @prop {JQuery} icon The DOM element for the icon showing the current state of this checkbox widget.
 * @prop {JQuery} input The DOM element for the hidden input field storing the value of this widget.
 * @prop {JQuery} itemLabel The DOM element for the label of the checkbox.
 * 
 * @interface {PrimeFaces.widget.TriStateCheckboxCfg} cfg The configuration for the {@link  TriStateCheckbox| TriStateCheckbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.TriStateCheckbox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
            this.box.on('mouseenter.triStateCheckbox', function () {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseleave.triStateCheckbox', function () {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.triStateCheckbox', function () {
                $this.toggle(1);
                $this.input.trigger('focus');
            });
            
            this.input.on('focus.triStateCheckbox', function () {
                $this.box.addClass('ui-state-focus');
            })
            .on('blur.triStateCheckbox', function () {
                $this.box.removeClass('ui-state-focus');
            })
            .on('keydown.triStateCheckbox', function (e) {
                var keyCode = $.ui.keyCode;

                switch (e.which) {
                    case keyCode.SPACE:
                    case keyCode.UP:
                    case keyCode.RIGHT:
                    case keyCode.LEFT:
                    case keyCode.DOWN:
                        e.preventDefault();
                        break;
                }
            })            
            .on('keyup.triStateCheckbox', function (e) {
                var keyCode = $.ui.keyCode;

                switch(e.which) {
                    case keyCode.SPACE:
                    case keyCode.UP:
                    case keyCode.RIGHT:
                        $this.toggle(1);
                        break;
                    case keyCode.LEFT:
                    case keyCode.DOWN:
                        $this.toggle(-1);
                        break;
                }
            });

            //toggle state on label click
            this.itemLabel.on('click.triStateCheckbox', function() {
                $this.toggle(1);
                $this.input.trigger('focus');
            });

            // client behaviors
            if (this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
            }
        }

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Toggles this tri state checkbox in the given direction. Moves between unchecked, half-checked, and fully-checked.
     * @param {-1 | 1} direction `-1` to move backwards through the states, `+1` to move forward through the states 
     */
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
            this.input.trigger('change');
        }
    }
});

