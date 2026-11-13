import { expressions } from "./core.expressions.js";
import { core } from "./core.js";
import { utils } from "./core.utils.js";
import type { BaseComponent } from "./core.component.js";
import { ajax } from "./core.ajax.js";

import { AjaxExceptionHandler } from "../ajaxexceptionhandler/ajaxexceptionhandler.js";

/**
 * The class containing utility methods for AJAX requests, primarily used internally.
 */
export class AjaxUtils {
    /**
     * Iterates over all immediate children of the given node and returns the concatenated content (`node value`)
     * of each such child node. For the document itself, the node value is `null`.
     * For text, comment, and CDATA nodes, the `node value` is the (text) content of the node.
     * For attribute nodes, the value of the attribute is used.
     * @param node An element for which to retrieve the content.
     * @return The content of all immediate child nodes, concatenated together.
     */
    getContent(node: Element | undefined | null): string {
        var content = '';

        if (node) {
            for (const child of node.childNodes) {
                content += child.nodeValue;
            }
        }

        return content;
    }

    /**
     * Checks if the given form is a Faces form.
     * @param form The form to check.
     * @return `true` if the form is a Faces form.
     */
    isFacesForm(form: HTMLFormElement): boolean {
        if (form.method === 'post') {
            for (let child of form.children) {
                if (child instanceof HTMLInputElement && child.name && child.name.includes(core.VIEW_STATE)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resolves the URL which should be used for the POST request.
     * For portlets, a different URL is used.
     *
     * @param form The closest form of the request source.
     * @return The POST url.
     */
    getPostUrl(form: JQuery): string {
        let postURL: string | string[] | number | undefined = form.attr('action');
        let encodedURLInput = form.children("input[name*='jakarta.faces.encodedURL']");

        if (encodedURLInput.length > 0) {
            postURL = encodedURLInput.val();
        }

        return typeof postURL === "string" ? postURL : "";
    }

    /**
     * Gets a selector to resolve all forms which needs to be updated with a new ViewState.
     * This is required in portlets as the DOM contains forms of multiple Faces views / applications.
     *
     * @param form The closest form of the request source.
     * @param parameterPrefix The portlet parameter prefix.
     * @return The selector for the forms, or `null` when no forms need to be updated.
     */
    getPorletForms(form: JQuery, parameterPrefix: string): string | null {
        const encodedURLInput = form.children("input[name*='jakarta.faces.encodedURL']");

        if (encodedURLInput.length > 0) {
            return 'form[id*="' + parameterPrefix + '"]';
        }

        return null;
    }

    /**
     * Get source ID from settings.
     *
     * @param settings containing source ID.
     * @return The source ID from settings or `null` if settings does not contain a source.
     */
    getSourceId(settings: PrimeFaces.ajax.PrimeFacesSettings): string | null {
        if (settings && settings.source) {
            if (typeof settings.source === 'string') {
                return settings.source;
            }
            if (settings.source instanceof Element) {
                return settings.source.getAttribute("name");
            }
            return settings.source.attr("name") ?? null;
        }
        return null;
    }

    /**
     * Checks whether the component ID from the provided component equals the source ID from the provided
     * settings.
     *
     * @param component The component to check for being the source.
     * @param settings containing source ID.
     * @returns `true` if the component ID from the provided component equals the source ID from the
     * provided settings.
     */
    isXhrSource(component: BaseComponent, settings: JQuery.AjaxSettings): boolean {
        return component.id === ajax.Utils.getSourceId(settings);
    }

    /**
     * Checks whether one of component's triggers equals the source ID from the provided settings.
     *
     * @param component The component to check for being the source.
     * @param settings containing source ID.
     * @param triggerMustExist flag to check if the trigger must exist
     * @returns `true` if if one of component's triggers equals the source ID from the provided settings.
     */
    isXhrSourceATrigger(component: BaseComponent<any>, settings: JQuery.AjaxSettings, triggerMustExist: boolean): boolean {
        var sourceId = ajax.Utils.getSourceId(settings);
        if (!sourceId) {
            return false;
        }
        var cfgTrigger = component.cfg.trigger || component.cfg.triggers;
        // we must evaluate it each time as the DOM might has been changed
        var triggers = expressions.SearchExpressionFacade.resolveComponents(component.jq, cfgTrigger);

        // if trigger is null it has been removed from DOM so we need to hide the block UI
        if (!triggers || triggers.length === 0) {
            return !triggerMustExist;
        }

        return $.inArray(sourceId, triggers) !== -1;
    }

    /**
     * Is this script an AJAX request?
     * @param script the JS script to check
     * @returns `true` if this script contains an AJAX request
     */
    isAjaxRequest(script: string): boolean {
        return script.includes("PrimeFaces.ab(") || script.includes("pf.ab(")
            || script.includes("mojarra.ab(")
            || script.includes("myfaces.ab(")
            || script.includes("faces.ajax.request");
    }

    /**
     * Updates the main hidden input element for each form.
     * @param name Name of the hidden form input element, usually the same as the form.
     * @param value Value to set on the hidden input element.
     * @param xhr Optional XHR request with `pfSettings` or `pfArgs` with further
     * data, such as which forms should be updated.
     */
    updateFormStateInput(name: string, value: string, xhr?: PrimeFaces.ajax.pfXHR): void {
        var trimmedValue = core.trim(value);

        var forms = null;
        if (xhr && xhr.pfSettings && xhr.pfSettings.portletForms) {
            forms = $(xhr.pfSettings.portletForms);
        }
        else {
            forms = $('form');
        }

        let parameterPrefix = '';
        if (xhr && xhr.pfArgs && xhr.pfArgs.parameterPrefix) {
            parameterPrefix = typeof xhr.pfArgs.parameterPrefix === "string" ? xhr.pfArgs.parameterPrefix : "";
        }

        for (var i = 0; i < forms.length; i++) {
            var form = forms.eq(i);

            if (form.attr('method') === 'post') {
                var input = form.children("input[name='" + CSS.escape(parameterPrefix + name) + "']");

                if (input.length > 0) {
                    input.val(trimmedValue);
                } else {
                    form.append('<input type="hidden" name="' + parameterPrefix + name + '" value="' + trimmedValue + '"></input>');
                }
            }
        }
    }

    /**
     * Updates the HTML `head` element of the current document with the content received from an AJAX request.
     * This method ensures that any new JavaScript or CSS resources are only added if they are not already present.
     * If the content does not contain any JavaScript or CSS links, it is directly appended to the head.
     * 
     * @param {string} content The content of the changeset that was returned by an AJAX request.
     */
    updateResource(content: string): void {
        var $head = $("head");
        try {
            var $content = $(content);
            var filteredContent = $content.length > 0 ? $content.filter("link[href], script[src]") : $();

            if (filteredContent.length === 0) {
                core.debug("Adding content to the head because it lacks any JavaScript or CSS links...");
                $head.append(content);
            } else {
                // #11714 Iterate through each script and stylesheet tag in the content
                // checking if resource is already attached to the head and adding it if not
                for (const resource of filteredContent) {
                    var $resource = $(resource);
                    var src = $resource.attr("href") || $resource.attr("src");
                    var type = resource.tagName.toLowerCase();
                    var $resources = $head.find(type + '[src="' + src + '"], ' + type + '[href="' + src + '"]');

                    // Check if script or stylesheet already exists and add it to head if it does not
                    if ($resources.length === 0) {
                        core.debug("Appending " + type + " to head: " + src);
                        $head.append($resource);
                    }
                }
            }
        } catch (error) {
            // MYFACES-4378 is incorrectly sending executable code here in the Resource section
            core.debug("Appending content to the head as it contains only raw JavaScript code...");
            $head.append(content);
        }
    }

    /**
     * Updates the HTML `head` element of the current document with the content received from an AJAX request.
     * @param content The content of the changeset that was returned by an AJAX request.
     */
    updateHead(content: string): void {
        var cache = $.ajaxSetup({})['cache'];
        $.ajaxSetup({})['cache'] = true;

        const headStartTag = /<head[^>]*>/gi.exec(content)?.[0] ?? "";
        const headStartIndex = content.indexOf(headStartTag) + headStartTag.length;
        $('head').html(content.substring(headStartIndex, content.lastIndexOf("</head>")));

        $.ajaxSetup({})['cache'] = cache;
    }

    /**
     * Updates the HTML `body` element of the current document with the content received from an AJAX request.
     * @param content The content of the changeset that was returned by an AJAX request.
     */
    updateBody(content: string): void {
        const bodyStartTag = /<body[^>]*>/gi.exec(content)?.[0] ?? "";
        const bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
        $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
    }

    /**
     * Updates an element with the given ID by applying a change set that was returned by an AJAX request. This
     * involves replacing the HTML content of the element with the new content.
     * @param id ID of the element that is to be updated.
     * @param content The new content of the changeset as returned by an AJAX request.
     * @param xhr Optional XHR request with `pfSettings` or `pfArgs` with further
     * data, such as which forms should be updated.
     */
    updateElement(id: string, content: string, xhr?: PrimeFaces.ajax.pfXHR): void {

        if (id.indexOf(core.VIEW_STATE) !== -1) {
            ajax.Utils.updateFormStateInput(core.VIEW_STATE, content, xhr);
        }
        else if (id.indexOf(core.CLIENT_WINDOW) !== -1) {
            ajax.Utils.updateFormStateInput(core.CLIENT_WINDOW, content, xhr);
        }
        // used by @all
        else if (id === core.VIEW_ROOT) {

            // reset PrimeFaces JS state because the view is completely replaced with a new one
            core.resetState();

            ajax.Utils.updateHead(content);
            ajax.Utils.updateBody(content);
        }
        else if (id === ajax.VIEW_HEAD) {
            ajax.Utils.updateHead(content);
        }
        else if (id === ajax.VIEW_BODY) {
            ajax.Utils.updateBody(content);
        }
        else if (id === ajax.RESOURCE) {
            ajax.Utils.updateResource(content);
        }
        else if (id === $('head')[0]?.id) {
            ajax.Utils.updateHead(content);
        }
        else {
            var target = $(core.escapeClientId(id));
            if (target.length === 0) {
                core.warn("DOM element with id '" + id + "' cant be found; skip update...");
            }
            else {
                var removedContent = target.replaceWith(content);
                // detach all handlers and data to clean up DOM
                utils.cleanseDomElement(removedContent);
            }
        }
    }

    /**
     * Handle the error either by calling the p:ajaxExceptionHandlers, trying to redirect to the error-page or by logging.
     * @param errorName The error name.
     * @param errorMessage The error message.
     */
    handleError(errorName: string, errorMessage: string): void {
        // always resolve the handlers, otherwise the global AjaxExceptionHandler below can never be found
        const exceptionHandlers = core.getComponentsByType(AjaxExceptionHandler);

        if (errorName) {
            // try to invoke specific AjaxExceptionHandler
            for (var exceptionHandler of exceptionHandlers) {
                if (exceptionHandler.handles(errorName)) {
                    exceptionHandler.handle(errorName, errorMessage);
                    return;
                }
            }
        }

        // try to invoke global AjaxExceptionHandler
        var globalExceptionHandler = exceptionHandlers.find(component => component.isGlobal());
        if (globalExceptionHandler) {
            globalExceptionHandler.handle(errorName, errorMessage);
            return;
        }

        if (core.settings.errorPages) {
            var errorPageUrl = null;
            if (errorName) {
                // strip off 'class ' prefix, which is logged with exception class
                if (errorName.startsWith('class ')) {
                    errorPageUrl = core.settings.errorPages[errorName.replace('class ', '')];
                }
                else {
                    errorPageUrl = core.settings.errorPages[errorName];
                }
            }
            if (!errorPageUrl) {
                errorPageUrl = core.settings.errorPages['java.lang.Throwable'];
            }
            if (!errorPageUrl) {
                errorPageUrl = core.settings.errorPages[''];
            }

            if (errorPageUrl) {
                try {
                    core.debug("Redirect to error page: " + errorPageUrl)
                    window.location.assign(errorPageUrl);
                } catch (error) {
                    core.warn('Error redirecting to URL: ' + errorPageUrl);
                }

                return;
            }
        }

        core.error("No ajaxExceptionHandler or error page found for '" + errorName + "' defined!");
        if (errorMessage) {
            core.error(errorMessage);
        }
    }
}
