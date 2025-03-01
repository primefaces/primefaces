/**
 * The configuration for the {@link  ConfirmPopup} widget.
 * 
 * You can access this configuration via {@link ConfirmPopup.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface ConfirmPopupCfg extends PrimeType.widget.DynamicOverlayWidgetCfg {
    /**
     * The search expression for the element to which the overlay panel should be
     * appended.
     */
    appendTo: string | null;

    /**
     * When set `true`, clicking outside of the popup hides the overlay.
     */
    dismissable: boolean;

    /**
     * When enabled, confirmPopup becomes a shared for other components that require confirmation.
     */
    global: boolean;

    /**
     * Event on target to hide the popup.
     */
    hideEvent: string;

    /**
     * Event on target to show the popup.
     */
    showEvent: string;
}

/**
 * __PrimeFaces ConfirmPopup Widget__
 * 
 * ConfirmPopup displays a confirmation overlay displayed relatively to its target.
 */
export class ConfirmPopup<Cfg extends ConfirmPopupCfg = ConfirmPopupCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /**
     * The DOM element for the content of the confirm popup.
     */
    content: JQuery = $();

    /**
     * Element that was focused before the dialog was opened.
     */
    focusedElementBeforeDialogOpened: Element | null = null;

    /**
     * Unbind callback for the hide overlay handler.
     */
    hideOverlayHandler?: PrimeType.Unbindable;

    /**
     * The DOM element for the message icon.
     */
    icon: JQuery = $();

    /**
     * DOM element of the confirmation message displayed in this confirm popup.
     */
    message: JQuery = $();

    /**
     * DOM element of the No button.
     */
    noButton: JQuery = $();

    /**
     * Unbind callback for the resize handler.
     */
    resizeHandler?: PrimeType.Unbindable;

    /**
     * Unbind callback for the scroll handler.
     */
    scrollHandler?: PrimeType.Unbindable;

    /**
     * Handler for CSS transitions used by this widget.
     */
    transition?: PrimeType.CssTransitionHandler | null = null;

    /**
     * DOM element of the Yes button.
     */
    yesButton: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        cfg.dismissable = cfg.dismissable !== false;
        if (!cfg.appendTo && cfg.global) {
            cfg.appendTo = '@(body)';
        }

        super.init(cfg);

        this.focusedElementBeforeDialogOpened = null;
        this.content = this.jq.children('.ui-confirm-popup-content');
        this.message = this.content.children('.ui-confirm-popup-message');
        this.icon = this.content.children('.ui-confirm-popup-icon');
        if (this.cfg.global) {
            this.content.data('p-text', this.content.text());
            this.message.data('p-text', this.message.text());
            this.icon.data('p-icon', this.icon.removeClass('ui-confirm-popup-icon').attr('class') ?? "");
            this.yesButton = this.jq.find('.ui-confirm-popup-yes');
            this.noButton = this.jq.find('.ui-confirm-popup-no');
            this.yesButton.data('p-text', this.yesButton.children('.ui-button-text').text());
            this.noButton.data('p-text', this.noButton.children('.ui-button-text').text());
            this.yesButton.data('p-icon', this.yesButton.children('.ui-icon').attr('class') ?? "");
            this.noButton.data('p-icon', this.noButton.children('.ui-icon').attr('class') ?? "");
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');

        this.bindEvents();
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    protected bindEvents(): void {
        if (this.cfg.global) {
            PrimeFaces.confirmPopup = this;

            this.jq.on('click.ui-confirmpopup', '.ui-confirm-popup-yes, .ui-confirm-popup-no', null, function (e) {
                const el = $(this);

                if (el.hasClass('ui-confirm-popup-yes') && PrimeFaces.confirmPopupSource) {
                    const source = PrimeFaces.confirmPopupSource;
                    const id = source.get(0);
                    const js = source.data('pfconfirmcommand');
                    const command = id ? $(id) : $();

                    // Test if the function matches the pattern
                    if (PrimeFaces.ajax.Utils.isAjaxRequest(js) || command.is('a')) {
                        // command is ajax=true
                        var originalOnClick;

                        if (source[0]) {
                            // @ts-expect-error Internal JQuery method
                            // TODO Do we really need to use JQuery internals?
                            const events = $._data(source[0], "events");
                            originalOnClick = source.prop('onclick') || (events && events.click ? events.click[0].handler : null);
                        }

                        // Temporarily remove the click handler and execute the new one
                        source.prop('onclick', null).off("click").on("click", function (event) {
                            PrimeFaces.csp.executeEvent(id ?? document.createElement("div"), js, event);
                        }).trigger("click");

                        // Restore the original click handler if it exists
                        if (originalOnClick) {
                            source.off("click").on("click", originalOnClick);
                        }
                    }
                    else {
                        // command is ajax=false
                        if (command.prop('onclick')) {
                            command.removeAttr("onclick");
                        }
                        else {
                            command.off("click");
                        }
                        command.removeAttr("data-pfconfirmcommand").trigger("click");
                    }

                    PrimeFaces.confirmPopup?.hide();
                    PrimeFaces.confirmPopupSource = null;
                }
                else if (el.hasClass('ui-confirm-popup-no')) {
                    PrimeFaces.confirmPopup?.hide();
                    PrimeFaces.confirmPopupSource = null;
                }

                e.preventDefault();
            });
        }
    }

    /**
     * Sets up all panel event listeners
     * @param target Selector or DOM element of the target component that triggers this popup.
     */
    private bindPanelEvents(target?: JQuery): void {
        //hide overlay when mousedown is at outside of overlay
        if (this.cfg.dismissable) {
            this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.jq,
                () => PrimeFaces.confirmPopupSource,
                (e, eventTarget) => {
                    if (e && !(this.jq.is(eventTarget) || this.jq.has(eventTarget[0] ?? "").length > 0)) {
                        this.hide();
                    }
                });
        }

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', target, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
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
     * Makes the popup visible.
     * @param target Selector or DOM element of the target component that triggers this popup.
     */
    show(target?: string | JQuery | HTMLElement | undefined): void {
        // Remember the focused element before we opened the dialog
        // so we can return focus to it once we close the dialog.
        this.focusedElementBeforeDialogOpened = document.activeElement;

        if (this.transition) {
            const $target = PrimeFaces.utils.toJQuery(target);
            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.jq);
                    this.align($target);
                },
                onEntered: () => {
                    this.bindPanelEvents($target);
                    this.applyFocus();
                }
            });
        }
    }

    /**
     * Hides the popup.
     * @param callback Callback that is invoked after this popup was closed.
     */
    hide(callback?: PrimeType.widget.ConfirmPopup.HideCallback): void {
        if (this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    if (callback) {
                        callback();
                    }
                    this.returnFocus(50);

                    // Remove added classes and reset button labels to their original values
                    if (!PrimeFaces.animationEnabled || !this.isVisible()) {
                        this.restoreButtons();
                    }
                }
            });
        }
        else {
            this.returnFocus();
            this.restoreButtons();
        }
    }

    /**
     * Restore the button text and styling to its original form.
     */
    private restoreButtons(): void {
        var $this = this;
        if ($this.cfg.global) {
            if ($this.content.data('p-text')) {
                $this.content.text($this.content.data('p-text'));
            }
            if ($this.message.data('p-text')) {
                $this.message.text($this.message.data('p-text'));
            }
            if ($this.icon.data('p-icon')) {
                $this.icon.attr('class', 'ui-confirm-popup-icon ' + $this.icon.data('p-icon'));
            }
            $this.yesButton.removeClass($this.yesButton.data('p-class'));
            $this.noButton.removeClass($this.noButton.data('p-class'));
            $this.yesButton.children('.ui-button-text').text($this.yesButton.data('p-text'));
            $this.noButton.children('.ui-button-text').text($this.noButton.data('p-text'));
            $this.yesButton.children('.ui-icon').attr('class', $this.yesButton.data('p-icon'));
            $this.noButton.children('.ui-icon').attr('class', $this.noButton.data('p-icon'));
        }
    }

    /**
     * Aligns the popup so that it is shown at the correct position.
     * @param target Jquery selector that is the target of this popup
     */
    private align(target?: JQuery): void {
        if (target) {
            const $this = this;

            this.jq.removeClass('ui-confirm-popup-flipped');

            this.jq.css({ left: '0px', top: '0px', 'transform-origin': 'center top' }).position({
                my: 'left top',
                at: 'left bottom',
                of: target,
                collision: 'flipfit',
                using: function (pos: { top: number; left: number }, directions: { horizontal: number; vertical: number }) {
                    const targetOffset = target.offset() ?? { top: 0, left:0 };
                    let arrowLeft = 0;

                    if (pos.left < targetOffset.left) {
                        arrowLeft = targetOffset.left - pos.left;
                    }
                    $this.jq.css('--overlayArrowLeft', arrowLeft + 'px');

                    if (pos.top < targetOffset.top) {
                        $this.jq.addClass('ui-confirm-popup-flipped');
                    }
                    else {
                        pos.top += parseFloat($this.jq.css('margin-top'));
                    }

                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

    /**
     * Applies focus to the first focusable element of the content in the popup.
     */
    applyFocus(): void {
        this.jq.find(':button:visible:enabled').first().trigger('focus');
    }

    /**
     * Puts focus on the element that opened this dialog.
     * @param delay how long to delay before focusing
     */
    protected returnFocus(delay?: number | undefined): void {
        const el = this.focusedElementBeforeDialogOpened;
        if (el instanceof HTMLElement || el instanceof SVGElement) {
            PrimeFaces.queueTask(() => el.focus({ preventScroll: true }), delay);
        }
    }

    /**
     * Checks whether this popup is opened and visible.
     * @returns `true` if this popup is currently being shown, `false` otherwise.
     */
    isVisible(): boolean {
        return this.jq.is(':visible');
    }

    /**
     * Shows the given message in this confirmation popup.
     * @param msg Message to show.
     */
    showMessage(msg: Partial<PrimeType.hook.confirm.ExtendedConfirmMessage>): void {
        PrimeFaces.confirmPopupSource = typeof msg.source === 'string' ? $(PrimeFaces.escapeClientId(msg.source)) : PrimeFaces.utils.toJQuery(msg.source);

        const beforeShow = () => {
            if (msg.beforeShow) {
                PrimeFaces.csp.eval(msg.beforeShow);
            }

            const iconClass = msg.icon || this.icon.data('p-icon');
            if (iconClass) {
                this.icon.removeClass().addClass('ui-confirm-popup-icon ' + iconClass);
                this.icon.show();
            } else {
                this.icon.hide();
            }

            if (msg.message) {
                if (msg.escape) {
                    this.message.text(msg.message);
                }
                else {
                    this.message.html(msg.message);
                }
            }

            // Set labels, icons, and CSS classes for yes/no buttons
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
        };

        if (this.isVisible()) {
            this.hide(() => {
                beforeShow.call(this);
                this.show(PrimeFaces.confirmPopupSource ?? undefined);
            });
        }
        else {
            beforeShow.call(this);
            this.show(PrimeFaces.confirmPopupSource);
        }
    }
}
