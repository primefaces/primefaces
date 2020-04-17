/**
 * __PrimeFaces FileUpload Widget__
 * 
 * FileUpload goes beyond the browser input `type="file"` functionality and features an HTML5 powered rich solution with
 * graceful degradation for legacy browsers.
 * 
 * @typedef PrimeFaces.widget.FileUpload.OnAddCallback Callback invoked when file was selected and is added to this
 * widget. See also {@link FileUploadCfg.onAdd}.
 * @this {PrimeFaces.widget.FileUpload} PrimeFaces.widget.FileUpload.OnAddCallback 
 * @param {File} PrimeFaces.widget.FileUpload.OnAddCallback.file The file that was selected for the upload.
 * @param {(processedFile: File) => void} PrimeFaces.widget.FileUpload.OnAddCallback.callback Callback that needs to be
 * invoked with the file that should be added to the upload queue.
 * 
 * @typedef PrimeFaces.widget.FileUpload.OnCancelCallback Callback that is invoked when a file upload was canceled. See
 * also {@link FileUploadCfg.oncancel}.
 * @this {PrimeFaces.widget.FileUpload} PrimeFaces.widget.FileUpload.OnCancelCallback 
 * 
 * @typedef PrimeFaces.widget.FileUpload.OnCompleteCallback Callback that is invoked after a file was uploaded to the
 * server successfully. See also {@link FileUploadCfg.oncomplete}.
 * @this {PrimeFaces.widget.FileUpload} PrimeFaces.widget.FileUpload.OnCompleteCallback 
 * @param {PrimeFaces.ajax.PrimeFacesArgs} PrimeFaces.widget.FileUpload.OnCompleteCallback.pfArgs The additional
 * arguments from the jQuery XHR requests.
 * @param {JQueryFileUpload.JQueryAjaxCallbackData} PrimeFaces.widget.FileUpload.OnCompleteCallback.data Details about
 * the uploaded file or files.
 * 
 * @typedef PrimeFaces.widget.FileUpload.OnErrorCallback Callback that is invoked when a file could not be uploaded to
 * the server. See also {@link FileUploadCfg.onerror}.
 * @this {PrimeFaces.widget.FileUpload} PrimeFaces.widget.FileUpload.OnErrorCallback 
 * @param {JQuery.jqXHR} PrimeFaces.widget.FileUpload.OnErrorCallback.jqXHR The XHR object from the HTTP request.
 * @param {string} PrimeFaces.widget.FileUpload.OnErrorCallback.textStatus The HTTP status text of the failed request.
 * @param {PrimeFaces.ajax.PrimeFacesArgs} PrimeFaces.widget.FileUpload.OnErrorCallback.pfArgs The additional arguments
 * from the jQuery XHR request.
 * 
 * @typedef PrimeFaces.widget.FileUpload.OnStartCallback Callback that is invoked at the beginning of a file upload,
 * when a file is sent to the server. See also {@link FileUploadCfg.onstart}.
 * @this {PrimeFaces.widget.FileUpload} PrimeFaces.widget.FileUpload.OnStartCallback 
 * 
 * @interface {PrimeFaces.widget.FileUpload.UploadMessage} UploadMessage A error message for a file upload widget.
 * @prop {number} UploadMessage.filesize The size of the uploaded file in bytes.
 * @prop {string} UploadMessage.filename The name of the uploaded file.
 * @prop {string} UploadMessage.summary A short summary of this message.
 * 
 * @interface {PrimeFaces.widget.FileUpload.UploadFile} UploadFile Represents an uploaded file added to the upload
 * widget.
 * @prop {JQuery} UploadFile.row Row of an uploaded file.
 * 
 * @prop {JQueryFileUpload.FileUploadOptions} ucfg Options for the BlueImp jQuery file upload plugin.
 * @prop {JQuery} form The DOM element for the form containing this upload widget.
 * @prop {JQuery} buttonBar The DOM element for the bar with the buttons of this widget.
 * @prop {JQuery} chooseButton The DOM element for the button for selecting a file.
 * @prop {JQuery} uploadButton The DOM element for the button for starting the file upload.
 * @prop {JQuery} cancelButton The DOM element for the button for canceling a file upload.
 * @prop {JQuery} content The DOM element for the content of this widget.
 * @prop {JQuery} filesTbody The DOM element for the table tbody with the files.
 * @prop {JQuery} input The DOM element for the file input element.
 * @prop {string[]} sizes Suffixes for formatting files sizes.
 * @prop {File[]} files List of currently selected files.
 * @prop {number} fileAddIndex Current index where to add files.
 * @prop {number} uploadedFileCount Number of currently uploaded files.
 * @prop {string} fileId ID of the current file.
 * @prop {number} width Width of the preview.
 * @prop {number} height Height of the preview.
 * @prop {string} rowActionSelector Selector for the available actions (buttons) of a row.
 * @prop {string} rowCancelActionSelector Selector for the button for canceling a file upload.
 * @prop {string} clearMessagesSelector Selector for the button to clear the error messages.
 * 
 * @interface {PrimeFaces.widget.FileUploadCfg} cfg The configuration for the {@link  FileUpload| FileUpload widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {RegExp} cfg.allowTypes Regular expression for accepted file types.
 * @prop {boolean} cfg.auto When set to true, selecting a file starts the upload process implicitly.
 * @prop {boolean} cfg.dnd Whether drag and drop is enabled.
 * @prop {boolean} cfg.disabled Whether this file upload is disabled.
 * @prop {number} cfg.fileLimit Maximum number of files allowed to upload.
 * @prop {string} cfg.fileLimitMessage Message to display when file limit exceeds.
 * @prop {string} cfg.invalidFileMessage Message to display when file is not accepted.
 * @prop {string} cfg.invalidSizeMessage Message to display when size limit exceeds.
 * @prop {number} cfg.maxFileSize Maximum allowed size in bytes for files.
 * @prop {string} cfg.messageTemplate Message template to use when displaying file validation errors.
 * @prop {PrimeFaces.widget.FileUpload.OnAddCallback} cfg.onAdd Callback invoked when an uploaded file is added.
 * @prop {PrimeFaces.widget.FileUpload.OnCancelCallback} cfg.oncancel Callback that is invoked when a file upload was
 * canceled.
 * @prop {PrimeFaces.widget.FileUpload.OnCompleteCallback} cfg.oncomplete Callback that is invoked after a file was
 * uploaded to the server successfully.
 * @prop {PrimeFaces.widget.FileUpload.OnErrorCallback} cfg.onerror Callback that is invoked when a file could not be
 * uploaded to the server.
 * @prop {PrimeFaces.widget.FileUpload.OnStartCallback} cfg.onstart Callback that is invoked at the beginning of a file
 * upload, when a file is sent to the server.
 * @prop {number} cfg.previewWidth Width for image previews in pixels.
 * @prop {string} cfg.process Component(s) to process in fileupload request.
 * @prop {boolean} cfg.sequentialUploads `true` to upload files one after each other, `false` to upload in parallel.
 * @prop {string} cfg.update Component(s) to update after fileupload completes.
 */
