import "blueimp-file-upload";
import "blueimp-file-upload/js/jquery.iframe-transport.js";

import { FileUpload } from "./src/fileupload.js";
import { SimpleFileUpload } from "./src/fileupload.simple.js";

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            FileUpload: typeof FileUpload;
            SimpleFileUpload: typeof SimpleFileUpload;
        }
    }
}

PrimeFaces.component.FileUpload = FileUpload;
PrimeFaces.component.SimpleFileUpload = SimpleFileUpload;
