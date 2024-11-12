/**
 * __PrimeFaces ConfirmPopup Widget__
 * 
 * ConfirmPopup displays a confirmation overlay displayed relatively to its target.
 *
 * @interface {PrimeFaces.widget.ConfirmPopup.ConfirmPopupMessage} ConfirmPopupMessage Interface for the message that
 * is shown in the confirm popup.
 * @prop {string} ConfirmPopupMessage.message Main content of the popup message.
 * @prop {boolean} ConfirmPopupMessage.escape If `true`, the message is escaped for HTML. If `false`, the message is
 * interpreted as an HTML string.
 * @prop {string} ConfirmPopupMessage.onShow A JavaScript code snippet that is be evaluated before the message is
 * shown.
 * 
 * @prop {JQuery} content The DOM element for the content of the confirm popup.
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {JQuery} icon The DOM element for the message icon.
 * @prop {JQuery} message DOM element of the confirmation message displayed in this confirm popup.
 * @prop {JQuery} yesButton DOM element of the Yes button.
 * @prop {JQuery} noButton DOM element of the No button.
 * @prop {HTMLElement} focusedElementBeforeDialogOpened Element that was focused before the dialog was opened.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 *
 * @interface {PrimeFaces.widget.ConfirmPopupCfg} cfg The configuration for the {@link  ConfirmPopup| ConfirmPopup widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg
 *
 * @typedef PrimeFaces.widget.ConfirmPopup.HideCallback Callback invoked after the popup is hidden.
 * @this {Window} PrimeFaces.widget.ConfirmPopup.HideCallback
 *
 * @prop {string | null} cfg.appendTo The search expression for the element to which the overlay panel should be
 * appended.
 * @prop {boolean} cfg.dismissable When set `true`, clicking outside of the popup hides the overlay.
 * @prop {string} cfg.showEvent Event on target to show the popup.
 * @prop {string} cfg.hideEvent Event on target to hide the popup.
 * @prop {boolean} cfg.global When enabled, confirmPopup becomes a shared for other components that require confirmation.
 */
