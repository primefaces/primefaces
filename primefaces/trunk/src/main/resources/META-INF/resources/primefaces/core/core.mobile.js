$(document).on('pfAjaxStart', function() {
    $.mobile.loading('show');
});

$(document).on('pfAjaxComplete', function() {
    $.mobile.loading('hide');
});