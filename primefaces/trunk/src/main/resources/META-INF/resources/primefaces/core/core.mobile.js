$(document).on('pfAjaxStart', function() {
    $.mobile.loading('show');
});

$(document).on('pfAjaxComplete', function() {
    $.mobile.loading('hide');
});

PrimeFaces.Mobile = {
    
    navigate: function(to, cfg) {
        cfg.changeHash = cfg.changeHash||false;
        
        $('body').pagecontainer('change', to, cfg);
    }
    
};