/**
 * __PrimeFaces Password Widget__
 * 
 * Password component is an extended version of standard inputSecret component with theme integration and strength
 * indicator.
 * 
 * @prop {JQuery} panel The DOM element for the overlay panel with the hint regarding how strong the password is.
 * @prop {JQuery} meter The DOM element for the gauge giving visual feedback regarding how strong the password is.
 * @prop {JQuery} infoText The DOM element for the informational text regarding how strong the password is.
 * @prop {JQuery} icon The DOM element for mask/unmask icon
 * 
 * @interface {PrimeFaces.widget.PasswordCfg} cfg The configuration for the {@link  Password| Password widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.feedback Enables strength indicator.
 * @prop {string} cfg.promptLabel Label of the password prompt.
 * @prop {string} cfg.weakLabel Text of the hint when the password is judged to be weak.
 * @prop {string} cfg.goodLabel Text of the hint when the password is judged to be good.
 * @prop {string} cfg.strongLabel Text of the hint when the password is judged to be strong.
 * @prop {boolean} cfg.inline Displays feedback inline rather than using a popup.
 * @prop {string} cfg.showEvent Event displaying the feedback overlay. Default is 'focus'.
 * @prop {string} cfg.hideEvent Event hiding the feedback overlay. Default is 'blur'.
 * @prop {boolean} cfg.unmaskable Whether or not this password can be unmasked/remasked.
 */
