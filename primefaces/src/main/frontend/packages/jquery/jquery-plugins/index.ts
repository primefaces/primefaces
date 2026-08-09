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

setupMousewheel($);
