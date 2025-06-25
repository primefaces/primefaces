import downloadJs from "downloadjs";

import { download as pfDownload } from "./src/pf.filedownload.js";

exposeToGlobalScope();

function exposeToGlobalScope() {
    if (typeof PrimeFaces.download === "function") {
        return;
    }

    // Expose download to the global scope
    Object.assign(window, { download: downloadJs });

    // Expose download function to the PrimeFaces scope
    PrimeFaces.download = pfDownload;
}

declare global {
    /**
     * The download() function is used to trigger a file download from JavaScript.
     * 
     * It specifies the contents and name of a new file placed in the browser's download
     * directory. The input can be a URL, String, Blob, or Typed Array of data, or via a
     * dataURL representing the file's data as base64 or url-encoded string. No matter the
     * input format, download() saves a file using the specified file name and mime
     * information in the same manner as a server using a Content-Disposition HTTP header.
     * 
     * @param data The Blob, File, String, or dataURL containing the soon-to-be File's contents.
     * @param fileName The name of the file to be created. Note that older browsers (like FF3.5, Ch5)
     * don't honor the file name you provide, instead they automatically name the downloaded file.
     * @param mimeType The MIME content-type of the file to download. While optional, it helps the
     * browser present friendlier information about the download to the user, encouraging them to
     * accept the download.
     */
    const download: typeof downloadJs;

    namespace PrimeType {
        interface WindowExtensions {
            /**
             * The download() function is used to trigger a file download from JavaScript.
             * 
             * It specifies the contents and name of a new file placed in the browser's download
             * directory. The input can be a URL, String, Blob, or Typed Array of data, or via a
             * dataURL representing the file's data as base64 or url-encoded string. No matter the
             * input format, download() saves a file using the specified file name and mime
             * information in the same manner as a server using a Content-Disposition HTTP header.
             * 
             * @param data The Blob, File, String, or dataURL containing the soon-to-be File's contents.
             * @param fileName The name of the file to be created. Note that older browsers (like FF3.5, Ch5)
             * don't honor the file name you provide, instead they automatically name the downloaded file.
             * @param mimeType The MIME content-type of the file to download. While optional, it helps the
             * browser present friendlier information about the download to the user, encouraging them to
             * accept the download.
             */
            download: typeof downloadJs;
        }
        
        interface PrimeFaces {
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
            download: typeof pfDownload;
        }
    }
}
