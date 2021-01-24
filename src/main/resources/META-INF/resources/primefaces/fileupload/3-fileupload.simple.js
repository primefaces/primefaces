/**
 * __PrimeFaces Simple FileUpload Widget__
 *
 * @prop {JQuery} button The DOM element for the button for selecting a file.
 * @prop {JQuery} display The DOM element for the UI display.
 * @prop {JQuery} form The DOM element of the (closest) form that contains this file upload.
 * @prop {JQuery} input The DOM element for the file input element.
 * @prop {number} maxFileSize Maximum allowed size in bytes for files.
 * @prop {string[]} sizes Array with suffixes for file sizes (`Bytes`, `KB` etc.).
 *
 * @interface {PrimeFaces.widget.SimpleFileUploadCfg} cfg The configuration for the
 * {@link  SimpleFileUpload| SimpleFileUpload widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.skinSimple Whether to apply theming to the simple upload widget.
 * @prop {boolean} cfg.disabled Whether this file upload is disabled.
 * @prop {number} cfg.fileLimit Maximum number of files allowed to upload.
 * @prop {string} cfg.fileLimitMessage Message to display when file limit exceeds.
 * @prop {string} cfg.invalidFileMessage Message to display when file is not accepted.
 * @prop {string} cfg.invalidSizeMessage Message to display when size limit exceeds.
 * @prop {number} cfg.maxFileSize Maximum allowed size in bytes for files.
 * @prop {string} cfg.messageTemplate Message template to use when displaying file validation errors.
 */
