/*
 * jQuery File Upload Plugin 3.7.1
 *
 * Copyright 2010, Sebastian Tschan, AQUANTUM
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 *
 * https://blueimp.net
 * http://www.aquantum.de
 */

/*jslint browser: true */
/*global File, FileReader, FormData, unescape, jQuery */

(function ($) {

    var defaultNamespace = 'file_upload',
        undef = 'undefined',
        func = 'function',
        num = 'number',
        FileUpload,
        methods,

        MultiLoader = function (callBack, numberComplete) {
            var loaded = 0;
            this.complete = function () {
                loaded += 1;
                if (loaded === numberComplete) {
                    callBack();
                }
            };
        };

    FileUpload = function (container) {
        var fileUpload = this,
            uploadForm,
            fileInput,
            settings = {
                namespace: defaultNamespace,
                uploadFormFilter: function (index) {
                    return true;
                },
                fileInputFilter: function (index) {
                    return true;
                },
                cssClass: defaultNamespace,
                dragDropSupport: true,
                dropZone: container,
                url: function (form) {
                    return form.attr('action');
                },
                method: function (form) {
                    return form.attr('method');
                },
                fieldName: function (input) {
                    return input.attr('name');
                },
                formData: function (form) {
                    return form.serializeArray();
                },
                multipart: true,
                multiFileRequest: false,
                withCredentials: false,
                forceIframeUpload: false
            },
            documentListeners = {},
            dropZoneListeners = {},
            protocolRegExp = /^http(s)?:\/\//,
            optionsReference,

            isXHRUploadCapable = function () {
                return typeof XMLHttpRequest !== undef && typeof File !== undef && (
                    !settings.multipart || typeof FormData !== undef || typeof FileReader !== undef
                );
            },

            initEventHandlers = function () {
                if (settings.dragDropSupport) {
                    if (typeof settings.onDocumentDragEnter === func) {
                        documentListeners['dragenter.' + settings.namespace] = function (e) {
                            settings.onDocumentDragEnter(e);
                        };
                    }
                    if (typeof settings.onDocumentDragLeave === func) {
                        documentListeners['dragleave.' + settings.namespace] = function (e) {
                            settings.onDocumentDragLeave(e);
                        };
                    }
                    documentListeners['dragover.'   + settings.namespace] = fileUpload.onDocumentDragOver;
                    documentListeners['drop.'       + settings.namespace] = fileUpload.onDocumentDrop;
                    $(document).bind(documentListeners);
                    if (typeof settings.onDragEnter === func) {
                        dropZoneListeners['dragenter.' + settings.namespace] = function (e) {
                            settings.onDragEnter(e);
                        };
                    }
                    if (typeof settings.onDragLeave === func) {
                        dropZoneListeners['dragleave.' + settings.namespace] = function (e) {
                            settings.onDragLeave(e);
                        };
                    }
                    dropZoneListeners['dragover.'   + settings.namespace] = fileUpload.onDragOver;
                    dropZoneListeners['drop.'       + settings.namespace] = fileUpload.onDrop;
                    settings.dropZone.bind(dropZoneListeners);
                }
                fileInput.bind('change.' + settings.namespace, fileUpload.onChange);
            },

            removeEventHandlers = function () {
                $.each(documentListeners, function (key, value) {
                    $(document).unbind(key, value);
                });
                $.each(dropZoneListeners, function (key, value) {
                    settings.dropZone.unbind(key, value);
                });
                fileInput.unbind('change.' + settings.namespace);
            },

            initUploadEventHandlers = function (files, index, xhr, settings) {
                if (typeof settings.onProgress === func) {
                    xhr.upload.onprogress = function (e) {
                        settings.onProgress(e, files, index, xhr, settings);
                    };
                }
                if (typeof settings.onLoad === func) {
                    xhr.onload = function (e) {
                        settings.onLoad(e, files, index, xhr, settings);
                    };
                }
                if (typeof settings.onAbort === func) {
                    xhr.onabort = function (e) {
                        settings.onAbort(e, files, index, xhr, settings);
                    };
                }
                if (typeof settings.onError === func) {
                    xhr.onerror = function (e) {
                        settings.onError(e, files, index, xhr, settings);
                    };
                }
            },

            getUrl = function (settings) {
                if (typeof settings.url === func) {
                    return settings.url(settings.uploadForm || uploadForm);
                }
                return settings.url;
            },

            getMethod = function (settings) {
                if (typeof settings.method === func) {
                    return settings.method(settings.uploadForm || uploadForm);
                }
                return settings.method;
            },

            getFieldName = function (settings) {
                if (typeof settings.fieldName === func) {
                    return settings.fieldName(settings.fileInput || fileInput);
                }
                return settings.fieldName;
            },

            getFormData = function (settings) {
                var formData;
                if (typeof settings.formData === func) {
                    return settings.formData(settings.uploadForm || uploadForm);
                } else if ($.isArray(settings.formData)) {
                    return settings.formData;
                } else if (settings.formData) {
                    formData = [];
                    $.each(settings.formData, function (name, value) {
                        formData.push({name: name, value: value});
                    });
                    return formData;
                }
                return [];
            },

            isSameDomain = function (url) {
                if (protocolRegExp.test(url)) {
                    var host = location.host,
                        indexStart = location.protocol.length + 2,
                        index = url.indexOf(host, indexStart),
                        pathIndex = index + host.length;
                    if ((index === indexStart || index === url.indexOf('@', indexStart) + 1) &&
                            (url.length === pathIndex || $.inArray(url.charAt(pathIndex), ['/', '?', '#']) !== -1)) {
                        return true;
                    }
                    return false;
                }
                return true;
            },

            nonMultipartUpload = function (file, xhr, sameDomain) {
                if (sameDomain) {
                    xhr.setRequestHeader('X-File-Name', unescape(encodeURIComponent(file.name)));
                }
                xhr.setRequestHeader('Content-Type', file.type);
                xhr.send(file);
            },

            formDataUpload = function (files, xhr, settings) {
                var formData = new FormData(),
                    i;
                $.each(getFormData(settings), function (index, field) {
                    formData.append(field.name, field.value);
                });
                for (i = 0; i < files.length; i += 1) {
                    formData.append(getFieldName(settings), files[i]);
                }
                xhr.send(formData);
            },

            loadFileContent = function (file, callBack) {
                var fileReader = new FileReader();
                fileReader.onload = function (e) {
                    file.content = e.target.result;
                    callBack();
                };
                fileReader.readAsBinaryString(file);
            },

            buildMultiPartFormData = function (boundary, files, filesFieldName, fields) {
                var doubleDash = '--',
                    crlf     = '\r\n',
                    formData = '';
                $.each(fields, function (index, field) {
                    formData += doubleDash + boundary + crlf +
                        'Content-Disposition: form-data; name="' +
                        unescape(encodeURIComponent(field.name)) +
                        '"' + crlf + crlf +
                        unescape(encodeURIComponent(field.value)) + crlf;
                });
                $.each(files, function (index, file) {
                    formData += doubleDash + boundary + crlf +
                        'Content-Disposition: form-data; name="' +
                        unescape(encodeURIComponent(filesFieldName)) +
                        '"; filename="' + unescape(encodeURIComponent(file.name)) + '"' + crlf +
                        'Content-Type: ' + file.type + crlf + crlf +
                        file.content + crlf;
                });
                formData += doubleDash + boundary + doubleDash + crlf;
                return formData;
            },

            fileReaderUpload = function (files, xhr, settings) {
                var boundary = '----MultiPartFormBoundary' + (new Date()).getTime(),
                    loader,
                    i;
                xhr.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + boundary);
                loader = new MultiLoader(function () {
                    xhr.sendAsBinary(buildMultiPartFormData(
                        boundary,
                        files,
                        getFieldName(settings),
                        getFormData(settings)
                    ));
                }, files.length);
                for (i = 0; i < files.length; i += 1) {
                    loadFileContent(files[i], loader.complete);
                }
            },

            upload = function (files, index, xhr, settings) {
                var url = getUrl(settings),
                    sameDomain = isSameDomain(url),
                    filesToUpload;
                initUploadEventHandlers(files, index, xhr, settings);
                xhr.open(getMethod(settings), url, true);
                if (sameDomain) {
                    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
                    xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                } else if (settings.withCredentials) {
                    xhr.withCredentials = true;
                }
                if (!settings.multipart) {
                    nonMultipartUpload(files[index], xhr, sameDomain);
                } else {
                    if (typeof index === num) {
                        filesToUpload = [files[index]];
                    } else {
                        filesToUpload = files;
                    }
                    if (typeof FormData !== undef) {
                        formDataUpload(filesToUpload, xhr, settings);
                    } else if (typeof FileReader !== undef) {
                        fileReaderUpload(filesToUpload, xhr, settings);
                    } else {
                        $.error('Browser does neither support FormData nor FileReader interface');
                    }
                }
            },

            handleUpload = function (event, files, input, form, index) {
                var xhr = new XMLHttpRequest(),
                    uploadSettings = $.extend({}, settings);
                uploadSettings.fileInput = input;
                uploadSettings.uploadForm = form;
                if (typeof uploadSettings.initUpload === func) {
                    uploadSettings.initUpload(
                        event,
                        files,
                        index,
                        xhr,
                        uploadSettings,
                        function () {
                            upload(files, index, xhr, uploadSettings);
                        }
                    );
                } else {
                    upload(files, index, xhr, uploadSettings);
                }
            },

            handleFiles = function (event, files, input, form) {
                var i;
                if (settings.multiFileRequest) {
                    handleUpload(event, files, input, form);
                } else {
                    for (i = 0; i < files.length; i += 1) {
                        handleUpload(event, files, input, form, i);
                    }
                }
            },

            legacyUploadFormDataInit = function (input, form, settings) {
                var formData = getFormData(settings);
                form.find(':input').not(':disabled')
                    .attr('disabled', true)
                    .addClass(settings.namespace + '_disabled');
                $.each(formData, function (index, field) {
                    $('<input type="hidden"/>')
                        .attr('name', field.name)
                        .val(field.value)
                        .addClass(settings.namespace + '_form_data')
                        .appendTo(form);
                });
                input
                    .attr('name', getFieldName(settings))
                    .appendTo(form);
            },

            legacyUploadFormDataReset = function (input, form, settings) {
                input.detach();
                form.find('.' + settings.namespace + '_disabled')
                    .removeAttr('disabled')
                    .removeClass(settings.namespace + '_disabled');
                form.find('.' + settings.namespace + '_form_data').remove();
            },

            legacyUpload = function (input, form, iframe, settings) {
                var originalAction = form.attr('action'),
                    originalMethod = form.attr('method'),
                    originalTarget = form.attr('target');
                iframe
                    .unbind('abort')
                    .bind('abort', function (e) {
                        iframe.readyState = 0;
                        // javascript:false as iframe src prevents warning popups on HTTPS in IE6
                        // concat is used here to prevent the "Script URL" JSLint error:
                        iframe.unbind('load').attr('src', 'javascript'.concat(':false;'));
                        if (typeof settings.onAbort === func) {
                            settings.onAbort(e, [{name: input.val(), type: null, size: null}], 0, iframe, settings);
                        }
                    })
                    .unbind('load')
                    .bind('load', function (e) {
                        iframe.readyState = 4;
                        if (typeof settings.onLoad === func) {
                            settings.onLoad(e, [{name: input.val(), type: null, size: null}], 0, iframe, settings);
                        }
                        // Fix for IE endless progress bar activity bug (happens on form submits to iframe targets):
                        $('<iframe src="javascript:false;" style="display:none"></iframe>').appendTo(form).remove();
                    });
                form
                    .attr('action', getUrl(settings))
                    .attr('method', getMethod(settings))
                    .attr('target', iframe.attr('name'));
                legacyUploadFormDataInit(input, form, settings);
                iframe.readyState = 2;
                form.get(0).submit();
                legacyUploadFormDataReset(input, form, settings);
                form
                    .attr('action', originalAction)
                    .attr('method', originalMethod)
                    .attr('target', originalTarget);
            },

            handleLegacyUpload = function (event, input, form) {
                // javascript:false as iframe src prevents warning popups on HTTPS in IE6:
                var iframe = $('<iframe src="javascript:false;" style="display:none" name="iframe_' +
                    settings.namespace + '_' + (new Date()).getTime() + '"></iframe>'),
                    uploadSettings = $.extend({}, settings);
                uploadSettings.fileInput = input;
                uploadSettings.uploadForm = form;
                iframe.readyState = 0;
                iframe.abort = function () {
                    iframe.trigger('abort');
                };
                iframe.bind('load', function () {
                    iframe.unbind('load');
                    if (typeof uploadSettings.initUpload === func) {
                        uploadSettings.initUpload(
                            event,
                            [{name: input.val(), type: null, size: null}],
                            0,
                            iframe,
                            uploadSettings,
                            function () {
                                legacyUpload(input, form, iframe, uploadSettings);
                            }
                        );
                    } else {
                        legacyUpload(input, form, iframe, uploadSettings);
                    }
                }).appendTo(form);
            },

            initUploadForm = function () {
                uploadForm = (container.is('form') ? container : container.find('form'))
                    .filter(settings.uploadFormFilter);
            },

            initFileInput = function () {
                fileInput = uploadForm.find('input:file')
                    .filter(settings.fileInputFilter);
            },

            replaceFileInput = function (input) {
                var inputClone = input.clone(true);
                $('<form/>').append(inputClone).get(0).reset();
                input.after(inputClone).detach();
                initFileInput();
            };

        this.onDocumentDragOver = function (e) {
            if (typeof settings.onDocumentDragOver === func &&
                    settings.onDocumentDragOver(e) === false) {
                return false;
            }
            e.preventDefault();
        };

        this.onDocumentDrop = function (e) {
            if (typeof settings.onDocumentDrop === func &&
                    settings.onDocumentDrop(e) === false) {
                return false;
            }
            e.preventDefault();
        };

        this.onDragOver = function (e) {
            if (typeof settings.onDragOver === func &&
                    settings.onDragOver(e) === false) {
                return false;
            }
            var dataTransfer = e.originalEvent.dataTransfer;
            if (dataTransfer) {
                dataTransfer.dropEffect = dataTransfer.effectAllowed = 'copy';
            }
            e.preventDefault();
        };

        this.onDrop = function (e) {
            if (typeof settings.onDrop === func &&
                    settings.onDrop(e) === false) {
                return false;
            }
            var dataTransfer = e.originalEvent.dataTransfer;
            if (dataTransfer && dataTransfer.files && isXHRUploadCapable()) {
                handleFiles(e, dataTransfer.files);
            }
            e.preventDefault();
        };

        this.onChange = function (e) {
            if (typeof settings.onChange === func &&
                    settings.onChange(e) === false) {
                return false;
            }
            var input = $(e.target),
                form = $(e.target.form);
            if (form.length === 1) {
                input.data(defaultNamespace + '_form', form);
                replaceFileInput(input);
            } else {
                form = input.data(defaultNamespace + '_form');
            }
            if (!settings.forceIframeUpload && e.target.files && isXHRUploadCapable()) {
                handleFiles(e, e.target.files, input, form);
            } else {
                handleLegacyUpload(e, input, form);
            }
        };

        this.init = function (options) {
            if (options) {
                $.extend(settings, options);
                optionsReference = options;
            }
            initUploadForm();
            initFileInput();
            if (container.data(settings.namespace)) {
                $.error('FileUpload with namespace "' + settings.namespace + '" already assigned to this element');
                return;
            }
            container
                .data(settings.namespace, fileUpload)
                .addClass(settings.cssClass);
            settings.dropZone.not(container).addClass(settings.cssClass);
            initEventHandlers();
        };

        this.options = function (options) {
            var oldCssClass,
                oldDropZone,
                uploadFormFilterUpdate,
                fileInputFilterUpdate;
            if (typeof options === undef) {
                return $.extend({}, settings);
            }
            if (optionsReference) {
                $.extend(optionsReference, options);
            }
            removeEventHandlers();
            $.each(options, function (name, value) {
                switch (name) {
                case 'namespace':
                    $.error('The FileUpload namespace cannot be updated.');
                    return;
                case 'uploadFormFilter':
                    uploadFormFilterUpdate = true;
                    fileInputFilterUpdate = true;
                    break;
                case 'fileInputFilter':
                    fileInputFilterUpdate = true;
                    break;
                case 'cssClass':
                    oldCssClass = settings.cssClass;
                    break;
                case 'dropZone':
                    oldDropZone = settings.dropZone;
                    break;
                }
                settings[name] = value;
            });
            if (uploadFormFilterUpdate) {
                initUploadForm();
            }
            if (fileInputFilterUpdate) {
                initFileInput();
            }
            if (typeof oldCssClass !== undef) {
                container
                    .removeClass(oldCssClass)
                    .addClass(settings.cssClass);
                (oldDropZone ? oldDropZone : settings.dropZone).not(container)
                    .removeClass(oldCssClass);
                settings.dropZone.not(container).addClass(settings.cssClass);
            } else if (oldDropZone) {
                oldDropZone.not(container).removeClass(settings.cssClass);
                settings.dropZone.not(container).addClass(settings.cssClass);
            }
            initEventHandlers();
        };

        this.option = function (name, value) {
            var options;
            if (typeof value === undef) {
                return settings[name];
            }
            options = {};
            options[name] = value;
            fileUpload.options(options);
        };

        this.destroy = function () {
            removeEventHandlers();
            container
                .removeData(settings.namespace)
                .removeClass(settings.cssClass);
            settings.dropZone.not(container).removeClass(settings.cssClass);
        };
    };

    methods = {
        init : function (options) {
            return this.each(function () {
                (new FileUpload($(this))).init(options);
            });
        },

        option: function (option, value, namespace) {
            namespace = namespace ? namespace : defaultNamespace;
            var fileUpload = $(this).data(namespace);
            if (fileUpload) {
                if (typeof option === 'string') {
                    return fileUpload.option(option, value);
                }
                return fileUpload.options(option);
            } else {
                $.error('No FileUpload with namespace "' + namespace + '" assigned to this element');
            }
        },

        destroy : function (namespace) {
            namespace = namespace ? namespace : defaultNamespace;
            return this.each(function () {
                var fileUpload = $(this).data(namespace);
                if (fileUpload) {
                    fileUpload.destroy();
                } else {
                    $.error('No FileUpload with namespace "' + namespace + '" assigned to this element');
                }
            });

        }
    };

    $.fn.fileUpload = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.fileUpload');
        }
    };

}(jQuery));

