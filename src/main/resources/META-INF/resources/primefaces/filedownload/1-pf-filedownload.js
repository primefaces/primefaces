if (!PrimeFaces.download) {
    PrimeFaces.download = function(url, mimeType, fileName, cookieName) {
        var cookiePath = PrimeFaces.settings.contextPath;
        if (!cookiePath || cookiePath === '') {
            cookiePath = '/';
        }

        var x = new XMLHttpRequest();
        x.open("GET", url, true);
        x.responseType = 'blob';
        x.onload = function (e) {
            window.download(x.response, fileName, mimeType);
            PrimeFaces.setCookie(cookieName, "true", {path: cookiePath});
        }
        x.send();
    }
}