PrimeFaces.widget.FileUpload = PrimeFaces.widget.BaseWidget.extend({

    /**
     * Regular expression that matches image files for which a preview can be shown.
     * @type {RegExp}
     */
    IMAGE_TYPES: /(\.|\/)(gif|jpe?g|png)$/i,

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        if(this.cfg.disabled) {
            return;
        }

        this.ucfg = {};
        this.form = this.jq.closest('form');
        this.buttonBar = this.jq.children('.ui-fileupload-buttonbar');
        this.chooseButton = this.buttonBar.children('.ui-fileupload-choose');
        this.uploadButton = this.buttonBar.children('.ui-fileupload-upload');
        this.cancelButton = this.buttonBar.children('.ui-fileupload-cancel');
        this.content = this.jq.children('.ui-fileupload-content');
        this.filesTbody = this.content.find('> div.ui-fileupload-files > div');
        this.sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        this.files = [];
        this.fileAddIndex = 0;
        this.cfg.invalidFileMessage = this.cfg.invalidFileMessage || 'Invalid file type';
        this.cfg.invalidSizeMessage = this.cfg.invalidSizeMessage || 'Invalid file size';
        this.cfg.fileLimitMessage = this.cfg.fileLimitMessage || 'Maximum number of files exceeded';
        this.cfg.messageTemplate = this.cfg.messageTemplate || '{name} {size}';
        this.cfg.previewWidth = this.cfg.previewWidth || 80;
        this.uploadedFileCount = 0;
        this.fileId = 0;

        this.renderMessages();

        this.bindEvents();

        var $this = this,
            postURL = this.form.attr('action'),
            encodedURLfield = this.form.children("input[name*='javax.faces.encodedURL']");

        //portlet support
        var porletFormsSelector = null;
        if(encodedURLfield.length > 0) {
            porletFormsSelector = 'form[action="' + postURL + '"]';
            postURL = encodedURLfield.val();
        }

        this.ucfg = {
            url: postURL,
            portletForms: porletFormsSelector,
            paramName: this.id,
            dataType: 'xml',
            dropZone: (this.cfg.dnd === false) ? null : this.jq,
            sequentialUploads: this.cfg.sequentialUploads,
            formData: function() {
                return $this.createPostData();
            },
            beforeSend: function(xhr, settings) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                xhr.pfSettings = settings;
                xhr.pfArgs = {}; // default should be an empty object
            },
            start: function(e) {
                if($this.cfg.onstart) {
                    $this.cfg.onstart.call($this);
                }
            },
            add: function(e, data) {
                $this.chooseButton.removeClass('ui-state-hover ui-state-focus');

                if($this.fileAddIndex === 0) {
                    $this.clearMessages();
                }

                if($this.cfg.fileLimit && ($this.uploadedFileCount + $this.files.length + 1) > $this.cfg.fileLimit) {
                    $this.clearMessages();
                    $this.showMessage({
                        summary: $this.cfg.fileLimitMessage
                    });

                    return;
                }

                var file = data.files ? data.files[0] : null;
                if(file) {
                    var validMsg = $this.validate(file);

                    if(validMsg) {
                        $this.showMessage({
                            summary: validMsg,
                            filename: file.name,
                            filesize: file.size
                        });

                        $this.postSelectFile(data);
                    }
                    else {
                        if($this.cfg.onAdd) {
                            $this.cfg.onAdd.call($this, file, function(processedFile) {
                                file = processedFile;
                                data.files[0] = processedFile;
                                $this.addFileToRow(file, data);
                            });
                        }
                        else {
                            $this.addFileToRow(file, data);
                        }
                    }
                }
            },
            send: function(e, data) {
                if(!window.FormData) {
                    for(var i = 0; i < data.files.length; i++) {
                        var file = data.files[i];
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
                    if ($this.cfg.oncancel) {
                        $this.cfg.oncancel.call($this);
                    }
                    return;
                }
                if($this.cfg.onerror) {
                    $this.cfg.onerror.call($this, data.jqXHR, data.textStatus, data.jqXHR.pfArgs);
                }
            },
            progress: function(e, data) {
                if(window.FormData) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    for(var i = 0; i < data.files.length; i++) {
                        var file = data.files[i];
                        if(file.row) {
                            file.row.children('.ui-fileupload-progress').find('> .ui-progressbar > .ui-progressbar-value').css({
                                width: progress + '%',
                                display: 'block'
                            });
                        }
                    }
                }
            },
            done: function(e, data) {
                $this.uploadedFileCount += data.files.length;
                $this.removeFiles(data.files);

                PrimeFaces.ajax.Response.handle(data.result, data.textStatus, data.jqXHR, null);
            },
            always: function(e, data) {
                if($this.cfg.oncomplete) {
                    $this.cfg.oncomplete.call($this, data.jqXHR.pfArgs, data);
                }
            }
        };

        this.jq.fileupload(this.ucfg);
        this.input = $(this.jqId + '_input');
    },

    /**
     * Adds a file selected by the user to this upload widget.
     * @private
     * @param {File} file A file to add.
     * @param {JQueryFileUpload.AddCallbackData} data The data from the selected file.
     */
    addFileToRow: function(file, data) {
        var $this = this,
            row = $('<div class="ui-fileupload-row"></div>').append('<div class="ui-fileupload-preview"></td>')
                .append('<div class="ui-fileupload-filename">' + PrimeFaces.escapeHTML(file.name) + '</div>')
                .append('<div>' + this.formatSize(file.size) + '</div>')
                .append('<div class="ui-fileupload-progress"></div>')
                .append('<div><button class="ui-fileupload-cancel ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"><span class="ui-button-icon-left ui-icon ui-icon ui-icon-close"></span><span class="ui-button-text">ui-button</span></button></div>')
                .appendTo(this.filesTbody);

        if(this.filesTbody.children('.ui-fileupload-row').length > 1) {
            $('<div class="ui-widget-content"></div>').prependTo(row);
        }

        //preview
        if(window.File && window.FileReader && $this.IMAGE_TYPES.test(file.name)) {
            var imageCanvas = $('<canvas></canvas>')
                                    .appendTo(row.children('div.ui-fileupload-preview')),
            context = imageCanvas.get(0).getContext('2d'),
            winURL = window.URL||window.webkitURL,
            url = winURL.createObjectURL(file),
            img = new Image();

            img.onload = function() {
                var imgWidth = null, imgHeight = null, scale = 1;

                if($this.cfg.previewWidth > this.width) {
                    imgWidth = this.width;
                }
                else {
                    imgWidth = $this.cfg.previewWidth;
                    scale = $this.cfg.previewWidth / this.width;
                }

                var imgHeight = parseInt(this.height * scale);

                imageCanvas.attr({width:imgWidth, height: imgHeight});
                context.drawImage(img, 0, 0, imgWidth, imgHeight);
            };

            img.src = url;
        }

        //progress
        row.children('div.ui-fileupload-progress').append('<div class="ui-progressbar ui-widget ui-widget-content ui-corner-all" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="ui-progressbar-value ui-widget-header ui-corner-left" style="display: none; width: 0%;"></div></div>');

        file.row = row;
        file.row.data('fileId', this.fileId++);
        file.row.data('filedata', data);

        this.files.push(file);

        if(this.cfg.auto) {
            this.upload();
        }

        this.postSelectFile(data);
    },

    /**
     * Called after a file was added to this upload widget. Takes care of the UI buttons.
     * @private
     * @param {JQueryFileUpload.AddCallbackData} data Data of the selected file.
     */
    postSelectFile: function(data) {
        if(this.files.length > 0) {
            this.enableButton(this.uploadButton);
            this.enableButton(this.cancelButton);
        }

        this.fileAddIndex++;
        if(this.fileAddIndex === (data.originalFiles.length)) {
            this.fileAddIndex = 0;
        }
    },

    /**
     * Sets up all events listeners for this file upload widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.buttonBar.children('button'));

        this.chooseButton.on('mouseover.fileupload', function(){
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-hover');
            }
        })
        .on('mouseout.fileupload', function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        })
        .on('mousedown.fileupload', function() {
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        })
        .on('mouseup.fileupload', function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        });

        var isChooseButtonClick = false;
        this.chooseButton.on('focus.fileupload', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.fileupload', function() {
            $(this).removeClass('ui-state-focus');
            isChooseButtonClick = false;
        });

        // For JAWS support
        this.chooseButton.on('click.fileupload', function() {
            $this.chooseButton.children('input').trigger('click');
        })
        .on('keydown.fileupload', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $this.chooseButton.children('input').trigger('click');
                $(this).blur();
                e.preventDefault();
            }
        });

        this.chooseButton.children('input').on('click', function(e){
            if(isChooseButtonClick) {
                isChooseButtonClick = false;
                e.preventDefault();
                e.stopPropagation();
            }
            else {
                isChooseButtonClick = true;
            }
        });

        this.uploadButton.on('click.fileupload', function(e) {
            $this.disableButton($this.uploadButton);
            $this.disableButton($this.cancelButton);

            $this.upload();

            e.preventDefault();
        });

        this.cancelButton.on('click.fileupload', function(e) {
            $this.clear();
            $this.disableButton($this.uploadButton);
            $this.disableButton($this.cancelButton);

            e.preventDefault();
        });

        this.clearMessageLink.on('click.fileupload', function(e) {
            $this.messageContainer.fadeOut(function() {
                $this.messageList.children().remove();
            });

            e.preventDefault();
        });

        this.rowActionSelector = this.jqId + " .ui-fileupload-files button";
        this.rowCancelActionSelector = this.jqId + " .ui-fileupload-files .ui-fileupload-cancel";
        this.clearMessagesSelector = this.jqId + " .ui-messages .ui-messages-close";

        $(document).off('mouseover.fileupload mouseout.fileupload mousedown.fileupload mouseup.fileupload focus.fileupload blur.fileupload click.fileupload ', this.rowCancelActionSelector)
                .on('mouseover.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-hover');
                })
                .on('mouseout.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).removeClass('ui-state-hover ui-state-active');
                })
                .on('mousedown.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-active').removeClass('ui-state-hover');
                })
                .on('mouseup.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-hover').removeClass('ui-state-active');
                })
                .on('focus.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).addClass('ui-state-focus');
                })
                .on('blur.fileupload', this.rowCancelActionSelector, null, function(e) {
                    $(this).removeClass('ui-state-focus');
                })
                .on('click.fileupload', this.rowCancelActionSelector, null, function(e) {
                    var row = $(this).closest('.ui-fileupload-row');
                    var removedFile = $.grep($this.files, function (value) {
                         return (value.row.data('fileId') === row.data('fileId'));
                    });

                    if (removedFile[0]) {
                        if (removedFile[0].ajaxRequest) {
                            removedFile[0].ajaxRequest.abort();
                        }

                        $this.removeFile(removedFile[0]);

                        if ($this.files.length === 0) {
                            $this.disableButton($this.uploadButton);
                            $this.disableButton($this.cancelButton);
                        }
                    }

                    e.preventDefault();
                });
    },

    /**
     * Uploads the selected files to the server.
     * @private
     */
    upload: function() {
        for(var i = 0; i < this.files.length; i++) {
            this.files[i].ajaxRequest = this.files[i].row.data('filedata');
            this.files[i].ajaxRequest.submit();
        }
    },

    /**
     * Creates the HTML post data for uploading the selected files.
     * @private
     * @return {PrimeFaces.ajax.ServerCallbackParameter} Parameters to post when upload the files.
     */
    createPostData: function() {
        var process = this.cfg.process ? this.id + ' ' + PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.cfg.process).join(' ') : this.id;
        var params = this.form.serializeArray();

        var parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);

        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_REQUEST_PARAM, true, parameterPrefix);
        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_PROCESS_PARAM, process, parameterPrefix);
        PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_SOURCE_PARAM, this.id, parameterPrefix);

        if (this.cfg.update) {
            var update = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.cfg.update).join(' ');
            PrimeFaces.ajax.Request.addParam(params, PrimeFaces.PARTIAL_UPDATE_PARAM, update, parameterPrefix);
        }

        return params;
    },

    /**
     * Formats the given file size in a more human-friendly format, e.g. `1.5 MB` etc.
     * @param {number} bytes File size in bytes to format
     * @return {string} The given file size, formatted in a more human-friendly format.
     */
    formatSize: function(bytes) {
        if(bytes === undefined)
            return '';

        if (bytes === 0)
            return 'N/A';

        var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
        if (i === 0)
            return bytes + ' ' + this.sizes[i];
        else
            return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + this.sizes[i];
    },

    /**
     * Removes the given uploadeds file from this upload widget.
     * @private
     * @param {PrimeFaces.widget.FileUpload.UploadFile[]} files Files to remove from this widget.
     */
    removeFiles: function(files) {
        for (var i = 0; i < files.length; i++) {
            this.removeFile(files[i]);
        }
    },

    /**
     * Removes the given uploaded file from this upload widget.
     * @private
     * @param {PrimeFaces.widget.FileUpload.UploadFile} file File to remove from this widget.
     */
    removeFile: function(file) {
        var $this = this;

        this.files = $.grep(this.files, function(value) {
            return (value.row.data('fileId') === file.row.data('fileId'));
        }, true);

        $this.removeFileRow(file.row);
        file.row = null;
    },

    /**
     * Removes a row with an uploaded file form this upload widget.
     * @private
     * @param {JQuery} row Row of an uploaded file to remove.
     */
    removeFileRow: function(row) {
        if(row) {
            this.disableButton(row.find('> div:last-child').children('.ui-fileupload-cancel'));

            row.fadeOut(function() {
                $(this).remove();
            });
        }
    },

    /**
     * Clears this file upload field, i.e. removes all uploaded files.
     */
    clear: function() {
        for (var i = 0; i < this.files.length; i++) {
            this.removeFileRow(this.files[i].row);
            this.files[i].row = null;
        }

        this.clearMessages();

        this.files = [];
    },

    /**
     * Validates the given file against the current validation settings
     * @private
     * @param {File} file Uploaded file to validate.
     * @return {string | null} `null` if the given file is valid, or an error message otherwise.
     */
    validate: function(file) {
        if (this.cfg.allowTypes && !(this.cfg.allowTypes.test(file.type) || this.cfg.allowTypes.test(file.name))) {
            return this.cfg.invalidFileMessage;
        }

        if (this.cfg.maxFileSize && file.size > this.cfg.maxFileSize) {
            return this.cfg.invalidSizeMessage;
        }

        return null;
    },

    /**
     * Displays the current error messages on this widget.
     * @private
     */
    renderMessages: function() {
        var markup = '<div class="ui-messages ui-widget ui-helper-hidden ui-fileupload-messages"><div class="ui-messages-error ui-corner-all">' +
                '<a class="ui-messages-close" href="#"><span class="ui-icon ui-icon-close"></span></a>' +
                '<span class="ui-messages-error-icon"></span>' +
                '<ul></ul>' +
                '</div></div>';

        this.messageContainer = $(markup).prependTo(this.content);
        this.messageList = this.messageContainer.find('> .ui-messages-error > ul');
        this.clearMessageLink = this.messageContainer.find('> .ui-messages-error > a.ui-messages-close');
    },

    /**
     * Removes all error messages that are shown for this widget.
     */
    clearMessages: function() {
        this.messageContainer.hide();
        this.messageList.children().remove();
    },

    /**
     * Shows the given error message
     * @param {PrimeFaces.widget.FileUpload.UploadMessage} msg Error message to show.
     * @private
     */
    showMessage: function(msg) {
        var summary = msg.summary,
        detail = '';

        if(msg.filename && msg.filesize) {
            detail = this.cfg.messageTemplate.replace('{name}', msg.filename).replace('{size}', this.formatSize(msg.filesize));
        }

        this.messageList.append('<li><span class="ui-messages-error-summary">' + PrimeFaces.escapeHTML(summary) + '</span><span class="ui-messages-error-detail">' + PrimeFaces.escapeHTML(detail) + '</span></li>');
        this.messageContainer.show();
    },

    /**
     * Disabled the given file upload button.
     * @param {JQuery} btn Button to disabled.
     * @private
     */
    disableButton: function(btn) {
        btn.prop('disabled', true).attr('aria-disabled', true).addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus');
    },

    /**
     * Enables the given file upload button.
     * @param {JQuery} btn Button to enable.
     * @private
     */
    enableButton: function(btn) {
        btn.prop('disabled', false).attr('aria-disabled', false).removeClass('ui-state-disabled');
    },

    /**
     * Brings up the native file selection dialog.
     */
    show: function() {
        this.input.click();
    }
});

