/*
 * Drag and drop, sorting and resizing with pointer events, replacing the jQuery UI interactions. The options, the
 * callbacks, the `ui` arguments, the events and the CSS classes are compatible with jQuery UI as far as PrimeFaces uses
 * them.
 */

export type Coordinates = { left: number; top: number };
export type Size = { width: number; height: number };
export type Callback = ((this: HTMLElement, event: JQuery.TriggeredEvent, ui: any) => unknown) | null | undefined;

export interface InteractionOptions {
    disabled: boolean;
    /** Minimum distance in pixels the pointer has to move before the interaction starts. */
    distance: number;
    [option: string]: unknown;
}

const HANDLED = '__pfInteractionHandled';

/** Removes `undefined` values, so they don't override the defaults. */
function defined<T extends object>(options: Partial<T> | undefined): Partial<T> {
    const result: Record<string, unknown> = {};
    const source = (options ?? {}) as Record<string, unknown>;
    for (const key of Object.keys(source)) {
        if (source[key] !== undefined) {
            result[key] = source[key];
        }
    }
    return result as Partial<T>;
}

/** The pointer position of an event in page coordinates. */
export function pageX(event: JQuery.TriggeredEvent): number {
    return (event.clientX ?? 0) + window.scrollX;
}

export function pageY(event: JQuery.TriggeredEvent): number {
    return (event.clientY ?? 0) + window.scrollY;
}

export function num(value: string | undefined): number {
    return parseFloat(value ?? '') || 0;
}

export function offsetOf(jq: JQuery): Coordinates {
    return jq.offset() ?? { left: 0, top: 0 };
}

/** The current CSS `left` / `top` of an element, as numbers. */
export function cssPosition(jq: JQuery): Coordinates {
    return { left: num(jq.css('left')), top: num(jq.css('top')) };
}

/** The bounds of the content box of an element in page coordinates: [left, top, right, bottom]. */
export function contentBox(jq: JQuery): [number, number, number, number] {
    const el = jq[0] as HTMLElement;
    const offset = offsetOf(jq);
    const left = offset.left + num(jq.css('borderLeftWidth')) + num(jq.css('paddingLeft'));
    const top = offset.top + num(jq.css('borderTopWidth')) + num(jq.css('paddingTop'));
    return [left, top, left + (jq.width() ?? el.clientWidth), top + (jq.height() ?? el.clientHeight)];
}

/**
 * Base of the interactions: options, `enable` / `disable` / `destroy`, the callbacks and events, and the pointer
 * handling that starts the interaction after the pointer moved the minimum distance.
 */
export abstract class Interaction<O extends InteractionOptions> {
    readonly element: JQuery<HTMLElement>;
    options: O;

    /** Name of the interaction, e.g. `draggable`. */
    readonly name: string;
    protected readonly eventPrefix: string;
    protected readonly namespace: string;
    private startEvent: JQuery.TriggeredEvent | null = null;
    private readonly preventNativeDrag = (e: Event): void => e.preventDefault();
    private started: boolean = false;

    protected constructor(element: HTMLElement, name: string, eventPrefix: string, defaults: O, options?: Partial<O>) {
        this.element = $(element);
        this.name = name;
        this.eventPrefix = eventPrefix;
        this.options = { ...defaults, ...defined(options) };
        this.namespace = '.pf' + name + Math.random().toString(36).slice(2);
        // destroyed with the element, like a jQuery UI widget
        this.element.on('remove' + this.namespace, (e) => {
            if (e.target === element) {
                this.destroy();
            }
        });
    }

    /** Gets all options, gets an option or sets one or more options. */
    option(key?: string | Partial<O>, value?: unknown): unknown {
        if (key === undefined) {
            return this.options;
        }
        if (typeof key === 'string') {
            if (value === undefined) {
                return this.options[key];
            }
            this.setOption(key, value);
            return undefined;
        }
        const values = key as Record<string, unknown>;
        for (const k of Object.keys(values)) {
            this.setOption(k, values[k]);
        }
        return undefined;
    }

    enable(): void {
        this.setOption('disabled', false);
    }

    disable(): void {
        this.setOption('disabled', true);
    }

    destroy(): void {
        this.cleanupPointer();
        this.element.off(this.namespace).removeClass('ui-' + this.name + ' ui-' + this.name + '-disabled');
        this.element.removeData('pf-' + this.name);
    }

    protected setOption(key: string, value: unknown): void {
        (this.options as Record<string, unknown>)[key] = value;
        if (key === 'disabled') {
            this.element.toggleClass('ui-' + this.name + '-disabled', !!value);
        }
    }

    /**
     * Calls the callback of the option and triggers the event (e.g. `dragstart`) on the element, both with the
     * `ui` argument. The event has the properties of the native event, e.g. `pageX`.
     * @return `false` if the callback returned `false` or the default of the event was prevented.
     */
    trigger(type: string, event: JQuery.TriggeredEvent | null, ui: unknown): boolean {
        const name = (type === this.eventPrefix ? type : this.eventPrefix + type).toLowerCase();
        const jqEvent = $.Event(name) as JQuery.TriggeredEvent;
        if (event?.originalEvent) {
            // jQuery reads pageX, clientX, ... from the original event
            jqEvent.originalEvent = event.originalEvent;
        }
        jqEvent.target = this.element[0]!;

        // a "resize" event would bubble to the window
        if (name === 'resize') {
            this.element.triggerHandler(jqEvent, [ui]);
        }
        else {
            this.element.trigger(jqEvent, [ui]);
        }

        const callback = this.options[type] as Callback;
        const result = typeof callback === 'function' ? callback.call(this.element[0]!, jqEvent, ui) : undefined;
        return result !== false && !jqEvent.isDefaultPrevented();
    }

