/**
 * Data passed to the slider callbacks.
 */
export interface SliderEventData {
    /** The moved handle. */
    handle: HTMLElement | undefined;
    /** The index of the moved handle. */
    handleIndex: number;
    /** The value of the moved handle. */
    value: number;
    /** The values of both handles of a range slider. */
    values?: number[];
}

export type SliderCallback = (this: Slider | HTMLElement, event: JQuery.TriggeredEvent | null, data: SliderEventData) => void;

/**
 * The configuration for the {@link Slider} widget.
 */
export interface SliderCfg extends PrimeType.widget.BaseWidgetCfg {
    /** Whether to animate the handle when the value is set by a click or programmatically. */
    animate: boolean;
    /** Whether the slider is disabled. */
    disabled: boolean;
    /** ID of the component to display the value. */
    display: string;
    /** Template of the display, with the placeholders `{value}`, `{min}` and `{max}`. */
    displayTemplate: string;
    /** ID of the input, or the IDs of both inputs of a range slider separated by a comma. */
    input: string;
    /** The maximum value. */
    max: number;
    /** The minimum value. */
    min: number;
    /** Called when a handle is moved. */
    onSlide: SliderCallback;
    /** Called when a handle stops moving. */
    onSlideEnd: SliderCallback;
    /** Called when a handle starts moving. */
    onSlideStart: SliderCallback;
    /** The orientation of the slider. */
    orientation: "horizontal" | "vertical";
    /** `true` for two handles with the range between, `min` / `max` for the range from the minimum / maximum to the handle. */
    range: boolean | "min" | "max";
    /** The step size. */
    step: number;
    /** The value of a slider with one handle. */
    value: number;
    /** The values of a range slider. */
    values: number[] | null;
}

/**
 * __PrimeFaces Slider Widget__
 *
 * Slider is used to provide input with various customization options like orientation, display modes and skinning.
 */
