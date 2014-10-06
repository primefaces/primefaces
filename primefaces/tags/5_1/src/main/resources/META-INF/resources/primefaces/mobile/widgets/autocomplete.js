/**
 * PrimeFaces Mobile AutoComplete Widget
 */
PrimeFaces.widget.AutoComplete = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.minLength = (this.cfg.minLength !== undefined) ? this.cfg.minLength : 1;
        this.cfg.delay = (this.cfg.delay !== undefined) ? this.cfg.delay : 300;
        this.inputContainer = this.jq.children('.ui-input-search');
        this.input = $(this.jqId + '_input');
        this.hinput = $(this.jqId + '_hinput');
        this.clearIcon = this.inputContainer.children('.ui-input-clear');
        this.cfg.pojo = (this.hinput.length === 1);
        this.panel = this.jq.children('.ui-controlgroup');
        this.itemContainer = this.panel.children('.ui-controlgroup-controls');
        
        this.bindEvents();
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {
        var $this = this;

        this.input.on('keyup.autoComplete', function(e) {
            var value = $this.input.val();

            if(value.length === 0) {
                $this.hide();
            }
            else {
                $this.showClearIcon();
            }

            if(value.length >= $this.cfg.minLength) {
                //Cancel the search request if user types within the timeout
                if($this.timeout) {
                    clearTimeout($this.timeout);
                }

                $this.timeout = setTimeout(function() {
                    $this.search(value);
                }, $this.cfg.delay);
            }
        });
        
        this.clearIcon.on('click.autoComplete', function(e) {
            $this.input.val('');
            $this.hinput.val('');
            $this.hide();
        });
    },
    
    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.on('click.autoComplete', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value');

            $this.input.val(item.attr('data-item-label')).focus();

            if($this.cfg.pojo) {
                $this.hinput.val(itemValue); 
            }

            $this.fireItemSelectEvent(event, itemValue);
            $this.hide();
        });
    },
    
    search: function(query) {
        //allow empty string but not undefined or null
        if(query === undefined || query === null) {
            return;
        }

        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.itemContainer.html(content);

                        this.showSuggestions();
                    }
                });
                
                return true;
            }
        };

        options.params = [
            {name: this.id + '_query', value: query}  
        ];
        
        if(this.hasBehavior('query')) {
            var queryBehavior = this.cfg.behaviors['query'];
            queryBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    show: function() {
        this.panel.removeClass('ui-screen-hidden');
    },
    
    hide: function() {        
        this.panel.addClass('ui-screen-hidden');
        this.hideClearIcon();
    },
    
    showSuggestions: function() {
        this.items = this.itemContainer.children('.ui-autocomplete-item');                   
        this.bindDynamicEvents();
                
        if(this.items.length) {
            this.items.first().addClass('ui-first-child');
            this.items.last().addClass('ui-last-child');
            
            if(this.panel.is(':hidden')) {
                this.show();
            }
        }
        else {
            if(this.cfg.emptyMessage) { 
                var emptyText = '<div class="ui-autocomplete-emptyMessage ui-widget">'+this.cfg.emptyMessage+'</div>';
                this.itemContainer.html(emptyText);
            }
            else {
                this.hide();
            }
        }
    },
    
    fireItemSelectEvent: function(event, itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.cfg.behaviors['itemSelect'].call(this, ext);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    },
    
    showClearIcon: function() {
        this.clearIcon.removeClass('ui-input-clear-hidden');
    },
    
    hideClearIcon: function() {
        this.clearIcon.addClass('ui-input-clear-hidden');
    }
    
});