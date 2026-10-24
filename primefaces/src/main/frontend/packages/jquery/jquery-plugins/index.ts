/// <reference types="jquery-mousewheel" preserve="true" />

import setupMousewheel from "jquery-mousewheel";
import "./src/jquery.pfextensions.js";

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
