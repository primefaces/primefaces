App = {
    init: function() {
        this.wrapper = $(document.body).children('.layout-wrapper');
        this.topbar = this.wrapper.children('.layout-topbar');
        this.topbarMenu = this.topbar.find('.topbar-menu');
        this.sidebar = this.wrapper.find('.layout-sidebar').first();
        this.menu = this.sidebar.children('.layout-menu');
        this.menuLinks = this.menu.find('a');
        this.mask = this.wrapper.children('.layout-mask');
        this.menuButton = this.topbar.find('.menu-button');
        this.configurator = this.wrapper.children('.layout-config');
        this.configuratorButton = $('#layout-config-button');
        this.configuratorCloseButton = $('#layout-config-close-button');
        this.filterPanel = $('.layout-sidebar-filter-panel');
        this.news = this.wrapper.children('.layout-news');
        this.activeSubmenus = [];
        
        this._bindEvents();
        this._bindNews();
        this._bindTopbarScroll();
        
        this.restoreMenu();
        Storage.restoreSettings();
    },

    _bindTopbarScroll: function() {
        var $this = this;
        var rafId = null;

        var update = function() {
            rafId = null;
            if (window.scrollY > 0) {
                $this.topbar.addClass('scrolled');
            } else {
                $this.topbar.removeClass('scrolled');
            }
        };

        $(window).off('scroll.showcaseTopbar').on('scroll.showcaseTopbar', function() {
            if (rafId) return;
            rafId = window.requestAnimationFrame(update);
        });

        update();
    },

    _bindEvents: function() {
        var $this = this;

        this.topbarMenu.find('> .topbar-submenu > a').off('click').on('click', function() {
            var item = $(this).parent();

            item.siblings('.topbar-submenu-active').removeClass('topbar-submenu-active');

            if (item.hasClass('topbar-submenu-active'))
                $this.hideTopbarSubmenu(item);
            else
                $this.showTopbarSubmenu(item);
        });

        this.topbarMenu.find('.connected-overlay-in a').off('click').on('click', function() {
            $this.hideTopbarSubmenu($this.topbarMenu.children('.topbar-submenu-active'));
        });

        this.menuLinks.off('click').on('click', function() {
            sessionStorage.setItem('scroll_position', $this.menu.scrollTop());
        });

        this.menu.off('scroll').on('scroll', function() {
            sessionStorage.setItem('scroll_position', $this.menu.scrollTop());
        });

        $(document).off('click.showcase').on('click.showcase', function(event) {
            var topbarMenuEl = $this.topbarMenu.get(0);
            if (topbarMenuEl && !$.contains(topbarMenuEl, event.target)) {
                $this.hideTopbarSubmenu($this.topbarMenu.children('.topbar-submenu-active'));
            }

            if ($this.sidebar.hasClass('active') && !$.contains($this.sidebar.get(0), event.target) && !$this.isMenuButton(event.target)) {
                $this.sidebar.removeClass('active');
                $this.mask.removeClass('layout-mask-active');
            }

            if ($this.configurator.hasClass('layout-config-active') && !$.contains($this.configurator.get(0), event.target)) {
                $this.configurator.removeClass('layout-config-active');
            }
        });

        this.menuButton.off('click').on('click', function() {
            $this.sidebar.addClass('active');
            $this.mask.addClass('layout-mask-active');
        });

        this.configuratorButton.off('click').on('click', function() {
            $this.configurator.toggleClass('layout-config-active');
        });

        this.configuratorCloseButton.off('click').on('click', function() {
            $this.configurator.removeClass('layout-config-active');
        });

        this.filterPanel.off('click.showcase', '.ui-autocomplete-item')
            .on('click.showcase', '.ui-autocomplete-item', function(e) {
                if (!$this.isLinkClicked) {
                    $this.isLinkClicked = true;

                    var link = $(this).find('a:first');
                    if (link) {
                        var url = new URL(link[0].href);
                        var menuItem = $this.menu.find('a[href*="' + url.pathname + '"]');
                        if(menuItem.length) {
                           var scroll_position = menuItem[0].offsetTop - $this.menu[0].offsetTop;;
                           sessionStorage.setItem('scroll_position', scroll_position); 
                        }

                        link.trigger('click');

                        var href = link.attr('href');
                        if (href && href !== '#') {
                            window.location.href = href;
                        }
                    }
                }
                $this.isLinkClicked = false;
                e.preventDefault(); 
            });
    },

    hideTopbarSubmenu: function(item) {
        var submenu = item.children('ul');
        submenu.addClass('connected-overlay-out');

        setTimeout(function () {
            item.removeClass('topbar-submenu-active'),
            submenu.removeClass('connected-overlay-out');
        }, 100);
    },

    showTopbarSubmenu: function(item) {
        item.addClass('topbar-submenu-active');
    },

    changeTheme: function(theme, dark) {
        PrimeFaces.changeTheme(theme);

        if (dark)
            $('#homepage-intro').addClass('introduction-dark');
        else
            $('#homepage-intro').removeClass('introduction-dark');
    },

    updateInputStyle: function(value) {
        if (value === 'filled')
            this.wrapper.addClass('ui-input-filled');
        else
            this.wrapper.removeClass('ui-input-filled');
    },

    isMenuButton: function(element) {
        return $.contains(this.menuButton.get(0), element) || this.menuButton.is(element);
    },

    restoreMenu: function() {
        var currentPath = window.location.pathname;

        // Exact match: the link whose href matches the current page
        this.menuLinks.filter('[href^="' + currentPath + '"]').addClass('router-link-active');

        // Parent match: a submenu-link is active when the current page
        // lives under the same directory as its first-child URL
        this.menuLinks.filter('.submenu-link').each(function() {
            var href = $(this).attr('href');
            if (href) {
                var dir = href.substring(0, href.lastIndexOf('/') + 1);
                if (currentPath.indexOf(dir) === 0) {
                    $(this).addClass('router-link-active');
                }
            }
        });

        var scrollPosition = sessionStorage.getItem('scroll_position');
        if (scrollPosition) {
            this.menu.scrollTop(parseInt(scrollPosition));
        } else {
            // First visit: scroll sidebar so the active link is visible
            var activeLink = this.menu.find('a.router-link-active:first');
            if (activeLink.length) {
                this.menu.scrollTop(activeLink[0].offsetTop - this.menu[0].offsetTop - 100);
            }
        }
    },

    onSearchClick: function(event, id) {
        if (id && this.activeSubmenus.indexOf(id) === -1) {
            this.activeSubmenus.push(id);
            $('#' + id).next().show();
            sessionStorage.setItem('active_submenus', this.activeSubmenus.join(','));
        }
    },
    
    _bindNews: function() {
        if (this.news && this.news.length > 0) {
            var $this = this;
            var closeButton = this.news.find('.layout-news-close');
            closeButton.off('click.news').on('click.news', function() {
                $this.wrapper.removeClass('layout-news-active');
                $this.news.hide();
                
                Storage.saveSettings(false);
            });
        }
    },
    
    changeNews: function(active) {
        if (this.news && this.news.length > 0) {
            if (active)
                this.wrapper.addClass('layout-news-active');
            else 
                this.wrapper.removeClass('layout-news-active');
        }
    }
}

var Storage = {
    storageKey: 'primefaces',
    saveSettings: function(newsActive) {
        var now = new Date();
        var item = {
            settings: {
                newsActive: newsActive
            },
            expiry: now.getTime() + 604800000
        }
        localStorage.setItem(this.storageKey, JSON.stringify(item));
    },
    restoreSettings: function() {
        var itemString = localStorage.getItem(this.storageKey);
        if (itemString) {
            var item = JSON.parse(itemString);
            if (!this.isStorageExpired()) {
                // News
                App.changeNews(item.settings.newsActive);
            }
        }
    },
    isStorageExpired: function() {
        var itemString = localStorage.getItem(this.storageKey);
        if (!itemString) {
            return true;
        }
        var item = JSON.parse(itemString);
        var now = new Date();

        if (now.getTime() > item.expiry) {
            localStorage.removeItem(this.storageKey);
            return true;
        }

        return false;
    }
}

$(function() {
    App.init();
});
