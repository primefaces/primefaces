import Cookies from "js-cookie";

import { PF as _PF, core, type Core as CoreImpl } from "./src/core/core.js";
import { globalAjaxSetup } from "./src/core/core.ajax.js";
import { globalUtilsSetup } from "./src/core/core.utils.js";
import { globalEffectsSetup } from "./src/core/core.effects.js";
import { globalJQueryExtensionsSetup } from "./src/jquery-extensions/jquery-extensions.js";
// global PrimeFaces declarations of the position options
import "./src/core/core.position.js";
// global declarations of the jQuery extensions $.fn.focus(delay) and $.fn.zIndex()
import "./src/jquery-extensions/jquery.focus.js";
import "./src/jquery-extensions/jquery.zindex.js";

import type { AjaxExceptionHandler } from "./src/ajaxexceptionhandler/ajaxexceptionhandler.js";
import type { AjaxStatus } from "./src/ajaxstatus/ajaxstatus.js";
import type { BaseComponent, DeferredComponent, DynamicOverlayComponent } from "./src/core/core.component.js";
import type { Poll } from "./src/poll/poll.js";

import { registerKillSwitchHookForPoll } from "./src/poll/poll.js";
import { registerCommonConverters } from "./src/validation/validation.converters.js";
import { registerCommonValidationMessages } from "./src/validation/validation.common.js";
import { registerCommonValidators } from "./src/validation/validation.validators.js";
import { registerCommonHighlighters } from "./src/validation/validation.highlighters.js";
import { registerBeanValidationMessages, registerBeanValidationValidators } from "./src/validation/validation.bv.js";

exposeToGlobalScope();

function exposeToGlobalScope() {
    // Do nothing if the core script was already loaded
    if ("PrimeFaces" in window) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    // the jQuery extensions first, the rest of PrimeFaces uses them (e.g. the remove event to destroy components)
    globalJQueryExtensionsSetup();

    // Expose js-cookie to the global scope
    Object.assign(window, {
        Cookies,
        PrimeFaces: core,
        PF: _PF,
    });

    // Seems unused, but define this for now for compatibility
    // TODO Can this be removed?
    Object.assign(core, { util: {} });

    // Register additional components
    registerCommonConverters();
    registerCommonHighlighters();
    registerCommonValidators();
    registerBeanValidationValidators();

    registerCommonValidationMessages();
    registerBeanValidationMessages();

    // Register additional features
    registerKillSwitchHookForPoll();

    // Global setup
    globalAjaxSetup();
    globalUtilsSetup();
    globalEffectsSetup();
}

// Re-exports
declare global {
    namespace PrimeFaces {
        /**
         * The class with functionality related to multiple window support in PrimeFaces applications.
         */
        export type ClientWindow = import("./src/core/core.clientwindow.js").ClientWindow;
        /**
         * The class with functionality related to handling the `script-src` directive of the HTTP `Content-Security-Policy`
         * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
         * time it transmits a policy. 
         */
        export type Csp = import("./src/core/core.csp.js").Csp;
        /**
         * The class with functionality related to the browser environment, such as information about the current browser.
         */
        export type Environment = import("./src/core/core.env.js").Environment;
        /**
         * The class with functionality related to working with search expressions.
         */
        export type Expressions = import("./src/core/core.expressions.js").Expressions;
        /**
         * The class with functionality related to handling resources on the server, such as CSS and JavaScript files.
         */
        export type Resources = import("./src/core/core.resources.js").Resources;
        /**
         * The class with various utilities needed by PrimeFaces.
         */
        export type Utils = import("./src/core/core.utils.js").Utils;
        /**
         * __PrimeFaces Client Side Validation Framework__
         * 
         * The class for enabling client side validation of form fields.
         */
        export type Validation = import("./src/validation/validation.common.js").Validation;
    }

    namespace PrimeFaces.ajax {
        /**
         * The class with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
         * request receives an XML response, which consists of one or multiple actions that are to be performed. This
         * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
         */
        export type Ajax = import("./src/core/core.ajax.js").Ajax;
        /**
         * This class contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
         * proper order and (b) that each response is processed in the same order as the requests were sent.
         */
        export type AjaxQueue = import("./src/core/core.ajax.queue.js").AjaxQueue;
        /**
         * The class containing low-level functionality related to sending AJAX requests.
         */
        export type AjaxRequest = import("./src/core/core.ajax.request.js").AjaxRequest;
        /**
         * The class containing low-level functionality related to handling AJAX responses. Note that
         * the different types of AJAX actions are handled by the {@link AjaxResponseProcessor PrimeFaces.ajax.ResponseProcessor}.
         */
        export type AjaxResponse = import("./src/core/core.ajax.response.js").AjaxResponse;
        /**
         * The class containing low-level functionality related to processing the different types
         * of actions from AJAX responses.
         */
        export type AjaxResponseProcessor = import("./src/core/core.ajax.responseprocessor.js").AjaxResponseProcessor;
        /**
         * The class containing utility methods for AJAX requests, primarily used internally.
         */
        export type AjaxUtils = import("./src/core/core.ajax.utils.js").AjaxUtils;
    }

    namespace PrimeFaces.expressions {
        /**
         * The class providing the entry point for functions related to search expressions. 
         */
        export type SearchExpressionFacade = import("./src/core/core.expressions.js").SearchExpressionFacade;
    }

    namespace PrimeFaces.validation {
        /**
         * The class that contains functionality related to handling faces messages, especially validation errror messages.
         * Contains methods for clearing message of an element or adding messages to an element.
         */
        export type ValidationContext = import("./src/validation/validation.common.js").ValidationContext;
        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
         * responsible for changing the visual state of an element so that the user notices the invalid element.
         */
        export type ValidationHighlighter = import("./src/validation/validation.highlighters.js").ValidationHighlighter;
        /**
         * The class with mostly internal utility methods used to validate data on the client.
         */
        export type ValidationUtils = import("./src/validation/validation.common.js").ValidationUtils;
    }
}

