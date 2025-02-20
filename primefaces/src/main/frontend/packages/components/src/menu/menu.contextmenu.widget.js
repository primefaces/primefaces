/**
 * __PrimeFaces ContextMenu Widget__
 * 
 * ContextMenu provides an overlay menu displayed on mouse right-click event.
 * 
 * @typedef {"single" | "multiple"} PrimeFaces.widget.ContextMenu.SelectionMode  Selection mode for the context, whether
 * the user may select only one or multiple items at the same time.
 * 
 * @typedef PrimeFaces.widget.ContextMenu.BeforeShowCallback Client side callback invoked before the context menu is
 * shown.
 * @this {PrimeFaces.widget.ContextMenu} PrimeFaces.widget.ContextMenu.BeforeShowCallback
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.ContextMenu.BeforeShowCallback.event Event that triggered the context menu to
 * be shown (e.g. a mouse click).
 * @return {boolean} PrimeFaces.widget.ContextMenu.BeforeShowCallback ` true` to show the context menu, `false` to
 * prevent is from getting displayed.
 * 
 * @interface {PrimeFaces.widget.ContextMenu.ContextMenuProvider} ContextMenuProvider Interface for widgets that wish to
 * provide a context menu. They need to implement the `bindContextMenu` method.  This method is called once when the
 * context menu is initialized. Widgets should register the appropriate event listeners and call `menuWidget.show()`
 * to bring up the context menu.
 * @template ContextMenuProvider.TTarget Type of the widget that wishes to provide a context menu.
 * @method ContextMenuProvider.bindContextMenu Callback that is invoked when the context menu is initialized. Lets the
 * context menu provider register the appropriate event listeners for when the context menu should be shown and hidden.
 * @param {PrimeFaces.widget.ContextMenu} ContextMenuProvider.bindContextMenu.menuWidget The widget instance of the
 * context menu.
 * @param {TTarget} ContextMenuProvider.bindContextMenu.targetWidget The widget instance of the target widget that wants
 * to add a context menu.
 * @param {string | JQuery} ContextMenuProvider.bindContextMenu.targetId ID selector or DOM element of the target, i.e.
 * the element the context menu belongs to.
 * @param {PrimeFaces.widget.ContextMenuCfg} ContextMenuProvider.bindContextMenu.cfg The current configuration of the
 * context menu.
 * 
 * @prop {JQuery} jqTarget Target element of this context menu. A right click on the target brings up this context menu.
 * @prop {string | JQuery} jqTargetId ID selector or DOM element of the target, i.e. the element this context menu
 * belongs to.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * 
 * @interface {PrimeFaces.widget.ContextMenuCfg} cfg The configuration for the {@link  ContextMenu| ContextMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.TieredMenuCfg} cfg
 * 
 * @prop {string} cfg.appendTo Search expression for the element to which this context menu is appended. This is usually
 * invoke before the context menu is shown. When it returns `false`, this context menu is not shown.
 * @prop {PrimeFaces.widget.ContextMenu.BeforeShowCallback} cfg.beforeShow Client side callback invoked before the
 * context menu is shown.
 * @prop {string} cfg.event Event that triggers this context menu, usually a (right) mouse click.
 * @prop {PrimeFaces.widget.ContextMenu.SelectionMode} cfg.selectionMode Defines the selection behavior.
 * @prop {string} cfg.target Client ID of the target widget.
 * @prop {string} cfg.targetFilter Selector to filter the elements to attach the menu.
 * @prop {string} cfg.targetWidgetVar Widget variable of the target widget.
 * @prop {boolean} cfg.disabled If true, prevents menu from being shown.
 */
