
/**
 * Namespace for the jQuery BlueImp File Upload plugin.
 * 
 * File Upload widget with multiple file selection, drag&drop support, progress bars, validation and preview images,
 * audio and video for jQuery.
 * 
 * Supports cross-domain, chunked and resumable file uploads and client-side image resizing.
 * 
 * Works with any server-side platform (PHP, Python, Ruby on Rails, Java, Node.js, Go etc.) that supports standard HTML
 * form file uploads.
 * 
 * See https://github.com/blueimp/jQuery-File-Upload
 */
declare namespace JQueryFileUpload {

    /**
     * The method to use for uploading files.
     */
    type HttpUploadMethod = "POST" | "PUT" | "PATCH";

    type HttpMethod = "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH";

    /**
     * Represents the response of a successful or failed request.
     * @typeparam TResponse Type of the response.
     * @typeparam TFailure Type of the error.
     */
    type ResponseObject<TResponse = unknown, TFailure = unknown> =
        ResponseSuccess<TResponse> | ResponseFailure<TFailure>;

    /**
     * Plain callback for event listeners. They only received the event that occurred.
     */
    type PlainCallback =
        /**
         * @param event The event that triggered this callback.
         */
        (event: JQuery.TriggeredEvent) => void;

    /**
     * A callback for an event that also receives some additional data, in addition to the event.
     */
    type DataCallback<TData> =
        /**
         * @param event The event that triggered this callback.
         * @param data Additional data passed to this callback. Specific to the event.
         */
        (event: JQuery.TriggeredEvent, data: TData) => void;

    /**
     * A callback for an event that receives some additional data and may also return a boolean that usually defines
     * whether the action that triggered this event is pursued further.
     */
    type DataCallbackBool<TData> =
        /**
         * @param event The event that triggered this callback.
         * @param data Additional data passed to this callback. Specific to the event.
         * @return A boolean flag that indicates how event handling proceeds.
         */
        (event: JQuery.TriggeredEvent, data: TData) => boolean;

    /**
     * A processing action that is available.
     */
    type ProcessAction =
        /**
         * @param data A copy of the data object that is passed to the add callback, with data.files referencing the files
         * array.
         * @param options The options object of the current process action.
         * @return Either the data object, or a Promise object which resolves or rejects with the data object as argument.
         */
        (this: FileUpload, data: ProcessActionData, options: JQueryFileUpload.FileUploadOptions) => ProcessActionData | JQuery.Promise<ProcessActionData>;

    /**
     * Constructor for a new file upload instance.
     */
    type FileUploadConstructor =
        /**
         * @param options Options for the widget.
         * @param Element where the widget is created.
         */
        new (options?: Partial<FileUploadOptions>, element?: HTMLFormElement) => FileUpload;



    /**
     * All the options that can be used to configure the file upload widget.
     */
    interface FileUploadOptions extends JQuery.AjaxSettings {

        // ==============================
        // === Configuration settings ===
        // ==============================

        /**
         * The HTTP request method for the file uploads.
         * 
         * "PUT" and "PATCH" are only supported by browser supporting XHR file uploads, as iframe transport uploads rely
         * on standard HTML forms which only support "POST" file uploads.
         * 
         * If the type is defined as "PUT" or "PATCH", the iframe transport will send the files via "POST" and transfer
         * the original method as "_method" URL parameter.
         * 
         * Note: As was noted above, it's a common practice to use "_method" to transfer the type of your request. For
         * example, "Ruby on Rails" framework uses a hidden input with the name "_method" within each form, so it will
         * likely override the value that you will set here.
         */
        type: HttpUploadMethod;

        /**
         * The type of data that is expected back from the server.
         * 
         * __Note__: The UI version of the File Upload plugin sets this option to "json" by default.
         */
        dataType: string;

        /**
         * The drop target jQuery object, by default the complete document.
         * 
         * Set to null or an empty jQuery collection to disable drag & drop support.
         */
        dropZone: Element | Element[] | JQuery | string | null;

        /**
         * The paste target jQuery object, by the default the complete document.
         * 
         * Set to a jQuery collection to enable paste support:
         */
        pasteZone: Element | Element[] | JQuery | string | null;

        /**
         * The file input field(s), that are listened to for change events.
         * 
         * If undefined, it is set to the file input fields inside of the widget element on plugin initialization.
         * 
         * Set to null to disable the change listener.
         */
        fileInput: Element | Element[] | JQuery | string | null;

        /**
         * By default, the file input field is replaced with a clone after each input field change event.
         * 
         * This is required for iframe transport queues and allows change events to be fired for the same file
         * selection, but can be disabled by setting this option to false.
         */
        replaceFileInput: boolean;

        /**
         * The parameter name for the file form data (the request argument name).
         * 
         * If undefined or empty, the name property of the file input field is used, or "files[]" if the file input name
         * property is also empty.
         */
        paramName: string | string[];

        /**
         * Allows to set the accept-charset attribute for the iframe upload forms.
         * 
         * If `formAcceptCharset` is not set, the accept-charset attribute of the file upload widget form is used, if
         * available.
         */
        formAcceptCharset: string;

        /**
         * By default, each file of a selection is uploaded using an individual request for XHR type uploads.
         * 
         * Set to false to upload file selections in one request each.
         */
        singleFileUploads: boolean;

        /**
         * To limit the number of files uploaded with one XHR request, set this option to an integer greater than 0.
         */
        limitMultiFileUploads: number;

