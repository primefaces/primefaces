/**
 * PrimeFaces Mobile DataList Widget
 */
PrimeFaces.widget.DataList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.list = $(this.jqId + '_list');
        this.content = this.jq.children('.ui-datalist-content');
        this.list.listview();
        this.items = this.list.children('li');
        
        if(this.content.prevAll("[class^='ui-bar']").length) {
            this.jq.addClass('ui-datalist-topbar');
        }
        
        if(this.content.nextAll("[class^='ui-bar']").length) {
            this.jq.addClass('ui-datalist-bottombar');
        }
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.paginator) {
            this.bindPaginator();
        }
        
        if(this.cfg.behaviors) {
            var $this = this;
            
            $.each(this.cfg.behaviors, function(eventName, fn) {
                $this.items.on(eventName, function() {
                    var ext = {
                        params: [{name: $this.id + '_item', value: $(this).index()}]
                    };
                            
                    fn.call($this, ext);
                });
            });
        }
    },
    
    bindPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.list = $(this.jqId + '_list');
                            this.list.listview();
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];
            pageBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
     
    getPaginator: function() {
        return this.paginator;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    }
});