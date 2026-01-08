/**
 * The configuration for the {@link Dialog} widget.
 * 
 * You can access this configuration via {@link Dialog.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface DialogCfg extends PrimeType.widget.DynamicOverlayWidgetCfg, PrimeType.hook.dialog.SharedDialogOptions {
    /**
     * Whether the dialog is positioned absolutely.
     */
    absolutePositioned: boolean;

    /**
     * Only relevant for dynamic="true": Defines if activating the dialog should load the contents from server again. For cache="true" (default) the dialog content is only loaded once.
     */
    cache: boolean;

    /**
     * Whether lazy loading of the content via AJAX is enabled.
     */
    dynamic: boolean;

    /**
     * Defines which component to apply focus by search expression.
     */
    focus: string;

    /**
     * Optional handler for obtaining additional DOM elements which are allowed to be focused via tabbing.
     */
    getModalTabbables: PrimeType.widget.Dialog.GetModalTabbablesHandler;

    /**
     * Whether the dialog contents contain an {@link HTMLIFrameElement}. 
     */
    hasIframe: boolean;

    /**
     * Used by the dialog framework. Expression for the element to use as the dialog header.
     */
    headerElement: string;

    /**
     * Effect to use when hiding the dialog.
     */
    hideEffect: string;

    /**
     * The minimum height of the dialog in pixels.
     */
    minHeight: number;

    /**
     * The minimum width of the dialog in pixels.
     */
    minWidth: number;

    /**
     * Position of the dialog relative to the target.
     */
    my: string;

    /**
     * Client-side callback to invoke when the dialog is
     * closed.
     */
    onHide: PrimeType.widget.Dialog.OnHideCallback;

    /**
     * Client-side callback to invoke when the dialog is opened.
     */
    onShow: PrimeType.widget.Dialog.OnShowCallback;

    /**
     * Effect to use when showing the dialog
     */
    showEffect: string;

    /**
     * Used by the dialog framework when opening dialogs within iframes. ID of the
     * component that requested the dialog to open.
     */
    sourceComponentId: string;

    /**
     * Used by the dialog framework when opening dialogs within iframes. CSS selectors
     * of the source frames.
     */
    sourceFrames: string[];

    /**
     * Used by the dialog framework when opening dialogs within iframes. Name of the
     * widget variable of the widget that requested the dialog to open.
     */
    sourceWidgetVar: string;

    /**
     * When enabled, dialog is visible by default.
     */
    visible: boolean;
}

