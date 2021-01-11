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
        var $this = this;

        this.stars.on("click", function() {
            var value = $this.stars.index(this) + 1;   //index starts from zero

            $this.setValue(value);
        });

        this.cancel.on("mouseenter", function() {
             $(this).addClass('ui-rating-cancel-hover');
        }).on("mouseleave", function() {
             $(this).removeClass('ui-rating-cancel-hover');
        }).on("click", function() {
            $this.reset();
        });
    },

    /**
     * Removes the event listeners that were added, called when this widget is disabled.
     * @private
     */
    unbindEvents: function() {
        this.stars.off('click');

        this.cancel.off('hover click');
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
        this.jqInput.val(newValue);

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
     * Is this widget currently disabled?
     * @return {boolean} true if disabled
     */
    isDisabled: function() {
        return this.jq.hasClass('ui-state-disabled');
    },
    
    /**
     * Is this widget currently read only?
     * @return {boolean} true if read only
     */
    isReadOnly: function() {
        return this.jqInput.is('[readonly]');
    },

    /**
     * Enables the rating so the user can give a rating.
     */
    enable: function() {
        if(!this.isDisabled() || this.isReadOnly()) {
           return; 
        }
        this.cfg.disabled = false;

        this.bindEvents();

        this.jq.removeClass('ui-state-disabled');
    },

    /**
     * Disables the rating so the user cannot give a rating anymore.
     */
    disable: function() {
        if(this.isDisabled()) {
           return; 
        }
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