        /**
         * This option limits the number of files uploaded with one XHR request to keep the request size under
         * or equal to the defined limit in bytes.
         */
        limitMultiFileUploadSize: number;

        /**
         * Multipart file uploads add a number of bytes to each uploaded file, therefore this option adds an
         * overhead for each file used in the {@link limitMultiFileUploadSize} configuration.
         */
        limitMultiFileUploadSizeOverhead: number;

        /**
         * Set this option to true to issue all file upload requests in a sequential order.
         */
        sequentialUploads: boolean;

        /**
         * To limit the number of concurrent uploads, set this option to an integer greater than 0.
         */
        limitConcurrentUploads: number;

        /**
         * Set this option to true to force iframe transport uploads, even if the browser is capable of XHR file
         * uploads.
         * 
         * This can be useful for cross-site file uploads, if the `Access-Control-Allow-Origin` header cannot be set for
         * the server-side upload handler which is required for cross-site XHR file uploads.
         */
        forceIframeTransport: boolean;

        /**
         * This option is only used by the iframe transport and allows to override the URL of the initial iframe src.
         */
        initialIframeSrc: string;

        /**
         * Set this option to the location of a redirect URL on the origin server (the server that hosts the file upload
         * form), for cross-domain iframe transport uploads.
         * 
         * If set, this value is sent as part of the form data to the upload server.
         * 
         * The upload server is supposed to redirect the browser to this URL after the upload completes and append the
         * upload information as URL encoded JSON string to the redirect URL, e.g. by replacing the `%s` character
         * sequence.
         */
        redirect: string;

        /**
         * The parameter name for the redirect url, sent as part of the form data and set to `redirect` if this option
         * is empty.
         */
        redirectParamName: string;

        /**
         * Set this option to the location of a postMessage API on the upload server, to enable cross-domain postMessage
         * transport uploads.
         */
        postMessage: string;

        /**
         * By default, XHR file uploads are sent as multipart/form-data.
         * 
         * The iframe transport is always using multipart/form-data.
         * 
         * If this option is set to `false`, the file content is streamed to the server URL instead of sending a RFC
         * 2388 multipart message for XMLHttpRequest file uploads. Non-multipart uploads are also referred to as HTTP
         * PUT file upload.
         * 
         * __Note__: Additional form data is ignored when the multipart option is set to `false`. Non-multipart uploads
         * (multipart: false) are broken in Safari 5.1.
         */
        multipart: boolean;

        /**
         * To upload large files in smaller chunks, set this option to a preferred maximum chunk size. If set to `0`,
         * `null` or `undefined`, or the browser does not support the required Blob API, files will be uploaded as a
         * whole.
         * 
         * For chunked uploads to work in Mozilla Firefox < 7, the multipart option has to be set to `false`. This is
         * due to Gecko 2.0 (Firefox 4 etc.) adding blobs with an empty filename when building a multipart upload
         * request using the FormData interface (fixed in FF 7.0).
         * 
         * Several server-side frameworks (including PHP and Django) cannot handle multipart file uploads with empty
         * filenames.
         * 
         * __Note__: If this option is enabled and {@link singleFileUploads} is set to false, only the first file of a
         * selection will be uploaded.
         */
        maxChunkSize: number;

        /**
         * When a non-multipart upload or a chunked multipart upload has been aborted, this option can be used to resume
         * the upload by setting it to the size of the already uploaded bytes. This option is most useful when modifying
         * the options object inside of the {@link add} or {@link send} callbacks, as the options are cloned for each
         * file upload.
         */
        uploadedBytes: number;

        /**
         * By default, failed (abort or error) file uploads are removed from the global progress calculation. Set this
         * option to `false` to prevent recalculating the global progress data.
         */
        recalculateProgress: boolean;

        /**
         * Interval in milliseconds to calculate and trigger progress events.
         */
        progressInterval: number;

        /**
         * Interval in milliseconds to calculate progress bitrate.
         */
        bitrateInterval: number;

        /**
         * By default, uploads are started automatically when adding files.
         * 
         * Please note that in the basic File Upload plugin, this option is set to `true` by default.
         */
        autoUpload: boolean;

        /**
         * Additional form data to be sent along with the file uploads can be set using this option, which accepts an
         * array of objects with name and value properties, a function returning such an array, a FormData object (for
         * XHR file uploads), or a simple object.
         * 
         * The form of the first file input is given as parameter to the function.
         * 
         * Note: Additional form data is ignored when the multipart option is set to false.
         */
        formData: FormData
        | NameValuePair<FormDataEntryValue>[]
        | Record<string, FormDataEntryValue>
        | ((this: unknown, form: JQuery<HTMLFormElement>) => NameValuePair<FormDataEntryValue>[]);

        // ===============================
        // === File processing options ===
        // ===============================

        /**
         * A list of file processing actions.
         */
        processQueue: ProcessingQueueItem[];

        // =====================
        // === Image options ===
        // =====================

        /**
         * Disable parsing and storing the image header.
         */
        disableImageHead: boolean;

        /**
         * Disable parsing EXIF data.
         */
        disableExif: boolean;

        /**
         * Disable parsing the EXIF Thumbnail.
         */
        disableExifThumbnail: boolean;

        /**
         * Disable parsing the EXIF Sub IFD (additional EXIF info).
         */
        disableExifSub: boolean;

        /**
         * Disable parsing EXIF GPS data.
         */
        disableExifGps: boolean;

        /**
         * Disable parsing image meta data (image head and EXIF data).
         */
        disableImageMetaDataLoad: boolean;