/*
 * jQuery File Upload User Interface Plugin 3.6
 *
 * Copyright 2010, Sebastian Tschan, AQUANTUM
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 *
 * https://blueimp.net
 * http://www.aquantum.de
 */

/*jslint browser: true */
/*global jQuery, FileReader, URL */

(function ($) {

    var undef = 'undefined',
        func = 'function',
        UploadHandler,
        methods,

        LocalImage = function (file, imageTypes) {
            var img,
                fileReader;
            if (!imageTypes.test(file.type)) {
                return null;
            }
            img = document.createElement('img');
            if (typeof URL !== undef && typeof URL.createObjectURL === func) {
                img.src = URL.createObjectURL(file);
                img.onload = function () {
                    URL.revokeObjectURL(this.src);
                };
                return img;
            }
            if (typeof FileReader !== undef) {
                fileReader = new FileReader();
                if (typeof fileReader.readAsDataURL === func) {
                    fileReader.onload = function (e) {
                        img.src = e.target.result;
                    };
                    fileReader.readAsDataURL(file);
                    return img;
                }
            }
            return null;
        };

    UploadHandler = function (container, options) {
        var uploadHandler = this,
            dragOverTimeout,
            isDropZoneEnlarged;

        this.dropZone = container;
        this.imageTypes = /^image\/(gif|jpeg|png)$/;
        this.previewSelector = '.file_upload_preview';
        this.progressSelector = '.file_upload_progress div';
        this.cancelSelector = '.file_upload_cancel button';
        this.cssClassSmall = 'file_upload_small';
        this.cssClassLarge = 'file_upload_large';
        this.cssClassHighlight = 'file_upload_highlight';
        this.dropEffect = 'highlight';
        this.uploadTable = this.downloadTable = null;

        this.buildUploadRow = this.buildDownloadRow = function () {
            return null;
        };

        this.addNode = function (parentNode, node, callBack) {
            if (node) {
                node.css('display', 'none').appendTo(parentNode).fadeIn(function () {
                    if (typeof callBack === func) {
                        try {
                            callBack();
                        } catch (e) {
                            // Fix endless exception loop:
                            $(this).stop();
                            throw e;
                        }
                    }
                });
            } else if (typeof callBack === func) {
                callBack();
            }
        };

        this.removeNode = function (node, callBack) {
            if (node) {
                node.fadeOut(function () {
                    $(this).remove();
                    if (typeof callBack === func) {
                        try {
                            callBack();
                        } catch (e) {
                            // Fix endless exception loop:
                            $(this).stop();
                            throw e;
                        }
                    }
                });
            } else if (typeof callBack === func) {
                callBack();
            }
        };

        this.onAbort = function (event, files, index, xhr, handler) {
            handler.removeNode(handler.uploadRow);
        };

        this.cancelUpload = function (event, files, index, xhr, handler) {
            var readyState = xhr.readyState;
            xhr.abort();
            // If readyState is below 2, abort() has no effect:
            if (isNaN(readyState) || readyState < 2) {
                handler.onAbort(event, files, index, xhr, handler);
            }
        };

        this.initProgressBar = function (node, value) {
            if (typeof node.progressbar === func) {
                return node.progressbar({
                    value: value
                });
            } else {
                var progressbar = $('<progress value="' + value + '" max="100"/>').appendTo(node);
                progressbar.progressbar = function (key, value) {
                    progressbar.attr('value', value);
                };
                return progressbar;
            }
        };

        this.initUploadRow = function (event, files, index, xhr, handler, callBack) {
            var uploadRow = handler.uploadRow = handler.buildUploadRow(files, index, handler);
            if (uploadRow) {
                handler.progressbar = handler.initProgressBar(
                    uploadRow.find(handler.progressSelector),
                    (xhr.upload ? 0 : 100)
                );
                uploadRow.find(handler.cancelSelector).click(function (e) {
                    handler.cancelUpload(e, files, index, xhr, handler);
                });
                uploadRow.find(handler.previewSelector).each(function () {
                    $(this).append(new LocalImage(files[index], handler.imageTypes));
                });
            }
            handler.addNode(
                (typeof handler.uploadTable === func ? handler.uploadTable(handler) : handler.uploadTable),
                uploadRow,
                callBack
            );
        };

        this.initUpload = function (event, files, index, xhr, handler, callBack) {
            handler.initUploadRow(event, files, index, xhr, handler, function () {
                if (typeof handler.beforeSend === func) {
                    handler.beforeSend(event, files, index, xhr, handler, callBack);
                } else {
                    callBack();
                }
            });
        };

        this.onProgress = function (event, files, index, xhr, handler) {
            if (handler.progressbar) {
                handler.progressbar.progressbar(
                    'value',
                    parseInt(event.loaded / event.total * 100, 10)
                );
            }
        };

        this.parseResponse = function (xhr) {
            if (typeof xhr.responseText !== undef) {
                return $.parseJSON(xhr.responseText);
            } else {
                // Instead of an XHR object, an iframe is used for legacy browsers:
                return $.parseJSON(xhr.contents().text());
            }
        };

        this.initDownloadRow = function (event, files, index, xhr, handler, callBack) {
            var json, downloadRow;
            try {
                json = handler.response = handler.parseResponse(xhr);
                downloadRow = handler.downloadRow = handler.buildDownloadRow(json, handler);
                handler.addNode(
                    (typeof handler.downloadTable === func ? handler.downloadTable(handler) : handler.downloadTable),
                    downloadRow,
                    callBack
                );
            } catch (e) {
                if (typeof handler.onError === func) {
                    handler.originalEvent = event;
                    handler.onError(e, files, index, xhr, handler);
                } else {
                    throw e;
                }
            }
        };

        this.onLoad = function (event, files, index, xhr, handler) {
            handler.removeNode(handler.uploadRow, function () {
                handler.initDownloadRow(event, files, index, xhr, handler, function () {
                    if (typeof handler.onComplete === func) {
                        handler.onComplete(event, files, index, xhr, handler);
                    }
                });
            });
        };

        this.dropZoneEnlarge = function () {
            if (!isDropZoneEnlarged) {
                if (typeof uploadHandler.dropZone.switchClass === func) {
                    uploadHandler.dropZone.switchClass(
                        uploadHandler.cssClassSmall,
                        uploadHandler.cssClassLarge
                    );
                } else {
                    uploadHandler.dropZone.addClass(uploadHandler.cssClassLarge);
                    uploadHandler.dropZone.removeClass(uploadHandler.cssClassSmall);
                }
                isDropZoneEnlarged = true;
            }
        };

        this.dropZoneReduce = function () {
            if (typeof uploadHandler.dropZone.switchClass === func) {
                uploadHandler.dropZone.switchClass(
                    uploadHandler.cssClassLarge,
                    uploadHandler.cssClassSmall
                );
            } else {
                uploadHandler.dropZone.addClass(uploadHandler.cssClassSmall);
                uploadHandler.dropZone.removeClass(uploadHandler.cssClassLarge);
            }
            isDropZoneEnlarged = false;
        };

        this.onDocumentDragEnter = function (event) {
            uploadHandler.dropZoneEnlarge();
        };

        this.onDocumentDragOver = function (event) {
            if (dragOverTimeout) {
                clearTimeout(dragOverTimeout);
            }
            dragOverTimeout = setTimeout(function () {
                uploadHandler.dropZoneReduce();
            }, 200);
        };

        this.onDragEnter = this.onDragLeave = function (event) {
            uploadHandler.dropZone.toggleClass(uploadHandler.cssClassHighlight);
        };

        this.onDrop = function (event) {
            if (dragOverTimeout) {
                clearTimeout(dragOverTimeout);
            }
            if (uploadHandler.dropEffect && typeof uploadHandler.dropZone.effect === func) {
                uploadHandler.dropZone.effect(uploadHandler.dropEffect, function () {
                    uploadHandler.dropZone.removeClass(uploadHandler.cssClassHighlight);
                    uploadHandler.dropZoneReduce();
                });
            } else {
                uploadHandler.dropZone.removeClass(uploadHandler.cssClassHighlight);
                uploadHandler.dropZoneReduce();
            }
        };

        $.extend(this, options);
    };

    methods = {
        init : function (options) {
            return this.each(function () {
                $(this).fileUpload(new UploadHandler($(this), options));
            });
        },

        option: function (option, value, namespace) {
            if (typeof option === undef || (typeof option === 'string' && typeof value === undef)) {
                return $(this).fileUpload('option', option, value, namespace);
            }
            return this.each(function () {
                $(this).fileUpload('option', option, value, namespace);
            });
        },

        destroy : function (namespace) {
            return this.each(function () {
                $(this).fileUpload('destroy', namespace);
            });
        }
    };

    $.fn.fileUploadUI = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.fileUploadUI');
        }
    };

}(jQuery));

