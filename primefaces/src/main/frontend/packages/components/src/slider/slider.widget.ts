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

/**
 * Callback that is invoked when a slider handle starts moving, moves or stops moving. `this` is the slider widget
 * for `onSlide` / `onSlideEnd` and the slider element for `onSlideStart`.
 * @param event Like with jQuery UI a `slidestart`, `slide` or `slidestop` event wrapping the pointer or keyboard
 * event, `null` if there is none. `onSlideStart` and `onSlide` cancel the slide with `event.preventDefault()`.
 * @param data The handle and the new value(s).
 * @returns `false` to cancel the slide (`onSlideStart` and `onSlide` only).
 */
export type SliderCallback = (this: Slider | HTMLElement, event: JQuery.TriggeredEvent | null, data: SliderEventData) => void | boolean;

/** The methods of the InputNumber / Spinner widgets the slider uses, they are not typed in this package. */
type InputWidget = { getValue(): number; setValue(value: number): void };

/**
 * The configuration for the {@link Slider} widget.
 */
export interface SliderCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
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
    /** The value of a slider with one handle (rendered as string by the server). */
    value: number | string;
    /** The values of a range slider. */
    values: (number | string)[] | null;
}

/**
 * __PrimeFaces Slider Widget__
 *
 * Slider is used to provide input with various customization options like orientation, display modes and skinning.
 */