        /**
         * Disables saving the image meta data into the resized images.
         */
        disableImageMetaDataSave: boolean;

        /**
         * The regular expression for the types of images to load, matched against the file type.
         */
        loadImageFileTypes: RegExp;

        /**
         * The maximum file size of images to load.
         */
        loadImageMaxFileSize: number;

        /**
         * Don't revoke the object URL created to load the image.
         */
        loadImageNoRevoke: boolean;

        /**
         * Disable loading and therefore processing of images.
         */
        disableImageLoad: boolean;

        /**
         * The maximum width of resized images.
         */
        imageMaxWidth: number;

        /**
         * The maximum height of resized images.
         */
        imageMaxHeight: number;

        /**
         * The minimum width of resized images.
         */
        imageMinWidth: number;

        /**
         * The minimum height of resized images.
         */
        imageMinHeight: number;

        /**
         * Define if resized images should be cropped or only scaled.
         */
        imageCrop: boolean;

        /**
         * Defines the image orientation (1-8) or takes the orientation value from Exif data if set to true.
         */
        imageOrientation: 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | boolean;

        /**
         * If set to true, forces writing to and saving images from canvas, even if the original image fits the maximum
         * image constraints.
         */
        imageForceResize: boolean;

        /**
         * Disables the resize image functionality.
         */
        disableImageResize: boolean;

        /**
         * Sets the quality parameter given to the canvas.toBlob() call when saving resized images.
         */
        imageQuality: number;

        /**
         * Sets the type parameter given to the canvas.toBlob() call when saving resized images.
         */
        imageType: string;

        /**
         * The maximum width of the preview images.
         */
        previewMaxWidth: number;

        /**
         * The maximum height of the preview images.
         */
        previewMaxHeight: number;

        /**
         * The minimum width of preview images.
         */
        previewMinWidth: number;

        /**
         * The minimum height of preview images.
         */
        previewMinHeight: number;

        /**
         * Define if preview images should be cropped or only scaled.
         */
        previewCrop: boolean;

        /**
         * Defines the preview orientation (1-8) or takes the orientation value from Exif data if set to true.
         */
        previewOrientation: 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | boolean;

        /**
         * Create the preview using the Exif data thumbnail.
         */
        previewThumbnail: boolean;

        /**
         * Define if preview images should be resized as canvas elements.
         */
        previewCanvas: boolean;

        /**
         * Define the name of the property that the preview element is stored as on the File object.
         */
        imagePreviewName: string;

        /**
         * Disables image previews.
         */
        disableImagePreview: boolean;

        // =============================
        // === Audio preview options ===
        // =============================

        /**
         * The regular expression for the types of audio files to load, matched against the file type.
         */
        loadAudioFileTypes: RegExp;

        /**
         * The maximum file size of audio files to load.
         */
        loadAudioMaxFileSize: number;

        /**
         * Define the name of the property that the preview element is stored as on the File object.
         */
        audioPreviewName: string;

        /**
         * Disable audio previews.
         */
        disableAudioPreview: boolean;

        // =============================
        // === Video preview options ===
        // =============================

        /**
         * The regular expression for the types of video files to load, matched against the file type.
         */
        loadVideoFileTypes: RegExp;

        /**
         * The maximum file size of video files to load.
         */
        loadVideoMaxFileSize: number;

        /**
         * Define the name of the property that the preview element is stored as on the File object.
         */
        videoPreviewName: string;

        /**
         * Disable video previews.
         */
        disableVideoPreview: boolean;


        // ==========================
        // === Validation options ===
        // ==========================

        /**
         * The regular expression for allowed file types, matches against either file type or file name as only browsers
         * with support for the File API report the file type.
         */
        acceptFileTypes: RegExp;

        /**
         * The maximum allowed file size in bytes.
         * 
         * __Note__: This option has only an effect for browsers supporting the File API.
         */
        maxFileSize: number;

        /**
         * The minimum allowed file size in bytes.
         * 
         * __Note__: This option has only an effect for browsers supporting the File API.
         */
        minFileSize: number;

        /**
         * This option limits the number of files that are allowed to be uploaded using this widget.
         * 
         * By default, unlimited file uploads are allowed.
         * 
         * __Note__: The maxNumberOfFiles option depends on the getNumberOfFiles option, which is defined by the UI and
         * AngularJS implementations.
         */
        maxNumberOfFiles: number;

        /**
         * Disables file validation.
         */
        disableValidation: boolean;

        // ==========================
        // === UI version options ===
        // ==========================

        /**
         * Callback to retrieve the list of files from the server response.
         * 
         * Is given the data argument of the done callback, which contains the result property. Must return an array.
         */
        getFilesFromResponse: null | ((data: JQueryAjaxCallbackData & ResponseObject) => File[]);

        /**
         * This option is a function that returns the current number of files selected and uploaded.
         * 
         * It is used in the {@link maxNumberOfFiles} validation.
         */
        getNumberOfFiles: null | (() => number);

        /**
         * The container for the files listed for upload / download.
         * 
         * Is transformed into a jQuery object if set as DOM node or string.
         */
        filesContainer: Element | Element[] | JQuery | string;

        /**
         * By default, files are appended to the files container.
         * 
         * Set this option to true, to prepend files instead.
         */
        prependFiles: boolean;

        /**
         * The upload template function.
         */
        uploadTemplate: null | ((data: TemplateData) => JQuery | string);

        /**
         * The ID of the upload template, given as parameter to the tmpl() method to set the uploadTemplate option.
         */
        uploadTemplateId: string;

