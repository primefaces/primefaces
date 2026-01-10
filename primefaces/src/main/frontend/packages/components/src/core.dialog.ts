/**
 * The class with functionality related to working with dialogs and the dialog framework.
*/

import type { Dialog, DialogCfg } from "./dialog/dialog.widget.js";

// Note: Named "Dialogs" to avoid name collision with the "Dialog" widget class
export class Dialogs {
    DialogHandler: DialogHandler = new DialogHandler();
}

/**
 * The class with functionality for handling dialogs, as part of the dialog framework.
 */
export class DialogHandler {
    messageDialog?: Dialog<DialogCfg>;

    /**
     * Opens the dialog as specified by the given configuration. When the dialog is dynamic, loads the content from
     * the server.
     * @param cfg Configuration of the dialog.
     */
    openDialog(cfg: PrimeType.hook.dialog.DialogConfiguration): void {
        var rootWindow = this.findRootWindow(),
            dialogId = cfg.sourceComponentId + '_dlg';

        if (rootWindow.document.getElementById(dialogId)) {
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

        var sourceFrames = function () {
            let w: Window = window;
            const sourceFrames: string[] = [];
            // Traverse up frameElement i.e. while we are in frames
            while (w.frameElement) {
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
                let e: Element | ParentNode | null = w.frameElement;
                const pieces: string[] = [];

                // Traverse up tags from the frameElement to generate an identifying selector
                for (; e && "tagName" in e && e.tagName !== undefined; e = e.parentNode) {
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
                        var elems = wrapParentNodeInJq(e.parentNode).find(classSelector);
                        if (elems.length > 1) {
                            pieces.unshift(":eq(" + elems.index(e) + ")");
                        }
                        pieces.unshift(classSelector);
                    } else {
                        // Without classes, we try to work with :eq and the tag name
                        var tagElems = wrapParentNodeInJq(e.parentNode).find(e.tagName);
                        if (tagElems.length > 1) {
                            pieces.unshift(":eq(" + tagElems.index(e) + ")");
                        }
                        pieces.unshift(e.tagName);
                    }
                    pieces.unshift(' > ');
                }

                const s = pieces.slice(1).join('');

                sourceFrames.unshift(s);
                w = parent;
            };

            return sourceFrames;
        }();

        const dialogWidgetVar = cfg.options.widgetVar || cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget';

        const styleClass = cfg.options.styleClass || '';
        const dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-hidden-container ui-overlay-hidden ' + styleClass + '" data-pfdlgcid="' + PrimeFaces.escapeHTML(cfg.pfdlgcid) + '" data-widget="' + dialogWidgetVar + '"></div>');
        const box = $('<div class="ui-dialog-box ui-widget-content ui-shadow"></div>').appendTo(dialogDOM);
        const titlebar = $('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix"><span id="' + dialogId + '_title" class="ui-dialog-title"></span></div>').appendTo(box);

        if (cfg.options.closable !== false) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a>');
        }

