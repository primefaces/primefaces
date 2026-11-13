import { csp } from "./core.csp.js";
import { core } from "./core.js";
import type { BaseComponent } from "./core.component.js";
import { ajax } from "./core.ajax.js";

/**
 * Retrieves the text content of a node.
 * @param node Node from which to get its text.
 * @returns The text content of the node. 
 */
function getNodeTextContent(node: Node): string {
    if (node.textContent) {
        return node.textContent;
    }
    if (node instanceof HTMLElement && node.innerText) {
        return node.innerText;
    }
    // Previously, we also tried node.text, but that's a old IE (IE6?) feature
    return "";
}

/**
 * The class containing low-level functionality related to processing the different types
 * of actions from AJAX responses.
 */
export class AjaxResponseProcessor {
    /**
     * Handles a `redirect` AJAX action by performing a redirect to the target URL.
     * @param node The XML node of the `redirect` action.
     */
    doRedirect(node: Node): void {
        if (!(node instanceof Element)) {
            return;
        }
        try {
            window.location.assign(node.getAttribute('url') ?? "");
        } catch (error) {
            core.warn('Error redirecting to URL: ' + node.getAttribute('url'));
        }
    }

    /**
     * Handles an `update` AJAX action by calling the given update handler. When no update handler is given,
     * replaces the HTML content of the element with the new content.
     * @typeParam Component Type of the component which triggered the AJAX request.
     * @param node The XML node of the `update` action.
     * @param xhr The XHR request to which a response was received.
     * @param updateHandler Optional handler for the update.
     */
    doUpdate<Component extends BaseComponent>(node: Node, xhr: PrimeFaces.ajax.pfXHR, updateHandler?: PrimeFaces.ajax.UpdateHandler<Component> | null): void {
        if (!(node instanceof Element)) {
            return;
        }
        const id = node.getAttribute('id') ?? "";
        const content = ajax.Utils.getContent(node);

        const component = updateHandler ? updateHandler.component ?? updateHandler.widget : undefined;
        if (updateHandler && component && component.id === id) {
            updateHandler.handle.call(component, content);
        } else {
            ajax.Utils.updateElement(id, content, xhr);
        }
    }

    /**
     * Handles an `eval` AJAX action by evaluating the returned JavaScript.
     * @param node The XML node of the `eval` action.
     * @param xhr The XHR request to which a response was received.
     */
    doEval(node: Node, xhr?: PrimeFaces.ajax.pfXHR): void {
        const textContent = getNodeTextContent(node);

        let nonce: string | undefined;
        if (xhr && xhr.pfSettings && typeof xhr.pfSettings.nonce === "string") {
            nonce = xhr.pfSettings.nonce;
        }
        csp.eval(textContent, nonce);
    }

    /**
     * Handles an `extension` AJAX action by extending the `pfArgs` property on the jQuery XHR object.
     * @param node The XML node of the `extension` action.
     * @param xhr The XHR request to which a response was received.
     */
    doExtension(node: Node, xhr?: PrimeFaces.ajax.pfXHR): void {
        if (xhr && node instanceof Element) {
            if (node.getAttribute("ln") === "primefaces") {
                if (node.getAttribute("type") === "args") {
                    const textContent = getNodeTextContent(node);
                    // it's possible that pfArgs are already defined e.g. if Portlet parameter namespacing is enabled
                    // the "parameterPrefix" will be encoded on document start
                    // the other parameters will be encoded on document end
                    // --> see PrimePartialResponseWriter
                    if (xhr.pfArgs) {
                        const json = JSON.parse(textContent);
                        for (var name in json) {
                            xhr.pfArgs[name] = json[name];
                        }
                    }
                    else {
                        xhr.pfArgs = JSON.parse(textContent);
                    }
                }
                else if (node.getAttribute("type") === "script") {
                    xhr.pfScripts ||= [];
                    xhr.pfScripts.push(getNodeTextContent(node));
                }
            }
        }
    }

    /**
     * Handles an `error` AJAX action by doing nothing currently.
     * @param node The XML node of the `error` action.
     * @param xhr The XHR request to which a response was received.
     */
    doError(node: Node, xhr?: PrimeFaces.ajax.pfXHR): void {
        if (!(node instanceof Element)) {
            return;
        }
        var errorName = ajax.Utils.getContent(node.getElementsByTagName("error-name")[0]);
        var errorMessage = ajax.Utils.getContent(node.getElementsByTagName("error-message")[0]);

        ajax.Response.handleResponseError(errorName, errorMessage, xhr);
    }

    /**
     * Handles a `delete` AJAX action by remove the DOM element.
     * @param node The XML node of the `delete` action.
     */
    doDelete(node: Node): void {
        if (!(node instanceof Element)) {
            return;
        }
        var id = node.getAttribute('id') ?? "";
        $(core.escapeClientId(id)).remove();
    }

    /**
     * Handles an `insert` AJAX action by inserting a newly creating DOM element.
     * @param node The XML node of the `insert` action.
     * @return `false` if the AJAX action could not be performed, `true` or `undefined`
     * otherwise.
     */
    doInsert(node: Node): boolean | undefined {
        if (!node.childNodes) {
            return false;
        }

        for (const childNode of node.childNodes) {
            if (!(childNode instanceof Element)) {
                continue;
            }
            var id = childNode.getAttribute('id') ?? "";
            var jq = $(core.escapeClientId(id));
            var content = ajax.Utils.getContent(childNode);

            if (childNode.nodeName === "after") {
                $(content).insertAfter(jq);
            }
            else if (childNode.nodeName === "before") {
                $(content).insertBefore(jq);
            }
        }

        return undefined;
    }

    /**
     * Handles an `attributes` AJAX action by setting the attributes on the DOM element.
     * @param node The XML node of the `attributes` action.
     * @return `false` if the AJAX action could not be performed, `true` or `undefined`
     * otherwise.
     */
    doAttributes(node: Node): boolean | undefined {
        if (!node.childNodes || !(node instanceof Element)) {
            return false;
        }

        var id = node.getAttribute('id') ?? "";
        var jq = $(core.escapeClientId(id));

        for (const attrNode of node.childNodes) {
            if (!(attrNode instanceof Element)) {
                continue;
            }
            const attrName = attrNode.getAttribute("name");
            let attrValue = attrNode.getAttribute("value");

            if (!attrName) {
                return;
            }

            if (!attrValue || attrValue === null) {
                attrValue = "";
            }

            jq.attr(attrName, attrValue);
        }

        return undefined;
    }
}
