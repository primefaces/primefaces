/**
 * __PrimeFaces Simple FileUpload Widget__
 *
 * @prop {JQuery} button The DOM element for the button for selecting a file.
 * @prop {JQuery} display The DOM element for the UI display.
 * @prop {JQuery} form The DOM element of the (closest) form that contains this file upload.
 * @prop {JQuery} input The DOM element for the file input element.
 *
 * @interface {PrimeFaces.widget.SimpleFileUploadCfg} cfg The configuration for the
 * {@link  SimpleFileUpload| SimpleFileUpload widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.disabled Whether this file upload is disabled.
 * @prop {boolean} cfg.global Global AJAX requests are listened to by `ajaxStatus`. When `false`, `ajaxStatus` will not
 * get triggered.
 * @prop {string} cfg.messageTemplate Message template to use when displaying file validation errors.
 * @prop {boolean} cfg.skinSimple Whether to apply theming to the simple upload widget.
 * @forcedProp {number} [ajaxCount] Number of concurrent active Ajax requests.
 * @prop {boolean} cfg.displayFilename Wheter the filename should be displayed.
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

        this.cfg.messageTemplate = this.cfg.messageTemplate || '{name} {size}';
        this.cfg.global = (this.cfg.global === true || this.cfg.global === undefined) ? true : false;

        this.form = this.jq.closest('form');
        this.input = $(this.jqId);

        if (this.cfg.skinSimple) {
            this.input = $(this.jqId + '_input');
            this.button = this.jq.children('.ui-button');
            this.display = this.jq.children('.ui-fileupload-filename');

            if (!this.input.prop('disabled')) {
                this.bindEvents();
                this.bindTriggers();
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
                // display filename
                if (files.length > 0 && $this.cfg.displayFilename) {
                    var toDisplay = $this.cfg.messageTemplate.replace('{name}', files[0].name)
                        .replace('{size}', PrimeFaces.utils.formatBytes(files[0].size));

                    if (files.length > 1) {
                            toDisplay = toDisplay + " + " + (files.length - 1);
                    }
                    $this.display.text(toDisplay);
                }
                else {
                    $this.display.text('');
                }

                if ($this.cfg.auto && files.length > 0) {
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
     * Sets up the global event listeners on the button.
     * @private
     */
    bindTriggers: function() {
        PrimeFaces.bindButtonInlineAjaxStatus(this, this.button);
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
     * Clears the currently selected file.
     */
    clear: function() {
        if (this.input) {
            this.input.val('');
        }
        if (this.display) {
            this.display.text('');
        }
    },

    /**
     * Uploads all selected files via AJAX.
     * @private
     */
    upload: function() {
        var $this = this;
        var process = this.cfg.process
            ? this.id + ' ' + PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.process).join(' ')
            : this.id;
        var update = this.cfg.update
            ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.update).join(' ')
            : null;

        var validationResult = PrimeFaces.validation.validate($this.jq, process, update, true, true, true, false);
        if (!validationResult.valid) {
            return;
        }

        var files = this.input[0].files;
        var parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);
        var formData = PrimeFaces.ajax.Request.createFacesAjaxFormData(this.form, parameterPrefix, this.id, process, update);

        if($this.cfg.global) {
            $(document).trigger('pfAjaxStart');
        }

        // append files
        for (var i = 0; i < files.length; i++) {
            formData.append(this.input.attr('id'), files[i]);
        }

        var xhrOptions = {
            url: PrimeFaces.ajax.Utils.getPostUrl(this.form),
            portletForms: PrimeFaces.ajax.Utils.getPorletForms(this.form, parameterPrefix),
            source: this.id,
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
                if($this.cfg.global) {
                     $(document).trigger('pfAjaxSend', [xhr, this]);
                }
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

                $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);

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

                    if($this.cfg.global) {
                        $(document).trigger('pfAjaxSuccess', [xhr, this]);
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
                $this.clear();

                if($this.cfg.global) {
                    $(document).trigger('pfAjaxComplete', [xhr, this, xhr.pfArgs]);
                }
            });

        PrimeFaces.ajax.Queue.addXHR(jqXhr);

    },

});
