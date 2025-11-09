import { core } from "./core.js";
import { ajax } from "./core.ajax.js";
import { env } from "./core.env.js";
import { expressions } from "./core.expressions.js";
import type { BaseWidget, DynamicOverlayWidget } from "./core.widget.js";

/**
 * The class with various utilities needed by PrimeFaces.
 */
export class Utils {
    /**
     * TextEncoder instance used for string encoding operations.
     * 
     * Initialized as `null` and typically set to a {@Link TextEncoder} instance
     * when needed.
     */
    private TEXT_ENCODER: TextEncoder | null = null;

    /**
     * Finds the element to which the overlay panel should be appended. If none is specified explicitly, append the
     * panel to the body.
     * @param widget A widget that has a panel to be appended.
     * @param target The DOM element that is the target of this overlay
     * @param overlay The DOM element for the overlay.
     * @return The search expression for the element to which the overlay panel should be appended.
     */
    resolveAppendTo(widget: DynamicOverlayWidget, target: JQuery, overlay: JQuery): string | null | undefined {
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
    }

    /**
     * Finds the container element to which an overlay widget should be appended. This is either the element
     * specified by the widget configurations's `appendTo` attribute, or the document BODY element otherwise.
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget A widget to be displayed as an overlay.
     * @return The container DOM element to which the overlay is to be appended.
     */
    resolveDynamicOverlayContainer<
        Cfg extends PrimeType.widget.DynamicOverlayFeatureWidgetCfg,
        Widget extends PrimeType.widget.DynamicOverlayFeatureWidget<Cfg>
    >(widget: Widget): JQuery {
        return widget.cfg.appendTo
            ? expressions.SearchExpressionFacade.resolveComponentsAsSelector(widget.jq, widget.cfg.appendTo)
            : $(document.body);
    }

    /**
     * Rounds a number towards 0, e.g. `-3.9` => `-3` and `3.9` => `3`.
     * @param value Ro
     */
    roundTowardsZero(value: number): number {
        return parseInt(String(value), 10);
    }

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
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget The (old) overlay widget instance.
     * @param overlay The DOM element for the overlay.
     * @param overlayId ID of the overlay, usually the widget ID.
     * @param appendTo The container to which the overlay is appended.
     */
    cleanupDynamicOverlay<
        Cfg extends PrimeType.widget.DynamicOverlayFeatureWidgetCfg,
        Widget extends PrimeType.widget.DynamicOverlayFeatureWidget<Cfg>
    >(widget: Widget, overlay: JQuery, overlayId: string, appendTo: JQuery): void {
        if (widget.cfg.appendTo) {
            var overlays = $("[id='" + overlayId + "']");
            if (overlays.length > 1) {
                appendTo.children("[id='" + overlayId + "']").remove();
            }
        }
    }

    /**
     * Removes the overlay from the overlay container as specified by the `appendTo` attribute.
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget The overlay widget instance.
     * @param overlay The (new) DOM element of the overlay. These will not be
     * removed. Pass null to remove all overlays with the matching ID.
     * @param overlayId ID of the the overlay, usually the widget ID.
     * @param appendTo The container to which the overlay is appended.
     */
    removeDynamicOverlay<
        Cfg extends PrimeType.widget.DynamicOverlayFeatureWidgetCfg,
        Widget extends PrimeType.widget.DynamicOverlayFeatureWidget<Cfg>
    >(widget: Widget, overlay: JQuery | null, overlayId: string, appendTo: JQuery): void {
        appendTo.children("[id='" +  overlayId + "']").not(overlay ?? (() => false)).remove();
    }

    /**
     * An overlay widget is moved in the DOM to the position as specified by the `appendTo` attribute. This function
     * moves the widget to its position in the DOM and removes old elements from previous AJAX updates.
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget The overlay widget instance.
     * @param overlay The DOM element for the overlay.
     * @param overlayId ID of the overlay, usually the widget ID.
     * @param appendTo The container to which the overlay is appended.
     */
    appendDynamicOverlay<
        Cfg extends PrimeType.widget.DynamicOverlayFeatureWidgetCfg,
        Widget extends PrimeType.widget.DynamicOverlayFeatureWidget<Cfg>
    >(widget: Widget, overlay: JQuery, overlayId: string, appendTo: JQuery): void {
        var elementParent = overlay.parent();

        // skip when the parent currently is already the same
        // this likely happens when the dialog is updated directly instead of a container
        // as our ajax update mechanism just updates by id
        if (!elementParent.is(appendTo)
                && !appendTo.is(overlay)) {

            this.removeDynamicOverlay(widget, overlay, overlayId, appendTo);

            overlay.appendTo(appendTo);
        }
    }

    /**
     * Creates a new (empty) container for a modal overlay. A modal overlay is an overlay that blocks the content
     * below it. To remove the modal overlay, use {@link removeModal}.
     * @param widget An overlay widget instance.
     * @param overlay The modal overlay element should be a DIV.
     * @param tabbablesCallback A supplier function that return a list of tabbable elements. A
     * tabbable element is an element to which the user can navigate to via the tab key.
     * @return The DOM element for the newly added modal overlay container.
     */
    addModal(widget: BaseWidget, overlay: JQuery, tabbablesCallback: () => JQuery): JQuery {
        const id = widget.getId();
        const zIndex = parseInt(overlay.css('z-index')) - 1;

        const role = core.isWidgetOfTypeName(widget, "ConfirmDialog") ? 'alertdialog' : 'dialog';
        overlay.attr({
            'role': role,
            'aria-hidden': false,
            'aria-modal': true,
            'aria-live': 'polite',
        });

        this.preventTabbing(widget, id, zIndex, tabbablesCallback);

        if (widget.cfg.blockScroll) {
            this.preventScrolling();
        }

        var modalId = id + '_modal';
        var modalOverlay = $('<div id="' + modalId + '" class="ui-widget-overlay ui-dialog-mask"></div>');
        modalOverlay.appendTo($(document.body));
        modalOverlay.css('z-index' , String(zIndex));

        return modalOverlay;
    }

