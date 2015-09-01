PrimeFaces.dialog = {};

PrimeFaces.dialog.DialogHandler = {
		
    openDialog: function(cfg) {
        var dialogId = cfg.sourceComponentId + '_dlg';
        if(document.getElementById(dialogId)) {
            return;
        }

        var dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget',
        dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container ui-overlay-hidden"' + 
                ' data-pfdlgcid="' + cfg.pfdlgcid + '" data-widgetvar="' + dialogWidgetVar + '"></div>')
                .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span></div>');
        
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
        
        dialogDOM.appendTo(document.body);
        
        var dialogFrame = dialogDOM.find('iframe'),
        symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
        frameURL = cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid,
        frameWidth = cfg.options.contentWidth||640;

        dialogFrame.width(frameWidth);

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
                PrimeFaces.cw('DynamicDialog', dialogWidgetVar, {
                    id: dialogId,
                    position: 'center',
                    sourceComponentId: cfg.sourceComponentId,
                    sourceWidget: cfg.sourceWidget,
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
                        
                        PF[dialogWidgetVar] = undefined;
                    },
                    modal: cfg.options.modal,
                    resizable: cfg.options.resizable,
                    hasIframe: true,
                    draggable: cfg.options.draggable,
                    width: cfg.options.width,
                    height: cfg.options.height,
                    minimizable: cfg.options.minimizable,
                    maximizable: cfg.options.maximizable,
                    headerElement: cfg.options.headerElement
                });
            }
            
            var title = PF(dialogWidgetVar).titlebar.children('span.ui-dialog-title');
            if(headerElement.length > 0) {
                if(isCustomHeader) {
                    title.append(headerElement);
                    headerElement.show();
                }
                else {
                    title.text(headerElement.text());
                }
            }
            
            //adjust height
            var frameHeight = null;
            if(cfg.options.contentHeight)
                frameHeight = cfg.options.contentHeight;
            else
                frameHeight = $frame.get(0).contentWindow.document.body.scrollHeight + (PrimeFaces.env.browser.webkit ? 5 : 20);
            
            $frame.css('height', frameHeight);
            
            dialogFrame.data('initialized', true);
            
            PF(dialogWidgetVar).show();
        })
        .attr('src', frameURL);
    },
    
    closeDialog: function(cfg) {
        var dlg = $(document.body).children('div.ui-dialog').filter(function() {
            return $(this).data('pfdlgcid') === cfg.pfdlgcid;
        }),
        dlgWidget = PF(dlg.data('widgetvar')),
        sourceWidget = dlgWidget.cfg.sourceWidget,
        sourceComponentId = dlgWidget.cfg.sourceComponentId,
        dialogReturnBehavior = null;

        if(sourceWidget && sourceWidget.cfg.behaviors) {
            dialogReturnBehavior = sourceWidget.cfg.behaviors['dialogReturn'];
        }
        else if(sourceComponentId) {
            var dialogReturnBehaviorStr = $(document.getElementById(sourceComponentId)).data('dialogreturn');
            if(dialogReturnBehaviorStr) {
                dialogReturnBehavior = eval('(function(ext){' + dialogReturnBehaviorStr + '})');
            }

        }
                    
        if(dialogReturnBehavior) {
            var ext = {
                    params: [
                        {name: sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid}
                    ]
                };
            
            dialogReturnBehavior.call(this, ext);
        }
        
        dlgWidget.hide();
    },
            
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

        this.messageDialog.titleContainer.text(msg.summary);
        this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + msg.severity.split(' ')[0].toLowerCase() + '-icon" />').append(msg.detail);
        this.messageDialog.show();
    },
            
    confirm: function(msg) {
        if(PrimeFaces.confirmDialog) {
            PrimeFaces.confirmSource = (typeof(msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
            PrimeFaces.confirmDialog.showMessage(msg);
        }
        else {
            PrimeFaces.warn('No global confirmation dialog available.');
        }
    }
};