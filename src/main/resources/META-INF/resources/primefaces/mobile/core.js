$(function() {
   $(document).bind('ajaxStart', function() {
        $.mobile.pageLoading();
    });

    $(document).bind('ajaxComplete', function() {
        $.mobile.pageLoading(true);
    });
});