    /**
     * Given a modal overlay, prevents navigating via the tab key to elements outside of that modal overlay. Use
     * {@link enableTabbing} to restore the original behavior.
     * @param widget An overlay widget instance.
     * @param id ID of a modal overlay widget.
     * @param zIndex The z-index of the modal overlay.
     * @param tabbablesCallback A supplier function that return a list of tabbable elements. A
     * tabbable element is an element to which the user can navigate to via the tab key.
     */
    preventTabbing(widget: BaseWidget | undefined, id: string, zIndex: number, tabbablesCallback: () => JQuery): void {
        //Disable tabbing out of modal and stop events from targets outside of the overlay element
        var $documentInIframe = widget && widget.cfg && widget.cfg.iframe ? widget.cfg.iframe.get(0)?.contentWindow?.document : undefined;
        var $document = $($documentInIframe ? [document, $documentInIframe] : document);
        $document.on('focus.' + id + ' mousedown.' + id + ' mouseup.' + id, (event) => {
            var target = $(event.target);
            if (!target.is(document.body) && (!$documentInIframe && target.zIndex() < zIndex && target.parent().zIndex() < zIndex)) {
                event.preventDefault();
            }
        });
        $document.on('keydown.' + id, (event) => {
            var target = $(event.target);
            if (event.key === 'Tab') {
                var tabbables = tabbablesCallback();
                if (tabbables.length) {
                    var first = tabbables.filter(':first'),
                    last = tabbables.filter(':last'),
                    focusingRadioItem = null;

                    if(first.is(':radio')) {
                        focusingRadioItem = tabbables.filter('[name="' + CSS.escape(first.attr('name') ?? "") + '"]').filter(':checked');
                        if(focusingRadioItem.length > 0) {
                            first = focusingRadioItem;
                        }
                    }

                    if(last.is(':radio')) {
                        focusingRadioItem = tabbables.filter('[name="' + CSS.escape(last.attr('name') ?? "") + '"]').filter(':checked');
                        if(focusingRadioItem.length > 0) {
                            last = focusingRadioItem;
                        }
                    }

                    const focusElement = (element: JQuery) => {
                        element.focus(1);
                        event.preventDefault();
                    };

                    if (target.is(document.body) || ($(event.target).is(last) || (event.target instanceof Element && last.has(event.target).length > 0)) && !event.shiftKey) {
                        focusElement(first);
                    } else if (($(event.target).is(first) || (event.target instanceof Element && first.has(event.target).length > 0)) && event.shiftKey) {
                        focusElement(last);
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
    }

    /**
     * Given a modal overlay widget, removes the modal overlay element from the DOM. This reverts the changes as
     * made by {@link addModal}.
     * @param widget A modal overlay widget instance.
     * @param overlay The modal overlay element should be a DIV.
     */
    removeModal(widget: BaseWidget, overlay?: JQuery | null): void {
        const id = widget.getId();
        const modalId = id + '_modal';

        if (overlay) {
            overlay.attr({
                'aria-hidden': true
                ,'aria-modal': false
                ,'aria-live': 'off'
            });
        }

        // if the id contains a ':'
        $(core.escapeClientId(modalId)).remove();

        // if the id does NOT contain a ':'
        $(document.body).children("[id='" + modalId + "']").remove();

        if (widget.cfg.blockScroll) {
            this.enableScrolling();
        }

        this.enableTabbing(widget, id);
    }

    /**
     * Enables navigating to an element via the tab key outside an overlay widget. Usually called when a modal
     * overlay is removed. This reverts the changes as made by {@link preventTabbing}.
     * @param widget A modal overlay widget instance.
     * @param id ID of a modal overlay, usually the widget ID.
     */
    enableTabbing(widget: BaseWidget | undefined, id: string): void {
        var $documentInIframe = widget && widget.cfg && widget.cfg.iframe ? widget.cfg.iframe.get(0)?.contentWindow?.document : undefined;
        var $document = $($documentInIframe ? [document, $documentInIframe] : document);

        $document.off('focus.' + id + ' mousedown.' + id + ' mouseup.' + id + ' keydown.' + id);
    }

    /**
     * Checks if a modal with the given ID is currently displayed.
     * @param id The base ID of a modal overlay, usually the widget ID.
     * @return Whether the modal with the given ID is displayed.
     */
    isModalActive(id: string): boolean {
        var modalId = id + '_modal';

        return $(core.escapeClientId(modalId)).length === 1
            || $(document.body).children("[id='" + modalId + "']").length === 1;
    }

    /**
     * Is this scrollable parent a type that should be bound to the window element.
     *
     * @param jq An element to check if should be bound to window scroll. 
     * @return true this this JQ should be bound to the window scroll event
     */
    isScrollParentWindow(jq: JQuery | undefined | null): boolean {
        // nodeType 9 is for document element;
        return jq !== undefined && jq !== null && (jq.is('body') || jq.is('html') || jq[0]?.nodeType === 9);
    }

    /**
     * Registers a callback on the document that is invoked when the user clicks on an element outside the overlay
     * widget.
     *
     * @param widget An overlay widget instance.
     * @param hideNamespace A click event with a namespace to listen to, such as `mousedown.widgetId`.
     * @param overlay The DOM element for the overlay.
     * @param resolveIgnoredElementsCallback The callback which
     * resolves the elements to ignore when the user clicks outside the overlay. The `hideCallback` is not invoked
     * when the user clicks on one those elements.
     * @param hideCallback A callback that is invoked when the user clicks on an element outside the overlay widget.
     * @returns Object that can be used to remove the registered handler.
     */
    registerHideOverlayHandler(
        widget: BaseWidget,
        hideNamespace: string,
        overlay: JQuery,
        resolveIgnoredElementsCallback: PrimeType.OverlayResolveIgnoredElementCallback | undefined | null,
        hideCallback: PrimeType.OverlayHideCallback
    ): PrimeType.Unbindable {

        widget.addDestroyListener(() => {
            $(document).off(hideNamespace);
        });

        $(document).off(hideNamespace).on(hideNamespace, (e) => {
            if (overlay.is(':hidden') || overlay.css('visibility') === 'hidden') {
                return;
            }

            var $eventTarget: JQuery<any> = $(e.target);

            // do nothing when the element should be ignored
            if (resolveIgnoredElementsCallback) {
                const elementsToIgnore: JQuery<any> | null | undefined = resolveIgnoredElementsCallback(e);
                if (elementsToIgnore) {
                    if (elementsToIgnore.is($eventTarget) || 
                        // @ts-expect-error @types/jquery is wrong, "has" accepts a JQuery instance
                        elementsToIgnore.has($eventTarget)
                        .length > 0) {
                        return;
                    }
                }
            }

            hideCallback(e, $eventTarget);
        });

        return {
            unbind: () => {
                $(document).off(hideNamespace);
            }
        };
    }

    /**
     * Registers a callback that is invoked when the window is resized.
     * @param widget A widget instance for which to register a resize handler.
     * @param resizeNamespace A resize event with a namespace to listen to, such as `resize.widgetId`.
     * @param element An element that prevents the callback from being invoked when it is not
     * visible, usually a child element of the widget.
     * @param resizeCallback A callback that is invoked when the window is resized.
     * @param params Optional CSS selector. If given, the callback is invoked only when the resize event
     * is triggered on an element the given selector.
     * @returns Object that can be used to remove the registered handler.
     */
    registerResizeHandler(
        widget: BaseWidget,
        resizeNamespace: string,
        element: JQuery | undefined | null,
        resizeCallback: PrimeType.ResizeCallback,
        params?: string
    ): PrimeType.Unbindable {

        const unbindResizeHandler = () => {
            $(window).off(resizeNamespace);
        };

        // #12172 - return early if mobile browser
        if (env.mobile) {
            return { unbind: unbindResizeHandler };
        }

        widget.addDestroyListener(unbindResizeHandler);
        widget.addRefreshListener(unbindResizeHandler);

        $(window).off(resizeNamespace).on(resizeNamespace, params || null, (e) => {
            if (element && (element.is(":hidden") || element.css('visibility') === 'hidden')) {
                return;
            }

            resizeCallback(e);
        });

        return { unbind: unbindResizeHandler };
    }

    /**
     * Registers a MutationObserver and ResizeObserver to watch for DOM changes
     * that may affect element's position and dimensions.
     * @param widget The widget instance to register the observer for
     * @param element The element to observe for changes
     * @param mutationCallback Callback function to execute when relevant
     * mutations occur.
     * @return Object containing bind and unbind functions for the observer
     */
    registerMutationObserver(
        widget: BaseWidget,
        element: JQuery | HTMLElement,
        mutationCallback: PrimeType.MutationCallback,
    ): PrimeType.Controllable {
        const domElement = this.isJQuery(element) ? element.get(0) : element;

        if (domElement === undefined) {
            return { bind:() => {}, unbind: () => {} };
        }

        const resizeObserver = new ResizeObserver(entries => {
            const entry = entries[0];
            const $entry = entry ? $(entry.target) : $();
            if ($entry && ($entry.is(":hidden") || $entry.css('visibility') === 'hidden')) {
                return;
            }
            mutationCallback();
        });

        let rafId: number | undefined;

        const mutationObserver = new MutationObserver(mutations => {
            // Check if mutation involves DOM position changes
            const shouldCallback = mutations.some(mutation => {
                const {type, target, attributeName} = mutation;
                return (type === "childList" && target instanceof Element && target.id === domElement.id) ||
                       (type === "attributes" && target instanceof Element && attributeName === "style" && target.id === domElement.id) ||
                       (type === "attributes" && attributeName === "class");
            });

            if (shouldCallback) {
                // Debounce multiple mutations using requestAnimationFrame
                if (rafId) {
                    window.cancelAnimationFrame(rafId);
                }
                rafId = window.requestAnimationFrame(() => {
                    mutationCallback();
                });
            }
        });

        // Cleanup function to disconnect both observers
        const unbindMutationObserver = () => {
            resizeObserver.unobserve(domElement);
            mutationObserver.disconnect();
        };


        // Add cleanup handlers to widget lifecycle
        widget.addDestroyListener(unbindMutationObserver);
        widget.addRefreshListener(unbindMutationObserver);

        // Start observing DOM mutations
        const bindMutationObserver = () => {
            mutationObserver.observe(document.body, {
                childList: true,
                subtree: true,
                attributes: true
            });

            // Start observing the element for resizing
            resizeObserver.observe(domElement);
        };

        bindMutationObserver();

        return {
            bind: bindMutationObserver,
            unbind: unbindMutationObserver
        };
    }

    /**
     * Sets up an overlay widget. Appends the overlay widget to the element as specified by the `appendTo`
     * attribute. Also makes sure the overlay widget is handled properly during AJAX updates.
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget An overlay widget instance.
     * @param overlay The DOM element for the overlay.
     * @param overlayId The ID of the overlay, usually the widget ID.
     * @return The overlay that was passed to this function.
     */
    registerDynamicOverlay<
        Cfg extends PrimeType.widget.DynamicOverlayFeatureWidgetCfg,
        Widget extends PrimeType.widget.DynamicOverlayFeatureWidget<Cfg>
    >(widget: Widget, overlay: JQuery, overlayId: string): JQuery {

        if (widget.cfg.appendTo) {
            var appendTo = this.resolveDynamicOverlayContainer(widget);
            this.appendDynamicOverlay(widget, overlay, overlayId, appendTo);

            widget.addDestroyListener(() => {
                var appendTo = this.resolveDynamicOverlayContainer(widget);
                // pass null as overlay - as every! overlay with this overlayId can be removed on destroying the whole widget
                this.removeDynamicOverlay(widget, null, overlayId, appendTo);
            });

            widget.addRefreshListener(() => {
                var appendTo = this.resolveDynamicOverlayContainer(widget);
                this.cleanupDynamicOverlay(widget, overlay, overlayId, appendTo);
            });
        }

        return overlay;
    }

    /**
     * Registers a callback that is invoked when a scroll event is triggered on the DOM element for the widget.
     * @param widget A widget instance for which to register a scroll handler.
     * @param scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
     * @param scrollCallback A callback that is invoked when a scroll event
     * occurs on the widget.
     * @return unbind callback handler
     */
    registerScrollHandler(
        widget: BaseWidget,
        scrollNamespace: string,
        scrollCallback: PrimeType.ScrollCallback,
    ): PrimeType.Unbindable {
        let widgetJq = widget.getJQ();
        let scrollParent: JQuery<any> | null = (widgetJq && typeof widgetJq.scrollParent === 'function') ? widgetJq.scrollParent() : null;

        if (!scrollParent || this.isScrollParentWindow(scrollParent)) {
            scrollParent = $(window);
        }

        // To avoid holding the $(window) variable explicitly, you can directly bind and unbind 
        // the scroll event on the window within the function itself.
        var scrollHandler = (e: JQuery.TriggeredEvent) => {
            scrollCallback(e);
        };

        scrollParent.off(scrollNamespace).on(scrollNamespace, scrollHandler);

        widget.addDestroyListener(() => {
            scrollParent.off(scrollNamespace, scrollHandler);
        });
        widget.addRefreshListener(() => {
            scrollParent.off(scrollNamespace, scrollHandler);
        });

        return {
            unbind: () => {
                scrollParent.off(scrollNamespace, scrollHandler);
            }
        };
    }

    /**
     * Registers a callback that is invoked when a scroll event is triggered on The DOM element for the widget that
     * has a connected overlay.
     * @param widget A widget instance for which to register a scroll handler.
     * @param scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
     * @param element A DOM element used to find scrollable parents.
     * @param scrollCallback A callback that is invoked when a scroll event occurs on the widget.
     * @return Unbind function to remove the registered handler.
     */
    registerConnectedOverlayScrollHandler(
        widget: BaseWidget,
        scrollNamespace: string,
        element: JQuery | undefined,
        scrollCallback: PrimeType.ScrollCallback
    ): PrimeType.Unbindable {
        const scrollableParents = this.getScrollableParents((element ?? widget.getJQ()).get(0));

        const scrollHandler = (e: JQuery.TriggeredEvent) => {
            scrollCallback(e);
        };

        for (const scrollParent of scrollableParents) {
            var $scrollParent = $(scrollParent);

            widget.addDestroyListener(() => {
                $scrollParent.off(scrollNamespace, scrollHandler);
            });

            $scrollParent.off(scrollNamespace, scrollHandler).on(scrollNamespace, scrollHandler);
        }

        return {
            unbind: () => {
                for (const scrollParent of scrollableParents) {
                    $(scrollParent).off(scrollNamespace, scrollHandler);
                }
            }
        };
    }

    /**
     * Finds scrollable parents (not the document).
     * @param element An element used to find its scrollable parents.
     * @returns The list of scrollable parents.
     */
    getScrollableParents(element: Element | undefined): (Window | Element)[] {
        const scrollableParents: (Window | Element)[] = [];
        const getParents = (element: ParentNode, parents: ParentNode[]): ParentNode[] => {
            return element.parentNode == null ? parents : getParents(element.parentNode, parents.concat([element.parentNode]));
        };

        const addScrollableParent = (node: HTMLElement): void => {
            if (this.isScrollParentWindow($(node))) {
                scrollableParents.push(window);
            } else {
                scrollableParents.push(node);
            }
        };

        if (element) {
            const parents = getParents(element, []);
            const overflowRegex = /(auto|scroll)/;
            const overflowCheck = (node: Element) => {
                const styleDeclaration = window.getComputedStyle(node, null);
                return overflowRegex.test(styleDeclaration.getPropertyValue('overflow')) || overflowRegex.test(styleDeclaration.getPropertyValue('overflowX')) || overflowRegex.test(styleDeclaration.getPropertyValue('overflowY'));
            };

            for (const parent of parents) {
                const scrollSelectors = parent instanceof HTMLElement && parent.dataset['scrollselectors'];
                if (scrollSelectors) {
                    const selectors = scrollSelectors.split(',');
                    for (const selector of selectors) {
                        const el = parent.querySelector(selector);
                        if (el instanceof HTMLElement && overflowCheck(el)) {
                            addScrollableParent(el);
                        }
                    }
                }

                if (parent instanceof HTMLElement && overflowCheck(parent)) {
                    addScrollableParent(parent);
                }
            }
        }

        // if no parents make it the window
        if (scrollableParents.length === 0) {
            scrollableParents.push(window);
        }

        return scrollableParents;
    }

    /**
     * Removes a scroll handler as registered by {@link registerScrollHandler}.
     * @param widget A widget instance for which a scroll handler was registered.
     * @param scrollNamespace A scroll event with a namespace, such as `scroll.widgetId`.
     */
    unbindScrollHandler(widget: BaseWidget, scrollNamespace: string): void {
        var scrollParent = widget.getJQ().scrollParent();
        if (this.isScrollParentWindow(scrollParent)) {
            $(window).off(scrollNamespace); // Unbind directly from window
        } else {
            scrollParent.off(scrollNamespace);
        }
    }

    /**
     * Prevents the user from scrolling the document BODY element. You can enable scrolling again via
     * {@link enableScrolling}.
     */
    preventScrolling(): void {
        $(document.body).addClass('ui-overflow-hidden');
    }

    /**
     * Enables scrolling again if previously disabled via {@link preventScrolling}.
     */
    enableScrolling(): void {
        $(document.body).removeClass('ui-overflow-hidden');
    }

    /**
     * Calculates an element offset relative to the current scroll position of the window.
     * @param element An element for which to calculate the scroll position.
     * @return The offset of the given element, relative to the current scroll position of the
     * window.
     */
    calculateRelativeOffset(element: JQuery): JQuery.Coordinates {
        const result: JQuery.Coordinates = {
            left : 0,
            top : 0,
        };
        const offset = element.offset() ?? { left: 0, top: 0 };
        const scrollTop = $(window).scrollTop() ?? 0;
        const scrollLeft = $(window).scrollLeft() ?? 0;
        result.top = offset.top - scrollTop;
        result.left = offset.left - scrollLeft;
        return result;
    }

    /**
     * Blocks the enter key for an event like `keyup` or `keydown`. Useful in filter input events in many
     * components.
     * @param e The key event that occurred.
     * @returns `true` if ENTER key was blocked, false if not.
     */
    blockEnterKey(e: JQuery.TriggeredEvent): boolean {
        if(e.key === 'Enter') {
            e.preventDefault();
            e.stopPropagation();
            return true;
        }
        return false;
    }

    /**
     * Checks if the ENTER key should be allowed for the given event target. This is useful for determining
     * whether to block or allow ENTER key events based on the type of element that received the event.
     * @param e The key event that occurred.
     * @returns `true` if ENTER key should be allowed for the target element, `false` otherwise.
     */
    isEnterKeyBlocked(e: JQuery.TriggeredEvent): boolean {
        // #7028/#13927 Do not proceed if target is a textarea, button, link, or TextEditor
        const eventTarget = $(e.target);
        if (eventTarget.is('textarea,button,input[type="submit"],a,.ql-editor')) {
            return true;
        }
        return false;
    }
    
    /**
     * Is this CMD on MacOs or CTRL key on other OSes. 
     * @param e The key event that occurred.
     * @return `true` if the key is a meta key, `false` or `undefined` otherwise.
     */
    isMetaKey(e: JQuery.TriggeredEvent): boolean | undefined {
        if (e.originalEvent instanceof KeyboardEvent || e.originalEvent instanceof MouseEvent || (window.TouchEvent && e.originalEvent instanceof TouchEvent)) {
            // original event returns the metaKey value at the time the event was generated
            return env.browser?.mac ? e.originalEvent.metaKey : e.originalEvent.ctrlKey;
        }
        else {
            // jQuery returns the real time value of the meta key
            return env.browser?.mac ? e.metaKey : e.ctrlKey;
        }
    }

    /**
     * Is this SPACE or ENTER key. Used throughout codebase to trigger and action.
     * @param e The key event that occurred.
     * @returns `true` if the key is an action key, or `false` otherwise.
     */
    isActionKey(e: JQuery.KeyboardEventBase | JQuery.TriggeredEvent): boolean {
        return ("code" in e && e.code === 'Space') || e.key === 'Enter';
    }

    /**
     * Checks if the key pressed is a printable key like 'a' or '4' etc.
     * @param e The key event that occurred.
     * @returns `true` if the key is a printable key, or a falsy value otherwise.
     */
    isPrintableKey(e: JQuery.TriggeredEvent | undefined | null): string | boolean | null | undefined {
        return e && e.key && (e.key.length === 1 || e.key === 'Unidentified');
    }
    
    /**
     * Checks if the key pressed is cut, copy, or paste.
     * @param e The key event that occurred.
     * @return {boolean} `true` if the key is cut/copy/paste, or `false` otherwise.
     */
    isClipboardKey(e: JQuery.TriggeredEvent): boolean | undefined {
        switch (e.key) {
            case 'a':
            case 'A':
            case 'c':
            case 'C':
            case 'x':
            case 'X':
            case 'v':
            case 'V':
                return this.isMetaKey(e);
            default:
                return false;
        }
    }

    /**
     * Ignores unprintable keys on filter input text box. Useful in filter input events in many components.
     * @param e The key event that occurred.
     * @return {boolean} `true` if the one of the keys to ignore was pressed, or `false` otherwise.
     */
    ignoreFilterKey(e: JQuery.KeyboardEventBase | JQuery.TriggeredEvent): boolean {
        // cut copy paste allows filter to trigger
        if (this.isClipboardKey(e)) {
            return false;
        }
        // backspace,enter,delete trigger a filter as well as printable key like 'a'
        switch ("code" in e ? e.code : e.key) {
            case 'Backspace':
            case 'Enter':
            case 'NumpadEnter':
            case 'Delete':
                return false;
            default:
                return !this.isPrintableKey(e);
        }
    }

    /**
     * Exclude elements such as buttons, links, inputs from being touch swiped.  Users can always add
     * `class="noSwipe"` to any element to exclude it as well.
     * @return {string} A CSS selector for the elements to be excluded from being touch swiped.
     */
    excludedSwipeElements() {
        return ":button:enabled, :input:enabled, a, [role='combobox'], .noSwipe";
    }

    /**
     * Helper to open a new URL and if CTRL is held down open in new browser tab.
     *
     * @param event The click event that occurred.
     * @param link The URL anchor link that was clicked.
     */
    openLink(event: JQuery.TriggeredEvent, link: JQuery): void {
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

    /**
     * Enables a widget for editing and sets it style as enabled.
     *
     * @param jq a required jQuery element to enable
     * @param input an optional jQuery input to enable (will use jq if null)
     */
    enableInputWidget(jq: JQuery, input?: JQuery | undefined | null): void {
        if(!input) {
            input = jq;
        }
        if (input.is(':disabled')) {
            input.prop('disabled', false);
        }
        jq.removeClass('ui-state-disabled');
    }

    /**
     * Disables a widget from editing and sets it style as disabled.
     *
     * @param jq a required jQuery element to disable
     * @param input an optional jQuery input to disable (will use jq if null)
     */
    disableInputWidget(jq: JQuery, input: JQuery | undefined | null): void {
        if(!input) {
            input = jq;
        }
        if (!input.is(':disabled')) {
            input.prop('disabled', true);
        }
        jq.addClass('ui-state-disabled');
    }

    /**
     * Enables a button.
     *
     * @param jq a required jQuery element to enable
     */
    enableButton(jq: JQuery): void {
        if (jq) {
            jq.removeClass('ui-state-disabled')
              .prop( "disabled", false)
              .removeAttr('aria-disabled');
        }
    }

    /**
     * Disables a button from being clicked.
     *
     * @param jq a required jQuery button to disable
     */
    disableButton(jq: JQuery): void {
        if (jq) {
            jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
              .addClass('ui-state-disabled')
              .attr('disabled', 'disabled')
              .attr('aria-disabled', 'true');
        }
    }

    /**
     * Enables CSS and jQuery animation.
     */
    enableAnimations(): void {
        $.fx.off = false;
        core.animationEnabled = true;
    }

    /**
     * Disables CSS and jQuery animation.
     */
    disableAnimations(): void {
        $.fx.off = true;
        core.animationEnabled = false;
    }

    /**
     * CSS Transition method for overlay panels such as SelectOneMenu/SelectCheckboxMenu/Datepicker's panel etc.
     * @param element An element for which to execute the transition.
     * @param className Class name used for transition phases.
     * @return Two handlers named `show` and `hide` that should be invoked
     * when the element gets shown and hidden. If the given element or className property is `undefined` or `null`,
     * this function returns `null`.
     */
    registerCSSTransition(element: JQuery | undefined | null, className: string | undefined | null): PrimeType.CssTransitionHandler | null {
        if (element && className != null) {
            const classNameStates = {
               'enter': className + '-enter',
               'enterActive': className + '-enter-active',
               'enterDone': className + '-enter-done',
               'exit': className + '-exit',
               'exitActive': className + '-exit-active',
               'exitDone': className + '-exit-done'
            };
            const callTransitionEvent = <K extends keyof PrimeType.CssTransitionCallback>(
                callbacks: PrimeType.CssTransitionCallback | null | undefined,
                key: K,
                param: ThisParameterType<Exclude<PrimeType.CssTransitionCallback[K], undefined>>
            ) => {
                callbacks?.[key]?.call(param as any);
            };

            return {
                show: (callbacks) => {
                    //clear exit state classes
                    element.removeClass([classNameStates.exit, classNameStates.exitActive, classNameStates.exitDone]);

                    if (element.is(':hidden')) {
                        if (core.animationEnabled) {
                            core.animationActive = true;
                            element.css('display', 'block').addClass(classNameStates.enter);
                            callTransitionEvent(callbacks, 'onEnter', window);

                            requestAnimationFrame(() => {
                                core.queueTask(() => {
                                    element.addClass(classNameStates.enterActive);
                                }, 1);

                                element.one('transitionrun.css-transition-show', (event) => {
                                    callTransitionEvent(callbacks, 'onEntering', event);
                                }).one('transitioncancel.css-transition-show', () => {
                                    element.removeClass([classNameStates.enter, classNameStates.enterActive, classNameStates.enterDone]);
                                    core.animationActive = false;
                                }).one('transitionend.css-transition-show', (event) => {
                                    element.removeClass([classNameStates.enterActive, classNameStates.enter]).addClass(classNameStates.enterDone);
                                    callTransitionEvent(callbacks, 'onEntered', event);
                                    core.animationActive = false;
                                });
                            });
                        }
                        else {
                            // animation globally disabled still call downstream callbacks
                            element.css('display', 'block');
                            callTransitionEvent(callbacks, 'onEnter', window);
                            callTransitionEvent(callbacks, 'onEntering', window);
                            callTransitionEvent(callbacks, 'onEntered', window);
                        }
                    }
                },
                hide: (callbacks) => {
                    //clear enter state classes
                    element.removeClass([classNameStates.enter, classNameStates.enterActive, classNameStates.enterDone]);

                    if (element.is(':visible')) {
                        if (core.animationEnabled) {
                            core.animationActive = true;
                            element.addClass(classNameStates.exit);
                            callTransitionEvent(callbacks, 'onExit', window);

                            core.queueTask(() => {
                                element.addClass(classNameStates.exitActive);
                            });

                            element.one('transitionrun.css-transition-hide', (event) => {
                                callTransitionEvent(callbacks, 'onExiting', event);
                            }).one('transitioncancel.css-transition-hide', () => {
                                element.removeClass([classNameStates.exit, classNameStates.exitActive, classNameStates.exitDone]);
                                core.animationActive = false;
                            }).one('transitionend.css-transition-hide', (event) => {
                                element.css('display', 'none').removeClass([classNameStates.exitActive, classNameStates.exit]).addClass(classNameStates.exitDone);
                                callTransitionEvent(callbacks, 'onExited', event);
                                core.animationActive = false;
                            });
                        }
                        else {
                            // animation globally disabled still call downstream callbacks
                            callTransitionEvent(callbacks, 'onExit', window);
                            callTransitionEvent(callbacks, 'onExiting', window);
                            callTransitionEvent(callbacks, 'onExited', window);
                            element.css('display', 'none');
                        }
                    }
                }
            };
        }

        return null;
    }

    /**
     * Count the bytes of the given text. Handles ASCII, UTF-8, and emojis.
     * @param text Text to count bytes from.
     * @return The byte count
     */
    countBytes(text: string): number {
        // lazy load TextEncoder so its only created once
        if (!this.TEXT_ENCODER) {
            this.TEXT_ENCODER = new TextEncoder();
        }
        return this.TEXT_ENCODER.encode(text).length;
    }

    /**
     * Converts a string to its uppercase form, using the root locale.
     * @typeParam S Type of the string to convert.
     * @param value String to convert.
     * @returns The string in its upper case form.
     */
    toRootUpperCase<S extends string>(value: S): Uppercase<S> {
        return value.toUpperCase() as Uppercase<S>;
    }

    /**
     * Converts a string to its lowercase form, using the root locale.
     * @typeParam S Type of the string to convert.
     * @param value String to convert.
     * @returns The string in its upper case form.
     */
    toRootLowerCase<S extends string>(value: S): Lowercase<S> {
        return value.toLowerCase() as Lowercase<S>;
    }

    /**
     * Formats the allowTypes regex pattern in a more human-friendly format.
     * @param allowTypes The allowTypes regex pattern to format
     * @return The allowTypes formatted in a more human-friendly format.
     */
    formatAllowTypes(allowTypes: string): string {
        if (!allowTypes) {
            return allowTypes;
        }

        // not a correct regex pattern
        if (!allowTypes.startsWith('/')) {
            return allowTypes;
        }

        // formats like /(\.|\/)(gif|jpeg|jpg|png)$/
        let match = allowTypes.match(/\/\(\\\.\|\\\/\)\(?(.*?)\)?\$?\//);
        if (match) {
            return '.' + match[1]?.replace(/\|/g, ', .');
        }

        // formats like .*\.(xls|xlsx|csv|txt)
        match = allowTypes.match(/\/\.\*\\\.\(?(.*?)\)?\$?\//);
        if (match) {
            return '.' + match[1]?.replace(/\|/g, ', .');
        }

        // others return unchanged
        return allowTypes;
    }

    /**
     * Formats the given data size in a more human-friendly format, e.g., `1.5 MB` etc.
     * @param bytes File size in bytes to format
     * @return The given file size, formatted in a more human-friendly format.
     */
    formatBytes(bytes: number): string {
        if (bytes === undefined) {
            return '';
        }

        if (bytes === 0) {
            return 'N/A';
        }

        const sizes = core.getLocaleLabel('fileSizeTypes');
        const i = this.roundTowardsZero(Math.floor(Math.log(bytes) / Math.log(1024)));
        if (i === 0) {
            return bytes + ' ' + sizes[i];
        }
        else {
            return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
        }
    }

    /**
     * This method concatenates the classes into a string and returns it. Numbers
     * are converted to strings. If an object is given, adds the keys mapped to
     * `true` to the list of style classes.
     * @returns The concatenated style classes.
     */
    styleClass(...args: (number | string | (number | string | null | undefined)[] | Record<string, boolean>)[]): string {
        let classes: string[] = [];

        for (const className of args) {

            if (!className) continue;

            if (typeof className === 'string' || typeof className === 'number') {
                classes.push(String(className));
            }
            else if (typeof className === 'object') {
                const _classes = Array.isArray(className) ? className : Object.keys(className).map((key) => className[key] ? key : null);

                classes = _classes.length ? classes.concat(_classes.filter((c) => !!c).map(c => String(c))) : classes;
            }
        }

        return classes.join(' ');
    }

    /**
     * When configuring numeric value like 'showDelay' and the user wants '0' we can't treat 0 as falsy 
     * so we make the value 0.  Otherwise falsy returns the default value.
     *
     * @param value The original value
     * @param defaultValue The required default value if value is not set
     * @return The calculated value
     */
    defaultNumeric(value: number | undefined, defaultValue: number): number {
        if (value === 0) {
            return 0;
        }
        return value || defaultValue;
    }

    /**
     * Is this component wrapped in a float label?
     *
     * @param jq An element to check if wrapped in float label. 
     * @returns `true` this this JQ has a float label parent
     */
    hasFloatLabel(jq: JQuery | undefined | null): boolean {
        if (!jq || !jq.parent()) {
            return false;
        }
        return jq.parent().hasClass('ui-float-label');
    }

    /**
     * Handles floating label CSS if wrapped in a floating label.
     * @param element the to add the CSS classes to
     * @param inputs the input(s) to check if filled
     * @param hasFloatLabel true if this is wrapped in a floating label
     * @returns `undefined` if any parameter is missing, `false`, if the float label was not updated, `undefined` if it was. 
     */
    updateFloatLabel(element: JQuery | undefined, inputs: JQuery | undefined, hasFloatLabel: boolean | undefined): boolean | undefined {
        if (!element || !inputs || !hasFloatLabel) {
            return;
        }

        var isEmpty = true;
        var value = null;
        for (const _input of inputs) {
            var input = $(_input);
            if (input.is('select')) {
                if (input.attr('multiple')) {
                    isEmpty = input.find('option:selected').length === 0;
                }
                else {
                    value = input.find('option:selected').attr('value');
                    isEmpty = value === null || value === '';
                }
            }
            else {
                value = input.val();
                isEmpty = value === null || value === '';
            }

            if (!isEmpty) {
                return false;
            }
        }

        if (isEmpty) {
            element.removeClass('ui-inputwrapper-filled');
        }
        else {
            element.addClass('ui-inputwrapper-filled');
        }

        return undefined;
    }

    /**
     * Decode escaped XML into regular string.
     *
     * @param input The input to check if filled.
     * @return Either the original string or escaped XML.
     */
    decodeXml(input: string | undefined): string | undefined | null {
        if (/&amp;|&quot;|&#39;|'&lt;|&gt;/.test(input ?? "")) {
            var doc = new DOMParser().parseFromString(input ?? "", "text/html");
            return doc.documentElement.textContent;
        }
        return input;
    }
    
    /**
     * Queue a microtask if delay is 0 or less and `setTimeout` if `> 0`.
     *
     * @param fn The function to call after the delay
     * @param delay The optional delay in milliseconds
     * @return The id associated to the timeout or undefined if no timeout used.
     */
    queueTask(fn: () => void, delay?: number | undefined): number | undefined {
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
    }

    /**
     * Killswitch that kills all AJAX requests, running Pollers and IdleMonitors.
     * @see {@link https://github.com/primefaces/primefaces/issues/10299 | GitHub Issue 10299}
     */
    killswitch(): void {
        core.warn("Abort all AJAX requests!");
        ajax.Queue.abortAll();

        // stop all pollers and idle monitors, etc.
        for (const killSwitchHook of core.getHook("killSwitch")) {
            try {
                killSwitchHook.kill();            
            } catch (e) {
                core.error(e);
            }
        }
    }

    /**
     * Retrieves the subsequent z-index for a sticky element. Typically, a sticky element requires a 
     * z-index higher than the current one, but certain scenarios arise, such as when an overlay mask 
     * is present or when there are multiple sticky elements on the page, necessitating a z-index 
     * one lower than the highest among them.
     *
     * @return The next `z-index` as a string.
     * @see {@link https://github.com/primefaces/primefaces/issues/10299 | GitHub Issue 10299}
     * @see {@link https://github.com/primefaces/primefaces/issues/9259 | GitHub Issue 9259}
     */
    nextStickyZindex(): number {
        // Get the z-index of the highest visible sticky, or use PrimeFaces.nextZindex() + 1 if none found
        var highestStickyZIndex = this.roundTowardsZero($('.ui-sticky:visible').last().zIndex()) || parseInt(core.nextZindex()) + 1;

        // GitHub #9295 Adjust z-index based on overlays
        var overlays = $('.ui-widget-overlay:visible');
        if (overlays.length) {
            for (const overlay of overlays) {
                var overlayZIndex = this.roundTowardsZero($(overlay).zIndex()) - 1;
                highestStickyZIndex = Math.min(overlayZIndex, highestStickyZIndex);
            }
        } else {
            // #12151 Adjust z-index for sticky elements when an overlay mask is not present
            core.zindex = highestStickyZIndex - 1;
        }

        // Decrease zIndex by 1 and return
        return highestStickyZIndex - 1;
    }
    
    /**
     * Deletes all events, 'on' attributes, data, and the element itself in a recursive manner, 
     * ensuring that the garbage collector does not retain any references to this element or its children.
     *
     * @param jq jQuery object to cleanse
     * @param clearData flag to clear data off elements (default to true)
     * @param removeElement flag to remove the element from DOM (default to true)
     * @see {@link https://github.com/primefaces/primefaces/issues/11696 | GitHub Issue 11696}
     * @see {@link https://github.com/primefaces/primefaces/issues/11702 | GitHub Issue 11702}
     */
    cleanseDomElement(jq: JQuery | undefined, clearData: boolean = true, removeElement: boolean = true): void {
        if (!jq || !jq.length) {
            return;
        }

        //Skip cleanse of select and svg tags, it can impact performance if a lot of tags are present. They don't have PF listeners attached, so cleanse it's unnecesary.
        if (!jq.is("select, svg, svg *")) {
            // Recursively remove events from children elements
            for (const child of jq.children()) {
                this.cleanseDomElement($(child), clearData, removeElement);
            }
        }
        // Remove inline event attributes
        const attributes = jq[0]?.attributes ?? [];
        for (const attribute of attributes) {
            const attributeName = attribute.name;
            if (attributeName.startsWith("on")) {
                jq.removeAttr(attributeName);
            }
        }

        // Trigger onRemove events for widget.destroy and remove the element from the DOM
        // IMPORTANT: This must occur before jq.off() to ensure the on("remove") events remain registered.
        if (removeElement) {
            jq.triggerHandler("remove");
            jq.get(0)?.remove()
        }

        // Remove event listeners
        jq.off();

        // Clear data
        if (clearData) {
            jq.removeData();
        }
    }
    
    /**
     * Replaces a specific CSS icon class on an element and appends a new icon class.
     * If the target class is found, all classes after it are removed and the new class is added.
     *
     * @param jq - The jQuery element to modify.
     * @param addIcon - The new CSS icon class to add.
     */
    replaceIcon(jq: JQuery | undefined, addIcon: string): void {
        if (!jq || !jq.length) {
            return;
        }
        // Get the value of the 'class' attribute and split it into an array
        const classes = jq.attr('class')?.split(' ') ?? [];

        // Find the index of the target class
        const targetIndex = classes.indexOf('ui-c');

        // If the target class is found, remove all classes after it and add the new class
        if (targetIndex !== -1) {
            // Create the new class string by keeping classes up to and including the target class, then adding the new class
            const newClasses = classes.slice(0, targetIndex + 1).join(' ') + ' ' + addIcon;

            // Set the new class string on the element and store the new class in the 'p-icon' data attribute
            jq.attr('class', newClasses).data('p-icon', addIcon);
        }
    }

    /**
     * Checks if the given value is a JQuery instance (usually DOM elements
     * in a JQuery wrapper).
     * 
     * Note to TypeScript users: This is a type predicate that can be useful
     * for narrowing, since `instanceof $` does not work as `$` is not a proper
     * class.
     * @param value Value to check.
     * @returns `true` if the value is a JQuery instance, `false` otherwise.
     */
    isJQuery(value: unknown): value is JQuery {
        return value instanceof $;
    }

    /**
     * Wraps the given element in a JQuery instance, if not already such an
     * instance. If a string is given, interprets it as a CSS selector and
     * returns a JQuery instance with all element in the page matching that
     * selector. 
     * @param value Value to wrap. 
     * @returns The value wrapped in a JQuery instance.
     */
    toJQuery(value: string | HTMLElement | JQuery | undefined | null): JQuery {
        if (value === undefined || value === null) {
            return $();
        }
        if (this.isJQuery(value)) {
            return value;
        }
        return typeof value === "string" ? $(value) : $(value);
    }

    /**
     * Checks if an element is currently visible within the browser viewport, with an optional offset.
     * @param element The element to check visibility for. Can be either a DOM element or jQuery object.
     * @param offset Optional offset in pixels to expand/contract the viewport boundaries.
     * @returns boolean True if the element is visible within the viewport, false otherwise.
     */
    isVisibleInViewport(element: string | HTMLElement | JQuery, offset: number = 0): boolean {
        if (!element) {
            return false;
        }
        
        var $element = this.toJQuery(element);
        var elementTop = $element.offset()?.top ?? 0;
        var elementBottom = elementTop + ($element.outerHeight() ?? 0);
    
        var $window = $(window);
        var viewportTop = ($window.scrollTop() ?? 0) - offset;
        var viewportBottom = viewportTop + ($window.height() ?? 0) + offset;
    
        return elementBottom > viewportTop && elementTop < viewportBottom;
    }
}

/**
 * The object with various utilities needed by PrimeFaces.
 */
export const utils: Utils = new Utils();

export function globalUtilsSetup(): void {
    // set animation state globally
    if (env.prefersReducedMotion) {
        utils.disableAnimations();
        core.warn("Animations are disabled because OS has requested prefers-reduced-motion: reduce")
    }
}
