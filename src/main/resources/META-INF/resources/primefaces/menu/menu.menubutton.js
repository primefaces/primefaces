PrimeFaces.widget.MenuButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.menuId = this.jqId + '_menu';
        this.button = this.jq.children('button');
        this.menu = this.jq.children('.ui-menu');
        this.menuitems = this.jq.find('.ui-menuitem');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();
            PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
        }
    },

    //@override
    refresh: function(cfg) {
        this._super(cfg);
    },

    bindEvents: function() {
        var $this = this;

        //button visuals
        this.button.mouseover(function(){
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.removeClass('ui-state-hover ui-state-active');
            }
        }).mousedown(function() {
            $(this).removeClass('ui-state-focus ui-state-hover').addClass('ui-state-active');
        }).mouseup(function() {
            var el = $(this);
            el.removeClass('ui-state-active')

            if($this.menu.is(':visible')) {
                el.addClass('ui-state-hover');
                $this.hide();
            }
            else {
                el.addClass('ui-state-focus');
                $this.show();
            }
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        });

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.button.removeClass('ui-state-focus');
            $this.hide();
        });

        //keyboard support
        this.button.keydown(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator)') : null;

                        if(prevItems && prevItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            prevItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator)') : $this.menuitems.eq(0);

                        if(nextItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            nextItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();
                break;

                case keyCode.ENTER:
                case keyCode.SPACE:
                    if($this.menu.is(':visible'))
                        $this.menuitems.filter('.ui-state-hover').children('a').trigger('click');
                    else
                        $this.show();

                    e.preventDefault();
                break;


                case keyCode.ESCAPE:
                case keyCode.TAB:
                    $this.hide();
                break;
            }
        });

        if (!$this.cfg.disabled) {
            PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.menu,
                function() { return $this.button; },
                function(e, eventTarget) {
                    if(!($this.menu.is(eventTarget) || $this.menu.has(eventTarget).length > 0)) {
                        $this.button.removeClass('ui-state-focus ui-state-hover');
                        $this.hide();
                    }
                });
        }

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.menu, function() {
            $this.alignPanel();
        });

        //aria
        this.button.attr('role', 'button').attr('aria-disabled', this.button.is(':disabled'));
    },

    show: function() {
        this.alignPanel();

        this.menu.show();
    },

    hide: function() {
        this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');

        this.menu.fadeOut('fast');
    },

    alignPanel: function() {
        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex});

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.menu.position({
                my: 'left top',
                at: 'left bottom',
                of: this.button,
                collision: this.cfg.collision || "flip"
            });
        }
    }

});
