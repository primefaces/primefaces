PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.overlay) {
            this.initOverlay();
        }

        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');
    },

    initOverlay: function() {
        var $this = this;

        this.jq.addClass('ui-menu-overlay');

        this.cfg.trigger = this.cfg.trigger.replace(/\\\\:/g,"\\:");

        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.jqId);

        this.cfg.pos = {
            my: this.cfg.my
            ,at: this.cfg.at
            ,of: this.trigger
        };

        this.trigger.off(this.cfg.triggerEvent + '.ui-menu').on(this.cfg.triggerEvent + '.ui-menu', function(e) {
            var trigger = $(this);

            if($this.jq.is(':visible')) {
                $this.hide();
            }
            else {
                $this.show();

                if(trigger.is(':button')) {
                    trigger.addClass('ui-state-focus');
                }

                e.preventDefault();
            }
        });

        //hide overlay on document click
        this.itemMouseDown = false;

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.jq,
            function() { return $this.trigger; },
            function(e, eventTarget) {
                if (eventTarget.is('.ui-menuitem-link') || eventTarget.closest('.ui-menuitem-link').length) {
                    $this.itemMouseDown = true;
                }
                else if(!($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0)) {
                    $this.hide(e);
                }
            });

        var hideUpNS = 'mouseup.' + this.id;
        $(document.body).off(hideUpNS).on(hideUpNS, function (e) {
            if($this.itemMouseDown) {
                $this.hide(e);
                $this.itemMouseDown = false;
            }
        });

        //Hide overlay on resize
        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            $this.align();
        });

        //dialog support
        this.setupDialogSupport();
    },

    setupDialogSupport: function() {
        var dialog = this.trigger.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.jq.css('position', 'fixed');
        }
    },

    show: function() {
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
    },

    hide: function() {
        this.jq.fadeOut('fast');

        if(this.trigger && this.trigger.is(':button')) {
            this.trigger.removeClass('ui-state-focus');
        }
    },

    align: function() {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.cfg.pos.offset = positionOffset;

        this.jq.css({left:'', top:''}).position(this.cfg.pos);
    }
});





