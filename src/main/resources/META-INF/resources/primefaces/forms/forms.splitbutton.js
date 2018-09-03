/*
 * PrimeFaces SplitButton Widget
 */
PrimeFaces.widget.SplitButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + "_menu";
        this.menu = $(this.menuId);
        this.menuitems = this.menu.find('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
        }

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    //@override
    refresh: function(cfg) {
        this.init(cfg);
    },

    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //toggle menu
        this.menuButton.click(function() {
            if($this.menu.is(':hidden'))
                $this.show();
            else
                $this.hide();
        });

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitem.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.hide();
        });

        //keyboard support
        this.menuButton.keydown(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                    prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator)') : null;

                    if(prevItems && prevItems.length) {
                        highlightedItem.removeClass('ui-state-hover');
                        prevItems.eq(0).addClass('ui-state-hover');
                    }

                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                    nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator)') : $this.menuitems.eq(0);

                    if(nextItems.length) {
                        highlightedItem.removeClass('ui-state-hover');
                        nextItems.eq(0).addClass('ui-state-hover');
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

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.menu, null,
            function(e) {
                $this.button.removeClass('ui-state-focus ui-state-hover');
                $this.hide();
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.menu, function() {
            $this.alignPanel();
        });
    },

    show: function() {
        this.alignPanel();

        this.menuButton.focus();

        this.menu.show();
    },

    hide: function() {
        this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
        this.menuButton.removeClass('ui-state-focus');

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
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            });
        }
    }

});