        if (cfg.options.minimizable) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-minimize" href="#" role="button"><span class="ui-icon ui-icon-minus"></span></a>');
        }

        if (cfg.options.maximizable) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-maximize" href="#" role="button"><span class="ui-icon ui-icon-extlink"></span></a>');
        }

        var iframeStyleClass = cfg.options.iframeStyleClass || '';
        box.append('<div class="ui-dialog-content ui-widget-content ui-df-content" style="height: auto;">' +
            '<iframe class="' + iframeStyleClass + '" style="border:0 none" frameborder="0"></iframe>' +
            '</div>');

        dialogDOM.appendTo(rootWindow.document.body);

        var dialogFrame = dialogDOM.find('iframe'),
            symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
            frameURL = cfg.url.indexOf('pfdlgcid') === -1 ? cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid : cfg.url,
            frameWidth = cfg.options.contentWidth || 640;

        dialogFrame.width(frameWidth);

        if (cfg.options.iframeTitle) {
            dialogFrame.attr('title', cfg.options.iframeTitle);
        }

        dialogFrame.on('load', function () {
            const $frame = $(this);
            let headerElement: JQuery = $frame.contents().find('title');
            let isCustomHeader = false;

            if (cfg.options.headerElement) {
                const customHeaderId = PrimeFaces.escapeClientId(cfg.options.headerElement);
                const customHeaderElement = dialogFrame.contents().find(customHeaderId);

                if (customHeaderElement.length) {
                    headerElement = customHeaderElement;
                    isCustomHeader = true;
                }
            }

            if (!$frame.data('initialized')) {
                PrimeFaces.cw.call<
                    PrimeType.PrimeFaces, ["DynamicDialog", string, PrimeType.widget.PartialCreateWidgetCfg<PrimeType.widget.DynamicDialogCfg>], void
                >(rootWindow.PrimeFaces, 'DynamicDialog', dialogWidgetVar, {
                    id: dialogId,
                    position: cfg.options.position || 'center',
                    sourceFrames: sourceFrames,
                    sourceComponentId: cfg.sourceComponentId,
                    sourceWidgetVar: cfg.sourceWidgetVar,
                    onShow: function () {
                        if (cfg.options.onShow) {
                            const onShowFunction = '(function(ext){' + cfg.options.onShow + '})';
                            const onShowCallback = rootWindow.PrimeFaces.csp.NONCE_VALUE
                                ? PrimeFaces.csp.evalResult(onShowFunction, rootWindow.PrimeFaces.csp.NONCE_VALUE, rootWindow) as PrimeType.widget.Dialog.OnShowCallback | undefined
                                : rootWindow.eval(onShowFunction) as PrimeType.widget.Dialog.OnShowCallback | undefined;
                            if (onShowCallback) {
                                onShowCallback.call(this);
                            }
                        }
                    },
                    onHide: function () {
                        if (cfg.options.onHide) {
                            const onHideFunction = '(function(ext){' + cfg.options.onHide + '})';
                            const onHideCallback = rootWindow.PrimeFaces.csp.NONCE_VALUE
                                ? PrimeFaces.csp.evalResult(onHideFunction, rootWindow.PrimeFaces.csp.NONCE_VALUE, rootWindow) as PrimeType.widget.Dialog.OnHideCallback | undefined
                                : rootWindow.eval(onHideFunction) as PrimeType.widget.Dialog.OnHideCallback | undefined;
                            if (onHideCallback) {
                                onHideCallback.call(this);
                            }
                        }

                        const $dialogWidget = this,
                            dialogFrame = this.content.children('iframe');

                        const dialogFramePF = dialogFrame.get(0)?.contentWindow?.PrimeFaces;
                        if (dialogFramePF) {
                            this.destroyIntervalId = setInterval(function () {
                                if (dialogFramePF.ajax.Queue.isEmpty()) {
                                    clearInterval($dialogWidget.destroyIntervalId);
                                    dialogFrame.attr('src', 'about:blank');
                                    $dialogWidget.jq.remove();
                                }
                            }, 10);
                        }
                        else {
                            dialogFrame.attr('src', 'about:blank');
                            $dialogWidget.jq.remove();
                        }

                        rootWindow.PrimeFaces.widgets[dialogWidgetVar] = undefined as any;
                    },
                    getModalTabbables: function () {
                        return $frame.contents().find(':tabbable');
                    },
                    modal: cfg.options.modal,
                    blockScroll: cfg.options.blockScroll,
                    resizable: cfg.options.resizable,
                    hasIframe: true,
                    iframe: $frame,
                    draggable: cfg.options.draggable,
                    width: cfg.options.width,
                    height: cfg.options.height,
                    minimizable: cfg.options.minimizable,
                    maximizable: cfg.options.maximizable,
                    headerElement: cfg.options.headerElement,
                    responsive: cfg.options.responsive,
                    closeOnEscape: cfg.options.closeOnEscape,
                    dismissibleMask: cfg.options.dismissibleMask,
                    fitViewport: cfg.options.fitViewport,
                    resizeObserver: cfg.options.resizeObserver,
                    resizeObserverCenter: cfg.options.resizeObserverCenter
                });
            }

            const dialogWidget = rootWindow.PF(dialogWidgetVar) as PrimeType.Widget<"Dialog"> | undefined;
            const title = dialogWidget?.titlebar.children('span.ui-dialog-title') ?? $();
            if (headerElement.length > 0) {
                if (isCustomHeader) {
                    title.append(headerElement);
                    headerElement.show();
                }
                else {
                    title.text(headerElement.text());
                }

                dialogFrame.attr('title', title.text());
            }

            // adjust height
            let frameHeight: number;
            if (cfg.options.contentHeight) {
                frameHeight = cfg.options.contentHeight;
            }
            else {
                const frame = $frame.get(0);
                if (frame && frame.contentWindow) {
                    const frameBody = frame.contentWindow.document.body;
                    const frameBodyStyle = window.getComputedStyle(frameBody);
                    frameHeight = frameBody.scrollHeight + parseFloat(frameBodyStyle.marginTop) + parseFloat(frameBodyStyle.marginBottom);
                } else {
                    frameHeight = 0;
                }
            }

            $frame.css('height', String(frameHeight));

            // fix #1290 - dialogs are not centered vertically
            dialogFrame.data('initialized', true);
            dialogWidget?.show();
        })
            .attr('src', frameURL);
    }

    /**
     * Closes the dialog as specified by the given configuration.
     * @param cfg Configuration of the dialog.
     */
    closeDialog(cfg: PrimeType.hook.dialog.DialogConfiguration): void {
        const rootWindow = this.findRootWindow();
        const dlgs = $(rootWindow.document.body).children('div.ui-dialog[data-pfdlgcid="' + CSS.escape(cfg.pfdlgcid) + '"]').not('[data-queuedforremoval]');
        const dlgsLength = dlgs.length;
        const dlg = dlgs.eq(dlgsLength - 1);
        const parentDlg = dlgsLength > 1 ? dlgs.eq(dlgsLength - 2) : null;
        let dialogReturnBehavior: PrimeType.widget.Behavior | undefined = undefined;
        let windowContext: Window | null | undefined = null;

        const dlgWidget = rootWindow.PF(dlg.data('widget')) as PrimeType.Widget<"Dialog"> | undefined;
        if (!dlgWidget) {
            // GitHub #2039 dialog may already be closed on slow internet
            PrimeFaces.error('Dialog widget was not found to close.');
            return;
        }

        const sourceWidgetVar = dlgWidget.cfg.sourceWidgetVar;
        const sourceComponentId = dlgWidget.cfg.sourceComponentId;

        dlg.attr('data-queuedforremoval', "true");

        if (parentDlg) {
            const parentDlgFrame = parentDlg.find('> .ui-dialog-box > .ui-dialog-content > iframe').get(0) as HTMLIFrameElement | undefined;
            windowContext = parentDlgFrame?.contentWindow;
        }
        else {
            // We have to resolve the frames from the root window to the source widget to invoke the dialog return behavior
            // Each source frame element is a selector. We step into every nested frame until we are in the source widget frame.
            windowContext = rootWindow;
            const frames = dlgWidget.cfg.sourceFrames;
            for (const frame of frames ?? []) {
                const foundFrame: HTMLIFrameElement | undefined = windowContext
                    ? $(windowContext.document).find(frame).get(0) as HTMLIFrameElement | undefined
                    : undefined;
                windowContext = foundFrame?.contentWindow;
            }
        }

        if (sourceWidgetVar) {
            const sourceWidget = windowContext?.PF(sourceWidgetVar);
            dialogReturnBehavior = sourceWidget?.cfg.behaviors?.['dialogReturn'];
        }
        else if (sourceComponentId) {
            const sourceComponent = windowContext?.document.getElementById(sourceComponentId);
            const dialogReturnBehaviorStr = sourceComponent ? $(sourceComponent).data('dialogreturn') : "";
            if (dialogReturnBehaviorStr) {
                const dialogFunction = '(function(ext){this.' + dialogReturnBehaviorStr + '})';
                const nonceValue = windowContext?.PrimeFaces.csp.NONCE_VALUE;
                if (nonceValue && windowContext) {
                    dialogReturnBehavior = PrimeFaces.csp.evalResult(dialogFunction, nonceValue, windowContext) as PrimeType.widget.Behavior | undefined;
                }
                else {
                    dialogReturnBehavior = windowContext?.eval(dialogFunction) as PrimeType.widget.Behavior | undefined;
                }
            }
        }

        if (dialogReturnBehavior) {
            var ext = {
                params: [
                    { name: sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid }
                ]
            };

            // TODO Possible issue: All other behaviors are called with the widget
            // as the "this context". Only here are we passing "window" instead, which
            // does not type check and may also be confusing for users. We should
            // change the following line as follows, then the @ts-expect-error
            // can also be removed.
            //
            // dialogReturnBehavior.call(dlgWidget, ext);

            // @ts-expect-error See comment above.
            dialogReturnBehavior.call(windowContext, ext);
        }

        dlgWidget.hide();
    }

    /**
     * Displays a message in the messages dialog.
     * @param msg Details of the message to show.
     */
    showMessageInDialog(msg: PrimeType.hook.messageInDialog.DialogMessageData): void {
        if (!this.messageDialog) {
            $('<div id="primefacesmessagedlg" class="ui-message-dialog ui-dialog ui-widget ui-hidden-container"></div>')
                .append($('<div class="ui-dialog-box ui-widget-content ui-shadow"></div>').append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix"><span class="ui-dialog-title"></span>' +
                    '<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div>' +
                    '<div class="ui-dialog-content ui-widget-content" style="height: auto;"></div>'))
                .appendTo(document.body);

            PrimeFaces.cw('Dialog', 'primefacesmessagedialog', {
                id: 'primefacesmessagedlg',
                modal: true,
                draggable: false,
                resizable: false,
                showEffect: 'fade',
                hideEffect: 'fade',
            });
            this.messageDialog = PF('primefacesmessagedialog') as Dialog<DialogCfg>;
            this.messageDialog.titleContainer = this.messageDialog.titlebar.children('span.ui-dialog-title');
        }

        var escape = msg.escape !== false;
        var summaryHtml = msg.summary ? msg.summary.split(/\r\n|\n|\r/g).map(function (line) { return escape ? PrimeFaces.escapeHTML(line) : line; }).join("<br>") : "";
        this.messageDialog.titleContainer?.html(summaryHtml);

        var detailHtml = msg.detail ? msg.detail.split(/\r\n|\n|\r/g).map(function (line) { return escape ? PrimeFaces.escapeHTML(line) : line; }).join("<br>") : "";
        this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + (msg.severity.split(' ')[0] ?? "INFO").toLowerCase() + '-icon"></span>')
            .append('<span class="ui-dialog-message-content"></span');
        this.messageDialog.content.children('.ui-dialog-message-content').append(detailHtml);
        this.messageDialog.show();
    }

    /**
     * Asks the user to confirm an action. Shows a confirmation dialog with the given message. Requires a global
     * `<p:confirmDialog>` to be available on the current page.
     * @param msg Message to show in the confirmation dialog.
     */
    confirm(msg: PrimeType.hook.confirm.ExtendedConfirmMessage): void {
        if (PrimeFaces.confirmDialog) {
            PrimeFaces.confirmSource = (typeof (msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
            PrimeFaces.confirmDialog.showMessage(msg);
        }
        else {
            PrimeFaces.warn('No global confirmation dialog available.');
        }
    }

    /**
     * Returns the current window instance. When inside an iframe, returns the window instance of the topmost
     * document.
     * @returns The root window instance.
     */
    findRootWindow(): Window {
        // Note that the determination of the sourceFrames is tightly coupled to the same traversing logic, so keep both in sync
        let w: Window = window;
        while (w.frameElement) {
            const parent = w.parent;
            if (parent.PF === undefined) {
                break;
            }
            w = parent;
        };

        return w;
    }
}

/**
 * Registers dialog related features with the core. In particular:
 * 
 * - `confirm` feature: Redirects the emitted messages
 * either to the ConfirmPopup widget if available and the message's
 * {@link PrimeType.hook.confirm.ExtendedConfirmMessage.type | type} is
 * `popup`; or to the {@link DialogHandler | PrimeFaces.dialog.DialogHandler}
 * otherwise.
 * - `dialog` feature: Opens and closes dialogs via the
 * {@link DialogHandler | PrimeFaces.dialog.DialogHandler}.
 * - `messageInDialog` feature: Shows messages via the
 * {@link DialogHandler | PrimeFaces.dialog.DialogHandler}.
 */
export function registerDialogHooks(): void {
    PrimeFaces.registerHook("confirm", {
        handleMessage: message => {
            if (message.type === 'popup' && PrimeFaces.confirmPopup) {
                PrimeFaces.confirmPopup.showMessage(message);
                return true;
            }
            else {
                dialog.DialogHandler.confirm(message);
                return true;
            }
        },
    });

    PrimeFaces.registerHook("messageInDialog", {
        showMessage: message => {
            PrimeFaces.dialog.DialogHandler.showMessageInDialog(message);
            return true;
        },
    });

    PrimeFaces.registerHook("dialog", {
        closeDialog: cfg => {
            dialog.DialogHandler.closeDialog(cfg);
            return true;
        },
        openDialog: cfg => {
            dialog.DialogHandler.openDialog(cfg);
            return true;
        },
    }); 
}

/**
 * Wraps a {@link ParentNode} in a JQuery instance, accounting for the fact
 * the parent node might not exist.
 * @param node Parent node to wrap.
 * @returns The wrapped node, empty if parent node does not exist.
 */
function wrapParentNodeInJq(node: ParentNode | null): JQuery<ParentNode> {
    return node === null ? $() : $(node);
}

/**
 * The object with functionality related to working with dialogs and the dialog framework.
 */
export const dialog: Dialogs = new Dialogs();
