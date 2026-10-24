/**
 * The configuration for the {@link Knob} widget.
 */
export interface KnobCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether only a cursor is drawn at the value instead of the filled arc. */
    cursor: boolean;
    /** Whether the knob is disabled. */
    disabled: boolean;
    /** Template of the label, e.g. `{value}%`. */
    labelTemplate: string;
    /** Line cap of the arc. */
    lineCap: "butt" | "round";
    /** Maximum value. */
    max: number;
    /** Minimum value. */
    min: number;
    /** Called when the value was changed by the user or via {@link Knob.setValue}. */
    onchange: (this: Knob, value: number) => void;
    /** Whether the value is shown as a label in the center. */
    showLabel: boolean;
    /** Step size of the value. */
    step: number;
    /** Thickness of the arc as a fraction of the radius, between 0 and 1. */
    thickness: number;
}

/** Thickness of the arc as a fraction of the radius, when the `thickness` attribute is not set. */
const DEFAULT_THICKNESS = 0.35;
/** Half of the length of the `cursor`, in percent of the circumference (0.3 radians). */
const CURSOR_EXTENT = 0.3 / (2 * Math.PI) * 100;
/** The namespace of SVG elements. */
const SVG_NS = 'http://www.w3.org/2000/svg';

/**
 * __PrimeFaces Knob Widget__
 *
 * Knob is an input component to insert numeric values in a range. The value is changed by dragging on the arc (mouse,
 * pen or touch), with the mouse wheel or with the keyboard, and shown as a label in the center. The knob is an SVG
 * styled with the `--p-knob-*` tokens.
 */
