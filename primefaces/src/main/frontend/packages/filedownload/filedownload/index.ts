import "./src/pf.filedownload.js";

// Expose download to the global scope
import download from 'downloadjs';
Object.assign(window, { download });