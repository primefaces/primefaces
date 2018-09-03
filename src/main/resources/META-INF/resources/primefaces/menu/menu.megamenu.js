PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.cfg.vertical = this.jq.hasClass('ui-megamenu-vertical');
        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootLinks = this.rootList.find('> li.ui-menuitem > a.ui-menuitem-link:not(.ui-state-disabled)');
        this.subLinks = this.jq.find('.ui-menu-child a.ui-menuitem-link:not(.ui-state-disabled)');
        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');

        if(this.cfg.activeIndex !== undefined) {
            this.rootLinks.eq(this.cfg.activeIndex).addClass('ui-state-hover').closest('li.ui-menuitem').addClass('ui-menuitem-active');
        }

        this.bindEvents();
        this.bindKeyEvents();
    },

    bindEvents: function() {
        var $this = this;

        this.rootLinks.mouseenter(function(e) {
            var link = $(this),
            menuitem = link.parent();

            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                current.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(current, false);
            }

            if($this.cfg.autoDisplay||$this.active) {
                $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }

        });

        if(this.cfg.autoDisplay === false) {
            this.rootLinks.data('primefaces-megamenu', this.id).find('*').data('primefaces-megamenu', this.id)

            this.rootLinks.click(function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = link.next();

                if(submenu.length === 1) {
                    if(submenu.is(':visible')) {
                        $this.active = false;
                        $this.deactivate(menuitem, true);
                    }
                    else {
                        $this.active = true;
                        $this.activate(menuitem);
                    }
                }
                else {
                    var href = link.attr('href');
                    if(href && href !== '#') {
                        window.location.href = href;
                    }
                }

                e.preventDefault();
            });
        }
        else {
            this.rootLinks.filter('.ui-submenu-link').click(function(e) {
                e.preventDefault();
            });
        }

        this.subLinks.mouseenter(function() {
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);
            }
            $this.highlight($(this).parent());
        })
        .mouseleave(function() {
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);
            }
            $(this).removeClass('ui-state-hover');
        });

        this.rootList.mouseleave(function(e) {
            var activeitem = $this.rootList.children('.ui-menuitem-active');
            if(activeitem.length === 1) {
                $this.deactivate(activeitem, false);
            }
        });

        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').mouseleave(function(e) {
            e.stopPropagation();
        });

        $(document.body).click(function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') === $this.id) {
                return;
            }

            $this.active = false;
            $this.deactivate($this.rootList.children('li.ui-menuitem-active'), true);
        });
    },

    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.megamenu', function(e) {
            $this.highlight($this.rootLinks.eq(0).parent());
        })
        .on('blur.megamenu', function() {
            $this.reset();
        })
        .on('keydown.megamenu', function(e) {
            var currentitem = $this.activeitem;
            if(!currentitem) {
                return;
            }

            var isRootLink = $this.isRootLink(currentitem),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                    case keyCode.LEFT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var prevItem = currentitem.prevAll('.ui-menuitem:first');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }

                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent') && currentitem.children('.ui-menu-child').is(':visible')) {
                                $this.deactivate(currentitem);
                                $this.highlight(currentitem);
                            }
                            else {
                                var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                                if(parentItem.length) {
                                    $this.deactivate(currentitem);
                                    $this.deactivate(parentItem);
                                    $this.highlight(parentItem);
                                }
                            }
                        }
                    break;

                    case keyCode.RIGHT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var nextItem = currentitem.nextAll('.ui-menuitem:visible:first');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }

                            e.preventDefault();
                        }
                        else {

                            if(currentitem.hasClass('ui-menu-parent')) {
                                var submenu = currentitem.children('.ui-menu-child');
                                if(submenu.is(':visible')) {
                                    $this.highlight(submenu.find('ul.ui-menu-list:visible > .ui-menuitem:visible:first'));
                                }
                                else {
                                    $this.activate(currentitem);
                                }
                            }
                        }
                    break;

                    case keyCode.UP:
                        if(!isRootLink || $this.cfg.vertical) {
                            var prevItem = $this.findPrevItem(currentitem);
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }
                        }

                        e.preventDefault();
                    break;

                    case keyCode.DOWN:
                        if(isRootLink && !$this.cfg.vertical) {
                            var submenu = currentitem.children('ul.ui-menu-child');
                            if(submenu.is(':visible')) {
                                var firstMenulist = $this.getFirstMenuList(submenu);
                                $this.highlight(firstMenulist.children('.ui-menuitem:visible:first'));
                            }
                            else {
                                $this.activate(currentitem);
                            }
                        }
                        else {
                            var nextItem = $this.findNextItem(currentitem);
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }
                        }

                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                        var currentLink = currentitem.children('.ui-menuitem-link');
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        $this.deactivate(currentitem);
                        e.preventDefault();
                    break;

                    case keyCode.ESCAPE:
                        if(currentitem.hasClass('ui-menu-parent')) {
                            var submenu = currentitem.children('ul.ui-menu-list:visible');
                            if(submenu.length > 0) {
                                submenu.hide();
                            }
                        }
                        else {
                            var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                            if(parentItem.length) {
                                $this.deactivate(currentitem);
                                $this.deactivate(parentItem);
                                $this.highlight(parentItem);
                            }
                        }
                        e.preventDefault();
                    break;
            }
        });
    },

    findPrevItem: function(menuitem) {
        var previtem = menuitem.prev('.ui-menuitem');

        if(!previtem.length) {
            var prevSubmenu = menuitem.closest('ul.ui-menu-list').prev('.ui-menu-list');

            if(!prevSubmenu.length) {
                prevSubmenu = menuitem.closest('td').prev('td').children('.ui-menu-list:visible:last');
            }

            if(prevSubmenu.length) {
                previtem = prevSubmenu.find('li.ui-menuitem:visible:last');
            }
        }
        return previtem;
    },

    findNextItem: function(menuitem) {
        var nextitem = menuitem.next('.ui-menuitem');

        if(!nextitem.length) {
            var nextSubmenu = menuitem.closest('ul.ui-menu-list').next('.ui-menu-list');
            if(!nextSubmenu.length) {
                nextSubmenu = menuitem.closest('td').next('td').children('.ui-menu-list:visible:first');
            }

            if(nextSubmenu.length) {
                nextitem = nextSubmenu.find('li.ui-menuitem:visible:first');
            }
        }
        return nextitem;
    },

    getFirstMenuList: function(submenu) {
        return submenu.find('.ui-menu-list:not(.ui-state-disabled):first');
    },

    isRootLink: function(menuitem) {
        var submenu = menuitem.closest('ul');
        return submenu.parent().hasClass('ui-menu');
    },

    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
    },

    deactivate: function(menuitem, animate) {
        var link = menuitem.children('a.ui-menuitem-link'),
        submenu = link.next();

        menuitem.removeClass('ui-menuitem-active');
        link.removeClass('ui-state-hover');
        this.activeitem = null;

        if(submenu.length > 0) {
            if(animate)
                submenu.fadeOut('fast');
            else
                submenu.hide();
        }
    },

    highlight: function(menuitem) {
        var link = menuitem.children('a.ui-menuitem-link');

        menuitem.addClass('ui-menuitem-active');
        link.addClass('ui-state-hover');
        this.activeitem = menuitem;
    },

    activate: function(menuitem) {
        var submenu = menuitem.children('.ui-menu-child'),
        $this = this;

        $this.highlight(menuitem);

        if(submenu.length > 0) {
            $this.showSubmenu(menuitem, submenu);
        }
    },

    showSubmenu: function(menuitem, submenu) {
        var pos = null;

        if(this.cfg.vertical) {
            pos = {
                my: 'left top',
                at: 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: 'left top',
                at: 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        submenu.css('z-index', ++PrimeFaces.zindex)
                .show()
                .position(pos);
    }

});
