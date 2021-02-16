/**
 * __PrimeFaces Menu Widget__
 * 
 * Base class for the different menu widets, such as the `PlainMenu` or the `TieredMenu`.
 * 
 * @prop {JQuery} keyboardTarget The DOM element for the form element that can be targeted via arrow or tab keys. 
 * @prop {boolean} itemMouseDown `true` if a menu item was clicked and the mouse button is still pressed.
 * @prop {JQuery} trigger DOM element which triggers this menu.
 * 
 * @interface {PrimeFaces.widget.MenuCfg} cfg The configuration for the {@link  Menu| Menu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg 
 * 
 * @prop {string} cfg.appendTo Search expression for the element to which the menu overlay is appended.
 * @prop {string} cfg.at Defines which position on the target element to align the positioned element against
 * @prop {string} cfg.collision When the positioned element overflows the window in some direction, move it to an
 * alternative position.
 * @prop {string} cfg.my Defines which position on the element being positioned to align with the target element.
 * @prop {boolean} cfg.overlay `true` if this menu is displayed as an overlay, or `false` otherwise.
 * @prop {JQueryUI.JQueryPositionOptions} cfg.pos Describes how to align this menu.
 * @prop {string} cfg.trigger ID of the event which triggers this menu.
 * @prop {string} cfg.triggerEvent Event which triggers this menu.
 */
PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.overlay) {
            this.initOverlay();
        }

        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');
    },

    /**
     * Initializes the overlay. Finds the element to which to append this menu and appends it to that element.
     * @protected
     */
    initOverlay: function() {
        var $this = this;

        this.jq.addClass('ui-menu-overlay');

        this.cfg.trigger = this.cfg.trigger.replace(/\\\\:/g,"\\:");

        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.id);
        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');

        this.cfg.pos = {
            my: this.cfg.my,
            at: this.cfg.at,
            of: this.trigger,
            collision: this.cfg.collision || "flip",
            using: function(pos, directions) {
                $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
            }
        };

        this.trigger.off(this.cfg.triggerEvent + '.ui-menu').on(this.cfg.triggerEvent + '.ui-menu', function(e) {
            var trigger = $(this);

            if($this.jq.is(':visible')) {
                $this.hide();
            }
            else {
                $this.show();

                if(trigger.is(':button')) {
                    trigger.addClass('ui-state-focus');
                }

                e.preventDefault();
            }
        });

        //dialog support
        this.setupDialogSupport();
    },

    /**
     * Sets up all panel event listeners
     * @protected
     */
    bindPanelEvents: function() {
        var $this = this;

        //hide overlay on document click
        this.itemMouseDown = false;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.jq,
            function() { return $this.trigger; },
            function(e, eventTarget) {
                var menuItemLink = '.ui-menuitem-link:not(.ui-submenu-link, .ui-state-disabled)';

                if (eventTarget.is(menuItemLink) || eventTarget.closest(menuItemLink).length) {
                    $this.itemMouseDown = true;
                }
                else if(!($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0)) {
                    $this.hide(e);
                }
            });

        $(document.body).on('mouseup.' + this.id, function (e) {
            if ($this.itemMouseDown) {
                $this.hide(e);
                $this.itemMouseDown = false;
            }
        });

        //Hide overlay on resize
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, function() {
            $this.hide();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.trigger, function() {
            $this.hide();
        });
    },

    /**
     * Unbind all panel event listeners
     * @protected
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

        $(document.body).off('mouseup.' + this.id);
    },

    /**
     * Performs some setup required to make this overlay menu work with dialogs.
     * @protected
     */
    setupDialogSupport: function() {
        var dialog = this.trigger.parents('.ui-dialog:first');

        if(dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.jq.css('position', 'fixed');
        }
    },

    /**
     * Shows (displays) this menu so that it becomes visible and can be interacted with.
     */
    show: function() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.jq.css('z-index', PrimeFaces.nextZindex());
                    $this.align();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                }
            });
        }
    },

    /**
     * Hides this menu so that it becomes invisible and cannot be interacted with any longer.
     */
    hide: function() {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    if ($this.trigger && $this.trigger.is(':button')) {
                        $this.trigger.removeClass('ui-state-focus');
                    }
                }
            });
        }
    },

    /**
     * Aligns this menu as specified in its widget configuration (property `pos`).
     */
    align: function() {
        this.jq.css({left:'', top:'', 'transform-origin': 'center top'}).position(this.cfg.pos);
    }
});





