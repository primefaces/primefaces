// TODO: Bad global modifications of object shapes
declare global {
    // This widget sets properties on the builtin File object (that it normally
    // does not have). For now, we just tell TypeScript that this is what we are
    // doing. In the future, we could use e.g. a WeakMap to associate additional
    // data with a File object, instead of mutating the shape of a built-in object. 
    interface File {
        ajaxRequest?: JQueryFileUpload.AddCallbackData | null;
        row?: JQuery | null;
    }
    // In a similar manner, this widget also sets additional properties on the
    // JQuery XHR settings object. For now, we just tell TypeScript that this
    // is what we are doing. In the future, we need to look for a better way,
    // such as passing the data directly to the methods.
    namespace JQuery {
        interface jqXHR {
            pfArgs?: PrimeType.ajax.PrimeFacesArgs;
        }
    }
    // In a similar manner, this widget also sets additional properties on the
    // blue imp file upload options. For now, we just tell TypeScript that this
    // is what we are doing. In the future, we need to look for a better way,
    // such as passing the data directly to the methods, or simply reading it
    // form out "this.cfg".  
    namespace JQueryFileUpload {
        interface FileUploadOptions {
            maxRetries?: number;
            portletForms?: string | null;
            retryTimeout?: number;
            source?: string;
        }
    }
}

/**
 * The configuration for the {@link FileUpload} widget.
 * 
 * You can access this configuration via {@link FileUpload.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface FileUploadCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Regular expression for accepted file types.
     */
    allowTypes: RegExp;

    /**
     * When set to true, selecting a file starts the upload process implicitly.
     */
    auto: boolean;

    /**
     * Whether this file upload is disabled.
     */
    disabled: boolean;

    /**
     * Whether drag and drop is enabled.
     */
    dnd: boolean;

    /**
     * Custom drop zone to use for drag and drop.
     */
    dropZone: string;

    /**
     * Global AJAX requests are listened to by `ajaxStatus`. When `false`, `ajaxStatus` will not
     * get triggered.
     */
    global: boolean;

    /**
     * When set true, components which use `p:autoUpdate` will not be updated for this request.
     */
    ignoreAutoUpdate: boolean;

    /**
     * To upload large files in smaller chunks, set this option to a preferred maximum chunk
     * size. If set to `0`, `null` or `undefined`, or the browser does not support the required Blob API, files will be
     * uploaded as a whole.
     */
    maxChunkSize: number;

    /**
     * Only for chunked file upload: Amount of retries when upload gets interrupted due to
     * e.g. an unstable network connection.
     */
    maxRetries: number;

    /**
     * Message template to use when displaying file validation errors.
     */
    messageTemplate: string;

    /**
     * Callback invoked when an uploaded file is added.
     */
    onAdd: PrimeType.widget.FileUpload.OnAddCallback;

    /**
     * Callback that is invoked when a file upload was
     * canceled.
     */
    oncancel: PrimeType.widget.FileUpload.OnCancelCallback;

    /**
     * Callback that is invoked after a file was
     * uploaded to the server successfully.
     */
    oncomplete: PrimeType.widget.FileUpload.OnCompleteCallback;

    /**
     * Callback that is invoked when a file could not be
     * uploaded to the server.
     */
    onerror: PrimeType.widget.FileUpload.OnErrorCallback;

    /**
     * Callback that is invoked at the beginning of a file
     * upload, when a file is sent to the server.
     */
    onstart: PrimeType.widget.FileUpload.OnStartCallback;

    /**
     * Callback to execute before the files are sent.
     * If this callback returns false, the file upload request is not started.
     */
    onupload: PrimeType.widget.FileUpload.OnUploadCallback;

    /**
     * Callback to execute when the files failed to validate, such as when the
     * file size exceed the configured limit. This callback is called once for
     * each invalid file.
     */
    onvalidationfailure: PrimeType.widget.FileUpload.OnValidationFailureCallback;

    /**
     * Width for image previews in pixels.
     */
    previewWidth: number;

    /**
     * Component(s) to process in fileupload request.
     */
    process: string;

    /**
     * Server-side path which provides information to resume chunked file upload.
     */
    resumeContextPath: string;

    /**
     * Only for chunked file upload: (Base) timeout in milliseconds to wait until the next
     * retry. It is multiplied with the retry count. (first retry: `retryTimeout * 1`, second retry: `retryTimeout * 2`,
     * ...)
     */
    retryTimeout: number;

    /**
     * `true` to upload files one after each other, `false` to upload in parallel.
     */
    sequentialUploads: boolean;

    /**
     * Component(s) to update after fileupload completes.
     */
    update: string;
}

