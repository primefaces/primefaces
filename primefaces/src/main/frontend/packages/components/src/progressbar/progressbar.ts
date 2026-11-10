/**
 * The configuration for the {@link ProgressBar} widget.
 */
export interface ProgressBarCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** In AJAX mode the value is polled from the server. */
    ajax: boolean;
    /** Animation duration in milliseconds. */
    animationDuration: number;
    /** Whether the polling requests trigger `p:ajaxStatus`, normalized to a boolean on init. */
    global: boolean;
    /** The initial value. */
    initialValue: number;
    /** Duration in milliseconds between two polling requests in AJAX mode. */
    interval: number;
    /** Template of the label, `{value}` is replaced by the value. */
    labelTemplate: string;
}

/**
 * __PrimeFaces ProgressBar widget__
 *
 * ProgressBar is a process status indicator that can either work purely on client side or interact with server side
 * using AJAX.
 */
export class ProgressBar<Cfg extends ProgressBarCfg = ProgressBarCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The value bar. */
    jqValue: JQuery = $();
    /** The label inside the value bar. */
    jqLabel: JQuery = $();
    /** The timer ID of the polling in AJAX mode. */
    progressPoll?: number;
    /** The current value, `undefined` until a valid value was set. */
    value?: number;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.jqValue = this.jq.children('.p-progressbar-value');
        this.jqLabel = this.jqValue.children('.p-progressbar-label');
        this.cfg.global = this.cfg.global !== false;
        this.setValue(this.cfg.initialValue || 0, 0);

        this.enableARIA();

        // a running poll survives a refresh (same widget instance), but must stop when the widget is gone
        this.addDestroyListener(() => {
            clearInterval(this.progressPoll);
        });
    }

    /**
     * Sets the value of this progress bar, values outside of `0` to `100` are ignored.
     * @param value New value, between `0` and `100`.
     * @param animationDuration Animation duration in milliseconds, defaults to the configured one.
     */
    setValue(value: number, animationDuration?: number): void {
        if (value < 0 || value > 100) {
            return;
        }
        let valueWidth = value;

        // the value bar must be at least as wide as the label
        if (this.cfg.labelTemplate) {
            // invisible copy of the label with the same font to measure its width
            const $span = $('<span></span>')
                .text(this.jqLabel.text())
                .css({
                    visibility: 'hidden',
                    position: 'absolute',
                    whiteSpace: 'nowrap',
                    font: this.jqLabel.css('font'),
                    fontSize: this.jqLabel.css('fontSize'),
                    fontWeight: this.jqLabel.css('fontWeight'),
                    fontFamily: this.jqLabel.css('fontFamily'),
                })
                .appendTo(document.body);

            const labelWidthPx = $span.outerWidth(true) ?? 0;
            const progressBarWidthPx = this.jq.width() ?? 0;
            const labelWidthPercentage = (labelWidthPx / progressBarWidthPx) * 100;

            valueWidth = Math.max(value, labelWidthPercentage);

            $span.remove();
        }

        const isIndeterminate = this.jq.hasClass('p-progressbar-indeterminate');
        const isTransparent = value === 0 && !isIndeterminate;

        const styles = isTransparent ?
            {
                value: { 'background-color': 'transparent' },
                label: { 'text-align': 'left', 'color': 'var(--p-text-color)' }
            } :
            {
                value: { 'background-color': '' },
                label: { 'text-align': 'center', 'color': '' }
            };
        this.jqValue.css(styles.value);

        const animation = animationDuration !== undefined ? animationDuration : (this.cfg.animationDuration || 0);
        this.jqValue.show().animate({
            'width': valueWidth + '%'
        }, animation, 'easeInOutCirc');

        if (this.cfg.labelTemplate) {
            this.jqLabel.css(styles.label);
            this.jqLabel.text(this.cfg.labelTemplate.replace(/{value}/gi, String(value))).show();
        }

        this.value = value;
        this.jq.attr('aria-valuenow', value);
    }

    /**
     * @returns The current value, between `0` and `100`.
     */
    getValue(): number | undefined {
        return this.value;
    }

    /**
     * Starts polling the value in AJAX mode, the current value is not reset.
     */
    start(): void {
        if (this.cfg.ajax) {

            this.callBehavior('start');

            clearInterval(this.progressPoll);
            this.progressPoll = window.setInterval(() => {
                const options: PrimeFacesTypes.ajax.Configuration = {
                    source: this.getId(),
                    process: this.getId(),
                    formId: this.getParentFormId(),
                    global: this.cfg.global,
                    async: true,
                    oncomplete: (_xhr, _status, args) => {
                        if (!args) {
                            return;
                        }
                        const value = args[this.getId() + '_value'] as number;
                        this.setValue(value);

                        if (value === 100) {
                            this.fireCompleteEvent();
                        }
                        else {
                            this.callBehavior('progress');
                        }
                    }
                };

                PrimeFaces.ajax.Request.handle(options);

            }, this.cfg.interval);
        }
    }

    private fireCompleteEvent(): void {
        clearInterval(this.progressPoll);

        this.callBehavior('complete');
    }

    /**
     * Stops the polling and resets the value to `0`.
     */
    cancel(): void {
        clearInterval(this.progressPoll);
        this.setValue(0);
    }

    private enableARIA(): void {
        this.jq.attr('role', 'progressbar')
            .attr('aria-valuemin', 0)
            .attr('aria-valuemax', 100);
        if (this.value !== undefined) {
            this.jq.attr('aria-valuenow', this.value);
        }
    }
}
