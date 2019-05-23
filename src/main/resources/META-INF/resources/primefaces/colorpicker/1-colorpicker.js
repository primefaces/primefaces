/**
 * PrimeFaces Color Picker Widget
 */
 PrimeFaces.widget.ColorPicker = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.cfg.popup = this.cfg.mode == 'popup';
        this.jqEl = this.cfg.popup ? $(this.jqId + '_button') : $(this.jqId + '_inline');
        this.cfg.flat = !this.cfg.popup;
        this.cfg.livePreview = false;

        this.bindCallbacks();

        //ajax update check
        if(this.cfg.popup) {
            this.clearOrphanOverlay();
        }

        //create colorpicker
        this.jqEl.ColorPicker(this.cfg);

        //popup ui
        if(this.cfg.popup) {
            PrimeFaces.skinButton(this.jqEl);
            this.overlay = $(PrimeFaces.escapeClientId(this.jqEl.data('colorpickerId')));
            this.livePreview = $(this.jqId + '_livePreview');
        }
    },

    bindCallbacks: function() {
        var $this = this;

        this.cfg.onChange = function(hsb, hex, rgb) {
            $this.input.val(hex);


            if($this.cfg.popup) {
                $this.livePreview.css('backgroundColor', '#' + hex);
            }

            $this.input.change();

            $this.callBehavior('change');
        };

        this.cfg.onShow = function() {
            if($this.cfg.popup) {
                $this.overlay.css({
                    'z-index': ++PrimeFaces.zindex,
                    'display':'block', 
                    'opacity':0, 
                    'pointer-events': 'none'
                });
            }

            $this.setupDialogSupport();

            //position the overlay relative to the button
            $this.overlay.css({
                        left:'',
                        top:''
                })
                .position({
                    my: 'left top'
                    ,at: 'left bottom'
                    ,of: $this.jqEl
                });
            
            if($this.cfg.popup) {
                $this.overlay.css({
                    'display':'none', 
                    'opacity':'', 
                    'pointer-events': ''
                });
            }
        };

        this.cfg.onHide = function(cp) {
            $this.overlay.css('z-index', ++PrimeFaces.zindex);
            $(cp).fadeOut('fast');
            return false;
        };
    },

    /**
     * When a popup colorpicker is updated with ajax, a new overlay is appended to body and old overlay
     * would be orphan. We need to remove the old overlay to prevent memory leaks.
     */
    clearOrphanOverlay: function() {
        var $this = this;

        $(document.body).children('.ui-colorpicker-container').each(function(i, element) {
            var overlay = $(element),
            options = overlay.data('colorpicker');

            if(options.id == $this.id) {
                overlay.remove();
                return false;   //break;
            }
        });
    },
    
    setupDialogSupport: function() {
        var dialog = this.jqEl.closest('.ui-dialog');
        
        if(dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.overlay.css('position', 'fixed');
        }
    }
});
