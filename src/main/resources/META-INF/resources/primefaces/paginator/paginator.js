/**
 * __PrimeFaces Paginator Widget__
 * 
 * A widget for handling pagination that is usually used by other widget via composition, that is, they create and save
 * an instance of this widget during initialization. After you create a new instance of this paginator, you should set
 * the `paginate` property to an appropriate callback function.
 * 
 * ```javascript
 * const paginator = new PrimeFaces.widget.Paginator(paginatorCfg);
 * paginator.paginator = newState => {
 *  // handle pagination
 * };
 * ```
 * 
 * @typedef PrimeFaces.widget.Paginator.PaginateCallback A callback method that is invoked when the pagination state
 * changes, see {@link PaginatorCfg.paginate}.
 * @param {PrimeFaces.widget.Paginator.PaginationState} PrimeFaces.widget.Paginator.PaginateCallback.newState The new
 * values for the current page and the rows per page count. 
 * 
 * @interface {PrimeFaces.widget.Paginator.PaginationState} PaginatorState Represents a pagination state, that is, a
 * range of items that should be displayed.
 * @prop {number} PaginatorState.first 0-based index of the first item on the current page.
 * @prop {number} PaginatorState.rows The number of rows per page.
 * @prop {number} PaginatorState.page The current page, 0-based index.
 * 
 * @prop {JQuery} currentReport DOM element of the status text as configured by the `currentPageTemplate`.
 * @prop {JQuery} endLink DOM element of the link to the last page.
 * @prop {JQuery} firstLink DOM element of the link back to the first page.
 * @prop {JQuery} jtpInput INPUT element for selecting a page to navigate to (`jump to page`)
 * @prop {JQuery} jtpSelect SELECT element for selecting a page to navigate to (`jump to page`)
 * @prop {JQuery} nextLink DOM element of the link to the next page.
 * @prop {JQuery} pagesContainer DOM element of the container with the numbered page links.
 * @prop {JQuery} pageLinks DOM elements of each numbered page link.
 * @prop {JQuery} prevLink DOM element of the link back to the previous page.
 * @prop {JQuery} rppSelect SELECT element for selection the number of pages to display (`rows per page`).
 * 
 * @interface {PrimeFaces.widget.PaginatorCfg} cfg The configuration for the {@link  Paginator| Paginator widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.alwaysVisible `true` if the paginator should be displayed always, or `false` if it is allowed to
 * be hidden under some circumstances that depend on the widget that uses the paginator.
 * @prop {string} cfg.ariaPageLabel ARIA LABEL attribute for the page links.
 * @prop {string} cfg.currentPageTemplate Template for the paginator text. It may contain placeholders such as
 * `{currentPage}` or `{totalPages}`. 
 * @prop {number} cfg.page The current page, 0-based index.
 * @prop {number} cfg.pageCount The number of pages.
 * @prop {number} cfg.pageLinks The maximum number of page links to display (when there are many pages).
 * @prop {PrimeFaces.widget.Paginator.PaginateCallback} cfg.paginate A callback method that is invoked when the
 * pagination state changes, such as when the user selects a different page or changes the current rows per page count.
 * This property is usually provided by another widget that makes use of this paginator. You should use this callback to
 * perform any actions required to apply the new pagination state.
 * @prop {number} cfg.prevRows The number of rows per page for the dropdown.
 * @prop {number} cfg.rowCount Total number of rows (records) to be displayed.
 * @prop {number} cfg.rows The number of rows per page.
 */