/**
 * __PrimeFaces Dialog Widget__
 * 
 * Dialog is a panel component that is displayed as an overlay on top of other elements on the current page. Optionally,
 * the dialog may be modal and block the user from interacting with elements below the dialog.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class Dialog<Cfg extends DialogCfg = DialogCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /**
     * DOM element of the icon for closing this dialog, when this dialog is closable (an `x` by
     * default).
     */
    closeIcon: JQuery = $();

    /**
     * DOM element of the dialog box.
     */
    box: JQuery = $();

    /**
     * DOM element of the container for the content of this dialog.
     */
    content: JQuery = $();

    /**
     * Used by the dialog framework to open the dialog.
     */
    destroyIntervalId?: number;

    /**
     * Element that was focused before the dialog was opened.
     */
    focusedElementBeforeDialogOpened: Element | null = null;

    /**
     * DOM element of the container with the footer of this dialog.
     */
    footer: JQuery = $();

    /**
     * DOM elements of the title bar icons of this dialog.
     */
    icons: JQuery = $();

    /**
     * The DOM element of the overlay that is put over iframes during a resize.
     */
    iframeFix?: JQuery = $();

    /**
     * The native DOM element instance of the container element of this widget (same element as the
     * `jq` property).
     */
    jqEl: HTMLElement | undefined;

    /**
     * The last known [ top, left ] offset.
     */
    lastOffset?: number[];

    /**
     * Whether the dialog content was already loaded (when dynamic loading via AJAX is
     * enabled.)
     */
    loaded?: boolean;

    /**
     * DOM element of the icon for maximizing this dialog, when this dialog can be maximized.
     */
    maximizeIcon: JQuery = $();

    /**
     * Whether the dialog is currently maximized.
     */
    maximized?: boolean;

    /**
     * DOM element clone of the JQ to be used for minimizing.
     */
    minimizeClone: JQuery = $();

    /**
     * DOM element of the icon for minimizing this dialog, when this dialog can be minimized.
     */
    minimizeIcon: JQuery = $();

    /**
     * Whether the dialog is currently minimized.
     */
    minimized?: boolean;

    /**
     * The DOM element of the parent that contains this dialog, i.e the element to which the dialog
     * was appended.
     */
    parent: JQuery = $();

    /**
     * Whether the position of the dialog was already set. If not, it must be set the
     * next time the dialog is shown.
     */
    positionInitialized: boolean = false;

    /**
     * The DOM element of the resize icons for resizing the dialog, if resizing is
     * enabled.
     */
    resizers?: JQuery = $();

    /**
     * The client-side state of the dialog such as its width
     * and height. The client-side state can be preserved during AJAX updates by sending it to the server.
     */
    state?: PrimeType.widget.Dialog.ClientState;

    /**
     * DOM element of the title bar container of this dialog.
     */
    titlebar: JQuery = $();

    /**
     * Used by the dialog framework to store the element for the title.
     */
    titleContainer?: JQuery;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.box = this.jq.children('.ui-dialog-box');
        this.content = this.box.children('.ui-dialog-content');
        this.titlebar = this.box.children('.ui-dialog-titlebar');
        this.footer = this.box.find('.ui-dialog-footer');
		this.icons = this.titlebar.find('.ui-dialog-titlebar-icon');
        this.closeIcon = this.titlebar.children('.ui-dialog-titlebar-close');
        this.minimizeIcon = this.titlebar.children('.ui-dialog-titlebar-minimize');
        this.maximizeIcon = this.titlebar.children('.ui-dialog-titlebar-maximize');
        this.cfg.absolutePositioned = this.jq.hasClass('ui-dialog-absolute');
        this.jqEl = this.jq[0];

        this.positionInitialized = false;

        //configuration
        this.cfg.width = this.cfg.width||'auto';
        this.cfg.height = this.cfg.height||'auto';
        this.cfg.draggable = this.cfg.draggable !== false;
        this.cfg.resizable = this.cfg.resizable !== false;
        this.cfg.cache = this.cfg.cache !== false;
        this.cfg.responsive = this.cfg.responsive !== false;
        this.cfg.minWidth = this.cfg.minWidth||150;
        this.cfg.minHeight = this.cfg.minHeight||this.titlebar.outerHeight();
        this.cfg.my = this.cfg.my||'center';
        this.cfg.position = this.cfg.position||'center';
        this.parent = this.jq.parent();
        this.focusedElementBeforeDialogOpened = null;

        this.initSize();
        
        //events
        this.bindEvents();

        if(this.cfg.draggable) {
            this.setupDraggable();
        }

        if(this.cfg.resizable){
            this.setupResizable();
        }

        //docking zone
        if($(document.body).children('.ui-dialog-docking-zone').length === 0) {
            $(document.body).append('<div class="ui-dialog-docking-zone"></div>');
        }

        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }

        if(this.cfg.responsive) {
            this.bindResizeListener();
        }
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.positionInitialized = false;
        this.loaded = false;

        $(document).off('keydown.dialog_' + cfg.id);

        if(this.minimized) {
            var dockingZone = $(document.body).children('.ui-dialog-docking-zone');
            if(dockingZone.length && dockingZone.children(this.jqId).length) {
                this.removeMinimize();
                dockingZone.children(this.jqId).remove();
            }
        }

        this.minimized = false;
        this.maximized = false;

        super.refresh(cfg);
    }

    /**
     * Computes and applies the correct size for this dialog, according to the current configuration.
     */
    protected initSize(): void {
        this.box.css({
            'width': String(this.cfg.width),
            'height': 'auto'
        });

        this.content.height(this.cfg.height ?? "auto");

        if(this.cfg.fitViewport) {
            this.fitViewport();
        }
    }

    /**
     * Makes this dialog fit the current browser window, if the `fitViewport` option is enabled.
     */
    protected fitViewport(): void {
       this.fitHeight();
       this.fitWidth();
    }

    private fitHeight(): void {
        const windowHeight = $(window).height() ?? 0;

        const margin = (this.box.outerHeight(true) ?? 0) - (this.box.outerHeight() ?? 0);
        const headerHeight = this.titlebar.outerHeight(true) ?? 0;
        const contentPadding = (this.content.innerHeight() ?? 0) - (this.content.height() ?? 0);
        const footerHeight = this.footer.outerHeight(true) || 0;

        const maxHeight = windowHeight - (margin + headerHeight + contentPadding + footerHeight);

        this.content.css('max-height', maxHeight + 'px');

        if (this.cfg.hasIframe) {
            this.content.children('iframe').css('max-height', maxHeight + 'px');
        }
    }

    private fitWidth(): void {
        const windowWidth = $(window).width() ?? 0;

        const margin = (this.box.outerWidth(true) ?? 0) - (this.box.outerWidth() ?? 0);
        const contentPadding = (this.content.innerWidth() ?? 0) - (this.content.width() ?? 0);

        const maxWidth = windowWidth - (margin + contentPadding);

        this.box.css('max-width', maxWidth + 'px');

        if (this.cfg.hasIframe) {
            this.content.children('iframe').css('max-width', maxWidth + 'px');
        }
    }

    /**
     * @returns The DOM elements which are allowed to be focused via tabbing.
     */
    protected override getModalTabbables(): JQuery {
        const tabbablesInIframe = this.cfg.getModalTabbables ? this.cfg.getModalTabbables() : $();
        
        return this.box.find(':tabbable').add(tabbablesInIframe).add(this.footer.find(':tabbable'));
    }

    /**
     * Displays this dialog. In case the `dynamic` option is enabled and the content was not yet loaded, this may result
     * in an AJAX request to the sever to retrieve the content. Also triggers the show behaviors registered for this
     * dialog.
     * 
     * @param duration Durations are given in milliseconds; higher values indicate slower
     * animations, not faster ones. The strings `fast` and `slow` can be supplied to indicate durations of 200 and 600
     * milliseconds, respectively.
     */
    show(duration?: number | string): void {
        if(this.isVisible()) {
			if(this.minimized) {
				this.toggleMinimize();
			}
            return;
        }

        $('body').addClass('ui-dialog-open');
        
        // Remember the focused element before we opened the dialog
        // so we can return focus to it once we close the dialog.
        this.focusedElementBeforeDialogOpened = document.activeElement;

        if(!this.loaded && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            if (this.positionInitialized === false && this.jqEl) {
                this.jqEl.style.visibility = "hidden";
                this.jqEl.style.display = "block";
                this.initPosition();
                this.jqEl.style.display = "none";
                this.jqEl.style.visibility = "visible";
            }

            this._show(duration);

            if(this.cfg.dynamic && !this.cfg.cache) {
                this.loaded = false;
            }
        }
    }

    /**
     * Performs the client-side actions needed to actually show this dialog. Compare to `show`, which loads the dialog
     * content from the server if required, then call this method.
     * 
     * @param duration Durations are given in milliseconds; higher values indicate slower
     * animations, not faster ones. The strings `fast` and `slow` can be supplied to indicate durations of 200 and 600
     * milliseconds, respectively.
     */
    protected _show(duration?: number | string): void {
        this.moveToTop();

        //offset
        if(this.cfg.absolutePositioned && this.lastOffset?.length === 2) {
            this.box.css({ top: this.lastOffset[0], left: this.lastOffset[1] });
        }

        if (this.cfg.showEffect) {
            this.jq.show(this.cfg.showEffect, duration, 'normal', () => {
                this.postShow();
            });
        }
        else {
            //display dialog
            this.jq.show(duration);

            this.postShow();
        }

        if(this.cfg.modal) {
            this.enableModality();
            this.bindDismissibleMaskListener();
        }
    }

    /**
     * Called after this dialog became visible. Triggers the behaviors and registered event listeners.
     */
    protected postShow(): void {
        if (this.cfg.fitViewport) {
            this.fitViewport();
        }
        
        this.callBehavior('open');

        PrimeFaces.invokeDeferredRenders(this.id);

        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.jq.attr({
            'aria-hidden': false
            ,'aria-live': 'polite'
        });

        this.applyFocus();
    }

    /**
     * Hide the dialog with an optional animation lasting for the given duration.
     * 
     * @param duration Durations are given in milliseconds; higher values indicate slower
     * animations, not faster ones. The strings `fast` and `slow` can be supplied to indicate durations of 200 and 600
     * milliseconds, respectively.
     */
    hide(duration?: number | string): void {
        if (!this.isVisible()) {
            return;
        }

		if(this.minimized) {
			this.toggleMinimize();
		}

        this.lastOffset = [ this.box.css('top'), this.box.css('left') ];

        if (this.cfg.hideEffect) {
            this.jq.hide(this.cfg.hideEffect, duration, 'normal', () => {
                if(this.cfg.modal) {
                    this.disableModality();
                    this.unbindDismissibleMaskListener();
                }
                this.onHide();
            });
        }
        else {
            this.jq.hide();
            if(this.cfg.modal) {
                this.disableModality();
                this.unbindDismissibleMaskListener();
            }
            this.onHide(duration);
        }

        var otherDialogs = $(".ui-dialog:visible").length > 0;
        if (!otherDialogs) {
            $('body').removeClass('ui-dialog-open');
        }
    }

    /**
     * Puts focus on the first element that can be focused.
     */
    protected applyFocus(): void {
        if (this.cfg.focus) {
            PrimeFaces.queueTask(() => {
                PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.focus).trigger('focus')
            }, 100);
        }
        else
            PrimeFaces.focus(null, this.getId());
    }
    
    /**
     * Puts focus on the element that opened this dialog.
     */
    protected returnFocus(): void {
        const el = this.focusedElementBeforeDialogOpened;
        if (el instanceof HTMLElement || el instanceof SVGElement) {
            // #11860 do not return focus to caller if other dialogs are still open
            const otherDialogs = $(".ui-dialog:visible").length > 0;
            if (otherDialogs) {
                return;
            }
            // #11318 prevent scrolling in Chrome by using delayed execution
            PrimeFaces.queueTask(function() { el.focus({ preventScroll: true }) }, 100);
        }
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    protected bindEvents(): void {
        var $this = this;

        //Move dialog to top if target is not a trigger for a PrimeFaces overlay
        this.jq.on("mousedown", function(e) {
            if(!$(e.target).data('primefaces-overlay-target')) {
                $this.moveToTop();
            }
        });

        this.icons.on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        }).on('focus', function() {
            $(this).addClass('ui-state-focus');
        }).on('blur', function() {
            $(this).removeClass('ui-state-focus');
        });

        PrimeFaces.skinCloseAction(this.closeIcon);
        this.closeIcon.on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });

        this.maximizeIcon.attr('aria-label', this.getAriaLabel('maximizeLabel'));
        this.maximizeIcon.on("click", function(e) {
            $this.toggleMaximize();
            e.preventDefault();
        });

        this.minimizeIcon.attr('aria-label', this.getAriaLabel('minimizeLabel'));
        this.minimizeIcon.on("click", function(e) {
            $this.toggleMinimize();
            e.preventDefault();
        });

        if(this.cfg.hasIframe && !this.cfg.resizable && this.cfg.resizeObserver) {
            // https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver
            const resizeObserver = new ResizeObserver(() => {
                const iframe = this.content.children('iframe')[0];
                const body = iframe?.contentWindow?.document.body;
                const frameHeight = body ? ($(body).outerHeight(true) ?? 0) + 8 : 8; // 8 because of weird p:messages - sizing issue
                if (iframe) {
                    $(iframe).height(frameHeight);
                }

                if (this.cfg.resizeObserverCenter) {
                    // further improvement possible - maybe only center the dialog again if parts of the dialog are outside the window
                    this.initPosition();
                }
            });
            const bodyToObserve = this.content.children('iframe')[0]?.contentWindow?.document.body;
            if (bodyToObserve) {
                resizeObserver.observe(bodyToObserve);
            }
        }

        if(this.cfg.closeOnEscape) {
            $(document).on('keydown.dialog_' + this.id, function(e) {
                if(!e.isDefaultPrevented() && e.key === 'Escape' && $this.isVisible()) {
                    // GitHub #6677 if multiple dialogs check if this is the topmost active dialog to close
                    var currentZIndex = parseInt($this.jq.css('z-index'));
                    var highestZIndex = Math.max(...$('.ui-dialog:visible').map(function() {
                        return parseInt($(this).css('z-index')) || 0;
                    }).get());
                    if (currentZIndex === highestZIndex) {
                        $this.hide();
                        e.preventDefault();
                        e.stopPropagation();
                    }
                };
            });
            this.addDestroyListener(function() {
                $(document).off('keydown.dialog_' + this.id);
            });
        }
    }

    /**
     * Binds a click event listener to the dialog mask that enables dismissing (hiding)
     * the dialog when the mask is clicked, if the dialog is modal and the dismissibleMask option is enabled.
     * The event is namespaced with the dialog's id to allow precise unbinding later.
     * Prevents the default and stops propagation if the dialog is topmost and visible.
     */
    protected bindDismissibleMaskListener(): void {
        if (this.cfg.dismissibleMask && this.cfg.modal) {
            var $this = this;
            $(".ui-dialog.ui-widget.ui-hidden-container:visible").on('click.mask_' + this.id, function (e) {
                if (!e.isDefaultPrevented() && $(e.target).is(this)) {
                    $this.hide();
                    e.preventDefault();
                    e.stopPropagation();
                }
            });
        }
    }

    /**
     * Removes the click event listener from the dialog mask that was used for dismissing
     * (hiding) the dialog when the mask is clicked. This only unbinds the event if the dialog
     * is modal and the dismissibleMask option is enabled.
     */
    protected unbindDismissibleMaskListener(): void {
        if (this.cfg.dismissibleMask && this.cfg.modal) {
            $(".ui-dialog.ui-widget.ui-hidden-container").off('click.mask_' + this.id);
        }
    }

    /**
     * Sets up all event listeners required to make this dialog draggable.
     */
    protected setupDraggable(): void {
        var $this = this;

        this.box.draggable({
            cancel: '.ui-dialog-content, .ui-dialog-titlebar-close',
            handle: '.ui-dialog-titlebar',
            containment : $this.jq,
            start: function( event, ui ) {
                $this.jq.addClass('ui-overflow-hidden');
            },
            drag: function( event, ui ) {
                if ($this.cfg.absolutePositioned && $(window).scrollTop() > $this.box.offset().top) {
                    $this.box[0].scrollIntoView();
                }
            },
            stop: function( event, ui ) {
                $this.jq.removeClass('ui-overflow-hidden');
                if($this.hasBehavior('move')) {
                    var ext = {
                        params: [
                            {name: $this.id + '_top', value: ui.offset.top},
                            {name: $this.id + '_left', value: ui.offset.left}
                        ]
                    };
                    $this.callBehavior('move', ext);
                }
            }
        });
    }

    /**
     * Sets up all event listeners required to make this dialog resizable.
     */
    protected setupResizable(): void {
        this.box.resizable({
            handles : 'n,s,e,w,ne,nw,se,sw',
            minWidth : this.cfg.minWidth,
            minHeight : this.cfg.minHeight,
            alsoResize : this.content,
            containment : this.jq,
            start: (_, ui) => {
                this.jq.addClass('ui-overflow-hidden');
                const offset: JQuery.Coordinates = this.box.offset() ?? { left: 0, top: 0 };
                this.box.data('offset', offset);

                if(this.cfg.hasIframe) {
                    this.iframeFix = $('<div style="position:absolute;background-color:transparent;width:100%;height:100%;top:0;left:0;"></div>').appendTo(this.content);
                }

                if (this.hasBehavior('resizeStart')) {
                    var ext = {
                        params: [
                            {name: this.id + '_width', value: ui.size.width},
                            {name: this.id + '_height', value: ui.size.height}
                        ]
                    };
                    this.callBehavior('resizeStart', ext);
                }
            },
            stop: (_, ui) => {
                this.jq.removeClass('ui-overflow-hidden');

                if(this.cfg.hasIframe) {
                    this.iframeFix?.remove();
                }

                if (this.hasBehavior('resizeStop')) {
                    var ext = {
                        params: [
                            {name: this.id + '_width', value: ui.size.width},
                            {name: this.id + '_height', value: ui.size.height}
                        ]
                    };
                    this.callBehavior('resizeStop', ext);
                }
            }
        });

        this.resizers = this.box.children('.ui-resizable-handle');
    }
    
    /**
     * Resets the dialog position as specified by the `position` property of this widget configuration.
     */
    protected resetPosition(): void {
       this.initPosition();
    }

    /**
     * Positions this dialog on the screen as specified by the widget configuration.
     */
    protected initPosition(): void {
        var $this = this;

        //reset
        this.box.css({left:'0',top:'0'});

        if(/(center|left|top|right|bottom)/.test(this.cfg.position ?? "")) {
            this.cfg.position = this.cfg.position?.replace(',', ' ');

            this.box.position({
                        my: this.cfg.my,
                        at: this.cfg.position,
                        collision: 'fit',
                        of: $this.cfg.absolutePositioned ? window : $this.jq,
                        //make sure dialog stays in viewport
                        using: function(pos: JQuery.Coordinates) {
                            const l = pos.left < 0 ? 0 : pos.left;
                            const t = pos.top < 0 ? 0 : pos.top;

                            $(this).css({
                                left: l + 'px',
                                top: t + 'px',
                            });
                        },
                    });
        }
        else {
            const coords = this.cfg.position?.split(',') ?? [];
            const x = PrimeFaces.trim(coords[0]);
            const y = PrimeFaces.trim(coords[1]);

            this.box.offset({
                left: parseFloat(x),
                top: parseFloat(y),
            });
        }

        if(!$this.cfg.absolutePositioned) {
            this.positionInitialized = true;
        }
    }

    /**
     * Called when this dialog was closed. Invokes the appropriate behaviors and event listeners.
     * @param duration The duration for the hide effect, if any. 
     */
    protected onHide(duration?: number | string): void {
        this.callBehavior('close');

        this.jq.attr({
            'aria-hidden': true,
            'aria-live': 'off',
        });

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, duration);
        }
        
        // return focus to where it was before we opened the dialog
        this.returnFocus();
    }

    /**
     * Moves this dialog to the top so that it is positioned above other elements and overlays.
     */
    moveToTop(): void {
        PrimeFaces.nextZindex(this.jq);
    }

    /**
     * Toggle maximization, as if the user had clicked the maximize button. If this dialog is not yet
     * maximized, maximizes it. If this dialog is already maximized, reverts it back to its original size.
     */
    toggleMaximize(): void {
        if (this.minimized) {
            this.toggleMinimize();
        }

        if(this.maximized) {
            this.jq.removeClass('ui-dialog-maximized');
            this.restoreState();

            this.maximizeIcon.children('.ui-icon').removeClass('ui-icon-newwin').addClass('ui-icon-extlink');
            this.maximized = false;

            this.callBehavior('restoreMaximize');
        }
        else {
            this.saveState();

            this.jq.addClass('ui-dialog-maximized').css({
                'width': String(($(window).width() ?? 0) - 6),
                'height': String($(window).height()),
            }).offset({
                top: $(window).scrollTop() ?? 0,
                left: $(window).scrollLeft() ?? 0,
            });

            //maximize content
            var contentPadding = (this.content.innerHeight() ?? 0) - (this.content.height() ?? 0);
            this.content.css({
                width: 'auto',
                height: String((this.box.height() ?? 0) - (this.titlebar.outerHeight() ?? 0) - contentPadding)
            });

            this.maximizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-extlink').addClass('ui-icon-newwin');
            this.maximized = true;

            this.callBehavior('maximize');
        }
    }

    /**
     * Toggles minification, as if the user had clicked the minimize button. If this dialog is not yet minimized,
     * minimizes it.  If this dialog is already minimized, restores its original position.
     */
    toggleMinimize(): void {
        var animate = true,
        dockingZone = $(document.body).children('.ui-dialog-docking-zone');

        if(this.maximized) {
            this.toggleMaximize();
            animate = false;
        }

        if(this.minimized) {
            this.removeMinimize();

            this.callBehavior('restoreMinimize');
        }
        else {
            this.saveState();
            
            // create a minimize clone which is just the titlebar
            this.minimizeClone = this.jq.clone(true);
            this.minimizeClone.attr('id', this.jq.attr('id') + '_clone')
            this.minimizeClone.addClass('ui-dialog-minimized');
            this.minimizeClone.children('.ui-dialog-content').remove();
            this.minimizeClone.find('.ui-dialog-footer').remove();

            if (this.cfg.resizable) {
                this.minimizeClone.children('.ui-resizable-handle').remove();
            }

            if (animate) {
                this.jq.transfer(
                    {
                        to: dockingZone,
                        className: 'ui-dialog-minimizing',
                        duration: 500,
                    },
                    () => this.dock(dockingZone));
            }
            else {
                this.dock(dockingZone);
            }
        }
    }

    /**
     * Called when this dialog is minimized. Restores the original position of this dialog.
     */
    protected removeMinimize(): void {
        this.minimizeClone.remove();
        this.jq.show();
        this.restoreState();
        this.minimized = false;
    }

    /**
     * Docks this dialog to the given docking zone. The docking zone is usually at the bottom of the screen and displays
     * a list of minimized dialogs.
     * @param zone Zone to dock to.
     */
    protected dock(zone: JQuery): void {
        zone.css('z-index', this.jq.css('z-index'));
        this.jq.hide();
        this.minimizeClone.appendTo(zone).css({'position':'static', 'height':'auto', 'width':'auto', 'float': 'left'});
        var titlebar = this.minimizeClone.children('.ui-dialog-titlebar');
        var minimizeIcon = titlebar.children('.ui-dialog-titlebar-minimize');
        minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-minus').addClass('ui-icon-plus');
        this.minimized = true;
        this.callBehavior('minimize');
    }

    /**
     * Saves the current state of this dialog, such as its width and height. Used by the minimize and maximize
     * feature to restore the dialog's original position and size.
     */
    protected saveState(): void {
        this.state = {
            width: this.box[0]?.style.width || this.box.width() || 0,
            height: this.box[0]?.style.height || this.box.height() || 0,
            contentWidth: this.content[0]?.style.width || this.content.width() || 0,
            contentHeight: this.content[0]?.style.height || this.content.height() || 0,
            offset: this.box.offset() ?? { top: 0, left: 0},
            windowScrollLeft: $(window).scrollLeft() ?? 0,
            windowScrollTop: $(window).scrollTop() ?? 0,
        };
    }

    /**
     * Restores the state as saved by {@link saveState}. Used by the minimize and maximize
     * feature to restore the dialog's original position and size.
     */
    protected restoreState(): void {
        if (this.state) {
            this.box.css({
                'width': this.state.width,
                'height': this.state.height
            });
            this.content.css({
                'width': this.state.contentWidth,
                'height': this.state.contentHeight
            });
    
            this.box.offset({
                top: this.state.offset.top + (($(window).scrollTop() ?? 0) - this.state.windowScrollTop),
                left: this.state.offset.left + (($(window).scrollLeft() ?? 0) - this.state.windowScrollLeft),
            });
        }
    }

    /**
     * Loads the content of the dialog via AJAx, if this dialog is `dynamic` and the the content has not yet been
     * loaded.
     */
    protected loadContents(): void {
        const options: PrimeType.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            ignoreAutoUpdate: true,
            params: [
                {name: this.getId() + '_contentLoad', value: true}
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.content.html(content);
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.loaded = true;
                this.show();
            }
        };

        if(this.hasBehavior('loadContent')) {
            this.callBehavior('loadContent', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Applies all `ARIA` attributes to the contents of this dialog.
     */
    protected applyARIA(): void {
        var role = this instanceof PrimeFaces.widget.ConfirmDialog ? 'alertdialog' : 'dialog';
        this.jq.attr({
            'role': role,
            'aria-describedby': this.id + '_content',
            'aria-hidden': !this.cfg.visible,
            'aria-modal': this.cfg.modal,
        });
        
        // GitHub #4727
        var title = this.id + '_title';
        if ($(PrimeFaces.escapeClientId(title)).length) {
            this.jq.attr('aria-labelledby', title);
        }

        this.titlebar.children('a.ui-dialog-titlebar-icon').attr('role', 'button');
    }

    /**
     * Checks whether this dialog is opened and visible. This method returns `true` irrespective of whether this dialog 
     * is minimized, maximized, or shown normally. Returns `false` only when this dialog is closed. 
     * @returns `true` if this dialog is currently being shown, `false` otherwise.
     */
    isVisible(): boolean {
        return this.jq.is(':visible') || this.minimized === true;
    }

    /**
     * Sets up the event listeners for handling resize events.
     */
    protected bindResizeListener(): void {
        var $this = this;

        // internal function to handle resize or scrolling
        function handleResize() {
            if ($this.isVisible() && !$this.box.hasClass("ui-resizable-resizing")) {
                if ($this.cfg.fitViewport) {
                    $this.fitViewport();
                }

                if (!$this.cfg.absolutePositioned) {
                    $this.initPosition();
                }
            }
        }

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, handleResize);

        // #11578 if not using dialog framework (it has its own observer) then resize if the dialog is resized
        if (!this.cfg.hasIframe && !this.cfg.resizable) {
            const observer = new ResizeObserver(_entries => {
                handleResize();
            });
            const firstElement = this.jq[0];
            if (firstElement) {
                observer.observe(firstElement);
            }
        }
    }
}

