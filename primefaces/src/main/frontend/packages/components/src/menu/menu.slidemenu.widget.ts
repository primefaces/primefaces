import { Menu, type MenuCfg } from "./menu.base.widget.js";

/**
 * The configuration for the {@link  SlideMenu} widget.
 * 
 * You can access this configuration via {@link SlideMenu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface SlideMenuCfg extends MenuCfg {
}

/**
 * __PrimeFaces SlideMenu Widget__
 * 
 * SlideMenu is used to display nested submenus with sliding animation.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class SlideMenu<Cfg extends SlideMenuCfg = SlideMenuCfg> extends Menu<Cfg> {
    /**
     * The DOM element for the link to navigate back to the previous menu page.
     */
    backward: JQuery = $();

    /**
     * The DOM element for the slide menu content.
     */
    content: JQuery = $();

    /**
     * Width of the menu container in pixels.
     */
    jqWidth: number = 0;

    /**
     * The DOM elements for the the links to sub menus.
     */
    links: JQuery = $();

    /**
     * Whether this menu was already rendered.
     */
    rendered: boolean = false;

    /**
     * The DOM elements for the root menu entries.
     */
    rootList: JQuery = $();

    /**
     * A stack with the menu items that were selected. Used to slide back to the previous menu page.
     */
    stack: JQuery[] = [];

    /**
     * The DOM elements for the sub menu items other that the root menu items.
     */
    submenus: JQuery = $();

    /**
     * The DOM element for the wrapper of the slide menu.
     */
    wrapper: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

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
        this.jqWidth = this.jq.width() ?? 0;

        if(!this.jq.hasClass('ui-menu-dynamic')) {

            if(this.jq.is(':not(:visible)')) {
                var hiddenParent = this.jq.closest('.ui-hidden-container'),
                $this = this;

                if(hiddenParent.length) {
                    PrimeFaces.addDeferredRender(this.id, hiddenParent.attr('id') ?? "", () => {
                        return $this.render();
                    });
                }
            }
            else {
                this.render();
            }
        }

        this.bindEvents();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        const $this = this;

        this.links.on("mouseenter", function() {
           $(this).addClass('ui-state-hover');
        })
        .on("mouseleave", function() {
           $(this).removeClass('ui-state-hover');
        })
        .on("click", function(e) {
            const link = $(this);
            const submenu = link.next();

            if(submenu.length) {
               $this.forward(submenu);
               e.preventDefault();
            }
        })
        .on("focus", function() {
            $(this).addClass('ui-state-focus');
        })
        .on("blur", function() {
            $(this).removeClass('ui-state-focus');
        });

        this.backward.on("click", function(e) {
            $this.back();
            e.preventDefault();
        });
    }

    /**
     * Slides to the given sub menu.
     * @param submenu A sub menu to show, with the class `ui-menuitem-link`.
     */
    forward(submenu: JQuery): void {
        const _self = this;

        this.push(submenu);

        const rootLeft = -1 * (this.depth() * this.jqWidth);

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
        this.backwardButton = _self.backward.children('ui-menuitem-link');
        this.backwardButton.attr('tabindex', 0);
        this.changeTabindex();
    }

    /**
     * Slides back to the previous menu page.
     */
    back(): void {
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
        this.backwardButton = _self.backward.children('ui-menuitem-link');
        this.backwardButton.attr('tabindex', -1);
        this.changeTabindex();
    }

    /**
     * Adds the menu page to the top of the stack.
     * @param submenu A menu page to push to the stack. 
     */
    private push(submenu: JQuery): void {
        this.stack.push(submenu);
    }

    /**
     * Pops the most recently a menu page from the stack and return it.
     * @return The item on top of the stack, or `null` if the stack is empty.
     */
    private pop(): JQuery | null {
        return this.stack.pop() ?? null;
    }

    /**
     * Peeks the stack and returns the topmost item.
     * @return The last item on the stack, or `undefined` if the stack is empty
     */
    last(): JQuery | undefined {
        return this.stack[this.stack.length - 1];
    }

    /**
     * Inspects the stack and returns its size.
     * @return The number of items on the stack.
     */
    private depth(): number {
        return this.stack.length;
    }

    /**
     * Renders the client-side parts of this widget.
     */
    private render(): void {
        this.menuList = this.backward.children('ul.ui-menu-list');
        this.menuItem = this.menuList.children('li.ui-menuitem ');
        this.backButton = this.menuItem.children('a.ui-menuitem-link');

        this.submenus.width(this.jq.width() ?? 0);
        this.wrapper.height((this.rootList.outerHeight(true) ?? 0) + (this.backButton.outerHeight(true) ?? 0));
        this.content.height(this.rootList.outerHeight(true) ?? 0);
        this.rendered = true;
        this.backward.attr('style', 'display: none;');
    }

    override show(): void {
        if (this.transition) {
            this.transition.show({
                onEnter: () => {
                    if (!this.rendered) {
                        this.render();
                    }
                    PrimeFaces.nextZindex(this.jq);
                    this.align();
                },
                onEntered: () => {
                    this.bindPanelEvents();
                }
            });
        }
    }

    /**
    * Change the tabindex of the menu elements
    * @private
    */
    private changeTabindex() {
        this.linksActive = this.jq.find('a.ui-menuitem-link[tabindex=0]');
        this.linksInactive = this.jq.find('a.ui-menuitem-link[tabindex=-1]');

        this.linksActive.attr('tabindex', -1);
        this.linksInactive.attr('tabindex', 0);
    }
}