export class Knob<Cfg extends KnobCfg = KnobCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The hidden input with the submitted value. */
    input: JQuery = $();
    /** The SVG element the knob is drawn with, it is focusable and has the role `slider`. */
    svg: JQuery<SVGSVGElement> = $();
    /** Minimum value. */
    min: number = 0;
    /** Maximum value. */
    max: number = 100;
    /** Step size of the value. */
    step: number = 1;

    /** The value, as last released (submitted). */
    private value: number = 0;
    /** The value shown while it is changed, e.g. while dragging. */
    private currentValue: number = 0;
    /** Whether the knob can't be changed. */
    private disabled: boolean = false;
    /** The circle of the filled part of the arc (or the cursor). */
    private valueCircle: SVGCircleElement | null = null;
    /** The label in the center, `null` if the label is hidden. */
    private label: SVGTextElement | null = null;
    /** The value before a drag started, restored when the drag is cancelled with Escape. */
    private valueBeforeDrag: number | null = null;
    /** Timer that releases the value after the mouse wheel stopped. */
    private wheelTimer: number | null = null;

    /**
     * Initializes the knob: creates the SVG in the container rendered by the server and binds the events.
     * @param cfg The configuration of the knob.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_hidden');
        this.min = this.cfg.min ?? 0;
        this.max = this.cfg.max ?? 100;
        this.step = this.cfg.step || 1;
        this.value = parseFloat(String(this.input.val() ?? ''));
        if (isNaN(this.value) || this.value < this.min || this.value > this.max) {
            this.value = this.min;
        }
        this.currentValue = this.value;

        this.render();
        this.bindEvents();
        this.setDisabled(!!this.cfg.disabled);
        this.change(this.currentValue);
    }

    /**
     * Creates the SVG: the range as full circle, the value as circle drawn from the top (with a dash), and the label.
     */
    private render(): void {
        const thickness = Math.max(Math.min(this.cfg.thickness || DEFAULT_THICKNESS, 1), 0.01);
        // the viewBox is 100 x 100, the arc fills it from the edge to the inside
        const strokeWidth = 50 * thickness;
        const circle = (styleClass: string): SVGCircleElement => this.createSvgElement('circle', {
            class: styleClass,
            cx: 50,
            cy: 50,
            r: 50 - strokeWidth / 2,
            'stroke-width': strokeWidth
        });

        const svg = this.createSvgElement('svg', {
            class: 'ui-knob-svg',
            viewBox: '0 0 100 100',
            role: 'slider',
            'aria-valuemin': this.min,
            'aria-valuemax': this.max
        });
        svg.appendChild(circle('ui-knob-range'));
        // pathLength 100: the dash is the value in percent; rotated, so it starts at the top and runs clockwise
        this.valueCircle = circle('ui-knob-value');
        this.valueCircle.setAttribute('pathLength', '100');
        this.valueCircle.setAttribute('transform', 'rotate(-90 50 50)');
        this.valueCircle.setAttribute('stroke-linecap', this.cfg.lineCap === 'round' ? 'round' : 'butt');
        svg.appendChild(this.valueCircle);

        if (this.cfg.showLabel !== false) {
            // the font size fits the longest of min and max into the circle
            const digits = Math.max(String(Math.abs(this.max)).length, String(Math.abs(this.min)).length, 2) + 2;
            this.label = this.createSvgElement('text', {
                class: 'ui-knob-label',
                x: 50,
                y: 50,
                'font-size': Math.floor(100 / digits),
                'aria-hidden': 'true'
            });
            svg.appendChild(this.label);
        }

        this.svg = $(svg);
        this.jq.append(svg);
    }

    /**
     * Creates an SVG element.
     * @typeParam K The name of the element.
     * @param name The name of the element, e.g. `circle`.
     * @param attributes The attributes to set.
     * @returns The new element.
     */
    private createSvgElement<K extends keyof SVGElementTagNameMap>(name: K, attributes: Record<string, string | number>): SVGElementTagNameMap[K] {
        const element = document.createElementNS(SVG_NS, name);
        for (const attribute in attributes) {
            element.setAttribute(attribute, String(attributes[attribute]));
        }
        return element;
    }

    /**
     * Binds dragging on the arc, the mouse wheel and the keyboard.
     */
    private bindEvents(): void {
        this.svg
            .on('pointerdown.knob', (e) => this.onPointerDown(e))
            .on('wheel.knob', (e) => this.onWheel(e))
            .on('keydown.knob', (e) => this.onKeyDown(e));
    }

    /**
     * Starts a drag: the value follows the pointer until it is released, Escape cancels the drag.
     * @param e The pointerdown event.
     */
    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as PointerEvent | undefined;
        if (this.disabled || !event || (event.pointerType === 'mouse' && event.button !== 0)) {
            return;
        }
        e.preventDefault();
        this.svg[0]!.focus();
        this.svg[0]!.setPointerCapture(event.pointerId);
        this.valueBeforeDrag = this.value;
        this.change(this.valueAt(event));

        this.svg
            .on('pointermove.knob-drag', (ev) => this.change(this.valueAt(ev.originalEvent as PointerEvent)))
            .on('pointerup.knob-drag pointercancel.knob-drag', () => this.endDrag(true));
        $(document).on('keydown.knob-drag' + this.id, (ev) => {
            if (ev.key === 'Escape') {
                this.endDrag(false);
            }
        });
    }

    /**
     * Ends a drag.
     * @param release `true` to release the dragged value, `false` to restore the value from before the drag.
     */
    private endDrag(release: boolean): void {
        this.svg.off('.knob-drag');
        $(document).off('keydown.knob-drag' + this.id);
        const before = this.valueBeforeDrag;
        this.valueBeforeDrag = null;
        if (release || before === null) {
            this.release(this.currentValue);
        }
        else {
            this.change(before);
        }
    }

    /**
     * Changes the value by one step per wheel event, the value is released when the wheel stopped (#4098: the page
     * does not scroll).
     * @param e The wheel event.
     */
    private onWheel(e: JQuery.TriggeredEvent): void {
        if (this.disabled) {
            return;
        }
        e.preventDefault();
        e.stopImmediatePropagation();
        const delta = (e.originalEvent as WheelEvent).deltaY;
        if (!delta) {
            return;
        }
        // wheel up (negative deltaY) increases the value
        this.change(this.currentValue + (delta < 0 ? this.step : -this.step));
        if (this.wheelTimer) {
            clearTimeout(this.wheelTimer);
        }
        this.wheelTimer = window.setTimeout(() => {
            this.wheelTimer = null;
            this.release(this.currentValue);
        }, 100);
    }

    /**
     * Changes the value with the keyboard: the arrow keys by one step, Page Up / Page Down by a tenth of the range,
     * Home / End to the minimum / maximum.
     * @param e The keydown event.
     */
    private onKeyDown(e: JQuery.TriggeredEvent): void {
        if (this.disabled) {
            return;
        }
        const page = Math.max(this.step, Math.round((this.max - this.min) / 10 / this.step) * this.step);
        const values: Record<string, number> = {
            ArrowUp: this.currentValue + this.step,
            ArrowRight: this.currentValue + this.step,
            ArrowDown: this.currentValue - this.step,
            ArrowLeft: this.currentValue - this.step,
            PageUp: this.currentValue + page,
            PageDown: this.currentValue - page,
            Home: this.min,
            End: this.max
        };
        const value = values[e.key ?? ''];
        if (value !== undefined) {
            e.preventDefault();
            this.release(value);
        }
    }

    /**
     * Computes the value at a pointer position: the angle from the top, clockwise, maps the range to a full circle.
     * @param event The pointer event.
     * @returns The value at the pointer, aligned to the step and within min / max.
     */
    private valueAt(event: PointerEvent): number {
        // the circle is centered in the SVG, also if it is not a square
        const rect = this.svg[0]!.getBoundingClientRect();
        let angle = Math.atan2(event.clientX - rect.left - rect.width / 2, -(event.clientY - rect.top - rect.height / 2));
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return this.align(angle * (this.max - this.min) / (2 * Math.PI) + this.min);
    }

    /**
     * Aligns a value to the step (relative to 0, like the former jQuery Knob) and keeps it within min / max.
     * @param value The value to align.
     * @returns The aligned value, rounded to two decimals.
     */
    private align(value: number): number {
        const aligned = Math.round(Math.round(value / this.step) * this.step * 100) / 100;
        return Math.max(Math.min(aligned, this.max), this.min);
    }

    /**
     * Shows a value while it is changed, without releasing it: draws the arc (or the cursor) and updates the label.
     * @param value The new value.
     */
    private change(value: number): void {
        this.currentValue = this.align(value);
        const text = this.format(this.currentValue);
        this.svg.attr({ 'aria-valuenow': this.currentValue, 'aria-valuetext': text });
        if (this.label) {
            this.label.textContent = text;
        }

        // the dash pattern repeats every 100 (the circumference), so a cursor at the top wraps around
        const percent = (this.currentValue - this.min) * 100 / ((this.max - this.min) || 1);
        if (this.cfg.cursor) {
            this.valueCircle?.setAttribute('stroke-dasharray', `${2 * CURSOR_EXTENT} ${100 - 2 * CURSOR_EXTENT}`);
            this.valueCircle?.setAttribute('stroke-dashoffset', String((CURSOR_EXTENT - percent + 100) % 100));
        }
        else {
            this.valueCircle?.setAttribute('stroke-dasharray', `${percent} ${100 - percent}`);
        }
    }

    /**
     * Releases a value: shows it, stores it in the hidden input and, if it changed, calls the `onchange` callback and
     * the `change` behavior.
     * @param value The new value.
     */
    private release(value: number): void {
        this.change(value);
        if (this.currentValue === this.value) {
            return;
        }
        this.value = this.currentValue;
        this.input.val(this.value);

        this.cfg.onchange?.call(this, this.value);
        if (this.hasBehavior('change')) {
            this.callBehavior('change', {
                params: [{ name: this.id + '_hidden', value: this.value }]
            });
        }
    }

    /**
     * Formats a value with the label template.
     * @param value The value to format.
     * @returns The label, e.g. `35%` for the template `{value}%`.
     */
    private format(value: number): string {
        return (this.cfg.labelTemplate || '{value}').replace('{value}', String(value));
    }

    /**
     * Enables or disables the knob: a disabled knob can't be changed, is not focusable and its value is not submitted.
     * @param disabled Whether the knob is disabled.
     */
    private setDisabled(disabled: boolean): void {
        this.disabled = disabled;
        this.jq.toggleClass('ui-state-disabled', disabled);
        this.svg.attr({ tabindex: disabled ? null : 0, 'aria-disabled': disabled ? 'true' : null });
        this.input.prop('disabled', disabled);
    }

    /**
     * Sets the value of this knob, like the user changed it: calls the `onchange` callback and the `change` behavior
     * if the value changed.
     * @param value The value to set, aligned to the step and kept within min / max.
     */
    setValue(value: number): void {
        this.release(Number(value));
    }

    /**
     * Retrieves the current value of this knob.
     * @returns The current numerical value of this knob.
     */
    getValue(): number {
        return this.currentValue;
    }

    /**
     * Increments the value of this knob by the step size, up to the maximum.
     */
    increment(): void {
        this.setValue(this.currentValue + this.step);
    }

    /**
     * Decrements the value of this knob by the step size, down to the minimum.
     */
    decrement(): void {
        this.setValue(this.currentValue - this.step);
    }

    /**
     * Disables the knob so that the user cannot change the value anymore, the value is not submitted.
     */
    override disable(): void {
        this.setDisabled(true);
    }

    /**
     * Enables the knob so that the user can change the value.
     */
    override enable(): void {
        this.setDisabled(false);
    }
}