/**
 * The configuration for the {@link  ConfirmDialog} widget. You can access this configuration via
 * {@link ConfirmDialog.cfg | cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 */
export interface ConfirmDialogCfg extends DialogCfg {
    /**
     * When enabled, the confirm dialog becomes a shared for other components that require confirmation.
     */
    global: boolean;
}

/**
 * __PrimeFaces ConfirmDialog Widget__
 * 
 * ConfirmDialog is a replacement to the legacy JavaScript confirmation box. Skinning, customization and avoiding popup
 * blockers are notable advantages over the classic JavaScript confirmation box.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class ConfirmDialog<Cfg extends ConfirmDialogCfg = ConfirmDialogCfg> extends Dialog<Cfg> {
    /**
     * DOM element of the icon displayed next to the confirmation message.
     */
    icon: JQuery = $();

    /**
     * DOM element of the confirmation message displayed in this confirm dialog.
     */
    message: JQuery = $();

    /**
     * DOM element of the No button.
     */
    noButton: JQuery = $();

    /**
     * DOM element of the title bar text.
     */
    title: JQuery = $();

    /**
     * DOM element of the Yes button.
     */
    yesButton: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        cfg.draggable = false;
        cfg.resizable = false;
        cfg.modal = true;

        if (!cfg.appendTo && cfg.global) {
        	cfg.appendTo = '@(body)';
        }

        super.init(cfg);

        this.title = this.titlebar.children('.ui-dialog-title');
        this.message = this.content.children('.ui-confirm-dialog-message');
        this.icon = this.content.children('.ui-confirm-dialog-severity');

        if(this.cfg.global) {
            PrimeFaces.confirmDialog = this;
            
            this.title.data('p-text', this.title.text());
            this.message.data('p-text', this.message.text());
            this.icon.data('p-icon', this.icon.removeClass('ui-icon ui-confirm-dialog-severity').attr('class') ?? "");
            this.yesButton = this.jq.find('.ui-confirmdialog-yes');
            this.noButton = this.jq.find('.ui-confirmdialog-no');
            this.yesButton.data('p-text', this.yesButton.children('.ui-button-text').text());
            this.noButton.data('p-text', this.noButton.children('.ui-button-text').text());
            this.yesButton.data('p-icon', this.yesButton.children('.ui-icon').attr('class') ?? "");
            this.noButton.data('p-icon', this.noButton.children('.ui-icon').attr('class') ?? "");

            this.jq.on('click.ui-confirmdialog', '.ui-confirmdialog-yes, .ui-confirmdialog-no', null, function(e) {
                const el = $(this);

                if(el.hasClass('ui-confirmdialog-yes') && PrimeFaces.confirmSource) {
                    const source = PrimeFaces.confirmSource;
                    const id = source.get(0) ?? document.createElement("div");
                    const js = source.data('pfconfirmcommand');
                    const command = id ? $(id) : $();

                    if (PrimeFaces.ajax.Utils.isAjaxRequest(js) || command.is('a')) {
                        // command is ajax=true
                        var originalOnClick;

                        if (source[0]) {
                            // @ts-expect-error Internal JQuery method
                            const events = $._data(source[0], "events");
                            originalOnClick = source.prop('onclick') || (events && events.click ? events.click[0].handler : null);
                        }

                        // Temporarily remove the click handler and execute the new one
                        source.prop('onclick', null).off("click").on("click", function(event) {
                            PrimeFaces.csp.executeEvent(id, js, event);
                        }).trigger("click");

                        // Restore the original click handler if it exists
                        if (originalOnClick) {
                            source.off("click").on("click", originalOnClick);
                        }
                    } else {
                        // #13736: regular button just execute its javascript
                        if (source.attr('type') === "button") {
                            PrimeFaces.csp.executeEvent(id, js, e);
                        } else {
                            // command is ajax=false
                            if (command.prop('onclick')) {
                                command.removeAttr("onclick");
                            } else {
                                command.off("click");
                            }
                            command.removeAttr("data-pfconfirmcommand").trigger("click");
                        }
                    }

                    PrimeFaces.confirmDialog?.hide();
                    PrimeFaces.confirmSource = null;
                }
                else if(el.hasClass('ui-confirmdialog-no')) {
                    PrimeFaces.confirmDialog?.hide();
                    PrimeFaces.confirmSource = null;
                }

                e.preventDefault();
            });
        }
    }

    protected override applyFocus(): void {
        this.jq.find(':button,:submit').filter(':visible:enabled').eq(0).trigger('focus');
    }
    
    protected override onHide(duration?: string | number): void {
        super.onHide(duration);

        // Remove added classes and reset button labels to their original values
        if (this.cfg.global) {
            if (this.title.data('p-text')) {
                this.title.text(this.title.data('p-text'));
            }
            if (this.message.data('p-text')) {
                this.message.text(this.message.data('p-text'));
            }
            if (this.icon.data('p-icon')) {
                this.icon.attr('class', 'ui-icon ui-confirm-dialog-severity ' + this.icon.data('p-icon'));
            }
            this.yesButton.removeClass(this.yesButton.data('p-class'));
            this.noButton.removeClass(this.noButton.data('p-class'));
            this.yesButton.children('.ui-button-text').text(this.yesButton.data('p-text'));
            this.noButton.children('.ui-button-text').text(this.noButton.data('p-text'));
            this.yesButton.children('.ui-icon').attr('class', this.yesButton.data('p-icon'));
            this.noButton.children('.ui-icon').attr('class', this.noButton.data('p-icon'));
        }
    }

    /**
     * Shows the given message in this confirmation dialog.
     * @param msg Message to show.
     */
    showMessage(msg: Partial<PrimeType.hook.confirm.ConfirmMessage>): void {
        // Execute any code specified to run before showing the message
        if (msg.beforeShow) {
            PrimeFaces.csp.eval(msg.beforeShow);
        }

        // Set icon if provided, or hide it otherwise
        var iconClass = msg.icon || this.icon.data('p-icon');
        if (iconClass) {
            this.icon.removeClass().addClass('ui-icon ui-confirm-dialog-severity ' + iconClass);
            this.icon.show();
        } else {
            this.icon.hide();
        }

        // Set labels and classes for yes and no buttons if provided
        if (msg.yesButtonLabel) {
            this.yesButton.children('.ui-button-text').text(msg.yesButtonLabel);
        }
        if (msg.yesButtonClass) {
            this.yesButton.addClass(msg.yesButtonClass).data('p-class', msg.yesButtonClass);
        }
        if (msg.yesButtonIcon) {
            PrimeFaces.utils.replaceIcon(this.yesButton.children('.ui-icon'), msg.yesButtonIcon);
        }
        if (msg.noButtonLabel) {
            this.noButton.children('.ui-button-text').text(msg.noButtonLabel);
        }
        if (msg.noButtonClass) {
            this.noButton.addClass(msg.noButtonClass).data('p-class', msg.noButtonClass);
        }
        if (msg.noButtonIcon) {
            PrimeFaces.utils.replaceIcon(this.noButton.children('.ui-icon'), msg.noButtonIcon);
        }

        // Set title, message content, and escape HTML if necessary
        if (msg.header) {
            this.title.text(msg.header);
        }
        if (msg.message) {
            if (msg.escape) {
                this.message.text(msg.message);
            } else {
                this.message.html(msg.message);
            }
        }

        // Reset position if specified in global configuration
        if (this.cfg.global) {
            this.positionInitialized = false;
        }

        this.show();
    }
}