/**
 * __PrimeFaces FileUpload Widget__
 *
 * FileUpload goes beyond the browser input `type="file"` functionality and features an HTML5 powered rich solution with
 * graceful degradation for legacy browsers.
 */
export class FileUpload<Cfg extends FileUploadCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The DOM element for the bar with the buttons of this widget.
     */
    buttonBar: JQuery = $();

    /**
     * The DOM element for the button for canceling a file upload.
     */
    cancelButton: JQuery = $();

    /**
     * The DOM element for the button for selecting a file.
     */
    chooseButton: JQuery = $();

    /**
     * The DOM element for the button to clear the file upload messages (which inform the
     * user about whether a file was uploaded).
     */
    clearMessageLink: JQuery = $();

    /**
     * The DOM element for the content of this widget.
     */
    content: JQuery = $();

    /**
     * Custom drop zone to use for drag and drop.
     */
    customDropZone: string = "";

    /**
     * Amount of dragover on drop zone and its children.
     */
    dragoverCount: number = 0;

    /**
     * Drop zone to use for drag and drop.
     */
    dropZone: JQuery | null = null;

    /**
     * The DOM element for the `f:facet` when no file is selected.
     */
    emptyFacet: JQuery = $();

    /**
     * Current index where to add files.
     */
    fileAddIndex: number = 0;

    /**
     * ID of the current file.
     */
    fileId: number = 0;

    /**
     * List of currently selected files.
     */
    files: File[] = [];

    /**
     * The DOM element for the `f:facet` when files are selected.
     */
    filesFacet: JQuery = $();

    /**
     * The DOM element for the table tbody with the files.
     */
    filesTbody: JQuery = $();

    /**
     * The DOM element for the form containing this upload widget.
     */
    form: JQuery = $();

    /**
     * The DOM element of the container with the file upload messages which inform the user
     * about whether a file was uploaded.
     */
    messageContainer: JQuery = $();

    /**
     * The DOM element of the UL list element with the file upload messages which inform the user
     * about whether a file was uploaded.
     */
    messageList: JQuery = $();

    /**
     * Selector for the button for canceling a file upload.
     */
    rowCancelActionSelector: string = "";

    /**
     * Options for the BlueImp jQuery file upload plugin.
     */
    ucfg: Partial<JQueryFileUpload.FileUploadOptions> = {};

    /**
     * The DOM element for the button for starting the file upload.
     */
    uploadButton: JQuery = $();

    /**
     * Number of currently uploaded files.
     */
    uploadedFileCount: number = 0;

    /**
     * Regular expression that matches image files for which a preview can be shown.
     * @type {RegExp}
     */
    static IMAGE_TYPES: RegExp = /([./])(gif|jpe?g|png)$/i;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        
        if (this.cfg.disabled) {
            return;
        }

        this.form = this.jq.closest('form');
        this.buttonBar = this.jq.children('.ui-fileupload-buttonbar');
        this.dragoverCount = 0;
        this.chooseButton = this.buttonBar.children('.ui-fileupload-choose');
        this.uploadButton = this.buttonBar.children('.ui-fileupload-upload');
        this.cancelButton = this.buttonBar.children('.ui-fileupload-cancel');
        this.content = this.jq.children('.ui-fileupload-content');
        this.emptyFacet = this.content.find('> .ui-fileupload-empty');
        this.filesFacet = this.content.find('> div.ui-fileupload-files > div');
        this.files = [];
        this.fileAddIndex = 0;
        this.cfg.previewWidth = this.cfg.previewWidth || 80;
        this.cfg.maxRetries = this.cfg.maxRetries || 30;
        this.cfg.retryTimeout = this.cfg.retryTimeout || 1000;
        this.cfg.global = this.cfg.global !== false;
        this.uploadedFileCount = 0;
        this.fileId = 0;
        this.dropZone = null;
        if (this.cfg.dnd !== false) {
            this.dropZone = this.cfg.dropZone !== undefined
                ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.dropZone)
                : this.jq;
        }

        this.renderMessages();

        this.bindEvents();

        const $this = this;

        const parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);

        this.ucfg = {
            url: PrimeFaces.ajax.Utils.getPostUrl(this.form),
            portletForms: PrimeFaces.ajax.Utils.getPorletForms(this.form, parameterPrefix ?? ""),
            paramName: Array.from({length: 999}, () => this.getId()), // required so drag´n´drop has for each file the id (Github #11879)
            dataType: 'xml',
            dropZone: this.dropZone,
            sequentialUploads: this.cfg.sequentialUploads,
            maxChunkSize: this.cfg.maxChunkSize,
            maxRetries: this.cfg.maxRetries,
            retryTimeout: this.cfg.retryTimeout,
            source: this.getId(),
            formData: () => this.createPostData(),
            beforeSend: function(xhr: PrimeType.ajax.pfXHR, settings) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                xhr.pfSettings = settings as PrimeType.ajax.PrimeFacesSettings;
                xhr.pfArgs = {}; // default should be an empty object

                const file = settings.files ? settings.files[0] : null;
                if (file && file.webkitRelativePath) {
                    if (settings.data instanceof FormData) {
                        settings.data.append('X-File-Webkit-Relative-Path', file.webkitRelativePath);
                    }
                }

                if($this.cfg.global) {
                    $(document).trigger('pfAjaxSend', [xhr, this]);
                }
            },
            start: function(e) {
                if($this.cfg.onstart) {
                    $this.cfg.onstart.call($this);
                }
            },
            add: (_, data) => {
                this.chooseButton.removeClass('ui-state-hover ui-state-focus');

                if(this.fileAddIndex === 0) {
                    this.clearMessages();
                }

                const update = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.update);

                // we need to fake the filelimit as the jquery-fileupload input always only contains 1 file
                let dataFileInput = data.fileInput;
                if (dataFileInput == null) { // drag´n´drop - Github #11879
                    dataFileInput = $(PrimeFaces.escapeClientId(data.paramName + '_input'));
                    const dataTransfer = new DataTransfer();
                    data.files.forEach((item) => {
                        dataTransfer.items.add(item);
                    });
                    if (dataFileInput[0] instanceof HTMLInputElement) {
                        dataFileInput[0].files = dataTransfer.files;
                    }
                }
                // CSV metadata
                dataFileInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

                var fileLimit = dataFileInput ? dataFileInput.data('p-filelimit') : null;
                if (fileLimit && (this.uploadedFileCount + this.files.length + 1) > fileLimit) {
                    this.clearMessages();

                    // try to render the msg first with our CSV framework
                    var vc = PrimeFaces.validation.ValidationContext;
                    vc.clear();
                    vc.addMessage(this.getId(), PrimeFaces.validation.Utils.getMessage('primefaces.FileValidator.FILE_LIMIT', [ fileLimit ]));
                    PrimeFaces.validation.Utils.renderMessages(vc.messages, update);

                    // if the messages hasn't been rendered, use our internal messages display
                    for (let clientId in vc.messages) {
                        for (let msg of vc.messages[clientId] ?? []) {
                            if (!msg.rendered) {
                                this.showMessage(msg);
                            }
                        }
                    }

                    vc.clear();

                    return;
                }

                var file = data.files ? data.files[0] : null;
                if (file) {
                    this.clearMessages();

                    // we need to pass the real invisible input, which contains the file list
                    var validationResult = PrimeFaces.validation.validate(this.jq, dataFileInput, update, true, true, true, true, false);
                    if (!validationResult.valid) {

                        // if the messages hasn't been rendered, use our internal messages display
                        for (let clientId in validationResult.messages) {
                            for (let msg of validationResult.messages[clientId] ?? []) {
                                if (!msg.rendered) {
                                    this.showMessage(msg);
                                }
                            }
                        }

                        this.postSelectFile(data);

                        if (this.cfg.onvalidationfailure) {
                            for (let clientId in validationResult.messages) {
                                for (let msg of validationResult.messages[clientId] ?? []) {
                                    this.cfg.onvalidationfailure({
                                        summary: msg.summary,
                                        filename: file.name,
                                        filesize: file.size
                                    });
                                }
                            }
                        }
                    }
                    else if (this.cfg.onAdd) {
                        this.cfg.onAdd.call(this, file, (processedFile) => {
                            file = processedFile;
                            data.files[0] = processedFile;
                            this.addFileToRow(file, data);
                        });
                    }
                    else {
                        this.addFileToRow(file, data);
                    }

                    if (this.cfg.resumeContextPath && (this.cfg.maxChunkSize ?? 0) > 0) {
                        $.getJSON(this.cfg.resumeContextPath, {'X-File-Id': this.createXFileId(file)}, function (result) {
                            const uploadedBytes = result.uploadedBytes;
                            data.uploadedBytes = uploadedBytes;
                        });
                    }
                }
            },
            send: function(e, data) {
                if(!window.FormData) {
                    for (const file of data.files) {
                        if(file.row) {
                            file.row.children('.ui-fileupload-progress').find('> .ui-progressbar > .ui-progressbar-value')
                                    .addClass('ui-progressbar-value-legacy')
                                    .css({
                                        width: '100%',
                                        display: 'block'
                                    });
                        }
                    }
                }
            },
            fail: function(e, data) {
                if (data.errorThrown === 'abort') {
                    if ($this.cfg.resumeContextPath && ($this.cfg.maxChunkSize ?? 0) > 0) {
                        if (data.files[0]) {
                            $.ajax({
                                url: $this.cfg.resumeContextPath + '?' + $.param({'X-File-Id' : $this.createXFileId(data.files[0])}),
                                dataType: 'json',
                                type: 'DELETE'
                            });
                        }
                    }

                    if ($this.cfg.oncancel) {
                        $this.cfg.oncancel.call($this);
                    }
                    return;
                }
                if ($this.cfg.resumeContextPath && ($this.cfg.maxChunkSize ?? 0) > 0) {
                    if (data.context === undefined) {
                        data.context = $(this);
                    }

                    // jQuery Widget Factory uses "namespace-widgetname" since version 1.10.0:
                    const fu = $(this).data('blueimp-fileupload') || $(this).data('fileupload');
                    let retries = data.context.data('retries') || 0;

                    const retry = function () {
                        if (data.files[0]) {
                            $.getJSON($this.cfg.resumeContextPath ?? "", {'X-File-Id': $this.createXFileId(data.files[0])})
                                .done(function (result) {
                                    const uploadedBytes = result.uploadedBytes;
                                    data.uploadedBytes = uploadedBytes;
                                    // clear the previous data:
                                    data.data = undefined;
                                    data.submit();
                                })
                                .fail(function () {
                                    fu._trigger('fail', e, data);
                                });
                        }
                    };

                    if (data.errorThrown !== 'abort' &&
                        data.uploadedBytes < (data.files[0]?.size ?? 0) &&
                        retries < fu.options.maxRetries) {
                        retries += 1;
                        data.context.data('retries', retries);
                        window.setTimeout(retry, retries * fu.options.retryTimeout);
                        return;
                    }
                    data.context.removeData('retries');
                }

                if ($this.cfg.onerror) {
                    $this.cfg.onerror.call($this, data.jqXHR, data.textStatus, data.jqXHR.pfArgs ?? {});
                }
            },
            progress: function(e, data) {
                if(window.FormData) {
                    var progress = PrimeFaces.utils.roundTowardsZero(data.loaded / data.total * 100);

                    for (const file of data.files) {
                        if (file.row) {
                            var fileuploadProgress = file.row.children(".ui-fileupload-progress").find("> .ui-progressbar");
                            fileuploadProgress.attr("aria-valuenow", progress);
                            fileuploadProgress.find("> .ui-progressbar-value").css({
                                width: progress + "%",
                                display: "block"
                            });
                        }
                    }
                }
            },
            done: function(_, data) {
                $this.uploadedFileCount += data.files.length;
                $this.removeFiles(data.files);

                // drag´n´drop - Github #11879, #12207
                const dataFileInput = $(PrimeFaces.escapeClientId(data.paramName + '_input'));
                if (dataFileInput.length > 0) {
                    let dataTransferCleaned = new DataTransfer();

                    if (dataFileInput[0] instanceof HTMLInputElement) {
                        for (const file of dataFileInput[0].files ?? []) {
                            if (!data.files.includes(file)) {
                                dataTransferCleaned.items.add(file);
                            }
                        }
    
                        dataFileInput[0].files = dataTransferCleaned.files;
                    }
                }

                PrimeFaces.ajax.Response.handle(data.result as XMLDocument, data.textStatus, data.jqXHR, null);
                
                if($this.cfg.global) {
                    $(document).trigger('pfAjaxSuccess', [data.jqXHR, this]);
                }
            },
            always: function(e, data) {
                if (data.jqXHR) {
                    if ($this.cfg.global) {
                        $(document).trigger('pfAjaxComplete', [data.jqXHR, this, data.jqXHR.pfArgs]);
                    }
                    if ($this.cfg.oncomplete) {
                        $this.cfg.oncomplete.call($this, data.jqXHR.pfArgs ?? {}, data);
                    }
                }
            },

            chunkbeforesend: (_, data) => {
                const params = this.createPostData();
                const file = data.files[0];
                if (file) {
                    params.push({name : 'X-File-Id', value: this.createXFileId(file)});
                }
                data.formData = params;
            }
        };

        this.jq.fileupload(this.ucfg);
    }
    
    override destroy(): void {
        try {
            this.jq.fileupload("destroy");
        } catch (err) {
            // this can throw file upload not initialized yet if an upload was never performed.
            PrimeFaces.debug("Could not destroy FileUpload: " + err);
        }
        super.destroy();
    }

    /**
     * Adds a file selected by the user to this upload widget.
     * @param file A file to add.
     * @param data The data from the selected file.
     */
    private addFileToRow(file: File, data: JQueryFileUpload.AddCallbackData): void {
        if (this.emptyFacet.length > 0) {
            this.emptyFacet.hide();
            this.filesFacet.parent().show();
        }

        const row = $('<div class="ui-fileupload-row"></div>')
                .append('<div class="ui-fileupload-preview"></td>')
                .append('<div class="ui-fileupload-filename">' + PrimeFaces.escapeHTML(file.name) + '</div>')
                .append('<div>' + PrimeFaces.utils.formatBytes(file.size) + '</div>')
                .append('<div class="ui-fileupload-progress"></div>')
                .append('<div><button class="ui-fileupload-cancel ui-button ui-widget ui-state-default ui-button-icon-only"><span class="ui-button-icon-left ui-icon ui-icon ui-icon-close"></span><span class="ui-button-text">ui-button</span></button></div>')
                .appendTo(this.filesFacet);

        if(this.filesFacet.children('.ui-fileupload-row').length > 1) {
            $('<div class="ui-widget-content"></div>').prependTo(row);
        }

        //preview
        if(window.File && window.FileReader && FileUpload.IMAGE_TYPES.test(file.name)) {
            const imageCanvas = $(document.createElement("canvas"))
                                    .appendTo(row.children('div.ui-fileupload-preview'));
            const context = imageCanvas.get(0)?.getContext('2d');
            const winURL = window.URL||window.webkitURL;
            const url = winURL.createObjectURL(file);
            const img = new Image();

            img.onload = () => {
                let imgWidth: number;
                let scale = 1;

                if((this.cfg.previewWidth ?? 0) > img.width) {
                    imgWidth = img.width;
                }
                else {
                    imgWidth = this.cfg.previewWidth ?? 0;
                    scale = imgWidth / img.width;
                }

                const imgHeight = PrimeFaces.utils.roundTowardsZero(img.height * scale);

                imageCanvas.attr({width:imgWidth, height: imgHeight});
                context?.drawImage(img, 0, 0, imgWidth, imgHeight);
            };

            img.src = url;
        }

        //progress
        row.children('div.ui-fileupload-progress')
                .append('<div class="ui-progressbar ui-widget ui-widget-content" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="ui-progressbar-value ui-widget-header" style="display: none; width: 0%;"></div></div>');

        file.row = row;
        file.row.data('fileId', this.fileId++);
        file.row.data('filedata', data);

        this.files.push(file);

        if(this.cfg.auto) {
            this.upload();
        }

        this.postSelectFile(data);
    }

    /**
     * Called after a file was added to this upload widget. Takes care of the UI buttons.
     * @param data Data of the selected file.
     */
    private postSelectFile(data: JQueryFileUpload.AddCallbackData): void {
        if(this.files.length > 0) {
            this.enableButton(this.uploadButton);
            this.enableButton(this.cancelButton);
        }

        this.fileAddIndex++;
        if(this.fileAddIndex === (data.originalFiles.length)) {
            this.fileAddIndex = 0;
        }
    }

    /**
     * Sets up all events listeners for this file upload widget.
     */
    private bindEvents(): void {
        var $this = this;

        PrimeFaces.skinButton(this.buttonBar.children('button'));

        var isChooseButtonClick = false;

        this.chooseButton.off('mouseover.fileupload mouseout.fileupload mouseup.fileupload focus.fileupload blur.fileupload mousedown.fileupload click.fileupload keydown.fileupload');
        this.chooseButton.on('mouseover.fileupload', function(){
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-hover');
            }
        })
        .on('mouseout.fileupload', function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        })
        .on('mouseup.fileupload', function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        })
        .on('focus.fileupload', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.fileupload', function() {
            $(this).removeClass('ui-state-focus');
            isChooseButtonClick = false;
        })
        .on('mousedown.fileupload', function() {
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        })
        .on('click.fileupload', (e) => {
            this.show();
        })
        .on('keydown.fileupload', function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                $this.show();
                $(this).trigger('blur');
                e.preventDefault();
            }
        });

        this.chooseButton.children('input').off('click.fileupload').on('click.fileupload', function(e){
            if (isChooseButtonClick) {
                isChooseButtonClick = false;
                e.preventDefault();
                e.stopPropagation();
            }
            else {
                isChooseButtonClick = true;
            }
        });

        this.uploadButton.off('click.fileupload').on('click.fileupload', (e) => {
            e.preventDefault();

            // GitHub #6396 allow cancel of upload with callback
            if (this.cfg.onupload) {
                if (this.cfg.onupload.call(this) === false) {
                    return false;
                }
            }

            this.disableButton(this.uploadButton);
            this.disableButton(this.cancelButton);

            this.upload();

            return undefined;
        });

        this.cancelButton.off('click.fileupload').on('click.fileupload', (e) => {
            this.clear();
            this.disableButton(this.uploadButton);
            this.disableButton(this.cancelButton);

            e.preventDefault();
        });

        this.clearMessageLink.off('click.fileupload').on('click.fileupload', (e) => {
            this.messageContainer.fadeOut(() => {
                this.messageList.children().remove();
            });

            e.preventDefault();
        });

        this.rowCancelActionSelector = this.jqId + " .ui-fileupload-files .ui-fileupload-cancel";

        const namespace = '.fileupload' + this.id;
        $(document).off(namespace, this.rowCancelActionSelector)
                .on('mouseover'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-hover');
                })
                .on('mouseout'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).removeClass('ui-state-hover ui-state-active');
                })
                .on('mousedown'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-active').removeClass('ui-state-hover');
                })
                .on('mouseup'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-hover').removeClass('ui-state-active');
                })
                .on('focus'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-focus');
                })
                .on('blur'+namespace, this.rowCancelActionSelector, null, function(e) {
                    $(this).removeClass('ui-state-focus');
                })
                .on('click'+namespace, this.rowCancelActionSelector, null, function(e) {
                    const row = $(this).closest('.ui-fileupload-row');
                    const removedFile = $.grep($this.files, function (value) {
                         return (value.row?.data('fileId') === row.data('fileId'));
                    });

                    if (removedFile[0]) {
                        if (removedFile[0].ajaxRequest) {
                            removedFile[0].ajaxRequest.abort();
                        }

                        $this.removeFile(removedFile[0]);

                        if ($this.files.length === 0) {
                            $this.disableButton($this.uploadButton);
                            $this.disableButton($this.cancelButton);

                            if ($this.emptyFacet.length > 0) {
                                $this.emptyFacet.show();
                                $this.filesFacet.parent().hide();
                            }
                        }
                    }

                    e.preventDefault();
                });
        this.addDestroyListener(function() {
            $(document).off(namespace);
        });

        if (this.dropZone) {
            this.dropZone
                    .off('dragover.fucdropzone dragenter.fucdropzone dragleave.fucdropzone drop.fucdropzone dragdrop.fucdropzone')
                    .on('dragover.fucdropzone', function(e){
                        e.preventDefault();
                    })
                    .on('dragenter.fucdropzone', function(e){
                        e.preventDefault();
                        $this.dragoverCount++;
                        $this.dropZone?.addClass('ui-state-drag');
                    })
                    .on('dragleave.fucdropzone', function(e){
                        $this.dragoverCount--;
                        if ($this.dragoverCount === 0) {
                            $this.dropZone?.removeClass('ui-state-drag');
                        }
                    })
                    .on('drop.fucdropzone dragdrop.fucdropzone', function(e){
                        $this.dragoverCount = 0;
                        $this.dropZone?.removeClass('ui-state-drag');
                    });
        }
    }

    /**
     * Uploads the selected files to the server.
     */
    private upload(): void {
        if(this.cfg.global) {
            $(document).trigger('pfAjaxStart');
        }

        for (const file of this.files) {
            file.ajaxRequest = file.row?.data('filedata');
            file.ajaxRequest?.submit();
        }
    }

    /**
     * Creates the HTML post data for uploading the selected files.
     * @return Parameters to post when upload the files.
     */
    private createPostData(): JQuery.NameValuePair[] {
        const process = this.cfg.process
            ? this.id + ' ' + PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.process).join(' ')
            : this.id;
        const params = this.form.serializeArray();

        const parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);

        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_REQUEST_PARAM, "true", parameterPrefix);
        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_PROCESS_PARAM, process, parameterPrefix);
        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_SOURCE_PARAM, this.id, parameterPrefix);
        PrimeFaces.ajax.Request.addParam(params, this.id + "_totalFilesCount", String(this.files.length), parameterPrefix);

        if (this.cfg.update) {
            var update = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.update).join(' ');
            PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_UPDATE_PARAM, update, parameterPrefix);
        }
        if (this.cfg.ignoreAutoUpdate) {
            PrimeFaces.ajax.Request.addParam(params, PrimeFaces.IGNORE_AUTO_UPDATE_PARAM, "true", parameterPrefix);
        }

        return params;
    }


    /**
     * Creates a unique identifier (file key) for a given file. That identifier consists e.g. of the name of the
     * uploaded file, its last modified-attribute etc. This is used by the server to identify uploaded files.
     * @param file A file for which to create an identifier.
     * @return An identifier for the given file.
     */
    private createXFileId(file: File): string {
        return [file.name, file.lastModified, file.type, file.size].join();
    }

    /**
     * Removes the given uploaded file from this upload widget.
     * @param files Files to remove from this widget.
     */
    private removeFiles(files: File[]) {
        for (const file of files) {
            this.removeFile(file);
        }
    }

    /**
     * Removes the given uploaded file from this upload widget.
     * @param file File to remove from this widget.
     */
    private removeFile(file: File): void {
        this.files = $.grep(this.files, (value) => {
            return value.row?.data('fileId') === file.row?.data('fileId');
        }, true);

        if (file.row) {
            this.removeFileRow(file.row);
            file.row = null;
        }
    }

    /**
     * Removes a row with an uploaded file form this upload widget.
     * @param row Row of an uploaded file to remove.
     */
    private removeFileRow(row: JQuery): void {
        if(row) {
            this.disableButton(row.find('> div:last-child').children('.ui-fileupload-cancel'));

            row.fadeOut(function() {
                $(this).remove();
            });
        }
    }

    /**
     * Clears this file upload field, i.e. removes all uploaded files.
     */
    clear(): void {
        for (const file of this.files) {
            if (file.row) {
                this.removeFileRow(file.row);
                file.row = null;
            }
        }

        this.clearMessages();

        this.files = [];
    }

    /**
     * Displays the current error messages on this widget.
     */
    private renderMessages(): void {
        var markup = '<div class="ui-messages ui-widget ui-helper-hidden ui-fileupload-messages"><div class="ui-messages-error">' +
                '<a class="ui-messages-close" href="#"><span class="ui-icon ui-icon-close"></span></a>' +
                '<span class="ui-messages-error-icon"></span>' +
                '<ul></ul>' +
                '</div></div>';

        this.messageContainer = $(markup).prependTo(this.content);
        this.messageList = this.messageContainer.find('> .ui-messages-error > ul');
        this.clearMessageLink = this.messageContainer.find('> .ui-messages-error > a.ui-messages-close');
    }

    /**
     * Removes all error messages that are shown for this widget.
     */
    clearMessages(): void {
        this.messageContainer.hide();
        this.messageList.children().remove();
    }

    /**
     * Shows the given error message
     * @param msg Error message to show.
     */
    private showMessage(msg: PrimeType.FacesMessage) {
        this.messageList.append('<li><span class="ui-messages-error-summary">'
            + PrimeFaces.escapeHTML(msg.summary)
            + '</span><span class="ui-messages-error-detail">'
            + PrimeFaces.escapeHTML(msg.detail)
            + '</span></li>');
        this.messageContainer.show();
        msg.rendered = true;
    }

    /**
     * Disabled the given file upload button.
     * @param btn Button to disabled.
     */
    disableButton(btn: JQuery): void {
        btn.prop('disabled', true).attr('aria-disabled', "true").addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus');
    }

    /**
     * Enables the given file upload button.
     * @param {JQuery} btn Button to enable.
     */
    enableButton(btn: JQuery): void {
        btn.prop('disabled', false).attr('aria-disabled', "false").removeClass('ui-state-disabled');
    }

    /**
     * Brings up the native file selection dialog.
     */
    show(): void {
        this.chooseButton.children('input').trigger('click');
    }
}
