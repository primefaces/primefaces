/*	SWFObject v2.2 <http://code.google.com/p/swfobject/>
	is released under the MIT License <http://www.opensource.org/licenses/mit-license.php>
*/
var swfobject=function(){var D="undefined",r="object",S="Shockwave Flash",W="ShockwaveFlash.ShockwaveFlash",q="application/x-shockwave-flash",R="SWFObjectExprInst",x="onreadystatechange",O=window,j=document,t=navigator,T=false,U=[h],o=[],N=[],I=[],l,Q,E,B,J=false,a=false,n,G,m=true,M=function(){var aa=typeof j.getElementById!=D&&typeof j.getElementsByTagName!=D&&typeof j.createElement!=D,ah=t.userAgent.toLowerCase(),Y=t.platform.toLowerCase(),ae=Y?/win/.test(Y):/win/.test(ah),ac=Y?/mac/.test(Y):/mac/.test(ah),af=/webkit/.test(ah)?parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,X=!+"\v1",ag=[0,0,0],ab=null;if(typeof t.plugins!=D&&typeof t.plugins[S]==r){ab=t.plugins[S].description;if(ab&&!(typeof t.mimeTypes!=D&&t.mimeTypes[q]&&!t.mimeTypes[q].enabledPlugin)){T=true;X=false;ab=ab.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ag[0]=parseInt(ab.replace(/^(.*)\..*$/,"$1"),10);ag[1]=parseInt(ab.replace(/^.*\.(.*)\s.*$/,"$1"),10);ag[2]=/[a-zA-Z]/.test(ab)?parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof O.ActiveXObject!=D){try{var ad=new ActiveXObject(W);if(ad){ab=ad.GetVariable("$version");if(ab){X=true;ab=ab.split(" ")[1].split(",");ag=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}}catch(Z){}}}return{w3:aa,pv:ag,wk:af,ie:X,win:ae,mac:ac}}(),k=function(){if(!M.w3){return}if((typeof j.readyState!=D&&j.readyState=="complete")||(typeof j.readyState==D&&(j.getElementsByTagName("body")[0]||j.body))){f()}if(!J){if(typeof j.addEventListener!=D){j.addEventListener("DOMContentLoaded",f,false)}if(M.ie&&M.win){j.attachEvent(x,function(){if(j.readyState=="complete"){j.detachEvent(x,arguments.callee);f()}});if(O==top){(function(){if(J){return}try{j.documentElement.doScroll("left")}catch(X){setTimeout(arguments.callee,0);return}f()})()}}if(M.wk){(function(){if(J){return}if(!/loaded|complete/.test(j.readyState)){setTimeout(arguments.callee,0);return}f()})()}s(f)}}();function f(){if(J){return}try{var Z=j.getElementsByTagName("body")[0].appendChild(C("span"));Z.parentNode.removeChild(Z)}catch(aa){return}J=true;var X=U.length;for(var Y=0;Y<X;Y++){U[Y]()}}function K(X){if(J){X()}else{U[U.length]=X}}function s(Y){if(typeof O.addEventListener!=D){O.addEventListener("load",Y,false)}else{if(typeof j.addEventListener!=D){j.addEventListener("load",Y,false)}else{if(typeof O.attachEvent!=D){i(O,"onload",Y)}else{if(typeof O.onload=="function"){var X=O.onload;O.onload=function(){X();Y()}}else{O.onload=Y}}}}}function h(){if(T){V()}else{H()}}function V(){var X=j.getElementsByTagName("body")[0];var aa=C(r);aa.setAttribute("type",q);var Z=X.appendChild(aa);if(Z){var Y=0;(function(){if(typeof Z.GetVariable!=D){var ab=Z.GetVariable("$version");if(ab){ab=ab.split(" ")[1].split(",");M.pv=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}else{if(Y<10){Y++;setTimeout(arguments.callee,10);return}}X.removeChild(aa);Z=null;H()})()}else{H()}}function H(){var ag=o.length;if(ag>0){for(var af=0;af<ag;af++){var Y=o[af].id;var ab=o[af].callbackFn;var aa={success:false,id:Y};if(M.pv[0]>0){var ae=c(Y);if(ae){if(F(o[af].swfVersion)&&!(M.wk&&M.wk<312)){w(Y,true);if(ab){aa.success=true;aa.ref=z(Y);ab(aa)}}else{if(o[af].expressInstall&&A()){var ai={};ai.data=o[af].expressInstall;ai.width=ae.getAttribute("width")||"0";ai.height=ae.getAttribute("height")||"0";if(ae.getAttribute("class")){ai.styleclass=ae.getAttribute("class")}if(ae.getAttribute("align")){ai.align=ae.getAttribute("align")}var ah={};var X=ae.getElementsByTagName("param");var ac=X.length;for(var ad=0;ad<ac;ad++){if(X[ad].getAttribute("name").toLowerCase()!="movie"){ah[X[ad].getAttribute("name")]=X[ad].getAttribute("value")}}P(ai,ah,Y,ab)}else{p(ae);if(ab){ab(aa)}}}}}else{w(Y,true);if(ab){var Z=z(Y);if(Z&&typeof Z.SetVariable!=D){aa.success=true;aa.ref=Z}ab(aa)}}}}}function z(aa){var X=null;var Y=c(aa);if(Y&&Y.nodeName=="OBJECT"){if(typeof Y.SetVariable!=D){X=Y}else{var Z=Y.getElementsByTagName(r)[0];if(Z){X=Z}}}return X}function A(){return !a&&F("6.0.65")&&(M.win||M.mac)&&!(M.wk&&M.wk<312)}function P(aa,ab,X,Z){a=true;E=Z||null;B={success:false,id:X};var ae=c(X);if(ae){if(ae.nodeName=="OBJECT"){l=g(ae);Q=null}else{l=ae;Q=X}aa.id=R;if(typeof aa.width==D||(!/%$/.test(aa.width)&&parseInt(aa.width,10)<310)){aa.width="310"}if(typeof aa.height==D||(!/%$/.test(aa.height)&&parseInt(aa.height,10)<137)){aa.height="137"}j.title=j.title.slice(0,47)+" - Flash Player Installation";var ad=M.ie&&M.win?"ActiveX":"PlugIn",ac="MMredirectURL="+O.location.toString().replace(/&/g,"%26")+"&MMplayerType="+ad+"&MMdoctitle="+j.title;if(typeof ab.flashvars!=D){ab.flashvars+="&"+ac}else{ab.flashvars=ac}if(M.ie&&M.win&&ae.readyState!=4){var Y=C("div");X+="SWFObjectNew";Y.setAttribute("id",X);ae.parentNode.insertBefore(Y,ae);ae.style.display="none";(function(){if(ae.readyState==4){ae.parentNode.removeChild(ae)}else{setTimeout(arguments.callee,10)}})()}u(aa,ab,X)}}function p(Y){if(M.ie&&M.win&&Y.readyState!=4){var X=C("div");Y.parentNode.insertBefore(X,Y);X.parentNode.replaceChild(g(Y),X);Y.style.display="none";(function(){if(Y.readyState==4){Y.parentNode.removeChild(Y)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.replaceChild(g(Y),Y)}}function g(ab){var aa=C("div");if(M.win&&M.ie){aa.innerHTML=ab.innerHTML}else{var Y=ab.getElementsByTagName(r)[0];if(Y){var ad=Y.childNodes;if(ad){var X=ad.length;for(var Z=0;Z<X;Z++){if(!(ad[Z].nodeType==1&&ad[Z].nodeName=="PARAM")&&!(ad[Z].nodeType==8)){aa.appendChild(ad[Z].cloneNode(true))}}}}}return aa}function u(ai,ag,Y){var X,aa=c(Y);if(M.wk&&M.wk<312){return X}if(aa){if(typeof ai.id==D){ai.id=Y}if(M.ie&&M.win){var ah="";for(var ae in ai){if(ai[ae]!=Object.prototype[ae]){if(ae.toLowerCase()=="data"){ag.movie=ai[ae]}else{if(ae.toLowerCase()=="styleclass"){ah+=' class="'+ai[ae]+'"'}else{if(ae.toLowerCase()!="classid"){ah+=" "+ae+'="'+ai[ae]+'"'}}}}}var af="";for(var ad in ag){if(ag[ad]!=Object.prototype[ad]){af+='<param name="'+ad+'" value="'+ag[ad]+'" />'}}aa.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ah+">"+af+"</object>";N[N.length]=ai.id;X=c(ai.id)}else{var Z=C(r);Z.setAttribute("type",q);for(var ac in ai){if(ai[ac]!=Object.prototype[ac]){if(ac.toLowerCase()=="styleclass"){Z.setAttribute("class",ai[ac])}else{if(ac.toLowerCase()!="classid"){Z.setAttribute(ac,ai[ac])}}}}for(var ab in ag){if(ag[ab]!=Object.prototype[ab]&&ab.toLowerCase()!="movie"){e(Z,ab,ag[ab])}}aa.parentNode.replaceChild(Z,aa);X=Z}}return X}function e(Z,X,Y){var aa=C("param");aa.setAttribute("name",X);aa.setAttribute("value",Y);Z.appendChild(aa)}function y(Y){var X=c(Y);if(X&&X.nodeName=="OBJECT"){if(M.ie&&M.win){X.style.display="none";(function(){if(X.readyState==4){b(Y)}else{setTimeout(arguments.callee,10)}})()}else{X.parentNode.removeChild(X)}}}function b(Z){var Y=c(Z);if(Y){for(var X in Y){if(typeof Y[X]=="function"){Y[X]=null}}Y.parentNode.removeChild(Y)}}function c(Z){var X=null;try{X=j.getElementById(Z)}catch(Y){}return X}function C(X){return j.createElement(X)}function i(Z,X,Y){Z.attachEvent(X,Y);I[I.length]=[Z,X,Y]}function F(Z){var Y=M.pv,X=Z.split(".");X[0]=parseInt(X[0],10);X[1]=parseInt(X[1],10)||0;X[2]=parseInt(X[2],10)||0;return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false}function v(ac,Y,ad,ab){if(M.ie&&M.mac){return}var aa=j.getElementsByTagName("head")[0];if(!aa){return}var X=(ad&&typeof ad=="string")?ad:"screen";if(ab){n=null;G=null}if(!n||G!=X){var Z=C("style");Z.setAttribute("type","text/css");Z.setAttribute("media",X);n=aa.appendChild(Z);if(M.ie&&M.win&&typeof j.styleSheets!=D&&j.styleSheets.length>0){n=j.styleSheets[j.styleSheets.length-1]}G=X}if(M.ie&&M.win){if(n&&typeof n.addRule==r){n.addRule(ac,Y)}}else{if(n&&typeof j.createTextNode!=D){n.appendChild(j.createTextNode(ac+" {"+Y+"}"))}}}function w(Z,X){if(!m){return}var Y=X?"visible":"hidden";if(J&&c(Z)){c(Z).style.visibility=Y}else{v("#"+Z,"visibility:"+Y)}}function L(Y){var Z=/[\\\"<>\.;]/;var X=Z.exec(Y)!=null;return X&&typeof encodeURIComponent!=D?encodeURIComponent(Y):Y}var d=function(){if(M.ie&&M.win){window.attachEvent("onunload",function(){var ac=I.length;for(var ab=0;ab<ac;ab++){I[ab][0].detachEvent(I[ab][1],I[ab][2])}var Z=N.length;for(var aa=0;aa<Z;aa++){y(N[aa])}for(var Y in M){M[Y]=null}M=null;for(var X in swfobject){swfobject[X]=null}swfobject=null})}}();return{registerObject:function(ab,X,aa,Z){if(M.w3&&ab&&X){var Y={};Y.id=ab;Y.swfVersion=X;Y.expressInstall=aa;Y.callbackFn=Z;o[o.length]=Y;w(ab,false)}else{if(Z){Z({success:false,id:ab})}}},getObjectById:function(X){if(M.w3){return z(X)}},embedSWF:function(ab,ah,ae,ag,Y,aa,Z,ad,af,ac){var X={success:false,id:ah};if(M.w3&&!(M.wk&&M.wk<312)&&ab&&ah&&ae&&ag&&Y){w(ah,false);K(function(){ae+="";ag+="";var aj={};if(af&&typeof af===r){for(var al in af){aj[al]=af[al]}}aj.data=ab;aj.width=ae;aj.height=ag;var am={};if(ad&&typeof ad===r){for(var ak in ad){am[ak]=ad[ak]}}if(Z&&typeof Z===r){for(var ai in Z){if(typeof am.flashvars!=D){am.flashvars+="&"+ai+"="+Z[ai]}else{am.flashvars=ai+"="+Z[ai]}}}if(F(Y)){var an=u(aj,am,ah);if(aj.id==ah){w(ah,true)}X.success=true;X.ref=an}else{if(aa&&A()){aj.data=aa;P(aj,am,ah,ac);return}else{w(ah,true)}}if(ac){ac(X)}})}else{if(ac){ac(X)}}},switchOffAutoHideShow:function(){m=false},ua:M,getFlashPlayerVersion:function(){return{major:M.pv[0],minor:M.pv[1],release:M.pv[2]}},hasFlashPlayerVersion:F,createSWF:function(Z,Y,X){if(M.w3){return u(Z,Y,X)}else{return undefined}},showExpressInstall:function(Z,aa,X,Y){if(M.w3&&A()){P(Z,aa,X,Y)}},removeSWF:function(X){if(M.w3){y(X)}},createCSS:function(aa,Z,Y,X){if(M.w3){v(aa,Z,Y,X)}},addDomLoadEvent:K,addLoadEvent:s,getQueryParamValue:function(aa){var Z=j.location.search||j.location.hash;if(Z){if(/\?/.test(Z)){Z=Z.split("?")[1]}if(aa==null){return L(Z)}var Y=Z.split("&");for(var X=0;X<Y.length;X++){if(Y[X].substring(0,Y[X].indexOf("="))==aa){return L(Y[X].substring((Y[X].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var X=c(R);if(X&&l){X.parentNode.replaceChild(l,X);if(Q){w(Q,true);if(M.ie&&M.win){l.style.display="block"}}if(E){E(B)}}a=false}}}}();

/*
Uploadify v2.1.0
Release Date: August 24, 2009

Copyright (c) 2009 Ronnie Garcia, Travis Nickels

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

if(jQuery)(
	function(jQuery){
		jQuery.extend(jQuery.fn,{
			uploadify:function(options) {
				jQuery(this).each(function(){
					settings = jQuery.extend({
					id             : jQuery(this).attr('id'), // The ID of the object being Uploadified
					uploader       : 'uploadify.swf', // The path to the uploadify swf file
					script         : 'uploadify.php', // The path to the uploadify backend upload script
					expressInstall : null, // The path to the express install swf file
					folder         : '', // The path to the upload folder
					height         : 30, // The height of the flash button
					width          : 110, // The width of the flash button
					cancelImg      : 'cancel.png', // The path to the cancel image for the default file queue item container
					wmode          : 'opaque', // The wmode of the flash file
					scriptAccess   : 'sameDomain', // Set to "always" to allow script access across domains
					fileDataName   : 'Filedata', // The name of the file collection object in the backend upload script
					method         : 'POST', // The method for sending variables to the backend upload script
					queueSizeLimit : 999, // The maximum size of the file queue
					simUploadLimit : 1, // The number of simultaneous uploads allowed
					queueID        : false, // The optional ID of the queue container
					displayData    : 'percentage', // Set to "speed" to show the upload speed in the default queue item
					onInit         : function() {}, // Function to run when uploadify is initialized
					onSelect       : function() {}, // Function to run when a file is selected
					onQueueFull    : function() {}, // Function to run when the queue reaches capacity
					onCheck        : function() {}, // Function to run when script checks for duplicate files on the server
					onCancel       : function() {}, // Function to run when an item is cleared from the queue
					onError        : function() {}, // Function to run when an upload item returns an error
					onProgress     : function() {}, // Function to run each time the upload progress is updated
					onComplete     : function() {}, // Function to run when an upload is completed
					onAllComplete  : function() {}  // Functino to run when all uploads are completed
				}, options);
				var pagePath = location.pathname;
				pagePath = pagePath.split('/');
				pagePath.pop();
				pagePath = pagePath.join('/') + '/';
				var data = {};
				data.uploadifyID = settings.id;
				data.pagepath = pagePath;
				if (settings.buttonImg) data.buttonImg = escape(settings.buttonImg);
				if (settings.buttonText) data.buttonText = escape(settings.buttonText);
				if (settings.rollover) data.rollover = true;
				data.script = settings.script;
				data.folder = escape(settings.folder);
				if (settings.scriptData) {
					var scriptDataString = '';
					for (var name in settings.scriptData) {
						scriptDataString += '&' + name + '=' + settings.scriptData[name];
					}
					data.scriptData = escape(scriptDataString.substr(1));
				}
				data.width          = settings.width;
				data.height         = settings.height;
				data.wmode          = settings.wmode;
				data.method         = settings.method;
				data.queueSizeLimit = settings.queueSizeLimit;
				data.simUploadLimit = settings.simUploadLimit;
				if (settings.hideButton)   data.hideButton   = true;
				if (settings.fileDesc)     data.fileDesc     = settings.fileDesc;
				if (settings.fileExt)      data.fileExt      = settings.fileExt;
				if (settings.multi)        data.multi        = true;
				if (settings.auto)         data.auto         = true;
				if (settings.sizeLimit)    data.sizeLimit    = settings.sizeLimit;
				if (settings.checkScript)  data.checkScript  = settings.checkScript;
				if (settings.fileDataName) data.fileDataName = settings.fileDataName;
				if (settings.queueID)      data.queueID      = settings.queueID;
				if (settings.onInit() !== false) {
					jQuery(this).css('display','none');
					jQuery(this).after('<div id="' + jQuery(this).attr('id') + 'Uploader"></div>');
					swfobject.embedSWF(settings.uploader, settings.id + 'Uploader', settings.width, settings.height, '9.0.24', settings.expressInstall, data, {'quality':'high','wmode':settings.wmode,'allowScriptAccess':settings.scriptAccess});
					if (settings.queueID == false) {
						jQuery("#" + jQuery(this).attr('id') + "Uploader").after('<div id="' + jQuery(this).attr('id') + 'Queue" class="uploadifyQueue"></div>');
					}
				}
				if (typeof(settings.onOpen) == 'function') {
					jQuery(this).bind("uploadifyOpen", settings.onOpen);
				}
				jQuery(this).bind("uploadifySelect", {'action': settings.onSelect, 'queueID': settings.queueID}, function(event, ID, fileObj) {
					if (event.data.action(event, ID, fileObj) !== false) {
						var byteSize = Math.round(fileObj.size / 1024 * 100) * .01;
						var suffix = 'KB';
						if (byteSize > 1000) {
							byteSize = Math.round(byteSize *.001 * 100) * .01;
							suffix = 'MB';
						}
						var sizeParts = byteSize.toString().split('.');
						if (sizeParts.length > 1) {
							byteSize = sizeParts[0] + '.' + sizeParts[1].substr(0,2);
						} else {
							byteSize = sizeParts[0];
						}
						if (fileObj.name.length > 20) {
							fileName = fileObj.name.substr(0,20) + '...';
						} else {
							fileName = fileObj.name;
						}
						queue = '#' + jQuery(this).attr('id') + 'Queue';
						if (event.data.queueID) {
							queue = '#' + event.data.queueID;
						}
						jQuery(queue).append('<div id="' + jQuery(this).attr('id') + ID + '" class="uploadifyQueueItem">\
								<div class="cancel">\
									<a href="javascript:jQuery(\'#' + jQuery(this).attr('id') + '\').uploadifyCancel(\'' + ID + '\')"><img src="' + settings.cancelImg + '" border="0" /></a>\
								</div>\
								<span class="fileName">' + fileName + ' (' + byteSize + suffix + ')</span><span class="percentage"></span>\
								<div class="uploadifyProgress">\
									<div id="' + jQuery(this).attr('id') + ID + 'ProgressBar" class="uploadifyProgressBar"><!--Progress Bar--></div>\
								</div>\
							</div>');
					}
				});
				if (typeof(settings.onSelectOnce) == 'function') {
					jQuery(this).bind("uploadifySelectOnce", settings.onSelectOnce);
				}
				jQuery(this).bind("uploadifyQueueFull", {'action': settings.onQueueFull}, function(event, queueSizeLimit) {
					if (event.data.action(event, queueSizeLimit) !== false) {
						alert('The queue is full.  The max size is ' + queueSizeLimit + '.');
					}
				});
				jQuery(this).bind("uploadifyCheckExist", {'action': settings.onCheck}, function(event, checkScript, fileQueueObj, folder, single) {
					var postData = new Object();
					postData = fileQueueObj;
					postData.folder = pagePath + folder;
					if (single) {
						for (var ID in fileQueueObj) {
							var singleFileID = ID;
						}
					}
					jQuery.post(checkScript, postData, function(data) {
						for(var key in data) {
							if (event.data.action(event, checkScript, fileQueueObj, folder, single) !== false) {
								var replaceFile = confirm("Do you want to replace the file " + data[key] + "?");
								if (!replaceFile) {
									document.getElementById(jQuery(event.target).attr('id') + 'Uploader').cancelFileUpload(key, true,true);
								}
							}
						}
						if (single) {
							document.getElementById(jQuery(event.target).attr('id') + 'Uploader').startFileUpload(singleFileID, true);
						} else {
							document.getElementById(jQuery(event.target).attr('id') + 'Uploader').startFileUpload(null, true);
						}
					}, "json");
				});
				jQuery(this).bind("uploadifyCancel", {'action': settings.onCancel}, function(event, ID, fileObj, data, clearFast) {
					if (event.data.action(event, ID, fileObj, data, clearFast) !== false) {
						var fadeSpeed = (clearFast == true) ? 0 : 250;
						jQuery("#" + jQuery(this).attr('id') + ID).fadeOut(fadeSpeed, function() { jQuery(this).remove() });
					}
				});
				if (typeof(settings.onClearQueue) == 'function') {
					jQuery(this).bind("uploadifyClearQueue", settings.onClearQueue);
				}
				var errorArray = [];
				jQuery(this).bind("uploadifyError", {'action': settings.onError}, function(event, ID, fileObj, errorObj) {
					if (event.data.action(event, ID, fileObj, errorObj) !== false) {
						var fileArray = new Array(ID, fileObj, errorObj);
						errorArray.push(fileArray);
						jQuery("#" + jQuery(this).attr('id') + ID + " .percentage").text(" - " + errorObj.type + " Error");
						jQuery("#" + jQuery(this).attr('id') + ID).addClass('uploadifyError');
					}
				});
				jQuery(this).bind("uploadifyProgress", {'action': settings.onProgress, 'toDisplay': settings.displayData}, function(event, ID, fileObj, data) {
					if (event.data.action(event, ID, fileObj, data) !== false) {
						jQuery("#" + jQuery(this).attr('id') + ID + "ProgressBar").css('width', data.percentage + '%');
						if (event.data.toDisplay == 'percentage') displayData = ' - ' + data.percentage + '%';
						if (event.data.toDisplay == 'speed') displayData = ' - ' + data.speed + 'KB/s';
						if (event.data.toDisplay == null) displayData = ' ';
						jQuery("#" + jQuery(this).attr('id') + ID + " .percentage").text(displayData);
					}
				});
				jQuery(this).bind("uploadifyComplete", {'action': settings.onComplete}, function(event, ID, fileObj, response, data) {
					if (event.data.action(event, ID, fileObj, unescape(response), data) !== false) {
						jQuery("#" + jQuery(this).attr('id') + ID + " .percentage").text(' - Completed');
						jQuery("#" + jQuery(this).attr('id') + ID).fadeOut(250, function() { jQuery(this).remove()});
					}
				});
				if (typeof(settings.onAllComplete) == 'function') {
					jQuery(this).bind("uploadifyAllComplete", {'action': settings.onAllComplete}, function(event, uploadObj) {
						if (event.data.action(event, uploadObj) !== false) {
							errorArray = [];
						}
					});
				}
			});
		},
		uploadifySettings:function(settingName, settingValue, resetObject) {
			var returnValue = false;
			jQuery(this).each(function() {
				if (settingName == 'scriptData' && settingValue != null) {
					if (resetObject) {
						var scriptData = settingValue;
					} else {
						var scriptData = jQuery.extend(settings.scriptData, settingValue);
					}
					var scriptDataString = '';
					for (var name in scriptData) {
						scriptDataString += '&' + name + '=' + escape(scriptData[name]);
					}
					settingValue = scriptDataString.substr(1);
				}
				returnValue = document.getElementById(jQuery(this).attr('id') + 'Uploader').updateSettings(settingName, settingValue);
			});
			if (settingValue == null) {
				if (settingName == 'scriptData') {
					var returnSplit = unescape(returnValue).split('&');
					var returnObj   = new Object();
					for (var i = 0; i < returnSplit.length; i++) {
						var iSplit = returnSplit[i].split('=');
						returnObj[iSplit[0]] = iSplit[1];
					}
					returnValue = returnObj;
				}
				return returnValue;
			}
		},
		uploadifyUpload:function(ID) {
			jQuery(this).each(function() {
				document.getElementById(jQuery(this).attr('id') + 'Uploader').startFileUpload(ID, false);
			});
		},
		uploadifyCancel:function(ID) {
			jQuery(this).each(function() {
				document.getElementById(jQuery(this).attr('id') + 'Uploader').cancelFileUpload(ID, true, false);
			});
		},
		uploadifyClearQueue:function() {
			jQuery(this).each(function() {
				document.getElementById(jQuery(this).attr('id') + 'Uploader').clearFileUploadQueue(false);
			});
		}
	})
})(jQuery);

/**
 * PrimeFaces FileUpload Widget
 */
PrimeFaces.widget.Uploader = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.inputFileId = '#' + this.cfg.inputFileId;

	this.cfg.scriptData = this.createPostParams();

	if(this.cfg.update) {
		this.cfg.onComplete = this.onComplete;
	}

	jQuery(this.inputFileId).uploadify(this.cfg);
}

PrimeFaces.widget.Uploader.prototype.upload = function() {
	jQuery(this.inputFileId).uploadifyUpload();
}

PrimeFaces.widget.Uploader.prototype.clear = function() {
	jQuery(this.inputFileId).uploadifyClearQueue();
}

PrimeFaces.widget.Uploader.prototype.onComplete = function(event, queueID, fileObj, response, data) {
	 var responseXML = {};

	 if(window.DOMParser) {
		 responseXML.documentElement = new DOMParser().parseFromString(response, "text/xml");
	 }
	 else {
		 var xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
		 xmlDoc.async = "false";
		 xmlDoc.loadXML(response);
		 responseXML.documentElement = xmlDoc.documentElement;
	 }

	 PrimeFaces.ajax.AjaxResponse(responseXML);

	 return true;
}

PrimeFaces.widget.Uploader.prototype.createPostParams = function() {
	var params = {};
	params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
    params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.id;
    params[PrimeFaces.VIEW_STATE] = PrimeFaces.ajax.AjaxUtils.encodeViewState();

	if(this.cfg.update) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.update;
	}

    //session handling
	var jsessionid = this.cfg.jsessionid,
    url = this.cfg.script,
    hasSessionId = url.indexOf('jsessionid') != -1,
    paramIndex = url.indexOf('?'),
    hasParam = paramIndex != -1;

    if(hasParam) {
        var plainUrl = url.substring(0, paramIndex),
        urlParamsString = url.substring(paramIndex + 1, url.length);

        if(!hasSessionId) {
            url = plainUrl + ';jsessionid=' + jsessionid + '?' + urlParamsString;
        }

        //add get params as post params
        var urlParams = urlParamsString.split('&');
        for(var i in urlParams) {
            var paramTokens = urlParams[i].split('='),
            paramName = paramTokens[0],
            paramValue = paramTokens[1];

            params[paramName] = paramValue;
        }

    }
    else {
        if(!hasSessionId) {
            url = url + ';jsessionid=' + jsessionid;
        }
    }

    this.cfg.script = url;

	return params;
}