/**
 * The configuration for the {@link DynamicDialog} widget.
 * 
 * You can access this configuration via {@link DynamicDialog.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface DynamicDialogCfg extends DialogCfg {
}

/**
 * __PrimeFaces Dynamic Dialog Widget__ 
 * 
 * Used by the dialog framework for displaying other Faces views or external pages in a dialog on the current.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class DynamicDialog<Cfg extends DynamicDialogCfg = DynamicDialogCfg> extends Dialog<Cfg> {
    override show(): void {
        if(this.jq.hasClass('ui-overlay-visible')) {
            return;
        }

        if(this.positionInitialized === false) {
            this.initPosition();
        }

        this._show();
    }

    protected override _show(): void {
        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });

        this.moveToTop();

        this.jq.show();

        if(this.cfg.height != "auto") {
            this.content.height((this.box.outerHeight() ?? 0) - (this.titlebar.outerHeight(true) ?? 0));
        }

        this.postShow();

        if(this.cfg.modal) {
            this.enableModality();
            this.bindDismissibleMaskListener();
        }
    }

    protected override initSize(): void {
        this.box.css({
            'width': String(this.cfg.width),
            'height': String(this.cfg.height)
        });

        if(this.cfg.fitViewport) {
            this.fitViewport();
        }
    }
}
