/*  ContentFlow, version 1.0.2 
 *  (c) 2007 - 2010 Sebastian Kutsch
 *  <http://www.jacksasylum.eu/ContentFlow/>
 *
 *  ContentFlow is distributed under the terms of the MIT license.
 *  (see http://www.jacksasylum.eu/ContentFlow/LICENSE)
 *
 *--------------------------------------------------------------------------*/
/* 
 * ============================================================
 * Global configutaion and initilization object
 * ============================================================
 */
var ContentFlowGlobal = {
    Flows: new Array,
    AddOns: {}, 
    scriptName: 'contentflow.js',
    scriptElement:  null,
    Browser: new (function () {
        this.Opera = window.opera ? true : false;
        this.IE = document.all && !this.Opera ? true : false;
        this.IE6 = this.IE && typeof(window.XMLHttpRequest) == "undefined" ? true : false;
        this.IE8 = this.IE && typeof(document.querySelectorAll) != "undefined" ? true : false;
        this.IE7 = this.IE && ! this.IE6 && !this.IE8 ? true : false;
        this.WebKit = /WebKit/i.test(navigator.userAgent) ? true : false, 
        this.iPhone = /iPhone|iPod/i.test(navigator.userAgent)? true : false;
        this.Chrome = /Chrome/i.test(navigator.userAgent) ? true : false;
        this.Safari = /Safari/i.test(navigator.userAgent) && !this.Chrome ? true : false;
        this.Konqueror = navigator.vendor == "KDE" ? true : false;
        this.Konqueror4 = this.Konqueror && /native code/.test(document.getElementsByClassName) ? true : false;
        this.Gecko = !this.WebKit && navigator.product == "Gecko" ? true : false;
        this.Gecko19 = this.Gecko && Array.reduce ? true : false;
    })(),

    getAddOnConf: function(name) {
        if(this.AddOns[name])
            return this.AddOns[name].conf;
        else
            return {};
    },

    setAddOnConf: function (name, conf) {
        this.AddOns[name].setConfig(conf);
    },

    getScriptElement:function (scriptName) {
        var regex = new RegExp(scriptName);
        var scripts = document.getElementsByTagName('script');
        for (var i=0; i<scripts.length; i++) {
            if (scripts[i].src && regex.test(scripts[i].src))
                return scripts[i];
        }
        return '';
    },

    getScriptPath: function (scriptElement, scriptName) {
        var regex = new RegExp(scriptName+".*");
        return scriptElement.src.replace(regex, '');
    },

    addScript: function  (path) {
        if (this.Browser.IE || this.Browser.WebKit || this.Browser.Konqueror) {
            document.write('<script type="text/javascript" src="'+path+'"><\/script>');
        }
        else {
            var script = document.createElement('script');
            script.src = path;
            script.setAttribute('type', 'text/javascript');
            document.getElementsByTagName('head')[0].appendChild(script);
        }
    },

    addScripts: function  (basePath, filenames) {
        for (var i=0; i<filename.length; i++)
            this.addScript(basepath+filenames[i]);
    },

    addStylesheet: function (path) {
        if (this.Browser.Gecko19) {
            var link = document.createElement('link');
            link.setAttribute('rel', 'stylesheet');
            link.setAttribute('href', path);
            link.setAttribute('type', 'text/css');
            link.setAttribute('media', 'screen');
            document.getElementsByTagName('head')[0].appendChild(link);
        }
        else {
            document.write('<link rel="stylesheet" href="'+path+'" type="text/css" media="screen" />');
        }

    },

    addStylesheets: function  (basePath, filenames) {
        for (var i=0; i<filename.length; i++)
            this.addStylesheet(basepath+filenames[i]);
    },

    initPath: function () {
        /* get / set basic values */
        this.scriptElement = this.getScriptElement(this.scriptName);
        if (!this.scriptElement) {
            this.scriptName = 'contentflow_src.js';
            this.scriptElement = this.getScriptElement(this.scriptName);
        }

        this.BaseDir = this.getScriptPath(this.scriptElement, this.scriptName) ;
        if (!this.AddOnBaseDir) this.AddOnBaseDir = this.BaseDir;
        if (!this.CSSBaseDir) this.CSSBaseDir = this.BaseDir;
    },

    init: function () {
        this.loadAddOns = new Array();

        /* ========== ContentFlow auto initialization on document load ==========
         * thanks to Dean Edwards
         * http://dean.edwards.name/weblog/2005/02/order-of-events/
         */
        var CFG = this;

        /* for Mozilla, Opera 9, Safari */
        if (document.addEventListener) {
            /* for Safari */
            if (this.Browser.WebKit) {
                var _timer = setInterval(function() {
                    if (/loaded|complete/.test(document.readyState)) {
                        clearInterval(_timer);
                        CFG.onloadInit(); // call the onload handler
                    }
                }, 10);
            }
            else {
              document.addEventListener("DOMContentLoaded", CFG.onloadInit, false);
            }
        }
        else if (this.Browser.IE) {
            document.write("<script id=__ie_cf_onload defer src=javascript:void(0)><\/script>");
            var script = document.getElementById("__ie_cf_onload");
            script.onreadystatechange = function() {
                if (this.readyState == "complete") {
                    CFG.onloadInit(); // call the onload handler
                }
            };
        }

        /* for all other browsers */
        window.addEvent('load', CFG.onloadInit, false);

        /* ================================================================== */

    },

    onloadInit: function () {
        // quit if this function has already been called
        if (arguments.callee.done) return;
        for (var i=0; i< ContentFlowGlobal.loadAddOns.length; i++) {
            var a = ContentFlowGlobal.loadAddOns[i];
            if (!ContentFlowGlobal.AddOns[a]) {
                var CFG = ContentFlowGlobal;
                window.setTimeout( CFG.onloadInit, 10);
                return;
            }
        }
        // flag this function so we don't do the same thing twice
        arguments.callee.done = true;
        
        /* init all manualy created flows */
        for (var i=0; i< ContentFlowGlobal.Flows.length; i++) {
            ContentFlowGlobal.Flows[i].init(); 
        }
    }

};

ContentFlowGlobal.initPath();


/*
 * ============================================================
 * ContentFlowAddOn
 * ============================================================
 */
var ContentFlowAddOn = function (name, methods, register) {
    if (typeof register == "undefined" || register != false)
        ContentFlowGlobal.AddOns[name] = this;
        
    this.name = name;
    if (!methods) methods = {};
    this.methods = methods;
    this.conf = {};
    if (this.methods.conf) {
       this.setConfig(this.methods.conf);
       delete this.methods.conf;
    }


    this.scriptpath = ContentFlowGlobal.AddOnBaseDir;
    if (methods.init) {
        var init = methods.init.bind(this);
        init(this);
    }
};

ContentFlowAddOn.prototype = {
    Browser: ContentFlowGlobal.Browser,

    addScript: ContentFlowGlobal.addScript,
    addScripts: ContentFlowGlobal.addScripts,

    addStylesheet: function (path) {
        if (!path)
            path = this.scriptpath+'ContentFlowAddOn_'+this.name+'.css';
        ContentFlowGlobal.addStylesheet(path);
    },
    addStylesheets: ContentFlowGlobal.addStylesheets,

    setConfig: function (conf) {
        for (var c in conf) {
            this.conf[c] = conf[c];
        }
    },

    _init: function (flow) {
        if (this.methods.ContentFlowConf) {
            flow.setConfig(this.methods.ContentFlowConf);
        }
    }


};



/* 
 * ============================================================
 * ContentFlowGUIElement
 * ============================================================
 */

var ContentFlowGUIElement = function (CFobj, element) {
    element.setDimensions = function () {
        this.dimensions = this.getDimensions();
        this.center = {x: this.dimensions.width/2, y:this.dimensions.height/2};
        this.position = this.findPos();
    };
    element.addObserver = function (eventName, method) {
        var m = this.eventMethod = method.bind(CFobj);
        this.observedEvent = eventName;
        this.addEvent(eventName, m, false);
    };
    
    element.makeDraggable = function (onDrag, beforeDrag, afterDrag) {

        this.stopDrag = function(event) {
            if (!event) var event = window.event;
            if (this.Browser.iPhone)  {
                window.removeEvent('touchemove', onDrag, false);
                if (!this.ontochmove) {
                    var t = event.target;
                    if (t.firstChild) t = t.firstChild;
                    var e = document.createEvent('MouseEvents');
                    e.initEvent('click', true, true);
                    t.dispatchEvent(e);
                }
            }
            else {
                window.removeEvent('mousemove', onDrag, false);
            }
            afterDrag(event); 
        }.bind(this);

        this.initDrag = function (event) {
            if (!event) var event = window.event;
            var e = event;
            if (event.touches) e = event.touches[0];

            this.mouseX = e.clientX; 
            this.mouseY = e.clientY; 

            beforeDrag(event);

        }.bind(this);

        this.startDrag = function (event) {
            if (!event) var event = window.event;

            var stopDrag = this.stopDrag;

            if (this.Browser.iPhone)  {
                var s = this;
                s.ontouchmove = false
                window.addEvent('touchmove', function (e) {
                        s.ontouchmove = true; 
                        onDrag(e);
                }, false);
                event.preventDefault();
                window.addEvent('touchend', stopDrag, false);
            }
            else {
                window.addEvent('mousemove', onDrag, false);
                window.addEvent('mouseup', stopDrag, false);
            }
            if(event.preventDefault) { event.preventDefault() }

        }.bind(this);

        var startDrag = this.startDrag;
        if (this.Browser.iPhone)  {
            this.addEventListener('touchstart', startDrag, false);
        }
        else {
            this.addEvent('mousedown', startDrag, false); 
        }
        
    };

    element.Browser = ContentFlowGlobal.Browser;
    $CF(element).setDimensions();
    return element;
};


