if (!PrimeFaces.csp) {

    /**
     * The object with functionality related to handling the `script-src` directive of the HTTP Content-Security-Policy
     * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
     * time it transmits a policy. 
     * @namespace
     */
    PrimeFaces.csp = {

        /**
         * Name of the POST parameter for transmitting the nonce.
         * @type {string}
         * @readonly
         */
        NONCE_INPUT : "primefaces.nonce",

        /**
         * The value of the nonce to be used.
         * @type {string}
         */
        NONCE_VALUE : "",

        /**
         * Sets the given nonce to all forms on the current page.
         * @param {string} nonce Nonce to set. This value is usually supplied by the server.
         */
        init : function(nonce) {
            PrimeFaces.csp.NONCE_VALUE = nonce;

            var forms = document.getElementsByTagName("form");
            for (var i = 0; i < forms.length; i++) {
                var form = forms[i];
                var input = form.elements[PrimeFaces.csp.NONCE_INPUT];
                if (!input) {
                    input = document.createElement("input");
                    input.setAttribute("name", PrimeFaces.csp.NONCE_INPUT);
                    input.setAttribute("type", "hidden");
                    form.appendChild(input);
                }
                input.setAttribute("value", nonce);
            }
        },

        /**
         * Registers an event listener for the given element.
         * @param {string} id ID of an element
         * @param {string} [event] Event to listen to, with the `on` prefix, such as `onclick` or `onblur`.
         * @param {() => boolean} [js] Callback that may return `false` to prevent the default behavior of the event.
         */
        register: function(id, event, js){
            if (event) {
                var shortenedEvent = event.substring(2, event.length),
                    element = document.getElementById(id),
                    jqEvent = shortenedEvent + '.' + id;

                // if the eventhandler return false, we must use preventDefault
                var jsWrapper = function(event) {
                    var retVal = js.call(element, event);
                    if (retVal === false && event.cancelable) {
                        event.preventDefault();
                    }
                };

                $(element).off(jqEvent).on(jqEvent, jsWrapper);
            }
        },

        /**
         * Perform a CSP safe `eval()`.
         *
         * @param {string} js The JavaScript code to evaluate.
         * @param {string} [nonceValue] Nonce value. Leave out if not using CSP.
         */
        eval: function (js, nonceValue) {
            // assign the NONCE if necessary
            var options = {};
            if (nonceValue) {
                options = {nonce: nonceValue};
            } else if (PrimeFaces.csp.NONCE_VALUE) {
                options = {nonce: PrimeFaces.csp.NONCE_VALUE};
            }

            // evaluate the script
            $.globalEval(js, options);
        },
        
        /**
         * Perform a CSP safe `eval()` with a return result value.
         *
         * @param {string} js The JavaScript code to evaluate.
         * @return {unknown} The result of the evaluated JavaScript code.
         * @see https://stackoverflow.com/a/33945236/502366
         */
        evalResult: function (js) {
            var executeJs = "var cspResult = " + js;
            PrimeFaces.csp.eval(executeJs);
            return cspResult;
        },

        /**
         * CSP won't allow string-to-JavaScript methods like eval() and new Function().
         * This method uses JQuery globalEval to safely evaluate the function if CSP enabled.
         *
         * @param {any} id The element executing the function (aka `this`).
         * @param {string} js The JavaScript code to evaluate. Two variables will be in scope for the code: (a) the
         * `this` context, which is set to the given `id`, and (b) the `event` variable, which is set to the given `e`.
         * @param {JQuery.TriggeredEvent} e The event from the caller to pass through.
         */
        executeEvent: function(id, js, e) {
            // create the wrapper function
            var scriptEval = 'var cspFunction = function(event){'+ js +'}';

            // evaluate JS into a function
            PrimeFaces.csp.eval(scriptEval, PrimeFaces.csp.NONCE_VALUE);

            // call the function
            cspFunction.call(id, e);
        },

        /**
         * GitHub #5790: When using jQuery to trigger a click event on a button while using CSP
         * we must set preventDefault or else it will trigger a non-ajax button click.
         * 
         * @return {JQuery.TriggeredEvent} the JQuery click event
         */
        clickEvent: function() {
            var clickEvent = $.Event( "click" );
            if (PrimeFaces.csp.NONCE_VALUE) {
                clickEvent.preventDefault();
            }
            return clickEvent;
        }

    };

};