// Module augmentation
declare global {
    // Extend PrimeFaces namespace with core methods and properties
    namespace PrimeFaces {
        export interface Core extends CoreImpl { }
    }

    // Extend Window global
    namespace PrimeFaces {
        export interface WindowExtensions {
            Cookies: typeof Cookies;
            PrimeFaces: Core;
            PF: typeof _PF;
        }
    }

    // Extend component registry (available component types)
    namespace PrimeFaces {
        export interface ComponentRegistry {
            AjaxExceptionHandler: typeof AjaxExceptionHandler;
            AjaxStatus: typeof AjaxStatus;
            BaseComponent: typeof BaseComponent;
            DeferredComponent: typeof DeferredComponent;
            DynamicOverlayComponent: typeof DynamicOverlayComponent;
            Poll: typeof Poll;
            /** @deprecated Use {@link BaseComponent} instead, both names point to the same class. */
            BaseWidget: typeof BaseComponent;
            /** @deprecated Use {@link DeferredComponent} instead, both names point to the same class. */
            DeferredWidget: typeof DeferredComponent;
            /** @deprecated Use {@link DynamicOverlayComponent} instead, both names point to the same class. */
            DynamicOverlayWidget: typeof DynamicOverlayComponent;
        }
    }

    // Extend validators (common)
    namespace PrimeFaces.validation {
        interface ValidatorInstanceMap {
            "jakarta.faces.Length"?: Validator;
            "jakarta.faces.LongRange"?: Validator;
            "jakarta.faces.DoubleRange"?: Validator;
            "jakarta.faces.RegularExpression"?: Validator;
            "primefaces.File"?: Validator;
        }
    }

    // Extend validators (bean validation)
    namespace PrimeFaces.validation {
        interface ValidatorInstanceMap {
            "AssertFalse"?: Validator;
            "AssertTrue"?: Validator;
            "DecimalMax"?: Validator;
            "DecimalMin"?: Validator;
            "Digits"?: Validator;
            "Email"?: Validator;
            "Future"?: Validator;
            "FutureOrPresent"?: Validator;
            "Max"?: Validator;
            "Min"?: Validator;
            "Negative"?: Validator;
            "NegativeOrZero"?: Validator;
            "NotBlank"?: Validator;
            "NotEmpty"?: Validator;
            "NotNull"?: Validator;
            "Null"?: Validator;
            "Past"?: Validator;
            "PastOrPresent"?: Validator;
            "Pattern"?: Validator;
            "Positive"?: Validator;
            "PositiveOrZero"?: Validator;
            "Size"?: Validator;
        }
    }

    // Extend component configurations
    namespace PrimeFaces.component {
        export type BaseComponentCfg = import("./src/core/core.component.js").BaseComponentCfg;
        export type DeferredComponentCfg = import("./src/core/core.component.js").DeferredComponentCfg;
        export type DynamicOverlayComponentCfg = import("./src/core/core.component.js").DynamicOverlayComponentCfg;
    }

    // Extend globals
    let PrimeFaces: PrimeFaces.Core;
    let PF: typeof _PF;
}