/**
 * PrimeFaces FileUpload Widget
 */
PrimeFaces.widget.FileUpload = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);
    this.form = this.jq.parents('form:first');
    
    if(this.cfg.mode != 'simple') {
        this.inputId = this.jqId + '_input';
        this.filesTable = jQuery(this.jqId + '_files');
        this.fileBrowser = jQuery(this.jqId + '_browser');
        this.filesCount = 0;
        var _self = this;

        //request config
        var params = this.form.serializeArray();
        params.push({name: PrimeFaces.PARTIAL_REQUEST_PARAM,value: true});
        params.push({name: PrimeFaces.PARTIAL_PROCESS_PARAM,value: this.id});
        params.push({name: PrimeFaces.PARTIAL_SOURCE_PARAM,value: this.id});

        if(this.cfg.update) {
            params.push({name: PrimeFaces.PARTIAL_UPDATE_PARAM,value: this.cfg.update});
        }

        //upload all and cancel all buttons
        if(!this.cfg.customUI && !this.cfg.auto) {
            this.setupControls();
        }

        //file type restrictions
        this.setupRestrictions();

        //core config
        this.cfg.fieldName = this.id;
        this.cfg.fileInputFilter = this.inputId;
        this.cfg.uploadTable = this.filesTable;
        this.cfg.formData = params;

        this.cfg.buildUploadRow = function (files, index) {
            if(_self.cfg.fileLimit && index + 1 > _self.cfg.fileLimit) {
                return null;
            }
            
            return jQuery('<tr class="ui-widget-content"><td class="ui-fileupload-preview"><\/td>' +
                    '<td>' + files[index].name + '<\/td>' +
                    '<td class="ui-fileupload-progress"><div><\/div><\/td>' +
                    '<td class="ui-fileupload-start">' +
                    '<button class="ui-state-default ui-corner-all" title="' + _self.cfg.uploadLabel + '" type="button">' +
                    '<span class="ui-icon ui-icon-circle-triangle-n">' + _self.cfg.uploadLabel + '<\/span>' +
                    '<\/button><\/td>' +
                    '<td class="ui-fileupload-cancel">' +
                    '<button class="ui-state-default ui-corner-all" title="' + _self.cfg.cancelLabel + '" type="button">' +
                    '<span class="ui-icon ui-icon-circle-close">' + _self.cfg.cancelLabel + '<\/span>' +
                    '<\/button><\/td><\/tr>');
        };

        this.cfg.beforeSend = function(event, files, index, xhr, handler, callBack) {
            var valid = _self.checkFileRestrictions(event, files, index, handler);
            if(valid == false) {
                return false;
            }
            
            _self.filesCount++;
            var isIE = xhr.length != undefined; //check if xhr is a jQuery object with an iframe node

            if(!_self.cfg.auto) {
                if(_self.controls && !_self.controls.is(':visible')) {
                    _self.controls.fadeIn('fast');
                }
                
                handler.uploadRow.find('.ui-fileupload-start button').click(function(e) {
                    callBack();

                    if(isIE) {
                        _self.startIEProgress(handler);
                    }
                });
            }
            else {
                callBack();

                if(isIE) {
                    _self.startIEProgress(handler);
                }
            }
        };

        this.cfg.parseResponse = function(response) {
            if(response.responseXML) {
                PrimeFaces.ajax.AjaxResponse(response.responseXML);
            }
            else {
                var responseXML = _self.parseIFrameResponse(response);
              
                PrimeFaces.ajax.AjaxResponse(responseXML);
            }
        };

        this.cfg.onComplete = function(event, files, index, xhr, handler) {
            _self.filesCount--;
            
            if(_self.filesCount == 0 && _self.controls) {
                _self.controls.fadeOut('fast');
            }
        }

        //css
        this.cfg.cssClass = 'ui-fileupload-browser';
        this.cfg.cssClassSmall = 'ui-fileupload-browser-small';
        this.cfg.cssClassLarge = 'ui-fileupload-browser-large';
        this.cfg.cssClassHighlight = 'ui-fileupload-browser-highlight';
        this.cfg.previewSelector = '.ui-fileupload-preview';
        this.cfg.progressSelector = '.ui-fileupload-progress div';
        this.cfg.cancelSelector = '.ui-fileupload-cancel button';

        //create fileupload
        this.form.fileUploadUI(this.cfg);

        //visuals
        this.form.removeClass('ui-fileupload-browser');
        
        this.fileBrowser.show()
            .addClass('ui-fileupload-browser ui-widget ui-state-default ui-corner-all')
            .mouseover(function() {
                jQuery(this).addClass('ui-state-highlight');
            }).mouseout(function() {
                jQuery(this).removeClass('ui-state-highlight');
            });
    }    
}


