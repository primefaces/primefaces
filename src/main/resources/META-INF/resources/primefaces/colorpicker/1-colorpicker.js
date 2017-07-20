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
        this.cfg.nestedInDialog = this.jqEl.parents('.ui-dialog:first').length == 1;

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
        var _self = this;

        this.cfg.onChange = function(hsb, hex, rgb) {
            _self.input.val(hex);


            if(_self.cfg.popup) {
                _self.livePreview.css('backgroundColor', '#' + hex);
            }

            _self.input.change();
        };

        this.cfg.onShow = function() {
            if(_self.cfg.popup) {
                _self.overlay.css('z-index', ++PrimeFaces.zindex);
            }

            var win = $(window),
            positionOffset = _self.cfg.nestedInDialog ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

            if(_self.cfg.nestedInDialog) {
                _self.overlay.css('position', 'fixed');
            }

            //position the overlay relative to the button
            _self.overlay.css({
                        left:'',
                        top:''
                })
                .position({
                    my: 'left top'
                    ,at: 'left bottom'
                    ,of: _self.jqEl,
                    offset : positionOffset
                });
        };

        this.cfg.onHide = function(cp) {
            _self.overlay.css('z-index', ++PrimeFaces.zindex);
            $(cp).fadeOut('fast');
            return false;
        };
    },

    /**
     * When a popup colorpicker is updated with ajax, a new overlay is appended to body and old overlay
     * would be orphan. We need to remove the old overlay to prevent memory leaks.
     */
    clearOrphanOverlay: function() {
        var _self = this;

        $(document.body).children('.ui-colorpicker-container').each(function(i, element) {
            var overlay = $(element),
            options = overlay.data('colorpicker');

            if(options.id == _self.id) {
                overlay.remove();
                return false;   //break;
            }
        });
    }
});
