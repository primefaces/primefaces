import { Menu, type MenuCfg } from "./menu.base.widget.js";

/**
 * The configuration for the {@link  TabMenu} widget.
 * 
 * You can access this configuration via {@link TabMenu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface TabMenuCfg extends MenuCfg {
}

/**
 * __PrimeFaces TabMenu Widget__
 * 
 * TabMenu is a navigation component that displays menuitems as tabs.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class TabMenu<Cfg extends TabMenuCfg = TabMenuCfg> extends Menu<Cfg> {
    /**
     * The DOM elements for the tab menu entries.
     */
    items: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');

        this.bindEvents();
        this.bindKeyEvents();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.items.on('mouseover.tabmenu', function() {
            const element = $(this);
                if(!element.hasClass('ui-state-active')) {
                    element.addClass('ui-state-hover');
                }
            })
            .on('mouseout.tabmenu', function() {
                $(this).removeClass('ui-state-hover');
            });
    }

    /**
     * Sets up all keyboard event listeners that are required by this widget.
     */
    private bindKeyEvents(): void {
        /* For Keyboard accessibility and Screen Readers */
        this.items.attr('tabindex', 0);

        this.items.on('focus.tabmenu', function() {
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.tabmenu', function() {
            $(this).removeClass('ui-menuitem-outline');
        })
        .on('keydown.tabmenu', function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                const currentLink = $(this).children('a');
                currentLink.trigger('click');
                PrimeFaces.utils.openLink(e, currentLink);
            }
        });
    }
}
