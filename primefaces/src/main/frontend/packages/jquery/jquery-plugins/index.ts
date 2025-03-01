/// <reference types="rangyinputs" preserve="true" />
/// <reference types="jqueryui" preserve="true" />
/// <reference types="jquery-mousewheel" preserve="true" />
/// <reference path="./src/jquery.caretposition.ts" preserve="true" />

import "./src/jquery.ui.js";
import "./src/jquery.caretposition.js";
import setupMousewheel from "jquery-mousewheel";
import "rangyinputs";
import "@rwap/jquery-ui-touch-punch";
import "./src/jquery.ui.pfextensions.js";

// JQuery extension made by PrimeFaces
declare global {
    interface JQuery {
        /**
         * Prevents the user from selection any text of this element.
         * @return this for chaining.
         */
        disableSelection(): this;
    
        /**
         * Allows the user to select text of this element again.
         * @return this for chaining.
         */
        enableSelection(): this;
    
        /**
         * Gets the z-index of this element.
         * @return The z-index of this element.
         */
        zIndex(): number;
    
        /**
         * Sets the z-index of this element the given value.
         * @param zIndex The z-index to set.
         * @return The z-index of this element.
         */
        zIndex(zIndex: number): this;
    }
}

// TODO: remove once https://github.com/DefinitelyTyped/DefinitelyTyped/pull/72074 was merged (and update @types/jqueryui)
declare global {
    namespace JQueryUI {
        /**
         * Options for the transfer JQuery plugin.
         */
        export interface TransferOptions {
            /**
             * The target of the transfer effect.
             */
            to: string | Element | ArrayLike<Element>;
            /**
             * A class to add to the transfer element, in addition to `ui-effects-transfer`.
             */
            className?: string | null;
            /**
             * A string or number determining how long the animation will run. The strings "fast" and "slow" can be supplied to indicate durations of 200 and 600 milliseconds, respectively. The number type indicates the duration in milliseconds.
             * @default 400
             */
            duration?: string | number;
            /**
             * A string indicating which easing function to use for the transition.
             * @default "swing"
             */
            easing?: string;
        }
    }
    interface JQuery<TElement = HTMLElement> {
        /**
         * Transfers the outline of an element to another element.
         * 
         * Very useful when trying to visualize interaction between two elements.
         * 
         * The transfer element itself has the class ui-effects-transfer, and needs to be styled by you, for example by
         * adding a background or border.
         * 
         * See https://api.jqueryui.com/transfer/
         * 
         * @param options Options for the transfer operation.
         * @param complete A function to call once the animation is complete, called once per matched element.
         * @returns This JQuery instance for chaining method calls.
         */
        transfer(options: JQueryUI.TransferOptions, complete?: () => void): this;
    }
}

setupMousewheel($);