        /**
         * The upload template function.
         */
        downloadTemplate: null | ((data: TemplateData) => JQuery | string);

        /**
         * The ID of the download template, given as parameter to the tmpl() method to set the downloadTemplate option.
         */
        downloadTemplateId: string;

        // =======================
        // === Basic callbacks ===
        // =======================

        /**
         * The add callback can be understood as the callback for the file upload request queue. It is invoked as soon
         * as files are added to the fileupload widget - via file input selection, drag & drop or add API call.
         * 
         * The data parameter allows to override plugin options as well as define ajax settings
         * 
         * data.files holds a list of files for the upload request: If the singleFileUploads option is enabled (which is
         * the default), the add callback will be called once for each file in the selection for XHR file uploads, with
         * a data.files array length of one, as each file is uploaded individually. Else the add callback will be called
         * once for each file selection.
         * 
         * The upload starts when the submit method is invoked on the data parameter.
         * 
         * data.submit() returns a Promise object and allows to attach additional handlers using jQuery's Deferred
         * callbacks.
         * 
         * The default add callback submits the files if the autoUpload option is set to true (the default for the basic
         * plugin). It also handles processing of files before upload, if any process handlers have been registered.
         */
        add: DataCallback<AddCallbackData> | null;

        /**
         * Callback for the submit event of each file upload.
         * 
         * If this callback returns `false`, the file upload request is not started.
         */
        submit: DataCallbackBool<AddCallbackData> | null;

        /**
         * Callback for the start of each file upload request.
         * 
         * If this callback returns `false`, the file upload request is aborted.
         */
        send: DataCallbackBool<ProgressCallbackData> | null;

        /**
         * Callback for upload progress events.
         */
        progress: DataCallback<ProgressCallbackData> | null;

        /**
         * Callback for global upload progress events.
         */
        progressall: DataCallback<ProgressAllCallbackData> | null;

        /**
         * Callback for uploads start, equivalent to the global ajaxStart event (but for file upload requests only).
         */
        start: PlainCallback | null;

        /**
         * Callback for uploads stop, equivalent to the global ajaxStop event (but for file upload requests only).
         */
        stop: PlainCallback | null;

        /**
         * Callback for change events of the fileInput collection.
         */
        change: DataCallback<ChangeCallbackData> | null;

        // ===========================
        // === Drop zone callbacks ===
        // ===========================

        /**
         * Callback for paste events to the drop zone collection.
         */
        paste: DataCallback<DropzoneCallbackData> | null;

        /**
         * Callback for drop events of the drop zone collection.
         */
        drop: DataCallback<DropzoneCallbackData> | null;

        /**
         * Callback for dragover events of the drop zone collection.
         * 
         * To prevent the plugin from calling the `preventDefault()` method on the original dragover event object and
         * setting the `dataTransfer.dropEffect` to copy, call the `preventDefault()` method on the event object of the
         * `fileuploaddragover` callback.
         * 
         * __Note__: The file upload plugin only provides a dragover callback, as it is used to make drag&drop callbacks
         * work. If you want callbacks for additional drag events, simply bind to these events with JQuery's native
         * event binding mechanism on your dropZone element.
         */
        dragover: DataCallback<DropzoneCallbackData> | null;

        // =======================
        // === Chunk callbacks ===
        // =======================

        /**
         * Callback before the start of each chunk upload request, before form data is initialized.
         */
        chunkbeforesend: null | DataCallback<ChunkCallbackData & ResponseObject> | null;

        /**
         * Callback for the start of each chunk upload request, after form data is initialized.
         * 
         * If this callback returns `false`, the chunk upload request is aborted.
         */
        chunksend: DataCallbackBool<ChunkCallbackData & ResponseObject> | null;

        /**
         * Callback for successful chunk upload requests.
         */
        chunkdone: DataCallback<ChunkCallbackData & ResponseObject> | null;

        /**
         * Callback for failed (abort or error) chunk upload requests
         */
        chunkfail: DataCallback<ChunkCallbackData & ResponseObject> | null;

        /**
         * Callback for completed (success, abort or error) chunk upload requests.
         */
        chunkalways: DataCallback<ChunkCallbackData & ResponseObject> | null;

        // ================================
        // === JQuery wrapper callbacks ===
        // ================================

        /**
         * Callback for successful upload requests. This callback is the equivalent to the success callback provided by
         * jQuery ajax() and will also be called if the server returns a JSON response with an error property.
         */
        done: DataCallback<JQueryAjaxCallbackData & ResponseObject>;

        /**
         * Callback for failed (abort or error) upload requests. This callback is the equivalent to the error callback
         * provided by jQuery ajax() and will not be called if the server returns a JSON response with an error
         * property, as this counts as successful request due to the successful HTTP response.
         */
        fail: DataCallback<JQueryAjaxCallbackData & ResponseObject>;

        /**
         * Callback for completed (success, abort or error) upload requests. This callback is the equivalent to the
         * complete callback provided by jQuery ajax().
         */
        always: DataCallback<JQueryAjaxCallbackData & ResponseObject>;

        // =================================
        // === File processing callbacks ===
        // =================================

        /**
         * Callback for the start of the fileupload processing queue.
         */
        processstart: PlainCallback | null;

        /**
         * Callback for the stop of the fileupload processing queue.
         */
        processstop: PlainCallback | null;

        /**
         * Callback for the start of an individual file processing queue.
         */
        process: DataCallback<ProcessCallbackData> | null;

