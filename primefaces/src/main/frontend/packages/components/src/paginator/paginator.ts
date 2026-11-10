/**
 * The configuration for the {@link Paginator} widget.
 */
export interface PaginatorCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /**
     * `true` if the paginator should be displayed always, or `false` if it is allowed to be hidden under some
     * circumstances that depend on the widget that uses the paginator.
     */
    alwaysVisible: boolean;
    /** Template for the paginator text. It may contain placeholders such as `{currentPage}` or `{totalPages}`. */
    currentPageTemplate: string;
    /** The current page, 0-based index. */
    page: number;
    /** The number of pages. */
    pageCount: number;
    /** The maximum number of page links to display (when there are many pages). */
    pageLinks: number;
    /**
     * A callback method that is invoked when the pagination state changes, such as when the user selects a different
     * page or changes the current rows per page count. This property is usually provided by another widget that makes
     * use of this paginator. You should use this callback to perform any actions required to apply the new pagination
     * state.
     */
    paginate: PrimeFacesTypes.widget.Paginator.PaginateCallback;
    /** The number of rows per page for the dropdown. */
    prevRows: number;
    /** Total number of rows (records) to be displayed. */
    rowCount: number;
    /** The number of rows per page. */
    rows: number;
    /** The configured number of rows set per page, `*` when all rows are shown. */
    rpp: number | string;
}

/**
 * __PrimeFaces Paginator Widget__
 *
 * A widget for handling pagination that is usually used by other widget via composition, that is, they create and save
 * an instance of this widget during initialization. After you create a new instance of this paginator, you should set
 * the `paginate` property to an appropriate callback function.
 *
 * ```javascript
 * const paginator = new PrimeFaces.widget.Paginator();
 * paginator.init(paginatorCfg);
 * paginator.paginator = newState => {
 *  // handle pagination
 * };
 * ```
 */
