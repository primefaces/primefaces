This document includes the log of the custom changes by PrimeFaces which needs to be done each time a corresponding
resource is updated.

* Add expressinstall.swf to /yui/assets. This is for users without flash player installed.

* jQuery noConflict. jQuery source is slightly modified to always work in noConflict mode.

* Add yui json utility to utilities.js

* Add to layout.css
 .yui-layout .yui-layout-unit div.yui-layout-hd h2 {
	font-size: 100%;
}

* Add to datatable.css
th.yui-dt-hidden, td.yui-dt-hidden {
	display: none;
}

