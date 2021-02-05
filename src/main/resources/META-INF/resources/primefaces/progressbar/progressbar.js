/**
 * __PrimeFaces ProgressBar widget__
 * 
 * ProgressBar is a process status indicator that can either work purely on client side or interact with server side
 * using AJAX.
 * 
 * @prop {JQuery} jqLabel The DOM element for the label of the progress bar.
 * @prop {JQuery} jqValue The DOM element for the value of the progress bar.
 * @prop {number} progressPoll The set-timeout timer ID of the time used for polling when `ajax` is set to `true`.
 * @prop {number} value The current value of this progress bar.
 * 
 * @interface {PrimeFaces.widget.ProgressBarCfg} cfg The configuration for the {@link  ProgressBar| ProgressBar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.ajax Specifies the mode of the progress bar, in AJAX mode progress value is retrieved from a
 * backing bean.
 * @prop {number} cfg.animationDuration Animation duration in milliseconds determining how long the animation will run.
 * @prop {string} cfg.formId ID of the form used for AJAX requests.
 * @prop {boolean} cfg.global Global AJAX requests are listened to by the `ajaxStatus` component, setting global to
 * `false` will not trigger `ajaxStatus`.
 * @prop {number} cfg.initialValue The initial value for the progress bar.
 * @prop {number} cfg.interval Duration in milliseconds between two AJAX polling requests, when `ajax` is set to `true`.
 * @prop {string} cfg.labelTemplate Template of the progress label.
 */
PrimeFaces.widget.ProgressBar = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.jqValue = this.jq.children('.ui-progressbar-value');
        this.jqLabel = this.jq.children('.ui-progressbar-label');
        this.value = this.cfg.initialValue;
        this.cfg.global = (this.cfg.global === false) ? false : true;

        this.enableARIA();
    },

    /**
     * Sets the value (progress) of this progress bar to a value between zero and a hundred percent.
     * @param {number} value New value for this progress bar, between `0` and `100`.
     */
    setValue: function(value) {
        if(value >= 0 && value<=100) {
            if(value == 0) {
                this.jqValue.hide().css('width', '0%').removeClass('ui-corner-right');

                this.jqLabel.hide();
            }
            else {
                this.jqValue.show().animate({
                    'width': value + '%'
                }, this.cfg.animationDuration, 'easeInOutCirc');

                if(this.cfg.labelTemplate) {
                    var formattedLabel = this.cfg.labelTemplate.replace(/{value}/gi, value);
                    this.jqLabel.text(formattedLabel).show();
                }
            }

            this.value = value;
            this.jq.attr('aria-valuenow', value);
        }
    },

    /**
     * Finds the progress currently shown by this progress bar.
     * @return {number} The current value of this progress bar, between `0` and `100`.
     */
    getValue: function() {
        return this.value;
    },

    /**
     * Starts the progress bar, if not already started. Does not reset its current value.
     */
    start: function() {
        var $this = this;

        if(this.cfg.ajax) {

            this.progressPoll = setInterval(function() {
                var options = {
                    source: $this.id,
                    process: $this.id,
                    formId: $this.getParentFormId(),
                    global: $this.cfg.global,
                    async: true,
                    oncomplete: function(xhr, status, args, data) {
                        var value = args[$this.id + '_value'];
                        $this.setValue(value);

                        //trigger complete listener
                        if(value === 100) {
                            $this.fireCompleteEvent();
                        }
                    }
                };

                PrimeFaces.ajax.Request.handle(options);

            }, this.cfg.interval);
        }
    },

    /**
     * Invokes the behavior for when the progress bar is complete.
     * @private
     */
    fireCompleteEvent: function() {
        clearInterval(this.progressPoll);

        this.callBehavior('complete');
    },

    /**
     * Cancels the progress bar, resetting it back to zero percent.
     */
    cancel: function() {
        clearInterval(this.progressPoll);
        this.setValue(0);
    },

    /**
     * Adds the appropriate aria attributes.
     * @private
     */
    enableARIA: function() {
        this.jq.attr('role', 'progressbar')
                .attr('aria-valuemin', 0)
                .attr('aria-valuenow', this.value)
                .attr('aria-valuemax', 100);
    }

});