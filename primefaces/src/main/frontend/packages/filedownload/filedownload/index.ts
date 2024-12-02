import _download from 'downloadjs';
import "./src/pf.filedownload.js";

declare global {
    const download: typeof _download;
    interface Window {
        download: typeof _download;
    }
}

// Expose download to the global scope
Object.assign(window, { download: _download });