        /**
         * Callback for the successful end of an individual file processing queue.
         */
        processdone: DataCallback<ProcessCallbackData> | null;

        /**
         * Callback for the failure of an individual file processing queue.
         */
        processfail: DataCallback<ProcessCallbackData> | null;

        /**
         * Callback for the end (done or fail) of an individual file processing queue.
         */
        processalways: DataCallback<ProcessCallbackData> | null;

        // ============================
        // === UI version callbacks ===
        // ============================

        /**
         * Callback for file deletion events.
         * 
         * __Note__ Since the UI version already sets this callback option, it is recommended to use the event binding
         * method to attach additional event listeners.
         */
        destroy: DataCallback<DestroyCallbackData> | null;

        /**
         * The destroyed callback is the equivalent to the destroy callback and is triggered after files have been
         * deleted, the transition effects have completed and the download template has been removed.
         */
        destroyed: DataCallback<DestroyCallbackData> | null;

        /**
         * The added callback is the equivalent to the add callback and is triggered after the upload template has been
         * rendered and the transition effects have completed.
         */
        added: DataCallback<AddCallbackData> | null;

        /**
         * The sent callback is the equivalent to the send callback and is triggered after the send callback has run and
         * the files are about to be sent.
         */
        sent: DataCallbackBool<ProgressCallbackData> | null;

        /**
         * The completed callback is the equivalent to the done callback and is triggered after successful uploads after
         * the download template has been rendered and the transition effects have completed.
         */
        completed: DataCallback<JQueryAjaxCallbackData & ResponseObject> | null;

        /**
         * The failed callback is the equivalent to the fail callback and is triggered after failed uploads after the
         * download template has been rendered and the transition effects have completed.
         */
        failed: DataCallback<JQueryAjaxCallbackData & ResponseObject> | null;

        /**
         * The finished callback is the equivalent to the always callback and is triggered after both completed and
         * failed uploads after the equivalent template has been rendered and the transition effects have completed.
         */
        finished: DataCallback<FileUploadOptions> | null;

        /**
         * The started callback is the equivalent to the start callback and is triggered after the start callback has
         * run and the transition effects called in the start callback have completed.
         * 
         * __Note__: Unlike the start callback, which is always called before all send callbacks, the started callback
         * might be called after the sent callbacks, depending on the duration of the transition effects in those
         * callbacks.
         */
        started: PlainCallback | null;

        /**
         * The stopped callback is the equivalent to the stop callback and is triggered after the stop callback has run
         * and the transition effects called in the stop callback and all done callbacks have completed.
         * 
         * The stopped callback is therefore always triggered after each completed, failed and finished callback is
         * done.
         */
        stopped: PlainCallback | null;
    }

    // =====================
    // === Callback data ===
    // =====================

    /**
     * Data passed to the drop zone event handlers.
     */
    interface DropzoneCallbackData {
        /**
         * An list of files.
         */
        files: File[];
    }

    /**
     * Represents the data passed to file related event handlers.
     */
    interface ChangeCallbackData {
        /**
         * The input element of type file.
         */
        fileInput?: JQuery;

        /**
         * The cloned input element of type file.
         */
        fileInputClone?: JQuery;

        /**
         * List of files currently selected.
         */
        files: File[];

        /**
         * Form element that holds the file input.
         */
        form?: JQuery;

        /**
         * Original list of files that were present.
         */
        originalFiles: File[];
    }

    /**
     * The data for the callback when a file is added.
     */
    interface AddCallbackData extends ChangeCallbackData, ConvenienceMethods {
        /**
         * Name of the file input or inputs.
         */
        paramName?: string | string[];
    }

    /**
     * The data for when a file is processed.
     */
    interface ProcessCallbackData extends ChangeCallbackData, ConvenienceMethods {
        /**
         * Name of the file input or inputs.
         */
        paramName?: string | string[];
    }

    /**
     * The data for the callback when the widget is destroyed.
     */
    interface DestroyCallbackData {
        /**
         * Download row
         */
        context: JQuery;

        /**
         * Deletion url
         */
        url: string;

        /**
         * Deletion request type, e.g. `DELETE`.
         */
        type: HttpMethod;

        /**
         * Deletion response type, e.g. `json`.
         */
        dataType: string;
    }

    /**
     * Represents the data passed to the global progress event handler.
     */
    interface ProgressAllCallbackData extends UploadProgress {
    }

    /**
     * Represents the data passed to various progress related event handlers.
     */
    interface ProgressCallbackData extends FileUploadOptions, UploadProgress {
    }

    /**
     * Represents the callback data that is passed to various chunk related event handlers.
     */
    interface ChunkCallbackData extends FileUploadOptions {
        /**
         * The blob of the chunk that is being sent.
         */
        blob: Blob;

        /**
         * Length of the chunk that is being sent.
         */
        chunkSize: number;

        /**
         * Content range of the chunk being sent.
         */
        contentRange: string;

        /**
         * The error that occurred, if any.
         */
        errorThrown?: unknown;
    }

    /**
     * Represents the data that is passed to various event handlers simulating JQuery AJAX handlers.
     */
    interface JQueryAjaxCallbackData extends FileUploadOptions {
    }

    // =============================
    // === Misc. data structures ===
    // =============================

    /**
     * The data passed to the template methods {@link FileUploadOptions.uploadTemplate} and
     * {@link FileUploadOptions.downloadTemplate}.
     */
    interface TemplateData {
        /**
         * List of files to be rendered.
         */
        files: File[],

