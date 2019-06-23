/**
 * PrimeFaces FileUpload Widget
 */
PrimeFaces.widget.FileUpload = PrimeFaces.widget.BaseWidget.extend({

    IMAGE_TYPES: /(\.|\/)(gif|jpe?g|png)$/i,

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
                                this.addFileToRow(file, data);
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
    },

    addFileToRow: function(file, data) {
        var $this = this,
            row = $('<div class="ui-fileupload-row"></div>').append('<div class="ui-fileupload-preview"></td>')
                .append('<div>' + PrimeFaces.escapeHTML(file.name) + '</div>')
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

    upload: function() {
        for(var i = 0; i < this.files.length; i++) {
            this.files[i].ajaxRequest = this.files[i].row.data('filedata');
            this.files[i].ajaxRequest.submit();
        }
    },

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

    removeFiles: function(files) {
        for (var i = 0; i < files.length; i++) {
            this.removeFile(files[i]);
        }
    },

    removeFile: function(file) {
        var $this = this;

        this.files = $.grep(this.files, function(value) {
            return (value.row.data('fileId') === file.row.data('fileId'));
        }, true);

        $this.removeFileRow(file.row);
        file.row = null;
    },

    removeFileRow: function(row) {
        if(row) {
            this.disableButton(row.find('> div:last-child').children('.ui-fileupload-cancel'));

            row.fadeOut(function() {
                $(this).remove();
            });
        }
    },

    clear: function() {
        for (var i = 0; i < this.files.length; i++) {
            this.removeFileRow(this.files[i].row);
            this.files[i].row = null;
        }

        this.clearMessages();

        this.files = [];
    },

    validate: function(file) {
        if (this.cfg.allowTypes && !(this.cfg.allowTypes.test(file.type) || this.cfg.allowTypes.test(file.name))) {
            return this.cfg.invalidFileMessage;
        }

        if (this.cfg.maxFileSize && file.size > this.cfg.maxFileSize) {
            return this.cfg.invalidSizeMessage;
        }

        return null;
    },

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

    clearMessages: function() {
        this.messageContainer.hide();
        this.messageList.children().remove();
    },

    showMessage: function(msg) {
        var summary = msg.summary,
        detail = '';

        if(msg.filename && msg.filesize) {
            detail = this.cfg.messageTemplate.replace('{name}', msg.filename).replace('{size}', this.formatSize(msg.filesize));
        }

        this.messageList.append('<li><span class="ui-messages-error-summary">' + PrimeFaces.escapeHTML(summary) + '</span><span class="ui-messages-error-detail">' + PrimeFaces.escapeHTML(detail) + '</span></li>');
        this.messageContainer.show();
    },

    disableButton: function(btn) {
        btn.prop('disabled', true).attr('aria-disabled', true).addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus');
    },

    enableButton: function(btn) {
        btn.prop('disabled', false).attr('aria-disabled', false).removeClass('ui-state-disabled');
    }
});

/**
 * PrimeFaces Simple FileUpload Widget
 */
PrimeFaces.widget.SimpleFileUpload = PrimeFaces.widget.BaseWidget.extend({

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

    validate: function(input, file) {
        var $this = this;

        if(file && $this.cfg.maxFileSize && file.size > $this.cfg.maxFileSize) {
            $(input).replaceWith($(input).val('').clone(true));
            return $this.cfg.invalidSizeMessage;
        }
        return null;
    }

});