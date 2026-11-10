/**
 * The configuration for the {@link Splitter} widget.
 */
export interface SplitterCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Size of the gutters in pixels. */
    gutterSize: number;
    /** Step in pixels when an arrow key is held down. */
    step: number;
    /** Orientation of the panels. */
    layout: PrimeFacesTypes.widget.Splitter.Layout;
    /** Key of the stored panel sizes, the splitter is stateful only with a key. */
    stateKey: string;
    /** Storage of the panel sizes. */
    stateStorage: PrimeFacesTypes.widget.Splitter.StateStorage;
    /** Called when the resizing ended. */
    onResizeEnd: PrimeFacesTypes.widget.Splitter.OnResizeEndCallback;
}

/**
 * __PrimeFaces Splitter Widget__
 *
 * Splitter is utilized to separate and resize panels.
 */
export class Splitter<Cfg extends SplitterCfg = SplitterCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The panels. */
    panels: JQuery = $();
    /** The gutters between the panels. */
    gutters: JQuery = $();
    /** Whether the panels are side by side. */
    horizontal: boolean = false;
    /** Whether the panels are on top of each other. */
    vertical: boolean = true;
    /** The sizes of the panels in percent, stored with `stateKey`. */
    panelSizes: number[] = [];
    /** While resizing, the gutter that is moved. */
    gutterElement: JQuery | null = null;
    /** While resizing, the handle of the gutter. */
    gutterHandle: JQuery | null = null;
    /** While resizing, the panel before the gutter. */
    prevPanelElement: JQuery | null = null;
    /** While resizing, the panel after the gutter. */
    nextPanelElement: JQuery | null = null;
    /** While resizing, the size of the panel before the gutter, in percent (in pixels while a key is held down). */
    prevPanelSize: number | null = null;
    /** While resizing, the size of the panel after the gutter, in percent (in pixels while a key is held down). */
    nextPanelSize: number | null = null;
    /** While resizing, the index of the panel before the gutter. */
    prevPanelIndex: number | null = null;
    /** While resizing, the width or height of the splitter in pixels. */
    size: number | null = null;
    /** While resizing, the start position of the pointer in pixels. */
    startPos: number | null = null;
    /** The interval repeating the resize while an arrow key is held down. */
    timeout: number | null = null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.panels = this.jq.children('.p-splitter-panel');
        this.gutters = this.jq.children('.p-splitter-gutter');
        this.horizontal = this.cfg.layout === 'horizontal';
        this.vertical = !this.horizontal;
        this.panelSizes = [];

        this.initPanelSize();
        this.bindGutterEvent();
    }

    private initPanelSize(): void {
        let initialized = false;

        if (this.isStateful()) {
            initialized = this.restoreState();
        }

        if (!initialized) {
            this.jq.hide();
            this.panels.each((i, panel) => {
                const panelSize = parseInt(panel.dataset['size'] ?? '') || 100 / this.panels.length;
                this.panelSizes[i] = panelSize;

                panel.style.flexBasis = `calc(${panelSize}% - ${(this.panels.length - 1) * (this.cfg.gutterSize ?? 0)}px)`;

                this.gutters.eq(i).find('.p-splitter-gutter-handle')
                    .attr('aria-valuenow', panelSize.toString())
                    .attr('aria-valuetext', panelSize.toFixed(0) + '%');
                this.prevPanelSize = panelSize;
            });
            this.jq.show();
        }
    }

    private bindMouseListeners(): void {
        $(document).on('mousemove.splitter' + this.id, (event) => {
            this.onResize(event);
        }).on('mouseup.splitter' + this.id, (event) => {
            this.onResizeEnd(event);
            this.unbindDocumentListeners();
        });
    }

    private bindTouchListeners(): void {
        $(document).on('touchmove.splitter' + this.id, (event) => {
            this.onResize(event);
        }).on('touchend.splitter' + this.id, (event) => {
            this.onResizeEnd(event);
            this.unbindDocumentListeners();
        });
    }

    private unbindDocumentListeners(): void {
        $(document).off('.splitter' + this.id);
    }

    private bindGutterEvent(): void {
        this.gutters.each((_index, gutter) => {
            $(gutter).off('mousedown.splitter touchstart.splitter touchmove.splitter touchend.splitter keydown.splitter keyup.splitter')
                .on('keydown.splitter', (event) => {
                    this.onGutterKeyDown(event);
                })
                .on('keyup.splitter', (event) => {
                    this.onGutterKeyUp(event);
                })
                .on('mousedown.splitter', (event) => {
                    this.onResizeStart(event);
                    this.bindMouseListeners();
                })
                .on('touchstart.splitter', (event) => {
                    this.onResizeStart(event);
                    this.bindTouchListeners();
                    if (event.cancelable) {
                        event.preventDefault();
                    }
                })
                .on('touchmove.splitter', (event) => {
                    this.onResize(event);
                    if (event.cancelable) {
                        event.preventDefault();
                    }
                })
                .on('touchend.splitter', (event) => {
                    this.onResizeEnd(event);
                    this.unbindDocumentListeners();
                    if (event.cancelable) {
                        event.preventDefault();
                    }
                });
        });
    }

    private onGutterKeyDown(event: JQuery.TriggeredEvent): void {
        this.onResizeStart(event);
        const minSize = parseFloat(this.panels[0]?.dataset['minsize'] ?? '') || 0;
        const step = this.cfg.step ?? 0;

        switch ((event as JQuery.TriggeredEvent & { code?: string }).code) {
            case 'ArrowLeft': {
                if (this.horizontal) {
                    this.setTimer(event, step * -1);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowRight': {
                if (this.horizontal) {
                    this.setTimer(event, step);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowDown': {
                if (this.vertical) {
                    this.setTimer(event, step * -1);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowUp': {
                if (this.vertical) {
                    this.setTimer(event, step);
                }

                event.preventDefault();
                break;
            }

            case 'Home': {
                this.resizePanel(100 - minSize, minSize);

                event.preventDefault();
                break;
            }

            case 'End': {
                this.resizePanel(minSize, 100 - minSize);

                event.preventDefault();
                break;
            }

            case 'Enter': {
                if ((this.prevPanelSize ?? 0) >= (100 - (minSize || 5))) {
                    this.resizePanel(minSize, 100 - minSize);
                }
                else {
                    this.resizePanel(100 - minSize, minSize);
                }

                event.preventDefault();
                break;
            }

            default:
                // no op
                break;
        }
    }

    private onGutterKeyUp(event: JQuery.TriggeredEvent): void {
        this.clearTimer();
        this.onResizeEnd(event);
    }

    /**
     * Starts resizing with the gutter of the event.
     * @param event The pointer or key event on the gutter.
     * @param isKeyDown `true` while an arrow key is held down, the sizes are kept in pixels.
     */
    private onResizeStart(event: JQuery.TriggeredEvent, isKeyDown?: boolean): void {
        const gutterElement = $(event.currentTarget || (event.target as HTMLElement).parentElement);
        this.gutterElement = gutterElement;
        this.gutterHandle = gutterElement.find('.p-splitter-gutter-handle');
        this.size = (this.horizontal ? this.jq.width() : this.jq.height()) ?? null;
        const prevPanelElement = gutterElement.prev();
        const nextPanelElement = gutterElement.next();
        this.prevPanelElement = prevPanelElement;
        this.nextPanelElement = nextPanelElement;
        this.prevPanelIndex = this.panels.index(prevPanelElement);
        gutterElement.addClass('p-splitter-gutter-resizing');
        this.jq.addClass('p-splitter-resizing');
        const prevSize = (this.horizontal ? prevPanelElement.outerWidth(true) : prevPanelElement.outerHeight(true)) ?? 0;
        const nextSize = (this.horizontal ? nextPanelElement.outerWidth(true) : nextPanelElement.outerHeight(true)) ?? 0;
        if (isKeyDown) {
            this.prevPanelSize = prevSize;
            this.nextPanelSize = nextSize;
        }
        else {
            const touch = event.type === 'touchstart' ? (event.originalEvent as TouchEvent | undefined)?.changedTouches[0] : undefined;
            const pageX = event.type === 'touchstart' ? touch?.pageX : event.pageX;
            const pageY = event.type === 'touchstart' ? touch?.pageY : event.pageY;
            this.startPos = (this.horizontal ? pageX : pageY) ?? null;
            this.prevPanelSize = (100 * prevSize) / (this.size ?? 0);
            this.nextPanelSize = (100 * nextSize) / (this.size ?? 0);
        }
    }

    /**
     * Resizes the panels while the gutter is moved.
     * @param event The pointer event, or the key event while a key is held down.
     * @param step The step in pixels while a key is held down.
     * @param isKeyDown `true` while an arrow key is held down.
     */
    private onResize(event: JQuery.TriggeredEvent, step: number = 0, isKeyDown: boolean = false): void {
        let newNextPanelSize: number;
        let newPrevPanelSize: number;
        const size = this.size ?? 0;

        if (isKeyDown) {
            const delta = this.horizontal ? step : -step;
            newPrevPanelSize = (100 * ((this.prevPanelSize ?? 0) + delta)) / size;
            newNextPanelSize = (100 * ((this.nextPanelSize ?? 0) - delta)) / size;
        }
        else {
            const isTouchEvent = event.type === 'touchmove';
            const pointer = isTouchEvent ? (event.originalEvent as TouchEvent | undefined)?.changedTouches[0] : event;
            const pagePos = (this.horizontal ? pointer?.pageX : pointer?.pageY) ?? 0;
            const newPos = (pagePos * 100) / size - ((this.startPos ?? 0) * 100) / size;
            newPrevPanelSize = (this.prevPanelSize ?? 0) + newPos;
            newNextPanelSize = (this.nextPanelSize ?? 0) - newPos;
        }

        this.resizePanel(newPrevPanelSize, newNextPanelSize);
    }

    /**
     * Ends the resizing, saves the state and calls the `onResizeEnd` callback and the `resizeEnd` behavior.
     * @param _event The event that ended the resizing, unused.
     */
    private onResizeEnd(_event: JQuery.TriggeredEvent): void {
        if (!this.gutterElement) {
            return;
        }
        if (this.isStateful()) {
            this.saveState();
        }

        this.gutterElement.removeClass('p-splitter-gutter-resizing');
        this.jq.removeClass('p-splitter-resizing');

        // call user onResizeEnd callback
        if (this.cfg.onResizeEnd) {
            this.cfg.onResizeEnd.call(this, this.panelSizes);
        }

        if (this.hasBehavior('resizeEnd')) {
            const sizesArr = this.panelSizes;
            const ext = {
                params: [
                    { name: this.getId() + '_panelSizes', value: sizesArr.map((e) => e.toFixed(2)).join('_') },
                ]
            };

            this.callBehavior('resizeEnd', ext);
        }

        this.clear();
    }

    /**
     * Resizes the panels of the gutter that is moved.
     * @param newPrevPanelSize The new size of the panel before the gutter, in percent.
     * @param newNextPanelSize The new size of the panel after the gutter, in percent.
     */
    resizePanel(newPrevPanelSize: number, newNextPanelSize: number): void {
        if (!this.prevPanelElement || !this.nextPanelElement || !this.gutterHandle || this.prevPanelIndex === null) {
            // no gutter is moved
            return;
        }
        if (this.validateResize(newPrevPanelSize, newNextPanelSize)) {
            const gutterSize = (this.panels.length - 1) * (this.cfg.gutterSize ?? 0);
            this.prevPanelElement.css('flexBasis', 'calc(' + newPrevPanelSize + '% - ' + gutterSize + 'px)');
            this.nextPanelElement.css('flexBasis', 'calc(' + newNextPanelSize + '% - ' + gutterSize + 'px)');
            this.panelSizes[this.prevPanelIndex] = newPrevPanelSize;
            this.panelSizes[this.prevPanelIndex + 1] = newNextPanelSize;

            // update ARIA for the primary panel
            this.gutterHandle.attr('aria-valuenow', newPrevPanelSize.toString())
                .attr('aria-valuetext', newPrevPanelSize.toFixed(0) + '%');
        }
    }

    /**
     * Resets the state of the resizing.
     */
    private clear(): void {
        this.size = null;
        this.startPos = null;
        this.prevPanelElement = null;
        this.nextPanelElement = null;
        this.prevPanelSize = null;
        this.nextPanelSize = null;
        this.gutterElement = null;
        this.gutterHandle = null;
        this.prevPanelIndex = null;
    }

    /**
     * Checks that the new sizes are between 0 and 100 and not less than the minimum size of the first two panels.
     * @param newPrevPanelSize The new size of the panel before the gutter.
     * @param newNextPanelSize The new size of the panel after the gutter.
     * @returns `true` if the new sizes are valid.
     */
    validateResize(newPrevPanelSize: number, newNextPanelSize: number): boolean {
        const isSizeOutOfRange = newPrevPanelSize > 100 || newPrevPanelSize < 0 || newNextPanelSize > 100 || newNextPanelSize < 0;
        if (isSizeOutOfRange) {
            return false;
        }
        const isPrevSizeTooSmall = parseFloat(this.panels[0]?.dataset['minsize'] ?? '') > newPrevPanelSize;
        const isNextSizeTooSmall = parseFloat(this.panels[1]?.dataset['minsize'] ?? '') > newNextPanelSize;

        return !(isPrevSizeTooSmall || isNextSizeTooSmall);
    }

    /**
     * @returns Whether the panel sizes are stored, with a `stateKey`.
     */
    isStateful(): boolean {
        return this.cfg.stateKey != null;
    }

    private saveState(): void {
        this.getStorage().setItem(this.cfg.stateKey ?? '', JSON.stringify(this.panelSizes));
    }

    /**
     * Restores the panel sizes from the storage.
     * @returns `true` if stored sizes were found.
     */
    restoreState(): boolean {
        const storage = this.getStorage();
        const stateString = storage.getItem(this.cfg.stateKey ?? '');

        if (stateString) {
            this.panelSizes = JSON.parse(stateString);
            this.panels.each((i, panel) => {
                panel.style.flexBasis = 'calc(' + this.panelSizes[i] + '% - ' + ((this.panels.length - 1) * (this.cfg.gutterSize ?? 0)) + 'px)';
            });

            return true;
        }

        return false;
    }

    /**
     * @returns The local or session storage, depending on `stateStorage`.
     */
    getStorage(): Storage {
        switch (this.cfg.stateStorage) {
            case 'local':
                return window.localStorage;

            case 'session':
                return window.sessionStorage;

            default:
                throw new Error(this.cfg.stateStorage + ' is not a valid value for the state storage, supported values are "local" and "session".');
        }
    }

    private repeat(event: JQuery.TriggeredEvent, step: number): void {
        this.onResizeStart(event, true);
        this.onResize(event, step, true);
    }

    private setTimer(event: JQuery.TriggeredEvent, step: number): void {
        if (!this.timeout) {
            this.clearTimer();
            this.timeout = window.setInterval(() => { this.repeat(event, step); }, 40);
        }
    }

    private clearTimer(): void {
        if (this.timeout) {
            clearInterval(this.timeout);
            this.timeout = null;
        }
    }
}
