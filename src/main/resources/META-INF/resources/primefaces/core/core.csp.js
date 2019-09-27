if (!PrimeFaces.csp) {

    PrimeFaces.csp = {

        NONCE_INPUT : "primefaces.nonce",
        TRIGGERS_HANDLED: false,

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

            // bind native DOM event handlers
            if (!PrimeFaces.csp.TRIGGERS_HANDLED) {
                PrimeFaces.csp.bindTriggerHandler();
                PrimeFaces.csp.TRIGGERS_HANDLED = true;
            }
        },

        /**
         * First look for native Jquery events and call .trigger(). If no Jquery handler is found
         * call the native DOM event handler.
         * 
         * @see https://stackoverflow.com/questions/21290775/jquery-el-triggerchange-doesnt-fire-native-listeners
         */
        bindTriggerHandler: function() {
            // detect if an jquery object event has events attached
            $.fn.hasHandlers = function(events,selector) {
                var result = false;

                this.each(function (i) {
                    var elem = this;
                    dEvents = $._data($(this).get(0), "events");
                    if (!dEvents) {
                        return false;
                    }
                    $.each(dEvents, function (name, handler) {
                        if (events === name) {
                            result = true;
                        }
                    });
                });
                return result;
             };

             // store off original jquery trigger handler
             $.fn.triggerJquery = $.fn.trigger;

             // trigger native DOM events 
             $.fn.triggerNative = function(eventName) {
                 return this.each(function() {
                     var $this = $(this);
                     var el = $this.get(0);
                     var evt = document.createEvent('Events');
                     evt.initEvent(eventName, true, false);
                     el.dispatchEvent(evt);
                 });
             };

             // override native Jquery trigger and call DOM event if no Jquery events found
             $.fn.trigger = function(eventName) {
                 return this.each(function() {
                     var $this = $(this);
                     if ($this.hasHandlers(eventName)) {
                         $this.triggerJquery(eventName);
                     } else {
                         $this.triggerNative(eventName);
                     }
                 });
             };
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