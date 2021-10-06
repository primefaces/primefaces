window.pfselenium = {
    navigating : false,
    submitting : false,
    xhr: 0,
    anyXhrStarted : false
};

var originalSend = XMLHttpRequest.prototype.send;
XMLHttpRequest.prototype.send = function() {
    window.pfselenium.xhr = window.pfselenium.xhr + 1;
    window.pfselenium.anyXhrStarted = true;

    var listener = function() {
        window.pfselenium.xhr = window.pfselenium.xhr - 1;
    };
    this.addEventListener("load", listener);
    this.addEventListener("error", listener);
    this.addEventListener("abort", listener);
    this.addEventListener("timeout", listener);

    originalSend.apply(this, arguments);
};

var originalSubmit = HTMLFormElement.prototype.submit;
HTMLFormElement.prototype.submit = function() {
    window.pfselenium.submitting = true;

    var submitToCurrentWindow = !this.target || this.target === '' || this.target === '_self' || this.target === window.name;

    originalSubmit.apply(this, arguments);

    // in case the form was submmited to another tab -> "release" the current window from the wait guard
    if (!submitToCurrentWindow) {
        window.pfselenium.submitting = false;
    }
};


// try to listen on navigation, which can happen inside AJAX requests
window.addEventListener("beforeunload", function() {
    window.pfselenium.navigating = true;
    window.pfselenium.submitting = false;
});
window.addEventListener("unload", function() {
    window.pfselenium.navigating = true;
    window.pfselenium.submitting = false;
});
