import { csp } from "./core.csp.js";
import { core } from "./core.js";
import { utils } from "./core.utils.js";
import type { BaseComponent } from "./core.component.js";
import { ajax } from "./core.ajax.js";

/**
 * The class containing low-level functionality related to handling AJAX responses. Note that
 * the different types of AJAX actions are handled by the {@link AjaxResponseProcessor PrimeFaces.ajax.ResponseProcessor}.
 */
export class AjaxResponse {
    /**
     * Handles the response of an AJAX request. The response consists of one or more actions such as executing a
     * script or updating a DOM element. See also {@link faces.ajax.response}.
     *
     * Also updates the specified components if any and synchronizes the client side Faces state. DOM updates are
     * implemented using jQuery which uses a very algorithm.
     *
     * @typeParam Component Type of the component which triggered the AJAX request.
     * @param xml The XML that was returned by the AJAX request.
     * @param status Text status of the request.
     * @param xhr The XHR request to which a response was received.
     * @param updateHandler Optional handler for `update` actions.
     */
    handle<Component extends BaseComponent<any>>(
        xml: XMLDocument,
        status: JQuery.Ajax.SuccessTextStatus,
        xhr: PrimeFaces.ajax.pfXHR,
        updateHandler?: PrimeFaces.ajax.UpdateHandler<Component> | null
    ): void {
        // Faces spec: emptyResponse if there is no response XML
        if (xml === undefined || xml === null) {
            this.handleResponseError(ajax.ERROR_EMPTY_RESPONSE, "The response has provided no data.", xhr);
            return;
        }

        const partialResponseNode = xml.getElementsByTagName("partial-response")[0];

        // Faces spec: malformedXML if the response is not a partial response, e.g. a login page after a session timeout
        if (!partialResponseNode) {
            this.handleResponseError(ajax.ERROR_MALFORMED_XML, "No partial-response found.", xhr);
            return;
        }

        for (const currentNode of partialResponseNode.childNodes) {

            switch (currentNode.nodeName) {
                case "redirect":
                    // will be done afterwards, we execute all changes (especially 'eval') first. See #13289.
                    break;

                case "changes":
                    var activeElement = document.activeElement ? $(document.activeElement) : $();
                    var activeElementId = activeElement.attr('id');
                    var activeElementSelection = utils.getTextSelection(activeElement) ?? undefined;

                    for (const currentChangeNode of currentNode.childNodes) {
                        switch (currentChangeNode.nodeName) {
                            case "update":
                                ajax.ResponseProcessor.doUpdate(currentChangeNode, xhr, updateHandler);
                                break;
                            case "delete":
                                ajax.ResponseProcessor.doDelete(currentChangeNode);
                                break;
                            case "insert":
                                ajax.ResponseProcessor.doInsert(currentChangeNode);
                                break;
                            case "attributes":
                                ajax.ResponseProcessor.doAttributes(currentChangeNode);
                                break;
                            case "eval":
                                ajax.ResponseProcessor.doEval(currentChangeNode, xhr);
                                break;
                            case "extension":
                                ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                                break;
                        }
                    }

                    this.handleReFocus(activeElementId, activeElementSelection);
                    this.destroyDetachedComponents();
                    break;

                case "eval":
                    ajax.ResponseProcessor.doEval(currentNode);
                    break;

                case "extension":
                    ajax.ResponseProcessor.doExtension(currentNode, xhr);
                    break;

                case "error":
                    ajax.ResponseProcessor.doError(currentNode, xhr);
                    break;
            }
        }

        // handle redirect as last step, see #13289
        const redirectNodes = Array.from(partialResponseNode.childNodes).filter(node => node.nodeName === "redirect");
        for (const currentNode of redirectNodes) {
            const pfArgs = xhr.pfArgs;
            if (pfArgs) {
                pfArgs.redirect = true;
            }
            ajax.ResponseProcessor.doRedirect(currentNode);
            break;
        }
    }

