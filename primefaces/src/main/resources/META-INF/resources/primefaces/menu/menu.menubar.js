/**
 * __PrimeFaces Menubar Widget__
 *
 * Menubar is a horizontal navigation component.
 *
 * @interface {PrimeFaces.widget.MenubarCfg} cfg The configuration for the {@link  Menubar| Menubar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.TieredMenuCfg} cfg
 *
 * @prop {JQuery | null} [activeitem] The currently active (highlighted) menu item.
 * @prop {number} [timeoutId] Timeout ID, used for the animation when the menu is shown.
 * 
 * @prop {number} cfg.delay Delay in milliseconds before displaying the sub menu. Default is 0 meaning immediate.
 */
PrimeFaces.widget.Menubar = PrimeFaces.widget.TieredMenu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} menuitem
     * @param {JQuery} submenu
     */
    showSubmenu: function(menuitem, submenu) {
        var pos = null;

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
            submenu.find('a.ui-menuitem-link:focusable:first').trigger('focus');
        }, this.cfg.showDelay);
    }

});
