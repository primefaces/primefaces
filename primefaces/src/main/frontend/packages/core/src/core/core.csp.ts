import { core } from "./core.js";
import { ajax } from "./core.ajax.js";

// Temporary global variables for the "eval" functions that add a global
// script element to the head / body.
declare global {
    interface Window {
        cspResult?: unknown;
    }
    let cspResult: unknown;
    let cspFunction: ((this: HTMLElement, event: JQuery.TriggeredEvent) => void) | undefined;
}

/**
 * The class with functionality related to handling the `script-src` directive of the HTTP `Content-Security-Policy`
 * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
 * time it transmits a policy. 
 */
export class Csp {
    /**
     * Name of the POST parameter for transmitting the nonce.
     */
    readonly NONCE_INPUT: string = "primefaces.nonce";

    /**
     * The value of the nonce to be used.
     */
    NONCE_VALUE: string = "";

    /**
     * Map of currently registered CSP events on this page.
     */
    readonly EVENT_REGISTRY: Map<string,Map<string,boolean>> = new Map();

    /**
     * Sets the given nonce to all forms on the current page.
     * @param nonce Nonce to set. This value is usually supplied by the server.
     */
    init(nonce: string): void {
        this.NONCE_VALUE = nonce;

        var forms = document.getElementsByTagName("form");
        for (const form of forms) {
            if (!this.isFacesForm(form)) {
                continue;
            }

            var input = form.elements.namedItem(this.NONCE_INPUT);
            if (!input) {
                input = document.createElement("input");
                input.setAttribute("name", this.NONCE_INPUT);
                input.setAttribute("type", "hidden");
                form.appendChild(input);
            }
            if (input instanceof Element) {
                input.setAttribute("value", nonce);
            }
        }
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
     * Registers an event listener for the given element.
     * @param id ID of an element
     * @param event Event to listen to, with the `on` prefix, such as `onclick` or `onblur`.
     * @param js Callback that may return `false` to prevent the default behavior of the event.
     */
    register(id: string, event: string | undefined, js: (event: JQuery.TriggeredEvent) => boolean): void {
        if (event) {
            const shortenedEvent = event.substring(2, event.length);
            let element: Window | HTMLElement | null = document.getElementById(id);
            const jqEvent = shortenedEvent + '.' + id;
            const isAjaxified = ajax.Utils.isAjaxRequest(js.toString());

            // if the event handler returns false, we must use preventDefault
            const jsWrapper = (event: JQuery.TriggeredEvent) => {
                const retVal = js.call(element, event);
                if (retVal === false && (typeof event.cancelable !== 'boolean' || event.cancelable)) {
                    event.preventDefault();
                }
            };

            // #9002 body onload rewrite as window onload
            if (event === 'onload' && element instanceof HTMLBodyElement) {
                element = window;
            }

            if (element !== null) {
                $(element).off(jqEvent)
                    .on(jqEvent, jsWrapper)
                    .attr('data-ajax', String(isAjaxified));
            }

            //Collect some basic information about registered AJAXified event listeners
            if (!core.isProductionProjectStage()) {
                if (!this.EVENT_REGISTRY.has(id)) {
                    this.EVENT_REGISTRY.set(id, new Map());
                }
                this.EVENT_REGISTRY.get(id)?.set(jqEvent, isAjaxified);
            }
        }
    }

    /**
     * Does this component have a registered AJAX event.
     * @param id ID of an element
     * @param event Event to listen to, with the `on` prefix, such as `onclick` or `onblur`.
     * @return `true` if component has this AJAX event
     */
    hasRegisteredAjaxifiedEvent(id: string, event: string): boolean | undefined {
        if (core.isProductionProjectStage()) {
            console.error("PrimeFaces CSP registry may not be used in Faces Production mode.");
            return false;
        }
        if (this.EVENT_REGISTRY.has(id)) {
            const shortenedEvent = event.substring(2, event.length);
            const jqEvent = shortenedEvent + '.' + id;
            return this.EVENT_REGISTRY.get(id)?.get(jqEvent);
        }
        return false;
    }

    /**
     * Perform a CSP safe `eval()`.
     *
     * @param js The JavaScript code to evaluate.
     * @param nonceValue Nonce value. Leave out if not using CSP.
     * @param windowContext Optional Window context to call eval from.
     */
    eval(js: string, nonceValue?: string, windowContext?: Window): void {
        // assign the NONCE if necessary
        var options = {};
        if (nonceValue) {
            options = {nonce: nonceValue};
        } else if (this.NONCE_VALUE) {
            if (windowContext) {
                options = {nonce: windowContext.PrimeFaces.csp.NONCE_VALUE};
            } else {
                options = {nonce: this.NONCE_VALUE};
            }
        }

        // evaluate the script
        if (windowContext) {
            $.globalEval(js, options, windowContext.document);
        } else {
            $.globalEval(js, options);
        }
    }
    
    /**
     * Perform a CSP safe `eval()` with a return result value.
     *
     * @param js The JavaScript code to evaluate.
     * @param nonceValue Nonce value. Omit if not using CSP.
     * @param windowContext Optional Window context to call eval from.
     * @return The result of the evaluated JavaScript code.
     * @see https://stackoverflow.com/a/33945236/502366
     */
    evalResult(js: string, nonceValue?: string, windowContext?: Window): unknown {
        var executeJs = "var cspResult = " + js;
        this.eval(executeJs, nonceValue, windowContext);
        return windowContext ? windowContext.cspResult : cspResult;
    }

    /**
     * CSP won't allow string-to-JavaScript methods like `eval()` and `new Function()`.
     * This method uses JQuery `globalEval` to safely evaluate the function if CSP is enabled.
     *
     * @param id The element executing the function (aka `this`).
     * @param js The JavaScript code to evaluate. Two variables will be in scope for the code: (a) the
     * `this` context, which is set to the given `id`, and (b) the `event` variable, which is set to the given `e`.
     * @param e The event from the caller to pass through.
     */
    executeEvent(id: HTMLElement, js: string, e: JQuery.TriggeredEvent): void {
        // create the wrapper function
        const scriptEval = 'var cspFunction = function(event){'+ js +'}';

        // evaluate JS into a function
        this.eval(scriptEval, this.NONCE_VALUE);

        // call the function
        cspFunction?.call(id, e);
    }

    /**
     * GitHub #5790: When using jQuery to trigger a click event on a button while using CSP
     * we must set preventDefault or else it will trigger a non-ajax button click.
     * 
     * @param target The target of this click event.
     * @return The JQuery click event
     */
    clickEvent(target: JQuery): JQuery.Event {
        const clickEvent = $.Event('click');
        if (this.NONCE_VALUE && target.attr('data-ajax') !== 'false') {
            clickEvent.preventDefault();
        }
        return clickEvent;
    }
}

/**
 * The object with functionality related to handling the `script-src` directive of the HTTP `Content-Security-Policy`
 * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
 * time it transmits a policy. 
 */
export const csp: Csp = new Csp();