/* 
 * ============================================================
 * ContentFlowItem
 * ============================================================
 */
var ContentFlowItem  = function (CFobj, element, index) {
    this.CFobj = CFobj;
    this._activeElement = CFobj.conf.activeElement;
    this.pre = null;
    this.next = null;
    /*
     * ==================== item click events ====================
     * handles the click event on an active and none active item
     */

    this.clickItem = function (event) {
        if(!event) var event = window.event;
        var el = event.target ? event.target : event.srcElement;
        var index = el.itemIndex ? el.itemIndex : el.parentNode.itemIndex;
        var item = this.items[index];
        if (this._activeItem == item) {
            this.conf.onclickActiveItem(item);
        }
        else {
            if (this.conf.onclickInactiveItem(item) != false ) this.moveToIndex(index);
        }
    }.bind(CFobj),

    this.setIndex = function (index) {
        this.index = index;
        this.element.itemIndex = index;
    };
    this.getIndex = function () {
        return this.index;
    };


    /* generate deault HTML structure if item is an image */
    if ($CF(element).nodeName == "IMG") {
        var el = document.createElement('div');
        el.className = "item";

        var cont = element.parentNode.replaceChild( el, element);
        cont.className = "content";
        el.appendChild(cont);

        if (element.title) {
            var cap = document.createElement('div');
            cap.className = "caption";
            cap.innerHTML = element.title;
            el.appendChild(cap);
        }
        element = el;
    }

    /* create item object */
    this.element = $CF(element);
    this.item = element;
    if (typeof index != "undefined") this.setIndex(index);
    this.content = this.element.getChildrenByClassName('content')[0];
    this.caption = this.element.getChildrenByClassName('caption')[0];
    this.label = this.element.getChildrenByClassName('label')[0];

    /* if content is image set properties */
    if (this.content.nodeName == "IMG") {
        CFobj._imagesToLoad++;

        var foobar = function () { 
            CFobj._imagesToLoad--;
            this.image = this.content;
            this.setImageFormat(this.image);
            if ( CFobj.conf.reflectionHeight > 0) {
                this.addReflection();
            }
            this.initClick();
            CFobj._addItemCueProcess(true);
        }.bind(this);

        if (this.content.complete && this.content.width > 0)
            window.setTimeout(foobar, 100);
        else if (this.Browser.IE && !this.content.onload) {
            var self = this;
            var t = window.setInterval( function () {
                if (self.content.complete && self.content.width > 0) {
                    window.clearInterval(t);
                    foobar();
                }
            }, 10);
        }
        else
            this.content.onload = window.setTimeout(foobar, 100);
    }
    else {
        this.initClick();
        CFobj._addItemCueProcess(true);
    }

};

ContentFlowItem.prototype = {
    
    Browser: ContentFlowGlobal.Browser,

    makeActive: function () {
        this.element.addClassName('active');
        this.CFobj.conf.onMakeActive(this);
    },
    
    makeInactive: function () {
        this.element.removeClassName('active');
        this.CFobj.conf.onMakeInactive(this);
    },

    initClick: function () {
        var cItem = this.clickItem;
        this[this._activeElement].addEvent('click', cItem, false);
    },
    
    setImageFormat: function (img) {
        if (this.Browser.IE6 || this.Browser.IE7) img.style.width = "auto";
        img.origProportion =  img.width / img.height;
        img.setAttribute('origProportion', img.width / img.height);
        if (this.Browser.IE6 || this.Browser.IE7) img.style.width = "";
        //img.origWidth = img.width;
        //img.origHeight = img.height;
        if (img.origProportion <= 1)
            img.addClassName('portray');
        else
            img.addClassName('landscape');
    },

    /*
     * add reflection to item
     */
    addReflection: function() {
        var CFobj = this.CFobj;
        var reflection;
        var image = this.content;


        if (this.Browser.IE) {
            var filterString = 'progid:DXImageTransform.Microsoft.BasicImage(rotation=2, mirror=1)';
            if (CFobj._reflectionColorRGB) {
                // transparent gradient
                if (CFobj.conf.reflectionColor == "transparent") {
                    var RefImg = reflection = this.reflection = document.createElement('img');
                    reflection.src = image.src;
                }
                // color gradient
                else {
                    reflection = this.reflection = document.createElement('div');
                    var RefImg = document.createElement('img');
                    RefImg.src = image.src;
                    reflection.width = RefImg.width;
                    reflection.height = RefImg.height;
                    RefImg.style.width = '100%';
                    RefImg.style.height = '100%';
                    var color = CFobj._reflectionColorRGB;
                    reflection.style.backgroundColor = '#'+color.hR+color.hG+color.hB;
                    reflection.appendChild(RefImg);
                }
                filterString += ' progid:DXImageTransform.Microsoft.Alpha(opacity=0, finishOpacity=50, style=1, finishX=0, startY='+CFobj.conf.reflectionHeight*100+' finishY=0)';
            } else {
                var RefImg = reflection = this.reflection = document.createElement('img');
                reflection.src = image.src;
            }
            // crop image (streches and crops (clip on default dimensions), original proportions will be restored through CSS)
            filterString += ' progid:DXImageTransform.Microsoft.Matrix(M11=1, M12=0, M21=0, M22='+1/CFobj.conf.reflectionHeight+')';

            if (ContentFlowGlobal.Browser.IE6) {
                if (image.src.match(/\.png$/) ) {
                    image.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+image.src+"', sizingMethod=scale )";
                    image.filterString = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+image.src+"', sizingMethod=scale )";
                    filterString += " progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+image.src+"', sizingMethod=scale )";
                    image.origSrc = image.src;
                    image.src='img/blank.gif';
                    RefImg.src="img/blank.gif";
                }
            }

            reflection.filterString = filterString;
            RefImg.style.filter = filterString;

        } else {
            if (CFobj._reflectionWithinImage)
                var canvas = this.canvas = $CF(document.createElement('canvas'));
            else 
                var canvas = reflection = this.reflection = document.createElement('canvas');

            if (canvas.getContext) {
                if (CFobj._reflectionWithinImage) {
                    for (var i=0; i <image.attributes.length; i++) {
                        canvas.setAttributeNode(image.attributes[i].cloneNode(true));
                    }
                }

                var context = canvas.getContext("2d");

                /* calc image size */
                var max = CFobj.maxHeight;
                var size = CFobj._scaleImageSize(this, {width: max, height: max }, max)
                var width = size.width;
                var height = size.height;

                // overwrite default height and width
                if (CFobj._reflectionWithinImage) {
                    canvas.width = width;
                    canvas.height = height; 
                    this.setImageFormat(canvas);
                    canvas.height = height * (1 + CFobj.conf.reflectionHeight + CFobj.conf.reflectionGap);

                }
                else {
                    canvas.width = width;
                    canvas.height = height * CFobj.conf.reflectionHeight;
                }
                    
                context.save(); /* save default context */

                /* draw image into canvas */
                if (CFobj._reflectionWithinImage) {
                    context.drawImage(image, 0, 0, width, height);
                }

                /* mirror image by transformation of context and image drawing */
                if (CFobj._reflectionWithinImage) {
                    var contextHeight = height * ( 1 + CFobj.conf.reflectionGap/2) * 2;
                }
                else {
                    var contextHeight = image.height;
                }
                // -1 for FF 1.5
                contextHeight -= 1;
                
                context.translate(0, contextHeight);
                context.scale(1, -1);
                /* draw reflection image into canvas */
                context.drawImage(image, 0, 0, width, height);

                /* restore default context for simpler further canvas manupulation */
                context.restore();
                    
                if (CFobj._reflectionColorRGB) {
                    var gradient = context.createLinearGradient(0, 0, 0, canvas.height);

                    var alpha = [0, 0.5, 1];
                    if (CFobj._reflectionColor == "transparent") {
                        context.globalCompositeOperation = "destination-in";
                        alpha = [1, 0.5, 0];
                    }

                    var red = CFobj._reflectionColorRGB.iR;
                    var green = CFobj._reflectionColorRGB.iG;
                    var blue = CFobj._reflectionColorRGB.iB;
                    if (CFobj._reflectionWithinImage) {
                        gradient.addColorStop(0, 'rgba('+red+','+green+','+blue+','+alpha[0]+')');
                        gradient.addColorStop(height/canvas.height, 'rgba('+red+','+green+','+blue+','+alpha[0]+')');
                        gradient.addColorStop(height/canvas.height, 'rgba('+red+','+green+','+blue+','+alpha[1]+')');
                    }
                    else {
                        gradient.addColorStop(0, 'rgba('+red+','+green+','+blue+','+alpha[1]+')');
                    }
                    gradient.addColorStop(1, 'rgba('+red+','+green+','+blue+','+alpha[2]+')');

                    context.fillStyle = gradient;
                    context.fillRect(0, 0, canvas.width, canvas.height);
                    
                }

                if (CFobj._reflectionWithinImage) {
                    image.parentNode.replaceChild(canvas, image);
                    this.content = canvas;
                    this.origContent = canvas;
                    delete this.image;// = true;

                }
                
            } else {
                CFobj._reflectionWithinImage = false;
                delete this.reflection;
            }

        }
        if (reflection) {
            reflection.className = "reflection";
            this.element.appendChild(reflection);

            /* be shure that caption is last child */
            if (this.caption) this.element.appendChild(this.caption);
        } 

    }


 };