        /**
         * Formats a file size as a human readable string.
         * @param size File size to format, in bytes.
         * @return Humand-readable string of a file size.
         */
        formatFileSize(size: number): string;

        /**
         * Current options of the upload widget.
         */
        options: FileUploadOptions;
    }

    interface ProcessingQueueItem {
        /**
         * MIME types a file must have to be accepted.
         */
        acceptFileTypes?: RegExp | string;

        /**
         * Actin to run.
         */
        action: string;

        /**
         * Whether the action is always run.
         */
        always?: boolean;

        /**
         * Whether a prefix is added.
         */
        prefix?: boolean;

        /**
         * List of recognized file types.
         */
        fileTypes?: string;

        /**
         * Whether this action is disabled.
         */
        disabled?: string;
    }

    /**
     * @typeparam V Type of the property value.
     * @typeparam V Type of the property name.
     * An object for a single property, with a name and value entry. 
     */
    interface NameValuePair<V, K = string> {
        /**
         * The name of the property.
         */
        name: K;
        /**
         * The value of the property.
         */
        value: V;
    }

    /**
     * Additional methods for the data objects that are passed to the event handlers.
     */
    interface ConvenienceMethods {
        /**
         * Aborts the file upload.
         * @return A promise that resolves once the file upload is aborted.
         */
        abort(): JQuery.Promise<void>;

        /**
         * Adds the handlers to the process queue and returns the process queue.
         * @param resolveFunc Additional handler for chaining to the promise.
         * @param rejectFunc Additional handler for chaining to the promise.
         * @return The process queue promise.
         */
        process<T>(
            resolveFunc: (value?: this) => JQuery.Promise<T> | JQuery.Thenable<T> | T,
            rejectFunc?: (reason?: unknown) => JQuery.Promise<T> | JQuery.Thenable<T> | T
        ): JQuery.Promise<T>;

        /**
         * Adds the handlers to the process queue and returns the process queue.
         * @param resolveFunc Additional handler for chaining to the promise.
         * @param rejectFunc Additional handler for chaining to the promise.
         * @return The process queue promise.
         */
        process<T>(
            resolveFunc: undefined,
            rejectFunc: (reason?: unknown) => JQuery.Promise<T> | JQuery.Thenable<T> | T
        ): JQuery.Promise<T>;

        /**
         * Retrieves the process queue.
         * @return The process queue promise.
         */
        process(): JQuery.Promise<this>;

        /**
         * Checks whether any upload is being processed.
         * @return Whether any upload is being processed.
         */
        processing(): boolean;

        /**
         * Retrieves the details about the current upload progress.
         * @return Details about the current upload progress.
         */
        progress(): UploadProgress;

        /**
         * Retrieves the current response object.
         * @return The current response object with the response info.
         */
        response(): ResponseObject | Record<string, unknown>;

        /**
         * Submits the form.
         * @return A promise that resolves when submission is done.
         */
        submit(): JQuery.Promise<unknown>;

        /**
         * Finds the current state of the file upload request.
         * @return A promise that resolves with the current state of the file upload request.
         */
        state(): JQuery.Promise<string>;
    }

    /**
     * Represents the progress of one or more file uploads
     */
    interface UploadProgress {
        /**
         * Amount of data already loaded.
         */
        loaded: number;

        /**
         * Total amount of data to be sent.
         */
        total: number;

        /**
         * Bitrate at which the data is transferred.
         */
        bitrate: number;
    }

    /**
     * Represents a response for a successful request.
     * @typeparam TResponse Type of the response.
     */
    interface ResponseSuccess<TResponse = unknown> {
        /**
         * The underlying JQuery XHR object.
         */
        jqXHR: JQuery.jqXHR;

        /**
         * The result of the request.
         */
        result: TResponse;

        /**
         * The status text of the request.
         */
        textStatus: string;
    }

    /**
     * Represents a response for a failed request.
     * @typeparam TFailure Type of the error.
     */
    interface ResponseFailure<TFailure = unknown> {
        /**
         * The reason why the request failed.
         */
        errorThrown: TFailure;

        /**
         * The underlying JQuery XHR object.
         */
        jqXHR: JQuery.jqXHR;

        /**
         * The status text of the request.
         */
        textStatus: string;
    }

    /**
     * Represents the data passed to the {@link FileUpload.processActions|$.blueimp.fileupload.prototype.processActions}
     * handlers.
     */
    interface ProcessActionData extends AddCallbackData {
        /**
         * The index of the current file to be processed.
         */
        index: number;
    }

    /**
     * Represents the options for adding or uploading files programmatically.
     */
    interface FileUploadData extends Partial<FileUploadOptions> {
        /**
         * An array or array-like list of File or Blob objects.
         */
        files: ArrayLike<File | Blob>;
    }

    /**
     * Interface for an instance of the jQuery BlueImp File Upload plugin.
     * 
     * The widget can be initialized on a file upload in the following way:
     * 
     * ```javascript
     * $('#fileupload').fileupload();
     * ```
     * 
     * File Upload widget with multiple file selection, drag&drop support, progress bars, validation and preview images,
     * audio and video for jQuery.
     * 
     * Supports cross-domain, chunked and resumable file uploads and client-side image resizing.
     * 
     * Works with any server-side platform (PHP, Python, Ruby on Rails, Java, Node.js, Go etc.) that supports standard
     * HTML form file uploads.
     * 
     * See https://github.com/blueimp/jQuery-File-Upload
     */
    interface FileUpload extends JQueryUI.WidgetCommonProperties {
        /**
         * Namespace for the events of this widget.
         */
        eventNamespace: string;

