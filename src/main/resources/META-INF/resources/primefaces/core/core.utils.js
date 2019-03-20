if (!PrimeFaces.utils) {

    PrimeFaces.utils = {

        resolveDynamicOverlayContainer: function(widget) {
            return widget.cfg.appendTo
                ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(widget.cfg.appendTo)
                : $(document.body);
        },

        /**
         * Cleanup the "detached" overlay.
         * If you update a component, the overlay is rendered below the component markup but moved to the "appendTo" via our scripts.
         * After the AJAX update, we have now 2 overlays with the same id:
         * 1) below the root element
         * 2) the old, detached overlay, below the "appendTo"
         *
         * We now need to remove the detached overlay.
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
         * Removes the overlay from the appendTo overlay container.
         */
        removeDynamicOverlay: function(widget, overlay, overlayId, appendTo) {
            appendTo.children("[id='" + overlayId + "']").not(overlay).remove();
        },

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

        addModal: function(widget, zIndex, tabbablesCallback) {
            var id = widget.id;
            PrimeFaces.utils.preventTabbing(id, zIndex, tabbablesCallback);

            if (widget.cfg.blockScroll) {
                PrimeFaces.utils.preventScrolling();
            }

            var modalId = id + '_modal';

            var modalOverlay = $('<div id="' + modalId + '" class="ui-widget-overlay ui-dialog-mask"></div>');
            modalOverlay.appendTo($(document.body));
            modalOverlay.css('z-index' , zIndex);

            return modalOverlay;
        },

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
                            focusingRadioItem = tabbables.filter('[name="' + first.attr('name') + '"]').filter(':checked');
                            if(focusingRadioItem.length > 0) {
                                first = focusingRadioItem;
                            }
                        }

                        if(last.is(':radio')) {
                            focusingRadioItem = tabbables.filter('[name="' + last.attr('name') + '"]').filter(':checked');
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

        removeModal: function(widget) {
            var id = widget.id;
            var modalId = id + '_modal';

            // if the id contains a ':'
            $(PrimeFaces.escapeClientId(modalId)).remove();

            // if the id does NOT contain a ':'
            $(document.body).children("[id='" + modalId + "']").remove();

            if (widget.cfg.blockScroll) {
                PrimeFaces.utils.enableScrolling();
            }
            PrimeFaces.utils.enableTabbing(id);
        },

        enableTabbing: function(id) {
            $(document).off('focus.' + id + ' mousedown.' + id + ' mouseup.' + id + ' keydown.' + id);
        },

        /**
         * Checks if a modal for the given id is currently displayed.
         *
         * @param {type} id the base id
         */
        isModalActive: function(id) {
            var modalId = id + '_modal';

            return $(PrimeFaces.escapeClientId(modalId)).length === 1
                || $(document.body).children("[id='" + modalId + "']").length === 1;
        },


        /**
         * Registers the document handler, to execute the hideCallback when it's clicked outside of the overlay panel.
         *
         * @param {type} widget the widget instance
         * @param {type} hideNamespace the namespace
         * @param {type} overlay the overlay $element
         * @param {type} resolveIgnoredElementsCallback the callback which resolves the elements to ignore when clicked
         * @param {type} hideCallback will be executed when clicked outside
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


        registerScrollHandler: function(widget, scrollNamespace, scrollCallback) {

            var scrollParent = widget.getJQ().scrollParent();
            if (scrollParent.is('body')) {
                scrollParent = $(window);
            }

            widget.addDestroyListener(function() {
                scrollParent.off(scrollNamespace);
            });

            scrollParent.off(scrollNamespace).on(scrollNamespace, function(e) {
                scrollCallback(e);
            });
        },

        unbindScrollHandler: function(widget, scrollNamespace) {
            var scrollParent = widget.getJQ().scrollParent();
            if (scrollParent.is('body')) {
                scrollParent = $(window);
            }

            scrollParent.off(scrollNamespace);
        },

        /**
         * Disables scrolling of the document body.
         */
        preventScrolling: function() {
            $(document.body).addClass('ui-overflow-hidden');
        },

        /**
         * Enables scrolling again if previously disabled.
         */
        enableScrolling: function() {
            $(document.body).removeClass('ui-overflow-hidden');
        },
        
        /**
         * Calculates an element offset relative to where the Window is currently scrolled.
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
        }
    };

}
