PrimeFaces.widget.SlideMenu = PrimeFaces.widget.Menu.extend({

    init: function(cfg) {
        this._super(cfg);

        //elements
        this.submenus = this.jq.find('ul.ui-menu-list');
        this.wrapper = this.jq.children('div.ui-slidemenu-wrapper');
        this.content = this.wrapper.children('div.ui-slidemenu-content');
        this.rootList = this.content.children('ul.ui-menu-list');
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.backward = this.wrapper.children('div.ui-slidemenu-backward');
        this.rendered = false;

        //config
        this.stack = [];
        this.jqWidth = this.jq.width();

        if(!this.jq.hasClass('ui-menu-dynamic')) {

            if(this.jq.is(':not(:visible)')) {
                var hiddenParent = this.jq.closest('.ui-hidden-container'),
                $this = this;

                if(hiddenParent.length) {
                    PrimeFaces.addDeferredRender(this.id, hiddenParent.attr('id'), function() {
                        return $this.render();
                    });
                }
            }
            else {
                this.render();
            }
        }

        this.bindEvents();
    },

    bindEvents: function() {
        var $this = this;

        this.links.mouseenter(function() {
           $(this).addClass('ui-state-hover');
        })
        .mouseleave(function() {
           $(this).removeClass('ui-state-hover');
        })
        .click(function(e) {
            var link = $(this),
            submenu = link.next();

            if(submenu.length) {
               $this.forward(submenu);
               e.preventDefault();
            }
        });

        this.backward.click(function() {
            $this.back();
        });
    },

    forward: function(submenu) {
        var _self = this;

        this.push(submenu);

        var rootLeft = -1 * (this.depth() * this.jqWidth);

        submenu.show().css({
            left: this.jqWidth
        });

        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            if(_self.backward.is(':hidden')) {
                _self.backward.fadeIn('fast');
            }
        });
    },

    back: function() {
        if(!this.rootList.is(':animated')) {
            var _self = this,
            last = this.pop(),
            depth = this.depth();

            var rootLeft = -1 * (depth * this.jqWidth);

            this.rootList.animate({
                left: rootLeft
            }, 500, 'easeInOutCirc', function() {
                if(last) {
                    last.hide();
                }

                if(depth == 0) {
                    _self.backward.fadeOut('fast');
                }
            });
        }
    },

    push: function(submenu) {
        this.stack.push(submenu);
    },

    pop: function() {
        return this.stack.length !== 0 ? this.stack.pop() : null;
    },

    last: function() {
        return this.stack[this.stack.length - 1];
    },

    depth: function() {
        return this.stack.length;
    },

    render: function() {
        this.submenus.width(this.jq.width());
        this.wrapper.height(this.rootList.outerHeight(true) + this.backward.outerHeight(true));
        this.content.height(this.rootList.outerHeight(true));
        this.rendered = true;
    },

    show: function() {
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();

        if(!this.rendered) {
            this.render();
        }
    }
});