PrimeFaces.widget.SimpleFileUpload = PrimeFaces.widget.BaseWidget.extend({

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

        this.cfg.invalidFileMessage = this.cfg.invalidFileMessage || 'Invalid file type';
        this.cfg.invalidSizeMessage = this.cfg.invalidSizeMessage || 'Invalid file size';
        this.cfg.fileLimitMessage = this.cfg.fileLimitMessage || 'Maximum number of files exceeded';
        this.cfg.messageTemplate = this.cfg.messageTemplate || '{name} {size}';
        this.sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

        this.maxFileSize = this.cfg.maxFileSize;

        this.form = this.jq.closest('form');
        this.input = $(this.jqId + '_input');

        if (this.cfg.skinSimple) {
            this.button = this.jq.children('.ui-button');
            this.display = this.jq.children('.ui-fileupload-filename');

            if (!this.input.prop('disabled')) {
                this.bindEvents();
            }
        }
        else if (this.cfg.auto) {
            var $this = this;
            this.input.on('change.fileupload', function() {
                $this.upload();
            });
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
            if (!el.prop('disabled')) {
                el.addClass('ui-state-hover');
            }
        })
        .on('mouseout.fileupload', function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        })
        .on('mousedown.fileupload', function() {
            var el = $(this);
            if (!el.prop('disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        })
        .on('mouseup.fileupload', function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        });

        this.input.on('change.fileupload', function() {
            var files = $this.input[0].files;
            if (files) {
            	var validationFailureMessage;
            	var validationFileName;
            	var validationFileSize;
            	if (files.length > $this.cfg.fileLimit) {
            		validationFailureMessage = $this.cfg.fileLimitMessage;
            		validationFileName = null;
            		validationFileSize = null;
            	}
            	// checking each file until find a violation
            	var i = 0;
            	for(; !validationFailureMessage && i < files.length; ++i) {
                    var file = files[i];
                    var validMsg = $this.validate(file);
                    if (validMsg) {
                    	validationFailureMessage = validMsg;
                    	validationFileName = file.name;
                		validationFileSize = file.size;
                    }
            	}

                if(validationFailureMessage) {
                    //a violation was found. Display the respective message, clear the input and
                    // call the validation failure handler if exists
                    var details = '';
                    if (validationFileName && validationFileSize) {
                            details += ': ' + $this.cfg.messageTemplate.replace('{name}', validationFileName).replace('{size}', $this.formatSize(validationFileSize));
                    }
                    $this.display.text(validationFailureMessage + details);
                    $this.input.val('');

                    if ($this.cfg.onvalidationfailure) {
                    	$this.cfg.onvalidationfailure({
                            summary: validationFailureMessage,
                            filename: validationFileName,
                            filesize: validationFileSize
                        });
                    }
                } else {
                    // If everything is ok, format the message and display it
                    var toDisplay = $this.cfg.messageTemplate.replace('{name}', files[0].name).replace('{size}', $this.formatSize(files[0].size));

                    if (files.length > 1) {
                            toDisplay = toDisplay + " + " + (files.length - 1);
                    }
                    $this.display.text(toDisplay);
                }

                if ($this.cfg.auto) {
                    $this.upload();
                }
            } else {
            	// no data was found so clear the input
            	$this.input.val('');
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
     * Formats the given file size in a more human-friendly format, e.g. `1.5 MB` etc.
     * @param {number} bytes File size in bytes to format
     * @return {string} The given file size, formatted in a more human-friendly format.
     */
    formatSize: function(bytes) {
        if (bytes === undefined)
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
     * Brings up the native file selection dialog.
     */
    show: function() {
        if (this.cfg.skinSimple) {
            this.input.trigger("click");
        }
        else {
            this.jq.trigger("click");
        }
    },

    /**
     * Uploads all selected files via AJAX.
     * @private
     */
    upload: function() {

        var $this = this,
            files = this.input[0].files;

        var parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);
        var process = this.cfg.process ? this.id + ' ' + PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.cfg.process).join(' ') : this.id;
        var update = this.cfg.update ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.cfg.update).join(' ') : null;

        var formData = PrimeFaces.ajax.Request.createFacesAjaxFormData(this.form, parameterPrefix, this.id, process, update);

        // append files
        for (var i = 0; i < files.length; i++) {
            formData.append(this.input.attr('id'), files[i]);
        }

        var xhrOptions = {
            url: PrimeFaces.ajax.Utils.getPostUrl(this.form),
            portletForms: PrimeFaces.ajax.Utils.getPorletForms(this.form, parameterPrefix),
            type : "POST",
            cache : false,
            dataType : "xml",
            data: formData,
            processData: false,
            contentType: false,
            global: false,
            beforeSend: function(xhr, settings) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                xhr.pfSettings = settings;
                xhr.pfArgs = {}; // default should be an empty object
            }
        };

        var jqXhr = $.ajax(xhrOptions)
            .fail(function(xhr, status, errorThrown) {
                var location = xhr.getResponseHeader("Location");
                if (xhr.status === 401 && location) {
                    PrimeFaces.debug('Unauthorized status received. Redirecting to ' + location);
                    window.location = location;
                    return;
                }
                if($this.cfg.onerror) {
                    $this.cfg.onerror.call(this, xhr, status, errorThrown);
                }

                PrimeFaces.error('Request return with error:' + status + '.');
            })
            .done(function(data, status, xhr) {
                PrimeFaces.debug('Response received successfully.');
                try {
                    var parsed;

                    //call user callback
                    if($this.cfg.onsuccess) {
                        parsed = $this.cfg.onsuccess.call(this, data, status, xhr);
                    }

                    //do not execute default handler as response already has been parsed
                    if(parsed) {
                        return;
                    }
                    else {
                        PrimeFaces.ajax.Response.handle(data, status, xhr);
                    }
                }
                catch(err) {
                    PrimeFaces.error(err);
                }

                PrimeFaces.debug('DOM is updated.');
            })
            .always(function(data, status, xhr) {
                if($this.cfg.oncomplete) {
                    $this.cfg.oncomplete.call(this, xhr, status, xhr.pfArgs, data);
                }

                PrimeFaces.debug('Response completed.');

                if ($this.display) {
                    $this.display.text('');
                }
                $this.input.val('');
            });

        PrimeFaces.ajax.Queue.addXHR(jqXhr);

    }

});
