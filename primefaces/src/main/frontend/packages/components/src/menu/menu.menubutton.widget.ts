import { TieredMenu, type TieredMenuCfg } from "./menu.tieredmenu.widget.js";

/**
 * The configuration for the {@link  MenuButton} widget.
 * 
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface MenuButtonCfg extends TieredMenuCfg {
    /**
     * When the positioned element overflows the window in some direction, move it to an
     * alternative position. Similar to my and at, this accepts a single value or a pair for horizontal/vertical,
     * e.g., `flip`, `fit`, `fit flip`, `fit none`.
     */
    collision: string;

    /**
     * The delay before showing sub menus.
     */
    delay: number;

    /**
     * Whether this menu button is initially disabled.
     */
    disabled: boolean;
}

/**
 * __PrimeFaces MenuButton Widget__
 *
 * MenuButton displays different commands in a popup menu.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class MenuButton<Cfg extends MenuButtonCfg = MenuButtonCfg> extends TieredMenu<Cfg> {
    /**
     * The DOM element for the menu button.
     */
    button: JQuery = $();

    /**
     * The DOM element for the menu overlay panel.
     */
    menu: JQuery | null = null;

    /**
     * Client ID of the menu overlay panel.
     */
    menuId: string = "";

    /**
     * The DOM elements for the individual menu entries.
     */
    menuitems: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.menu = null;
        this.trigger = this.jq.children('button');
        this.cfg.disabled = this.trigger.is(':disabled');
        
        this.bindButtonEvents();
        PrimeFaces.utils.registerDynamicOverlay(this, this.getMenuElement(), this.id + '_menu');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.getMenuElement(), 'ui-connected-overlay');
        
        //dialog support
        this.setupDialogSupport();
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.trigger.off('.menubutton');
        $(document).off('.' + this.id);

        super.refresh(cfg);
    }

    /**
     * Retrieves the jQuery object representing the menu DOM element.
     * @returns {JQuery} The jQuery object for the menu.
     */
    override getMenuElement(): JQuery {
        if (!this.menu) {
            this.menu = this.jq.children('.ui-menu');
        }
        return this.menu;
    }

    override showSubmenu(menuitem: JQuery, submenu: JQuery): void {
        var pos = {
            my: 'left top',
            at: 'right top',
            of: menuitem,
            collision: 'flipfit'
        };

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
        }, this.cfg.delay);
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindButtonEvents(): void {
        var $this = this;

        //button visuals
        this.trigger.on('mouseover.menubutton', () => {
            if (!this.trigger.hasClass('ui-state-focus')) {
                this.trigger.addClass('ui-state-hover');
            }
        }).on('mouseout.menubutton', () => {
            this.trigger.removeClass('ui-state-hover');
            if (!this.trigger.hasClass('ui-state-focus')) {
                this.trigger.removeClass('ui-state-active');
            }
        }).on('mousedown.menubutton', function() {
            if (!$this.cfg.disabled) {
                $(this).removeClass('ui-state-focus ui-state-hover').addClass('ui-state-active');
            }
        }).on('mouseup.menubutton', function() {
            if ($this.cfg.disabled) {
                return;
            }
            var el = $(this);
            el.removeClass('ui-state-active');

            if ($this.menu?.is(':visible')) {
                el.addClass('ui-state-hover');
                $this.hide();
            }
            else {
                el.addClass('ui-state-focus');
                $this.show();
            }
        }).on('focus.menubutton', function() {
            $(this).addClass('ui-state-focus');
        }).on('blur.menubutton', function() {
            $(this).removeClass('ui-state-focus');
        });

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        this.trigger.on('keydown.menubutton', (e) => {
            if (this.cfg.disabled) {
                return;
            }
            switch ("code" in e ? e.code : e.key) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                case 'ArrowDown':
                    this.show();
                    e.preventDefault();
                    break;

                case 'Escape':
                case 'Tab':
                    this.hide();
                    break;
            }
        });

        PrimeFaces.bindButtonInlineAjaxStatus(this, this.trigger, (widget, settings) => {
            // Checks whether one if its menu items equals the source ID from the provided settings.
            var sourceId = PrimeFaces.ajax.Utils.getSourceId(settings);
            if (!widget || sourceId === null) {
                return false;
            }
            return this.links.filter('[id="' + sourceId + '"]').length > 0;
        });

        //aria
        this.trigger.attr('role', 'button').attr('aria-disabled', String(this.cfg.disabled ?? false));
    }

    /**
     * Brings up the overlay menu with the menu items, as if the menu button were pressed.
     */
    override show(): void {
        if (this.cfg.disabled) {
            return;
        }
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    PrimeFaces.nextZindex($this.menu ?? $());
                    $this.align();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.resetFocus(true);
                    $this.trigger.attr('aria-expanded', 'true');
                    $this.menu?.find('a.ui-menuitem-link:focusable:first').trigger('focus');
                }
            });
        }
    }

    /**
     * Hides the overlay menu with the menu items, as if the user clicked outside the menu.
     */
    override hide(): void {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.deactivateAndReset();
                    if ($this.trigger && $this.trigger.is(':button')) {
                        $this.trigger.attr('aria-expanded', 'false');
                    }
                }
            });
        }
    }

    /**
     * Align the overlay panel with the menu items so that it is positioned next to the menu button.
     */
    override align(): void {
        this.menu?.css({ left: '', top: '', 'transform-origin': 'center top' });

        if (this.menu?.parent().is(this.jq)) {
            this.menu.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.menu?.position({
                my: 'left top',
                at: 'left bottom',
                of: this.trigger,
                collision: this.cfg.collision || 'flip',
                using: function(pos: { top: number; left: number }, directions: {horizontal: number; vertical: number;}) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    override disable(): void {
        this.cfg.disabled = true;
        this.hide();
        PrimeFaces.utils.disableButton(this.trigger);
    }

    /**
     * Enables this button so that the user can press the button.
     */
    override enable(): void {
        this.cfg.disabled = false;
        PrimeFaces.utils.enableButton(this.trigger);
    }
}
