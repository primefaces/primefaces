PrimeFaces.widget.PlainMenu = PrimeFaces.widget.Menu.extend({

    init: function(cfg) {
        this._super(cfg);

        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');

        //events
        this.bindEvents();

        if(this.cfg.toggleable) {
            this.collapsedIds = [];
            this.stateKey = 'menu-' + this.id;
            this.restoreState();
        }
    },

    bindEvents: function() {
        var $this = this;

        this.menuitemLinks.mouseenter(function(e) {
            if($this.jq.is(':focus')) {
                $this.jq.blur();
            }

            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function(e) {
            $(this).removeClass('ui-state-hover');
        });

        if(this.cfg.overlay) {
            this.menuitemLinks.click(function() {
                $this.hide();
            });

            this.trigger.on('keydown.ui-menu', function(e) {
                var keyCode = $.ui.keyCode;

                switch(e.which) {
                    case keyCode.DOWN:
                        $this.keyboardTarget.trigger('focus.menu');
                        e.preventDefault();
                    break;

                    case keyCode.TAB:
                        if($this.jq.is(':visible')) {
                            $this.hide();
                        }
                    break;
                }
            });
        }

        if(this.cfg.toggleable) {
            this.jq.find('> .ui-menu-list > .ui-widget-header').on('mouseover.menu', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.menu', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.menu', function(e) {
                var header = $(this);

                if(header.find('> h3 > .ui-icon').hasClass('ui-icon-triangle-1-s'))
                    $this.collapseSubmenu(header, true);
                else
                    $this.expandSubmenu(header, true);

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }

        this.keyboardTarget.on('focus.menu', function() {
            $this.menuitemLinks.eq(0).addClass('ui-state-hover');
        })
        .on('blur.menu', function() {
            $this.menuitemLinks.filter('.ui-state-hover').removeClass('ui-state-hover');
        })
        .on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-hover'),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                    case keyCode.UP:
                        var prevItem = currentLink.parent().prevAll('.ui-menuitem:first');
                        if(prevItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            prevItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;

                    case keyCode.DOWN:
                        var nextItem = currentLink.parent().nextAll('.ui-menuitem:first');
                        if(nextItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            nextItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }

                        e.preventDefault();
                    break;

                    case keyCode.ESCAPE:
                        $this.hide();

                        if($this.cfg.overlay) {
                            $this.trigger.focus();
                        }
                    break;

            }
        });
    },

    collapseSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        items.filter('.ui-submenu-child').hide();

        if(stateful) {
            this.collapsedIds.push(header.attr('id'));
            this.saveState();
        }
    },

    expandSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.filter('.ui-submenu-child').show();

        if(stateful) {
            var id = header.attr('id');
            this.collapsedIds = $.grep(this.collapsedIds, function(value) {
                return (value !== id);
            });
            this.saveState();
        }
    },

    saveState: function() {
        PrimeFaces.setCookie(this.stateKey, this.collapsedIds.join(','));
    },

    restoreState: function() {
        var collapsedIdsAsString = PrimeFaces.getCookie(this.stateKey);

        if(collapsedIdsAsString) {
            this.collapsedIds = collapsedIdsAsString.split(',');

            for(var i = 0 ; i < this.collapsedIds.length; i++) {
                this.collapseSubmenu($(PrimeFaces.escapeClientId(this.collapsedIds[i])), false);
            }
        }
    },

    clearState: function() {
        PrimeFaces.setCookie(this.stateKey, null);
    }

});
