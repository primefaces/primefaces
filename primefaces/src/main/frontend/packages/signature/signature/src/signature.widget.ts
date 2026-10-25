/**
 * The configuration for the {@link Signature} widget.
 */
export interface SignatureCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Label of the canvas for screen readers. */
    ariaLabel: string;
    /** ID of the label of the canvas. */
    ariaLabelledBy: string;
    /** Background color of the canvas. */
    background: string;
    /** Whether the signature is also stored as a PNG data URL. */
    base64: boolean;
    /** Color of the lines. */
    color: string;
    /** Font of a typed signature. */
    fontFamily: string;
    /** Font size of a typed signature in pixels. */
    fontSize: number;
    /** Whether to draw a guideline. */
    guideline: boolean;
    /** Color of the guideline. */
    guidelineColor: string;
    /** Indent of the guideline from the left and right edge in pixels. */
    guidelineIndent: number;
    /** Offset of the guideline from the bottom in pixels. */
    guidelineOffset: number;
    /** Called when the signature changes. */
    onchange: (this: Signature) => void;
    /** Whether the signature is readonly. */
    readonly: boolean;
    /** Tabindex of the canvas. */
    tabindex: string;
    /** Thickness of the lines in pixels. */
    thickness: number;
}

/** A point of a line as `[x, y]`, in pixels relative to the top left corner of the canvas. */
type Point = [number, number];

/**
 * __PrimeFaces Signature Widget__
 *
 * Signature is used to draw a signature as an input, with the mouse, a pen, by touch or by typing a text.
 */
