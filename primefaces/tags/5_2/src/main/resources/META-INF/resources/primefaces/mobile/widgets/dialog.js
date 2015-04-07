/**
 * PrimeFaces Mobile Dialog Widget
 */
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.popupElement = this.jq.children('.ui-popup');
        this.mask = this.jq.prev('.ui-popup-screen');
        this.content = this.popupElement.children('.ui-content');
        this.header = this.popupElement.children('.ui-header');
        this.closeIcon = this.header.children('.ui-icon-delete');
        
        //cleanup duplicate masks due to ajax update
        var orphanMask = this.mask.prev('.ui-popup-screen');
        if(orphanMask.length) {
            orphanMask.remove();
        } 
        
        this.popupElement.popup({
            positionTo: 'window',
            dismissible: false,
            overlayTheme: 'b',
            enhanced: true
        });
    
        this.bindEvents();
    },
        
    bindEvents: function() {
        var $this = this;
        
        this.closeIcon.on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });
    },
    
    show: function() {
        this.popupElement.popup('open', {transition:this.cfg.showEffect});
    },
    
    hide: function() {
        this.popupElement.popup('close');
    }
});

/**
 * PrimeFaces Mobile ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = PrimeFaces.widget.Dialog.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.title = this.header.children('.ui-title');
        this.message = this.content.children('.ui-title');

        if(this.cfg.global) {
            PrimeFaces.confirmDialog = this;

            this.content.find('.ui-confirmdialog-yes').on('click.ui-confirmdialog', function(e) {                
                if(PrimeFaces.confirmSource) {
                    var fn = new Function('event',PrimeFaces.confirmSource.data('pfconfirmcommand'));
                    
                    fn.call(PrimeFaces.confirmSource.get(0),e);
                    PrimeFaces.confirmDialog.hide();
                    PrimeFaces.confirmSource = null;
                }
                
                e.preventDefault();
            });

            this.jq.find('.ui-confirmdialog-no').on('click.ui-confirmdialog', function(e) {
                PrimeFaces.confirmDialog.hide();
                PrimeFaces.confirmSource = null;
                
                e.preventDefault();
            });
        }
    },

    applyFocus: function() {
        this.jq.find(':button,:submit').filter(':visible:enabled').eq(0).focus();
    },
            
    showMessage: function(msg) {
        if(msg.header)
            this.title.text(msg.header);
        
        if(msg.message)
            this.message.text(msg.message);
        
        this.show();
    }

});