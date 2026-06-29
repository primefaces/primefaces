if (!PrimeFaces.download) {
    /**
     * Fetches the resource at the given URL and prompts the user to download that file, without leaving the current
     * page. This function is usually called by server-side code to download a data URI or a data from a remote URL.
     * @function
     * @param {string} url URL pointing to the resource to download. 
     * @param {string} mimeType The MIME content-type of the file to download. It helps the browser present friendlier
     * information about the download to the user, encouraging them to accept the download.
     * @param {string} fileName The name of the file to be created. Note that older browsers (like FF3.5, Ch5) do not
     * honor the file name you provide, instead they automatically name the downloaded file.
     * @param {string} cookieName Name of the file download cookie (by default `primefaces.download`). This function
     * makes sure the cookie is set properly when the download finishes.
     */
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
