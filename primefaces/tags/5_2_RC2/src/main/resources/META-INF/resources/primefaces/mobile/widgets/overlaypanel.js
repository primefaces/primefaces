/**
 * PrimeFaces Mobile OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.showEvent = this.cfg.showEvent||'click.overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.overlaypanel';
        this.cfg.target = this.cfg.targetId ? $(PrimeFaces.escapeClientId(this.cfg.targetId)): null;
        
        this.jq.panel({
            position: this.cfg.at,
            display: this.cfg.showEffect,
            dismissable: this.cfg.dismissable
        });
        
        if(this.cfg.dynamic) {
            this.jq.append('<div class="ui-panel-inner"></div>');
            this.content = this.jq.children('div.ui-panel-inner');
        }

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        if(this.cfg.target) {
            if(this.cfg.showEvent === this.cfg.hideEvent) {
                this.cfg.target.on(this.cfg.showEvent, function(e) {
                    $this.toggle();
                });
            }
            else {
                this.cfg.target.on(this.cfg.showEvent, function(e) {
                    $this.show();
                })
                .on(this.cfg.hideEffect, function(e) {
                    $this.hide();
                });
            }
        }
    },
    
    show: function() {
        if(!this.loaded && this.cfg.dynamic)
            this.loadContents();
        else
            this._show();
    },
    
    _show: function() {
        this.jq.panel('open');
        
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },
    
    hide: function() {
        this.jq.panel('close');
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },
    
    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    },
    
    loadContents: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    isVisible: function() {
        this.jq.is(':visible');
    }
});
