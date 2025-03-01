import { TieredMenu, type TieredMenuCfg } from "./menu.tieredmenu.widget.js";

/**
 * The configuration for the {@link  ContextMenu} widget.
 * 
 * You can access this configuration via {@link ContextMenu.cfg |cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface ContextMenuCfg extends TieredMenuCfg, PrimeType.widget.ContextMenuLikeWidgetCfg {
    /**
     * Search expression for the element to which this context menu is appended. This is usually
     * invoke before the context menu is shown. When it returns `false`, this context menu is not shown.
     */
    appendTo: string;

    /**
     * Client side callback invoked before the
     * context menu is shown.
     */
    beforeShow: PrimeType.widget.ContextMenu.BeforeShowCallback;

    /**
     * If true, prevents menu from being shown.
     */
    disabled: boolean;

    /**
     * Event that triggers this context menu, usually a (right) mouse click.
     */
    event: string;

    /**
     * Defines the selection behavior.
     */
    selectionMode: PrimeType.widget.ContextMenu.SelectionMode;

    /**
     * Client ID of the target widget.
     */
    target: string;

    /**
     * Selector to filter the elements to attach the menu.
     */
    targetFilter: string;

    /**
     * Widget variable of the target widget.
     */
    targetWidgetVar: string;
}

/**
 * __PrimeFaces ContextMenu Widget__
 * 
 * ContextMenu provides an overlay menu displayed on mouse right-click event.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class ContextMenu<Cfg extends ContextMenuCfg = ContextMenuCfg> extends TieredMenu<Cfg> implements PrimeType.widget.ContextMenuLikeWidget {
    /**
     * Target element of this context menu. A right click on the target brings up this context menu.
     */
    jqTarget: JQuery = $();

    /**
     * ID selector or DOM element of the target, i.e. the element this context menu
     * belongs to.
     */
    jqTargetId: string | Document = "";

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        cfg.autoDisplay = true;
        super.init(cfg);
        this.cfg.overlay = true;
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';

        const documentTarget = this.cfg.target === undefined;

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target ?? "");
        this.jqTarget = typeof this.jqTargetId === "string" ? $(this.jqTargetId) : $<any>(this.jqTargetId);

        //append to body
        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.getId());

        //attach contextmenu
        if (documentTarget) {
            const event = 'contextmenu.' + this.id + '_contextmenu';
            
            $(document).off(event).on(event, (e) => {
                this.show(e);
            });

            if (PrimeFaces.env.isTouchable(this.cfg)) {
                $(document).swipe({
                    longTap: (e) => {
                       this.show(e);
                    }
                });
            }
        }
        else {
            let bound = false;

            if (this.cfg.targetWidgetVar) {
                const targetWidget = PrimeFaces.widgets[this.cfg.targetWidgetVar];

                if (targetWidget) {
                    if (typeof targetWidget.bindContextMenu === 'function') {
                        // TODO Requires cast "as string" to encode the implicit logic
                        // that when this.cfg.target is not undefined, we set this.jqTargetId to a string. 
                        targetWidget.bindContextMenu(this, targetWidget, this.jqTargetId as string, this.cfg);
                        // GitHub #6776 IOS needs long touch on table/tree but Android does not
                        if(PrimeFaces.env.ios) {
                            this.bindTouchEvents();
                        }
                        bound = true;
                    }
                }
                else {
                    PrimeFaces.warn("ContextMenu targets a widget which is not available yet. Please place the contextMenu after the target component. targetWidgetVar: " + this.cfg.targetWidgetVar);
                }
            }

            if (bound === false) {
                const customEvent = this.cfg.event + '.' + this.id + '_contextmenu';

                // TODO Requires cast "as string" to encode the implicit logic
                // that when this.cfg.target is not undefined, we set this.jqTargetId to a string. 
                $(document).off(customEvent, this.jqTargetId as string).on(customEvent, this.jqTargetId as string, null, (e) => {
                    this.show(e);
                });

                this.bindTouchEvents();
            }
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this._cleanup();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this._cleanup();
    }

    /**
    * Clean up this widget and remove events from the DOM.
    */
    private _cleanup(): void {
        if (this.cfg.target === undefined) {
            var event = 'contextmenu.' + this.id + '_contextmenu';
            $(document).off(event);
            if (PrimeFaces.env.isTouchable(this.cfg)) {
                $(document).swipe('destroy');
            }
        }
        else {
            var eventCustom = this.cfg.event + '.' + this.id + '_contextmenu';
            // TODO Requires cast "as string" to encode the implicit logic
            // that when this.cfg.target is not undefined, we set this.jqTargetId to a string. 
            $(document).off(eventCustom, this.jqTargetId as string);
            if (PrimeFaces.env.isTouchable(this.cfg)) {
                this.jqTarget.swipe('destroy');
            }
        }
    }

    protected override bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'click.' + this.id + '_hide', this.jq,
            (e) => { return e.key === 'Cancel' ? this.jqTarget : null; },
            (e, eventTarget) => {
                if(e && !(this.jq.is(eventTarget) || this.jq.has(eventTarget[0] ?? "").length > 0)) {
                    this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jqTarget, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });
    }

    protected override unbindPanelEvents(): void {
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
     */
    protected bindTouchEvents(): void {
        if (PrimeFaces.env.isTouchable(this.cfg)) {
             // GitHub #6776 turn off Copy/Paste menu for IOS
             if(PrimeFaces.env.ios) {
                $(document.body).addClass('ui-touch-selection-disabled');
             }

             this.jqTarget.swipe({
                 longTap: (e) => {
                      this.show(e);
                 }
             });
        }
    }

    protected override bindItemEvents(): void {
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
     * @param e The event that triggered this context menu to be shown.
     * 
     * Note:  __This parameter is not optional__, but is marked as such since
     * this method overrides a parent method that does not have any parameters.
     * It semantically violates the Liskov substitution principle. Do not
     * (implicitly) cast an instance of this class to a parent type.
     */
    // TODO Is the event parameter required? If so, perhaps this should be a separate method.
    override show(e?: JQuery.TriggeredEvent): void {
        if(this.cfg.disabled || this.cfg.targetFilter && $(e?.target).is(':not(' + this.cfg.targetFilter + ')')) {
            return;
        }

        // hide other context menus if any
        $(document.body).children('.ui-contextmenu:visible').hide();

        if(this.cfg.beforeShow) {
            const  retVal = e !== undefined ? this.cfg.beforeShow.call(this, e) : undefined;
            if (retVal === false) {
                return;
            }
        }

        if (this.transition) {
            this.transition.show({
                onEnter: () => {
                    const win = $(window);
                    let left = e?.pageX ?? 0;
                    let top = e?.pageY ?? 0;
                    const width = this.jq.outerWidth() ?? 0;
                    const height = this.jq.outerHeight() ?? 0;

                    //collision detection for window boundaries
                    if ((left + width) > (win.width() ?? 0) + (win.scrollLeft() ?? 0)) {
                        left = left - width;
                    }
                    if ((top + height ) > ((win.height() ?? 0) + (win.scrollTop() ?? 0))) {
                        top = top - height;
                    }
                    if (top < 0) {
                        top = e?.pageY ?? 0;
                    }

                    this.jq.css({
                        'left': left + 'px',
                        'top': top + 'px',
                        'z-index': PrimeFaces.nextZindex(),
                        'transform-origin': 'center top'
                    });
                },
                onEntered: () => {
                    this.bindPanelEvents();
                    this.resetFocus(true);
                    this.jq.find('a.ui-menuitem-link:focusable:first').trigger('focus');
                }
            });
        }

        e?.preventDefault();
        e?.stopPropagation();
    }

    override hide(): void {
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
     * @return `true` if this context menu is currently visible, `false` otherwise.
     */
    isVisible(): boolean {
        return this.jq.is(':visible');
    }

    /**
     * Finds the target element of this context menu. A right-click on that target element brings up this context menu. 
     * @return The target element of this context men.
     */
    getTarget(): JQuery {
        return this.jqTarget;
    }
}
