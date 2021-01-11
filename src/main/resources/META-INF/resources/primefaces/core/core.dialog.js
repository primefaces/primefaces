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

            // The widget that opens a dialog can be nested inside of a frame which might be nested again.
            // The dialog is put in the outermost frame to be able to fill the whole browser tab,
            // so we traverse upwards to find the root window and put the dialog DOM in there.
            // When a dialog is closed, we need to clean up the global variables and notify the source widget for the dialog return feature.
            // Accessing a component nested within frames requires recursive resolving of frames.
            // Every frame has it's own contentWindow and thus also it's own document object.
            // To be able to access a DOM element from an outer frame, one needs to first resolve the containing frame,
            // and then resolve the element from the contentWindow. With nested frames, nested frame resolving has to be done.
            // In order to do this, we traverse up the window frameElement until we reach the top window.
            // While traversing up, we construct a selector for finding the frameElement from within the parent window.
            // We build up the selectors backwards as we traverse up. Imagine the example
            //
            // --------------------------------------------------
            // | Frame 1                                        |
            // |           -------------------------------      |
            // |           | Frame 1_1                   |      |
            // |           |                             |      |
            // |           |  ------------               |      |
            // |           |  | Button 1 |               |      |
            // |           |  ------------               |      |
            // |           |                             |      |
            // |           -------------------------------      |
            // |------------------------------------------------|
            // | Frame 2                                        |
            // |                                                |
            // |                                                |
            // |                                                |
            // --------------------------------------------------
            //
            // Here "Button 1" is our source widget that opened the dialog.
            // The root window contains two frames "Frame 1" and "Frame 2".
            // The "Frame 1" contains another frame "Frame 1_1" within which the widget lives.
            // Since we have to install the dialog in the root window, we need to be able to get access
            // to the source widget when closing the dialog.
            // The only way to find the DOM node, is by traversing into "Frame 1" then into "Frame 1_1" and look it up there.
            // So from the root window we do e.g. `$(rootWindow.document).find("#frame1").contentWindow` to get into "Frame 1".
            // We do the same to get into "Frame 1_1" e.g. `$(frame1Window.document).find("#frame1_1").contentWindow`.
            // Finally, we can look up the source widget `$(frame1_1Window.document).find("#sourceWidgetId")`.

            var sourceFrames = function() {
                var w = window;
                var sourceFrames = [];
                // Traverse up frameElement i.e. while we are in frames
                while(w.frameElement) {
                    var parent = w.parent;
                    if (parent.PF === undefined) {
                        break;
                    }

                    // Since we traverse DOM elements upwards, we build the selector backwards i.e. from target to source.
                    // This is why we use `unshift` which is like an `addAtIndex(0, object)`.
                    // If an element has an id, we can use that to uniquely identify the DOM element and can jump to the next parent window.
                    // If we can't find an id, we collect class names and the tag name of an element.
                    // If that doesn't uniquely identify an element within it's parent, we also append the node index via the `:eq(index)` selector.
                    // We connect selectors for each DOM element with the `>` operator.
                    var e = w.frameElement;
                    var pieces = [];

                    // Traverse up tags from the frameElement to generate an identifying selector
                    for (; e && e.tagName !== undefined; e = e.parentNode) {
                        if (e.id && !/\s/.test(e.id)) {
                            // If we find a parent with an id, we can use that as basis and stop there
                            pieces.unshift(e.id);
                            pieces.unshift('#');
                            pieces.unshift(' > ');
                            break;
                        } else if (e.className) {
                            // Without an id, we try to use a combination of :eq, class names and tag name and hope a parent has an id
                            var classes = e.className.split(' ');
                            var classSelectorPieces = [];
                            for (var i in classes) {
                                if (classes.hasOwnProperty(i) && classes[i]) {
                                    classSelectorPieces.unshift(classes[i]);
                                    classSelectorPieces.unshift('.');
                                }
                            }
                            classSelectorPieces.unshift(e.tagName);

                            var classSelector = classSelectorPieces.join('');
                            var elems = $(e.parentNode).find(classSelector);
                            if (elems.length > 1) {
                                pieces.unshift(":eq(" + elems.index(e) + ")");
                            }
                            pieces.unshift(classSelector);
                        } else {
                            // Without classes, we try to work with :eq and the tag name
                            var elems = $(e.parentNode).find(e.tagName);
                            if (elems.length > 1) {
                                pieces.unshift(":eq(" + elems.index(e) + ")");
                            }
                            pieces.unshift(e.tagName);
                        }
                        pieces.unshift(' > ');
                    }

                    var s = pieces.slice(1).join('');

                    sourceFrames.unshift(s);
                    w = parent;
                };

                return sourceFrames;
            }();

            var dialogWidgetVar = cfg.options.widgetVar;
            if (!dialogWidgetVar) {
                dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget';
            }

            var styleClass = cfg.options.styleClass||'',
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
                    '<iframe style="border:0 none" frameborder="0"></iframe>' +
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
                        sourceFrames: sourceFrames,
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

                            rootWindow.PrimeFaces.widgets[dialogWidgetVar] = undefined;
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
                        closeOnEscape: cfg.options.closeOnEscape,
                        focus: cfg.options.focus
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

                $frame.css('height', String(frameHeight));

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
            dlgs = $(rootWindow.document.body).children('div.ui-dialog[data-pfdlgcid="' + $.escapeSelector(cfg.pfdlgcid) +'"]').not('[data-queuedforremoval]'),
            dlgsLength = dlgs.length,
            dlg = dlgs.eq(dlgsLength - 1),
            parentDlg = dlgsLength > 1 ? dlgs.eq(dlgsLength - 2) : null,
            dialogReturnBehavior = null,
            windowContext = null;

            var dlgWidget = rootWindow.PF(dlg.data('widget'));
            if(!dlgWidget) {
                // GitHub #2039 dialog may already be closed on slow internet
                PrimeFaces.error('Dialog widget was not found to close.');
                return;
            }

            var sourceWidgetVar = dlgWidget.cfg.sourceWidgetVar,
                sourceComponentId = dlgWidget.cfg.sourceComponentId;

            dlg.attr('data-queuedforremoval', true);

            if(parentDlg) {
                var parentDlgFrame = parentDlg.find('> .ui-dialog-content > iframe').get(0),
                windowContext = parentDlgFrame.contentWindow||parentDlgFrame;
                sourceWidget = windowContext.PF(sourceWidgetVar);
            }
            else {
                // We have to resolve the frames from the root window to the source widget to invoke the dialog return behavior
                // Each source frame element is a selector. We step into every nested frame until we are in the source widget frame.
                windowContext = rootWindow;
                var frames = dlgWidget.cfg.sourceFrames;
                for (var i = 0; i < frames.length; i++) {
                    windowContext = $(windowContext.document).find(frames[i]).get(0).contentWindow;
                }
            }

            if(sourceWidgetVar) {
                var sourceWidget = windowContext.PF(sourceWidgetVar);
                dialogReturnBehavior = sourceWidget.cfg.behaviors ? sourceWidget.cfg.behaviors['dialogReturn']: null;
            }
            else if(sourceComponentId) {
                var dialogReturnBehaviorStr = $(windowContext.document.getElementById(sourceComponentId)).data('dialogreturn');
                if(dialogReturnBehaviorStr) {
                    var dialogFunction = '(function(ext){this.' + dialogReturnBehaviorStr + '})';
                    if (PrimeFaces.csp.NONCE_VALUE) {
                        dialogReturnBehavior = PrimeFaces.csp.evalResult(dialogFunction);
                    }
                    else {
                        dialogReturnBehavior = windowContext.eval(dialogFunction);
                    }
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
                var messageDialogDOM = $('<div id="primefacesmessagedlg" class="ui-message-dialog ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container"></div>')
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
            this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + msg.severity.split(' ')[0].toLowerCase() + '-icon"></span>').append(detailHtml);
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
            // Note that the determination of the sourceFrames is tightly coupled to the same traversing logic, so keep both in sync
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