    // ===============
    // === Pointer ===
    // ===============

    /** Starts listening for pointerdown on the element (or another target). */
    protected bindPointer(target: JQuery = this.element): void {
        target.on('pointerdown' + this.namespace, (e) => this.onPointerDown(e));
    }

    /** Whether the interaction can start for this pointerdown. */
    protected abstract capture(event: JQuery.TriggeredEvent): boolean;

    /** Starts the interaction, `false` to cancel. */
    protected abstract start(event: JQuery.TriggeredEvent): boolean;

    protected abstract move(event: JQuery.TriggeredEvent): void;

    protected abstract stop(event: JQuery.TriggeredEvent): void;

    /** Whether the target of the event matches the `cancel` option. */
    protected isCancel(event: JQuery.TriggeredEvent, cancel: unknown): boolean {
        return typeof cancel === 'string' && !!cancel && $(event.target).closest(cancel).length > 0;
    }

    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as (PointerEvent & Record<string, unknown>) | undefined;
        if (!event || event[HANDLED] || (event.pointerType === 'mouse' && event.button !== 0) || this.options.disabled) {
            return;
        }
        if (this.started) {
            this.end(e);
        }
        if (!this.capture(e)) {
            return;
        }
        // an inner interaction takes precedence over an outer one
        event[HANDLED] = true;

        this.startEvent = e;
        const doc = $(document);
        doc.on('pointermove' + this.namespace, (ev) => this.onPointerMove(ev))
            .on('pointerup' + this.namespace, (ev) => this.end(ev))
            .on('pointercancel' + this.namespace, (ev) => this.started ? this.abort(ev) : this.end(ev))
            // no text selection while dragging
            .on('selectstart' + this.namespace, (ev) => ev.preventDefault());
        // no native drag of images and links, it would cancel the pointer events (native listener, the jQuery
        // "dragstart" event of the draggable bubbles to the document too)
        document.addEventListener('dragstart', this.preventNativeDrag, true);
    }

    /** The browser took over the pointer (e.g. for scrolling), the interaction ends without a drop. */
    protected abort(e: JQuery.TriggeredEvent): void {
        this.end(e);
    }

    private onPointerMove(e: JQuery.TriggeredEvent): void {
        if (!this.started) {
            const start = this.startEvent!;
            const distance = Math.max(Math.abs((start.clientX ?? 0) - (e.clientX ?? 0)), Math.abs((start.clientY ?? 0) - (e.clientY ?? 0)));
            if (distance < (this.options.distance ?? 1)) {
                return;
            }
            window.getSelection()?.removeAllRanges();
            this.started = this.start(start);
            if (!this.started) {
                this.cleanupPointer();
                return;
            }
        }
        e.preventDefault();
        this.move(e);
    }

    /** Ends the interaction, e.g. on pointerup. */
    protected end(e: JQuery.TriggeredEvent): void {
        const started = this.started;
        const target = this.startEvent?.target;
        this.cleanupPointer();
        if (started) {
            this.stop(e);
            // no click after a drag
            if (target instanceof Element) {
                const preventClick = (ev: Event) => {
                    ev.stopImmediatePropagation();
                    ev.preventDefault();
                };
                target.addEventListener('click', preventClick, { capture: true, once: true });
                setTimeout(() => target.removeEventListener('click', preventClick, { capture: true }), 0);
            }
        }
    }

    private cleanupPointer(): void {
        $(document).off(this.namespace);
        document.removeEventListener('dragstart', this.preventNativeDrag, true);
        this.started = false;
        this.startEvent = null;
    }

    /**
     * Scrolls the closest scrollable parent of the element (or the window) when the pointer is near its edge, like the
     * `scroll` option of jQuery UI.
     */
    protected autoScroll(event: JQuery.TriggeredEvent, from: HTMLElement | null = this.element[0]!.parentElement, sensitivity: number = 20, speed: number = 20): void {
        const x = event.clientX ?? 0;
        const y = event.clientY ?? 0;
        let parent: HTMLElement | null = from;
        while (parent && parent !== document.body) {
            const style = getComputedStyle(parent);
            if (/(auto|scroll)/.test(style.overflow + style.overflowY + style.overflowX)
                    && (parent.scrollHeight > parent.clientHeight || parent.scrollWidth > parent.clientWidth)) {
                break;
            }
            parent = parent.parentElement;
        }

        const scroll = (rect: { top: number; bottom: number; left: number; right: number }, by: (dx: number, dy: number) => void) => {
            const dy = y - rect.top < sensitivity ? -speed : rect.bottom - y < sensitivity ? speed : 0;
            const dx = x - rect.left < sensitivity ? -speed : rect.right - x < sensitivity ? speed : 0;
            if (dx || dy) {
                by(dx, dy);
            }
        };
        if (parent && parent !== document.body) {
            const p = parent;
            scroll(p.getBoundingClientRect(), (dx, dy) => p.scrollBy(dx, dy));
        }
        else {
            scroll({ top: 0, left: 0, bottom: window.innerHeight, right: window.innerWidth }, (dx, dy) => window.scrollBy(dx, dy));
        }
    }

    /** Whether the interaction is in progress. */
    isActive(): boolean {
        return this.started;
    }
}