        /**
         * List of focusable elements.
         */
        focusable: JQuery;

        /**
         * List of hoverable elements.
         */
        hoverable: JQuery;

        /**
         * Current options for this file upload widget.
         */
        options: FileUploadOptions;

        /**
         * This actions that are defined as a property of `$.blueimp.fileupload.prototype.processActions`.
         */
        processActions: Record<string, ProcessAction>;

        /**
         * Sets the given option to the given value.
         * @typeparam K Name of an option to set.
         * @param optionName Name of an option to set.
         * @return The value of the given option
         */
        option<K extends keyof JQueryFileUpload.FileUploadOptions>(optionName: K, optionValue: JQueryFileUpload.FileUploadOptions[K]): this;

        /**
         * Retrieves the value of the given option.
         * @param method The method to call on this file upload instance.
         * @param optionName Name of an option to retrieve.
         * @return The value of the given option.
         */
        option<K extends keyof JQueryFileUpload.FileUploadOptions>(optionName: K): JQueryFileUpload.FileUploadOptions[K];

        /**
         * Sets the given options on this file upload instance.
         * @param options Options to apply.
         * @return This JQuery instance for chaining.
         */
        option(options: Partial<JQueryFileUpload.FileUploadOptions>): this;

        /**
         * Return the current set of options. This includes default options.
         * @return An object with all options.
         */
        option(): JQueryFileUpload.FileUploadOptions;

        /**
         * To remove the file upload widget functionality from the element node, call the destroy method. This will also
         * remove any added event listeners.
         * @return This JQuery instance for chaining.
         */
        destroy(): undefined;

        /**
         * As other widgets based on jQuery UI Widget, the file upload widget can also be enabled.
         */
        enable(): this;

        /**
         * As other widgets based on jQuery UI Widget, the file upload widget can also be disabled.
         */
        disable(): this;

        /**
         * Finds the overall progress of all uploads.
         * @return The overall progress of all uploads.
         */
        progress(): JQueryFileUpload.UploadProgress;

        /**
         * Finds the number of currently active uploads.
         * @return The number of active uploads.
         */
        active(): number;

        /**
         * Usually, file uploads are invoked by selecting files via file input button or by dropping files on the drop zone.
         * 
         * However it is also possible to upload files programmatically for browsers with support for XHR file uploads.
         * 
         * This uploads files by adding them to the upload queue, the same way that files are added via the file input
         * button or drag & drop.
         * 
         * @param filesAndOptions A list of files to add to this widget. You can also override options of this widget.
         * @return This JQuery instance for chaining.
         */
        add(filesAndOptions: JQueryFileUpload.FileUploadData): void;

        /**
         * Usually, file uploads are invoked by selecting files via file input button or by dropping files on the drop zone.
         * 
         * However it is also possible to upload files programmatically for browsers with support for XHR file uploads.
         * 
         * This sends the file directly to the server.
         * 
         * __Note__: The send API method sends the given files directly, without splitting them up into multiple requests.
         * So if your files argument is made up of 3 files, it will still only send one request. If the multipart option is
         * true, it will still send all 3 files as part of one multipart request, else it will only send the first file. So
         * if you need to send files with multiple requests, either call the send API method multiple times, or use the add
         * API method instead.
         * 
         * @param filesAndOptions A list of files to add to this widget. You can also override options of this widget.
         * @return A jqXHR object that allows to bind callbacks to the AJAX file upload requests.
         */
        send(filesAndOptions: JQueryFileUpload.FileUploadData): JQuery.jqXHR;
    }

    // ==========================
    // === Blue Imp Namespace ===
    // ==========================

    /**
     * Namespace for JQuery plugins created by https://github.com/blueimp
     */
    interface BlueImpNamespace {
        /**
         * The constructor for the {@link FileUpload} widget.
         * 
         * The widget can be initialized on a file upload in the following way:
         * 
         * ```javascript
         * $('#fileupload').fileupload();
         * ```
         */
        fileupload: FileUploadConstructor;
    }

}

interface JQuery {
    /**
     * The file upload widget is initialized by calling the fileupload method on a jQuery collection with the target
     * HTML element.
     * 
     * The target element is usually a container element holding the file upload form, or the file upload form itself,
     * but it can also be just the file input element itself for a customized UI and if a URL is provided as options
     * parameter.
     * @param settings Options for configuring the file upload.
     * @return This JQuery instance for chaining.
     */
    fileupload(settings?: Partial<JQueryFileUpload.FileUploadOptions>): this;

    /**
     * Sets the given option to the given value.
     * @typeparam K Name of an option to set.
     * @param method The method to call on this file upload instance.
     * @param optionName Name of an option to set.
     * @return The value of the given option
     */
    fileupload<K extends keyof JQueryFileUpload.FileUploadOptions>(
        method: "option",
        optionName: K,
        optionValue: JQueryFileUpload.FileUploadOptions[K]
    ): this;

    /**
     * Retrieves the value of the given option.
     * @typeparam K Name of an option to retrieve.
     * @param method The method to call on this file upload instance.
     * @param optionName Name of an option to retrieve.
     * @return The value of the given option.
     */
    fileupload<K extends keyof JQueryFileUpload.FileUploadOptions>(
        method: "option",
        optionName: K
    ): JQueryFileUpload.FileUploadOptions[K];

