import downloadJs = require("downloadjs");

/**
 * Fetches the resource at the given URL and prompts the user to download that file, without leaving the current
 * page. This function is usually called by server-side code to download a data URI or a data from a remote URL.
 * @param url URL pointing to the resource to download.
 * @param mimeType The MIME content-type of the file to download. It helps the browser present friendlier
 * information about the download to the user, encouraging them to accept the download.
 * @param fileName The name of the file to be created. Note that older browsers (like FF3.5, Ch5) do not
 * honor the file name you provide, instead they automatically name the downloaded file.
 * @param cookieName Name of the file download cookie (by default `primefaces.download`). This function
 * makes sure the cookie is set properly when the download finishes.
 */
export function download(url: string, mimeType: string, fileName: string, cookieName: string): void {
    let cookiePath = PrimeFaces.settings.contextPath;
    if (!cookiePath || cookiePath === '') {
        cookiePath = '/';
    }

    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.responseType = 'blob';
    xhr.onload = () => {
        downloadJs(xhr.response, fileName, mimeType);
        PrimeFaces.setCookie(cookieName, "true", {path: cookiePath});
    };
    xhr.send();
}
