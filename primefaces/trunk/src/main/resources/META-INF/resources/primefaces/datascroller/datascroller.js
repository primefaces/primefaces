/**
 * PrimeFaces DataScroller Widget
 */
PrimeFaces.widget.DataScroller = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.list = this.jq.children('ul');
        this.loading = false;
        this.cfg.offset = this.cfg.chunkSize;
        
        var win = $(window),
        doc = $(document),
        $this = this;

        this.shouldLoad();

        win.scroll(function () {
            if((win.scrollTop() === (doc.height() - win.height()) && $this.shouldLoad())) {
                $this.load();
            }
        });
    },
    
    load: function() {
        this.loading = true;
        
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
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
                $this.cfg.offset += $this.cfg.chunkSize;
                $this.loading = false;
            }
        };
        
        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    shouldLoad: function() {
        return (!this.loading && ((this.cfg.offset + this.cfg.chunkSize) <= this.cfg.totalSize));
    }
});