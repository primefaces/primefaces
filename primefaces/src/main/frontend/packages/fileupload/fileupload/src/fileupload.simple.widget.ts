/**
 * The configuration for the {@link SimpleFileUpload} widget.
 * 
 * You can access this configuration via
 * {@link SimpleFileUpload.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface SimpleFileUploadCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Whether auto upload mode is enabled. If enabled, files are uploaded
     * immediately to the server, directly after the user chose a file. If
     * disabled, files are uploaded only once the user click on the upload
     * button. 
     */
    auto: boolean;

    /**
     * Whether this file upload is disabled.
     */
    disabled: boolean;

    /**
     * Whether the filename should be displayed.
     */
    displayFilename: boolean;

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
     * Message template to use when displaying file validation errors.
     */
    messageTemplate: string;

    /**
     * Client-side callback to invoke when the AJAX request to upload files
     * completes (either successfully or with an error).
     */
    oncomplete: PrimeType.ajax.CallbackOncomplete;

    /**
     * Client-side callback to invoke when the AJAX request to upload files
     * fails.
     */
    onerror: PrimeType.ajax.CallbackOnerror;

    /**
     * Client-side callback to invoke when the AJAX request to upload files
     * is about to begin.
     */
    onstart: PrimeType.widget.SimpleFileUpload.OnStartCallback;

    /**
     * Client-side callback to invoke when the AJAX request to upload files
     * succeeds.
     */
    onsuccess: PrimeType.ajax.CallbackOnsuccess;

    /**
     * List of components to process (include in the request) when files
     * are uploaded.
     */
    process: string;

    /**
     * Whether to apply theming to the simple upload widget.
     */
    skinSimple: boolean;

    /**
     * List of components to update (re-render) when files are uploaded.
     */
    update: string;
}

/**
 * __PrimeFaces Simple FileUpload Widget__
 *
 * Allows uploading files to the server, either via simple mode (form submit)
 * or via AJAX.
 */
