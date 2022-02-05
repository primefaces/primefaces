/**
 * __PrimeFaces MenuButton Widget__
 *
 * MenuButton displays different commands in a popup menu.
 *
 * @prop {JQuery} button The DOM element for the menu button.
 * @prop {JQuery} menu The DOM element for the menu overlay panel.
 * @prop {JQuery} menuitems The DOM elements for the individual menu entries.
 * @prop {string} menuId Client ID of the menu overlay panel.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {number} [timeoutId] Timeout ID used for the animation when the menu is shown.
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
        this.button = this.jq.children('button');
        this.menu = this.jq.children('.ui-menu');
        this.menuitems = this.jq.find('.ui-menuitem');
        this.cfg.disabled = this.button.is(':disabled');

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
        PrimeFaces.utils.disableButton(this.button);
    },

    /**
     * Enables this button so that the user can press the button.
     */
    enable: function() {
        this.cfg.disabled = false;
        PrimeFaces.utils.enableButton(this.button);
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
        if(this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = setTimeout(function () {
           submenu.css('z-index', PrimeFaces.nextZindex())
                  .show()
                  .position(pos);
        }, this.cfg.delay);
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.button.off('mouseover.menubutton mouseout.menubutton mousedown.menubutton mouseup.menubutton focus.menubutton blur.menubutton');
        this.menuitems.off('mouseover.menubutton mouseout.menubutton click.menubutton');
        this.button.off('keydown.menubutton');
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
        this.button.on('mouseover.menubutton', function(){
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.addClass('ui-state-hover');
            }
        }).on('mouseout.menubutton', function() {
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.removeClass('ui-state-hover ui-state-active');
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

            if($this.menu.is(':visible')) {
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
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //menuitem visuals
        this.menuitems.on('mouseover.menubutton', function(e) {
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).on('mouseout.menubutton', function(e) {
            $(this).removeClass('ui-state-hover');
        }).on('click.menubutton', function() {
            $this.button.removeClass('ui-state-focus');
            $this.hide();
        });

        //keyboard support
        this.button.on('keydown.menubutton', function(e) {
            if ($this.cfg.disabled) {
                return;
            }
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator)') : null;

                        if(prevItems && prevItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            prevItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator)') : $this.menuitems.eq(0);

                        if(nextItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            nextItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();
                break;

                case keyCode.ENTER:
                case keyCode.SPACE:
                    if($this.menu.is(':visible'))
                        $this.menuitems.filter('.ui-state-hover').children('a').trigger('click');
                    else
                        $this.show();

                    e.preventDefault();
                break;

                case keyCode.ESCAPE:
                case keyCode.TAB:
                    $this.hide();
                break;
            }
        });

        if (this.cfg.disableOnAjax === true) {
            $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
                if ($this.isXhrSource(settings)) {
                    $this.disable();
                }
            }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
                if ($this.isXhrSource(settings)) {
                    $this.enable();
                }
            });
        }

        //aria
        this.button.attr('role', 'button').attr('aria-disabled', this.cfg.disabled);
    },

    /**
     * Checks whether one if its menu items equals the source ID from the provided settings.
     *
     * @param {JQuery.AjaxSettings} settings containing source ID.
     * @returns {boolean} `true` if one if its menu items equals the source ID from the provided settings.
     * @private
     */
    isXhrSource: function(settings) {
        var sourceId = PrimeFaces.ajax.Utils.getSourceId(settings);
        if (sourceId === null) {
            return false;
        }
        return this.menuitems.find('[id="' + sourceId + '"]').length;
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
                function() { return $this.button; },
                function(e, eventTarget) {
                    if (!($this.menu.is(eventTarget) || $this.menu.has(eventTarget).length > 0)) {
                        $this.button.removeClass('ui-state-focus ui-state-hover');
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
        if (PrimeFaces.env.mobile) {
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
                    $this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
                }
            });
        }
    },

    /**
     * Align the overlay panel with the menu items so that it is positioned next to the menu button.
     */
    alignPanel: function() {
        this.menu.css({left:'', top:'', 'transform-origin': 'center top'});

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.menu.position({
                my: 'left top',
                at: 'left bottom',
                of: this.button,
                collision: this.cfg.collision || 'flip',
                using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

});
