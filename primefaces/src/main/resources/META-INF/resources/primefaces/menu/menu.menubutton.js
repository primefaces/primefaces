/**
 * __PrimeFaces MenuButton Widget__
 *
 * MenuButton displays different commands in a popup menu.
 *
 * @prop {JQuery} trigger The DOM element for the menu button.
 * @prop {JQuery} menu The DOM element for the menu overlay panel.
 * @prop {string} menuId Client ID of the menu overlay panel.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {number} [timeoutId] Timeout ID used for the animation when the menu is shown.
 * @forcedProp {number} [ajaxCount] Number of concurrent active Ajax requests.
 *
 * @interface {PrimeFaces.widget.MenuButtonCfg} cfg The configuration for the {@link  MenuButton| MenuButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.TieredMenuCfg} cfg
 *
 * @prop {boolean} cfg.disabled Whether this menu button is initially disabled.
 * @prop {string} cfg.collision When the positioned element overflows the window in some direction, move it to an
 * alternative position. Similar to my and at, this accepts a single value or a pair for horizontal/vertical,
 * e.g., `flip`, `fit`, `fit flip`, `fit none`.
 */
PrimeFaces.widget.MenuButton = PrimeFaces.widget.TieredMenu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.menuId = this.jqId + '_menu';
        this.trigger = this.jq.children('button');
        this.menu = this.jq.children('.ui-menu');
        this.cfg.disabled = this.trigger.is(':disabled');
        this.cfg.overlay = true;

        this.bindButtonEvents();
        PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.menu, 'ui-connected-overlay');
    },

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    disable: function() {
        this.cfg.disabled = true;
        this.hide();
        PrimeFaces.utils.disableButton(this.trigger);
    },

    /**
     * Enables this button so that the user can press the button.
     */
    enable: function() {
        this.cfg.disabled = false;
        PrimeFaces.utils.enableButton(this.trigger);
    },

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} menuitem
     * @param {JQuery} submenu
     */
    showSubmenu: function(menuitem, submenu) {
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
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.trigger.off('mouseover.menubutton mouseout.menubutton mousedown.menubutton mouseup.menubutton focus.menubutton blur.menubutton');
        this.trigger.off('keydown.menubutton');
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        this._super(cfg);
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindButtonEvents: function() {
        var $this = this;

        //button visuals
        this.trigger.on('mouseover.menubutton', function() {
            if (!$this.trigger.hasClass('ui-state-focus')) {
                $this.trigger.addClass('ui-state-hover');
            }
        }).on('mouseout.menubutton', function() {
            if (!$this.trigger.hasClass('ui-state-focus')) {
                $this.trigger.removeClass('ui-state-hover ui-state-active');
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

            if ($this.menu.is(':visible')) {
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

        this.trigger.on('keydown.menubutton', function(e) {
            if ($this.cfg.disabled) {
                return;
            }
            switch (e.code) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    $this.show();
                    e.preventDefault();
                    break;

                case 'Escape':
                case 'Tab':
                    $this.hide();
                    break;
            }
        });

        PrimeFaces.bindButtonInlineAjaxStatus($this, $this.trigger, function(widget, settings) {
            // Checks whether one if its menu items equals the source ID from the provided settings.
            var sourceId = PrimeFaces.ajax.Utils.getSourceId(settings);
            if (!widget || sourceId === null) {
                return false;
            }
            return $this.links.filter('[id="' + sourceId + '"]').length;
        });

        //aria
        this.trigger.attr('role', 'button').attr('aria-disabled', this.cfg.disabled);
    },

    /**
     * Sets up all panel event listeners
     *
     * @override
     */
    bindPanelEvents: function() {
        var $this = this;

        if (!$this.cfg.disabled) {
            this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.menu,
                function() { return $this.trigger; },
                function(e, eventTarget) {
                    if (e && !($this.menu.is(eventTarget) || $this.menu.has(eventTarget).length > 0)) {
                        $this.trigger.removeClass('ui-state-focus ui-state-hover');
                        $this.hide();
                    }
                });
        }

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.menu, function() {
            $this.handleOverlayViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.handleOverlayViewportChange();
        });
    },

    /**
     * Unbind all panel event listeners
     *
     * @override
     */
    unbindPanelEvents: function() {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    },

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a 
     * resize immediately and close the overlay. See GitHub #7075.
     * @private
     */
    handleOverlayViewportChange: function() {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.alignPanel();
        } else {
            this.hide();
        }
    },

    /**
     * Brings up the overlay menu with the menu items, as if the menu button were pressed.
     *
     * @override
     */
    show: function() {
        if (this.cfg.disabled) {
            return;
        }
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.menu.css('z-index', PrimeFaces.nextZindex());
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.resetFocus(true);
                    $this.trigger.attr('aria-expanded', 'true');
                    $this.menu.find('a.ui-menuitem-link:focusable:first').trigger('focus');
                }
            });
        }
    },

    /**
     * Hides the overlay menu with the menu items, as if the user clicked outside the menu.
     *
     * @override
     */
    hide: function() {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.resetFocus(false);
                    if ($this.trigger && $this.trigger.is(':button')) {
                        $this.trigger.attr('aria-expanded', 'false');
                        PrimeFaces.queueTask(() =>  $this.trigger.trigger('focus'));
                    }
                }
            });
        }
    },

    /**
     * Align the overlay panel with the menu items so that it is positioned next to the menu button.
     */
    alignPanel: function() {
        this.menu.css({ left: '', top: '', 'transform-origin': 'center top' });

        if (this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.menu.position({
                my: 'left top',
                at: 'left bottom',
                of: this.trigger,
                collision: this.cfg.collision || 'flip',
                using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

});