export class Signature<Cfg extends SignatureCfg = SignatureCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The canvas the signature is drawn on. */
    canvas: JQuery<HTMLCanvasElement> = $();
    /** The hidden input with the signature lines as JSON. */
    inputJson: JQuery = $();
    /** The hidden input with the typed text. */
    inputText: JQuery = $();
    /** The hidden input with the PNG data URL, if `base64` is enabled. */
    inputBase64: JQuery = $();

    /** The 2D rendering context of the canvas, `null` after the widget was destroyed. */
    private ctx: CanvasRenderingContext2D | null = null;
    /** The drawn lines, each a list of points; empty for a typed signature or a drawn image. */
    private lines: Point[][] = [];
    /** The line currently drawn with the pointer, `null` while not drawing. */
    private currentLine: Point[] | null = null;

    /**
     * Initializes the signature: renders the canvas, binds the events and draws the initial value, from the JSON
     * lines or else from the typed text.
     * @param cfg The configuration of the signature.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.inputJson = this.jq.children(this.jqId + '_value');
        this.inputText = this.jq.children(this.jqId + '_text');
        this.inputBase64 = this.cfg.base64 ? this.jq.children(this.jqId + '_base64') : $();

        this.render();

        if (this.cfg.readonly) {
            this.disable();
        }

        this.bindEvents();

        // draw the signature from the JSON value, or else from the typed text
        const json = String(this.inputJson.val() ?? '');
        if (json) {
            this.draw(json);
            if (this.lines.length) {
                return;
            }
        }
        const text = String(this.inputText.val() ?? '');
        if (text) {
            this.createSignatureFromText(text);
        }
    }

    /** Removes the canvas and releases its rendering context. */
    override destroy(): void {
        super.destroy();
        this.canvas.remove();
        this.ctx = null;
    }

    /**
     * Renders the client-side parts of this widget: a canvas with the size of the widget and its accessibility
     * attributes, filled with the background and the guideline. Replaces the canvas of a previous rendering.
     */
    private render(): void {
        this.jq.children('canvas').remove();
        this.jq.addClass('ui-signature');

        this.canvas = $('<canvas></canvas>') as JQuery<HTMLCanvasElement>;
        this.canvas.attr({
            width: this.jq.width() ?? 0,
            height: this.jq.height() ?? 0,
            id: this.id + '_canvas',
            role: 'img',
            tabindex: this.cfg.readonly ? '-1' : (this.cfg.tabindex || '0'),
            'aria-label': this.cfg.ariaLabel || 'Sign here',
            'aria-labelledby': this.cfg.ariaLabelledBy ?? null
        }).appendTo(this.jq);

        this.ctx = this.canvas[0]!.getContext('2d');
        this.clearCanvas();
    }

    /**
     * Binds event handlers to the signature canvas: hover and focus styles, drawing with the pointer and typing with
     * the keyboard. A click on the label focuses the canvas. Nothing is bound when the signature is readonly.
     */
    private bindEvents(): void {
        if (this.cfg.readonly) {
            return;
        }

        // the label is not part of this widget, so it can survive an update of this widget
        if (this.cfg.ariaLabelledBy) {
            const label = $(PrimeFaces.escapeClientId(this.cfg.ariaLabelledBy));
            const labelClickEvent = 'click.signature.' + this.id;
            label.off(labelClickEvent).on(labelClickEvent, () => this.canvas.trigger('focus'));
            this.addDestroyListener(() => label.off(labelClickEvent));
        }

        this.canvas.off('.signature')
            .on('mouseenter.signature', () => this.jq.addClass('ui-state-hover'))
            .on('mouseleave.signature', () => this.jq.removeClass('ui-state-hover'))
            .on('focus.signature', () => this.jq.addClass('ui-state-focus'))
            .on('blur.signature', () => {
                this.jq.removeClass('ui-state-hover ui-state-focus');
                this.updateBase64();
            })
            .on('pointerdown.signature', (e) => this.onPointerDown(e))
            // no text selection while drawing; not on pointerdown, that would suppress the mousedown event the overlays
            // of other widgets listen to on the document to hide
            .on('mousedown.signature', (e) => e.preventDefault())
            .on('pointermove.signature', (e) => this.onPointerMove(e))
            .on('pointerup.signature pointercancel.signature', (e) => this.onPointerUp(e))
            .on('keydown.signature', (e) => this.onKeyDown(e));
    }

    /**
     * Checks whether the user cannot change the signature.
     * @returns `true` if the signature is readonly or disabled.
     */
    private isDisabled(): boolean {
        return !!this.cfg.readonly || this.jq.hasClass('ui-state-disabled');
    }

    /**
     * Determines the position of a pointer event on the canvas.
     * @param e The pointer event.
     * @returns The point relative to the canvas, rounded to two decimals.
     */
    private pointAt(e: JQuery.TriggeredEvent): Point {
        const event = e.originalEvent as PointerEvent;
        const rect = this.canvas[0]!.getBoundingClientRect();
        const round = (value: number) => Math.round(value * 100) / 100;
        return [round(event.clientX - rect.left), round(event.clientY - rect.top)];
    }

    /**
     * Starts a new line at the pointer when the canvas is pressed with the primary mouse button, a pen or a touch.
     * Focuses the canvas and captures the pointer, so the line continues outside of the canvas.
     * @param e The `pointerdown` event.
     */
    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as PointerEvent;
        if (this.isDisabled() || (event.pointerType === 'mouse' && event.button !== 0)) {
            return;
        }
        this.canvas.trigger('focus');
        this.canvas[0]!.setPointerCapture(event.pointerId);
        this.currentLine = [this.pointAt(e)];
        this.lines.push(this.currentLine);
    }

    /**
     * Extends the current line to the pointer while drawing.
     * @param e The `pointermove` event.
     */
    private onPointerMove(e: JQuery.TriggeredEvent): void {
        if (this.currentLine) {
            this.lineTo(this.pointAt(e));
        }
    }

    /**
     * Ends the current line, draws a dot for a single click and stores the changed signature.
     * @param e The `pointerup` or `pointercancel` event.
     */
    private onPointerUp(e: JQuery.TriggeredEvent): void {
        if (!this.currentLine) {
            return;
        }
        // a single click draws a dot
        if (this.currentLine.length === 1) {
            const [x, y] = this.currentLine[0]!;
            this.lineTo([x, y + this.thickness()]);
        }
        this.currentLine = null;
        this.changed();
        e.preventDefault();
    }

    /**
     * Adds a point to the current line and draws the segment from the previous point to it.
     * @param point The new point of the line.
     */
    private lineTo(point: Point): void {
        const line = this.currentLine!;
        const last = line[line.length - 1]!;
        line.push(point);
        if (this.ctx) {
            this.ctx.beginPath();
            this.ctx.moveTo(last[0], last[1]);
            this.ctx.lineTo(point[0], point[1]);
            this.ctx.stroke();
        }
    }

    /**
     * Types the signature with the keyboard: printable keys append to the text, `Backspace` / `Delete` remove the
     * last character and `Escape` clears the signature. The text is stored and drawn as signature.
     * @param e The `keydown` event of the canvas.
     */
    private onKeyDown(e: JQuery.TriggeredEvent): void {
        if (this.isDisabled()) {
            return;
        }
        let text = String(this.inputText.val() ?? '');
        switch ((e.originalEvent as KeyboardEvent | undefined)?.code) {
            case 'Backspace':
            case 'Delete':
                if (text.length > 1) {
                    text = text.slice(0, -1);
                }
                else {
                    text = '';
                    this.clear();
                }
                break;
            case 'Escape':
                this.clear();
                break;
            default:
                if (!PrimeFaces.utils.isPrintableKey(e)) {
                    return;
                }
                text += e.key;
        }

        e.preventDefault();
        if (text) {
            this.inputText.val(text);
            this.createSignatureFromText(text);
        }
    }

    /**
     * Determines the thickness of the lines.
     * @returns The configured thickness in pixels, `2` by default.
     */
    private thickness(): number {
        return this.cfg.thickness ?? 2;
    }

    /**
     * Fills the canvas with the background and draws the guideline. Also sets up the line style and forgets the
     * drawn lines, but does not update the inputs.
     */
    private clearCanvas(): void {
        const ctx = this.ctx;
        if (!ctx) {
            return;
        }
        const width = this.canvas[0]!.width;
        const height = this.canvas[0]!.height;

        ctx.fillStyle = this.cfg.background || '#fff';
        ctx.strokeStyle = this.cfg.color || '#000';
        ctx.lineWidth = this.thickness();
        ctx.lineCap = 'round';
        ctx.lineJoin = 'round';
        ctx.clearRect(0, 0, width, height);
        ctx.fillRect(0, 0, width, height);

        if (this.cfg.guideline) {
            const indent = this.cfg.guidelineIndent ?? 10;
            const y = height - (this.cfg.guidelineOffset ?? 25);
            ctx.save();
            ctx.strokeStyle = this.cfg.guidelineColor || '#a0a0a0';
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(indent, y);
            ctx.lineTo(width - indent, y);
            ctx.stroke();
            ctx.restore();
        }
        this.lines = [];
    }

    /** Stores the lines (and the PNG data URL with `base64`) in the inputs and calls the `onchange` callback. */
    private changed(): void {
        this.inputJson.val(this.toJSON());
        this.updateBase64();
        this.cfg.onchange?.call(this);
    }

    /**
     * Updates the base64 value of the signature widget with the canvas as PNG data URL, if `base64` is enabled.
     * @param clear Whether to clear the base64 value.
     */
    private updateBase64(clear: boolean = false): void {
        if (this.cfg.base64) {
            this.inputBase64.val(clear ? '' : this.canvas[0]!.toDataURL());
        }
    }

    /**
     * Converts the signature to a JSON string for transferral / storage, the format stored in the hidden input and
     * accepted by {@link draw}. A typed signature or a drawn image has no lines.
     * @returns The drawn lines as JSON, e.g. `{"lines":[[[10,20],[11,22]]]}`.
     */
    toJSON(): string {
        return JSON.stringify({ lines: this.lines });
    }

    /**
     * Determines whether no lines are drawn. Note that a typed signature or a drawn image has no lines either.
     * @returns `true` if no lines are drawn, `false` otherwise.
     */
    isEmpty(): boolean {
        return this.lines.length === 0;
    }

    /**
     * Clears this signature widget, removing all drawn lines and the typed text, and empties the hidden inputs. Calls
     * the `onchange` callback.
     */
    clear(): void {
        this.clearCanvas();
        this.changed();
        this.inputJson.val('');
        this.inputText.val('');
        this.updateBase64(true);
    }

    /**
     * Draws a signature, given as JSON lines (as string or object), as SVG with polylines or as a `data:` URL of an
     * image. An image does not restore the lines. Replaces the current signature, stores it in the inputs and calls
     * the `onchange` callback (for an image once it is loaded).
     * @param value The signature data to draw: JSON lines as obtained from {@link toJSON} (as string or object), an
     * SVG string with polylines or a `data:` URL. Nothing is drawn for an empty value.
     */
    draw(value: string | { lines: Point[][] } | null | undefined): void {
        if (!value) {
            return;
        }
        this.clearCanvas();

        if (typeof value === 'string' && value.indexOf('data:') === 0) {
            this.lines = [];
            const image = new Image();
            image.onload = () => {
                this.ctx?.drawImage(image, 0, 0, image.width, image.height);
                this.changed();
            };
            image.src = value;
            return;
        }

        if (typeof value === 'string' && value.indexOf('<svg') > -1) {
            // parsed in an inert document: the value may come from a request, markup like <img onerror> must not run
            const svg = new DOMParser().parseFromString(value, 'text/html');
            this.lines = Array.from(svg.querySelectorAll('polyline'), (polyline) => (polyline.getAttribute('points') ?? '')
                .split(' ').map((point) => point.split(',').map(parseFloat) as Point));
        }
        else {
            const json = typeof value === 'string' ? JSON.parse(value) as { lines?: Point[][] } : value;
            this.lines = json.lines ?? [];
        }

        for (const line of this.lines) {
            this.ctx?.beginPath();
            line.forEach(([x, y], i) => i === 0 ? this.ctx?.moveTo(x, y) : this.ctx?.lineTo(x, y));
            this.ctx?.stroke();
        }
        this.changed();
    }

    /**
     * Draws the text as signature, replacing the current signature, with the configured `fontFamily`, `fontSize` and
     * `color`. Stores the signature in the inputs and calls the `onchange` callback; the JSON value has no lines.
     * @param text The text to draw as signature.
     */
    createSignatureFromText(text: string): void {
        const canvas = this.canvas[0];
        const ctx = this.ctx;
        if (!canvas || !ctx) {
            return;
        }
        const fontSize = this.cfg.fontSize || 40;

        this.clearCanvas();
        ctx.save();
        ctx.fillStyle = this.cfg.color || 'black';
        ctx.font = fontSize + 'px ' + (this.cfg.fontFamily || 'Brush Script MT, cursive');
        ctx.fillText(text, 10, (canvas.height + fontSize) / 2);
        ctx.restore();
        this.lines = [];
        this.changed();
    }

    /** Disables this input so that the user cannot draw or type a signature anymore. */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputJson);
    }

    /** Enables this input so that the user can draw or type a signature. */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputJson);
    }
}
