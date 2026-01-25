/// <reference types="jquery" preserve="true" />

import jQuery from "jquery";
import "../jquery-plugins/src/jquery.browser.js";

declare global {
    interface Window {
        $: typeof jQuery;
        jQuery: typeof jQuery;
    }
    namespace JQuery {
        export interface GlobalEvalOptions {
            /**
             * The nonce attribute passed to the executed script, see
             * {@link HTMLScriptElement.nonce}.
             */
            nonce?: string;
        }
    }

    // These types should be in @types/jquery, but they aren't
    interface JQueryStatic {
        /**
         * Execute some JavaScript code globally.
         * @param code The JavaScript code to execute.
         * @param options Options for the evaluation.
         */
        globalEval(code: string, options: JQuery.GlobalEvalOptions): void;
        /**
         * Execute some JavaScript code globally.
         * @param code The JavaScript code to execute.
         * @param options Options for the evaluation.
         * @param doc A document in which context the code will be evaluated.
         */
        globalEval(code: string, options: JQuery.GlobalEvalOptions, doc: Document): void;
    }
}

Object.assign(window, { $: jQuery, jQuery });