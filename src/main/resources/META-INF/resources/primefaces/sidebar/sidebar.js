/**
 * PrimeFaces Sidebar Widget
 */
PrimeFaces.widget.Sidebar = PrimeFaces.widget.DynamicOverlayWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.closeIcon = this.jq.children('.ui-sidebar-close');
        this.cfg.baseZIndex = this.cfg.baseZIndex||0;

        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }

        this.bindEvents();
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

    //@override
    enableModality: function() {
        this._super();

        var $this = this;
        this.modalOverlay.on('click', function() {
            $this.hide();
        });
    },

    //@override
    getModalTabbables: function(){
        return this.jq.find(':tabbable');
    },

    applyARIA: function() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-hidden': !this.cfg.visible
        });

        this.closeIcon.attr('role', 'button');
    }

});