    /**
     * Handles an error of a response (invalid response or server error) via {@link AjaxUtils.handleError}, if not
     * skipped via {@link PrimeFaces.ajax.Configuration.skipErrorHandling}.
     * @param errorName The error name.
     * @param errorMessage The error message.
     * @param xhr The XHR request to which the response was received.
     */
    handleResponseError(errorName: string, errorMessage: string, xhr: PrimeFaces.ajax.pfXHR | null | undefined): void {
        // e.g. the request of a p:ajaxExceptionHandler itself, see Configuration.skipErrorHandling
        if (xhr?.pfSettings?.skipErrorHandling) {
            core.error(errorName + ": " + errorMessage);
            return;
        }
        ajax.Utils.handleError(errorName, errorMessage);
    }

    /**
     * Executes the scripts added via `PrimeFaces.current().executeScript(...)`, which were collected in
     * {@link PrimeFaces.ajax.pfXHR.pfScripts} while handling the response. They are invoked the same way as the
     * `oncomplete` attribute of an AJAX source. Components sending their own AJAX request instead of using
     * {@link AjaxRequest.send} (e.g. FileUpload) must call this after their `oncomplete` callback.
     * @param xhr The XHR request to which a response was received.
     * @param status Text status of the request.
     * @param data The data returned by the request.
     */
    executeScripts(xhr: string | PrimeFaces.ajax.pfXHR | null | undefined, status?: string, data?: unknown): void {
        if (!xhr || typeof xhr === "string" || !xhr.pfScripts?.length) {
            return;
        }

        const { pfArgs = {}, pfScripts, pfSettings = {} } = xhr;
        try {
            (window.PrimeFaces as any).scriptContext = {
                xhr: xhr,
                status: status,
                args: pfArgs,
                data: data
            };

            for (const script of pfScripts) {
                csp.eval(`(function(xhr, status, args, data) {
                    ${script}
                })(
                    window.PrimeFaces.scriptContext.xhr,
                    window.PrimeFaces.scriptContext.status,
                    window.PrimeFaces.scriptContext.args,
                    window.PrimeFaces.scriptContext.data
                );`, pfSettings.nonce, window);
            }
        } finally {
            delete (window.PrimeFaces as any).scriptContext;
        }
    }

    /**
     * Puts focus on the given element if necessary.
     * @param activeElementId ID of the active to refocus.
     * @param activeElementSelection The range to select, for INPUT and TEXTAREA elements.
     */
    handleReFocus(activeElementId: string | undefined, activeElementSelection?: PrimeFaces.ajax.ActiveElementSelection): void {
        // skip when customFocus is active
        if (core.customFocus === true) {
            core.customFocus = false;
            return;
        }

        // no active element remembered
        if (!activeElementId) {
            return;
        }

        var elementToFocus = $(core.escapeClientId(activeElementId));
        if (elementToFocus.length > 0) {

            var refocus = function() {
                // already focussed?
                if (activeElementId !== document.activeElement?.id) {
                    // focus
                    elementToFocus.trigger('focus');

                    // reapply cursor / selection
                    if (activeElementSelection) {
                        utils.setTextSelection(elementToFocus, activeElementSelection.start, activeElementSelection.end);
                    }
                }
            };

            refocus();
        }
    }

    /**
     * Destroys all components that are not part of the DOM anymore, usually because they were removed by an AJAX
     * update. Calls the `destroy` method on the component and removes the component from the global component registry.
     */
    destroyDetachedComponents(): void {
        // destroy detached components
        for (const ref of core.detachedComponents) {
            // Do not use PF() which logs an error when the component is missing
            // Since the component is detached, it may already be gone.
            const component = core.components[ref];
            if (component && component.isDetached() === true) {
                try {
                    component.destroy();
                    delete core.components[ref];
                } catch (e) { core.warn("Error destroying component: " + ref) }
            }
        }

        core.detachedComponents = [];
    }

    /**
     * @deprecated Use {@link destroyDetachedComponents} instead.
     */
    destroyDetachedWidgets(): void {
        ajax.Response.destroyDetachedComponents();
    }
}