export class Slider<Cfg extends SliderCfg = SliderCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The inputs storing the value of each handle. */
    input: JQuery = $();
    /** The element displaying the value. */
    output: JQuery | undefined;
    /** The handles. */
    handles: JQuery = $();
    /** The range between the handles, `null` without range. */
    range: JQuery | null = null;

    private values: number[] = [];
    private min: number = 0;
    private max: number = 100;
    private step: number = 1;
    private readonlyState: boolean = false;
    private dragIndex: number | null = null;
    private keySliding: boolean = false;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.min = Number(this.cfg.min) || 0;
        this.max = PrimeFaces.utils.defaultNumeric(Number(this.cfg.max), 100);
        this.step = Number(this.cfg.step) > 0 ? Number(this.cfg.step) : 1;
        this.cfg.displayTemplate = this.cfg.displayTemplate || (this.cfg.range === true ? '{min} - {max}' : '{value}');

        const inputIds = (this.cfg.input ?? '').split(',');
        this.input = $(inputIds.map((id) => PrimeFaces.escapeClientId(id)).join(','));
        this.output = this.cfg.display ? $(PrimeFaces.escapeClientId(this.cfg.display)) : undefined;

        const values = this.cfg.range === true ? (this.cfg.values ?? []) : [this.cfg.value];
        this.values = this.cfg.range === true
            ? [this.align(values[0]), this.align(values[1] ?? values[0])]
            : [this.align(values[0])];

        this.readonlyState = this.jq.hasClass('ui-state-readonly');

        this.renderSlider();
        this.bindEvents();
        this.bindInputEvents();
    }

    private renderSlider(): void {
        const vertical = this.cfg.orientation === 'vertical';
        this.jq.children('.ui-slider-handle, .ui-slider-range').remove();
        this.jq.addClass('ui-slider').toggleClass('ui-slider-vertical', vertical).toggleClass('ui-slider-horizontal', !vertical);

        this.range = this.cfg.range ? $('<div class="ui-slider-range"></div>').appendTo(this.jq) : null;
        if (this.cfg.range === 'min' || this.cfg.range === 'max') {
            this.range?.addClass('ui-slider-range-' + this.cfg.range);
        }

        this.handles = $(this.values.map((_, i) => $('<span class="ui-slider-handle" role="slider" tabindex="0"></span>')
            .attr({ 'aria-orientation': vertical ? 'vertical' : 'horizontal', 'aria-valuemin': this.min, 'aria-valuemax': this.max })
            .attr('aria-readonly', this.readonlyState ? 'true' : null)
            .data('index', i)[0]!)).appendTo(this.jq);

        this.setDisabled(!!this.cfg.disabled);
        this.updateUI(false);
    }

    private bindEvents(): void {
        this.jq.off('.slider')
            .on('pointerdown.slider', (e) => this.onPointerDown(e))
            .on('pointermove.slider', (e) => {
                if (this.dragIndex !== null) {
                    this.slide(e, this.dragIndex, this.valueAt(e), false);
                }
            })
            .on('pointerup.slider pointercancel.slider', (e) => {
                if (this.dragIndex !== null) {
                    const index = this.dragIndex;
                    this.dragIndex = null;
                    this.onSlideEnd(e, this.data(index));
                }
            });

        this.handles
            .on('keydown.slider', (e) => this.onKeyDown(e))
            .on('keyup.slider', (e) => {
                if (this.keySliding) {
                    this.keySliding = false;
                    this.onSlideEnd(e, this.data(this.indexOf(e)));
                }
            });
    }

    private bindInputEvents(): void {
        if (this.input.parent().hasClass('ui-inputnumber')) {
            this.input.parent().find('input:hidden').off('input.slider').on('input.slider', (e) => {
                this.setValue(String($(e.target).val()));
            });
        }
        else if (this.input.hasClass('ui-spinner-input')) {
            this.input.off('change.slider').on('change.slider', () => {
                const spinner = PrimeFaces.getWidgetById(this.input.closest('.ui-spinner').attr('id') ?? '') as unknown as { getValue(): number } | null;
                if (spinner) {
                    this.setValue(spinner.getValue());
                }
            });
        }
        else {
            this.input.off('keydown.slider keyup.slider').on('keydown.slider', (e) => {
                if (PrimeFaces.utils.ignoreFilterKey(e)) {
                    return;
                }
                const key = e.key ?? '';
                const current = String($(e.target).val() ?? '');
                const decimal = this.step % 1 !== 0;

                // allow a single decimal separator for decimal steps and a single minus if min < 0 (#6319)
                if ((decimal && (key === ',' || key === '.') && /[.,]/.test(current))
                        || (key === '-' && (this.min >= 0 || current.indexOf('-') !== -1))) {
                    return false;
                }
                return new RegExp('^[0-9' + (decimal ? '.,' : '') + (this.min < 0 ? '-' : '') + ']$').test(key);
            }).on('keyup.slider', (e) => {
                this.setValue(String($(e.target).val() ?? ''));
            });
        }
    }

    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as PointerEvent | undefined;
        if (this.cfg.disabled || this.readonlyState || !event || (event.pointerType === 'mouse' && event.button !== 0)
                || (event.pointerType === 'touch' && !PrimeFaces.env.isTouchable(this.cfg))) {
            return;
        }

        e.preventDefault();
        const value = this.valueAt(e);
        const onHandle = $(e.target).closest('.ui-slider-handle');

        // drag the grabbed handle, otherwise the closest one (the upper one if both are equally close and above)
        let index = onHandle.length ? Number(onHandle.data('index')) : 0;
        if (!onHandle.length && this.values.length === 2) {
            const [low, high] = this.values as [number, number];
            index = Math.abs(value - high) < Math.abs(value - low) || (low === high && value > high) ? 1 : 0;
        }

        this.dragIndex = index;
        this.jq[0]?.setPointerCapture(event.pointerId);
        this.handles.eq(index).trigger('focus');
        this.onSlideStart(e, this.data(index));
        if (!onHandle.length) {
            this.slide(e, index, value, true);
        }
    }

    private onKeyDown(e: JQuery.TriggeredEvent): void {
        if (this.cfg.disabled || this.readonlyState) {
            return;
        }

        const index = this.indexOf(e);
        const current = this.values[index]!;
        const page = (this.max - this.min) / 5;
        const steps: Record<string, number> = {
            ArrowUp: this.step, ArrowRight: this.step, ArrowDown: -this.step, ArrowLeft: -this.step,
            PageUp: page, PageDown: -page, Home: -Infinity, End: Infinity
        };
        const delta = steps[e.key ?? ''];
        if (delta === undefined) {
            return;
        }

        e.preventDefault();
        if (!this.keySliding) {
            this.keySliding = true;
            this.onSlideStart(e, this.data(index));
        }
        this.slide(e, index, this.align(current + delta), false);
    }

    /** The step aligned value at the position of a pointer event. */
    private valueAt(e: JQuery.TriggeredEvent): number {
        const rect = this.jq[0]!.getBoundingClientRect();
        const event = e.originalEvent as PointerEvent;
        const percent = this.cfg.orientation === 'vertical'
            ? 1 - (event.clientY - rect.top) / rect.height
            : (event.clientX - rect.left) / rect.width;
        return this.align(this.min + Math.min(1, Math.max(0, percent || 0)) * (this.max - this.min));
    }

    private slide(event: JQuery.TriggeredEvent, index: number, value: number, animate: boolean): void {
        // the handles of a range slider can't pass each other
        if (this.values.length === 2) {
            value = index === 0 ? Math.min(value, this.values[1]!) : Math.max(value, this.values[0]!);
        }
        if (value === this.values[index]) {
            return;
        }

        this.values[index] = value;
        this.updateUI(animate);
        this.onSlide(event, this.data(index));
    }

    private updateUI(animate: boolean): void {
        const vertical = this.cfg.orientation === 'vertical';
        const span = this.max - this.min;
        const percents = this.values.map((value) => span ? (value - this.min) / span * 100 : 0);

        this.jq.toggleClass('ui-slider-animate', animate && !!this.cfg.animate);
        this.handles.each((i, handle) => {
            $(handle).css(vertical ? 'bottom' : 'left', percents[i] + '%').attr('aria-valuenow', this.values[i]!);
        });

        if (this.range) {
            const [first, second] = percents as [number, number?];
            const start = this.cfg.range === 'min' ? 0 : first;
            const size = this.cfg.range === true ? (second ?? first) - first : this.cfg.range === 'max' ? 100 - first : first;
            this.range.css(vertical ? { bottom: start + '%', height: size + '%' } : { left: start + '%', width: size + '%' });
        }
    }

    /** Returns the value trimmed to min / max and aligned to the step (#8619: min / max may not align with the step). */
    private align(value: number | string | undefined | null): number {
        const val = Number(value);
        const { min, max, step } = this;
        if (isNaN(val) || val <= min) {
            return min;
        }
        if (val >= max) {
            return max;
        }
        // rounded quotients, e.g. 0.7 + 0.1 = 0.7999… must give 0.8 for the step 0.1
        const steps = parseFloat((val / step).toFixed(8));
        const aligned = parseFloat(((val - min) % step).toFixed(8)) % step > 0 ? Math.floor(steps) * step : Math.ceil(steps) * step;
        return Math.min(max, Math.max(min, parseFloat(aligned.toFixed(5))));
    }

    private indexOf(e: JQuery.TriggeredEvent): number {
        return Number($(e.target).data('index')) || 0;
    }

    private data(index: number): SliderEventData {
        return {
            handle: this.handles[index],
            handleIndex: index,
            value: this.values[index]!,
            values: this.values.length === 2 ? this.values.slice() : undefined
        };
    }

    private onSlideStart(event: JQuery.TriggeredEvent | null, data: SliderEventData): void {
        this.cfg.onSlideStart?.call(this.jq[0]!, event, data);
    }

    /**
     * Updates the inputs and the display after a handle moved.
     * @param event The event that moved the handle.
     * @param data The data of the moved handle.
     */
    onSlide(event: JQuery.TriggeredEvent | null, data: SliderEventData): void {
        this.cfg.onSlide?.call(this, event, data);

        const decimals = this.step % 1 !== 0 ? String(this.step).split('.')[1]?.length ?? 0 : 0;
        const format = (value: number) => value.toFixed(decimals);

        if (this.cfg.range === true && data.values) {
            this.setInputValue(this.input.eq(0), data.values[0]!);
            this.setInputValue(this.input.eq(1), data.values[1]!);
            this.output?.text((this.cfg.displayTemplate ?? '').replace('{min}', format(data.values[0]!)).replace('{max}', format(data.values[1]!)));
        }
        else {
            this.setInputValue(this.input, data.value);
            this.output?.text((this.cfg.displayTemplate ?? '').replace('{value}', format(data.value)));
        }
    }

    /**
     * Notifies the inputs and the `slideEnd` behavior after a handle stopped moving.
     * @param event The event that ended the slide.
     * @param data The data of the moved handle.
     */
    onSlideEnd(event: JQuery.TriggeredEvent | null, data: SliderEventData): void {
        this.cfg.onSlideEnd?.call(this, event, data);

        this.input.each((_, input) => {
            const $input = $(input);
            if ($input.parent().hasClass('ui-inputnumber') || $input.hasClass('ui-spinner-input')) {
                $input.trigger('change');
            }
        });

        if (this.hasBehavior('slideEnd')) {
            this.callBehavior('slideEnd', { params: [{ name: this.id + '_slideValue', value: data.value }] });
        }
    }

    private setInputValue(input: JQuery, value: number): void {
        const holder = input.closest('.ui-inputnumber, .ui-spinner');
        const widget = holder.length ? PrimeFaces.getWidgetById(holder.attr('id') ?? '') as unknown as { setValue(value: number): void } | null : null;
        if (widget) {
            widget.setValue(value);
        }
        else {
            input.val(value);
        }
    }

    /** The value of the slider, or of the first handle of a range slider. */
    getValue(): number {
        return this.values[0]!;
    }

    /** Sets the value of the slider, or of the first handle of a range slider. */
    setValue(value: number | string): void {
        this.values[0] = this.align(value);
        if (this.values.length === 2) {
            this.values[0] = Math.min(this.values[0], this.values[1]!);
        }
        this.updateUI(true);
    }

    /** The values of a range slider. */
    getValues(): number[] {
        return this.values.slice();
    }

    /** Sets the values of a range slider. */
    setValues(values: (number | string)[]): void {
        this.values = this.values.map((_, i) => this.align(values[i]));
        this.updateUI(true);
    }

    /** Enables the slider. */
    override enable(): void {
        this.setDisabled(false);
    }

    /** Disables the slider. */
    override disable(): void {
        this.setDisabled(true);
    }

    private setDisabled(disabled: boolean): void {
        this.cfg.disabled = disabled;
        this.jq.toggleClass('ui-state-disabled', disabled);
        this.handles.attr('aria-disabled', String(disabled));
    }
}