PrimeFaces.widget.Password = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if (!this.jq.is(':disabled')) {
            if (this.cfg.feedback) {
                this.setupFeedback();
            }

            if (this.cfg.unmaskable) {
                this.setupUnmasking();
            }

            PrimeFaces.skinInput(this.jq);
        }
    },

    /**
     * Sets up the overlay panel informing the user about how good the password their typed is.
     * @private
     */
    setupFeedback: function() {
        var $this = this;

        //remove previous panel if any
        var oldPanel = $(this.jqId + '_panel');
        if(oldPanel.length == 1) {
            oldPanel.remove();
        }

        //config
        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.password' : 'focus.password';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.password' : 'blur.password';
        this.cfg.promptLabel = this.cfg.promptLabel||'Please enter a password';
        this.cfg.weakLabel = this.cfg.weakLabel||'Weak';
        this.cfg.goodLabel = this.cfg.goodLabel||'Medium';
        this.cfg.strongLabel = this.cfg.strongLabel||'Strong';

        var panelStyle = this.cfg.inline ? 'ui-password-panel-inline' : 'ui-password-panel-overlay';

        //create panel element
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-password-panel ui-widget ui-state-highlight ui-corner-all ui-helper-hidden ' + panelStyle + '">';
        panelMarkup += '<div class="ui-password-meter" style="background-position:0pt 0pt">&nbsp;</div>';
        panelMarkup += '<div class="ui-password-info">' + PrimeFaces.escapeHTML(this.cfg.promptLabel) + '</div>';
        panelMarkup += '</div>';

        this.panel = $(panelMarkup).insertAfter(this.jq);
        this.meter = this.panel.children('div.ui-password-meter');
        this.infoText = this.panel.children('div.ui-password-info');

        if (!this.cfg.inline) {
            this.panel.addClass('ui-shadow');
        }

        //events
        this.jq.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent + ' keyup.password')
        .on(this.cfg.showEvent, function() {
            $this.show();
        })
        .on(this.cfg.hideEvent, function() {
            $this.hide();
        })
        .on("keyup.password", function() {
            var value = $this.jq.val(),
            label = null,
            meterPos = null;

            if(value.length == 0) {
                label = $this.cfg.promptLabel;
                meterPos = '0px 0px';
            }
            else {
                var score = $this.testStrength($this.jq.val());

                if(score < 30) {
                    label = $this.cfg.weakLabel;
                    meterPos = '0px -10px';
                }
                else if(score >= 30 && score < 80) {
                    label = $this.cfg.goodLabel;
                    meterPos = '0px -20px';
                }
                else if(score >= 80) {
                    label = $this.cfg.strongLabel;
                    meterPos = '0px -30px';
                }
            }

            //update meter and info text
            $this.meter.css('background-position', meterPos);
            $this.infoText.text(label);
        });

        //overlay setting
        if (!this.cfg.inline) {
            this.panel.appendTo('body');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
        }
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        //Hide overlay on resize/scroll
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, function() {
            $this.hide();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.hide();
        });
    },

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents: function() {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    },

    /**
     * Sets up the ability to unmask and remask the password.
     * @private
     */
    setupUnmasking: function() {
        var $this = this;
        this.icon = $(PrimeFaces.escapeClientId(this.id + '_mask'));
        this.icon.off('click.password').on('click.password', function() {
            $this.toggleMask();
        });
    },
    
    /**
     * Toggle masking and unmasking the password.
     */
    toggleMask: function() {
        if(!this.cfg.unmaskable) {
            return;
        }

        if (this.jq.attr('type') === 'password') {
            this.jq.attr('type', 'text').parent().removeClass('ui-password-masked').addClass('ui-password-unmasked');
        } 
        else {
            this.jq.attr('type', 'password').parent().removeClass('ui-password-unmasked').addClass('ui-password-masked');
        } 
    },

    /**
     * Computes a numerical score that estimates how strong the given password is. The returned value can range from `0`
     * (very weak) to `128` (very strong). This test takes into account whether the password has got a certain minimal
     * length and whether it contains characters from certain character classes.
     * @param {string} password A password to check for its strength.
     * @return {number} A value between `0` and `128` that indicates how good the password is, with `0` indicating a
     * very weak password and `128` indicating a very strong password.
     */
    testStrength: function(password) {
        // return a number between 0 and 100.
        var score = 0;

        // must be at least 8 characters
        if (!password || password.length < 8)
            return score;

        // require 3 of the following 4 categories
        var variations = {
            digits : /\d/.test(password),
            lower : /[a-z]/.test(password),
            upper : /[A-Z]/.test(password),
            nonWords : /\W/.test(password)
        }

        variationCount = 0;
        for ( var check in variations) {
            variationCount += (variations[check] == true) ? 1 : 0;
        }
        score += variationCount * 28;

        return parseInt(score);
    },

    /**
     * Returns a normalized value between `0` and `1.5` that indicates how much bigger the first input x is compared
     * to the other input y. `0` means that x is much smaller than `y`, a value of `1.5` mean that `x` is much larger
     * than `y`.
     * @private
     * @param {number} x First input, must be a non-negative number.
     * @param {number} y  Second input, must be a positive number
     * @return {number} A value between `0` and `1.5` that indicates how big `x` is compared to `y`.
     */
    normalize: function(x, y) {
        var diff = x - y;

        if (diff <= 0) {
            return x / y;
        }
        else {
            return 1 + 0.5 * (x / (x + y/4));
        }
    },

    /**
     * Align the panel with the password strength indicator so that it is next to the password input field.
     * @private
     */
    align: function() {
        this.panel.css({
            left:'',
            top:'',
            'min-width': this.jq.outerWidth(),
            'transform-origin': 'center top'
        })
        .position({
            my: 'left top'
            ,at: 'left bottom'
            ,of: this.jq
            ,collision: 'flipfit'
            ,using: function(pos, directions) {
                $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
            }
        });
    },

    /**
     * Brings up the panel with the password strength indicator.
     */
    show: function() {
        if (!this.cfg.inline) {
            var $this = this;
    
            if (this.transition) {
                this.transition.show({
                    onEnter: function() {
                        $this.panel.css('z-index', PrimeFaces.nextZindex());
                        $this.align();
                    },
                    onEntered: function() {
                        $this.bindPanelEvents();
                    }
                });
            }
        }
        else {
            this.panel.css({ width: this.jq.outerWidth()});
            this.panel.slideDown();
        }
    },

    /**
     * Hides the panel with the password strength indicator.
     */
    hide: function() {
        if (this.cfg.inline) {
            this.panel.slideUp();
        }
        else if (this.transition) {
            var $this = this;
    
            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                }
            });
        }
    }
    
});