PrimeFaces.widget.ContextMenu = class ContextMenu extends PrimeFaces.widget.TieredMenu {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        cfg.autoDisplay = true;
        super.init(cfg);
        this.cfg.overlay = true;
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';

        var $this = this,
        documentTarget = (this.cfg.target === undefined);

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target);
        this.jqTarget = $(this.jqTargetId);

        //append to body
        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.id);

        //attach contextmenu
        if(documentTarget) {
            var event = 'contextmenu.' + this.id + '_contextmenu';
            
            $(document).off(event).on(event, function(e) {
                $this.show(e);
            });

            if (PrimeFaces.env.isTouchable(this.cfg)) {
                $(document).swipe({
                    longTap:function(e) {
                       $this.show(e);
                    }
                });
            }
        }
        else {
            var binded = false;

            if (this.cfg.targetWidgetVar) {
                var targetWidget = PrimeFaces.widgets[this.cfg.targetWidgetVar];

                if (targetWidget) {
                    if (typeof targetWidget.bindContextMenu === 'function') {
                        targetWidget.bindContextMenu(this, targetWidget, this.jqTargetId, this.cfg);
                        // GitHub #6776 IOS needs long touch on table/tree but Android does not
                        if(PrimeFaces.env.ios) {
                            $this.bindTouchEvents();
                        }
                        binded = true;
                    }
                }
                else {
                    PrimeFaces.warn("ContextMenu targets a widget which is not available yet. Please place the contextMenu after the target component. targetWidgetVar: " + this.cfg.targetWidgetVar);
                }
            }

            if (binded === false) {
                var customEvent = this.cfg.event + '.' + this.id + '_contextmenu';

                $(document).off(customEvent, this.jqTargetId).on(customEvent, this.jqTargetId, null, function(e) {
                    $this.show(e);
                });

                $this.bindTouchEvents();
            }
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        this._cleanup();
        super.refresh(cfg);
    }

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        super.destroy();
        this._cleanup();
    }

    /**
    * Clean up this widget and remove events from the DOM.
    * @private
    */
    _cleanup() {
        if (this.cfg.target === undefined) {
            var event = 'contextmenu.' + this.id + '_contextmenu';
            $(document).off(event);
            if (PrimeFaces.env.isTouchable(this.cfg)) {
                $(document).swipe('destroy');
            }
        }
        else {
            var eventCustom = this.cfg.event + '.' + this.id + '_contextmenu';
            $(document).off(eventCustom, this.jqTargetId);
            if (PrimeFaces.env.isTouchable(this.cfg)) {
                this.jqTarget.swipe('destroy');
            }
        }
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindPanelEvents() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'click.' + this.id + '_hide', this.jq,
            function(e) { return e.key === 'Cancel' ? $this.jqTarget : null; },
            function(e, eventTarget) {
                if(e && !($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jqTarget, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    unbindPanelEvents() {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }

    /**
     * Binds mobile touch events.
     * @protected
     */
    bindTouchEvents() {
        if (PrimeFaces.env.isTouchable(this.cfg)) {
             var $this = this;

             // GitHub #6776 turn off Copy/Paste menu for IOS
             if(PrimeFaces.env.ios) {
                $(document.body).addClass('ui-touch-selection-disabled');
             }

             $this.jqTarget.swipe({
                 longTap:function(e) {
                      $this.show(e);
                 }
             });
        }
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindItemEvents() {
        super.bindItemEvents();

        var $this = this;

        //hide menu on item click
        this.links.on('click', function(e) {
            var target = $(e.target),
                submenuLink = target.hasClass('ui-submenu-link') ? target : target.closest('.ui-submenu-link');

            if (submenuLink.length) {
                return;
            }

            $this.hide();
        });
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery.TriggeredEvent} [e] The event that triggered this context menu to be shown.
     * 
     * Note:  __This parameter is not optional__, but is marked as such since this method overrides a parent method
     * that does not have any parameters. Do not (implicitly) cast an instance of this class to a parent type.
     */
    show(e) {
        var $this = this;

        if(this.cfg.disabled || this.cfg.targetFilter && $(e.target).is(':not(' + this.cfg.targetFilter + ')')) {
            return;
        }

        //hide other contextmenus if any
        $(document.body).children('.ui-contextmenu:visible').hide();

        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this, e);
            if(retVal === false) {
                return;
            }
        }

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    var win = $(window),
                    left = e.pageX,
                    top = e.pageY,
                    width = $this.jq.outerWidth(),
                    height = $this.jq.outerHeight();

                    //collision detection for window boundaries
                    if ((left + width) > (win.width())+ win.scrollLeft()) {
                        left = left - width;
                    }
                    if ((top + height ) > (win.height() + win.scrollTop())) {
                        top = top - height;
                    }
                    if (top < 0) {
                        top = e.pageY;
                    }

                    $this.jq.css({
                        'left': left + 'px',
                        'top': top + 'px',
                        'z-index': PrimeFaces.nextZindex(),
                        'transform-origin': 'center top'
                    });
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.resetFocus(true);
                    $this.jq.find('a.ui-menuitem-link:focusable:first').trigger('focus');
                }
            });
        }

        e.preventDefault();
        e.stopPropagation();
    }

    /**
     * @override
     * @inheritdoc
     */
    hide() {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    //hide submenus
                    $this.jq.find('li.ui-menuitem-active').each(function() {
                        $this.deactivate($(this), true);
                    });
                }
            });
        }
    }

    /**
     * Checks whether this context menu is open.
     * @return {boolean} `true` if this context menu is currently visible, `false` otherwise.
     */
    isVisible() {
        return this.jq.is(':visible');
    }

    /**
     * Finds the target element of this context menu. A right-click on that target element brings up this context menu. 
     * @private
     * @return {JQuery} The target element of this context men.
     */
    getTarget() {
        return this.jqTarget;
    }

}
