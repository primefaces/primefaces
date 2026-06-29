/**
 * __PrimeFaces DataScroller Widget__
 * 
 * DataScroller displays a collection of data with on demand loading using scrolling.
 * 
 * @typedef {"document" | "inline"} PrimeFaces.widget.DataScroller.Mode Target to listen to for the scroll event.
 * `document` registers a delegated listener on the document element, `inline` registers it on an element of the data
 * scroller.
 * 
 * @typedef {"scroll" | "manual"} PrimeFaces.widget.DataScroller.LoadEvent Defines when more items are loaded by the
 * data scroller. `scroll` loads more items as the user scrolls down the page, `manual` loads more items only when the
 * user click the `more` button. 
 * 
 * @prop {boolean} allLoaded `true` if all items were loaded and there are no more items to be loaded, or `false`
 * otherwise.
 * @prop {JQuery} content DOM element of the container for the content with the data scroller.
 * @prop {JQuery} list DOM element of the list with the data items.
 * @prop {boolean} loading `true` if an AJAX request for loading more items is currently process, or `false` otherwise.
 * @prop {JQuery} loaderContainer DOM element of the container with the `more` button for loading more items.
 * @prop {JQuery} loadStatus DOM element of the status text or icon shown while loading.
 * @prop {JQuery} loadTrigger DOM element of the `more` button for loading more item manually.
 * 
 * @interface {PrimeFaces.widget.DataScrollerCfg} cfg The configuration for the {@link  DataScroller| DataScroller widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number} cfg.buffer Percentage height of the buffer between the bottom of the page and the scroll position to
 * initiate the load for the new chunk. For example, a value of `10` means that loading happens after the user has
 * scrolled down to at least `90%` of the viewport height.
 * @prop {number} cfg.chunkSize Number of items to load on each load.
 * @prop {PrimeFaces.widget.DataScroller.LoadEvent} cfg.loadEvent Defines when more items are loaded.
 * @prop {PrimeFaces.widget.DataScroller.Mode} cfg.mode Defines the target to listen for scroll event.
 * @prop {number} cfg.offset Number of additional items currently loaded.
 * @prop {boolean} cfg.startAtBottom `true` to set the scroll position to the bottom initally and load data from the
 * bottom, or `false` otherwise.
 * @prop {number} cfg.totalSize The total number of items that can be displayed.
 * @prop {boolean} cfg.virtualScroll Loads data on demand as the scrollbar gets close to the bottom.
 */
