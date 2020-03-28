if (!PrimeFaces.dialog) {

    /**
     * The object with functionality related to working with dialogs and the dialog framework.
     * @namespace
     * 
     * @interface {PrimeFaces.dialog.DialogHandlerCfg} DialogHandlerCfg Interface of the configuration object for a
     * dialog of the dialog framework. Used by `PrimeFaces.dialog.DialogHandler.openDialog`.
     * @prop {Partial<PrimeFaces.dialog.DialogHandlerCfgOptions>} DialogHandlerCfg.options The options for the dialog.
     * @prop {string} DialogHandlerCfg.pfdlgcid PrimeFaces dialog client ID.
     * @prop {string} DialogHandlerCfg.sourceComponentId ID of the dialog.
     * @prop {string} DialogHandlerCfg.sourceWidgetVar Widget variable of the dialog.
     * @prop {string} DialogHandlerCfg.url Source URL for the IFRAME element with the dialog.
     *
     * @interface {PrimeFaces.dialog.DialogHandlerCfgOptions} DialogHandlerCfgOptions Interface of the dialog
     * configuration object for a dialog of the dialog framework. Used by `PrimeFaces.dialog.DialogHandlerCfg`. This is
     * mainly just the `PrimeFaces.widget.DialogCfg`, but adds a few more properties.
     * @extends {PrimeFaces.widget.DialogCfg} DialogHandlerCfgOptions
     * @prop {number} DialogHandlerCfgOptions.contentHeight Height of the IFRAME in pixels.
     * @prop {number} DialogHandlerCfgOptions.contentWidth Width of the IFRAME in pixels.
     * @prop {string} DialogHandlerCfgOptions.headerElement ID of the header element of the dialog.
     */
    PrimeFaces.dialog = {};

    /**
     * The interface of the object with all methods for working with dialogs and the dialog framework.
     * @interface
     * @constant {PrimeFaces.dialog.DialogHandler} . The object with all methods for dialogs and the dialog framework.
     */
    PrimeFaces.dialog.DialogHandler = {

        /**
         * Opens the dialog as specified by the given configuration. When the dialog is dynamic, loads the content from
         * the server.
         * @param {PrimeFaces.dialog.DialogHandlerCfg} cfg Configuration of the dialog.
         */
        openDialog: function(cfg) {
            var rootWindow = this.findRootWindow(),
            dialogId = cfg.sourceComponentId + '_dlg';

            if(rootWindow.document.getElementById(dialogId)) {
                return;
            }

            var dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget',
            styleClass = cfg.options.styleClass||'',
            dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container ui-overlay-hidden ' + styleClass + '"' +
                    ' data-pfdlgcid="' + PrimeFaces.escapeHTML(cfg.pfdlgcid) + '" data-widget="' + dialogWidgetVar + '"></div>')
                    .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span id="' + dialogId + '_title" class="ui-dialog-title"></span></div>');

            var titlebar = dialogDOM.children('.ui-dialog-titlebar');
            if(cfg.options.closable !== false) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a>');
            }

            if(cfg.options.minimizable) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-minimize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-minus"></span></a>');
            }

            if(cfg.options.maximizable) {
                titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-maximize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-extlink"></span></a>');
            }

            dialogDOM.append('<div class="ui-dialog-content ui-widget-content ui-df-content" style="height: auto;">' +
                    '<iframe style="border:0 none" frameborder="0"/>' +
                    '</div>');

            dialogDOM.appendTo(rootWindow.document.body);

            var dialogFrame = dialogDOM.find('iframe'),
            symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
            frameURL = cfg.url.indexOf('pfdlgcid') === -1 ? cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid: cfg.url,
            frameWidth = cfg.options.contentWidth||640;

            dialogFrame.width(frameWidth);

            if(cfg.options.iframeTitle) {
               dialogFrame.attr('title', cfg.options.iframeTitle);
            }

            dialogFrame.on('load', function() {
                var $frame = $(this),
                headerElement = $frame.contents().find('title'),
                isCustomHeader = false;

                if(cfg.options.headerElement) {
                    var customHeaderId = PrimeFaces.escapeClientId(cfg.options.headerElement),
                    customHeaderElement = dialogFrame.contents().find(customHeaderId);

                    if(customHeaderElement.length) {
                        headerElement = customHeaderElement;
                        isCustomHeader = true;
                    }
                }

                if(!$frame.data('initialized')) {
                    PrimeFaces.cw.call(rootWindow.PrimeFaces, 'DynamicDialog', dialogWidgetVar, {
                        id: dialogId,
                        position: cfg.options.position||'center',
                        sourceComponentId: cfg.sourceComponentId,
                        sourceWidgetVar: cfg.sourceWidgetVar,
                        onHide: function() {
                            var $dialogWidget = this,
                            dialogFrame = this.content.children('iframe');

                            if(dialogFrame.get(0).contentWindow.PrimeFaces) {
                                this.destroyIntervalId = setInterval(function() {
                                    if(dialogFrame.get(0).contentWindow.PrimeFaces.ajax.Queue.isEmpty()) {
                                        clearInterval($dialogWidget.destroyIntervalId);
                                        dialogFrame.attr('src','about:blank');
                                        $dialogWidget.jq.remove();
                                    }
                                }, 10);
                            }
                            else {
                                dialogFrame.attr('src','about:blank');
                                $dialogWidget.jq.remove();
                            }

                            rootWindow.PF[dialogWidgetVar] = undefined;
                        },
                        modal: cfg.options.modal,
                        blockScroll: cfg.options.blockScroll,
                        resizable: cfg.options.resizable,
                        hasIframe: true,
                        draggable: cfg.options.draggable,
                        width: cfg.options.width,
                        height: cfg.options.height,
                        minimizable: cfg.options.minimizable,
                        maximizable: cfg.options.maximizable,
                        headerElement: cfg.options.headerElement,
                        responsive: cfg.options.responsive,
                        closeOnEscape: cfg.options.closeOnEscape
                    });
                }

                var title = rootWindow.PF(dialogWidgetVar).titlebar.children('span.ui-dialog-title');
                if(headerElement.length > 0) {
                    if(isCustomHeader) {
                        title.append(headerElement);
                        headerElement.show();
                    }
                    else {
                        title.text(headerElement.text());
                    }

                    dialogFrame.attr('title', title.text());
                }

                //adjust height
                var frameHeight = null;
                if(cfg.options.contentHeight)
                    frameHeight = cfg.options.contentHeight;
                else
                    frameHeight = $frame.get(0).contentWindow.document.body.scrollHeight + (PrimeFaces.env.browser.webkit ? 5 : 25);

                $frame.css('height', frameHeight);

                // fix #1290 - dialogs are not centered vertically
                dialogFrame.data('initialized', true);
                rootWindow.PF(dialogWidgetVar).show();
            })
            .attr('src', frameURL);
        },

        /**
         * Closes the dialog as specified by the given configuration.
         * @param {PrimeFaces.dialog.DialogHandlerCfg} cfg Configuration of the dialog.
         */
        closeDialog: function(cfg) {
            var rootWindow = this.findRootWindow(),
            dlgs = $(rootWindow.document.body).children('div.ui-dialog[data-pfdlgcid="' + cfg.pfdlgcid +'"]').not('[data-queuedforremoval]'),
            dlgsLength = dlgs.length,
            dlg = dlgs.eq(dlgsLength - 1),
            parentDlg = dlgsLength > 1 ? dlgs.eq(dlgsLength - 2) : null,
            dlgWidget = rootWindow.PF(dlg.data('widget')),
            sourceWidgetVar = dlgWidget.cfg.sourceWidgetVar,
            sourceComponentId = dlgWidget.cfg.sourceComponentId,
            dialogReturnBehavior = null,
            windowContext = null;

            dlg.attr('data-queuedforremoval', true);

            if(parentDlg) {
                var parentDlgFrame = parentDlg.find('> .ui-dialog-content > iframe').get(0),
                windowContext = parentDlgFrame.contentWindow||parentDlgFrame;
                sourceWidget = windowContext.PF(sourceWidgetVar);
            }
            else {
                windowContext = rootWindow;
            }

            if(sourceWidgetVar) {
                var sourceWidget = windowContext.PF(sourceWidgetVar);
                dialogReturnBehavior = sourceWidget.cfg.behaviors ? sourceWidget.cfg.behaviors['dialogReturn']: null;
            }
            else if(sourceComponentId) {
                var dialogReturnBehaviorStr = $(windowContext.document.getElementById(sourceComponentId)).data('dialogreturn');
                if(dialogReturnBehaviorStr) {
                    dialogReturnBehavior = windowContext.eval('(function(ext){this.' + dialogReturnBehaviorStr + '})');
                }
            }

            if(dialogReturnBehavior) {
                var ext = {
                        params: [
                            {name: sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid}
                        ]
                    };

                dialogReturnBehavior.call(windowContext, ext);
            }

            dlgWidget.hide();
        },

        /**
         * Displays a message in the messages dialog.
         * @param {string} msg Message to show.
         */
        showMessageInDialog: function(msg) {
            if(!this.messageDialog) {
                var messageDialogDOM = $('<div id="primefacesmessagedlg" class="ui-message-dialog ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container"/>')
                            .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span>' +
                            '<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div>' +
                            '<div class="ui-dialog-content ui-widget-content" style="height: auto;"></div>')
                            .appendTo(document.body);

                PrimeFaces.cw('Dialog', 'primefacesmessagedialog', {
                    id: 'primefacesmessagedlg',
                    modal:true,
                    draggable: false,
                    resizable: false,
                    showEffect: 'fade',
                    hideEffect: 'fade'
                });
                this.messageDialog = PF('primefacesmessagedialog');
                this.messageDialog.titleContainer = this.messageDialog.titlebar.children('span.ui-dialog-title');
            }

            var escape = msg.escape !== false;
            var summaryHtml = msg.summary ? msg.summary.split(/\r\n|\n|\r/g).map(function(line) { return escape ? PrimeFaces.escapeHTML(line) : line; }).join("<br>") : "";
            this.messageDialog.titleContainer.html(summaryHtml);

            var detailHtml = msg.detail ? msg.detail.split(/\r\n|\n|\r/g).map(function(line) { return escape ? PrimeFaces.escapeHTML(line) : line; }).join("<br>") : "";
            this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + msg.severity.split(' ')[0].toLowerCase() + '-icon" />').append(detailHtml);
            this.messageDialog.show();
        },

        /**
         * Asks the user to confirm an action. Shows a confirmation dialog with the given message. Requires a
         * `<p:confirmDialog>` to be available on the current page.
         * @param {string} msg Message to show in the confirmation dialog.
         */
        confirm: function(msg) {
            if(PrimeFaces.confirmDialog) {
                PrimeFaces.confirmSource = (typeof(msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
                PrimeFaces.confirmDialog.showMessage(msg);
            }
            else {
                PrimeFaces.warn('No global confirmation dialog available.');
            }
        },

        /**
         * Returns the current window instance. When inside an iframe, returns the window instance of the topmost
         * document.
         * @return {Window} The root window instance.
         */
        findRootWindow: function() {
            var w = window;
            while(w.frameElement) {
                var parent = w.parent;
                if (parent.PF === undefined) {
                	break;
                }
                w = parent;
            };

            return w;
        }
    };
}
