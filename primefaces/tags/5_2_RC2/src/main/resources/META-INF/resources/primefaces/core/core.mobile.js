$(document).on('pfAjaxStart', function() {
    $.mobile.loading('show');
});

$(document).on('pfAjaxComplete', function() {
    $.mobile.loading('hide');
});

PrimeFaces.getCoreScriptName = function() {
    return 'primefaces-mobile.js';
};

PrimeFaces.confirm = function(msg) {
    if(PrimeFaces.confirmDialog) {
        PrimeFaces.confirmSource = (typeof(msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
        PrimeFaces.confirmDialog.showMessage(msg);
    }
    else {
        PrimeFaces.warn('No global confirmation dialog available.');
    }
}

PrimeFaces.Mobile = {
    
    navigate: function(to, cfg) {
        cfg.changeHash = cfg.changeHash||false;
        
        var targetPage = $(to);
        if(targetPage.hasClass('ui-lazypage')) {
            var pageId = to.substring(1),
            options = {
                source: pageId,
                process: pageId,
                update: pageId,
                params: [
                    {name: pageId + '_lazyload', value: true}
                ],
                oncomplete: function() {
                    $(to).page();
                    $('body').pagecontainer('change', to, cfg);
                }
            };

            PrimeFaces.ajax.Request.handle(options);
        }
        else {
            $('body').pagecontainer('change', to, cfg);
        }
    }
    
};