PrimeFaces.widget.DataScroller = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.content = this.jq.children('div.ui-datascroller-content');
        this.list = this.cfg.virtualScroll ? this.content.children('div').children('ul') : this.content.children('ul');
        this.loaderContainer = this.content.children('div.ui-datascroller-loader');
        this.loadStatus = $('<div class="ui-datascroller-loading"></div>');
        this.loading = false;
        this.allLoaded = false;
        this.cfg.offset = 0;
        this.cfg.mode = this.cfg.mode||'document';
        this.cfg.buffer = (100 - this.cfg.buffer) / 100;

        if(this.cfg.loadEvent === 'scroll') {
            this.bindScrollListener();
        }
        else {
            this.loadTrigger = this.loaderContainer.children();
            this.bindManualLoader();
        }
    },

    /**
     * Sets up the event listeners for the scroll event, to load more items on-demand.
     * @private
     */
    bindScrollListener: function() {
        var $this = this;

        if(this.cfg.mode === 'document') {
            var win = $(window),
            doc = $(document),
            $this = this;

            PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
                if (win.scrollTop() >= ((doc.height() * $this.cfg.buffer) - win.height()) && $this.shouldLoad()) {
                    $this.load();
                }
            });
        }
        else {
            this.itemHeight = 0;

            if(this.cfg.virtualScroll) {
                var item = this.list.children('li.ui-datascroller-item');
                if(item) {
                    this.itemHeight = item.outerHeight();
                    this.content.children('div').css('min-height', parseFloat((this.cfg.totalSize * this.itemHeight) + 'px'));
                }

                if (this.cfg.startAtBottom) {
                    var pageHeight = this.itemHeight * this.cfg.chunkSize,
                    virtualListHeight = parseFloat(this.cfg.totalSize * this.itemHeight),
                    viewportHeight = this.content.height(),
                    pageCount = Math.floor(virtualListHeight / pageHeight)||1,
                    page = (this.cfg.totalSize % this.cfg.chunkSize) == 0 ? pageCount - 2 : pageCount - 1,
                    top = (virtualListHeight < viewportHeight) ? (viewportHeight - virtualListHeight) : (Math.max(page, 0) * pageHeight);

                    this.list.css('top', top + 'px');
                    this.content.scrollTop(this.content[0].scrollHeight);
                }
            }
            else if (this.cfg.startAtBottom) {                
                this.content.scrollTop(this.content[0].scrollHeight);
                this.cfg.offset = this.cfg.totalSize > this.cfg.chunkSize ? this.cfg.totalSize - this.cfg.chunkSize : this.cfg.totalSize;
                
                var paddingTop = '0';
                if (this.content.height() > this.list.height()) {
                    paddingTop = (this.getInnerContentHeight() - this.list.outerHeight() - this.loaderContainer.outerHeight());
                }
                
                this.list.css('padding-top', paddingTop + 'px');
            }
            
            this.content.on('scroll', function () {
                if($this.cfg.virtualScroll) {                    
                    var virtualScrollContent = this;
                    
                    clearTimeout($this.scrollTimeout);
                    $this.scrollTimeout = setTimeout(function() {
                        var viewportHeight = $this.content.outerHeight(),
                        listHeight = $this.list.outerHeight() + Math.ceil(viewportHeight - $this.content.height()),
                        pageHeight = $this.itemHeight * $this.cfg.chunkSize,
                        virtualListHeight = parseFloat($this.cfg.totalSize * $this.itemHeight),
                        pageCount = (virtualListHeight / pageHeight)||1;

                        if(virtualScrollContent.scrollTop + viewportHeight > parseFloat($this.list.css('top')) + listHeight || virtualScrollContent.scrollTop < parseFloat($this.list.css('top'))) {
                            var page = Math.floor((virtualScrollContent.scrollTop * pageCount) / (virtualScrollContent.scrollHeight)) + 1;
                            $this.loadRowsWithVirtualScroll(page, function () {
                                $this.list.css('top', ((page - 1) * pageHeight) + 'px');
                            });
                        }
                    }, 200);
                }
                else {                    
                    var scrollTop = this.scrollTop,
                    scrollHeight = this.scrollHeight,
                    viewportHeight = this.clientHeight,
                    shouldLoad = $this.shouldLoad() && ($this.cfg.startAtBottom ?
                                (scrollTop <= (scrollHeight - (scrollHeight * $this.cfg.buffer))) && ($this.cfg.totalSize > $this.cfg.chunkSize)
                                :
                                (scrollTop >= ((scrollHeight * $this.cfg.buffer) - viewportHeight)));
                    if (shouldLoad) {
                        $this.load();
                    }
                }
            });
        }
    },

    /**
     * Loads more items and inserts them into the DOM so that the user can see them.
     * @private
     * @param {number} page The page of the items to load. The items are grouped into pages, each page containts
     * `chunkSize` items. 
     * @param {() => void} callback Callback that is invoked when the new items have been loaded and inserted into the
     * DOM.
     */
    loadRowsWithVirtualScroll: function(page, callback) {
        if(this.virtualScrollActive) {
            return;
        }

        this.virtualScrollActive = true;

        var $this = this,
        first = (page - 1) * this.cfg.chunkSize,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_virtualScrolling', value: true},
                     {name: this.id + '_first', value: first}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        //insert new rows
                        this.updateData(content);
                        callback();
                        this.virtualScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                if(typeof args.totalSize !== 'undefined') {
                    $this.cfg.totalSize = args.totalSize;
                }
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    /**
     * Inserts newly loaded items into the DOM.
     * @private
     * @param {string} data New HTML content of the items to insert. 
     * @param {boolean} [clear] `true` to clear all currently existing items, or `false` otherwise.
     * @param {boolean} [pre] `true` to prepend the items, or `false` or `undefined` to append the items to the list of
     * items.
     */
    updateData: function(data, clear, pre) {
        var empty = (clear === undefined) ? true: clear;

        if(empty)
            this.list.html(data);
        else if (pre)
            this.list.prepend(data);
        else
            this.list.append(data);
    },
    
    /**
     * Sets up the event listeners for the click on the `more` button.
     * @private
     */
    bindManualLoader: function() {
        var $this = this;

        this.loadTrigger.on('click.dataScroller', function(e) {
            $this.load();
            e.preventDefault();
        });
    },

    /**
     * Loads more items from the server. Usually triggered either when the user scrolls down or when they click on the
     * `more` button.
     */
    load: function() {
        this.loading = true;
        this.cfg.offset += (this.cfg.chunkSize * (this.cfg.startAtBottom ? -1 : 1));

        this.loadStatus.appendTo(this.loaderContainer);
        if(this.loadTrigger) {
            this.loadTrigger.hide();
        }

        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_load', value: true},{name: this.id + '_offset', value: this.cfg.offset}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.updateData(content, false, $this.cfg.startAtBottom);
                    }
                });

                return true;
            },
            oncomplete: function() {
                if ($this.cfg.offset < 0) {
                    $this.cfg.offset = 0;
                }

                $this.loading = false;
                $this.allLoaded = ($this.cfg.startAtBottom) ? $this.cfg.offset == 0 : ($this.cfg.offset + $this.cfg.chunkSize) >= $this.cfg.totalSize;

                $this.loadStatus.remove();

                if($this.loadTrigger && !$this.allLoaded) {
                    $this.loadTrigger.show();
                }
            }
        };

        if(this.hasBehavior('load')) {
            this.callBehavior('load', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Checks whether more items can be loaded now. Item are not allowed to be loaded when an AJAX request is currently
     * in process, or when all items have been loaded already.
     * @return {boolean} `true` if more items are allowed to be loaded, `false` otherwise.
     */
    shouldLoad: function() {
        return (!this.loading && !this.allLoaded);
    },
            
    /**
     * Finds the height of the content, excluding the padding.
     * @private
     * @return {number} The inner height of the content element.
     */
    getInnerContentHeight: function() {
        return (this.content.innerHeight() - parseFloat(this.content.css('padding-top')) - parseFloat(this.content.css('padding-bottom')));
    }
    
});