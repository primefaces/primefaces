/**
 * __PrimeFaces Rating Widget__
 *
 * Rating component features a star based rating system.
 *
 * @prop {JQuery} cancel The DOM element for the cancel button.
 * @prop {JQuery} input The DOM element for the hidden input field storing the value of this widget.
 * @prop {JQuery} stars The DOM elements for the clickable stars.
 * @prop {number} value The current value, i.e. the number of selected stars.
 * @prop {string} tabindex The tabindex initially set.
 *
 * @typedef PrimeFaces.widget.Rating.OnRateCallback Callback that is invoked when the user gives a rating. See also
 * {@link RatingCfg.onRate}.
 * @this {PrimeFaces.widget.Rating} PrimeFaces.widget.Rating.OnRateCallback
 * @param {number} PrimeFaces.widget.Rating.OnRateCallback.currentNumberOfStars The number of rated stars.
 *
 * @interface {PrimeFaces.widget.RatingCfg} cfg The configuration for the {@link  Rating| Rating widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.disabled Whether this widget is initially disabled.
 * @prop {PrimeFaces.widget.Rating.OnRateCallback} cfg.onRate Callback that is invoked when the user gives a rating.
 * @prop {boolean} cfg.readonly Whether this widget is in read-only mode.
 */
PrimeFaces.widget.Rating = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.input = $(this.jqId + '_input');
        this.value = this.getValue();
        this.stars = this.jq.children('.ui-rating-star');
        this.cancel = this.jq.children('.ui-rating-cancel');
        this.tabindex = this.jq.attr('tabindex');

        if(!this.cfg.disabled && !this.cfg.readonly) {
            this.bindEvents();
        }
        else {
            this.jq.attr('tabindex', -1);
        }

        if(this.cfg.readonly) {
            this.jq.children().css('cursor', 'default');
        }
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        this.jq.attr('tabindex', this.tabindex);
        var $this = this;

        this.input.on("keydown.rating", function(e) {
            var value = $this.getValue() || 0;
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if ((key === keyCode.LEFT || key === keyCode.DOWN) && value > 0) {
                $this.setValue(--value);
            }
            else if ((key === keyCode.RIGHT || key === keyCode.UP) && $this.stars.length !== value) {
                $this.setValue(++value);
            }
            
            $this.focus($this.getFocusableElement());
        }).on("focus.rating", function(){
            $this.focus($this.getFocusableElement());
        }).on("blur.rating", function(){
            $this.jq.children('.ui-state-focus').removeClass("ui-state-focus");
        });

        this.stars.on("click.rating", function() {
            var value = $this.stars.index(this) + 1;   //index starts from zero

            $this.setValue(value);
            $this.focus($(this), true);
        });

        this.cancel.on("mouseenter.rating", function() {
            $(this).addClass('ui-rating-cancel-hover');
        }).on("mouseleave.rating", function() {
            $(this).removeClass('ui-rating-cancel-hover');
        }).on("click.rating", function() {
            $this.reset();
            $this.focus($(this), true);
        });
    },
    
    /**
     * Set focus to element
     * @param {JQuery} el focusable element
     * @param {boolean} isInputFocus Whether to refocus to input element
     * @private
     */
    focus: function(el, isInputFocus) {
        if (!this.cfg.disabled && el) {
            this.jq.children('.ui-state-focus').removeClass("ui-state-focus");
            el.addClass('ui-state-focus');
            
            if (isInputFocus) {
                this.input.focus();
            }
        }
    },
    
    /**
     * Get focusable element
     * @return {JQuery} element
     * @private
     */
    getFocusableElement: function() {
        var value = this.getValue() || 0;
        return value === 0 ? (this.cancel && this.cancel.length ? this.cancel : this.stars.eq(0)) : this.stars.eq(value - 1);    
    },

    /**
     * Removes the event listeners that were added, called when this widget is disabled.
     * @private
     */
    unbindEvents: function() {
        this.jq.attr('tabindex', -1);
        this.jq.off('keydown.rating focus.rating blur.rating');
        this.stars.off('click.rating');
        this.cancel.off('mouseenter.rating mouseleave.rating click.rating');
    },

    /**
     * Finds the current rating, i.e. the number of stars selected.
     * @return {number | null} The current rating value.
     */
    getValue: function() {
        var inputVal = this.input.val();

        return inputVal === '' ? null : parseInt(inputVal);
    },

    /**
     * Sets the rating to the given value.
     * @param {number | undefined | null} value New rating value to set (number of stars selected). Pass `undefined` or
     * a value not greater thatn 0 to reset the value.
     */
    setValue: function(value) {
        if(this.isDisabled() || this.isReadOnly()) {
            return;
        }

        // check minimum and maximum
        var newValue = parseInt(value);
        if(isNaN(newValue) || newValue <= 0) {
            this.reset();
            return;
        }
        else if(newValue > this.stars.length) {
            newValue = this.stars.length;
        }

        //set hidden value
        this.input.val(newValue);
        this.input.attr('aria-valuenow', newValue);

        //update visuals
        this.stars.removeClass('ui-rating-star-on');
        for(var i = 0; i < newValue; i++) {
            this.stars.eq(i).addClass('ui-rating-star-on');
        }

        //invoke callback
        if(this.cfg.onRate) {
            this.cfg.onRate.call(this, newValue);
        }

        this.callBehavior('rate');
    },

    /**
     * Checks whether this widget is currently disabled. Whe disabled, the user cannot edit the value and it will not be
     * sent to the server when the form is submitted.
     * @return {boolean} `true` if this rating widget is disabled, `false` otherwise.
     */
    isDisabled: function() {
        return this.jq.hasClass('ui-state-disabled');
    },

    /**
     * Checks whether this widget is currently read-only. When read-only, the user cannot edit the value, but the value
     * will be sent to the server when the form is submitted.
     * @return {boolean} `true` if this rating widget is read-only, `false` otherwise.
     */
    isReadOnly: function() {
        return this.cfg.readonly;
    },

    /**
     * Enables this rating widget so the user can give a rating.
     */
    enable: function() {
        if(!this.isDisabled() || this.isReadOnly()) {
            return;
        }
        this.cfg.disabled = false;

        this.bindEvents();

        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    },

    /**
     * Disables this rating widget so the user cannot give a rating anymore.
     */
    disable: function() {
        if(this.isDisabled()) {
            return;
        }
        this.cfg.disabled = true;

        this.unbindEvents();

        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    },

    /**
     * Resets the rating so that no stars are selected.
     */
    reset: function() {
        this.input.val('');
        this.input.attr('aria-valuenow', '');

        this.stars.filter('.ui-rating-star-on').removeClass('ui-rating-star-on');

        this.callBehavior('cancel');
    }
});
