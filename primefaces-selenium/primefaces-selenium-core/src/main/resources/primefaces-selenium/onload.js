window.pfselenium = {
    navigating : false,
    submitting : false,
    xhr : null
};

var originalSend = XMLHttpRequest.prototype.send;
XMLHttpRequest.prototype.send = function() {
    window.pfselenium.xhr = this;

    this.addEventListener("load", function() {
        window.pfselenium.xhr = null;
    });
    this.addEventListener("error", function() {
        window.pfselenium.xhr = null;
    });
    this.addEventListener("abort", function() {
        window.pfselenium.xhr = null;
    });

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
