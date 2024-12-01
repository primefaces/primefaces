import "./pf.filedownload.js";

// Expose download to the global scope
// Not needed for our code, but may already be used by external code
import download from 'downloadjs';
Object.assign(window, { download });