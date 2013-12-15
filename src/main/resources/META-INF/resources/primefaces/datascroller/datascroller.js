/**
 * PrimeFaces DataScroller Widget
 */
PrimeFaces.widget.DataScroller = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.content = this.jq.children('div.ui-datascroller-content');
        this.list = this.content.children('ul');
        this.loaderContainer = this.content.children('div.ui-datascroller-loader');
        this.loadStatus = $('<div class="ui-datascroller-loading"></div>');
        this.loading = false;
        this.allLoaded = false;
        this.cfg.offset = 0;
        this.cfg.mode = this.cfg.mode||'document';
        
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
            $this = this,
            NS = 'scroll.' + this.id;

            win.off(NS).on(NS, function () {
                if((win.scrollTop() === (doc.height() - win.height())) && $this.shouldLoad()) {
                    $this.load();
                }
            });
        }
        else {
            this.content.on('scroll', function () {
                var scrollTop = this.scrollTop,
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= (scrollHeight - (viewportHeight))) && $this.shouldLoad()) {
                    $this.load();
                }
            });
        }
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
            onsuccess: function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");

                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    content = PrimeFaces.ajax.AjaxUtils.getContent(update);

                    if(id === $this.id) {
                        $this.list.append(content);
                    }
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, responseXML);

                return true;
            },
            oncomplete: function() {
                $this.loading = false;
                $this.allLoaded = ($this.cfg.offset + $this.cfg.chunkSize) >= $this.cfg.totalSize;
                
                $this.loadStatus.remove();
                if($this.loadTrigger) {
                    $this.loadTrigger.show();
                }
                
            }
        };
        
        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    shouldLoad: function() {
        return (!this.loading && !this.allLoaded);
    }
    
});