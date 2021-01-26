if (!PrimeFaces.utils) {

    /**
     * The object with various utilities needed by PrimeFaces.
     * @namespace
     */
    PrimeFaces.utils = {

        /**
         * Finds the element to which the overlay panel should be appended. If none is specified explicitly, append the
         * panel to the body.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget A widget that has a panel to be appended.
         * @return {string} The search expression for the element to which the overlay panel should be appended.
         */
        resolveAppendTo: function(widget) {
            var dialog = widget.jq[0].closest('.ui-dialog');
            var panel = widget.panel;

            if (dialog && panel && panel.length) {
                var $dialog = $(dialog);

                //set position as fixed to scroll with dialog
                if ($dialog.css('position') === 'fixed') {
                    panel.css('position', 'fixed');
                }

                //append to body if not already appended by user choice
                if(!panel.parent().is(document.body)) {
                    widget.cfg.appendTo = "@(body)"
                    return widget.cfg.appendTo;
                }
            }

            return widget.cfg.appendTo;
        },

        /**
         * Finds the container element to which an overlay widget should be appended. This is either the element
         * specified by the widget configurations's `appendTo` attribute, or the document BODY element otherwise.
         * @param {PrimeFaces.widget.DynamicOverlayWidget} widget A widget to be displayed as an overlay.
         * @return {JQuery} The container DOM element to which the overlay is to be appended. 
         */
        resolveDynamicOverlayContainer: function(widget) {
            return widget.cfg.appendTo
                ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(widget.cfg.appendTo)
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
            
            overlay.attr({
                'role': 'dialog'
                ,'aria-hidden': false
                ,'aria-modal': true
                ,'aria-live': 'polite'
            });
            
            PrimeFaces.utils.preventTabbing(id, zIndex, tabbablesCallback);

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
         * @param {string} id ID of a modal overlay widget.
         * @param {number} zIndex The z-index of the modal overlay.
         * @param {() => JQuery} tabbablesCallback A supplier function that return a list of tabbable elements. A
         * tabbable element is an element to which the user can navigate to via the tab key.
         */
        preventTabbing: function(id, zIndex, tabbablesCallback) {
            //Disable tabbing out of modal and stop events from targets outside of the overlay element
            var $document = $(document);
            $document.on('focus.' + id + ' mousedown.' + id + ' mouseup.' + id, function(event) {
                if ($(event.target).zIndex() < zIndex) {
                    event.preventDefault();
                }
            });
            $document.on('keydown.' + id, function(event) {
                var target = $(event.target);
                if (event.which === $.ui.keyCode.TAB) {
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
                else if(!target.is(document.body) && (target.zIndex() < zIndex)) {
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
                    'role': ''
                    ,'aria-hidden': true
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
            PrimeFaces.utils.enableTabbing(id);
        },

        /**
         * Enables navigating to an element via the tab key outside an overlay widget. Usually called when a modal
         * overlay is removed. This reverts the changes as made by `PrimeFaces.utils.preventTabbing`.
         * @param {string} id ID of a modal overlay, usually the widget ID.
         */
        enableTabbing: function(id) {
            $(document).off('focus.' + id + ' mousedown.' + id + ' mouseup.' + id + ' keydown.' + id);
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
         * Registers a callback on the document that is invoked when the user clicks on an element outside the overlay
         * widget.
         *
         * @param {PrimeFaces.widget.BaseWidget} widget An overlay widget instance.
         * @param {string} hideNamespace A click event with a namspace to listen to, such as `mousedown.widgetId`. 
         * @param {JQuery} overlay The DOM element for the overlay.
         * @param {((event: JQuery.TriggeredEvent) => JQuery) | undefined} resolveIgnoredElementsCallback The callback which
         * resolves the elements to ignore when the user clicks outside the overlay. The `hideCallback` is not invoked
         * when the user clicks on one those elements.
         * @param {(event: JQuery.TriggeredEvent, eventTarget: JQuery) => void} hideCallback A callback that is invoked when the
         * user clicks on an element outside the overlay widget.
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


                // this checks were moved to the used components

                // do nothing when the clicked element is a child of the overlay
                /*
                if (overlay.is($eventTarget) || overlay.has($eventTarget).length > 0) {
                    return;
                }
                */

                // OLD WAY: do nothing when the clicked element is a child of the overlay
                /*
                var offset = overlay.offset();
                if (e.pageX < offset.left
                        || e.pageX > offset.left + overlay.width()
                        || e.pageY < offset.top
                        || e.pageY > offset.top + overlay.height()) {
                    hideCallback();
                }
                */

                hideCallback(e, $eventTarget);
            });
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
        },

        /**
         * Sets up an overlay widget. Appends the overlay widget to the element as specified by the `appendTo`
         * attribute. Also makes sure the overlay widget is handled propertly during AJAX updates.
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
         */
        registerScrollHandler: function(widget, scrollNamespace, scrollCallback) {

            var scrollParent = widget.getJQ().scrollParent();
            if (scrollParent.is('body') || scrollParent.is('html') || scrollParent[0].nodeType === 9) { // nodeType 9 is for document element
                scrollParent = $(window);
            }

            widget.addDestroyListener(function() {
                scrollParent.off(scrollNamespace);
            });

            scrollParent.off(scrollNamespace).on(scrollNamespace, function(e) {
                scrollCallback(e);
            });
        },

        /**
         * Registers a callback that is invoked when a scroll event is triggered on The DOM element for the widget that
         * has a connected overlay.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which to register a scroll handler.
         * @param {string} scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
         * @param {(event: JQuery.TriggeredEvent) => void} scrollCallback A callback that is invoked when a scroll event
         * occurs on the widget.
         */
        registerConnectedOverlayScrollHandler: function(widget, scrollNamespace, scrollCallback) {
            var element = widget.getJQ().get(0);
            var scrollableParents = [];
            var getParents = function(element, parents) {
                return element['parentNode'] === null ? parents : getParents(element.parentNode, parents.concat([element.parentNode]));
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
                                scrollableParents.push(el);
                            }
                        }
                    }
    
                    if (parent.nodeType !== 9 && overflowCheck(parent)) {
                        scrollableParents.push(parent);
                    }
                }
            }

            for (var i = 0; i < scrollableParents.length; i++) {
                var scrollParent = $(scrollableParents[i]);

                widget.addDestroyListener(function() {
                    scrollParent.off(scrollNamespace);
                });
    
                scrollParent.off(scrollNamespace).on(scrollNamespace, function(e) {
                    scrollCallback(e);
                });
            }
        },

        /**
         * Removes a scroll handler as registered by `PrimeFaces.utils.registerScrollHandler`.
         * @param {PrimeFaces.widget.BaseWidget} widget A widget instance for which a scroll handler was registered.
         * @param {string} scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
         */
        unbindScrollHandler: function(widget, scrollNamespace) {
            var scrollParent = widget.getJQ().scrollParent();
            if (scrollParent.is('body') || scrollParent.is('html') || scrollParent[0].nodeType === 9) { // nodeType 9 is for document element
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
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                e.preventDefault();
            }
        },

        /**
         * Ignores certain keys on filter input text box. Useful in filter input events in many components.
         * @param {JQuery.TriggeredEvent} e The key event that occurred.
         * @return {boolean} `true` if the one of the keys to ignore was pressed, or `false` otherwise.
         */
        ignoreFilterKey: function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode,
            ignoredKeys = [
                keyCode.END, 
                keyCode.HOME, 
                keyCode.LEFT, 
                keyCode.RIGHT, 
                keyCode.UP, 
                keyCode.DOWN,
                keyCode.TAB, 
                16/*Shift*/, 
                17/*Ctrl*/, 
                18/*Alt*/, 
                91, 92, 93/*left/right Win/Cmd*/,
                keyCode.ESCAPE, 
                keyCode.PAGE_UP, 
                keyCode.PAGE_DOWN,
                19/*pause/break*/, 
                20/*caps lock*/, 
                44/*print screen*/, 
                144/*num lock*/, 
                145/*scroll lock*/];

            if (ignoredKeys.indexOf(key) > -1) {
                return true;
            }
            return false;
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
        }

    };

}
