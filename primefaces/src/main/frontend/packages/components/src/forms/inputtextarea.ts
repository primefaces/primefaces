/**
 * The configuration for the {@link InputTextarea} widget.
 */
export interface InputTextareaCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Whether the textarea grows with its content. */
    autoResize: boolean;
    /** Whether tokens are suggested via AJAX while typing. */
    autoComplete: boolean;
    /** Client ID of the component that displays the counter. */
    counter: string;
    /** Template of the counter: `{0}` remaining, `{1}` entered and `{2}` maximum characters, defaults to `{0}`. */
    counterTemplate: string;
    /** Whether the counter and the maximum length count bytes instead of characters. */
    countBytesAsChars: boolean;
    /** Maximum number of characters, only sent if set. */
    maxlength: number;
    /** Number of characters of a token before the suggestions are queried. */
    minQueryLength: number;
    /** Delay in milliseconds before the suggestions are queried. */
    queryDelay: number;
    /** Maximum height of the suggestions panel in pixels. */
    scrollHeight: number;
}

/**
 * __PrimeFaces InputTextarea Widget__
 *
 * InputTextarea is an extension to standard inputTextarea with autoComplete, autoResize, remaining characters counter
 * and theming features.
 */
export class InputTextarea<Cfg extends InputTextareaCfg = InputTextareaCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The element that displays the counter, only with `counter`. */
    counter?: JQuery | null;
    /** The panel with the suggestions, only with `autoComplete`. */
    panel?: JQuery;
    /** The suggestions. */
    items: JQuery = $();
    /** The token for which the suggestions were queried. */
    query?: string;
    /** The timer ID of the delayed query. */
    timeout?: number | null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.items = $();

        if (this.cfg.autoResize) {
            this.renderDeferred();
        }
        else {
            this._render();
        }
    }

    protected override _render(): void {
        PrimeFaces.skinInput(this.jq);

        if (this.cfg.autoComplete) {
            this.setupAutoComplete();
        }

        if (this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate || '{0}';
            this.updateCounter();

            if (this.counter) {
                this.jq.on('input.inputtextarea-counter', () => {
                    this.updateCounter();
                });
            }
        }

        if (this.cfg.maxlength) {
            this.applyMaxlength();
        }

        if (this.cfg.autoResize) {
            this.setupAutoResize();
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // remove autocomplete panel
        if (cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        super.refresh(cfg);
    }

    private setupAutoResize(): void {
        window.autosize(this.jq);
    }

    /**
     * Cuts the value at the maximum length, counting line breaks correctly.
     */
    private applyMaxlength(): void {
        this.jq.on('keyup.inputtextarea-maxlength', () => {
            let value = this.jq.val() as string;
            const length = this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;
            const maxlength = this.cfg.maxlength ?? 0;

            if (length > maxlength) {
                // Trim by bytes if counting bytes, otherwise by chars
                if (this.cfg.countBytesAsChars) {
                    // Keep removing chars from end until we're under byte limit
                    while (PrimeFaces.utils.countBytes(value) > maxlength) {
                        value = value.slice(0, -1);
                    }
                    this.jq.val(value);
                }
                else {
                    this.jq.val(value.slice(0, maxlength));
                }
            }
        });
    }

    /**
     * Updates the counter with the remaining and entered characters.
     */
    private updateCounter(): void {
        const value = this.jq.val() as string;
        const length = this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;

        if (this.counter) {
            const maxlength = this.cfg.maxlength;
            // without maxlength {0} (remaining) and {2} (maximum) are empty
            let remaining: number | string = '';
            if (maxlength) {
                remaining = Math.max(maxlength - length, 0);
            }

            const counterText = (this.cfg.counterTemplate ?? '')
                    .replace('{0}', String(remaining))
                    .replace('{1}', String(length))
                    .replace('{2}', maxlength ? String(maxlength) : '');

            this.counter.text(counterText);
        }
    }

    /**
     * Creates the suggestions panel and binds the keys that query and select suggestions.
     */
    private setupAutoComplete(): void {
        const panelMarkup = '<div id="' + this.getId() + '_panel" class="p-autocomplete-panel p-widget-content p-hidden"></div>';

        const panel = $(panelMarkup).appendTo(document.body);
        this.panel = panel;

        this.jq.on('keyup.inputtextarea', (e) => {
            switch ((e as JQuery.TriggeredEvent & { code?: string }).code) {
                case 'ArrowUp':
                case 'ArrowDown':
                case 'ArrowLeft':
                case 'ArrowRight':
                case 'Enter':
                case 'NumpadEnter':
                case 'Tab':
                case 'Space':
                case 'Shift':
                case 'Control':
                case 'Alt':
                case 'Meta':
                case 'Escape':
                    // do not search
                    break;

                default: {
                    const query = this.extractQuery();
                    if (query && query.length >= (this.cfg.minQueryLength ?? 0)) {

                        // Cancel the search request if user types within the timeout
                        if (this.timeout) {
                            this.clearTimeout();
                        }

                        this.timeout = PrimeFaces.queueTask(() => {
                            this.search(query);
                        }, this.cfg.queryDelay);

                    }
                    break;
                }
            }

        }).on('keydown.inputtextarea', (e) => {
            const overlayVisible = panel.is(':visible');
            const highlightedItem = this.items.filter('.p-state-highlight');

            switch (e.key) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    if (overlayVisible) {
                        const prev = highlightedItem.length === 0 ? this.items.eq(0) : highlightedItem.prev();

                        if (prev.length === 1) {
                            highlightedItem.removeClass('p-state-highlight');
                            prev.addClass('p-state-highlight');

                            if (this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(panel, prev);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        this.clearTimeout();
                    }
                    break;

                case 'ArrowDown':
                case 'ArrowRight':
                    if (overlayVisible) {
                        const next = highlightedItem.length === 0 ? this.items.eq(0) : highlightedItem.next();

                        if (next.length === 1) {
                            highlightedItem.removeClass('p-state-highlight');
                            next.addClass('p-state-highlight');

                            if (this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(panel, next);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        this.clearTimeout();
                    }
                    break;

                case 'Enter':
                    if (overlayVisible) {
                        this.items.filter('.p-state-highlight').trigger('click');

                        e.preventDefault();
                    }
                    else {
                        this.clearTimeout();
                    }
                    break;

                case ' ':
                case 'Shift':
                case 'Control':
                case 'Alt':
                case 'Meta':
                case 'Backspace':
                case 'Escape':
                    this.clearTimeout();

                    if (overlayVisible) {
                        this.hide();
                    }
                    break;

                case 'Tab':
                    this.clearTimeout();

                    if (overlayVisible) {
                        this.items.filter('.p-state-highlight').trigger('click');
                        this.hide();
                    }
                    break;
            }
        });

        // hide panel when outside is clicked
        $(document.body).on('mousedown.p-inputtextarea' + this.id, (e) => {
            if (panel.is(':hidden')) {
                return;
            }
            const offset = panel.offset();
            if (e.target === this.jq.get(0) || !offset) {
                return;
            }

            const pageX = e.pageX ?? 0;
            const pageY = e.pageY ?? 0;
            if (pageX < offset.left
                    || pageX > offset.left + (panel.width() ?? 0)
                    || pageY < offset.top
                    || pageY > offset.top + (panel.height() ?? 0)) {
                this.hide();
            }
        });
        const cleanup = () => {
            $(document.body).off('mousedown.p-inputtextarea' + this.id);
            panel.remove();
        };
        this.addRefreshListener(cleanup);
        this.addDestroyListener(cleanup);

        // Hide overlay on resize
        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', panel, () => {
            this.hide();
        });

        // dialog support
        this.setupDialogSupport();

        if (!this.jq.hasClass('p-state-disabled')) {
            this.jq.data('primefaces-overlay-target', true);
        }
    }

    /**
     * Binds the hover and click of the suggestions.
     */
    private bindDynamicEvents(): void {
        this.items.on('mouseover.inputtextarea', (e) => {
            const item = $(e.currentTarget);

            if (!item.hasClass('p-state-highlight')) {
                this.items.filter('.p-state-highlight').removeClass('p-state-highlight');
                item.addClass('p-state-highlight');
            }
        })
        .on('click.inputtextarea', (event) => {
            const item = $(event.currentTarget);
            const itemValue = item.attr('data-item-value') ?? '';
            const selectionStart = PrimeFaces.utils.getTextSelection(this.jq)?.start ?? 0;
            const queryLength = this.query?.length ?? 0;

            this.jq.trigger('focus');

            PrimeFaces.utils.setTextSelection(this.jq, selectionStart - queryLength, selectionStart);
            PrimeFaces.utils.replaceTextSelection(this.jq, itemValue);

            this.invokeItemSelectBehavior(event, itemValue);

            this.hide();
        });
    }

    /**
     * Calls the `itemSelect` behavior after a suggestion was selected.
     * @param _event The event that selected the suggestion, unused.
     * @param itemValue The value of the suggestion.
     */
    private invokeItemSelectBehavior(_event: JQuery.TriggeredEvent, itemValue: string): void {
        if (this.hasBehavior('itemSelect')) {
            const ext = {
                params: [
                    { name: this.getId() + '_itemSelect', value: itemValue }
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    }

    /**
     * Cancels the delayed query.
     */
    private clearTimeout(): void {
        if (this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    }

    /**
     * @returns The word before the caret, the token for the suggestions, `null` if there is none.
     */
    private extractQuery(): string | null {
        const end = PrimeFaces.utils.getTextSelection(this.jq)?.end;
        const result = /\S+$/.exec((this.jq[0] as HTMLTextAreaElement).value.slice(0, end));
        const lastWord = result ? result[0] : null;

        return lastWord;
    }

    /**
     * Queries the suggestions for the given token and shows them.
     * @param query The token.
     */
    search(query: string): void {
        this.query = query;

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            params: [
                { name: this.getId() + '_query', value: query }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        const panel = this.panel;
                        if (!panel) {
                            return;
                        }
                        panel.html(content);
                        this.items = panel.find('.p-autocomplete-item');

                        this.bindDynamicEvents();

                        if (this.items.length > 0) {
                            // highlight first item
                            this.items.eq(0).addClass('p-state-highlight');

                            // adjust height
                            if (this.cfg.scrollHeight && (panel.height() ?? 0) > this.cfg.scrollHeight) {
                                panel.height(this.cfg.scrollHeight);
                            }

                            if (panel.is(':hidden')) {
                                this.show();
                            }
                            else {
                                this.alignPanel(); // with new items
                            }

                        }
                        else {
                            panel.hide();
                        }
                    }
                });

                return true;
            }
        };

        if (this.hasBehavior('query')) {
            this.callBehavior('query', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Aligns the suggestions panel below the caret.
     */
    alignPanel(): void {
        if (!this.panel) {
            return;
        }
        const pos = this.getCaretPosition();
        const posLeft = (pos.left > 0 ? '+' : '-') + pos.left;
        const posTop = (pos.top > 0 ? '+' : '-') + pos.top;

        PrimeFaces.utils.position(this.panel.css({ left: '', top: '' }), {
            my: 'left top',
            at: 'left' + posLeft + 'px' + ' top' + posTop + 'px',
            of: this.jq,
            collision: 'flipfit'
        });
    }

    /**
     * Finds the position of the caret in pixels by rendering the text into a hidden copy of the textarea with the same
     * styles.
     * @returns The position below the caret line, relative to the top left corner of the textarea.
     */
    private getCaretPosition(): { top: number; left: number } {
        const element = this.jq[0] as HTMLTextAreaElement;
        const value = element.value;
        const caret = element.selectionStart || 0;
        const mirror = $('<div></div>').css({
            position: 'absolute',
            top: '0px',
            left: '0px',
            visibility: 'hidden',
            width: this.jq.width() + 'px',
            height: this.jq.height() + 'px',
            // the size is the content size of the textarea, the global border-box would subtract padding and border
            'box-sizing': 'content-box',
            'white-space': 'pre-wrap',
            'word-wrap': 'break-word',
            'overflow-x': 'hidden',
            'overflow-y': 'auto'
        });
        // a zero width space, so the caret has a line box in every browser
        const focus = $('<span></span>').text('​');

        for (const style of ['fontFamily', 'fontSize', 'fontWeight', 'fontVariant', 'fontStyle', 'lineHeight', 'letterSpacing',
                'wordSpacing', 'textTransform', 'textIndent', 'tabSize',
                'paddingLeft', 'paddingTop', 'paddingBottom', 'paddingRight', 'borderLeftStyle', 'borderTopStyle',
                'borderBottomStyle', 'borderRightStyle', 'borderLeftWidth', 'borderTopWidth', 'borderBottomWidth',
                'borderRightWidth']) {
            mirror.css(style, this.jq.css(style));
        }

        mirror.append(document.createTextNode(value.substring(0, caret)), focus, document.createTextNode(value.substring(caret)))
            .appendTo(document.body);

        const focusOffset = focus.offset() ?? { top: 0, left: 0 };
        const mirrorOffset = mirror.offset() ?? { top: 0, left: 0 };
        const position = {
            top: focusOffset.top - mirrorOffset.top - element.scrollTop + (parseFloat(this.jq.css('fontSize')) || 0),
            left: focusOffset.left - mirrorOffset.left - element.scrollLeft
        };
        mirror.remove();
        return position;
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, undefined);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq);
    }

    /**
     * Shows the suggestions panel.
     */
    private show(): void {
        if (!this.panel) {
            return;
        }
        this.panel.css({
            'z-index': PrimeFaces.nextZindex(),
            'width': this.jq.innerWidth() + 'px',
            'visibility': 'hidden'
        }).show();

        this.alignPanel();

        this.panel.css('visibility', '');
    }

    /**
     * Hides the suggestions panel.
     */
    private hide(): void {
        this.panel?.hide();
    }

    /**
     * Fixes the position of the suggestions panel if the textarea is in a fixed dialog.
     */
    private setupDialogSupport(): void {
        const dialog = this.jq.parents('.p-dialog').first();

        if (dialog.length === 1 && dialog.css('position') === 'fixed') {
            this.panel?.css('position', 'fixed');
        }
    }
}
