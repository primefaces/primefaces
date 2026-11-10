/**
 * The configuration for the {@link AccordionPanel} widget.
 */
export interface AccordionPanelCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /**
     * The open tab(s), read from the state input on init: with `multiple` a list of keys or indices, otherwise a key, an
     * index or `none`.
     */
    active: PrimeFacesTypes.widget.AccordionPanel.Active;
    /** Whether dynamically loaded tabs are not loaded again. */
    cache: boolean;
    /** Whether a tab controller decides if a tab may be opened or closed. */
    controlled: boolean;
    /** Whether the content of a tab is loaded via AJAX when it is opened. */
    dynamic: boolean;
    /** Whether several tabs can be open at the same time. */
    multiple: boolean;
    /** Whether the text direction is right-to-left, set on init. */
    rtl: boolean;
    /** Whether the open tabs are kept across views. */
    multiViewState: boolean;
    /** Duration of the toggle animation in milliseconds. */
    toggleSpeed: number;
    /** Where an opened tab is scrolled into view, not scrolled if not set. */
    scrollIntoView: ScrollLogicalPosition;
    /** Called before a tab is opened, `false` keeps it closed. */
    onTabChange: PrimeFacesTypes.widget.AccordionPanel.OnTabChangeCallback;
    /** Called when a tab was opened. */
    onTabShow: PrimeFacesTypes.widget.AccordionPanel.OnTabShowCallback;
    /** Called when a tab was closed. */
    onTabClose: PrimeFacesTypes.widget.AccordionPanel.OnTabCloseCallback;
}

/**
 * __PrimeFaces AccordionPanel Widget__
 *
 * The AccordionPanel is a container component that displays content in a stacked format.
 */
