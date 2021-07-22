if (PrimeFaces.csp) {
    PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS = new Map();

    var cspRegisterOrig = PrimeFaces.csp.register;
    PrimeFaces.csp.register = function(id, event, js){
        if (event) {
            cspRegisterOrig(id, event, js);

            //Collect some basic information about registered ajaxified event listeners
            if (!PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS.has(id)) {
                PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS.set(id, new Map())
            }

            var shortenedEvent = event.substring(2, event.length),
                jqEvent = shortenedEvent + '.' + id;

            var jsAsString = js.toString();
            var ajaxified = (jsAsString.indexOf("PrimeFaces.ab(") >= 0) || (jsAsString.indexOf("pf.ab(") >= 0) || (jsAsString.indexOf("mojarra.ab(") >= 0) || (jsAsString.indexOf("jsf.ajax.request") >= 0);
            PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS.get(id).set(jqEvent, ajaxified);
        }
    };

    PrimeFaces.csp.hasRegisteredAjaxifiedEvent = function(id, event) {
        if (PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS.has(id)) {
            var shortenedEvent = event.substring(2, event.length),
                jqEvent = shortenedEvent + '.' + id;

            return PrimeFaces.csp.REGISTERED_AJAXIFIED_EVENT_LISTENERS.get(id).get(jqEvent);
        }
        return false;
    };
}
