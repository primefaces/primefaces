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

        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        // we might have two menus with same ids if an ancestor of a menu is updated, if so remove the previous one and refresh jq
        // the first check is required if the id contains a ':' - See #2485
        if(this.jq.length > 1){
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
            this.jq.appendTo(document.body);
        }
	else {
            // this is required if the id does NOT contain a ':' - See #2485
	    $(document.body).children("[id='" + this.id + "']").not(this.jq).remove();
	    if(this.jq.parent().is(':not(body)')) {
		this.jq.appendTo(document.body);
	    }
	}

        this.cfg.pos = {
            my: this.cfg.my
            ,at: this.cfg.at
            ,of: this.trigger
        }

        this.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
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
        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {
            if($this.jq.is(":hidden")) {
                return;
            }

            //do nothing if mousedown is on trigger
            var target = $(e.target);
            if(target.is($this.trigger.get(0))||$this.trigger.has(target).length > 0) {
                return;
            }

            //hide if mouse is outside of overlay except trigger
            var offset = $this.jq.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.jq.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.jq.height()) {

                if(target.is('.ui-menuitem-link') || target.closest('.ui-menuitem-link').length)
                    $this.itemMouseDown = true;
                else
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
        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
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