export class AccordionPanel<Cfg extends AccordionPanelCfg = AccordionPanelCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The hidden input with the open tabs. */
    stateHolder: JQuery<HTMLInputElement> = $();
    /** The headers of the tabs. */
    headers: JQuery = $();
    /** The contents of the tabs. */
    panels: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.stateHolder = $(this.jqId + '_active') as JQuery<HTMLInputElement>;
        this.headers = this.jq.children('.p-accordion-header');
        this.panels = this.jq.children('.p-accordion-content');
        this.cfg.rtl = this.jq.hasClass('p-accordion-rtl');

        this.initActive();
        this.bindEvents();

        if (this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    }

    /**
     * Reads the open tabs from the state input and marks the headers of tabs with errors.
     */
    private initActive(): void {
        const stateHolderVal = this.stateHolder.val() as string | undefined;
        if (this.cfg.multiple) {
            const active: (string | number)[] = [];
            this.cfg.active = active;

            if (stateHolderVal != null && stateHolderVal.length > 0) {
                const activated = stateHolderVal.split(',');
                for (const a of activated) {
                    active.push(a);
                }
            }
        }
        else if (stateHolderVal != null && stateHolderVal.length > 0) {
            this.cfg.active = stateHolderVal;
        }
        else {
            this.cfg.active = 'none';
        }

        this.headers.each((_i, el) => {
            const containerId = PrimeFaces.escapeClientId(el.id.replace('_header', ''));
            if ($(containerId + ' .p-state-error').length > 0 || $(containerId + ' .p-message-error-detail').length > 0) {
                $(el).addClass('p-state-error');
            }
            else {
                $(el).removeClass('p-state-error');
            }
        });
    }

    private bindEvents(): void {
        this.headers.on('mouseover.accordion', (e) => {
            const element = $(e.currentTarget);
            if (!element.hasClass('p-state-active') && !element.hasClass('p-state-disabled')) {
                element.addClass('p-state-hover');
            }
        }).on('mouseout.accordion', (e) => {
            const element = $(e.currentTarget);
            if (!element.hasClass('p-state-active') && !element.hasClass('p-state-disabled')) {
                element.removeClass('p-state-hover');
            }
        }).on('click.accordion', (e) => {
            const header = $(e.currentTarget);
            if (!header.hasClass('p-state-disabled')) {
                const tabIndex = this.headers.index(header);

                if (header.hasClass('p-state-active')) {
                    this.unselect(tabIndex);
                }
                else {
                    this.select(tabIndex);
                    header.trigger('focus.accordion');

                    if (this.cfg.scrollIntoView) {
                        // 300 ms delay is so the content actually exists that we are trying to scroll to
                        PrimeFaces.queueTask(() => {
                            header.next()[0]?.scrollIntoView({
                                behavior: 'smooth',
                                block: this.cfg.scrollIntoView,
                                inline: 'center'
                            });
                        }, 300);
                    }
                }
            }

            e.preventDefault();
        });

        this.bindKeyEvents();
    }

    private bindKeyEvents(): void {
        this.headers.on('focus.accordion', (e) => {
            $(e.currentTarget).addClass('p-tabs-outline');
        }).on('blur.accordion', (e) => {
            $(e.currentTarget).removeClass('p-tabs-outline');
        }).on('keydown.accordion', (e) => {
            const currentHeader = $(e.currentTarget);
            let index = 0;
            switch ((e as JQuery.TriggeredEvent & { code?: string }).code) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    currentHeader.trigger('click');
                    e.preventDefault();
                    break;
                case 'PageUp':
                case 'Home':
                    this.headers.first().trigger('focus');
                    e.preventDefault();
                    break;
                case 'PageDown':
                case 'End':
                    this.headers.last().trigger('focus');
                    e.preventDefault();
                    break;
                case 'ArrowDown':
                    index = this.headers.index(currentHeader) + 1;
                    while (index < this.headers.length && !this.headers.eq(index).is(':visible')) {
                        index++;
                    }
                    if (index < this.headers.length) {
                        this.headers.eq(index).trigger('focus');
                    }
                    e.preventDefault();
                    break;

                case 'ArrowUp':
                    index = this.headers.index(currentHeader) - 1;
                    while (index < this.headers.length && !this.headers.eq(index).is(':visible')) {
                        index--;
                    }
                    if (index >= 0) {
                        this.headers.eq(index).trigger('focus');
                    }
                    e.preventDefault();
                    break;
            }
        });
    }

    /**
     * Marks the open tabs as loaded, their content is not loaded again.
     */
    private markLoadedPanels(): void {
        // the active tabs are keys (data-key of the header) or indices
        const markPanelAsLoaded = (active: string | number) => {
            if (active === null || active === '') {
                return;
            }
            const header = this.headers.filter((_i, el) => el.getAttribute('data-key') === String(active));
            const index = header.length ? this.headers.index(header) : Number(active);
            if (index >= 0) {
                this.markAsLoaded(this.panels.eq(index));
            }
        };

        if (this.cfg.multiple) {
            (this.cfg.active as (string | number)[]).forEach(markPanelAsLoaded);
        }
        else {
            markPanelAsLoaded(this.cfg.active as string | number);
        }
    }

    /**
     * Opens the tab with the given index.
     * @param index 0-based index of the tab.
     * @returns `true` if the tab is opened, `false` if `onTabChange` prevented it, `undefined` if it was open already.
     */
    select(index: number): boolean | undefined {
        const panel = this.panels.eq(index);
        const header = panel.prev();

        // don't select already selected panel
        if (header.hasClass('p-state-active')) {
            return undefined;
        }

        // Call user onTabChange callback
        if (this.cfg.onTabChange) {
            const result = this.cfg.onTabChange.call(this, panel);
            if (result === false) {
                return false;
            }
        }

        const shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

        const key = header.attr('data-key') ?? index;

        // update state
        if (this.cfg.multiple) {
            this.addToSelection(key);
        }
        else {
            this.cfg.active = key;
        }

        this.saveState();

        if (shouldLoad) {
            this.loadDynamicTab(panel);
        }
        else if (this.cfg.controlled) {
            this.fireTabChangeEvent(panel);
        }
        else {
            this.show(panel);
            this.fireTabChangeEvent(panel);
        }

        return true;
    }

    /**
     * Opens all tabs with `multiple`, otherwise the first tab.
     */
    selectAll(): void {
        this.panels.each((index) => {
            this.select(index);

            if (!this.cfg.multiple) {
                return false; // breaks
            }
            return undefined;
        });
    }

    /**
     * Closes the tab with the given index.
     * @param index 0-based index of the tab.
     */
    unselect(index: number): void {
        const panel = this.panels.eq(index);
        const header = panel.prev();

        // don't unselect already unselected panel
        if (!header.hasClass('p-state-active')) {
            return;
        }

        if (!this.cfg.controlled) {
            this.hide(index);
        }

        this.fireTabCloseEvent(index);
    }

    /**
     * Closes all tabs.
     */
    unselectAll(): void {
        this.panels.each((index) => {
            this.unselect(index);
        });
    }

    /**
     * Opens the given tab, without `multiple` the open tab is closed.
     * @param panel The content of the tab.
     */
    private show(panel: JQuery): void {
        // deactivate current
        if (!this.cfg.multiple) {
            const oldHeader = this.headers.filter('.p-state-active');
            oldHeader.attr('aria-expanded', 'false').removeClass('p-state-active')
                .next().attr('aria-hidden', 'true').slideUp(this.cfg.toggleSpeed as number, () => {
                    if (this.cfg.onTabClose) {
                        this.cfg.onTabClose.call(this, panel);
                    }
                });
        }

        // activate selected
        const newHeader = panel.prev();
        newHeader.attr('aria-expanded', 'true').addClass('p-state-active').removeClass('p-state-hover');

        panel.attr('aria-hidden', 'false').slideDown(this.cfg.toggleSpeed as number, () => {
            this.postTabShow(panel);
        });
    }

    /**
     * Closes the tab with the given index.
     * @param index 0-based index of the tab.
     */
    private hide(index: number): void {
        const panel = this.panels.eq(index);
        const header = panel.prev();

        header.attr('aria-expanded', 'false').removeClass('p-state-active');
        panel.attr('aria-hidden', 'true').slideUp(this.cfg.toggleSpeed as number, () => {
            if (this.cfg.onTabClose) {
                this.cfg.onTabClose.call(this, panel);
            }
        });

        const key = header.attr('data-key') ?? index;

        this.removeFromSelection(key);
        this.saveState();
    }

    /**
     * Loads the content of the given tab via AJAX and opens it.
     * @param panel The content of the tab.
     */
    loadDynamicTab(panel: JQuery): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            ignoreAutoUpdate: true,
            params: [
                { name: this.getId() + '_contentLoad', value: true },
                { name: this.getId() + '_currentTab', value: panel.attr('id') },
                { name: this.getId() + '_tabindex', value: Math.floor(panel.index() / 2) },
                // #13551: normally set through hidden state input but necessary here for accordion not wrapped in form
                { name: this.getId() + '_active', value: this.cfg.active }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        panel.html(content);

                        if (this.cfg.cache) {
                            this.markAsLoaded(panel);
                        }
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.show(panel);
            }
        };

        if (this.hasBehavior('tabChange')) {
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Calls the `tabChange` behavior, or saves the state with `multiViewState`.
     * @param panel The content of the opened tab.
     */
    private fireTabChangeEvent(panel: JQuery): void {
        if (this.hasBehavior('tabChange')) {
            const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                params: [
                    { name: this.getId() + '_currentTab', value: panel.attr('id') },
                    { name: this.getId() + '_tabindex', value: Math.floor(panel.index() / 2) }
                ]
            };

            if (this.cfg.controlled) {
                ext.oncomplete = (_xhr, _status, args) => {
                    if (args?.['access'] && !args.validationFailed) {
                        this.show(panel);
                    }
                };
            }

            this.callBehavior('tabChange', ext);
        }
        else if (this.cfg.multiViewState) {
            const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.getId(),
                partialSubmit: true,
                process: this.getId(),
                ignoreAutoUpdate: true,
                global: false,
                params: [
                    { name: this.getId() + '_skipChildren', value: true },
                    { name: this.getId() + '_currentTab', value: panel.attr('id') },
                    { name: this.getId() + '_tabindex', value: Math.floor(panel.index() / 2) }
                ]
            };

            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Calls the `tabClose` behavior, or saves the state with `multiViewState`.
     * @param index 0-based index of the closed tab.
     */
    private fireTabCloseEvent(index: number): void {
        const panel = this.panels.eq(index);
        if (this.hasBehavior('tabClose')) {
            const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                params: [
                    { name: this.getId() + '_currentTab', value: panel.attr('id') },
                    { name: this.getId() + '_tabindex', value: Math.floor(index) }
                ]
            };

            if (this.cfg.controlled) {
                ext.oncomplete = (_xhr, _status, args) => {
                    if (args?.['access'] && !args.validationFailed) {
                        this.hide(index);
                    }
                };
            }

            this.callBehavior('tabClose', ext);
        }
        else if (this.cfg.multiViewState) {
            const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.getId(),
                partialSubmit: true,
                process: this.getId(),
                ignoreAutoUpdate: true,
                global: false,
                params: [
                    { name: this.getId() + '_skipChildren', value: true },
                    { name: this.getId() + '_currentTab', value: panel.attr('id') },
                    { name: this.getId() + '_tabindex', value: Math.floor(index) }
                ]
            };

            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Marks the given dynamic tab as loaded.
     * @param panel The content of the tab.
     */
    private markAsLoaded(panel: JQuery): void {
        panel.data('loaded', true);
    }

    /**
     * @param panel The content of a tab.
     * @returns `true` if the content of the dynamic tab was loaded.
     */
    isLoaded(panel: JQuery): boolean {
        return panel.data('loaded') === true;
    }

    /**
     * Adds the key or index of a tab to the open tabs, with `multiple`.
     * @param nodeId The key or index of the tab.
     */
    private addToSelection(nodeId: string | number): void {
        (this.cfg.active as (string | number)[]).push(nodeId);
    }

    /**
     * Removes the key or index of a tab from the open tabs.
     * @param nodeId The key or index of the tab.
     */
    private removeFromSelection(nodeId: string | number): void {
        if (!this.cfg.multiple) {
            // a single active tab is a string or number, not a list: grep would split a string into characters
            // != on purpose, the key may be the index as string
            if (this.cfg.active != nodeId) {
                return;
            }
            this.cfg.active = '';
            return;
        }
        // != on purpose, the key may be the index as string
        this.cfg.active = $.grep(this.cfg.active as (string | number)[], (r) => r != nodeId);
    }

    /**
     * Writes the open tabs to the state input.
     */
    private saveState(): void {
        if (this.cfg.multiple) {
            this.stateHolder.val((this.cfg.active as (string | number)[]).join(','));
        }
        else {
            this.stateHolder.val(this.cfg.active as string | number);
        }
    }

    /**
     * Calls the `onTabShow` callback and renders deferred children.
     * @param newPanel The content of the opened tab.
     */
    private postTabShow(newPanel: JQuery): void {
        // Call user onTabShow callback
        if (this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }
}
