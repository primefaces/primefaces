if (!PrimeFaces.csp) {

    PrimeFaces.csp = {

        NONCE_INPUT : "primefaces.nonce",
        NONCE_VALUE : "",

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

        register: function(id, event, js){
            if (event) {
                var shortenedEvent = event.substring(2, event.length);

                var element = document.getElementById(id);

                // if the eventhandler return false, we must use preventDefault
                var jsWrapper = function(event) {
                    var retVal = js.call(element, event);
                    if (retVal === false && event.cancelable) {
                        event.preventDefault();
                    }
                };

                $(element).on(shortenedEvent, jsWrapper);
            }
        },

        /**
         * Perform a CSP safe eval().
         *
         * @param js the Javascript to evaluate
         * @param nonceValue nonce value or null if not using CSP
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
         * CSP won't allow  string-to-JavaScript methods like eval() and new Function().
         * This method uses JQuery globalEval to safely evaluate the function if CSP enabled.
         *
         * @param id the element executing the function (aka this)
         * @param js the Javascript to evaluate
         * @param e the event from the caller to pass through
         */
        executeEvent: function(id, js, e) {
            // create the wrapper function
            var scriptEval = 'var cspFunction = function(event){'+ js +'}';

            // evaluate JS into a function
            PrimeFaces.csp.eval(scriptEval, PrimeFaces.csp.NONCE_VALUE);

            // call the function
            cspFunction.call(id, e);
        }

    };

};