PrimeFaces.widget.ConfirmPopup = class ConfirmPopup extends PrimeFaces.widget.DynamicOverlayWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
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
            this.yesButton = this.jq.find('.ui-confirm-popup-yes');
            this.noButton = this.jq.find('.ui-confirm-popup-no');
            this.yesButton.data('p-text', this.yesButton.children('.ui-button-text').text());
            this.noButton.data('p-text', this.noButton.children('.ui-button-text').text());
            this.yesButton.data('p-icon', this.yesButton.children('.ui-icon').attr('class'));
            this.noButton.data('p-icon', this.noButton.children('.ui-icon').attr('class'));
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');

        this.bindEvents();
    }

    /**
     * Sets up all event listeners required by this widget.
     * @protected
     */
    bindEvents() {
        if (this.cfg.global) {
            PrimeFaces.confirmPopup = this;

            this.jq.on('click.ui-confirmpopup', '.ui-confirm-popup-yes, .ui-confirm-popup-no', null, function(e) {
                var el = $(this);

                if (el.hasClass('ui-confirm-popup-yes') && PrimeFaces.confirmPopupSource) {
                    var source = PrimeFaces.confirmPopupSource;
                    var id = source.get(0);
                    var js = source.data('pfconfirmcommand');
                    var command = $(id);

                    // Test if the function matches the pattern
                    if (PrimeFaces.ajax.Utils.isAjaxRequest(js) || command.is('a')) {
                        // command is ajax=true
                        var originalOnClick;

                        if (source[0]) {
                            var events = $._data(source[0], "events");
                            originalOnClick = source.prop('onclick') || (events && events.click ? events.click[0].handler : null);
                        }

                        // Temporarily remove the click handler and execute the new one
                        source.prop('onclick', null).off("click").on("click", function(event) {
                            PrimeFaces.csp.executeEvent(id, js, event);
                        }).click();

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
                        command.removeAttr("data-pfconfirmcommand").click();
                    }

                    PrimeFaces.confirmPopup.hide();
                    PrimeFaces.confirmPopupSource = null;
                }
                else if (el.hasClass('ui-confirm-popup-no')) {
                    PrimeFaces.confirmPopup.hide();
                    PrimeFaces.confirmPopupSource = null;
                }

                e.preventDefault();
            });
        }
    }

    /**
     * Sets up all panel event listeners
     * @param {string | JQuery} [target] Selector or DOM element of the target component that triggers this popup.
     * @private
     */
    bindPanelEvents(target) {
        var $this = this;

        //hide overlay when mousedown is at outside of overlay
        if (this.cfg.dismissable) {
            this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.jq,
                function() { return PrimeFaces.confirmPopupSource; },
                function(e, eventTarget) {
                    if (e && !($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0)) {
                        $this.hide();
                    }
                });
        }

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', target, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });
    }

    /**
     * Unbind all panel event listeners
     * @private
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
     * Makes the popup visible.
     * @param {string | JQuery} [target] Selector or DOM element of the target component that triggers this popup.
     */
    show(target) {
        // Remember the focused element before we opened the dialog
        // so we can return focus to it once we close the dialog.
        this.focusedElementBeforeDialogOpened = document.activeElement;

        if (this.transition) {
            var $this = this;

            if (typeof target === 'string') {
                target = $(document.querySelector(target));
            }
            else if (!(target instanceof $)) {
                target = $(target);
            }

            this.transition.show({
                onEnter: function() {
                    $this.jq.css('z-index', PrimeFaces.nextZindex());
                    $this.align(target);
                },
                onEntered: function() {
                    $this.bindPanelEvents(target);
                    $this.applyFocus();
                }
            });
        }
    }

    /**
     * Hides the popup.
     * @param {PrimeFaces.widget.ConfirmPopup.HideCallback} callback Callback that is invoked after this popup was closed.
     */
    hide(callback) {
        var $this = this;

        if (this.transition) {
            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    if (callback) {
                        callback();
                    }
                    $this.returnFocus(50);

                    // Remove added classes and reset button labels to their original values
                    if (!PrimeFaces.animationEnabled || !$this.isVisible()) {
                        $this.restoreButtons();
                    }
                }
            });
        }
        else {
            $this.returnFocus();
            $this.restoreButtons();
        }
    }

    /**
     * Restore the button text and styling to its original form.
     * @private
     */
    restoreButtons() {
        var $this = this;
        if ($this.cfg.global) {
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
     * @param {JQuery} [target] Jquery selector that is the target of this popup
     * @private
     */
    align(target) {
        if (target) {
            var $this = this;

            this.jq.removeClass('ui-confirm-popup-flipped');

            this.jq.css({ left: '0px', top: '0px', 'transform-origin': 'center top' }).position({
                my: 'left top'
                , at: 'left bottom'
                , of: target
                , collision: 'flipfit'
                , using: function(pos, directions) {
                    var targetOffset = target.offset();
                    var arrowLeft = 0;

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
    applyFocus() {
        this.jq.find(':button:visible:enabled').first().trigger('focus');
    }

    /**
     * Puts focus on the element that opened this dialog.
     * @param {number | undefined} [delay] how long to delay before focusing
     * @protected
     */
    returnFocus(delay) {
        var el = this.focusedElementBeforeDialogOpened;
        if (!el) {
            return;
        }

        PrimeFaces.queueTask(function() { el.focus({ preventScroll: true }) }, delay);
    }

    /**
     * Checks whether this popup is opened and visible.
     * @return {boolean} `true` if this popup is currently being shown, `false` otherwise.
     */
    isVisible() {
        return this.jq.is(':visible');
    }

    /**
     * Shows the given message in this confirmation popup.
     * @param {Partial<PrimeFaces.widget.ConfirmPopup.ConfirmPopupMessage>} msg Message to show.
     */
    showMessage(msg) {
        PrimeFaces.confirmPopupSource = (typeof (msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);

        var $this = this;
        var beforeShow = function() {
            if (msg.beforeShow) {
                PrimeFaces.csp.eval(msg.beforeShow);
            }

            $this.icon.removeClass().addClass('ui-confirm-popup-icon');
            if (msg.icon !== 'null') {
                $this.icon.addClass(msg.icon);
            }

            if (msg.message) {
                if (msg.escape) {
                    $this.message.text(msg.message);
                }
                else {
                    $this.message.html(msg.message);
                }
            }

            // Set labels, icons, and CSS classes for yes/no buttons
            if (msg.yesButtonLabel) {
                $this.yesButton.children('.ui-button-text').text(msg.yesButtonLabel);
            }
            if (msg.yesButtonClass) {
                $this.yesButton.addClass(msg.yesButtonClass).data('p-class', msg.yesButtonClass);
            }
            if (msg.yesButtonIcon) {
                PrimeFaces.utils.replaceIcon($this.yesButton.children('.ui-icon'), msg.yesButtonIcon);
            }
            if (msg.noButtonLabel) {
                $this.noButton.children('.ui-button-text').text(msg.noButtonLabel);
            }
            if (msg.noButtonClass) {
                $this.noButton.addClass(msg.noButtonClass).data('p-class', msg.noButtonClass);
            }
            if (msg.noButtonIcon) {
                PrimeFaces.utils.replaceIcon($this.noButton.children('.ui-icon'), msg.noButtonIcon);
            }
        };

        if (this.isVisible()) {
            this.hide(function() {
                beforeShow.call($this);
                $this.show(PrimeFaces.confirmPopupSource);
            });
        }
        else {
            beforeShow.call(this);
            this.show(PrimeFaces.confirmPopupSource);
        }
    }
}
