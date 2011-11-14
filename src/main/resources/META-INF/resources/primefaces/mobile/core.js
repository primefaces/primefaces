$(document).bind('ajaxStart', function() {
    $.mobile.showPageLoadingMsg();
});

$(document).bind('ajaxComplete', function() {
    $.mobile.hidePageLoadingMsg();
});

$(document).bind("mobileinit", function(){
  $.mobile.ajaxEnabled = false;
});