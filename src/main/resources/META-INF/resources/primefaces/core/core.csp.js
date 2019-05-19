if (!PrimeFaces.csp) {

    PrimeFaces.csp = {

        NONCE_INPUT : "primefaces.nonce",

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