/**
 * __PrimeFaces Simple FileUpload Widget__
 * 
 * @prop {number} maxFileSize Maximum allowed size in bytes for files.
 * @prop {JQuery} button The DOM element for the button for selecting a file.
 * @prop {JQuery} display The DOM element for the UI display.
 * @prop {JQuery} input The DOM element for the file input element.
 * 
 * @interface {PrimeFaces.widget.SimpleFileUploadCfg} cfg The configuration for the {@link  SimpleFileUpload| SimpleFileUpload widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number} cfg.maxFileSize Maximum allowed size in bytes for files.
 * @prop {string} cfg.invalidSizeMessage Message to display when size limit exceeds.
 * @prop {boolean} cfg.skinSimple Applies theming to simple uploader.
 */
PrimeFaces.widget.SimpleFileUpload = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.invalidSizeMessage = this.cfg.invalidSizeMessage || 'Invalid file size';
        this.maxFileSize = this.cfg.maxFileSize;

        if(this.cfg.skinSimple) {
            this.button = this.jq.children('.ui-button');
            this.input = $(this.jqId + '_input');
            this.display = this.jq.children('.ui-fileupload-filename');

            if(!this.input.prop('disabled')) {
                this.bindEvents();
            }
        }
    },

    /**
     * Sets up all events listeners for this file upload widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.button.on('mouseover.fileupload', function(){
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-hover');
            }
        })
        .on('mouseout.fileupload', function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        })
        .on('mousedown.fileupload', function() {
            var el = $(this);
            if(!el.prop('disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        })
        .on('mouseup.fileupload', function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        });

        this.input.on('change.fileupload', function() {
            var files = $this.input[0].files;
            var file = files.length > 0 ? files[files.length - 1] : null;
            var validMsg = $this.validate($this.input[0], file);
            if(validMsg) {
                $this.display.text(validMsg);
            } else {
                $this.display.text($this.input.val().replace(/\\/g, '/').replace(/.*\//, ''));
            }
        })
        .on('focus.fileupload', function() {
            $this.button.addClass('ui-state-focus');
        })
        .on('blur.fileupload', function() {
            $this.button.removeClass('ui-state-focus');
        });

    },

    /**
     * Validates the given file against the current validation settings
     * @private
     * @param {HTMLElement} input File input element to validate.
     * @param {File} file Uploaded file to validate.
     * @return {string | null} `null` if the given file is valid, or an error message otherwise.
     */
    validate: function(input, file) {
        var $this = this;

        if(file && $this.cfg.maxFileSize && file.size > $this.cfg.maxFileSize) {
            $(input).replaceWith($(input).val('').clone(true));
            return $this.cfg.invalidSizeMessage;
        }
        return null;
    },

    /**
     * Brings up the native file selection dialog.
     */
    show: function() {
        if(this.cfg.skinSimple) {
            this.input.click();
        }
        else {
            this.jq.click();
        }
    }

});