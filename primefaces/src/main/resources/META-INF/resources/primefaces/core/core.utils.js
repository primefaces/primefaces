if (!PrimeFaces.utils) {
    
   /**
    * Shortcut for is this CMD on MacOs or CTRL key on other OSes. 
    * @param {JQuery.TriggeredEvent} e The key event that occurred.
    * @return {boolean} `true` if the key is a meta key, or `false` otherwise.
    */
    PF.metaKey = function(e) {
        return PrimeFaces.utils.isMetaKey(e);
    };

    /**
     * The object with various utilities needed by PrimeFaces.
     * @namespace
     */
    PrimeFaces.utils = {

        /**
         * Finds the element to which the overlay panel should be appended. If none is specified explicitly, append the
         * panel to the body.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget A widget that has a panel to be appended.
         * @param {JQuery} target The DOM element that is the target of this overlay
         * @param {JQuery} overlay The DOM element for the overlay.
         * @return {string | null} The search expression for the element to which the overlay panel should be appended.
         */
        resolveAppendTo: function(widget, target, overlay) {
            if (widget && target && target[0]) {
                var dialog = target[0].closest('.ui-dialog');

                if (dialog && overlay && overlay.length) {
                    var $dialog = $(dialog);

                    //set position as fixed to scroll with dialog
                    if ($dialog.css('position') === 'fixed') {
                        overlay.css('position', 'fixed');
                    }

                    //append to body if not already appended by user choice
                    if(!widget.cfg.appendTo) {
                        widget.cfg.appendTo = "@(body)";
                        return widget.cfg.appendTo;
                    }
                }

                return widget.cfg.appendTo;
            }

            return null;
        },

        /**
         * Finds the container element to which an overlay widget should be appended. This is either the element
         * specified by the widget configurations's `appendTo` attribute, or the document BODY element otherwise.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget A widget to be displayed as an overlay.
         * @return {JQuery} The container DOM element to which the overlay is to be appended.
         */
        resolveDynamicOverlayContainer: function(widget) {
            return widget.cfg.appendTo
                ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(widget.jq, widget.cfg.appendTo)
                : $(document.body);
        },

        /**
         * Cleanup the `detached` overlay.
         *
         * If you update a component, the overlay is rendered as specified in the component tree (XHTML view), but moved
         * to a different container via JavaScript.
         *
         * This means that after an AJAX update, we now have 2 overlays with the same id:
         *
         * 1. The newly rendered overlay, as a child of the element specified by the component tree (XHTML view)
         * 1. The old, detached overlay, as a child of the element specified by `appendTo` attribute
         *
         * We now need to remove the detached overlay. This is done by this function.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget The (old) overlay widget instance.
         * @param {JQuery} overlay The DOM element for the overlay.
         * @param {string} overlayId ID of the overlay, usually the widget ID.
         * @param {JQuery} appendTo The container to which the overlay is appended.
         */
        cleanupDynamicOverlay: function(widget, overlay, overlayId, appendTo) {
            if (widget.cfg.appendTo) {
                var overlays = $("[id='" + overlayId + "']");
                if (overlays.length > 1) {
                    appendTo.children("[id='" + overlayId + "']").remove();
                }
            }
        },

        /**
         * Removes the overlay from the overlay container as specified by the `appendTo` attribute.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget The overlay widget instance.
         * @param {JQuery} overlay The (new) DOM element of the overlay.
         * @param {string} overlayId ID of the the overlay, usually the widget ID.
         * @param {JQuery} appendTo The container to which the overlay is appended.
         */
        removeDynamicOverlay: function(widget, overlay, overlayId, appendTo) {
            appendTo.children("[id='" +  overlayId + "']").not(overlay).remove();
        },

        /**
         * An overlay widget is moved in the DOM to the position as specified by the `appendTo` attribute. This function
         * moves the widget to its position in the DOM and removes old elements from previous AJAX updates.
         * @param {PrimeFaces.widget.BaseWidget} widget The overlay widget instance.
         * @param {JQuery} overlay The DOM element for the overlay.
         * @param {string} overlayId ID of the overlay, usually the widget ID.
         * @param {JQuery} appendTo The container to which the overlay is appended.
         */
        appendDynamicOverlay: function(widget, overlay, overlayId, appendTo) {
            var elementParent = overlay.parent();

            // skip when the parent currently is already the same
            // this likely happens when the dialog is updated directly instead of a container
            // as our ajax update mechanism just updates by id
            if (!elementParent.is(appendTo)
                    && !appendTo.is(overlay)) {

                PrimeFaces.utils.removeDynamicOverlay(widget, overlay, overlayId, appendTo);

                overlay.appendTo(appendTo);
            }
        },

        /**
         * Creates a new (empty) container for a modal overlay. A modal overlay is an overlay that blocks the content
         * below it. To remove the modal overlay, use `PrimeFaces.utils.removeModal`.
         * @param {PrimeFaces.widget.BaseWidget} widget An overlay widget instance.
         * @param {JQuery} overlay The modal overlay element should be a DIV.
         * @param {() => JQuery} tabbablesCallback A supplier function that return a list of tabbable elements. A
         * tabbable element is an element to which the user can navigate to via the tab key.
         * @return {JQuery} The DOM element for the newly added modal overlay container.
         */
        addModal: function(widget, overlay, tabbablesCallback) {
            var id = widget.id,
                zIndex = overlay.css('z-index') - 1;

            var role = widget instanceof PrimeFaces.widget.ConfirmDialog ? 'alertdialog' : 'dialog';
            overlay.attr({
                'role': role
                ,'aria-hidden': false
                ,'aria-modal': true
                ,'aria-live': 'polite'
            });

            PrimeFaces.utils.preventTabbing(widget, id, zIndex, tabbablesCallback);

            if (widget.cfg.blockScroll) {
                PrimeFaces.utils.preventScrolling();
            }

            var modalId = id + '_modal';
            var modalOverlay = $('<div id="' + modalId + '" class="ui-widget-overlay ui-dialog-mask"></div>');
            modalOverlay.appendTo($(document.body));
            modalOverlay.css('z-index' , String(zIndex));

            return modalOverlay;
        },

        /**
         * Given a modal overlay, prevents navigating via the tab key to elements outside of that modal overlay. Use
         * `PrimeFaces.utils.enableTabbing` to restore the original behavior.
         * @param {PrimeFaces.widget.BaseWidget} widget An overlay widget instance.
         * @param {string} id ID of a modal overlay widget.
         * @param {number} zIndex The z-index of the modal overlay.
         * @param {() => JQuery} tabbablesCallback A supplier function that return a list of tabbable elements. A
         * tabbable element is an element to which the user can navigate to via the tab key.
         */
        preventTabbing: function(widget, id, zIndex, tabbablesCallback) {
            //Disable tabbing out of modal and stop events from targets outside of the overlay element
            var $documentInIframe = widget.cfg && widget.cfg.iframe ? widget.cfg.iframe.get(0).contentWindow.document : undefined;
            var $document = $($documentInIframe ? [document, $documentInIframe] : document);
            $document.on('focus.' + id + ' mousedown.' + id + ' mouseup.' + id, function(event) {
                var target = $(event.target);
                if (!target.is(document.body) && (!$documentInIframe && target.zIndex() < zIndex && target.parent().zIndex() < zIndex)) {
                    event.preventDefault();
                }
            });
            $document.on('keydown.' + id, function(event) {
                var target = $(event.target);
                if (event.key === 'Tab') {
                    var tabbables = tabbablesCallback();
                    if (tabbables.length) {
                        var first = tabbables.filter(':first'),
                        last = tabbables.filter(':last'),
                        focusingRadioItem = null;

                        if(first.is(':radio')) {
                            focusingRadioItem = tabbables.filter('[name="' + $.escapeSelector(first.attr('name')) + '"]').filter(':checked');
                            if(focusingRadioItem.length > 0) {
                                first = focusingRadioItem;
                            }
                        }

                        if(last.is(':radio')) {
                            focusingRadioItem = tabbables.filter('[name="' + $.escapeSelector(last.attr('name')) + '"]').filter(':checked');
                            if(focusingRadioItem.length > 0) {
                                last = focusingRadioItem;
                            }
                        }

                        if(target.is(document.body)) {
                            first.focus(1);
                            event.preventDefault();
                        }
                        else if(event.target === last[0] && !event.shiftKey) {
                            first.focus(1);
                            event.preventDefault();
                        }
                        else if (event.target === first[0] && event.shiftKey) {
                            last.focus(1);
                            event.preventDefault();
                        }
                    }
                }
                else if (event.ctrlKey) { 
                    // #8965 allow cut, copy, paste
                    return;
                }
                else if (!target.is(document.body) && (!$documentInIframe && target.zIndex() < zIndex && target.parent().zIndex() < zIndex)) {
                    event.preventDefault();
                }
            });
        },

        /**
         * Given a modal overlay widget, removes the modal overlay element from the DOM. This reverts the changes as
         * made by `PrimeFaces.utils.addModal`.
         * @param {PrimeFaces.widget.BaseWidget} widget A modal overlay widget instance.
         * @param {JQuery | null} [overlay] The modal overlay element should be a DIV.
         */
        removeModal: function(widget, overlay) {
            var id = widget.id;
            var modalId = id + '_modal';

            if (overlay) {
                overlay.attr({
                    'aria-hidden': true
                    ,'aria-modal': false
                    ,'aria-live': 'off'
                });
            }

            // if the id contains a ':'
            $(PrimeFaces.escapeClientId(modalId)).remove();

            // if the id does NOT contain a ':'
            $(document.body).children("[id='" + modalId + "']").remove();

            if (widget.cfg.blockScroll) {
                PrimeFaces.utils.enableScrolling();
            }
            PrimeFaces.utils.enableTabbing(widget, id);
        },

        /**
         * Enables navigating to an element via the tab key outside an overlay widget. Usually called when a modal
         * overlay is removed. This reverts the changes as made by `PrimeFaces.utils.preventTabbing`.
         * @param {PrimeFaces.widget.BaseWidget} widget A modal overlay widget instance.
         * @param {string} id ID of a modal overlay, usually the widget ID.
         */
        enableTabbing: function(widget, id) {
            var $documentInIframe = widget.cfg && widget.cfg.iframe ? widget.cfg.iframe.get(0).contentWindow.document : undefined;
            var $document = $($documentInIframe ? [document, $documentInIframe] : document);

            $document.off('focus.' + id + ' mousedown.' + id + ' mouseup.' + id + ' keydown.' + id);
        },

        /**
         * Checks if a modal with the given ID is currently displayed.
         * @param {string} id The base ID of a modal overlay, usually the widget ID.
         * @return {boolean} Whether the modal with the given ID is displayed.
         */
        isModalActive: function(id) {
            var modalId = id + '_modal';

            return $(PrimeFaces.escapeClientId(modalId)).length === 1
                || $(document.body).children("[id='" + modalId + "']").length === 1;
        },

        /**
         * Is this scrollable parent a type that should be bound to the window element.
         *
         * @param {JQuery | undefined | null} jq An element to check if should be bound to window scroll. 
         * @return {boolean} true this this JQ should be bound to the window scroll event
         */
        isScrollParentWindow: function(jq) {
            return jq && (jq.is('body') || jq.is('html') || jq[0].nodeType === 9); // nodeType 9 is for document element;
        },

        /**
         * Registers a callback on the document that is invoked when the user clicks on an element outside the overlay
         * widget.
         *
         * @param {PrimeFaces.widget.BaseWidget} widget An overlay widget instance.
         * @param {string} hideNamespace A click event with a namespace to listen to, such as `mousedown.widgetId`.
         * @param {JQuery} overlay The DOM element for the overlay.
         * @param {((event: JQuery.TriggeredEvent) => JQuery) | undefined} resolveIgnoredElementsCallback The callback which
         * resolves the elements to ignore when the user clicks outside the overlay. The `hideCallback` is not invoked
         * when the user clicks on one those elements.
         * @param {(event: JQuery.TriggeredEvent, eventTarget: JQuery) => void} hideCallback A callback that is invoked when the
         * user clicks on an element outside the overlay widget.
         * @return {PrimeFaces.UnbindCallback} Unbind callback handler
         */
        registerHideOverlayHandler: function(widget, hideNamespace, overlay, resolveIgnoredElementsCallback, hideCallback) {

            widget.addDestroyListener(function() {
                $(document).off(hideNamespace);
            });

            $(document).off(hideNamespace).on(hideNamespace, function(e) {
                if (overlay.is(':hidden') || overlay.css('visibility') === 'hidden') {
                    return;
                }

                var $eventTarget = $(e.target);

                // do nothing when the element should be ignored
                if (resolveIgnoredElementsCallback) {
                    var elementsToIgnore = resolveIgnoredElementsCallback(e);
                    if (elementsToIgnore) {
                        if (elementsToIgnore.is($eventTarget) || elementsToIgnore.has($eventTarget).length > 0) {
                            return;
                        }
                    }
                }

                if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                    hideCallback(e, $eventTarget);
                }
            });

            return {
                unbind: function() {
                    $(document).off(hideNamespace);
                }
            };
        },

        /**
         * Registers a callback that is invoked when the window is resized.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which to register a resize handler.
         * @param {string} resizeNamespace A resize event with a namespace to listen to, such as `resize.widgetId`.
         * @param {JQuery | undefined} element An element that prevents the callback from being invoked when it is not
         * visible, usually a child element of the widget.
         * @param {(event: JQuery.TriggeredEvent) => void} resizeCallback A callback that is invoked when the window is resized.
         * @param {string} [params] Optional CSS selector. If given, the callback is invoked only when the resize event
         * is triggered on an element the given selector.
         * @return {PrimeFaces.UnbindCallback} Unbind callback handler
         */
        registerResizeHandler: function(widget, resizeNamespace, element, resizeCallback, params) {

            widget.addDestroyListener(function() {
                $(window).off(resizeNamespace);
            });

            $(window).off(resizeNamespace).on(resizeNamespace, params||null, function(e) {
                if (element && (element.is(":hidden") || element.css('visibility') === 'hidden')) {
                    return;
                }

                resizeCallback(e);
            });

            return {
                unbind: function() {
                    $(window).off(resizeNamespace);
                }
            };
        },

        /**
         * Sets up an overlay widget. Appends the overlay widget to the element as specified by the `appendTo`
         * attribute. Also makes sure the overlay widget is handled properly during AJAX updates.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget An overlay widget instance.
         * @param {JQuery} overlay The DOM element for the overlay.
         * @param {string} overlayId The ID of the overlay, usually the widget ID.
         * @return {JQuery} The overlay that was passed to this function.
         */
        registerDynamicOverlay: function(widget, overlay, overlayId) {

            if (widget.cfg.appendTo) {
                var appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(widget);
                PrimeFaces.utils.appendDynamicOverlay(widget, overlay, overlayId, appendTo);

                widget.addDestroyListener(function() {
                    var appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(widget);
                    // pass null as overlay - as every! overlay with this overlayId can be removed on destroying the whole widget
                    PrimeFaces.utils.removeDynamicOverlay(widget, null, overlayId, appendTo);
                });

                widget.addRefreshListener(function() {
                    var appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(widget);
                    PrimeFaces.utils.cleanupDynamicOverlay(widget, overlay, overlayId, appendTo);
                });
            }

            return overlay;
        },

        /**
         * Registers a callback that is invoked when a scroll event is triggered on the DOM element for the widget.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which to register a scroll handler.
         * @param {string} scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
         * @param {(event: JQuery.TriggeredEvent) => void} scrollCallback A callback that is invoked when a scroll event
         * occurs on the widget.
         * @return {PrimeFaces.UnbindCallback} unbind callback handler
         */
        registerScrollHandler: function(widget, scrollNamespace, scrollCallback) {
            var scrollParent;
            var widgetJq = widget.getJQ();
            if (widgetJq && typeof widgetJq.scrollParent === 'function') {
                scrollParent = widgetJq.scrollParent();
            }
            if (!scrollParent || PrimeFaces.utils.isScrollParentWindow(scrollParent)) {
                scrollParent = $(window);
            }

            widget.addDestroyListener(function() {
                scrollParent.off(scrollNamespace);
            });

            scrollParent.off(scrollNamespace).on(scrollNamespace, function(e) {
                scrollCallback(e);
            });

            return {
                unbind: function() {
                    scrollParent.off(scrollNamespace);
                }
            };
        },

        /**
         * Registers a callback that is invoked when a scroll event is triggered on The DOM element for the widget that
         * has a connected overlay.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which to register a scroll handler.
         * @param {string} scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
         * @param {JQuery | undefined} element A DOM element used to find scrollable parents.
         * @param {(event: JQuery.TriggeredEvent) => void} scrollCallback A callback that is invoked when a scroll event
         * occurs on the widget.
         * @return {PrimeFaces.UnbindCallback} unbind callback handler
         */
        registerConnectedOverlayScrollHandler: function(widget, scrollNamespace, element, scrollCallback) {
            var scrollableParents = PrimeFaces.utils.getScrollableParents((element || widget.getJQ()).get(0));

            for (var i = 0; i < scrollableParents.length; i++) {
                var scrollParent = $(scrollableParents[i]);

                widget.addDestroyListener(function() {
                    scrollParent.off(scrollNamespace);
                });

                scrollParent.off(scrollNamespace).on(scrollNamespace, function(e) {
                    scrollCallback(e);
                });
            }

            return {
                unbind: function() {
                    for (var i = 0; i < scrollableParents.length; i++) {
                        $(scrollableParents[i]).off(scrollNamespace);
                    }
                }
            };
        },

        /**
         * Finds scrollable parents (not  the document).
         * @param {Element} element An element used to find its scrollable parents.
         * @return {Element[]} the list of scrollable parents.
         */
        getScrollableParents: function(element) {
            var scrollableParents = [];
            var getParents = function(element, parents) {
                return element['parentNode'] == null ? parents : getParents(element.parentNode, parents.concat([element.parentNode]));
            };

            var addScrollableParent = function(node) {
                if (PrimeFaces.utils.isScrollParentWindow($(node))) {
                    scrollableParents.push(window);
                } else {
                    scrollableParents.push(node);
                }
            };

            if (element) {
                var parents = getParents(element, []);
                var overflowRegex = /(auto|scroll)/;
                var overflowCheck = function(node) {
                    var styleDeclaration = window['getComputedStyle'](node, null);
                    return overflowRegex.test(styleDeclaration.getPropertyValue('overflow')) || overflowRegex.test(styleDeclaration.getPropertyValue('overflowX')) || overflowRegex.test(styleDeclaration.getPropertyValue('overflowY'));
                };

                for (var i = 0; i < parents.length; i++) {
                    var parent = parents[i];
                    var scrollSelectors = parent.nodeType === 1 && parent.dataset['scrollselectors'];
                    if (scrollSelectors) {
                        var selectors = scrollSelectors.split(',');
                        for (var j = 0; j < selectors.length; j++) {
                            var selector = selectors[j];
                            var el = parent.querySelector(selector);
                            if (el && overflowCheck(el)) {
                                addScrollableParent(el);
                            }
                        }
                    }

                    if (parent.nodeType !== 9 && overflowCheck(parent)) {
                        addScrollableParent(parent);
                    }
                }
            }

            // if no parents make it the window
            if (scrollableParents.length === 0) {
                scrollableParents.push(window);
            }

            return scrollableParents;
        },

        /**
         * Removes a scroll handler as registered by `PrimeFaces.utils.registerScrollHandler`.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which a scroll handler was registered.
         * @param {string} scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
         */
        unbindScrollHandler: function(widget, scrollNamespace) {
            var scrollParent = widget.getJQ().scrollParent();
            if (PrimeFaces.utils.isScrollParentWindow(scrollParent)) {
                scrollParent = $(window);
            }

            scrollParent.off(scrollNamespace);
        },

        /**
         * Prevents the user from scrolling the document BODY element. You can enable scrolling again via
         * `PrimeFaces.utils.enableScrolling`.
         */
        preventScrolling: function() {
            $(document.body).addClass('ui-overflow-hidden');
        },

        /**
         * Enables scrolling again if previously disabled via `PrimeFaces.utils.preventScrolling`.
         */
        enableScrolling: function() {
            $(document.body).removeClass('ui-overflow-hidden');
        },

        /**
         * Calculates an element offset relative to the current scroll position of the window.
         * @param {JQuery} element An element for which to calculate the scroll position.
         * @return {JQuery.Coordinates} The offset of the given element, relative to the current scroll position of the
         * window.
         */
        calculateRelativeOffset: function (element) {
            var result = {
                left : 0,
                top : 0
            };
            var offset = element.offset();
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            result.top = offset.top - scrollTop;
            result.left = offset.left - scrollLeft;
            return result;
        },

        /**
         * Blocks the enter key for an event like `keyup` or `keydown`. Useful in filter input events in many
         * components.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         */
        blockEnterKey: function(e) {
            if(e.key === 'Enter') {
                e.preventDefault();
            }
        },
        
        /**
         * Is this CMD on MacOs or CTRL key on other OSes. 
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the key is a meta key, or `false` otherwise.
         */
        isMetaKey: function(e) {
            if (e.originalEvent) {
                // original event returns the metakey value at the time the event was generated
                return PrimeFaces.env.browser.mac ? e.originalEvent.metaKey : e.originalEvent.ctrlKey;
            }
            else {
                // jQuery returns the real time value of the meta key
                return PrimeFaces.env.browser.mac ? e.metaKey  : e.ctrlKey;
            }
        },

        /**
         * Is this SPACE or ENTER key. Used throughout codebase to trigger and action.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the key is an action key, or `false` otherwise.
         */
        isActionKey: function(e) {
            return e.code === 'Space' || e.key === 'Enter';
        },

        /**
         * Checks if the key pressed is a printable key like 'a' or '4' etc.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the key is a printable key, or `false` otherwise.
         */
        isPrintableKey: function(e) {
            return e && e.key && (e.key.length === 1 || e.key === 'Unidentified');
        },
        
        /**
         * Checks if the key pressed is cut, copy, or paste.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the key is cut/copy/paste, or `false` otherwise.
         */
        isClipboardKey: function(e) {
            switch (e.key) {
                case 'a':
                case 'A':
                case 'c':
                case 'C':
                case 'x':
                case 'X':
                case 'v':
                case 'V':
                    return PrimeFaces.utils.isMetaKey(e);
                default:
                    return false;
            }
        },

        /**
         * Ignores unprintable keys on filter input text box. Useful in filter input events in many components.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the one of the keys to ignore was pressed, or `false` otherwise.
         */
        ignoreFilterKey: function(e) {
            // cut copy paste allows filter to trigger
            if (PrimeFaces.utils.isClipboardKey(e)) {
                return false;
            }
            // backspace,enter,delete trigger a filter as well as printable key like 'a'
            switch (e.code) {
                case 'Backspace':
                case 'Enter':
                case 'NumpadEnter':
                case 'Delete':
                    return false;
                default:
                    return !PrimeFaces.utils.isPrintableKey(e);
            }
        },

        /**
         * Exclude elements such as buttons, links, inputs from being touch swiped.  Users can always add
         * `class="noSwipe"` to any element to exclude it as well.
         * @return {string} A CSS selector for the elements to be excluded from being touch swiped.
         */
        excludedSwipeElements: function() {
            return ":button:enabled, :input:enabled, a, [role='combobox'], .noSwipe";
        },

        /**
         * Helper to open a new URL and if CTRL is held down open in new browser tab.
         *
         * @param {JQuery.TriggeredEvent} event The click event that occurred.
         * @param {JQuery} link The URL anchor link that was clicked.
         */
        openLink: function(event, link) {
            var href = link.attr('href');
            var win;
            if(href && href !== '#') {
                if (event.ctrlKey) {
                    win = window.open(href, '_blank');
                } else {
                    var target = link.attr('target') || '_self';
                    win = window.open(href, target);
                }
                if (win) {
                    win.focus();
                }
            }
            event.preventDefault();
        },

        /**
         * Enables a widget for editing and sets it style as enabled.
         *
         * @param {JQuery} jq a required jQuery element to enable
         * @param {JQuery | undefined | null} input an optional jQuery input to enable (will use jq if null)
         */
        enableInputWidget: function(jq, input) {
            if(!input) {
                input = jq;
            }
            if (input.is(':disabled')) {
                input.prop('disabled', false);
            }
            jq.removeClass('ui-state-disabled');
        },

        /**
         * Disables a widget from editing and sets it style as disabled.
         *
         * @param {JQuery} jq a required jQuery element to disable
         * @param {JQuery | undefined | null} input an optional jQuery input to disable (will use jq if null)
         */
        disableInputWidget: function(jq, input) {
            if(!input) {
                input = jq;
            }
            if (!input.is(':disabled')) {
                input.prop('disabled', true);
            }
            jq.addClass('ui-state-disabled');
        },

        /**
         * Enables a button.
         *
         * @param {JQuery} jq a required jQuery element to enable
         */
        enableButton: function(jq) {
            if (jq) {
                jq.removeClass('ui-state-disabled')
                  .prop( "disabled", false)
                  .removeAttr('aria-disabled');
            }
        },

        /**
         * Disables a button from being clicked.
         *
         * @param {JQuery} jq a required jQuery button to disable
         */
        disableButton: function(jq) {
            if (jq) {
                jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                  .addClass('ui-state-disabled')
                  .attr('disabled', 'disabled')
                  .attr('aria-disabled', 'true');
            }
        },

        /**
         * Enables CSS and jQuery animation.
         */
        enableAnimations: function() {
            $.fx.off = false;
            PrimeFaces.animationEnabled = true;
        },

        /**
         * Disables CSS and jQuery animation.
         */
        disableAnimations: function() {
            $.fx.off = true;
            PrimeFaces.animationEnabled = false;
        },

        /**
         * CSS Transition method for overlay panels such as SelectOneMenu/SelectCheckboxMenu/Datepicker's panel etc.
         * @param {JQuery | undefined | null} element An element for which to execute the transition.
         * @param {string | undefined | null} className Class name used for transition phases.
         * @return {PrimeFaces.CssTransitionHandler | null} Two handlers named `show` and `hide` that should be invoked
         * when the element gets shown and hidden. If the given element or className property is `undefined` or `null`,
         * this function returns `null`.
         */
        registerCSSTransition: function(element, className) {
            if (element && className != null) {
                var classNameStates = {
                   'enter': className + '-enter',
                   'enterActive': className + '-enter-active',
                   'enterDone': className + '-enter-done',
                   'exit': className + '-exit',
                   'exitActive': className + '-exit-active',
                   'exitDone': className + '-exit-done'
                };
                var callTransitionEvent = function(callbacks, key, param) {
                    if (callbacks != null && callbacks[key] != null) {
                        callbacks[key].call(param);
                    }
                };

                return {
                    show: function(callbacks) {
                        //clear exit state classes
                        element.removeClass([classNameStates.exit, classNameStates.exitActive, classNameStates.exitDone]);

                        if (element.is(':hidden')) {
                            if (PrimeFaces.animationEnabled) {
                                PrimeFaces.animationActive = true;
                                element.css('display', 'block').addClass(classNameStates.enter);
                                callTransitionEvent(callbacks, 'onEnter');

                                requestAnimationFrame(function() {
                                    PrimeFaces.queueTask(function() {
                                        element.addClass(classNameStates.enterActive);
                                    });

                                    element.one('transitionrun.css-transition-show', function(event) {
                                        callTransitionEvent(callbacks, 'onEntering', event);
                                    }).one('transitioncancel.css-transition-show', function() {
                                        element.removeClass([classNameStates.enter, classNameStates.enterActive, classNameStates.enterDone]);
                                        PrimeFaces.animationActive = false;
                                    }).one('transitionend.css-transition-show', function(event) {
                                        element.removeClass([classNameStates.enterActive, classNameStates.enter]).addClass(classNameStates.enterDone);
                                        callTransitionEvent(callbacks, 'onEntered', event);
                                        PrimeFaces.animationActive = false;
                                    });
                                });
                            }
                            else {
                                // animation globally disabled still call downstream callbacks
                                element.css('display', 'block');
                                callTransitionEvent(callbacks, 'onEnter');
                                callTransitionEvent(callbacks, 'onEntering');
                                callTransitionEvent(callbacks, 'onEntered');
                            }
                        }
                    },
                    hide: function(callbacks) {
                        //clear enter state classes
                        element.removeClass([classNameStates.enter, classNameStates.enterActive, classNameStates.enterDone]);

                        if (element.is(':visible')) {
                            if (PrimeFaces.animationEnabled) {
                                PrimeFaces.animationActive = true;
                                element.addClass(classNameStates.exit);
                                callTransitionEvent(callbacks, 'onExit');

                                PrimeFaces.queueTask(function() {
                                    element.addClass(classNameStates.exitActive);
                                });

                                element.one('transitionrun.css-transition-hide', function(event) {
                                    callTransitionEvent(callbacks, 'onExiting', event);
                                }).one('transitioncancel.css-transition-hide', function() {
                                    element.removeClass([classNameStates.exit, classNameStates.exitActive, classNameStates.exitDone]);
                                    PrimeFaces.animationActive = false;
                                }).one('transitionend.css-transition-hide', function(event) {
                                    element.css('display', 'none').removeClass([classNameStates.exitActive, classNameStates.exit]).addClass(classNameStates.exitDone);
                                    callTransitionEvent(callbacks, 'onExited', event);
                                    PrimeFaces.animationActive = false;
                                });
                            }
                            else {
                                // animation globally disabled still call downstream callbacks
                                callTransitionEvent(callbacks, 'onExit');
                                callTransitionEvent(callbacks, 'onExiting');
                                callTransitionEvent(callbacks, 'onExited');
                                element.css('display', 'none');
                            }
                        }
                    }
                };
            }

            return null;
        },

        /**
         * Count the bytes of the inputtext.
         * borrowed from the ckeditor wordcount plugin
         * @private
         * @param {string} text Text to count bytes from.
         * @return {number} the byte count
         */
        countBytes: function(text) {
            var count = 0, stringLength = text.length, i;
            text = String(text || "");
            for (i = 0; i < stringLength; i++) {
                var partCount = encodeURI(text[i]).split("%").length;
                count += partCount === 1 ? 1 : partCount - 1;
            }
            return count;
        },

        /**
         * Formats the given data size in a more human-friendly format, e.g., `1.5 MB` etc.
         * @param {number} bytes File size in bytes to format
         * @return {string} The given file size, formatted in a more human-friendly format.
         */
        formatBytes: function(bytes) {
            if (bytes === undefined)
                return '';

            if (bytes === 0)
                return 'N/A';

            var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
            var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
            if (i === 0)
                return bytes + ' ' + sizes[i];
            else
                return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
        },

        /**
         * This method concatenates the classes into a string according to the condition of the arguments and returns it.
         * @private
         * @return {string} class
         */
        styleClass: function() {
            var args = Array.prototype.slice.call(arguments);

            if (args) {
                var classes = [];

                for (var i = 0; i < args.length; i++) {
                    var className = args[i];

                    if (!className) continue;

                    var type = typeof className;

                    if (type === 'string' || type === 'number') {
                        classes.push(className);
                    }
                    else if (type === 'object') {
                        var _classes = Array.isArray(className) ? className : Object.keys(className).map(function(key) { return !!className[key] ? key : null });

                        classes = _classes.length ? classes.concat(_classes.filter(function(c) { return !!c })) : classes;
                    }
                }

                return classes.join(' ');
            }

            return undefined;
        },

        /**
         * When configuring numeric value like 'showDelay' and the user wants '0' we can't treat 0 as Falsey 
         * so we make the value 0.  Otherwise Falsey returns the default value.
         *
         * @param {number|undefined} value the original value
         * @param {number} defaultValue the required default value if value is not set
         * @return {number} the calculated value
         */
        defaultNumeric: function(value, defaultValue) {
            if (value === 0) {
                return 0;
            }
            return value || defaultValue;
        },

        /**
         * Is this component wrapped in a float label?
         *
         * @param {JQuery | undefined | null} jq An element to check if wrapped in float label. 
         * @return {boolean} true this this JQ has a float label parent
         */
        hasFloatLabel: function(jq) {
            if (!jq || !jq.parent()) {
                return false;
            }
            return jq.parent().hasClass('ui-float-label');
        },

        /**
         * Handles floating label CSS if wrapped in a floating label.
         * @private
         * @param {JQuery | undefined} element the to add the CSS classes to
         * @param {JQuery | undefined} inputs the input(s) to check if filled
         * @param {boolean | undefined} hasFloatLabel true if this is wrapped in a floating label
         */
        updateFloatLabel: function(element, inputs, hasFloatLabel) {
            if (!element || !inputs || !hasFloatLabel) {
                return;
            }

            var isEmpty = true;
            inputs.each(function() {
                var input = $(this);
                if (input.is('select')) {
                    if (input.attr('multiple')) {
                        isEmpty = input.find('option:selected').length === 0;
                    }
                    else {
                        var value = input.find('option:selected').attr('value');
                        isEmpty = value === null || value === '';
                    }
                }
                else {
                    var value = input.val();
                    isEmpty = value === null || value === '';
                }

                if (!isEmpty) {
                    return false;
                }
            });

            if (isEmpty) {
                element.removeClass('ui-inputwrapper-filled');
            }
            else {
                element.addClass('ui-inputwrapper-filled');
            }
        },

        /**
         * Decode escaped XML into regular string.
         *
         * @param {string | undefined} input the input to check if filled
         * @return {string | undefined} either the original string or escaped XML
         */
        decodeXml: function(input) {
            if (/&amp;|&quot;|&#39;|'&lt;|&gt;/.test(input)) {
                var doc = new DOMParser().parseFromString(input, "text/html");
                return doc.documentElement.textContent;
            }
            return input;
        },
        
        /**
         * Queue a microtask if delay is 0 or less and setTimeout if > 0.
         *
         * @param {() => void} fn the function to call after the delay
         * @param {number | undefined} delay the optional delay in milliseconds
         * @return {number | undefined} the id associated to the timeout or undefined if no timeout used
         */
        queueTask: function(fn, delay) {
            // if delay is 0 use microtask
            if (!delay || delay <= 0) {
                // queueMicrotask adds the function (task) into a queue and each function is executed one by one (FIFO)
                // after the current task has completed its work and when there is no other code waiting to be run 
                // before control of the execution context is returned to the browser's event loop.
                window.queueMicrotask(fn);
                return undefined;
            }
            // In the case of setTimeout, each task is executed from the event queue, after control is given to the event loop.
            return window.setTimeout(fn, delay);
        },

        /**
         * Killswitch that kills all AJAX requests, running Pollers and IdleMonitors.
         * @see {@link https://github.com/primefaces/primefaces/issues/10299|GitHub Issue 10299}
         */
        killswitch: function() {
            PrimeFaces.warn("Abort all AJAX requests!");
            PrimeFaces.ajax.Queue.abortAll();

            // stop all pollers and idle monitors
            for (item in PrimeFaces.widgets) {
                widget = PrimeFaces.widgets[item];
                if (widget instanceof PrimeFaces.widget.Poll) {
                    PrimeFaces.warn("Stopping Poll");
                    widget.stop();
                }
                if (widget instanceof PrimeFaces.widget.IdleMonitor) {
                    PrimeFaces.warn("Stopping IdleMonitor");
                    widget.pause();
                }
            }
        },
    };

    // set animation state globally
    if (PrimeFaces.env.prefersReducedMotion) {
        PrimeFaces.utils.disableAnimations();
        PrimeFaces.warn("Animations are disabled because OS has requested prefers-reduced-motion: reduce")
    }
}
