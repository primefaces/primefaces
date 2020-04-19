/**
 * __PrimeFaces Rating Widget__
 * 
 * Rating component features a star based rating system.
 * 
 * @prop {JQuery} cancel The DOM element for the cancel button.
 * @prop {JQuery} jqInput The DOM element for the hidden input field storing the value of this widget.
 * @prop {JQuery} stars The DOM elements for the clickable stars.
 * @prop {number} value The current value, i.e. the number of selected stars
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
        this.jqInput = $(this.jqId + '_input');
        this.value = this.getValue();
        this.stars = this.jq.children('.ui-rating-star');
        this.cancel = this.jq.children('.ui-rating-cancel');

        if(!this.cfg.disabled && !this.cfg.readonly) {
            this.bindEvents();
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
        var _self = this;

        this.stars.click(function() {
            var value = _self.stars.index(this) + 1;   //index starts from zero

            _self.setValue(value);
        });

        this.cancel.hover(function() {
            $(this).toggleClass('ui-rating-cancel-hover');
        })
        .click(function() {
            _self.reset();
        });
    },

    /**
     * Removes the event listeners that were added, called when this widget is disabled.
     * @private
     */
    unbindEvents: function() {
        this.stars.unbind('click');

        this.cancel.unbind('hover click');
    },

    /**
     * Finds the current rating, i.e. the number of stars selected.
     * @return {number | null} The current rating value.
     */
    getValue: function() {
        var inputVal = this.jqInput.val();

        return inputVal == '' ? null : parseInt(inputVal);
    },

    /**
     * Sets the rating to the given value.
     * @param {number} value New rating value to set (number of starts selected).
     */
    setValue: function(value) {
        //set hidden value
        this.jqInput.val(value);

        //update visuals
        this.stars.removeClass('ui-rating-star-on');
        for(var i = 0; i < value; i++) {
            this.stars.eq(i).addClass('ui-rating-star-on');
        }

        //invoke callback
        if(this.cfg.onRate) {
            this.cfg.onRate.call(this, value);
        }

        this.callBehavior('rate');
    },

    /**
     * Enables the rating so the user can give a rating.
     */
    enable: function() {
        this.cfg.disabled = false;

        this.bindEvents();

        this.jq.removeClass('ui-state-disabled');
    },

    /**
     * Disables the rating so the user cannot give a rating anymore.
     */
    disable: function() {
        this.cfg.disabled = true;

        this.unbindEvents();

        this.jq.addClass('ui-state-disabled');
    },

    /**
     * Resets the rating so that no stars are selected.
     */
    reset: function() {
        this.jqInput.val('');

        this.stars.filter('.ui-rating-star-on').removeClass('ui-rating-star-on');

        this.callBehavior('cancel');
    }
});