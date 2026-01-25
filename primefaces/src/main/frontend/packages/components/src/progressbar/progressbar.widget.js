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
 * @prop {boolean} cfg.global Global AJAX requests are listened to by the `ajaxStatus` component, setting global to
 * `false` will not trigger `ajaxStatus`.
 * @prop {number} cfg.initialValue The initial value for the progress bar.
 * @prop {number} cfg.interval Duration in milliseconds between two AJAX polling requests, when `ajax` is set to `true`.
 * @prop {string} cfg.labelTemplate Template of the progress label.
 */
PrimeFaces.widget.ProgressBar = class ProgressBar extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.jqValue = this.jq.children('.ui-progressbar-value');
        this.jqLabel = this.jqValue.children('.ui-progressbar-label');
        this.cfg.global = this.cfg.global !== false;
        this.setValue(this.cfg.initialValue || 0, 0);

        this.enableARIA();
    }

    /**
     * Sets the value (progress) of this progress bar to a value between zero and a hundred percent.
     * @param {number} value New value for this progress bar, between `0` and `100`.
     * @param {number} [animationDuration] Optional animation duration in milliseconds. If not specified, 
     * the widget's configured animation duration will be used.
     */
    setValue(value, animationDuration) {
        if (value < 0 || value > 100) {
            return;
        }
        let valueWidth = value;

        // This code handles measuring the width of the label text to ensure proper display
        if (this.cfg.labelTemplate) {
            // Create a temporary span element that's invisible but has the same styling as the label
            // This allows us to measure the exact width the text will occupy without affecting the UI
            var $span = $("<span></span>")
                .text(this.jqLabel.text())
                .css({
                    visibility: "hidden", // Make it invisible
                    position: "absolute", // Take it out of the document flow
                    whiteSpace: "nowrap", // Prevent text wrapping
                    // Copy all font properties from the actual label to ensure accurate measurement
                    font: this.jqLabel.css("font"),
                    fontSize: this.jqLabel.css("fontSize"),
                    fontWeight: this.jqLabel.css("fontWeight"),
                    fontFamily: this.jqLabel.css("fontFamily"),
                })
                .appendTo(document.body); // Add to document to calculate dimensions

            // Calculate the width of the label as a percentage of the progressbar's width
            var labelWidthPx = $span.outerWidth(true);
            var progressBarWidthPx = this.jq.width();

            // Convert the label width to a percentage of the progressbar width
            var labelWidthPercentage = (labelWidthPx / progressBarWidthPx) * 100;

            // Ensure the value width is at least the percentage needed to display the label
            valueWidth = Math.max(value, labelWidthPercentage);

            // Remove the temporary element to avoid memory leaks
            $span.remove();
        }

        const isIndeterminate = this.jq.hasClass('ui-progressbar-indeterminate');
        const isTransparent = value === 0 && !isIndeterminate;

        // Handle styling based on value
        const styles = isTransparent ?
            {
                value: { 'background-color': 'transparent' },
                label: { 'text-align': 'left', 'color': getComputedStyle(document.documentElement).getPropertyValue('--text-color') }
            } :
            {
                value: { 'background-color': '' },
                label: { 'text-align': 'center', 'color': '' }
            };
        this.jqValue.css(styles.value);

        // Animate width
        const animation = animationDuration !== undefined ? animationDuration : (this.cfg.animationDuration || 0);
        this.jqValue.show().animate({
            'width': valueWidth + '%'
        }, animation, 'easeInOutCirc');

        // Update label if template exists
        if (this.cfg.labelTemplate) {
            this.jqLabel.css(styles.label);
            this.jqLabel.text(this.cfg.labelTemplate.replace(/{value}/gi, value)).show();
        }

        // Update internal state and ARIA
        this.value = value;
        this.jq.attr('aria-valuenow', value);
    }

    /**
     * Finds the progress currently shown by this progress bar.
     * @return {number} The current value of this progress bar, between `0` and `100`.
     */
    getValue() {
        return this.value;
    }

    /**
     * Starts the progress bar, if not already started. Does not reset its current value.
     */
    start() {
        var $this = this;

        if (this.cfg.ajax) {

            $this.callBehavior('start');

            this.progressPoll = setInterval(function () {
                var options = {
                    source: $this.id,
                    process: $this.id,
                    formId: $this.getParentFormId(),
                    global: $this.cfg.global,
                    async: true,
                    oncomplete: function (xhr, status, args, data) {
                        var value = args[$this.id + '_value'];
                        $this.setValue(value);

                        //trigger complete listener
                        if (value === 100) {
                            $this.fireCompleteEvent();
                        }
                        else {
                            $this.callBehavior('progress');
                        }
                    }
                };

                PrimeFaces.ajax.Request.handle(options);

            }, this.cfg.interval);
        }
    }

    /**
     * Invokes the behavior for when the progress bar is complete.
     * @private
     */
    fireCompleteEvent() {
        clearInterval(this.progressPoll);

        this.callBehavior('complete');
    }

    /**
     * Cancels the progress bar, resetting it back to zero percent.
     */
    cancel() {
        clearInterval(this.progressPoll);
        this.setValue(0);
    }

    /**
     * Adds the appropriate aria attributes.
     * @private
     */
    enableARIA() {
        this.jq.attr('role', 'progressbar')
            .attr('aria-valuemin', 0)
            .attr('aria-valuenow', this.value)
            .attr('aria-valuemax', 100);
    }

}