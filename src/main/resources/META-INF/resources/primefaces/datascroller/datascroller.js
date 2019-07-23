/**
 * PrimeFaces DataScroller Widget
 */
PrimeFaces.widget.DataScroller = PrimeFaces.widget.BaseWidget.extend({

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

                    this.list.css('top', top);
                    this.content.scrollTop(this.content[0].scrollHeight);
                }
            }

            this.content.on('scroll', function () {
                if($this.cfg.virtualScroll) {
                    if ($this.blockScrollEvent) {
                        $this.blockScrollEvent = false;
                        return;
                    }
                    
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
                    viewportHeight = this.clientHeight;
                
                    if((scrollTop >= ((scrollHeight * $this.cfg.buffer) - (viewportHeight))) && $this.shouldLoad()) {
                        $this.load();
                    }
                }
            });
        }
    },

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
            formId: this.cfg.formId,
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
    
    updateData: function(data, clear) {
        var empty = (clear === undefined) ? true: clear;

        if(empty)
            this.list.html(data);
        else
            this.list.append(data);
    },
    
    bindManualLoader: function() {
        var $this = this;

        this.loadTrigger.on('click.dataScroller', function(e) {
            $this.load();
            e.preventDefault();
        });
    },

    load: function() {
        this.loading = true;
        this.cfg.offset += this.cfg.chunkSize;

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
                        this.updateData(content, false);
                    }
                });

                return true;
            },
            oncomplete: function() {
                $this.loading = false;
                $this.allLoaded = ($this.cfg.offset + $this.cfg.chunkSize) >= $this.cfg.totalSize;

                $this.loadStatus.remove();

                if($this.loadTrigger && !$this.allLoaded) {
                    $this.loadTrigger.show();
                }
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    shouldLoad: function() {
        return (!this.loading && !this.allLoaded);
    }

});