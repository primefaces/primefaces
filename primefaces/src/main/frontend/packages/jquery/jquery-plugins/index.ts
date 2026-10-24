/// <reference types="rangyinputs" preserve="true" />
/// <reference types="jquery-mousewheel" preserve="true" />
/// <reference path="./src/jquery.caretposition.ts" preserve="true" />

import "./src/jquery.caretposition.js";
import setupMousewheel from "jquery-mousewheel";
import "rangyinputs";
import "./src/jquery.pfextensions.js";
// side effect import, so the declarations keep the jQuery plugin types
import "./src/interaction/plugins.js";
import { registerInteractions } from "./src/interaction/plugins.js";

// JQuery extension made by PrimeFaces
declare global {
    interface JQuery {
        /**
         * Focuses the elements after the given delay.
         * @param delay Delay in milliseconds.
         * @param callback Called after the elements were focused.
         * @return this for chaining.
         */
        focus(delay: number, callback?: (this: HTMLElement) => void): this;

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

setupMousewheel($);
registerInteractions();
