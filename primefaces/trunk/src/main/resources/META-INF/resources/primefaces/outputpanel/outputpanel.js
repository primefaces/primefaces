/**
 * PrimeFaces OutputPanel Widget
 */
PrimeFaces.widget.OutputPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        if(this.cfg.deferred) {
            if(this.cfg.deferredMode === 'load') {
                this.loadContent();
            }
            else if(this.cfg.deferredMode === 'visible') {
                if(this.visible())
                    this.loadContent();
                else
                    this.bindScrollMonitor();
            }
        }
    },
            
    loadContent: function() {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            ignoreAutoUpdate: true
        },
        $this = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id === $this.id) {
                    $this.jq.html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };
        
        options.params = [{name: this.id + '_load', value: true}];
        
        PrimeFaces.ajax.AjaxRequest(options);
    },
            
    bindScrollMonitor: function() {
        var $this = this,
        win = $(window);
        win.off('scroll.' + this.id).on('scroll.' + this.id, function() {
            if($this.visible()) {
                $this.unbindScrollMonitor();
                $this.loadContent();
            }
        });
    },
            
    visible: function() {
        var win = $(window),
        scrollTop = win.scrollTop(),
        height = win.height(),
        top = this.jq.offset().top,
        bottom = top + this.jq.innerHeight();

        if((top >= scrollTop && top <= (scrollTop + height)) || (bottom >= scrollTop && bottom <= (scrollTop + height))) {
            return true;
        }
    },
            
    unbindScrollMonitor: function() {
        $(window).off('scroll.' + this.id);
    }
});