export class Paginator<Cfg extends PaginatorCfg = PaginatorCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** DOM element of the container for all the navigation elements. */
    navigator: JQuery = $();
    /** DOM element of the container with the numbered page links. */
    pagesContainer: JQuery = $();
    /** DOM elements of each numbered page link. */
    pageLinks: JQuery = $();
    /** SELECT element for selection the number of pages to display (`rows per page`). */
    rppSelect: JQuery = $();
    /** SELECT element for selecting a page to navigate to (`jump to page`). */
    jtpSelect: JQuery<HTMLSelectElement> = $();
    /** INPUT element for selecting a page to navigate to (`jump to page`). */
    jtpInput: JQuery = $();
    /** DOM element of the link back to the first page. */
    firstLink: JQuery = $();
    /** DOM element of the link back to the previous page. */
    prevLink: JQuery = $();
    /** DOM element of the link to the next page. */
    nextLink: JQuery = $();
    /** DOM element of the link to the last page. */
    endLink: JQuery = $();
    /** DOM element of the status text as configured by the `currentPageTemplate`. */
    currentReport: JQuery = $();
    /** ARIA LABEL attribute for the page links. */
    ariaPageLabel: string = "";

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        //elements
        this.navigator = this.jq.children('.p-paginator-center-content');
        this.pagesContainer = this.navigator.children('.p-paginator-pages');
        this.pageLinks = this.pagesContainer.children('.p-paginator-page');
        this.rppSelect = this.navigator.children('.p-paginator-rpp-options');
        this.jtpSelect = this.navigator.children('.p-paginator-jtp-select') as JQuery<HTMLSelectElement>;
        this.jtpInput = this.navigator.children('.p-paginator-jtp-input');
        this.firstLink = this.navigator.children('.p-paginator-first');
        this.prevLink  = this.navigator.children('.p-paginator-prev');
        this.nextLink  = this.navigator.children('.p-paginator-next');
        this.endLink   = this.navigator.children('.p-paginator-last');
        this.currentReport = this.navigator.children('.p-paginator-current');

        //metadata
        this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
        this.cfg.rpp = this.cfg.rows;
        this.cfg.prevRows = this.cfg.rows;
        this.cfg.pageCount = Math.ceil((this.cfg.rowCount as number) / (this.cfg.rows as number))||1;
        this.cfg.pageLinks = this.cfg.pageLinks||10;
        this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPages})';

        //aria messages
        this.configureAria();
        //event bindings
        this.bindEvents();
    }

    /**
     * Configures ARIA labels for screenreaders.
     */
    private configureAria(): void {
        this.ariaPageLabel = this.getAriaLabel('pageLabel');
        this.navigator.attr('aria-label', this.getAriaLabel('navigation'));
        this.rppSelect.attr('aria-label', this.getAriaLabel('rowsPerPageLabel'));
        this.jtpSelect.attr('aria-label', this.getAriaLabel('jumpToPageDropdownLabel'));
        this.jtpInput.attr('aria-label', this.getAriaLabel('jumpToPageInputLabel'));
        this.firstLink.attr('aria-label', this.getAriaLabel('firstPageLabel'));
        this.prevLink.attr('aria-label', this.getAriaLabel('prevPageLabel'));
        this.nextLink.attr('aria-label', this.getAriaLabel('nextPageLabel'));
        this.endLink.attr('aria-label', this.getAriaLabel('lastPageLabel'));
    }

    /**
     * Sets up all event listeners for this widget.
     */
    private bindEvents(): void {
        //visuals for first,prev,next,last buttons
        this.navigator.children('button.p-state-default').on('mouseover.paginator', (e) => {
            const item = $(e.currentTarget);
            if(!item.hasClass('p-state-disabled')) {
                item.addClass('p-state-hover');
            }
        })
        .on('mouseout.paginator', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('focus.paginator', (e) => {
            const item = $(e.currentTarget);
            if(!item.hasClass('p-state-disabled')) {
                item.addClass('p-state-focus');
            }
        })
        .on('blur.paginator', (e) => {
            $(e.currentTarget).removeClass('p-state-focus');
        })
        .on('keydown.paginator', (e) => {
            if(e.key === 'Enter') {
                $(e.currentTarget).trigger('click');
                e.preventDefault();
            }
        });

        //page links
        this.bindPageLinkEvents();

        //records per page selection
        PrimeFaces.skinSelect(this.rppSelect);
        this.rppSelect.on('change', (e) => {
            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setRowsPerPage($(e.currentTarget).val() as string);
            }
        });

        //jump to page dropdown
        PrimeFaces.skinSelect(this.jtpSelect);
        this.jtpSelect.on('change', (e) => {
            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setPage(this.parseLocalizedInt($(e.currentTarget).val() as string));
            }
        });

        //jump to page input
        PrimeFaces.skinInput(this.jtpInput);
        this.jtpInput.on('change', (e) => {
            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                const page = this.parseLocalizedInt($(e.currentTarget).val() as string);
                if (isNaN(page) || page > (this.cfg.pageCount as number) || page < 1) {
                    // restore old value if invalid
                    $(e.currentTarget).val((this.cfg.page as number) + 1);
                }
                else {
                    this.setPage(page - 1);
                }
            }
        });

        //First page link
        this.firstLink.on("click", (e) => {
            PrimeFaces.clearSelection();

            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setPage(0);
            }

            e.preventDefault();
        });

        //Prev page link
        this.prevLink.on("click", (e) => {
            PrimeFaces.clearSelection();

            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setPage((this.cfg.page as number) - 1);
            }

            e.preventDefault();
        });

        //Next page link
        this.nextLink.on("click", (e) => {
            PrimeFaces.clearSelection();

            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setPage((this.cfg.page as number) + 1);
            }

            e.preventDefault();
        });

        //Last page link
        this.endLink.on("click", (e) => {
            PrimeFaces.clearSelection();

            if(!$(e.currentTarget).hasClass("p-state-disabled")){
                this.setPage((this.cfg.pageCount as number) - 1);
            }

            e.preventDefault();
        });
    }

    /**
     * Sets up the event listeners for page link buttons.
     */
    private bindPageLinkEvents(): void {
        const pageLinks = this.pagesContainer.children('.p-paginator-page');

        pageLinks.each((_i, el) => {
            const link = $(el),
            pageNumber = this.parseLocalizedInt(link.text());

            link.attr('aria-label', this.ariaPageLabel.replace('{page}', String(pageNumber)));
            if (link.hasClass('p-state-active')) {
                link.attr('aria-current', 'page');
            }
        });

        pageLinks.on('click.paginator', (e) => {
            const link = $(e.currentTarget),
            pageNumber = this.parseLocalizedInt(link.text());

            if(!link.hasClass('p-state-disabled')&&!link.hasClass('p-state-active')) {
                this.setPage(pageNumber - 1);
            }

            e.preventDefault();
        })
        .on('mouseover.paginator', (e) => {
            const item = $(e.currentTarget);
            if(!item.hasClass('p-state-disabled')&&!item.hasClass('p-state-active')) {
                item.addClass('p-state-hover');
            }
        })
        .on('mouseout.paginator', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('focus.paginator', (e) => {
            $(e.currentTarget).addClass('p-state-focus');
        })
        .on('blur.paginator', (e) => {
            $(e.currentTarget).removeClass('p-state-focus');
        })
        .on('keydown.paginator', (e) => {
            if(e.key === 'Enter') {
                $(e.currentTarget).trigger('click');
                e.preventDefault();
            }
        });
    }

    /**
     * Binds swipe events to this paginator to the JQ element passed in.
     * @param owner the owner JQ element of the paginator
     * @param ownerConfig the owner configuration to check if touch enabled or not
     */
    bindSwipeEvents(owner: JQuery, ownerConfig: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.BaseWidgetCfg>): void {
        if (!PrimeFaces.env.isTouchable(ownerConfig)) {
            return;
        }
        owner.swipe({
            swipeLeft: () => {
                this.prev();
            },
            swipeRight: () => {
                this.next();
            },
            excludedElements: PrimeFaces.utils.excludedSwipeElements()
        });
    }

    /**
     * Removes all event listeners.
     */
    unbindEvents(): void {
        const buttons = this.navigator.children('button.p-state-default');
        if (buttons.length > 0) {
            buttons.off();
        }
        const pageLinks = this.pagesContainer.children('.p-paginator-page');
        if (pageLinks.length > 0) {
            pageLinks.off();
        }
    }

    /**
     * Updates the UI so that it reflects the current pagination state.
     */
    updateUI(): void {
        //boundaries
        if(this.cfg.page === 0) {
            this.disableElement(this.firstLink);
            this.disableElement(this.prevLink);
        }
        else {
            this.enableElement(this.firstLink);
            this.enableElement(this.prevLink);
        }

        if(this.cfg.page === ((this.cfg.pageCount as number) - 1)) {
            this.disableElement(this.nextLink);
            this.disableElement(this.endLink);
        }
        else {
            this.enableElement(this.nextLink);
            this.enableElement(this.endLink);
        }

        //current page report
        const page = this.cfg.page as number,
        rows = this.cfg.rows as number,
        rowCount = this.cfg.rowCount as number;
        const startRecord = (rowCount === 0) ? 0 : (page * rows) + 1;
        let endRecord = (page * rows) + rows;
        if(endRecord > rowCount) {
            endRecord = rowCount;
        }

        const text = (this.cfg.currentPageTemplate as string)
            .replace("{currentPage}", String(page + 1))
            .replace("{totalPages}", String(this.cfg.pageCount))
            .replace("{totalRecords}", String(rowCount))
            .replace("{startRecord}", String(startRecord))
            .replace("{endRecord}", String(endRecord));
        this.currentReport.text(text);

        //rows per page dropdown
        if(this.cfg.prevRows !== this.cfg.rows) {
            this.rppSelect.filter(':not(.p-state-focus)').children('option').filter('option[value="' + CSS.escape(String(this.cfg.rows)) + '"]').prop('selected', true);
            this.cfg.prevRows = this.cfg.rows;
        }

        //jump to page dropdown
        const jtpSelect = this.jtpSelect[0];
        if(jtpSelect) {
            if(jtpSelect.options.length != this.cfg.pageCount){
                let jtpOptions = '';
                for(let i = 0; i < (this.cfg.pageCount as number); i++) {
                    jtpOptions += '<option value="' + i + '">' + (i + 1) + '</option>';
                }

                // GitHub #6929: performance improvement not using JQ html()
                jtpSelect.innerHTML = jtpOptions;
            }
            this.jtpSelect.children('option[value=' + (this.cfg.page) + ']').prop('selected','selected');
        }

        //jump to page input
        if(this.jtpInput.length > 0) {
            this.jtpInput.val(page + 1);
        }

        //page links
        this.updatePageLinks();
    }

    /**
     * Updates the UI of page link button so that they reflect the current pagination state.
     */
    private updatePageLinks(): void {
        let start: number, end: number, delta: number,
        focusContainer: JQuery | undefined;
        const focusedElement = $(document.activeElement as Element);

        if(focusedElement.hasClass('p-paginator-page')) {
            const pagesContainerIndex = this.pagesContainer.index(focusedElement.parent());
            if(pagesContainerIndex >= 0) {
                focusContainer = this.pagesContainer.eq(pagesContainerIndex);
            }
        }

        //calculate visible page links
        this.cfg.pageCount = Math.ceil((this.cfg.rowCount as number) / (this.cfg.rows as number))||1;
        const visiblePages = Math.min(this.cfg.pageLinks as number, this.cfg.pageCount);

        //calculate range, keep current in middle if necessary
        start = Math.max(0, Math.ceil((this.cfg.page as number) - ((visiblePages) / 2)));
        end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);

        //check when approaching to last page
        delta = (this.cfg.pageLinks as number) - (end - start + 1);
        start = Math.max(0, start - delta);

        //update dom
        this.pagesContainer.children().remove();
        for(let i = start; i <= end; i++) {
            let styleClass = 'p-paginator-page p-button p-button-flat p-state-default',
            ariaCurrentPage = '';
            const ariaLabel = this.ariaPageLabel.replace('{page}', String(i+1));

            if(this.cfg.page == i) {
                styleClass += ' p-state-active';
                ariaCurrentPage = 'aria-current="page"';
            }

            this.pagesContainer.append('<button class="' + styleClass + '" aria-label="' + ariaLabel + '" ' + ariaCurrentPage + ' tabindex="0" type="button">' + (i + 1) + '</button>');
        }

        if(focusContainer) {
            focusContainer.children().filter('.p-state-active').trigger('focus');
        }

        this.bindPageLinkEvents();
    }

    /**
     * Switches this pagination to the given page.
     * @param p 0-based index of the page to switch to.
     * @param silent `true` to not invoke any event listeners, `false` otherwise.
     */
    setPage(p: number, silent?: boolean): void {
        if(p >= 0 && p < (this.cfg.pageCount as number) && this.cfg.page != p){
            const newState: PrimeFacesTypes.widget.Paginator.PaginationState = {
                first: (this.cfg.rows as number) * p,
                rows: this.cfg.rows as number,
                page: p
            };

            if(silent) {
                this.cfg.page = p;
                this.updateUI();
            }
            else {
                this.cfg.paginate!.call(this, newState);
            }
        }
    }

    /**
     * Modifies the number of rows that are shown per page.
     * @param rpp Number of rows per page to set, `*` to show all rows.
     */
    setRowsPerPage(rpp: number | string): void {
        this.rppSelect.find('option').removeAttr('selected');
        this.cfg.rpp = rpp;
        if (rpp === '*') {
            this.cfg.rows = this.cfg.rowCount;
            this.cfg.pageCount = 1;
            this.cfg.page = 0;

            const newState: PrimeFacesTypes.widget.Paginator.PaginationState = {
                first: 0,
                // '*' is passed on as it is
                rows: rpp as unknown as number,
                page: this.cfg.page
            };

            this.cfg.paginate!.call(this, newState);
            this.rppSelect.val('*');
        }
        else {
            const first = (this.cfg.rows as number) * (this.cfg.page as number);
            this.cfg.rows = this.parseLocalizedInt(rpp);
            const page = this.parseLocalizedInt(first / this.cfg.rows);

            this.cfg.pageCount = Math.ceil((this.cfg.rowCount as number) / this.cfg.rows);
            this.cfg.page = -1;

            this.setPage(page);
        }
        this.rppSelect.find('option[value="'+rpp+'"]').attr('selected', 'selected');
    }

    /**
     * Modifies the total number of items that are available, and switches to the first page.
     * @param value The total number of items to set.
     */
    setTotalRecords(value: number): void {
        if (this.cfg.rpp === '*') {
            this.cfg.rows = value;
        }
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / (this.cfg.rows as number))||1;
        this.cfg.page = 0;
        this.updateUI();
    }

    /**
     * Modifies the total number of items that are available.
     * @param value The total number of items to set.
     */
    updateTotalRecords(value: number): void {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / (this.cfg.rows as number))||1;
        this.updateUI();
    }

    /**
     * Finds the index of the page that is currently displayed.
     * @returns 0-based index of the current page.
     */
    getCurrentPage(): number {
        return this.cfg.page as number;
    }

    /**
     * Finds the index of the item that is shown first on the current page.
     * @returns 0-based index of the first item on the current page.
     */
    getFirst(): number {
        return ((this.cfg.rows as number) * (this.cfg.page as number));
    }

    /**
     * Finds the current number of rows per page.
     * @returns The number of rows per page.
     */
    getRows(): number {
        return this.cfg.rows as number;
    }

    /**
     * Calculates the required height of the container with the items of the current page.
     * @param margin Whether to include the margin.
     * @returns The height of the items container in pixels
     */
    getContainerHeight(margin?: boolean): number {
        let height = 0;

        for(let i = 0; i < this.jq.length; i++) {
            height += this.jq.eq(i).outerHeight(margin) as number;
        }

        return height;
    }

    /**
     * Disables one of the items of this pagination.
     * @param element Element to disabled.
     */
    private disableElement(element: JQuery): void {
        element.removeClass('p-state-hover p-state-focus p-state-active').addClass('p-state-disabled').attr('tabindex', -1);
        element.removeClass('p-state-hover p-state-focus p-state-active').addClass('p-state-disabled').attr('tabindex', -1);
    }

    /**
     * Enables one of the items of this pagination.
     * @param element Element to disabled.
     */
    private enableElement(element: JQuery): void {
        element.removeClass('p-state-disabled').attr('tabindex', 0);
    }

    /**
     * Switches to the next page. Does nothing when this pagination is already on the last page.
     */
    next(): void {
        this.setPage((this.cfg.page as number) + 1);
    }

    /**
     * Switches to the previous page. Does nothing when this pagination is already on the first page.
     */
    prev(): void {
        this.setPage((this.cfg.page as number) - 1);
    }

    /**
     * Parses a localized integer value from a string by removing all dots and commas,
     * then parsing the result as a base-10 integer.
     * Example: "1.000" -> 1000
     * Example: "1,000" -> 1000
     * Example: "1000" -> 1000
     * @param value The string representation of a localized integer (may contain dots or commas as grouping separators).
     * @returns The parsed integer.
     */
    private parseLocalizedInt(value: string | number | null | undefined): number {
        if (value === null || value === undefined) {
            return NaN;
        }
        if (typeof value === 'string') {
           value = value.replace(/[.,]/g, '');
        }
        return parseInt(value as string, 10);
    }
}
