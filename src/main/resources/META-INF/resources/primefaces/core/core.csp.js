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
         * Sets the given nonce to all forms on the current page.
         * @param {string} nonce Nonce to set. This value is usually supplied by the server.
         */
        init : function(nonce) {
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
                event = event.substring(2, event.length);

                // if the eventhandler return false, we must use preventDefault
                var jsWrapper = function(event) {
                    var retVal = js();
                    if (retVal === false && event.cancelable) {
                        event.preventDefault();
                    }
                };

                document.getElementById(id).addEventListener(event, jsWrapper);
            }
        }

    };

};