export class SimpleFileUpload<Cfg extends SimpleFileUploadCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The DOM element for the button for selecting a file.
     */
    button: JQuery | undefined = undefined;

    /**
     * The DOM element for the UI display.
     */
    display: JQuery | undefined = undefined;

    /**
     * Drop zone to use for drag and drop.
     */
    dropZone: JQuery = $();

    /**
     * The DOM element of the (closest) form that contains this file upload.
     */
    form: JQuery= $();

    /**
     * The DOM element for the file input element.
     */
    input: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        
        if (this.cfg.disabled) {
            return;
        }

        this.cfg.messageTemplate = this.cfg.messageTemplate || '{name} {size}';
        this.cfg.global = this.cfg.global !== false;

        this.form = this.jq.closest('form');
        this.input = $(this.jqId);

        if (this.cfg.dropZone) {
            this.dropZone = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.dropZone);
        }

        if (this.cfg.skinSimple) {
            this.input = $(this.jqId + '_input');
            this.button = this.jq.children('.ui-button');
            this.display = this.jq.children('.ui-fileupload-filename');

            if (!this.input.prop('disabled')) {
                this.bindEvents();
                this.bindTriggers();
                this.bindDropZone();
            }
        }
        else if (this.cfg.auto) {
            this.input.on('change.fileupload', () => this.upload());
            this.bindDropZone();
        }
    }

    /**
     * Sets up all events listeners for this file upload widget.
     */
    private bindEvents(): void {
        this.button?.on('mouseover.fileupload', function(){
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

        this.input.on('change.fileupload', () => {
            const input = this.input[0];
            const files = input instanceof HTMLInputElement ? input.files : undefined;
            if (files) {
                // display filename
                if (files.length > 0 && this.cfg.displayFilename) {
                    let toDisplay = (this.cfg.messageTemplate ?? "")
                        .replace('{name}', files[0]?.name ?? "")
                        .replace('{size}', PrimeFaces.utils.formatBytes(files[0]?.size ?? 0));

                    if (files.length > 1) {
                        toDisplay = toDisplay + " + " + (files.length - 1);
                    }
                    this.display?.text(toDisplay);
                }
                else {
                    this.display?.text('');
                }

                if (this.cfg.auto && files.length > 0) {
                    this.upload();
                }
            } else {
            	// no data was found so clear the input
            	this.input.val('');
            }
        })
        .on('focus.fileupload', () => {
            this.button?.addClass('ui-state-focus');
        })
        .on('blur.fileupload', () => {
            this.button?.removeClass('ui-state-focus');
        });
    }

    /**
     * Sets up the event handling for the dropZone.
     */
    private bindDropZone(): void {
        if (this.dropZone) {
            this.dropZone.on("dragenter dragover dragleave drop", (event) => {
                event.preventDefault();
                event.stopPropagation();
            });

            this.dropZone.on("dragenter dragover", () => {
                this.dropZone.addClass('ui-state-drag');
            });
            this.dropZone.on("dragleave drop", () => {
                this.dropZone.removeClass('ui-state-drag');
            });

            this.dropZone.on("drop", (event) => {
                const dataTransfer = event.originalEvent?.dataTransfer;
                if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
                    const input = this.input[0];
                    if (input instanceof HTMLInputElement) {
                        input.files = dataTransfer.files;
                    }
                    this.input.trigger('change.fileupload');
                }
            });
        }
    }

    /**
     * Sets up the global event listeners on the button.
     */
    private bindTriggers(): void {
        PrimeFaces.bindButtonInlineAjaxStatus(this, this.button ?? $());
    }

    /**
     * Brings up the native file selection dialog.
     */
    show(): void {
        if (this.cfg.skinSimple) {
            this.input.trigger("click");
        }
        else {
            this.jq.trigger("click");
        }
    }

    /**
     * Clears the currently selected file.
     */
    clear(): void {
        if (this.input) {
            this.input.val('');
        }
        if (this.display) {
            this.display.text('');
        }
    }

    /**
     * Uploads all selected files via AJAX.
     */
    private upload(): void {
        const $this = this;
        const process = this.cfg.process
            ? this.getId() + ' ' + PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.process).join(' ')
            : this.getId();
            const update = this.cfg.update
            ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.jq, this.cfg.update).join(' ')
            : null;

            const validationResult = PrimeFaces.validation.validate($this.jq, process, update, true, true, true, false, true);
        if (!validationResult.valid) {
            return;
        }
        const ignoreAutoUpdate = this.cfg.ignoreAutoUpdate;
        const files = this.input[0] instanceof HTMLInputElement ? this.input[0].files ?? [] : [];
        const parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(this.form);
        const formData = PrimeFaces.ajax.Request.createFacesAjaxFormData(this.form, parameterPrefix, this.getId(), process, update, ignoreAutoUpdate);

        if($this.cfg.onstart) {
            $this.cfg.onstart.call($this);
        }
        if($this.cfg.global) {
            $(document).trigger('pfAjaxStart');
        }

        // append files
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            if (file) {
                formData.append(this.input.attr('id') ?? "", file);
            }
        }

        const xhrOptions: PrimeType.ajax.PrimeFacesSettings = {
            url: PrimeFaces.ajax.Utils.getPostUrl(this.form),
            portletForms: PrimeFaces.ajax.Utils.getPorletForms(this.form, parameterPrefix ?? ""),
            source: this.getId(),
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

        const jqXhr = $.ajax(xhrOptions)
            .fail(function(this: PrimeType.ajax.PrimeFacesSettings, xhr, status, errorThrown) {
                var location = xhr.getResponseHeader("Location");
                if (xhr.status === 401 && location) {
                    PrimeFaces.debug('Unauthorized status received. Redirecting to ' + location);
                    // There are subtle differences between
                    // window.location = "..." and window.location.href = "..."
                    // https://github.com/microsoft/TypeScript/issues/48949
                    (window as Window).location = location;
                    return;
                }
                if($this.cfg.onerror) {
                    $this.cfg.onerror.call(this, xhr, status, errorThrown);
                }

                $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);

                PrimeFaces.error('Request return with error:' + status + '.');
            })
            .done(function(this: PrimeType.ajax.PrimeFacesSettings, data, status, xhr: PrimeType.ajax.pfXHR) {
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
            .always(function(this: PrimeType.ajax.PrimeFacesSettings, data, status, xhr: string | PrimeType.ajax.pfXHR) {
                const pfArgs = typeof xhr === "string" ? undefined : xhr.pfArgs;

                if($this.cfg.oncomplete) {
                    $this.cfg.oncomplete.call(this, xhr, status, pfArgs, data);
                }

                PrimeFaces.debug('Response completed.');
                $this.clear();

                if($this.cfg.global) {
                    $(document).trigger('pfAjaxComplete', [xhr, this, pfArgs]);
                }
            });

        PrimeFaces.ajax.Queue.addXHR(jqXhr);
    }
}
