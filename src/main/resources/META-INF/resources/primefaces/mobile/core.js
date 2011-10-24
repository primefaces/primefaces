$(function() {
   $(document).bind('ajaxStart', function() {
        $.mobile.showPageLoadingMsg();
    });

    $(document).bind('ajaxComplete', function() {
        $.mobile.hidePageLoadingMsg();
    });
});