    /**
     * Sets the given options on this file upload instance.
     * @param method The method to call on this file upload instance.
     * @param options Options to apply.
     * @return This JQuery instance for chaining.
     */
    fileupload(method: "option", options: Partial<JQueryFileUpload.FileUploadOptions>): this;

    /**
     * Return the current set of options. This includes default options.
     * @param method The method to call on this file upload instance.
     * @return An object with all options.
     */
    fileupload(method: "option"): JQueryFileUpload.FileUploadOptions;

    /**
     * To remove the file upload widget functionality from the element node, call the destroy method. This will also
     * remove any added event listeners.
     * @param method The method to call on this file upload instance.
     * @return This JQuery instance for chaining.
     */
    fileupload(method: "destroy"): this;

    /**
     * As other widgets based on jQuery UI Widget, the file upload widget can also be enabled.
     * @param method The method to call on this file upload instance.
     */
    fileupload(method: "enable"): this;

    /**
     * As other widgets based on jQuery UI Widget, the file upload widget can also be disabled.
     * @param method The method to call on this file upload instance.
     */
    fileupload(method: "disable"): this;

    /**
     * Finds the overall progress of all uploads.
     * @param method The method to call on this file upload instance.
     * @return The overall progress of all uploads.
     */
    fileupload(method: "progress"): JQueryFileUpload.UploadProgress;

    /**
     * Finds the number of currently active uploads.
     * @param method The method to call on this file upload instance.
     * @return The number of active uploads.
     */
    fileupload(method: "active"): number;

    /**
     * Usually, file uploads are invoked by selecting files via file input button or by dropping files on the drop zone.
     * 
     * However it is also possible to upload files programmatically for browsers with support for XHR file uploads.
     * 
     * This uploads files by adding them to the upload queue, the same way that files are added via the file input
     * button or drag & drop.
     * 
     * @param method The method to call on this file upload instance.
     * @param filesAndOptions A list of files to add to this widget. You can also override options of this widget.
     * @return This JQuery instance for chaining.
     */
    fileupload(method: "add", filesAndOptions: JQueryFileUpload.FileUploadData): this;

    /**
     * Usually, file uploads are invoked by selecting files via file input button or by dropping files on the drop zone.
     * 
     * However it is also possible to upload files programmatically for browsers with support for XHR file uploads.
     * 
     * This sends the file directly to the server.
     * 
     * __Note__: The send API method sends the given files directly, without splitting them up into multiple requests.
     * So if your files argument is made up of 3 files, it will still only send one request. If the multipart option is
     * true, it will still send all 3 files as part of one multipart request, else it will only send the first file. So
     * if you need to send files with multiple requests, either call the send API method multiple times, or use the add
     * API method instead.
     * 
     * @param method The method to call on this file upload instance.
     * @param filesAndOptions A list of files to add to this widget. You can also override options of this widget.
     * @return A jqXHR object that allows to bind callbacks to the AJAX file upload requests.
     */
    fileupload(method: "send", filesAndOptions: JQueryFileUpload.FileUploadData): JQuery.jqXHR;
}

declare namespace JQuery {
    interface TypeToTriggeredEventMap<
        TDelegateTarget,
        TData,
        TCurrentTarget,
        TTarget
        > {

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.add|FileUploadOptions.add}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadadd: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.added|FileUploadOptions.added}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadadded: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.always|FileUploadOptions.always}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadalways: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.change|FileUploadOptions.change}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchange: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.chunkalways|FileUploadOptions.chunkalways}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchunkalways: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.chunkbeforesend|FileUploadOptions.chunkbeforesend}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchunkbeforesend: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.chunkdone|FileUploadOptions.chunkdone}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchunkdone: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.chunkfail|FileUploadOptions.chunkfail}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchunkfail: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.chunksend|FileUploadOptions.chunksend}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadchunksend: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.completed|FileUploadOptions.completed}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadcompleted: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.destroy|FileUploadOptions.destroy}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploaddestroy: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.destroyed|FileUploadOptions.destroyed}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploaddestroyed: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.done|FileUploadOptions.done}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploaddone: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.dragover|FileUploadOptions.dragover}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploaddragover: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.drop|FileUploadOptions.drop}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploaddrop: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.fail|FileUploadOptions.fail}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadfail: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.failed|FileUploadOptions.failed}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadfailed: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.finished|FileUploadOptions.finished}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadfinished: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.paste|FileUploadOptions.paste}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadpaste: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.process|FileUploadOptions.process}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocess: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.processalways|FileUploadOptions.processalways}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocessalways: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.processdone|FileUploadOptions.processdone}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocessdone: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.processfail|FileUploadOptions.processfail}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocessfail: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.processstart|FileUploadOptions.processstart}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocessstart: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.processstop|FileUploadOptions.processstop}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprocessstop: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.progress|FileUploadOptions.progress}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprogress: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.progressall|FileUploadOptions.progressall}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadprogressall: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.send|FileUploadOptions.send}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadsend: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.sent|FileUploadOptions.sent}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadsent: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.start|FileUploadOptions.start}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadstart: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.started|FileUploadOptions.started}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadstarted: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.stop|FileUploadOptions.stop}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadstop: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.stopped|FileUploadOptions.stopped}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadstopped: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.fileupload|jQuery BlueImp File Upload plugin}.
         * 
         * See {@link JQueryFileUpload.FileUploadOptions.submit|FileUploadOptions.submit}, the callback passed via
         * the options, for more details on the parameters passed to the callback.
         */
        fileuploadsubmit: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}

interface JQueryStatic {
    blueimp: JQueryFileUpload.BlueImpNamespace;
}