PrimeFaces.widget.FileUpload.prototype.setupControls = function() {
    var _self = this;
    
    this.jq.find('.ui-fileupload-upload-button').button({icons: {primary: "ui-icon-circle-triangle-n"}}).click(function() {_self.upload();});
    this.jq.find('.ui-fileupload-cancel-button').button({icons: {primary: "ui-icon-circle-close"}}).click(function() {_self.cancel();});

    this.controls = this.jq.children('.ui-fileupload-controls');
}

PrimeFaces.widget.FileUpload.prototype.upload = function() {
    jQuery(this.jqId + ' .ui-fileupload-start button').click();

    if(this.controls) {
        this.controls.fadeOut('fast');
    }
}

PrimeFaces.widget.FileUpload.prototype.cancel = function() {
    jQuery(this.jqId + ' .ui-fileupload-cancel button').click();
    this.filesCount = 0;

    if(this.controls) {
        this.controls.fadeOut('fast');
    }
}

PrimeFaces.widget.FileUpload.prototype.checkFileRestrictions = function(event, files, index, handler) {
    var valid = true;

    //size limit
    if(this.cfg.sizeLimit && files[index].size > this.cfg.sizeLimit) {
        this.showError(handler, this.cfg.sizeExceedMessage);
        valid = false;
    }

    //file type
    if(this.cfg.allowTypes) {
        var regexp = new RegExp('\\.' + this.cfg.allowTypes + '$', 'i');
        if(!regexp.test(files[index].name)) {
            this.showError(handler, this.cfg.invalidFileMessage);
            valid = false;
        }
    }
    
    if(valid == false) {
        setTimeout(function () {handler.removeNode(handler.uploadRow);}, this.cfg.errorMessageDelay);
    }

    return valid;
}

