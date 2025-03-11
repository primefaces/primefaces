import { TieredMenu, type TieredMenuCfg } from "./menu.tieredmenu.widget.js";

/**
 * The configuration for the {@link  Menubar} Menubar.
 * 
 * You can access this configuration via {@link Menubar.cfg cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface MenubarCfg extends TieredMenuCfg {
    /**
     * Delay in milliseconds before displaying the sub menu. Default is 0 meaning immediate.
     */
    delay: number;
}

/**
 * __PrimeFaces Menubar Widget__
 *
 * Menubar is a horizontal navigation component.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Menubar<Cfg extends MenubarCfg = MenubarCfg> extends TieredMenu<Cfg> {

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        // #13323 MenuBar only for hideDelay=0 only closes on document.click
        this.cfg.hideOnDocumentClick = this.cfg.hideDelay === 0;
    }

    override showSubmenu(menuitem: JQuery, submenu: JQuery, focus?: boolean): void {
        let pos: JQueryUI.JQueryPositionOptions | null = null;

        if (menuitem.parent().hasClass('ui-menu-child')) {
            pos = {
                my: this.isRTL ? 'right top' : 'left top',
                at: this.isRTL ? 'left top' : 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: this.isRTL ? 'right top' : 'left top',
                at: this.isRTL ? 'right bottom' : 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        //avoid queuing multiple runs
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = PrimeFaces.queueTask(function() {
            submenu.css('z-index', PrimeFaces.nextZindex())
                .show()
                .position(pos);
            var $link = menuitem.children('a.ui-menuitem-link');
            $link.attr('aria-expanded', 'true');
            if (focus) {
                submenu.find('a.ui-menuitem-link:focusable:first').trigger('focus');
            }
        }, this.cfg.showDelay);
    }

}
