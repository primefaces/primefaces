/**
 * __PrimeFaces SlideMenu Widget__
 * 
 * SlideMenu is used to display nested submenus with sliding animation.
 * 
 * @prop {JQuery} backward The DOM element for the link to navigate back to the previous menu page.
 * @prop {JQuery} submenus The DOM elements for the sub menu items other that the root menu items.
 * @prop {JQuery} content The DOM element for the slide menu content.
 * @prop {number} jqWidth Width of the menu container in pixels.
 * @prop {JQuery} links The DOM elements for the the links to sub menus.
 * @prop {boolean} rendered Whether this menu was already rendered.
 * @prop {JQuery} rootList The DOM elements for the root menu entries.
 * @prop {JQuery[]} stack A stack with the menu items that were selected. Used to slide back to the previous menu page.
 * @prop {JQuery} wrapper The DOM element for the wrapper of the slide menu.
 * 
 * @interface {PrimeFaces.widget.SlideMenuCfg} cfg The configuration for the {@link  SlideMenu| SlideMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg
 */
PrimeFaces.widget.SlideMenu = PrimeFaces.widget.Menu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.links.on("mouseenter", function() {
           $(this).addClass('ui-state-hover');
        })
        .on("mouseleave", function() {
           $(this).removeClass('ui-state-hover');
        })
        .on("click", function(e) {
            var link = $(this),
            submenu = link.next();

            if(submenu.length) {
               $this.forward(submenu);
               e.preventDefault();
            }
        });

        this.backward.on("click", function() {
            $this.back();
        });
    },

    /**
     * Slides to the given sub menu.
     * @param {JQuery} submenu A sub menu to show, with the class `ui-menuitem-link`.
     */
    forward: function(submenu) {
        var _self = this;

        this.push(submenu);

        var rootLeft = -1 * (this.depth() * this.jqWidth);

        submenu.show().css({
            left: this.jqWidth + 'px'
        });

        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            if(_self.backward.is(':hidden')) {
                _self.backward.fadeIn('fast');
            }
        });
    },

    /**
     * Slides back to the previous menu page.
     */
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

    /**
     * Adds the menu page to the top of the stack.
     * @param {JQuery} submenu A menu page to push to the stack. 
     * @private
     */
    push: function(submenu) {
        this.stack.push(submenu);
    },

    /**
     * Pops the most recently a menu page from the stack and return it.
     * @return {JQuery | null} The item on top of the stack, or `null` if the stack is empty.
     * @private
     */
    pop: function() {
        return this.stack.length !== 0 ? this.stack.pop() : null;
    },

    /**
     * Peeks the stack and returns the topmost item.
     * @return {JQuery | undefined} The last item on the stack, or `undefined` if the stack is empty
     * @private
     */
    last: function() {
        return this.stack[this.stack.length - 1];
    },

    /**
     * Inspects the stack and returns its size.
     * @return {number} The number of items on the stack.
     * @private
     */
    depth: function() {
        return this.stack.length;
    },

    /**
     * Renders the client-side parts of this widget.
     * @private
     */
    render: function() {
        this.submenus.width(this.jq.width());
        this.wrapper.height(this.rootList.outerHeight(true) + this.backward.outerHeight(true));
        this.content.height(this.rootList.outerHeight(true));
        this.rendered = true;
    },

    /**
     * @override
     * @inheritdoc
     */
    show: function() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    if (!$this.rendered) {
                        $this.render();
                    }
                    $this.jq.css('z-index', PrimeFaces.nextZindex());
                    $this.align();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                }
            });
        }
    }
});