PrimeFaces.widget.FileUpload.prototype.showError = function(handler, message) {
    handler.uploadRow.find('.ui-fileupload-progress').html('<div><span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-alert"></span>' + message + '</div>');
    handler.uploadRow.find('.ui-fileupload-start').hide();
    handler.uploadRow.find('.ui-fileupload-cancel').hide();
    handler.uploadRow.addClass('ui-state-error');
}

PrimeFaces.widget.FileUpload.prototype.setupRestrictions = function() {

    //file types
    if(this.cfg.allowTypes) {
        this.extensions = this.cfg.allowTypes.split(",");

        for(var i in this.extensions) {
            this.extensions[i] = '(' + this.extensions[i] + ")";
        }

        this.cfg.allowTypes = this.extensions.join('|');
    }

    //configuration
    this.cfg.sizeExceedMessage = this.cfg.sizeExceedMessage ? this.cfg.sizeExceedMessage : 'File is too large!';
    this.cfg.invalidFileMessage = this.cfg.invalidFileMessage ? this.cfg.invalidFileMessage : 'Invalid file type!';
    this.cfg.errorMessageDelay = this.cfg.errorMessageDelay ? this.cfg.errorMessageDelay : 5000;
}

/**
 * IFrame response response for legacy browsers e.g. IE.
 */
PrimeFaces.widget.FileUpload.prototype.parseIFrameResponse = function(iframe) {
    var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
    xmlDoc.async = "false";

    //format response so IE can parse
    var iframeContent = iframe.contents().text();
    iframeContent = jQuery.trim(iframeContent.replace(/(> -)|(>-)/g,'>'));

    xmlDoc.loadXML(iframeContent);

    var responseXML = {};
    responseXML.documentElement = xmlDoc.documentElement;

    return responseXML;
}

PrimeFaces.widget.FileUpload.prototype.startIEProgress = function(handler) {
    handler.uploadRow.find('.ui-progressbar-value').addClass('ui-progressbar-value-ie');
}