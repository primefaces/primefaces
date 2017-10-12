/**
 * PrimeFaces Sidebar Widget
 */
PrimeFaces.widget.Sidebar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.closeIcon = this.jq.children('.ui-sidebar-close');
        this.cfg.baseZIndex = this.cfg.baseZIndex||0;
        
        if(this.cfg.appendTo) {
            this.parent = this.jq.parent();
            this.targetParent = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo);
            
            if (!this.parent.is(this.targetParent)) {
                this.jq.appendTo(this.targetParent);
            }
        }
        
        //remove related modality if there is one
        var modal = $(this.jqId + '_modal');
        if(modal.length > 0) {
            modal.remove();
        }
        
        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }
        
        this.bindEvents();
    },
    
    //override
    refresh: function(cfg) {
        this.mask = false;

        if(cfg.appendTo) {
            var jqs = $('[id=' + cfg.id.replace(/:/g,"\\:") + ']');
            if(jqs.length > 1) {
            	PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(cfg.appendTo).children(this.jqId).remove();
            }
        }

        this.init(cfg);
    },
    
    bindEvents: function() {
        var $this = this;

        this.closeIcon.on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        }).on('focus', function() {
            $(this).addClass('ui-state-focus');
        }).on('blur', function() {
            $(this).removeClass('ui-state-focus');
        }).on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });
    },
    
    show: function() {
        if(this.isVisible()) {
            return;
        }

        this.jq.addClass('ui-sidebar-active');
        this.jq.css('z-index', this.cfg.baseZIndex + (++PrimeFaces.zindex));
        
        this.postShow();
        this.enableModality();
    },
    
    postShow: function() {
        PrimeFaces.invokeDeferredRenders(this.id);

        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.jq.attr({
            'aria-hidden': false
            ,'aria-live': 'polite'
        });
    },
    
    hide: function() {
        if(!this.isVisible()) {
            return;
        }

        this.jq.removeClass('ui-sidebar-active');
        this.onHide();
        this.disableModality();
    },
    
    isVisible: function() {
        return this.jq.hasClass('ui-sidebar-active');
    },
    
    onHide: function(event, ui) {
        this.jq.attr({
            'aria-hidden': true
            ,'aria-live': 'off'
        });

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
    },

    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    },
    
    enableModality: function() {
        if(!this.mask) {
            var $this = this, 
            docBody = $(document.body);
           
            this.mask = $('<div id="' + this.id + '_modal" class="ui-widget-overlay ui-sidebar-mask"></div>');
            this.mask.css('z-index' , this.jq.css('z-index') - 1);
            docBody.append(this.mask);
            
            this.mask.on('click.sidebar-mask', function() {
                $this.hide(); 
            });
        
            if(this.cfg.blockScroll) {
                docBody.addClass('ui-overflow-hidden');
            }
        }
    },
        
    disableModality: function() {
        if(this.mask) {
            var docBody = $(document.body);
            this.mask.off('click.sidebar-mask');
            this.mask.remove();
            
            if(this.blockScroll) {
                docBody.removeClass('ui-overflow-hidden');
            }
            this.mask = null;
        }
    },
    
    applyARIA: function() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-hidden': !this.cfg.visible
        });

        this.closeIcon.attr('role', 'button');
    }
    
});