PrimeFaces.widget.Paginator = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        //elements
        this.pagesContainer = this.jq.children('.ui-paginator-pages');
        this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
        this.rppSelect = this.jq.children('.ui-paginator-rpp-options');
        this.jtpSelect = this.jq.children('.ui-paginator-jtp-select');
        this.jtpInput = this.jq.children('.ui-paginator-jtp-input');
        this.firstLink = this.jq.children('.ui-paginator-first');
        this.prevLink  = this.jq.children('.ui-paginator-prev');
        this.nextLink  = this.jq.children('.ui-paginator-next');
        this.endLink   = this.jq.children('.ui-paginator-last');
        this.currentReport = this.jq.children('.ui-paginator-current');

        //metadata
        this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
        this.cfg.prevRows = this.cfg.rows;
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        this.cfg.pageLinks = this.cfg.pageLinks||10;
        this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPages})';

        //aria message
        this.cfg.ariaPageLabel = PrimeFaces.getAriaLabel('paginator.PAGE');

        //event bindings
        this.bindEvents();
    },

    /**
     * Sets up all event listeners for this widget.
     * @private
     */
    bindEvents: function(){
        var $this = this;

        //visuals for first,prev,next,last buttons
        this.jq.children('a.ui-state-default').on('mouseover.paginator', function(){
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-focus');
            }
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });

        //page links
        this.bindPageLinkEvents();

        //records per page selection
        PrimeFaces.skinSelect(this.rppSelect);
        this.rppSelect.on('change', function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setRowsPerPage($(this).val());
            }
        });

        //jump to page dropdown
        PrimeFaces.skinSelect(this.jtpSelect);
        this.jtpSelect.on('change', function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(parseInt($(this).val()));
            }
        });

        //jump to page input
        PrimeFaces.skinInput(this.jtpInput);
        this.jtpInput.on('change', function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                var page = parseInt($(this).val());
                if (isNaN(page) || page > $this.cfg.pageCount || page < 1) {
                    // restore old value if invalid
                    $(this).val($this.cfg.page + 1);
                }
                else {
                    $this.setPage(page - 1);
                }
            }
        });

        //First page link
        this.firstLink.on("click", function(e) {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(0);
            }

            e.preventDefault();
        });

        //Prev page link
        this.prevLink.on("click", function(e) {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page - 1);
            }

            e.preventDefault();
        });

        //Next page link
        this.nextLink.on("click", function(e) {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page + 1);
            }

            e.preventDefault();
        });

        //Last page link
        this.endLink.on("click", function(e) {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.pageCount - 1);
            }

            e.preventDefault();
        });
    },

    /**
     * Sets up the event listeners for page link buttons.
     * @private
     */
    bindPageLinkEvents: function(){
        var $this = this,
        pageLinks = this.pagesContainer.children('.ui-paginator-page');

        pageLinks.each(function() {
            var link = $(this),
            pageNumber = parseInt(link.text());

            link.attr('aria-label', $this.cfg.ariaPageLabel.replace('{0}', (pageNumber)));
        });

        pageLinks.on('click.paginator', function(e) {
            var link = $(this),
            pageNumber = parseInt(link.text());

            if(!link.hasClass('ui-state-disabled')&&!link.hasClass('ui-state-active')) {
                $this.setPage(pageNumber - 1);
            }

            e.preventDefault();
        })
        .on('mouseover.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')&&!item.hasClass('ui-state-active')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });
    },

    /**
     * Binds swipe events to this paginator to the JQ element passed in.
     * @private
     * @param {JQuery} owner the owner JQ element of the paginator
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} ownerConfig the owner configuration to check if touch enabled or not
     */
    bindSwipeEvents: function(owner, ownerConfig) {
        if (!PrimeFaces.env.isTouchable(ownerConfig)) {
            return;
        }
        var $this = this;
        owner.swipe({
            swipeLeft:function(event) {
                $this.prev();
            },
            swipeRight: function(event) {
                $this.next();
            },
            excludedElements: PrimeFaces.utils.excludedSwipeElements()
        });
    },

   /**
    * Removes all event listeners.
    * @private
    */
   unbindEvents: function() {
        var buttons = this.jq.children('a.ui-state-default');
        if (buttons.length > 0) {
            buttons.off();
        }
        var pageLinks = this.pagesContainer.children('.ui-paginator-page');
        if (pageLinks.length > 0) {
            pageLinks.off();
        }
    },

    /**
     * Updates the UI so that it reflects the current pagination state.
     * @private
     */
    updateUI: function() {
        //boundaries
        if(this.cfg.page === 0) {
            this.disableElement(this.firstLink);
            this.disableElement(this.prevLink);
        }
        else {
            this.enableElement(this.firstLink);
            this.enableElement(this.prevLink);
        }

        if(this.cfg.page === (this.cfg.pageCount - 1)) {
            this.disableElement(this.nextLink);
            this.disableElement(this.endLink);
        }
        else {
            this.enableElement(this.nextLink);
            this.enableElement(this.endLink);
        }

        //current page report
        var startRecord = (this.cfg.rowCount === 0) ? 0 : (this.cfg.page * this.cfg.rows) + 1,
        endRecord = (this.cfg.page * this.cfg.rows) + this.cfg.rows;
        if(endRecord > this.cfg.rowCount) {
            endRecord = this.cfg.rowCount;
        }

        var text = this.cfg.currentPageTemplate
            .replace("{currentPage}", this.cfg.page + 1)
            .replace("{totalPages}", this.cfg.pageCount)
            .replace("{totalRecords}", this.cfg.rowCount)
            .replace("{startRecord}", startRecord)
            .replace("{endRecord}", endRecord);
        this.currentReport.text(text);

        //rows per page dropdown
        if(this.cfg.prevRows !== this.cfg.rows) {
            this.rppSelect.filter(':not(.ui-state-focus)').children('option').filter('option[value="' + $.escapeSelector(this.cfg.rows) + '"]').prop('selected', true);
            this.cfg.prevRows = this.cfg.rows;
        }

        //jump to page dropdown
        if(this.jtpSelect.length > 0) {
            if(this.jtpSelect[0].options.length != this.cfg.pageCount){
                var jtpOptions = '';
                for(var i = 0; i < this.cfg.pageCount; i++) {
                    jtpOptions += '<option value="' + i + '">' + (i + 1) + '</option>';
                }

                // GitHub #6929: performance improvement not using JQ html()
                this.jtpSelect[0].innerHTML = jtpOptions;
            }
            this.jtpSelect.children('option[value=' + (this.cfg.page) + ']').prop('selected','selected');
        }

        //jump to page input
        if(this.jtpInput.length > 0) {
            this.jtpInput.val(this.cfg.page + 1);
        }

        //page links
        this.updatePageLinks();
    },

    /**
     * Updates the UI of page link button so that they reflect the current pagination state.
     * @private
     */
    updatePageLinks: function() {
        var start, end, delta,
        focusedElement = $(document.activeElement),
        focusContainer;

        if(focusedElement.hasClass('ui-paginator-page')) {
            var pagesContainerIndex = this.pagesContainer.index(focusedElement.parent());
            if(pagesContainerIndex >= 0) {
                focusContainer = this.pagesContainer.eq(pagesContainerIndex);
            }
        }

        //calculate visible page links
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        var visiblePages = Math.min(this.cfg.pageLinks, this.cfg.pageCount);

        //calculate range, keep current in middle if necessary
        start = Math.max(0, Math.ceil(this.cfg.page - ((visiblePages) / 2)));
        end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);

        //check when approaching to last page
        delta = this.cfg.pageLinks - (end - start + 1);
        start = Math.max(0, start - delta);

        //update dom
        this.pagesContainer.children().remove();
        for(var i = start; i <= end; i++) {
            var styleClass = 'ui-paginator-page ui-state-default ui-corner-all',
            ariaLabel = this.cfg.ariaPageLabel.replace('{0}', (i+1));

            if(this.cfg.page == i) {
                styleClass += " ui-state-active";
            }

            this.pagesContainer.append('<a class="' + styleClass + '" aria-label="' + ariaLabel + '" tabindex="0" href="#">' + (i + 1) + '</a>');
        }

        if(focusContainer) {
            focusContainer.children().filter('.ui-state-active').trigger('focus');
        }

        this.bindPageLinkEvents();
    },

    /**
     * Switches this pagination to the given page.
     * @param {number} p 0-based index of the page to switch to.
     * @param {boolean} [silent=false] `true` to not invoke any event listeners, `false` otherwise. 
     */
    setPage: function(p, silent) {
        if(p >= 0 && p < this.cfg.pageCount && this.cfg.page != p){
            var newState = {
                first: this.cfg.rows * p,
                rows: this.cfg.rows,
                page: p
            };

            if(silent) {
                this.cfg.page = p;
                this.updateUI();
            }
            else {
                this.cfg.paginate.call(this, newState);
            }
        }
    },

    /**
     * Modifies the number of rows that are shown per page.
     * @param {number} rpp Number of rows per page to set.
     */
    setRowsPerPage: function(rpp) {
        if (rpp === '*') {
            this.cfg.rows = this.cfg.rowCount;
            this.cfg.pageCount = 1;
            this.cfg.page = 0;

            var newState = {
                first: 0,
                rows: rpp,
                page: this.cfg.page
            };

            this.cfg.paginate.call(this, newState);
        }
        else {
            var first = this.cfg.rows * this.cfg.page;
            this.cfg.rows = parseInt(rpp);
            var page = parseInt(first / this.cfg.rows);

            this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
            this.cfg.page = -1;

            this.setPage(page);
        }
    },

    /**
     * Modifies the total number of items that are available, and switches to the first page.
     * @param {number} value The total number of items to set.
     */
    setTotalRecords: function(value) {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
        this.cfg.page = 0;
        this.updateUI();
    },

    /**
     * Modifies the total number of items that are available.
     * @param {number} value The total number of items to set.
     * @private
     */
    updateTotalRecords: function(value) {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
        this.updateUI();
    },

    /**
     * Finds the index of the page that is currently displayed.
     * @return {number} 0-based index of the current page.
     */
    getCurrentPage: function() {
        return this.cfg.page;
    },

    /**
     * Finds the index of the item that is shown first on the current page.
     * @return {number} 0-based index of the first item on the current page.
     */
    getFirst: function() {
        return (this.cfg.rows * this.cfg.page);
    },

    /**
     * Finds the current number of rows per page.
     * @return {number} The number of rows per page.
     */
    getRows: function() {
        return this.cfg.rows;
    },

    /**
     * Calculates the required height of the container with the items of the current page.
     * @private
     * @param {number} margin Additional margin in pixels to consider.
     * @return {number} The height of the items container in pixels
     */
    getContainerHeight: function(margin) {
        var height = 0;

        for(var i = 0; i < this.jq.length; i++) {
            height += this.jq.eq(i).outerHeight(margin);
        }

        return height;
    },

    /**
     * Disables one of the items of this pagination.
     * @private
     * @param {JQuery} element Element to disabled.
     */
    disableElement: function(element) {
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
    },

    /**
     * Enables one of the items of this pagination.
     * @private
     * @param {JQuery} element Element to disabled.
     */
    enableElement: function(element) {
        element.removeClass('ui-state-disabled').attr('tabindex', 0);
    },

    /**
     * Switches to the next page. Does nothing when this pagination is already on the last page.
     */
    next: function() {
        this.setPage(this.cfg.page + 1);
    },

    /**
     * Switches to the previous page. Does nothing when this pagination is already on the first page.
     */
    prev: function() {
        this.setPage(this.cfg.page - 1);
    }
});