/*
 * ============================================================
 * ContentFlow
 * ============================================================
 */
var ContentFlow = function (container, config) {

    if (container) {
        ContentFlowGlobal.Flows.push(this);
        this.Container = container;
        this._userConf = config?config:{};
        this.conf = {};
        this._loadedAddOns = new Array();
        
        ContentFlowGlobal.Flows[ContentFlowGlobal.Flows.length - 1].init();
    } else {
        throw ('ContentFlow ERROR: No flow container node or id given');
    }

};

ContentFlow.prototype = {
    _imagesToLoad: 0,
    _activeItem: 0,
    _currentPosition: 0,
    _targetPosition: 0,
    _stepLock: false,
    _millisecondsPerStep: 40, 
    _reflectionWithinImage: true,
    Browser: ContentFlowGlobal.Browser,
    
    _defaultConf: { 
        /* pre conf */
        useAddOns: 'all', // all, none, [AddOn1, ... , AddOnN]

        biggestItemPos: 0,
        loadingTimeout: 30000, //milliseconds
        activeElement: 'content', // item or content

        maxItemHeight: 0,
        scaleFactor: 1,
        scaleFactorLandscape: 1.33,
        scaleFactorPortrait: 1.0,
        fixItemSize: false,
        relativeItemPosition: "top center", // align top/above, bottom/below, left, right, center of position coordinate

        circularFlow: true,
        verticalFlow: false,
        visibleItems: -1,
        endOpacity: 1,
        startItem:  "center",
        scrollInFrom: "pre",

        flowSpeedFactor: 1.0,
        flowDragFriction: 1.0,
        scrollWheelSpeed: 1.0,
        keys: {
            13: function () { this.conf.onclickActiveItem(this._activeItem) },
            37: function () { this.moveTo('pre') }, 
            38: function () { this.moveTo('visibleNext') },
            39: function () { this.moveTo('next') },
            40: function () { this.moveTo('visiblePre') }
        },

        reflectionColor: "transparent", // none, transparent or hex RGB CSS style #RRGGBB
        reflectionHeight: 0.5,          // float (relative to original image height)
        reflectionGap: 0.0,

        /* ==================== actions ==================== */

        onInit: function () {},

        onclickInactiveItem: function (item) {},

        onclickActiveItem: function (item) {
            var url, target;

            if (url = item.content.getAttribute('href')) {
                target = item.content.getAttribute('target');
            }
            else if (url = item.element.getAttribute('href')) {
                target = item.element.getAttribute('target');
            }
            else if (url = item.content.getAttribute('src')) {
                target = item.content.getAttribute('target');
            }

            if (url) {
                if (target)
                    window.open(url, target).focus();
                else
                    window.location.href = url;
            }
        },

        onMakeInactive: function (item) {},

        onMakeActive: function (item) {},

        onReachTarget: function(item) {},

        onMoveTo: function(item) {},

        //onDrawItem: function(item, relativePosition, relativePositionNormed, side, size) {},
        onDrawItem: function(item) {},
        
        onclickPreButton: function (event) {
            this.moveToIndex('pre');
            return Event.stop(event);
        },
        
        onclickNextButton: function (event) {
            this.moveToIndex('next');
            return Event.stop(event);
        },

        /* ==================== calculations ==================== */

        calcStepWidth: function(diff) {
            var vI = this.conf.visibleItems;
            var items = this.items.length;
            items = items == 0 ? 1 : items;
            if (Math.abs(diff) > vI) {
                if (diff > 0) {
                    var stepwidth = diff - vI;
                } else {
                    var stepwidth = diff + vI;
                }
            } else if (vI >= this.items.length) {
                var stepwidth = diff / items;
            } else {
                var stepwidth = diff * ( vI / items);
                //var stepwidth = diff/absDiff * Math.max(diff * diff,Math.min(absDiff,0.3)) * ( vI / this.items.length);
                //var stepwidth = this.flowSpeedFactor * diff / this.visibleItems;
                //var stepwidth = this.flowSpeedFactor * diff * ( this.visibleItems / this.items.length)
                //var stepwidth = this.flowSpeedFactor * diff / this._millisecondsPerStep * 2; // const. speed
            }
            return stepwidth;
        },
        
        calcSize: function (item) {
            var rP = item.relativePosition;
            //var rPN = relativePositionNormed;
            //var vI = this.conf.visibleItems; 

            var h = 1/(Math.abs(rP)+1);
            var w = h;
            return {width: w, height: h};
        },

        calcCoordinates: function (item) {
            var rP = item.relativePosition;
            //var rPN = item.relativePositionNormed;
            var vI = this.conf.visibleItems; 

            var f = 1 - 1/Math.exp( Math.abs(rP)*0.75);
            var x =  item.side * vI/(vI+1)* f; 
            var y = 1;

            return {x: x, y: y};
        },

        /*
        calcRelativeItemPosition: function (item) {
            var x = 0;
            var y = -1;
            return {x: x, y: y};
        },
        */

        calcZIndex: function (item) {
            return -Math.abs(item.relativePositionNormed);
        },

        calcFontSize: function (item) {
            return item.size.height;
        },
   
        calcOpacity: function (item) {
            return Math.max(1 - ((1 - this.conf.endOpacity ) * Math.sqrt(Math.abs(item.relativePositionNormed))), this.conf.endOpacity);
        }
    },

    /* ---------- end of defaultConf ---------- */

    
    /*
     * ==================== index helper methods ====================
     */

    /*
     * checks if index is within the index range of the this.items array
     * returns a value that is within this range
     */
    _checkIndex: function (index) {
        index = Math.max(index, 0);
        index = Math.min(index, this.itemsLastIndex);
        return index;
    },

    /*
     * sets the object property itemsLastIndex
     */
    _setLastIndex: function () {
        this.itemsLastIndex = this.items.length - 1;
    },

    /*
*/
    _getItemByIndex: function (index) {
        return this.items[this._checkIndex(index)];
    },

    _getItemByPosition: function (position) {
        return this._getItemByIndex(this._getIndexByPosition(position));
    },

    /* returns the position of an item-index relative to current position */
    _getPositionByIndex: function(index) {
        if (!this.conf.circularFlow) return this._checkIndex(index);
        var cI = this._getIndexByPosition(this._currentPosition);
        var dI = index - cI;
        if (Math.abs(dI) > dI+this.items.length)
            dI += this.items.length;
        else if (Math.abs(dI) > (Math.abs(dI-this.items.length)))
            dI -= this.items.length;

        return this._currentPosition + dI;

    },

    /* returns the index an item at position p would have */
    _getIndexByPosition: function (position) {
        if (position < 0) var mod = 0;
        else var mod = 1;

        var I = (Math.round(position) + mod) % this.items.length;
        if (I>0) I -= mod;
        else if(I<0) I += this.items.length - mod;
        else if(position<0) I = 0;
        else I = this.items.length - 1;

        return I;
    },

    _getIndexByKeyWord: function (keyword, relativeTo, check) {
        if (relativeTo)
            var index = relativeTo;
        else if (this._activeItem)
            var index = this._activeItem.index;
        else
            var index = 0;

        if (isNaN(keyword)) {
            switch (keyword) {
                case "first":
                case "start":
                    index = 0;
                    break;
                case "last":
                case "end":
                    index = this.itemsLastIndex;
                    break;
                case "middle":
                case "center":
                    index = Math.round(this.itemsLastIndex/2);
                    break;
                case "right":
                case "next":
                    index += 1;
                    break;
                case "left":
                case "pre":
                case "previous":
                    index -= 1;
                    break;
                case 'visible':
                case 'visiblePre':
                case 'visibleLeft':
                    index -= this.conf.visibleItems;
                    break;
                case 'visibleNext':
                case 'visibleRight':
                    index += this.conf.visibleItems;
                    break;
                default:
                    index = index;
            }
        }
        else {
            index = keyword;
        }
        if (check != false)
            index = this._checkIndex(index);
        
        return index;
    },


    _setCaptionLabel: function (index) {
        if(this.Position && !this.Slider.locked)
            this.Position.setLabel(index);
        this._setGlobalCaption();
    },


    /*
     * ==================== public methods ==================== 
     */
    getAddOnConf: function(name) {
          return ContentFlowGlobal.getAddOnConf(name);
    },

    setAddOnConf: function(name, conf) {
          ContentFlowGlobal.setAddOnConf(name, conf);
    },


    /*
     * calls _init() if ContentFlow has not been initialized before
     * needed if ContentFlow is not automatically initialized on window.load
     */
    init: function () {
        if(this.isInit) return;
        this._init();
    },

    /*
     * parses configuration object and initializes configuration values
     */
    setConfig: function(config) {
        if (!config) return;
        var dC = this._defaultConf;
        for (var option in config) {
            if (dC[option] == "undefined" ) continue;
            switch (option) {
                case "scrollInFrom":
                case "startItem":
                    if (typeof(config[option]) == "number"  || typeof(config[option]) == "string") {
                        //this["_"+option] = config[option];
                        this.conf[option] = config[option];
                    }
                    break;
                default:
                    if (typeof(dC[option] == config[option])) {
                        //this["_"+option] = config[option];
                        if (typeof config[option] == "function") {
                            this.conf[option] = config[option].bind(this);
                        }
                        else {
                            this.conf[option] = config[option];
                        }
                    }
            }
        }
        switch (this.conf.reflectionColor) {
            case this.conf.reflectionColor.search(/#[0-9a-fA-F]{6}/)>= 0?this.conf.reflectionColor:this.conf.reflectionColor+"x":
                this._reflectionColorRGB = {
                    hR: this.conf.reflectionColor.slice(1,3),
                    hG: this.conf.reflectionColor.slice(3,5),
                    hB: this.conf.reflectionColor.slice(5,7),
                    iR: parseInt(this.conf.reflectionColor.slice(1,3), 16),
                    iG: parseInt(this.conf.reflectionColor.slice(3,5), 16),
                    iB: parseInt(this.conf.reflectionColor.slice(5,7), 16)
                };
                break;
            case "none":
            case "transparent":
            default:
                this._reflectionColor = "transparent"; 
                this._reflectionColorRGB = {
                    hR: 0, hG: 0, hB:0,
                    iR: 0, iG: 0, iB:0
                };
                break;
        }
        if (this.items) {
            if (this.conf.visibleItems <  0)
                this.conf.visibleItems = Math.round(Math.sqrt(this.items.length));
            this.conf.visibleItems = Math.min(this.conf.visibleItems, this.items.length - 1);
        }

        if (this.conf.relativeItemPosition) {
            var calcRP = {
                x: {
                    left: function(size) { return -1 },
                    center: function(size) { return 0 },
                    right: function(size) { return 1 }
                },
                y: {
                    top: function(size) { return -1 },
                    center: function(size) { return 0 },
                    bottom: function(size) { return 1 }
                }
            };

            var iP = this.conf.relativeItemPosition;
            iP = iP.replace(/above/,"top").replace(/below/,"bottom");
            var x, y = null;
            x = iP.match(/left|right/);
            y = iP.match(/top|bottom/);
            c = iP.match(/center/);
            if (!x) {
                if (c) x = "center";
                else x = "center";
            }
            if (!y) {
                if (c) y = "center";
                else y = "top";
            }
            var calcX = calcRP.x[x];
            var calcY = calcRP.y[y];
            this.conf.calcRelativeItemPosition = function (item) {
                var x = calcX(item.size);
                var y = calcY(item.size);
                return {x: x, y: y};
            };
            this.conf.relativeItemPosition = null;
        }

        if (this._reflectionType && this._reflectionType != "clientside") {
            this.conf.reflectionHeight = 0;
        }

    },

    getItem: function (index) {
        return this.items[this._checkIndex(Math.round(index))]; 
    },

    /*
     * returns the index number of the active item
     */
    getActiveItem: function() {
        return this._activeItem;
    },

    /*
     * returns the number of items the flow contains
     */
    getNumberOfItems: function () {
        return this.items.length;
    },

    /*
     * reinitializes sizes.
     * called on window.resize
     */
    resize: function () {
        this._initSizes();
        this._initStep();
    }, 

    /*
     * scrolls flow to item i
     */
    moveToPosition: function (p, holdPos) {
        if (!this.conf.circularFlow) p = this._checkIndex(p);
        this._targetPosition = p;
        this.conf.onMoveTo(this._getItemByPosition(p));
        this._initStep(false, holdPos);
    },
    moveToIndex: function (index) {
        this._targetPosition = Math.round(this._getPositionByIndex(this._getIndexByKeyWord(index, this._activeItem.index, !this.conf.circularFlow)));
        this.conf.onMoveTo(this._getItemByPosition(this._targetPosition));
        this._initStep();
    },
    moveToItem: function (item) {
        var i;
        if (item.itemIndex) i = item.itemIndex;
        else i = item.index;
        this.moveToIndex(i);
    },
    moveTo: function (i) {
        if (typeof i == "object") this.moveToItem(i);
        else if (isNaN(i) || (i == Math.floor(i) && i < this.items.length) ) this.moveToIndex(i);
        else this.moveToPosition(i);
    }, 

    /*
     * initializes item and adds it at index position
     */
    _addItemCue: [],
    _addItemCueProcess: function (deleteFirst) {
        var c = this._addItemCue;
        if (deleteFirst == true) 
            c.shift();
        if (c.length > 0 && ! c[0].p) {
            c[0].p = true;
            var self = this;
            var t = c.length > 5 ? 1 : 40;
            window.setTimeout(function () { self._addItem(c[0].el, c[0].i)}, t) ;
        }
    },
    addItem: function(el, index) {
        this._addItemCue.push({ el: el, i: index, p: false});
        if (this._addItemCue.length == 1) 
            this._addItemCueProcess();
    },

    _addItem: function(el, index) {
        if (typeof index == "string") {
            switch (index) {
                case "first":
                case "start":
                    index = 0;
                    break;
                case "last":
                case "end":
                    index = isNaN(this.itemsLastIndex) ? 0 : this.itemsLastIndex;
                    index += 1;
                    break;
                default:
                    index = this._getIndexByKeyWord(index);
            }
        }

        index = Math.max(index, 0);
        index = Math.min(index, this.itemsLastIndex + 1);
        index = isNaN(index) ? 0 : index;
        
        this.Flow.appendChild(el);

        /* init item after insertion. that way it's part of the document and all styles are applied */
        var item = new ContentFlowItem(this, el, index);
        if (this.items.length == 0 ) {
            this.resize();
            if (this.conf.circularFlow) {
                item.pre = item;
                item.next = item;
            }
        }
        else {
            if (index == this.itemsLastIndex + 1) {
                item.pre = this.items[this.itemsLastIndex];
                item.next = item.pre.next;
            }
            else {
                item.next = this.items[index];
                item.pre = item.next.pre;
            }
            if (item.pre) item.pre.next = item;
            if (item.next) item.next.pre = item;
        }
        this.items.splice(index,0, item);

        /* adjust item indices */
        for (var i = index; i < this.items.length; i++) {
            this.items[i].setIndex(i);
        }
        this._setLastIndex();

        if (this.conf.origVisibleItems < 0) {
            this.conf.visibleItems = Math.round(Math.sqrt(this.items.length));
        }
        this.conf.visibleItems = Math.min(this.conf.visibleItems, this.items.length - 1);

        /* adjust targetItem, currentPos so that current view does not change*/
        if (Math.round(this._getPositionByIndex(index)) <= Math.round(this._targetPosition)) {
            this._targetPosition++;
            if (!this.conf.circularFlow)
                this._targetPosition = Math.min(this._targetPosition, this.itemsLastIndex);
        } 
        if (this._getPositionByIndex(index) <= this._currentPosition) {
            this._currentPosition++;
            if (!this.conf.circularFlow)
                this._currentPosition = Math.min(this._currentPosition, this.itemsLastIndex);
        }
        
        // avoid display errors (wrong sizing)
        var CF = this;
        window.setTimeout(function () {
            if(CF.items.length == 1 ) {
                CF._currentPosition = -0.01;
                CF._targetPosition = 0;
                CF.resize(); 
            }
            else {
                CF._initStep();
            }
        }, 100);

        return index;
        
    },
        
    /*
     * removes item at index position, cleans it up and returns it
     */
    rmItem: function(index) {
        if  (index == "undefined") index = this._activeItem.index;
        index = this._getIndexByKeyWord(index);
        if (!this.items[index]) return null;

        var item = this.items[index];

        if (item.pre) item.pre.next = item.next;
        if (item.next) item.next.pre = item.pre;
        this.items.splice(index,1);

        /* adjust item indices */
        for (var i = index ; i < this.items.length; i++) {
            this.items[i].setIndex(i);
        }
        this._setLastIndex();
        
        /* adjust targetItem, currentPos and activeItem so that current view does not change*/
        if (Math.round(this._getPositionByIndex(index)) < Math.round(this._targetPosition)) {
            this._targetPosition--;
            if (!this.conf.circularFlow)
                this._targetPosition = this._checkIndex(this._targetPosition);
        }
        if (this._getPositionByIndex(index) < this._currentPosition) {
            this._currentPosition--;
            if (!this.conf.circularFlow)
                this._currentPosition = this._checkIndex(this._currentPosition);
        }
        this._activeItem = this._getItemByPosition(this._currentPosition);

        /* remove item from DOM tree, take the next step and return removed item  */
        var removedItem = item.element.parentNode.removeChild(item.element);
        // avoid display errors (wrong sizing)
        var CF = this;
        window.setTimeout(function () { CF._initStep() }, 10);
        return removedItem;

    },


    /*
     * ==================== initialization ====================
     */
    

    /* -------------------- main init -------------------- */
    _init: function () {

        if (typeof(this.Container) == 'string') { // no node
            var container = document.getElementById(this.Container);
            if (container) {
                this.Container = container;
            } else {
                throw ('ContentFlow ERROR: No element with id \''+this.Container+'\' found!');
                return;
            }
        }
        
        /* ----------  reserve CSS namespace */

        //$CF(this.Container).addClassName('ContentFlow');

        /* ---------- detect GUI elements */
        var flow = $CF(this.Container).getChildrenByClassName('flow')[0];
        if (!flow) {
            throw ('ContentFlow ERROR: No element with class\'flow\' found!');
            return;
        }
        this.Flow = new ContentFlowGUIElement(this, flow);

        var scrollbar = this.Container.getChildrenByClassName('scrollbar')[0];
        if (scrollbar) {
            this.Scrollbar = new ContentFlowGUIElement(this, scrollbar);
            var slider = this.Scrollbar.getChildrenByClassName('slider')[0];
            if (slider) {
                this.Slider = new ContentFlowGUIElement(this, slider);
                var position = this.Slider.getChildrenByClassName('position')[0];
                if (position) {
                    this.Position = new ContentFlowGUIElement(this, position);
                }
            }

        }

        /* ----------  init configuration */ 
        this.setConfig(this._defaultConf);
        this._initAddOns(); /* init AddOns */
        this.setConfig(this._userConf);
        
        this._initSizes(); // ......


        /* ---------- init item lists ---------- */
        var items = this.Flow.getChildrenByClassName('item');

        this.items = new Array();
        for (var i=0; i<items.length; i++) {
            var item = this.items[i] = new ContentFlowItem(this, items[i], i);
            if (i > 0) { 
                item.pre = this.items[i-1];
                item.pre.next = item;
            }
        }
        this._setLastIndex();
        if (this.conf.circularFlow && this.items.length > 0) {
            var s = this.items[0];
            s.pre = this.items[this.items.length-1];
            s.pre.next = s;
        }

        /* ----------  init GUI */
        this._initGUI();

        /* ---------- init start parameters ---------- */
        if (this._activeElement != "content")
            this._activeElement = "element";

        this.conf.origVisibleItems = this.conf.visibleItems;
        if (this.conf.visibleItems < 0) {
            this.conf.visibleItems = Math.round(Math.sqrt(this.items.length));
        }
        this.conf.visibleItems = Math.min(this.conf.visibleItems, this.items.length - 1);

        this._targetPosition = this._getIndexByKeyWord(this.conf.startItem, 0);

        var index = this._getIndexByKeyWord(this.conf.scrollInFrom, this._targetPosition);
        switch (this.conf.scrollInFrom) {
            case "next":
            case "right":
                index -= 0.5;
                break;
            case "pre":
            case "previous":
            case "left":
                index += 0.5;
                break;
        } 
        this._currentPosition = index;
        

        /* ---------- wait till all images are loaded or 
         * grace time is up to show all and take the first step  
        */
        var now = new Date();
        var cf = this;
        var timer = window.setInterval (
            function() {
                if ( cf._imagesToLoad == 0 || new Date() - now > cf._loadingTimeout ) {
                    clearInterval(timer);

                    cf._activeItem = cf.getItem(cf._currentPosition);
                    if (cf._activeItem) {
                        cf._activeItem.makeActive();
                        cf._setCaptionLabel(cf._activeItem.index);
                    }
                    
                    cf.Flow.style.visibility = "visible"; // show flow after images are loaded
                    if (cf.loadIndicator) cf.loadIndicator.style.display = "none";
                    if (cf.Scrollbar) cf.Scrollbar.style.visibility = "visible";

                    cf.resize();
                    for (var i=0; i < cf._loadedAddOns.length; i++) {
                        var a = ContentFlowGlobal.AddOns[cf._loadedAddOns[i]];
                        if (a.methods.afterContentFlowInit)
                            a.methods.afterContentFlowInit(cf);
                    }
                    cf.conf.onInit();
                }
            }, 10
        );
        
        this.isInit = true;

    },
    
    /* ---------- init AddOns ---------- */ 
    _initAddOns: function () {

        // get an array of names of all AddOns that should be used
        var loadAddOns = [];
        if (this._userConf.useAddOns) {
            if (typeof this._userConf.useAddOns == "string") {
                loadAddOns = this._userConf.useAddOns.split(" ");
            }
            else if (typeof this._userConf.useAddOns == "array") {
                loadAddOns = this._userConf.useAddOns;
            }
        }
        else if (this.Container.getAttribute("useAddOns")) {
            loadAddOns = this.Container.getAttribute("useAddOns").split(" ");
        }
        else {
            loadAddOns = this.conf.useAddOns.split(' ');
        }


        // check the names for keywords
        for (var i=0; i<loadAddOns.length; i++) {
            if (loadAddOns[i] == "none") {
                loadAddOns = new Array();
                break;
            }
            else if (loadAddOns[i] == "all") {
                loadAddOns = new Array();
                for (var AddOn in ContentFlowGlobal.AddOns)
                    loadAddOns.push(AddOn);
                break;
            }
        }

        // init all AddOns that should be used and exist
        for (var i=0; i<loadAddOns.length; i++) {
            var AddOn = ContentFlowGlobal.AddOns[loadAddOns[i]];
            if (AddOn) {
                this._loadedAddOns.push(loadAddOns[i]);
                AddOn._init(this);
                this.Container.addClassName('ContentFlowAddOn_'+AddOn.name);
                if (AddOn.methods.onloadInit)
                    AddOn.methods.onloadInit(this);
            }
        }

    },


    _initGUI: function () {
        
        // resize
        //if (!this.Browser.iPhone) {
            var resize = this.resize.bind(this);
            window.addEvent('resize', resize, false);
        //}
        //else {
            //var g = this;
            //window.addEvent('resize', function () {
                //g._initSizes();
                //g._initStep();
            //} , false);
        //}
        
        // pre and next buttons
        var divs = this.Container.getElementsByTagName('div');
        for (var i = 0; i < divs.length; i++) {
            if ($CF(divs[i]).hasClassName('preButton')) {
                var pre = divs[i];
                var mt = this.conf.onclickPreButton;
                pre.addEvent('click', mt, false);
            }
            else if (divs[i].hasClassName('nextButton')) {
                var next = divs[i];
                var mt = this.conf.onclickNextButton;
                next.addEvent('click', mt, false);
            }
        }

        // Container object
        // mousewheel
        if (this.conf.scrollWheelSpeed != 0) {
            var wheel = this._wheel.bind(this);
            if(window.addEventListener) this.Container.addEventListener('DOMMouseScroll', wheel, false);
            this.Container.onmousewheel = wheel;
        }

        // key strokes
        var key = this._keyStroke.bind(this);
        if (this.conf.keys && !this.Browser.iPhone) {
            if (document.addEventListener) {
                if (!this.Browser.Opera) {
                    var mouseoverCheck = document.createElement('div');
                    mouseoverCheck.addClassName('mouseoverCheckElement');
                    this.Container.appendChild(mouseoverCheck);

                    if (this.Browser.WebKit) {
                        document.body.addEvent('keydown',  function (event) {
                            if (mouseoverCheck.offsetLeft > 0) key(event) ;
                        });
                    } else {
                        window.addEvent('keydown',  function (event) {
                            if (mouseoverCheck.offsetLeft > 0) key(event) ;
                        });
                    }
                } 
                else {
                    this.Container.addEvent('keydown', key);
                }
            }
            else {
               this.Container.onkeydown = key;
            }
        }


        // Flow object
        if (this.conf.flowDragFriction > 0) {
            var onDrag = function (event) {
                var e = event;
                if (event.touches) e = event.touches[0];
                var mouseX = e.clientX;
                var mouseY = e.clientY;
                
                if (this.conf.verticalFlow) {
                    var dist = mouseY - this.Flow.mouseY; // px / or px per sec because _dragFlow wil be called in shorter intervalls if draged fast
                    var dim = this.Flow.dimensions.height;
                }
                else {
                    var dist = mouseX - this.Flow.mouseX; // px / or px per sec because _dragFlow wil be called in shorter intervalls if draged fast
                    var dim = this.Flow.dimensions.width;
                }
                var itemDist = (dist / dim )* (2*this.conf.visibleItems +1); // items
                var target = this._currentPosition - itemDist * 2*this.conf.visibleItems / this.conf.flowDragFriction ;

                this.Flow.mouseX = mouseX; 
                this.Flow.mouseY = mouseY; 

                this.moveToPosition(target, true);
            }.bind(this);

            var beforeDrag = function () {};

            var afterDrag = function (event) {
                var t = Math.round(this._targetPosition);
                if (Math.abs( t - this._currentPosition) > 0.001 )
                    this.moveToPosition(t);
            }.bind(this);


            this.Flow.makeDraggable(onDrag, beforeDrag, afterDrag);
        }

        // Scrollbar Object
        if (this.Scrollbar) {
            var click = function(event) {
                if (!event) var event = window.event;

                if (!this.Scrollbar.clickLocked) {
                    var mouseX = event.clientX; 
                    var positionOnScrollbar = mouseX - this.Scrollbar.position.left;
                    var targetIndex = Math.round(positionOnScrollbar/this.Scrollbar.dimensions.width * this.itemsLastIndex);
                    this.moveToIndex(targetIndex);
                }
                else
                    this.Scrollbar.clickLocked = false;
            }.bind(this);
            this.Scrollbar.addObserver('click', click);
        }

        // Slider Object
        if (this.Slider) {

            if (this.Browser.IE6) {
                var virtualSlider = document.createElement('div');
                virtualSlider.className = 'virtualSlider';
                this.Slider.appendChild(virtualSlider);
            }

            // position slider on scrollbar
            this.Slider.setPosition = function (relPos) {
                relPos = relPos - Math.floor(relPos) + this._getIndexByPosition(Math.floor(relPos));
                if (Math.round(relPos) < 0)
                    relPos = this.itemsLastIndex;
                else if (relPos <= 0)
                    relPos = 0;
                else if (Math.round(relPos) > this.itemsLastIndex)
                    relPos = 0;
                else if (relPos >= this.itemsLastIndex)
                    relPos = this.itemsLastIndex;


                if (this.items.length > 1) {
                    var sPos = (relPos / this.itemsLastIndex)* this.Scrollbar.dimensions.width;
                } else {
                    var sPos = 0.5 * this.Scrollbar.dimensions.width;
                }
                this.Slider.style.left = sPos - this.Slider.center.x+ "px";
                this.Slider.style.top = this.Scrollbar.center.y - this.Slider.center.y +"px";

            }.bind(this);

            // make slider draggable
            var beforeDrag = function (event) {
                this.Scrollbar.clickLocked = true;
            }.bind(this);

            var onDrag = function (event) {
                var e = event;
                if (event.touches) e = event.touches[0];
                var selectedIndex = this._checkIndex((e.clientX - this.Scrollbar.position.left) / this.Scrollbar.dimensions.width * this.itemsLastIndex);
                this._targetPosition = this._getPositionByIndex(selectedIndex);
                this.Slider.setPosition(selectedIndex);
                if (this.Position) this.Position.setLabel(selectedIndex);
                this._initStep(true, true);
            }.bind(this);

            var afterDrag = function (event) {
                this._targetPosition = Math.round(this._targetPosition);
                this.conf.onMoveTo(this._getItemByPosition(this._targetPosition));
                this._initStep(true);
            }.bind(this);

            this.Slider.makeDraggable(onDrag, beforeDrag, afterDrag);
        }

                
        // Position object
        if (this.Position) {
            this.Position.setLabel = function (index) {
                index = this._checkIndex(Math.round(index));
                if (this.items && this.items[index].label)
                    this.Position.innerHTML = this.items[index].label.innerHTML;
                else
                    this.Position.innerHTML = index + 1;
            }.bind(this);
        }


        this.globalCaption = this.Container.getChildrenByClassName('globalCaption')[0];
        this.loadIndicator = this.Container.getChildrenByClassName('loadIndicator')[0];
    },

    /* ---------- init element sizes ---------- */ 
    _initSizes: function (x) {
        //if (this.Browser.Konqueror4 && x != true) {
            //var t = this;
            //window.setTimeout( function () { t._initSizes(true) }, 0);
            //return;
        //}

        // sets this.maxHeight
        this._initMaxHeight();

        var scrollbarHeight = this._initScrollbarSize();

        // reduce maxHeit if container has a fixed height
        if (!this.conf.verticalFlow && this.Container.style.height && this.Container.style.height != "auto")
            this.maxHeight -= scrollbarHeight; 

        if (!this._activeItem) return;

        var mFS = this._findBiggestItem();

        var pF = this.Flow.findPos();

        /* set height / width of flow */
        if (this.conf.verticalFlow) {
            this.Flow.style.width = mFS.width.width+"px";
            this.Flow.style.height =3* mFS.width.width * (1 + this.conf.reflectionHeight + this.conf.reflectionGap) + "px";
        } else {
            this.Flow.style.height = mFS.height.height +(mFS.height.top - pF.top)+"px";
        }

        /* remove gap */
        var s = this.conf.verticalFlow ? mFS.width.width : mFS.height.height;
        var cH = s /(1 + this.conf.reflectionHeight + this.conf.reflectionGap);
        this.Flow.style.marginBottom = - (s - cH)+ "px";

        this.Flow.dimensions = this.Flow.getDimensions();

        if (!this.Browser.IE6) {
            if (this.conf.verticalFlow && this.Container.clientWidth < this.Flow.dimensions.width) {
                //this.Container.style.width = this.Flow.dimensions.width+"px";
            }
            else if (this.Container.clientHeight < this.Flow.dimensions.height) {
                this.Container.style.height = this.Flow.dimensions.height+"px";
            }
        }

        if (this.conf.verticalFlow) {
           this.Flow.center = {x: this.Flow.dimensions.height/2, y:mFS.width.width/2};
        } else {
           this.Flow.center = {x: this.Flow.dimensions.width/2, y:mFS.height.height/2};
        }

    },

    /* -------------------------------------------------------------------------------- */

    _initScrollbarSize: function () {
        var SB;
        var SL;
        var PO;
        if (SB = this.Scrollbar) {
            SB.setDimensions();
            var scrollbarHeight = SB.dimensions.height;

            if (SL = this.Slider) {
                SL.setDimensions();
                scrollbarHeight += SL.dimensions.height;

                if (PO = this.Position) {
                    
                    var oldLabel = PO.innerHTML;
                    var maxH = maxW = 0;
                    PO.style.width = "auto";

                    if (this.items) {
                        for (var i=0; i < this.items.length; i++) {
                            var item = this.items[i];
                            if (item.label) {
                                PO.innerHTML = item.label.innerHTML;
                            }
                            else {
                                PO.innerHTML = item.index;
                            }
                            var h = PO.clientHeight;
                            var w = PO.clientWidth;
                            if ( h >  maxH) maxH = h;
                            if ( w >  maxW) maxW = w;
                        }
                    }
                    else {
                        PO.innerHTML = "&nbsp;";
                        maxH = PO.clientHeight;
                        maxW = PO.clientWidth;
                    }

                    PO.innerHTML = oldLabel;

                    PO.setDimensions();

                    PO.style.width = maxW +"px";
                    PO.style.left = (SL.dimensions.width - maxW)/2 + "px";

                    var extraSpace = PO.position.top - SL.position.top;
                    if (extraSpace > 0) {
                        extraSpace += -SB.dimensions.height + maxH;
                        SB.style.marginBottom = extraSpace + "px";
                    }
                    else {
                        extraSpace *= -1;
                        SB.style.marginTop = extraSpace + "px";
                    }
                    scrollbarHeight += extraSpace;
                }
            }
        }
        else {
            scrollbarHeight = 0;
        }

        return scrollbarHeight;

    },

    /* -------------------------------------------------------------------------------- */

    _initMaxHeight: function () {

        if (this.conf.verticalFlow) {
            var proportion = screen.width/screen.height;
            var Csd = this.Container.style.width;
            var Cdim = this.Container.clientWidth;
            var Fsd = this.Flow.style.width;
            var Fdim = this.Flow.clientWidth;
            var Fdim_o = this.Flow.clientHeight;
        } else {
            var proportion = screen.height/screen.width;
            var Csd = this.Container.style.height;
            var Cdim = this.Container.clientHeight;
            var Fsd = this.Flow.style.height;
            var Fdim = this.Flow.clientHeight;
            var Fdim_o = this.Flow.clientWidth;
        }

        // set height of container and flow
        if (this.ContainerOldDim) 
            Csd = this.ContainerOldDim;
        if (this.FlowOldDim) 
            Fsd = this.FlowOldDim;

        this.ContainerOldDim = "auto";
        this.FlowOldDim = "auto";
        

        /* calc maxHeight */
        if (this.conf.maxItemHeight <= 0) {

            this.maxHeight = Fdim_o / 3 * proportion/1 * this.conf.scaleFactor;  // divided by 3 because of left/center/right, yes it's a magic number

            if (this.conf.verticalFlow && (this.maxHeight == 0 || this.maxHeight > Fdim)) {
                this.maxHeight = Fdim;
            }

            if (Csd && Csd != "auto") {
                var gap = this.conf.verticalFlow  ? 0 : this.conf.reflectionGap;
                var rH = this.conf.verticalFlow  ? 0 : this.conf.reflectionHeight;
                this.maxHeight = Cdim/ (this.conf.scaleFactor* (1 + rH + gap)); 
                this.ContainerOldDim = Csd;
            }
            else if (Fsd && Fsd != "auto") {
                var gap = this.conf.verticalFlow  ? 0 : this.conf.reflectionGap;
                this.maxHeight = Fdim / (this.conf.scaleFactor* (1 + this.conf.reflectionHeight + gap));
                this.FlowOldDim = Fsd;
            }
        }
        else {
            this.maxHeight = this.conf.maxItemHeight;
        }
    },

    /* -------------------------------------------------------------------------------- */

    _findBiggestItem: function () {

        var currentItem = this._activeItem;

        var itemP = currentItem.pre;
        var itemN = currentItem.next;
        var mFS = maxFlowSize = {
            width: { width: 0, left: 0, height:0, top: 0, item: null, rI: 0 },
            height: { width: 0, left: 0, height:0, top: 0, item: null, rI: 0 }
        }


        var checkMax = function (item, rI) {
            var el = item.element;
            el.style.display = "block";
            var p = el.findPos();
            var h =  el.clientHeight;
            var w = el.clientWidth;
            if (h + p.top >= mFS.height.height + mFS.height.top) {
                mFS.height.height = h;
                mFS.height.top = p.top;
                mFS.height.item = item;
                mFS.height.rI = rI;
            }
            if (w + p.left >= mFS.width.width + mFS.width.left) {
                mFS.width.width = w;
                mFS.width.left = p.left;
                mFS.width.item = item;
                mFS.width.rI = rI;
            }
            el.style.display = "none";
        }

        var ocp = this._currentPosition;
        this._currentPosition = this.conf.visibleItems+1;

        // find the position with highest y-value
        for (var i=-this.conf.visibleItems; i <= this.conf.visibleItems; i++) {
            currentItem.element.style.display = "none";
            this._positionItem(currentItem, i);
            checkMax(currentItem, i);
        }

        // find the biggest item
        var index = mFS.height.rI;
        for (var i=0; i < this.items.length; i++) {
            var item = this.items[i];
            item.element.style.display = "none";
            this._positionItem(item, index);
            checkMax(item, index);
        }

        this._currentPosition = ocp;

        return mFS
    },



    /*
     * ==================== Key strok ====================
     */

    /*
     * handles keystroke events
     */
    _keyStroke: function(event) {
        if(!event) var event = window.event;

        if (event.which) {
            var keyCode = event.which;
        } else if (event.keyCode) {
            var keyCode = event.keyCode;
        }

        if (this.conf.keys[keyCode]) {
            this.conf.keys[keyCode].bind(this)();
            return Event.stop(event);
        }
        else {
            return true;
        }
    },
    
    /*
     * ==================== mouse wheel ====================
     * Event handler for mouse wheel event
     * http://adomas.org/javascript-mouse-wheel/
     */

    _wheel: function (event) {
        if (!event) var event = window.event; // MS
        
        var delta = 0;
        if (event.wheelDelta) {
            delta = event.wheelDelta/120; 
        } else if (event.detail) {
            delta = -event.detail/3;
        }

        if (delta) {
            var target = this._targetPosition ;
            if (delta < 0 ) {
                target += (1 * this.conf.scrollWheelSpeed);
            } else {
                target -= (1 * this.conf.scrollWheelSpeed);
            } 
            this.moveToPosition(Math.round(target));
        }

        return Event.stop(event);
    },


    /*
     * ==================== set global Caption ====================
     */
    _setGlobalCaption: function () {
        if (this.globalCaption) {
            this.globalCaption.innerHTML = '';
            if(this._activeItem && this._activeItem.caption)
                this.globalCaption.appendChild(this._activeItem.caption.cloneNode(true));
        }
    },

    /*
     * ==================== move items ====================
     */

    /*
     * intend to make a step 
     */
    _initStep: function (holdSlider, holdPos) {
        if (this.Slider) {
            if(holdSlider) {
                this.Slider.locked = true;
            } else {
                this.Slider.locked = false;
            }
        }
        this._holdPos = holdPos == true ? true : false;
        if (!this._stepLock) {
            this._stepLock = true;
            this._step();
        }
    },

    /*
     * make a step
     */
    _step: function () {

        var diff = this._targetPosition - this._currentPosition; 
        var absDiff = Math.abs(diff);
        if ( absDiff > 0.001) { // till activeItem is nearly at position 0

            this._currentPosition += this.conf.flowSpeedFactor * this.conf.calcStepWidth(diff, absDiff, this.items.length, this.conf.visibleItems);

            var AI = this.items[(this._getIndexByPosition(this._currentPosition))];

            if (AI && AI != this._activeItem) {
                if (this._activeItem) this._activeItem.makeInactive();
                this._activeItem = AI;
                this._activeItem.makeActive();
                this._setCaptionLabel(this._activeItem.index);
                if (Math.abs(this._targetPosition - this._currentPosition) <= 0.5 ) this.conf.onReachTarget(this._activeItem);
            }
            
            this._positionItems();

            var st = this._step.bind(this);
            window.setTimeout(st,this._millisecondsPerStep);

        } else if (!this._holdPos) {
            if (this.Slider) this.Slider.locked = false;
            this._currentPosition = Math.round(this._currentPosition);
            if(this.Position && !this.Slider.locked && this._activeItem) {
                this._setCaptionLabel(this._activeItem.index);
            }
            this._positionItems();
            this._stepLock = false;
        } else {
            this._stepLock = false;
        }

        if (this.Slider && !this.Slider.locked) {
            this.Slider.setPosition(this._currentPosition);
        }
    },
    


/* ------------------------------------------------------------------------------------------------------ */
    
    /*
     * position items
     */
    _positionItems: function () {

        if (this._lastStart) {
            var item = this._lastStart;
            while (item) {
                item.element.style.display="none";
                item = item.next;
                if (item == this._lastStart) break;
                if (item && item.pre == this._lastEnd) break;
            }
        }
        else {
            this._lastStart = this._activeItem;
        }

        if (!this._activeItem) return;
        var currentItem = this._activeItem;
        var itemP = currentItem.pre;
        var itemN = currentItem.next;

        this._positionItem(currentItem, 0);
        for (var i=1; i <= this.conf.visibleItems; i++) {
            if (itemP) {
                this._positionItem(itemP, -i);
                this._lastStart = itemP;
                itemP = itemP.pre;
            }
            if (itemN) {
                this._positionItem(itemN, i);
                this._lastEnd = itemN;
                itemN = itemN.next;
            }
        }

    },

    _positionItem: function (item, relativeIndex) {

        var conf = this.conf;
        var vF = conf.verticalFlow;

        var els = item.element.style;
        //els.display =" none";
        //if (els.display != "none") return;

        /* Index and position relative to activeItem */
        var p = item.position = this._currentPosition + relativeIndex;
        var relativePosition = item.relativePosition = Math.round(p) - this._currentPosition;
        var relativePositionNormed = item.relativePositionNormed = conf.visibleItems > 0 ? relativePosition/conf.visibleItems : 0;
        var side = relativePosition < 0 ? -1 : 1;
        side *= relativePosition == 0 ? 0 : 1;
        item.side = side;

        var size = conf.calcSize(item);
        size.height = Math.max(size.height, 0);
        size.width = Math.max(size.width, 0);
        if (item.content.origProportion) size = this._scaleImageSize(item, size);
        item.size = size;

        var coords = item.coordinates = conf.calcCoordinates (item);
        var relItemPos = item.relativeItemPosition = conf.calcRelativeItemPosition(item);
        var zIndex = item.zIndex = conf.calcZIndex (item);
        var fontSize = item.fontSize = conf.calcFontSize (item);
        var opacity = item.opacity = conf.calcOpacity(item);

        size.height *= this.maxHeight;
        size.width *= this.maxHeight;

        /* set position */
        var sA = vF ? size.height : size.width;
        var sB = vF ? size.width : size.height;
        var pX = this.Flow.center.x * ( 1 + coords.x )  + (relItemPos.x - 1)  * sA/2;
        var pY = this.maxHeight/2 * ( 1 + coords.y ) + (relItemPos.y - 1 )* sB/2;
        els.left = (vF ? pY : pX)+"px";
        els.top = (vF ? pX : pY)+"px";
        
        this._setItemSize(item, size);

        /* set opacity */
        if (conf.endOpacity != 1) {
            this._setItemOpacity(item);
        }

        /* set font size */
        if (!this.Browser.IE) els.fontSize = (fontSize * 100) +"%";

        /* set z-index */
        els.zIndex = 32768 + Math.round(zIndex * this.items.length); // just for FF

        conf.onDrawItem(item);

        els.visibility = "visible";
        els.display = "block";
    },

    _scaleImageSize: function (item, size, max) {
        var sFL = this.conf.scaleFactorLandscape;
        var sFP = this.conf.scaleFactorPortrait;
        var vF = this.conf.verticalFlow;
        var prop = item.content.origProportion;
        var width = size.width;
        var height = size.height;
        var c = item.content;

        if (vF) {
            if (prop <= 1) {
                if (sFL != "max" && sFL != 1) {
                    height *= sFL;
                    width = Math.min(height * prop, max ? max : 1 );
                }
                height = width / prop;
            }
            else if (prop > 1) {
                if (sFP == "max") {
                    height = max ? max : 1;
                }
                else if (sFP != 1) {
                    width *= sFP;
                    height = Math.min(width/prop, max ? max : 1) 
                }
                else {
                    height = width / prop;
                }
                width = height * prop;
            }
        }
        else {
            if (prop > 1) {
                if (sFL != "max" && sFL != 1) {
                    width *= sFL;
                    height = Math.min(width / prop, max ? max : 1);
                }
                width = height * prop;
            }
            else if (prop <= 1) {
                if (sFP == "max") {
                    width = max ? max : 1;
                } 
                else if (sFP != 1) {
                    height *= sFP;
                    width = Math.min(height*prop, max ? max : 1);
                }
                else {
                    width = height * prop;
                }
                height = width / prop;
            }
        }

        height = isNaN(height) ? 0 : height;
        width = isNaN(width) ? 0 : width;

        if (!max && this.conf.fixItemSize) {

            var propS = size.width / size.height;

            var max = Math.max(size.width, size.height);
            var s = this._scaleImageSize(item, {width: max, height: max}, max);

            if (propS < 1) {
                height = s.height/size.height;
                width = height * prop / propS;
            }
            else {
                width = s.width/size.width;
                height = width / prop * propS;
            }

            var h = height * 100;
            var w = width * 100;
            var mL= (1 - width)/2 * 100;
            var mT= ( 1 - height ) / propS * 100 * (vF ? 0.5 : 1 );
            c.style.height = h+"%";
            if (item.reflection) item.reflection.style.height = h*this.conf.reflectionHeight+"%";
            c.style.width = w+"%";
            if (item.reflection) item.reflection.style.width = w+"%";
            c.style.marginLeft = mL+"%";
            if (item.reflection) item.reflection.style.marginLeft = mL+"%";
            c.style.marginTop = mT+"%";

            item.element.style.overflow = "hidden";

            return size;
        }
        else {
            return {width: width, height: height};
        }

    },

    _setItemSize: (function () {
        if (ContentFlowGlobal.Browser.IE) {
            var _setItemSize = function (item, size) {
                if (!this.conf.fixItemSize) {
                    item.content.style.height = size.height+"px";
                }
                else if (ContentFlowGlobal.Browser.IE6) {
                    var h = parseInt(item.content.style.height)/100;
                    item.content.style.height = size.height*h+"px"; 
                    var mT = parseInt(item.content.style.marginTop)/100;
                    item.content.style.marginTop = size.height*mT+"px";
                }
                if (item.reflection) {
                    var h = parseInt(item.content.style.height);
                    item.reflection.style.height = h*this.conf.reflectionHeight+"px";
                    item.reflection.style.marginTop = h * this.conf.reflectionGap + "px";
                }
                item.element.style.width = size.width +"px";
                item.element.style.height = size.height*(1+this.conf.reflectionHeight+this.conf.reflectionGap)+"px";
            }
        }
        else {
            var _setItemSize = function (item, size) {
                if (item.reflection) {
                    item.element.style.height = size.height*(1+this.conf.reflectionHeight + this.conf.reflectionGap) +"px";
                    item.reflection.style.marginTop = size.height * this.conf.reflectionGap + "px";
                }
                else if (this._reflectionWithinImage) {
                    item.element.style.height = size.height*(1+this.conf.reflectionHeight + this.conf.reflectionGap) +"px";
                }
                else {
                    item.element.style.height = size.height +"px";
                }
                item.element.style.width = size.width +"px";
            }
        }
        return _setItemSize;

    })(),

    _setItemOpacity: (function () {
            if (ContentFlowGlobal.Browser.IE6) {
                var _setItemOpacity = function (item) { 
                    if (item.content.origSrc && item.content.origSrc.match(/\.png$/) ) {
                        var s = item.content.src;
                        item.content.src = item.content.origSrc;
                        item.content.style.filter = item.content.filterString+" progid:DXImageTransform.Microsoft.BasicImage(opacity="+item.opacity+")";
                        item.content.src = s;
                    }
                    else {
                        item.content.style.filter = "progid:DXImageTransform.Microsoft.BasicImage(opacity="+item.opacity+")";
                    }
                    if (item.reflection) item.reflection.style.filter = item.reflection.filterString+"progid:DXImageTransform.Microsoft.BasicImage(opacity="+item.opacity+")"; 
                }
            }
            else if (ContentFlowGlobal.Browser.IE) {
                var _setItemOpacity = function (item) { item.element.style.filter = "progid:DXImageTransform.Microsoft.BasicImage(opacity="+item.opacity+")"; }
            }
            else {
                var _setItemOpacity = function (item) { item.element.style.opacity = item.opacity; }
            }
            return  _setItemOpacity;
    })()


};


/* ==================== extendig javascript/DOM objects ==================== */

/*
 *  adds bind method to Function class
 *  http://www.digital-web.com/articles/scope_in_javascript/
 */

if (!Function.bind) {
    Function.prototype.bind = function(obj) {
        var method = this;
        return function () {
            return method.apply(obj, arguments);
        };
    };
}


/*
 * extending Math object
 */
if (!Math.erf2) {
    // error function (http://en.wikipedia.org/wiki/Error_function), implemented as erf(x)^2
    Math.erf2 = function (x) {
        var a = - (8*(Math.PI -3)/(3*Math.PI*(Math.PI -4)));
        var x2 = x*x;
        var f = 1 - Math.pow(Math.E, -x2 * (4/Math.PI + a*x2)/(1+a*x2));
        return f;
    };
}

if (!Math._2PI05) {
    Math._2PI05 = Math.sqrt(2*Math.PI);
}

if (!Math.normDist) {
    // normal distribution
    Math.normDist = function (x, sig, mu) {
        if (!sig) var sig = 1;
        if (!mu) var mu = 0;
        if (!x) var x = - mu;
        return 1/(sig * Math._2PI05) * Math.pow(Math.E, - (x-mu)*(x-mu)/(2*sig*sig) );
    };
}

if (!Math.normedNormDist) {
    Math.normedNormDist = function (x, sig, mu) {
        return this.normDist(x, sig, mu)/this.normDist(mu, sig, mu);
    };
}

if (!Math.exp) {
    Math.exp = function(x) {
        return Math.pow(Math.E, x);
    };
}

if (!Math.ln) {
    Math.ln = Math.log;
}

if (!Math.log2) {
    Math.log2 = function (x) {
        return Math.log(x)/Math.LN2;
    };
}

if (!Math.log10) {
    Math.log10 = function (x) {
        return Math.log(x)/Math.LN10;
    };
}

if (!Math.logerithm) {
    Math.logerithm = function (x, b) {
        if (!b || b == Math.E)
            return Math.log(x);
        else if (b == 2)
            return Math.log2(x);
        else if (b == 10)
            return Math.log10(x);
        else
            return Math.log(x)/Math.log(b);
    };
}


/*
 * extending Event object
 */
if (!Event) var Event = {};

if (!Event.stop) {
    Event.stop = function (event) {
        event.cancelBubble = true;
        if (event.preventDefault) event.preventDefault();
        if (event.stopPropagation) event.stopPropagation();
        return false;
    };
}

/*
 * extending Element object
 */
if (document.all && !window.opera) {
    window.$CF = function (el) {
        if (typeof el == "string") {
            return window.$CF(document.getElementById(el));
        }
        else {
            if (CFElement.prototype.extend && el && !el.extend) CFElement.prototype.extend(el);
        }
        return el;
    };
} else {
    window.$CF = function (el) {
        return el;
    };
}

if (!window.HTMLElement) {
    CFElement = {};
    CFElement.prototype = {};
    CFElement.prototype.extend = function (el) {
        for (var method in this) {
            if (!el[method]) el[method] = this[method];
        }
    };
}
else {
    CFElement = window.HTMLElement;
}


/*
 * Thanks to Peter-Paul Koch
 * http://www.quirksmode.org/js/findpos.html
 */
if (!CFElement.findPos) {
    CFElement.prototype.findPos = function() {
        var obj = this;
        var curleft = curtop = 0;
        try {
            if (obj.offsetParent) {
                curleft = obj.offsetLeft;
                curtop = obj.offsetTop;
                while (obj = obj.offsetParent) {
                    curleft += obj.offsetLeft;
                    curtop += obj.offsetTop;
                }
            }
        }
        catch (ex) {}
        return {left:curleft, top:curtop};
    };
}

if (!CFElement.getDimensions) {
    CFElement.prototype.getDimensions = function() {
        return {width: this.clientWidth, height: this.clientHeight};
    };
}

/*
 * checks if an element has the class className
 */
if (!CFElement.hasClassName) {
    CFElement.prototype.hasClassName = function(className) {
        return (new RegExp('\\b'+className+'\\b').test(this.className));
    };
}

/*
 * adds the class className to the element
 */ 
if (!CFElement.addClassName) {
    CFElement.prototype.addClassName = function(className) {
        if(!this.hasClassName(className)) {
           this.className += (this.className ? ' ':'') + className ;
        }
    };
}

/*
 * removes the class className from the element el
 */ 
if (!CFElement.removeClassName) {
    CFElement.prototype.removeClassName = function(className) {
        this.className = this.className.replace(new RegExp('\\b'+className+'\\b'), '').replace(/\s\s/g,' ');
    };
}

/*
 * removes or adds the class className from/to the element el
 * depending if the element has the class className or not.
 */
if (!CFElement.toggleClassName) {
    CFElement.prototype.toggleClassName = function(className) {
        if(this.hasClassName(className)) {
            this.removeClassName(className);
        } else {
            this.addClassName(className);
        }
    };
}

/*
 * returns all children of element el, which have the class className
 */
if (!CFElement.getChildrenByClassName) {
    CFElement.prototype.getChildrenByClassName = function(className) {
        var children = new Array();
        for (var i=0; i < this.childNodes.length; i++) {
            var c = this.childNodes[i];
            if (c.nodeType == 1 && $CF(c).hasClassName(className)) {
                children.push(c);
            }
        }
        return children;
    };
}

/*
 * Browser independent event handling method.
 * adds the eventListener  eventName to element el and attaches the function method to it.
 */
if (!CFElement.addEvent) {
    CFElement.prototype.addEvent = function(eventName, method, capture) {
        if (this.addEventListener)
            this.addEventListener(eventName, method, capture);
        else
            this.attachEvent('on'+eventName, method);
    };
}
   
/*
 * Browser independent event handling method.
 * removes the eventListener  eventName with the attached function method from element el.
 */
if (!CFElement.removeEvent) {
    CFElement.prototype.removeEvent = function(eventName, method, capture) {
        if (this.removeEventListener)
            this.removeEventListener(eventName, method, capture);
        else
            this.detachEvent('on'+eventName, method);
    };
}

/*
 * Browser independent event handling method.
 * adds the eventListener  eventName to element el and attaches the function method to it.
 */
if (!window.addEvent) {
    window.addEvent = function(eventName, method, capture) {
        if (this.addEventListener) {
            this.addEventListener(eventName, method, capture);
        } else {
            if (eventName != 'load' && eventName != 'resize')
                document.attachEvent('on'+eventName, method);
            else
                this.attachEvent('on'+eventName, method);
        }
    };
}
   
/*
 * Browser independent event handling method.
 * removes the eventListener  eventName with the attached function method from element el.
 */
if (!window.removeEvent) {
    window.removeEvent = function(eventName, method, capture) {
        if (this.removeEventListener) {
            this.removeEventListener(eventName, method, capture);
        } else {
            if (eventName != 'load' && eventName != 'resize')
                document.detachEvent('on'+eventName, method);
            else
                this.detachEvent('on'+eventName, method);
        }
    };
}

/* ==================== start it all up ==================== */
//ContentFlowGlobal.init();

/**
 * PrimeFaces ContentFlow Widget
 */
PrimeFaces.widget.ContentFlow = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.renderDeferred();
    },
    
     _render: function() {
        this.cf = new ContentFlow(this.id, this.cfg);
     }
});