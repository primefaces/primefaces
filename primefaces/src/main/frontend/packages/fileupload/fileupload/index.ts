import "blueimp-file-upload";
import "blueimp-file-upload/js/jquery.iframe-transport.js";

import { FileUpload } from "./src/fileupload.widget.js";
import { SimpleFileUpload } from "./src/fileupload.simple.widget.js";

declare global {
    namespace PrimeType.widget.SimpleFileUpload {
        /**
         * Callback that is invoked at the beginning of a file upload,
         * when a file is sent to the server. See also {@link FileUploadCfg.onstart | onstart}.
         */
        export type OnStartCallback = (this: SimpleFileUpload<SimpleFileUploadCfg>) => void;
    }
    namespace PrimeType.widget.FileUpload {
        /**
         * Callback invoked when file was selected and is added to this
         * widget. See also {@link FileUploadCfg.onAdd | onAdd}.
         */
        export type OnAddCallback =
            /**
             * @param file The file that was selected for the upload.
             * @param callback Callback that needs to be
             * invoked with the file that should be added to the upload queue.
             */
            (this: FileUpload<FileUploadCfg>, file: File, callback: (processedFile: File) => void) => void;

        /**
         * Callback that is invoked when a file upload was canceled. See
         * also {@link FileUploadCfg.oncancel}.
         */
        export type OnCancelCallback = (this: FileUpload<FileUploadCfg>) => void;

        /**
         * Callback that is invoked after a file was uploaded to the
         * server successfully. See also {@link FileUploadCfg.oncomplete | oncomplete}.
         */
        export type OnCompleteCallback =
            /**
             * @param pfArgs The additional
             * arguments from the jQuery XHR requests.
             * @param data Details about
             * the uploaded file or files.
             */
            (this: FileUpload<FileUploadCfg>, pfArgs: PrimeType.ajax.PrimeFacesArgs, data: JQueryFileUpload.JQueryAjaxCallbackData) => void;

        /**
         * Callback that is invoked when a file could not be uploaded to
         * the server. See also {@link FileUploadCfg.onerror | onerror}.
         */
        export type OnErrorCallback =
            /**
             * @param jqXHR The XHR object from the HTTP request.
             * @param textStatus The HTTP status text of the failed request.
             * @param pfArgs The additional arguments
             * from the jQuery XHR request.
             */
            (this: FileUpload<FileUploadCfg>, jqXHR: PrimeType.ajax.pfXHR, textStatus: string, pfArgs: PrimeType.ajax.PrimeFacesArgs) => void;

        /**
         * Callback to execute before the files are sent.
         * If this callback returns false, the file upload request is not started. See also {@link FileUploadCfg.onupload | onupload}.
         */
        export type OnUploadCallback = (this: FileUpload<FileUploadCfg>) => boolean | void;

        /**
         * Callback to execute when the files failed to validate, such as when the
         * file size exceed the configured limit. This callback is called once
         * for each invalid file.
         */
        export type OnValidationFailureCallback = 
            /**
             * @param invalidFile Details regarding the file that failed to validate.
             */
            (invalidFile: FileValidationError) => void;

        /**
         * Callback that is invoked at the beginning of a file upload,
         * when a file is sent to the server. See also {@link FileUploadCfg.onstart | onstart}.
         */
        export type OnStartCallback = (this: FileUpload<FileUploadCfg>) => void;

        /**
         * Represents an error produced when a file failed to validate, such as
         * when its size exceeded the configured limit.
         */
        export interface FileValidationError {
            /**
             * A summary of the validation failure.
             */
            summary: string;
            /**
             * The name of the file.
             */
            filename: string;
            /**
             * The size of the file in bytes.
             */
            filesize: number;
        }
    }
}

declare global {
    namespace PrimeType {
        export interface WidgetRegistry {
            FileUpload: typeof FileUpload;
            SimpleFileUpload: typeof SimpleFileUpload;
        }
    }
    namespace PrimeType.widget {
        export type FileUploadCfg = import("./src/fileupload.widget.js").FileUploadCfg;
        export type SimpleFileUploadCfg = import("./src/fileupload.simple.widget.js").SimpleFileUploadCfg;
    }
}

PrimeFaces.widget.FileUpload = FileUpload;
PrimeFaces.widget.SimpleFileUpload = SimpleFileUpload;