export class Slider<Cfg extends SliderCfg = SliderCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The inputs storing the value of each handle. */
    input: JQuery = $();
    /** The element displaying the value, `null` without display. */
    output: JQuery | null = null;
    /** The handles. */
    handles: JQuery = $();
    /** The range between the handles, `null` without range. */
    range: JQuery | null = null;

    /** The current value of each handle, aligned to the step; two values for a range slider. */
    private values: number[] = [];
    /** The minimum value. */
    private min: number = 0;
    /** The maximum value. */
    private max: number = 100;
    /** The step size, always positive. */
    private step: number = 1;
    /** Whether the slider is readonly (style class `p-state-readonly`), so the user cannot move the handles. */
    private readonlyState: boolean = false;
    /** Index of the handle dragged with the pointer, `null` while not dragging. */
    private dragIndex: number | null = null;
    /** Index of the handle moved last, dragged first when both handles of a range slider have the same value. */
    private lastIndex: number = 0;
    /** Offset of the pointer from the center of the grabbed handle along the slider axis, in pixels. */
    private clickOffset: number = 0;
    /** Whether a handle is being moved with the keyboard, so `onSlideEnd` is fired on key up. */
    private keySliding: boolean = false;

    /**
     * Initializes the slider: reads the bounds, the step and the initial value(s) from the configuration, renders the
     * handles and binds the pointer, keyboard and input events.
     * @param cfg The configuration of the slider.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.min = Number(this.cfg.min) || 0;
        this.max = PrimeFaces.utils.defaultNumeric(Number(this.cfg.max), 100);
        this.step = Number(this.cfg.step) > 0 ? Number(this.cfg.step) : 1;
        this.cfg.displayTemplate = this.cfg.displayTemplate || (this.cfg.range === true ? '{min} - {max}' : '{value}');

        const inputIds = (this.cfg.input ?? '').split(',');
        this.input = $(inputIds.map((id) => PrimeFaces.escapeClientId(id)).join(','));
        this.output = this.cfg.display ? $(PrimeFaces.escapeClientId(this.cfg.display)) : null;

        const values = this.cfg.range === true ? (this.cfg.values ?? []) : [this.cfg.value];
        this.values = this.cfg.range === true
            ? [this.align(values[0]), this.align(values[1] ?? values[0])]
            : [this.align(values[0])];

        this.readonlyState = this.jq.hasClass('p-state-readonly');

        this.renderSlider();
        this.bindEvents();
        this.bindInputEvents();
    }

    /**
     * Renders the client-side parts of the slider: the orientation classes, the range and one focusable handle with
     * ARIA attributes per value. Replaces the handles and range of a previous rendering.
     */
    private renderSlider(): void {
        const vertical = this.cfg.orientation === 'vertical';
        this.jq.children('.p-slider-handle, .p-slider-range').remove();
        this.jq.addClass('p-slider').toggleClass('p-slider-vertical', vertical).toggleClass('p-slider-horizontal', !vertical);

        this.range = this.cfg.range
            ? $('<div class="p-slider-range"></div>').addClass(this.cfg.range === true ? '' : 'p-slider-range-' + this.cfg.range).appendTo(this.jq)
            : null;

        this.handles = $(this.values.map((_, i) => $('<span class="p-slider-handle" role="slider" tabindex="0"></span>')
            .attr({ 'aria-orientation': vertical ? 'vertical' : 'horizontal', 'aria-valuemin': this.min, 'aria-valuemax': this.max })
            .attr('aria-readonly', this.readonlyState ? 'true' : null)
            .data('index', i)[0]!)).appendTo(this.jq);

        this.setDisabled(!!this.cfg.disabled);
        this.updateUI(false);
    }

    /**
     * Sets up the pointer events of the slider (start, drag and end of a slide) and the keyboard events of the handles.
     */
    private bindEvents(): void {
        this.jq.off('.slider')
            .on('pointerdown.slider', (e) => this.onPointerDown(e))
            // no text selection and no focus change; not on pointerdown, that would suppress the mousedown event the
            // overlays of other widgets listen to on the document to hide
            .on('mousedown.slider', (e) => e.preventDefault())
            .on('pointermove.slider', (e) => {
                const pointer = e.originalEvent as PointerEvent | undefined;
                // the pointerup got lost, e.g. the button was released over an iframe
                if (pointer?.pointerType === 'mouse' && pointer.buttons === 0) {
                    this.endSlide(e);
                }
                else if (this.dragIndex !== null) {
                    this.slide(e, this.dragIndex, this.valueAt(e), false);
                }
            })
            .on('pointerup.slider pointercancel.slider', (e) => this.endSlide(e));

        this.handles
            .on('keydown.slider', (e) => this.onKeyDown(e))
            .on('keyup.slider', (e) => {
                if (this.keySliding) {
                    this.keySliding = false;
                    this.onSlideEnd(e, this.data(this.indexOf(e)));
                }
            });
    }

    /**
     * Moves the handles when the value of their input changes: listens to the InputNumber / Spinner widget of the
     * input, or else to the keys of a plain input, where only digits and, if allowed, a decimal separator and a minus
     * sign can be typed.
     */
    private bindInputEvents(): void {
        // each input moves its own handle; the inputs of a range slider may be the InputNumber / Spinner wrappers
        this.input.each((index, input) => {
            const inputNumber = $(input).closest('.p-inputnumber');
            const spinner = $(input).closest('.p-spinner');
            if (inputNumber.length) {
                inputNumber.find('input:hidden').off('input.slider').on('input.slider', (e) => {
                    this.setHandleValue(index, String($(e.target).val()));
                });
            }
            else if (spinner.length) {
                spinner.find('.p-spinner-input').off('change.slider').on('change.slider', () => {
                    const widget = this.inputWidget(spinner);
                    if (widget) {
                        this.setHandleValue(index, widget.getValue());
                    }
                });
            }
            else {
                $(input).off('keydown.slider keyup.slider').on('keydown.slider', (e) => {
                    // editing keys, shortcuts and the keys of virtual keyboards ("Unidentified") are not filtered
                    if (!PrimeFaces.utils.isPrintableKey(e) || e.key === 'Unidentified' || e.ctrlKey || e.metaKey || e.altKey) {
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
                    this.setHandleValue(index, String($(e.target).val() ?? ''));
                });
            }
        });
    }

    /**
     * Starts a slide when the slider is pressed with the primary mouse button, a pen or a touch (if touch is enabled).
     * Picks the grabbed handle or else the closest one, captures the pointer, focuses the handle and fires
     * `onSlideStart`; a press on the track moves the handle to the pointer right away.
     * @param e The `pointerdown` event.
     */
    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as PointerEvent | undefined;
        if (this.cfg.disabled || this.readonlyState || !event || (event.pointerType === 'mouse' && event.button !== 0)
                || (event.pointerType === 'touch' && !PrimeFaces.env.isTouchable(this.cfg))) {
            return;
        }

        this.clickOffset = 0;
        const value = this.valueAt(e);
        const onHandle = $(e.target).closest('.p-slider-handle');

        // drag the grabbed handle, otherwise the closest one
        let index = onHandle.length ? Number(onHandle.data('index')) : 0;
        if (this.values.length === 2) {
            const [low, high] = this.values as [number, number];
            if (low === high) {
                // both handles at the same value: the one that can move towards the pointer, otherwise the last moved one
                index = low === this.max || value < low ? 0 : low === this.min || value > high ? 1 : this.lastIndex;
            }
            else if (!onHandle.length) {
                index = Math.abs(value - high) < Math.abs(value - low) ? 1 : 0;
            }
        }

        // keep the offset of the pointer to the center of a grabbed handle, so it doesn't jump on the first move
        const rect = onHandle[0]?.getBoundingClientRect();
        if (rect) {
            this.clickOffset = this.cfg.orientation === 'vertical'
                ? event.clientY - rect.top - rect.height / 2
                : event.clientX - rect.left - rect.width / 2;
        }

        this.handles.eq(index).trigger('focus');
        if (!this.onSlideStart(e, this.data(index))) {
            return;
        }
        this.dragIndex = index;
        try {
            this.jq[0]?.setPointerCapture(event.pointerId);
        }
        catch {
            // the pointer is not active anymore
        }
        if (!onHandle.length) {
            this.slide(e, index, value, true);
        }
    }

    /**
     * Ends a slide with the pointer and fires `onSlideEnd`, if a handle is being dragged.
     * @param e The `pointerup`, `pointercancel` or `pointermove` event that ended the slide.
     */
    private endSlide(e: JQuery.TriggeredEvent): void {
        if (this.dragIndex !== null) {
            const index = this.dragIndex;
            this.dragIndex = null;
            this.onSlideEnd(e, this.data(index));
        }
    }

    /**
     * Moves the focused handle with the keyboard: the arrow keys by one step, `PageUp` / `PageDown` by a fifth of the
     * range, `Home` / `End` to the minimum / maximum. Fires `onSlideStart` on the first key press of a slide.
     * @param e The `keydown` event of a handle.
     */
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
            if (!this.onSlideStart(e, this.data(index))) {
                return;
            }
            this.keySliding = true;
        }
        this.slide(e, index, this.align(current + delta), true);
    }

    /**
     * The step aligned value at the position of a pointer event, taking the offset to the center of a grabbed handle
     * into account.
     * @param e The pointer event.
     * @returns The value at the pointer, trimmed to min / max.
     */
    private valueAt(e: JQuery.TriggeredEvent): number {
        const rect = this.jq[0]!.getBoundingClientRect();
        const event = e.originalEvent as PointerEvent;
        const percent = this.cfg.orientation === 'vertical'
            ? 1 - (event.clientY - this.clickOffset - rect.top) / rect.height
            : (event.clientX - this.clickOffset - rect.left) / rect.width;
        return this.align(this.min + Math.min(1, Math.max(0, percent || 0)) * (this.max - this.min));
    }

    /**
     * Moves a handle to a new value by user interaction and fires `onSlide` if the value changed, the callback can
     * cancel the move (see {@link callSlideCallback}). The handles of a range slider cannot pass each other.
     * @param event The event that moved the handle.
     * @param index The index of the handle.
     * @param value The new, step aligned value.
     * @param animate Whether to animate the handle (if `animate` is enabled), e.g. for a click on the track.
     */
    private slide(event: JQuery.TriggeredEvent, index: number, value: number, animate: boolean): void {
        // the handles of a range slider can't pass each other
        if (this.values.length === 2) {
            value = index === 0 ? Math.min(value, this.values[1]!) : Math.max(value, this.values[0]!);
        }
        if (value === this.values[index]) {
            return;
        }

        const values = this.values.slice();
        values[index] = value;
        if (!this.onSlide(event, this.data(index, values))) {
            return;
        }

        this.values = values;
        this.lastIndex = index;
        this.updateUI(animate);
    }

    /**
     * Positions the handles and the range according to the current values and updates `aria-valuenow`.
     * @param animate Whether to animate the handles and the range (if `animate` is enabled).
     */
    private updateUI(animate: boolean): void {
        const vertical = this.cfg.orientation === 'vertical';
        const span = this.max - this.min;
        const percents = this.values.map((value) => span ? (value - this.min) / span * 100 : 0);

        this.jq.toggleClass('p-slider-animate', animate && !!this.cfg.animate);
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

    /**
     * Returns the value trimmed to min / max and aligned to the step (#8619: min / max may not align with the step).
     * @param value The value to align, a number or a numeric string.
     * @returns The aligned value; the minimum for a missing or invalid value.
     */
    private align(value: number | string | undefined | null): number {
        // an empty value (e.g. a null model value or a cleared input) is the minimum, not 0
        const val = value === null || value === undefined || String(value).trim() === '' ? NaN : Number(value);
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

    /**
     * Determines the index of the handle that is the target of an event.
     * @param e An event of a handle.
     * @returns The index of the handle, `0` if the target is not a handle.
     */
    private indexOf(e: JQuery.TriggeredEvent): number {
        return Number($(e.target).data('index')) || 0;
    }

    /**
     * Creates the data passed to the slider callbacks for a handle.
     * @param index The index of the handle.
     * @param values The values of the handles, the current values by default.
     * @returns The handle, its index and value, and both values of a range slider.
     */
    private data(index: number, values: number[] = this.values): SliderEventData {
        return {
            handle: this.handles[index],
            handleIndex: index,
            value: values[index]!,
            values: values.length === 2 ? values.slice() : undefined
        };
    }

    /**
     * Calls the `onSlideStart` or `onSlide` callback like jQuery UI did: with a `slidestart` / `slide` event that
     * wraps the original event. The callback cancels the slide by calling `event.preventDefault()` or by returning
     * `false`.
     * @param callback The callback to call, if any.
     * @param thisArg The `this` of the callback.
     * @param type The type of the event passed to the callback.
     * @param event The original event, `null` if there is none.
     * @param data The data of the moved handle.
     * @returns `false` if the callback cancelled the slide, `true` otherwise.
     */
    private callSlideCallback(callback: SliderCallback | undefined, thisArg: Slider | HTMLElement, type: string,
            event: JQuery.TriggeredEvent | null, data: SliderEventData): boolean {
        if (!callback) {
            return true;
        }
        // jQuery.Event also wraps an event object (typed for event names only), like jQuery UI did
        const wrap = $.Event as unknown as (src: JQuery.TriggeredEvent) => JQuery.TriggeredEvent;
        const slideEvent = event ? wrap(event) : null;
        if (slideEvent) {
            slideEvent.type = type;
        }
        return callback.call(thisArg, slideEvent, data) !== false && !slideEvent?.isDefaultPrevented();
    }

    /**
     * Fires the `onSlideStart` callback, with the slider element as `this`, when a handle starts moving; the callback
     * can cancel the slide (see {@link callSlideCallback}).
     * @param event The event that started the slide.
     * @param data The data of the moved handle.
     * @returns `false` if the callback cancelled the slide.
     */
    onSlideStart(event: JQuery.TriggeredEvent | null, data: SliderEventData): boolean {
        return this.callSlideCallback(this.cfg.onSlideStart, this.jq[0]!, 'slidestart', event, data);
    }

    /**
     * Fires the `onSlide` callback before a handle moves and, unless the callback cancelled the move (see
     * {@link callSlideCallback}), updates the inputs and the display (formatted with the decimals of the step).
     * @param event The event that moves the handle.
     * @param data The data of the moved handle with the new value(s).
     * @returns `false` if the callback cancelled the move.
     */
    onSlide(event: JQuery.TriggeredEvent | null, data: SliderEventData): boolean {
        if (!this.callSlideCallback(this.cfg.onSlide, this, 'slide', event, data)) {
            return false;
        }
        this.updateInputs(data);
        return true;
    }

    /**
     * Updates the inputs and the display (formatted with the decimals of the step) after a handle moved.
     * @param data The data of the moved handle.
     */
    private updateInputs(data: SliderEventData): void {
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
     * Notifies the inputs and the `slideEnd` behavior after a handle stopped moving: fires the `onSlideEnd` callback,
     * triggers `change` on the inputs of InputNumber / Spinner widgets and calls the `slideEnd` behavior with the
     * value of the moved handle.
     * @param event The event that ended the slide.
     * @param data The data of the moved handle.
     */
    onSlideEnd(event: JQuery.TriggeredEvent | null, data: SliderEventData): void {
        // like jQuery UI a `slidestop` event, the slide has ended already and can't be cancelled
        this.callSlideCallback(this.cfg.onSlideEnd, this, 'slidestop', event, data);

        this.input.each((_, input) => {
            // the input of an InputNumber / Spinner; the inputs of a range slider are their wrappers
            if ($(input).closest('.p-inputnumber, .p-spinner').length) {
                ($(input).is('input') ? $(input) : $(input).find('input').not('[type="hidden"]').first()).trigger('change');
            }
        });

        if (this.hasBehavior('slideEnd')) {
            this.callBehavior('slideEnd', { params: [{ name: this.id + '_slideValue', value: data.value }] });
        }
    }

    /**
     * Stores the value of a handle in its input, via the InputNumber / Spinner widget if the input belongs to one.
     * @param input The input, or the InputNumber / Spinner wrapper of a range slider.
     * @param value The value of the handle.
     */
    private setInputValue(input: JQuery, value: number): void {
        const widget = this.inputWidget(input);
        if (widget) {
            widget.setValue(value);
        }
        else {
            input.val(value);
        }
    }

    /**
     * Finds the InputNumber / Spinner widget an input belongs to.
     * @param input The input, or the InputNumber / Spinner wrapper of a range slider.
     * @returns The widget, `null` for a plain input.
     */
    private inputWidget(input: JQuery): InputWidget | null {
        const holder = input.closest('.p-inputnumber, .p-spinner');
        return holder.length ? PrimeFaces.getWidgetById(holder.attr('id') ?? '') as unknown as InputWidget | null : null;
    }

    /**
     * Determines the value of the slider, if there is only one handle. If there is more than one handle, determines
     * the value of the first handle.
     * @returns The value of the first slider handle.
     */
    getValue(): number {
        return this.values[0]!;
    }

    /**
     * Sets the value of the slider, if there is only one handle. If there is more than one handle, sets the value of
     * the first handle. The value is trimmed to min / max and aligned to the step, and the handle moves there. The
     * inputs, the display and the callbacks are not updated.
     * @param value The new value for the first slider handle.
     */
    setValue(value: number | string): void {
        this.setHandleValue(0, value);
    }

    /**
     * Moves a handle to the given value, e.g. when the value of its input changed. The value is aligned to the step,
     * the handles of a range slider cannot pass each other. No callbacks are fired.
     * @param index The index of the handle.
     * @param value The new value, a number or a numeric string.
     */
    private setHandleValue(index: number, value: number | string): void {
        this.values[index] = this.align(value);
        // the handles of a range slider can't pass each other
        if (this.values.length === 2) {
            this.values[index] = index === 0 ? Math.min(this.values[0]!, this.values[1]!) : Math.max(this.values[0]!, this.values[1]!);
        }
        this.updateUI(true);
    }

    /**
     * Finds the values of all slider handles.
     * @returns A copy of the list with the values of all handles, one value for a slider with one handle.
     */
    getValues(): number[] {
        return this.values.slice();
    }

    /**
     * Sets the values of all slider handles. Each value is trimmed to min / max and aligned to the step, and the
     * handles move there. The inputs, the display and the callbacks are not updated.
     * @param values The new values for the handles, in the order of the handles; surplus values are ignored.
     */
    setValues(values: (number | string)[]): void {
        this.values = this.values.map((_, i) => this.align(values[i]));
        this.updateUI(true);
    }

    /** Enables this slider widget so that the user can move the slider. */
    override enable(): void {
        this.setDisabled(false);
    }

    /** Disables this slider widget so that the user cannot move the slider anymore. */
    override disable(): void {
        this.setDisabled(true);
    }

    /**
     * Enables or disables the slider: updates the configuration, the style class `p-state-disabled` and the
     * `aria-disabled` attribute of the handles.
     * @param disabled `true` to disable, `false` to enable the slider.
     */
    private setDisabled(disabled: boolean): void {
        this.cfg.disabled = disabled;
        this.jq.toggleClass('p-state-disabled', disabled);
        this.handles.attr('aria-disabled', String(disabled));
    }
}
