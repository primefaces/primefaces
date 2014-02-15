$(document).on('pfAjaxStart', function() {
    $.mobile.loading('show');
});

$(document).on('pfAjaxComplete', function() {
    $.mobile.loading('hide');
});

PrimeFaces.Mobile = {
    
    navigate: function(to, cfg) {
        $('body').pagecontainer('change', to, cfg);
    }
    
};