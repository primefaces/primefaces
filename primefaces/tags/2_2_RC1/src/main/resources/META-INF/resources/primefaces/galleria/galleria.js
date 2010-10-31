/*!
 * Galleria v 1.1.95 2010-08-06
 * http://galleria.aino.se
 *
 * Copyright (c) 2010, Aino
 * Licensed under the MIT license.
 */

(function() {

var initializing = false,
    fnTest = /xyz/.test(function(){xyz;}) ? /\b__super\b/ : /.*/,
    Class = function(){},
    window = this;

Class.extend = function(prop) {
    var __super = this.prototype;
    initializing = true;
    var proto = new this();
    initializing = false;
    for (var name in prop) {
        if (name) {
            proto[name] = typeof prop[name] == "function" && 
                typeof __super[name] == "function" && fnTest.test(prop[name]) ? 
                (function(name, fn) { 
                    return function() { 
                        var tmp = this.__super; 
                        this.__super = __super[name]; 
                        var ret = fn.apply(this, arguments);       
                        this.__super = tmp; 
                        return ret; 
                    }; 
                })(name, prop[name]) : prop[name]; 
        } 
    }

    function Class() {
        if ( !initializing && this.__constructor ) {
            this.__constructor.apply(this, arguments);
        }
    }
    Class.prototype = proto;
    Class.constructor = Class;
    Class.extend = arguments.callee;
    return Class;
};

var Base = Class.extend({
    loop : function( elem, fn) {
        var scope = this;
        if (typeof elem == 'number') {
            elem = new Array(elem);
        }
        jQuery.each(elem, function() {
            fn.call(scope, arguments[1], arguments[0]);
        });
        return elem;
    },
    create : function( elem, className ) {
        elem = elem || 'div';
        var el = document.createElement(elem);
        if (className) {
            el.className = className;
        }
        return el;
    },
    getElements : function( selector ) {
        var elems = {};
        this.loop( jQuery(selector), this.proxy(function( elem ) {
            this.push(elem, elems);
        }));
        return elems;
    },
    setStyle : function( elem, css ) {
        jQuery(elem).css(css);
        return this;
    },
    getStyle : function( elem, styleProp, parse ) {
        var val = jQuery(elem).css(styleProp);
        return parse ? this.parseValue( val ) : val;
    },
    cssText : function( string ) {
        var style = document.createElement('style');
        this.getElements('head')[0].appendChild(style);
        if (style.styleSheet) { // IE
            style.styleSheet.cssText = string;
        } else {
            var cssText = document.createTextNode(string);
            style.appendChild(cssText);
        }
        return this;
    },
    touch : function(el) {
        var sibling = el.nextSibling;
        var parent = el.parentNode;
        parent.removeChild(el);
        if ( sibling ) {
            parent.insertBefore(el, sibling);
        } else {
            parent.appendChild(el);
        }
        if (el.styleSheet && el.styleSheet.imports.length) {
            this.loop(el.styleSheet.imports, function(i) {
                el.styleSheet.addImport(i.href);
            });
        }
    },
    loadCSS : function(href, callback) {
        //var exists = this.getElements('link[href="'+href+'"]').length;
        var exists = true;
        if (exists) {
            callback.call(null);
            return exists[0];
        }
        var link = this.create('link');
        link.rel = 'stylesheet';
        link.href = href;
        
        if (typeof callback == 'function') {
            // a new css check method, still experimental...
            this.wait(function() {
                return !!document.body;
            }, function() {
                var testElem = this.create('div', 'ui-ui-galleria-container ui-galleria-stage');
                this.moveOut(testElem);
                document.body.appendChild(testElem);
                var getStyles = this.proxy(function() {
                    var str = '';
                    var props;
                    if (document.defaultView && document.defaultView.getComputedStyle) {
                        props = document.defaultView.getComputedStyle(testElem, "");
                        this.loop(props, function(prop) {
                            str += prop + props.getPropertyValue(prop);
                        });
                    } else if (testElem.currentStyle) { // IE
                        props = testElem.currentStyle;
                        this.loop(props, function(val, prop) {
                            str += prop + val;
                        });
                    }
                    return str;
                });
                var current = getStyles();
                this.wait(function() {
                    return getStyles() !== current;
                }, function() {
                    document.body.removeChild(testElem);
                    callback.call(link);
                }, function() {
                    G.raise('Could not confirm theme CSS');
                }, 2000);
            });
        }
        window.setTimeout(this.proxy(function() {
            var styles = this.getElements('link[rel="stylesheet"],style');
            if (styles.length) {
                styles[0].parentNode.insertBefore(link, styles[0]);
            } else {
                this.getElements('head')[0].appendChild(link);
            }
            // IE needs a manual touch to re-order the cascade
            if (G.IE) {
                this.loop(styles, function(el) {
                    this.touch(el);
                })
            }
        }), 2);
        return link;
    },
    moveOut : function( elem ) {
        return this.setStyle(elem, {
            position: 'absolute',
            left: '-10000px',
            display: 'block'
        });
    },
    moveIn : function( elem ) {
        return this.setStyle(elem, {
            left: '0'
        }); 
    },
    reveal : function( elem ) {
        return jQuery( elem ).show();
    },
    hide : function( elem ) {
        return jQuery( elem ).hide();
    },
    mix : function() {
        return jQuery.extend.apply(jQuery, arguments);
    },
    proxy : function( fn, scope ) {
        if ( typeof fn !== 'function' ) {
            return function() {};
        }
        scope = scope || this;
        return function() {
            return fn.apply( scope, Array.prototype.slice.call(arguments) );
        };
    },
    listen : function( elem, type, fn ) {
        jQuery(elem).bind( type, fn );
    },
    forget : function( elem, type, fn ) {
        jQuery(elem).unbind(type, fn);
    },
    dispatch : function( elem, type ) {
        jQuery(elem).trigger(type);
    },
    clone : function( elem, keepEvents ) {
        keepEvents = keepEvents || false;
        return jQuery(elem).clone(keepEvents)[0];
    },
    removeAttr : function( elem, attributes ) {
        this.loop( attributes.split(' '), function(attr) {
            jQuery(elem).removeAttr(attr);
        });
    },
    push : function( elem, obj ) {
        if (typeof obj.length == 'undefined') {
            obj.length = 0;
        }
        Array.prototype.push.call( obj, elem );
        return elem;
    },
    width : function( elem, outer ) {
        return this.meassure(elem, outer, 'Width');
    },
    height : function( elem, outer ) {
        return this.meassure(elem, outer, 'Height');
    },
    meassure : function(el, outer, meassure) {
        var elem = jQuery( el );
        var ret = outer ? elem['outer'+meassure](true) : elem[meassure.toLowerCase()]();
        // fix quirks mode
        if (G.QUIRK) {
            var which = meassure == "Width" ? [ "left", "right" ] : [ "top", "bottom" ];
            this.loop(which, function(s) {
                ret += elem.css('border-' + s + '-width').replace(/[^\d]/g,'') * 1;
                ret += elem.css('padding-' + s).replace(/[^\d]/g,'') * 1;
            });
        }
        return ret;
    },
    toggleClass : function( elem, className, arg ) {
        if (typeof arg !== 'undefined') {
            var fn = arg ? 'addClass' : 'removeClass';
            jQuery(elem)[fn](className);
            return this;
        }
        jQuery(elem).toggleClass(className);
        return this;
    },
    hideAll : function( el ) {
        jQuery(el).find('*').hide();
    },
    animate : function( el, options ) {
        options.complete = this.proxy(options.complete);
        var elem = jQuery(el);
        if (!elem.length) {
            return;
        }
        if (options.from) {
            elem.css(from);
        }
        elem.animate(options.to, {
            duration: options.duration || 400,
            complete: options.complete,
            easing: options.easing || 'swing'
        });
    },
    wait : function(fn, callback, err, max) {
        fn = this.proxy(fn);
        callback = this.proxy(callback);
        err = this.proxy(err);
        var ts = new Date().getTime() + (max || 3000);
        window.setTimeout(function() {
            if (fn()) {
                callback();
                return false;
            }
            if (new Date().getTime() >= ts) {
                err();
                callback();
                return false;
            }
            window.setTimeout(arguments.callee, 2);
        }, 2);
        return this;
    },
    loadScript: function(url, callback) {
       var script = document.createElement('script');
       script.src = url;
       script.async = true; // HTML5

       var done = false;
       var scope = this;

       // Attach handlers for all browsers
       script.onload = script.onreadystatechange = function() {
           if ( !done && (!this.readyState ||
               this.readyState == "loaded" || this.readyState == "complete") ) {
               done = true;
               
               if (typeof callback == 'function') {
                   callback.call(scope, this);
               }

               // Handle memory leak in IE
               script.onload = script.onreadystatechange = null;
           }
       };
       var s = document.getElementsByTagName('script')[0];
       s.parentNode.insertBefore(script, s);
       
       return this;
    },
    parseValue: function(val) {
        if (typeof val == 'number') {
            return val;
        } else if (typeof val == 'string') {
            var arr = val.match(/\-?\d/g);
            return arr && arr.constructor == Array ? arr.join('')*1 : 0;
        } else {
            return 0;
        }
    }
});

var Picture = Base.extend({
    __constructor : function(order) {
        this.image = null;
        this.elem = this.create('div', 'ui-galleria-image');
        this.setStyle( this.elem, {
            overflow: 'hidden',
            position: 'relative' // for IE Standards mode
        } );
        this.order = order;
        this.orig = { w:0, h:0, r:1 };
    },
    
    cache: {},
    ready: false,
    
    add: function(src) {
        if (this.cache[src]) {
            return this.cache[src];
        }
        var image = new Image();
        image.src = src;
        this.setStyle(image, {display: 'block'});
        if (image.complete && image.width) {
            this.cache[src] = image;
            return image;
        }
        image.onload = (function(scope) {
            return function() {
                scope.cache[src] = image;
            };
        })(this);
        return image;
    },
    
    isCached: function(src) {
        return this.cache[src] ? this.cache[src].complete : false;
    },
    
    make: function(src) {
        var i = this.cache[src] || this.add(src);
        return this.clone(i);
    },
    
    load: function(src, callback) {
        callback = this.proxy( callback );
        this.elem.innerHTML = '';
        this.image = this.make( src );
        this.moveOut( this.image );
        this.elem.appendChild( this.image );
        this.wait(function() {
            return (this.image.complete && this.image.width);
        }, function() {
            this.orig = {
                h: this.h || this.image.height,
                w: this.w || this.image.width
            };
            callback( {target: this.image, scope: this} );
        }, function() {
            G.raise('image not loaded in 20 seconds: '+ src);
        }, 20000);
        return this;
    },
    
    scale: function(options) {
        var o = this.mix({
            width: 0,
            height: 0,
            min: undefined,
            max: undefined,
            margin: 0,
            complete: function(){},
            position: 'center',
            crop: false
        }, options);
        if (!this.image) {
            return this;
        }
        var width,height;
        this.wait(function() {
            width  = o.width || this.width(this.elem);
            height = o.height || this.height(this.elem);
            return width && height;
        }, function() {
            var nw = (width - o.margin*2) / this.orig.w;
            var nh = (height- o.margin*2) / this.orig.h;
            var rmap = {
                'true': Math.max(nw,nh),
                'width': nw,
                'height': nh,
                'false': Math.min(nw,nh)
            }
            var ratio = rmap[o.crop.toString()];
            if (o.max) {
                ratio = Math.min(o.max, ratio);
            }
            if (o.min) {
                ratio = Math.max(o.min, ratio);
            }
            this.setStyle(this.elem, {
                width: width,
                height: height
            });
            this.image.width = Math.ceil(this.orig.w * ratio);
            this.image.height = Math.ceil(this.orig.h * ratio);
            
            var getPosition = this.proxy(function(value, img, m) {
                var result = 0;
                if (/\%/.test(value)) {
                    var pos = parseInt(value) / 100;
                    result = Math.ceil(this.image[img] * -1 * pos + m * pos);
                } else {
                    result = parseInt(value);
                }
                return result;
            });
            
            var map = {
                'top': { top: 0 },
                'left': { left: 0 },
                'right': { left: '100%' },
                'bottom': { top: '100%' }
            }
            
            var pos = {};
            var mix = {};
            
            this.loop(o.position.toLowerCase().split(' '), function(p, i) {
                if (p == 'center') {
                    p = '50%';
                }
                pos[i ? 'top' : 'left'] = p;
            });

            this.loop(pos, function(val, key) {
                if (map.hasOwnProperty(val)) {
                    mix = this.mix(mix, map[val]);
                }
            });
            
            pos = pos.top ? this.mix(pos, mix) : mix;
            
            pos = this.mix({
                top: '50%',
                left: '50%'
            }, pos);

            this.setStyle(this.image, {
                position : 'relative',
                top :  getPosition(pos.top, 'height', height),
                left : getPosition(pos.left, 'width', width)
            });
            this.ready = true;
            o.complete.call(this);
        });
        return this;
    }
});

var G = window.Galleria = Base.extend({
    
    __constructor : function(options) {
        this.theme = undefined;
        this.options = options;
        this.playing = false;
        this.playtime = 5000;
        this.active = null;
        this.queue = {};
        this.data = {};
        this.dom = {};
        
        var kb = this.keyboard = {
            keys : {
                UP: 38,
                DOWN: 40,
                LEFT: 37,
                RIGHT: 39,
                RETURN: 13,
                ESCAPE: 27,
                BACKSPACE: 8
            },
            map : {},
            bound: false,
            press: this.proxy(function(e) {
                var key = e.keyCode || e.which;
                if (kb.map[key] && typeof kb.map[key] == 'function') {
                    kb.map[key].call(this, e);
                }
            }),
            attach: this.proxy(function(map) {
                for( var i in map ) {
                    var k = i.toUpperCase();
                    if ( kb.keys[k] ) {
                        kb.map[kb.keys[k]] = map[i];
                    }
                }
                if (!kb.bound) {
                    kb.bound = true;
                    this.listen(document, 'keydown', kb.press);
                }
            }),
            detach: this.proxy(function() {
                kb.bound = false;
                this.forget(document, 'keydown', kb.press);
            })
        };
        
        this.timeouts = {
            trunk: {},
            add: function(id, fn, delay, loop) {
                loop = loop || false;
                this.clear(id);
                if (loop) {
                    var self = this;
                    var old = fn;
                    fn = function() {
                        old();
                        self.add(id,fn,delay);
                    }
                }
                this.trunk[id] = window.setTimeout(fn,delay);
            },
            clear: function(id) {
                if (id && this.trunk[id]) {
                    window.clearTimeout(this.trunk[id]);
                    delete this.trunk[id];
                } else if (typeof id == 'undefined') {
                    for (var i in this.trunk) {
                        window.clearTimeout(this.trunk[i]);
                        delete this.trunk[i];
                    }
                }
            }
        };
        
        this.controls = {
            0 : null,
            1 : null,
            active : 0,
            swap : function() {
                this.active = this.active ? 0 : 1;
            },
            getActive : function() {
                return this[this.active];
            },
            getNext : function() {
                return this[Math.abs(this.active - 1)];
            }
        };
        
        var fs = this.fullscreen = {
            scrolled: 0,
            enter: this.proxy(function() {
                this.toggleClass( this.get('container'), 'fullscreen');
                fs.scrolled = jQuery(window).scrollTop();
                this.loop(fs.getElements(), function(el, i) {
                    fs.styles[i] = el.getAttribute('style');
                    el.removeAttribute('style');
                });
                this.setStyle(fs.getElements(0), {
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    zIndex: 10000
                });
                var bh = {
                    height: '100%',
                    overflow: 'hidden',
                    margin:0,
                    padding:0
                };
                this.setStyle( fs.getElements(1), bh );
                this.setStyle( fs.getElements(2), bh );
                this.attachKeyboard({
                    escape: this.exitFullscreen,
                    right: this.next,
                    left: this.prev
                });
                this.rescale(this.proxy(function() {
                    this.trigger(G.FULLSCREEN_ENTER);
                }));
                this.listen(window, 'resize', fs.scale);
            }),
            scale: this.proxy(function() {
                this.rescale();
            }),
            exit: this.proxy(function() {
                this.toggleClass( this.get('container'), 'fullscreen', false);
                if (!fs.styles.length) {
                    return;
                }
                this.loop(fs.getElements(), function(el, i) {
                    el.removeAttribute('style');
                    el.setAttribute('style', fs.styles[i]);
                });
                window.scrollTo(0, fs.scrolled);
                this.detachKeyboard();
                this.rescale(this.proxy(function() {
                    this.trigger(G.FULLSCREEN_EXIT);
                }));
                this.forget(window, 'resize', fs.scale);
            }),
            styles: [],
            getElements: this.proxy(function(i) {
                var elems = [ this.get('container'), document.body, this.getElements('html')[0] ];
                return i ? elems[i] : elems;
            })
        };
        
        var idle = this.idle = {
            trunk: [],
            bound: false,
            add: this.proxy(function(elem, styles, fn) {
                if (!elem) {
                    return;
                }
                if (!idle.bound) {
                    idle.addEvent();
                }
                elem = jQuery(elem);
                var orig = {};
                for (var style in styles) {
                    orig[style] = elem.css(style);
                }
                elem.data('idle', {
                    from: orig,
                    to: styles,
                    complete: true,
                    busy: false,
                    fn: this.proxy(fn)
                });
                idle.addTimer();
                idle.trunk.push(elem);
            }),
            remove: this.proxy(function(elem) {
                elem = jQuery(elem);
                this.loop(idle.trunk, function(el, i) {
                    if ( el && !el.not(elem).length ) {
                        idle.show(elem);
                        idle.trunk.splice(i,1);
                    }
                });
                if (!idle.trunk.length) {
                    idle.removeEvent();
                    this.clearTimer('idle');
                }
            }),
            addEvent: this.proxy(function() {
                idle.bound = true;
                this.listen( this.get('container'), 'mousemove click', idle.showAll );
            }),
            removeEvent: this.proxy(function() {
                idle.bound = false;
                this.forget( this.get('container'), 'mousemove click', idle.showAll );
            }),
            addTimer: this.proxy(function() {
                this.addTimer('idle', this.proxy(function() {
                    idle.hide();
                }),this.options.idle_time);
            }),
            hide: this.proxy(function() {
                this.trigger(G.IDLE_ENTER);
                this.loop(idle.trunk, function(elem) {
                    var data = elem.data('idle');
                    data.complete = false;
                    data.fn();
                    elem.animate(data.to, {
                        duration: 600,
                        queue: false,
                        easing: 'swing'
                    });
                });
            }),
            showAll: this.proxy(function() {
                this.clearTimer('idle');
                this.loop(idle.trunk, function(elem) {
                    idle.show(elem);
                });
            }),
            show: this.proxy(function(elem) {
                var data = elem.data('idle');
                if (!data.busy && !data.complete) {
                    data.busy = true;
                    this.trigger(G.IDLE_EXIT);
                    elem.animate(data.from, {
                        duration: 300,
                        queue: false,
                        easing: 'swing',
                        complete: function() {
                            $(this).data('idle').busy = false;
                            $(this).data('idle').complete = true;
                        }
                    });
                }
                idle.addTimer();
            })
        };
        
        var lightbox = this.lightbox = {
            w: 0,
            h: 0,
            initialized: false,
            active: null,
            init: this.proxy(function() {
                if (lightbox.initialized) {
                    return;
                }
                lightbox.initialized = true;
                var elems = 'lightbox-overlay lightbox-box lightbox-content lightbox-shadow lightbox-title ' +
                            'lightbox-info lightbox-close lightbox-prev lightbox-next lightbox-counter';
                this.loop(elems.split(' '), function(el) {
                    this.addElement(el);
                    lightbox[el.split('-')[1]] = this.get(el);
                });
                
                lightbox.image = new Galleria.Picture();
                
                this.append({
                    'lightbox-box': ['lightbox-shadow','lightbox-content', 'lightbox-close'],
                    'lightbox-info': ['lightbox-title','lightbox-counter','lightbox-next','lightbox-prev'],
                    'lightbox-content': ['lightbox-info']
                });
                document.body.appendChild( lightbox.overlay );
                document.body.appendChild( lightbox.box );
                lightbox.content.appendChild( lightbox.image.elem );
                
                lightbox.close.innerHTML = '&#215;';
                lightbox.prev.innerHTML = '&#9668;';
                lightbox.next.innerHTML = '&#9658;';
                
                this.listen( lightbox.close, 'click', lightbox.hide );
                this.listen( lightbox.overlay, 'click', lightbox.hide );
                this.listen( lightbox.next, 'click', lightbox.showNext );
                this.listen( lightbox.prev, 'click', lightbox.showPrev );
                
                if (this.options.lightbox_clicknext) {
                    this.setStyle( lightbox.image.elem, {cursor:'pointer'} );
                    this.listen( lightbox.image.elem, 'click', lightbox.showNext);
                }
                this.setStyle( lightbox.overlay, {
                    position: 'fixed', display: 'none',
                    opacity: this.options.overlay_opacity,
                    top: 0, left: 0, width: '100%', height: '100%',
                    background: this.options.overlay_background, zIndex: 99990
                });
                this.setStyle( lightbox.box, {
                    position: 'fixed', display: 'none',
                    width: 400, height: 400, top: '50%', left: '50%',
                    marginTop: -200, marginLeft: -200, zIndex: 99991
                });
                this.setStyle( lightbox.shadow, {
                    background:'#000', opacity:.4, width: '100%', height: '100%', position: 'absolute'
                });
                this.setStyle( lightbox.content, {
                    backgroundColor:'#fff',position: 'absolute',
                    top: 10, left: 10, right: 10, bottom: 10, overflow: 'hidden'
                });
                this.setStyle( lightbox.info, {
                    color: '#444', fontSize: '11px', fontFamily: 'arial,sans-serif', height: 13, lineHeight: '13px',
                    position: 'absolute', bottom: 10, left: 10, right: 10, opacity: 0
                });
                this.setStyle( lightbox.close, {
                    background: '#fff', height: 20, width: 20, position: 'absolute', textAlign: 'center', cursor: 'pointer',
                    top: 10, right: 10, lineHeight: '22px', fontSize: '16px', fontFamily:'arial,sans-serif',color:'#444', zIndex: 99999
                });
                this.setStyle( lightbox.image.elem, {
                    top: 10, left: 10, right: 10, bottom: 30, position: 'absolute'
                });
                this.loop('title prev next counter'.split(' '), function(el) {
                    var css = { display: 'inline', 'float':'left' };
                    if (el != 'title') {
                        this.mix(css, { 'float': 'right'});
                        if (el != 'counter') {
                            this.mix(css, { cursor: 'pointer'});
                        } else {
                            this.mix(css, { marginLeft: 8 });
                        }
                    }
                    this.setStyle(lightbox[el], css);
                });
                this.loop('prev next close'.split(' '), function(el) {
                    this.listen(lightbox[el], 'mouseover', this.proxy(function() {
                        this.setStyle(lightbox[el], { color:'#000' });
                    }));
                    this.listen(lightbox[el], 'mouseout', this.proxy(function() {
                        this.setStyle(lightbox[el], { color:'#444' });
                    }));
                });
                this.trigger(G.LIGHTBOX_OPEN);
            }),
            rescale: this.proxy(function(e) {
                var w = Math.min( this.width(window), lightbox.w );
                var h = Math.min( this.height(window), lightbox.h );
                var r = Math.min( (w-60) / lightbox.w, (h-80) / lightbox.h );
                var destW = (lightbox.w * r) + 40;
                var destH = (lightbox.h * r) + 60;
                var dest = {
                    width: destW,
                    height: destH,
                    marginTop: Math.ceil(destH/2)*-1,
                    marginLeft: Math.ceil(destW)/2*-1
                }
                if (!e) {
                    this.animate( lightbox.box, {
                        to: dest,
                        duration: this.options.lightbox_transition_speed,
                        easing: 'galleria',
                        complete: function() {
                            this.trigger({
                                type: G.LIGHTBOX_IMAGE,
                                imageTarget: lightbox.image.image
                            });
                            this.moveIn( lightbox.image.image );
                            this.animate( lightbox.image.image, { to: { opacity:1 }, duration: this.options.lightbox_fade_speed } );
                            this.animate( lightbox.info, { to: { opacity:1 }, duration: this.options.lightbox_fade_speed } );
                        }
                    });
                } else {
                    this.setStyle( lightbox.box, dest );
                }
            }),
            hide: this.proxy(function() {
                lightbox.image.image = null;
                this.forget(window, 'resize', lightbox.rescale);
                this.hide( lightbox.box );
                this.setStyle( lightbox.info, { opacity: 0 } );
                this.animate( lightbox.overlay, {
                    to: { opacity: 0 },
                    duration: 200,
                    complete: function() {
                        this.hide( lightbox.overlay );
                        this.setStyle( lightbox.overlay, { opacity: this.options.overlay_opacity});
                        this.trigger(G.LIGHTBOX_CLOSE);
                    }
                });
            }),
            showNext: this.proxy(function() {
                lightbox.show(this.getNext(lightbox.active));
            }),
            showPrev: this.proxy(function() {
                lightbox.show(this.getPrev(lightbox.active));
            }),
            show: this.proxy(function(index) {
                if (!lightbox.initialized) {
                    lightbox.init();
                }
                this.forget( window, 'resize', lightbox.rescale );
                index = typeof index == 'number' ? index : this.getIndex();
                lightbox.active = index;
                
                var data = this.getData(index);
                var total = this.data.length;
                this.setStyle( lightbox.info, {opacity:0} );

                lightbox.image.load( data.image, function(o) {
                    lightbox.w = o.scope.orig.w;
                    lightbox.h = o.scope.orig.h;
                    this.setStyle(o.target, {
                        width: '100.5%',
                        height: '100.5%',
                        top:0,
                        zIndex: 99998,
                        opacity: 0
                    });
                    lightbox.title.innerHTML = data.title;
                    lightbox.counter.innerHTML = (index+1) + ' / ' + total;
                    this.listen( window, 'resize', lightbox.rescale );
                    lightbox.rescale();
                });
                this.reveal( lightbox.overlay );
                this.reveal( lightbox.box );
            })
        };
        
        this.thumbnails = { width: 0 };
        this.stageWidth = 0;
        this.stageHeight = 0;
        
        var elems = 'container stage images image-nav image-nav-left image-nav-right ' + 
                    'info info-text info-title info-description info-author ' +
                    'thumbnails thumbnails-list thumbnails-container thumb-nav-left thumb-nav-right ' +
                    'loader counter';
        elems = elems.split(' ');
        
        this.loop(elems, function(blueprint) {
            this.dom[ blueprint ] = this.create('div', 'ui-galleria-' + blueprint);
        });
        
        this.target = this.dom.target = options.target.nodeName ? 
            options.target : this.getElements(options.target)[0];

        if (!this.target) {
             G.raise('Target not found.');
        }
    },
    
    init: function() {
        
        this.options = this.mix(G.theme.defaults, this.options);
        this.options = this.mix({
            autoplay: false,
            carousel: true,
            carousel_follow: true,
            carousel_speed: 400,
            carousel_steps: 'auto',
            clicknext: false,
            data_config : function( elem ) { return {}; },
            data_image_selector: 'img',
            data_source: this.target,
            data_type: 'auto',
            debug: false,
            extend: function(options) {},
            height: 'auto',
            idle_time: 3000,
            image_crop: false,
            image_margin: 0,
            image_pan: false,
            image_pan_smoothness: 12,
            image_position: '50%',
            keep_source: false,
            lightbox_clicknext: true,
            lightbox_fade_speed: 200,
            lightbox_transition_speed: 300,
            link_source_images: true,
            max_scale_ratio: undefined,
            min_scale_ratio: undefined,
            on_image: function(img,thumb) {},
            overlay_opacity: .85,
            overlay_background: '#0b0b0b',
            popup_links: false,
            preload: 2,
            queue: true,
            show: 0,
            show_info: true,
            show_counter: true,
            show_imagenav: true,
            thumb_crop: true,
            thumb_fit: true,
            thumb_margin: 0,
            thumb_quality: 'auto',
            thumbnails: true,
            transition: G.transitions.fade,
            transition_speed: 400
        }, this.options);
        
        var o = this.options;
        
        this.bind(G.DATA, function() {
            this.run();
        });
        
        if (o.clicknext) {
            this.loop(this.data, function(data) {
                delete data.link;
            });
            this.setStyle(this.get('stage'), { cursor: 'pointer'} );
            this.listen(this.get('stage'), 'click', this.proxy(function() {
                this.next();
            }));
        }
        
        this.bind(G.IMAGE, function(e) {
            o.on_image.call(this, e.imageTarget, e.thumbTarget);
        });
        
        this.bind(G.READY, function() {
            if (G.History) {
                G.History.change(this.proxy(function(e) {
                    var val = parseInt(e.value.replace(/\//,''));
                    if (isNaN(val)) {
                        window.history.go(-1);
                    } else {
                        this.show(val, undefined, true);
                    }
                }));
            }

            G.theme.init.call(this, o);
            o.extend.call(this, o);
            
            if (/^[0-9]{1,4}$/.test(hash) && G.History) {
                this.show(hash, undefined, true);
            } else if (typeof o.show == 'number') {
                this.show(o.show);
            }
            
            if (o.autoplay) {
                if (typeof o.autoplay == 'number') {
                    this.playtime = o.autoplay;
                }
                this.trigger( G.PLAY );
                this.playing = true;
            }
        });
        this.load();
        return this;
    },
    
    bind : function(type, fn) {
        this.listen( this.get('container'), type, this.proxy(fn) );
        return this;
    },
    
    unbind : function(type) {
        this.forget( this.get('container'), type );
    },
    
    trigger : function( type ) {
        type = typeof type == 'object' ? 
            this.mix( type, { scope: this } ) : 
            { type: type, scope: this };
        this.dispatch( this.get('container'), type );
        return this;
    },
    
    addIdleState: function() {
        this.idle.add.apply(this, arguments);
        return this;
    },
    
    removeIdleState: function() {
        this.idle.remove.apply(this, arguments);
        return this;
    },
    
    enterIdleMode: function() {
        this.idle.hide();
        return this;
    },
    
    exitIdleMode: function() {
        this.idle.show();
        return this;
    },
    
    addTimer: function() {
        this.timeouts.add.apply(this.timeouts, arguments);
        return this;
    },
    
    clearTimer: function() {
        this.timeouts.clear.apply(this.timeouts, arguments);
        return this;
    },
    
    enterFullscreen: function() {
        this.fullscreen.enter.apply(this, arguments);
        return this;
    },
    
    exitFullscreen: function() {
        this.fullscreen.exit.apply(this, arguments);
        return this;
    },
    
    openLightbox: function() {
        this.lightbox.show.apply(this, arguments);
    },
    
    closeLightbox: function() {
        this.lightbox.hide.apply(this, arguments);
    },
    
    getActive: function() {
        return this.controls.getActive();
    },
    
    getActiveImage: function() {
        return this.getActive().image || null;
    },
    
    run : function() {
        var o = this.options;
        if (!this.data.length) {
            G.raise('Data is empty.');
        }
        if (!o.keep_source && !Galleria.IE) {
            this.target.innerHTML = '';
        }
        this.loop(2, function() {
            var image = new Picture();
            this.setStyle( image.elem, {
                position: 'absolute',
                top: 0,
                left: 0
            });
            this.setStyle(this.get( 'images' ), {
                position: 'relative',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%'
            });
            this.get( 'images' ).appendChild( image.elem );
            this.push(image, this.controls);
        }, this);
        
        if (o.carousel) {
            // try the carousel on each thumb load
            this.bind(G.THUMBNAIL, this.parseCarousel);
        }
        
        this.build();
        this.target.appendChild(this.get('container'));
        
        this.loop(['info','counter','image-nav'], function(el) {
            if ( o[ 'show_'+el.replace(/-/,'') ] === false ) {
                this.moveOut( this.get(el) );
            }
        });
        
        var w = 0;
        var h = 0;
        
        for( var i=0; this.data[i]; i++ ) {
            var thumb;
            if (o.thumbnails === true) {
                thumb = new Picture(i);
                var src = this.data[i].thumb || this.data[i].image;
                
                this.get( 'thumbnails' ).appendChild( thumb.elem );
                
                w = this.getStyle(thumb.elem, 'width', true);
                h = this.getStyle(thumb.elem, 'height', true);
                
                // grab & reset size for smoother thumbnail loads
                if (o.thumb_fit && o.thum_crop !== true) {
                    this.setStyle(thumb.elem, { width:0, height: 0});
                }
                
                thumb.load(src, this.proxy(function(e) {
                    var orig = e.target.width;
                    e.scope.scale({
                        width: w,
                        height: h,
                        crop: o.thumb_crop,
                        margin: o.thumb_margin,
                        complete: this.proxy(function() {
                            // shrink thumbnails to fit
                            var top = ['left', 'top'];
                            var arr = ['Height', 'Width'];
                            this.loop(arr, function(m,i) {
                                if ((!o.thumb_crop || o.thumb_crop == m.toLowerCase()) && o.thumb_fit) {
                                    var css = {};
                                    var opp = arr[Math.abs(i-1)].toLowerCase();
                                    css[opp] = e.target[opp];
                                    this.setStyle(e.target.parentNode, css);
                                    var css = {};
                                    css[top[i]] = 0;
                                    this.setStyle(e.target, css);
                                }
                                e.scope['outer'+m] = this[m.toLowerCase()](e.target.parentNode, true);
                            });
                            // set high quality if downscale is moderate
                            this.toggleQuality(e.target, o.thumb_quality === true || ( o.thumb_quality == 'auto' && orig < e.target.width * 3 ));
                            this.trigger({
                                type: G.THUMBNAIL,
                                thumbTarget: e.target,
                                thumbOrder: e.scope.order
                            });
                        })
                    });
                }));
                if (o.preload == 'all') {
                    thumb.add(this.data[i].image);
                }
            } else if (o.thumbnails == 'empty') {
                thumb = {
                    elem:  this.create('div','ui-galleria-image'),
                    image: this.create('span','img')
                };
                thumb.elem.appendChild(thumb.image);
                this.get( 'thumbnails' ).appendChild( thumb.elem );
            } else {
                thumb = {
                    elem: false,
                    image: false
                }
            }
            var activate = this.proxy(function(e) {
                this.pause();
                e.preventDefault();
                var ind = e.currentTarget.rel;
                if (this.active !== ind) {
                    this.show( ind );
                }
            });
            if (o.thumbnails !== false) {
                thumb.elem.rel = i;
                this.listen(thumb.elem, 'click', activate);
            }
            if (o.link_source_images && o.keep_source && this.data[i].elem) {
                this.data[i].elem.rel = i;
                this.listen(this.data[i].elem, 'click', activate);
            }
            this.push(thumb, this.thumbnails );
        }
        this.setStyle( this.get('thumbnails'), { opacity: 0 } );
        
        if (o.height && o.height != 'auto') {
            this.setStyle( this.get('container'), { height: o.height })
        }
        
        this.wait(function() {
            // the most sensitive piece of code in Galleria, we need to have all the meassurements right to continue
            var cssHeight = this.getStyle( this.get( 'container' ), 'height', true );
            this.stageWidth = this.width(this.get( 'stage' ));
            this.stageHeight = this.height( this.get( 'stage' ));
            if (this.stageHeight < 50 && o.height == 'auto') {
                // no height detected for sure, set reasonable ratio (16/9)
                this.setStyle( this.get( 'container' ),  { 
                    height: Math.round( this.stageWidth*9/16 ) 
                } );
                this.stageHeight = this.height( this.get( 'stage' ));
            }
            return this.stageHeight && this.stageWidth;
        }, function() {
            this.listen(this.get('image-nav-right'), 'click', this.proxy(function(e) {
                if (o.clicknext) {
                    e.stopPropagation();
                }
                this.pause();
                this.next();
            }));
            this.listen(this.get('image-nav-left'), 'click', this.proxy(function(e) {
                if (o.clicknext) {
                    e.stopPropagation();
                }
                this.pause();
                this.prev();
            }));
            this.setStyle( this.get('thumbnails'), { opacity: 1 } );
            this.trigger( G.READY );
        }, function() {
            G.raise('Galleria could not load properly. Make sure stage has a height and width.');
        }, 5000);
    },
    
    mousePosition : function(e) {
        return {
            x: e.pageX - this.$('stage').offset().left + jQuery(document).scrollLeft(),
            y: e.pageY - this.$('stage').offset().top + jQuery(document).scrollTop()
        };
    },
    
    addPan : function(img) {
        var c = this.options.image_crop;
        if ( c === false ) {
            return;
        }
        if (this.options.image_crop === false) {
            return;
        }
        img = img || this.controls.getActive().image;
        if (img.tagName.toUpperCase() != 'IMG') {
            G.raise('Could not add pan');
        }
        
        var x = img.width/2;
        var y = img.height/2;
        var curX = destX = this.getStyle(img, 'left', true) || 0;
        var curY = destY = this.getStyle(img, 'top', true) || 0;
        var distX = 0;
        var distY = 0;
        var active = false;
        var ts = new Date().getTime();
        var calc = this.proxy(function(e) {
            if (new Date().getTime() - ts < 50) {
                return;
            }
            active = true;
            x = this.mousePosition(e).x;
            y = this.mousePosition(e).y;
        });
        var loop = this.proxy(function(e) {
            if (!active) {
                return;
            }
            distX = img.width - this.stageWidth;
            distY = img.height - this.stageHeight;
            destX = x / this.stageWidth * distX * -1;
            destY = y / this.stageHeight * distY * -1;
            curX += (destX - curX) / this.options.image_pan_smoothness;
            curY += (destY - curY) / this.options.image_pan_smoothness;
            if (distY > 0) {
                this.setStyle(img, { top: Math.max(distY*-1, Math.min(0, curY)) });
            }
            if (distX > 0) {
                this.setStyle(img, { left: Math.max(distX*-1, Math.min(0, curX)) });
            }
        });
        this.forget(this.get('stage'), 'mousemove');
        this.listen(this.get('stage'), 'mousemove', calc);
        this.addTimer('pan', loop, 30, true);
    },
    
    removePan: function() {
        this.forget(this.get('stage'), 'mousemove');
        this.clearTimer('pan');
    },
    
    parseCarousel : function(e) {
        var w = 0;
        var h = 0;
        var hooks = [0];
        this.loop(this.thumbnails, function(thumb,i) {
            if (thumb.ready) {
                w += thumb.outerWidth || this.width(thumb.elem, true);
                hooks[i+1] = w;
                h = Math.max(h, this.height(thumb.elem));
            }
        });
        this.toggleClass(this.get('thumbnails-container'), 'ui-galleria-carousel', w > this.stageWidth);
        this.setStyle(this.get('thumbnails-list'), {
            overflow:'hidden',
            position: 'relative' // for IE Standards mode
        });
        this.setStyle(this.get('thumbnails'), {
            width: w,
            height: h,
            position: 'relative',
            overflow: 'hidden'
        });
        if (!this.carousel) {
            this.initCarousel();
        }
        this.carousel.max = w;
        this.carousel.hooks = hooks;
        this.carousel.width = this.width(this.get('thumbnails-list'));
        this.carousel.setClasses();
    },
    
    initCarousel : function() {
        var c = this.carousel = {
            right: this.get('thumb-nav-right'),
            left: this.get('thumb-nav-left'),
            update: this.proxy(function() {
                this.parseCarousel();
                // todo: fix so the carousel moves to the left
            }),
            width: 0,
            current: 0,
            set: function(i) {
                i = Math.max(i,0);
                while (c.hooks[i-1] + c.width > c.max && i >= 0) {
                    i--;
                }
                c.current = i;
                c.animate();
            },
            hooks: [],
            getLast: function(i) {
                i = i || c.current
                
                return i-1;
            },
            follow: function(i) {
                if (i == 0 || i == c.hooks.length-2) {
                    c.set(i);
                    return;
                }
                var last = c.current;
                while(c.hooks[last] - c.hooks[c.current] < c.width && last<= c.hooks.length) {
                    last++;
                }
                if (i-1 < c.current) {
                    c.set(i-1)
                } else if (i+2 > last) {
                    c.set(i - last + c.current + 2)
                }
            },
            max: 0,
            setClasses: this.proxy(function() {
                this.toggleClass( c.left, 'disabled', !c.current );
                this.toggleClass( c.right, 'disabled', c.hooks[c.current] + c.width > c.max );
            }),
            animate: this.proxy(function(to) {
                c.setClasses();
                this.animate( this.get('thumbnails'), {
                    to: { left: c.hooks[c.current] * -1 },
                    duration: this.options.carousel_speed,
                    easing: 'galleria',
                    queue: false
                });
            })
        };
        this.listen(c.right, 'click', this.proxy(function(e) {
            if (this.options.carousel_steps == 'auto') {
                for (var i = c.current; i<c.hooks.length; i++) {
                    if (c.hooks[i] - c.hooks[c.current] > c.width) {
                        c.set(i-2);
                        break;
                    }
                }
            } else {
                c.set(c.current + this.options.carousel_steps);
            }
        }));
        this.listen(c.left, 'click', this.proxy(function(e) {
            if (this.options.carousel_steps == 'auto') {
                for (var i = c.current; i>=0; i--) {
                    if (c.hooks[c.current] - c.hooks[i] > c.width) {
                        c.set(i+2);
                        break;
                    } else if (i == 0) {
                        c.set(0);
                        break;
                    }
                }
            } else {
                c.set(c.current - this.options.carousel_steps);
            }
        }));
    },
    addElement : function() {
        this.loop(arguments, function(b) {
            this.dom[b] = this.create('div', 'ui-galleria-' + b );
        });
        return this;
    },
    getDimensions: function(i) {
        return {
            w: i.width,
            h: i.height,
            cw: this.stageWidth,
            ch: this.stageHeight,
            top: (this.stageHeight - i.height) / 2,
            left: (this.stageWidth - i.width) / 2
        };
    },
    attachKeyboard : function(map) {
        this.keyboard.attach(map);
        return this;
    },
    detachKeyboard : function() {
        this.keyboard.detach();
        return this;
    },
    build : function() {
        this.append({
            'info-text' :
                ['info-title', 'info-description', 'info-author'],
            'info' : 
                ['info-text'],
            'image-nav' : 
                ['image-nav-right', 'image-nav-left'],
            'stage' : 
                ['images', 'loader', 'counter', 'image-nav'],
            'thumbnails-list' :
                ['thumbnails'],
            'thumbnails-container' : 
                ['thumb-nav-left', 'thumbnails-list', 'thumb-nav-right'],
            'container' : 
                ['stage', 'thumbnails-container', 'info']
        });
        
        this.current = this.create('span', 'current');
        this.current.innerHTML = '-';
        this.get('counter').innerHTML = ' / <span class="total">' + this.data.length + '</span>';
        this.prependChild('counter', this.current);
    },
    
    appendChild : function(parent, child) {
        try {
            this.get(parent).appendChild(this.get(child));
        } catch(e) {}
    },
    
    prependChild : function(parent, child) {
        var child = this.get(child) || child;
        try {
            this.get(parent).insertBefore(child, this.get(parent).firstChild);
        } catch(e) {}
    },
    
    remove : function() {
        var a = Array.prototype.slice.call(arguments);
        this.jQuery(a.join(',')).remove();
    },
    
    append : function(data) {
        for( var i in data) {
            if (data[i].constructor == Array) {
                for(var j=0; data[i][j]; j++) {
                    this.appendChild(i, data[i][j]);
                }
            } else {
                this.appendChild(i, data[i]);
            }
        }
        return this;
    },
    
    rescale : function(width, height, callback) {
        
        var o = this.options;
        callback = this.proxy(callback);
        
        if (typeof width == 'function') {
            callback = this.proxy(width);
            width = undefined;
        }
        
        var scale = this.proxy(function() {
            this.stageWidth = width || this.width(this.get('stage'));
            this.stageHeight = height || this.height(this.get('stage'));
            this.controls.getActive().scale({
                width: this.stageWidth, 
                height: this.stageHeight, 
                crop: o.image_crop, 
                max: o.max_scale_ratio,
                min: o.min_scale_ratio,
                margin: o.image_margin,
                position: o.image_position
            });
            if (this.carousel) {
                this.carousel.update();
            }
            this.trigger(G.RESCALE)
            callback();
        });
        if ( G.WEBKIT && !width && !height ) {
            this.addTimer('scale', scale, 5);// webkit is too fast
        } else {
            scale.call(this); 
        }
    },
    
    show : function(index, rewind, history) {
        if (!this.options.queue && this.queue.stalled) {
            return;
        }
        rewind = typeof rewind != 'undefined' ? !!rewind : index < this.active;
        history = history || false;
        index = Math.max(0, Math.min(parseInt(index), this.data.length - 1));
        if (!history && G.History) {
            G.History.value(index.toString());
            return;
        }
        this.active = index;
        this.push([index,rewind], this.queue);
        if (!this.queue.stalled) {
            this.showImage();
        }
        return this;
    },
    
    showImage : function() {
        var o = this.options;
        var args = this.queue[0];
        var index = args[0];
        var rewind = !!args[1];
        if (o.carousel && this.carousel && o.carousel_follow) {
            this.carousel.follow(index);
        }
        
        var src = this.getData(index).image;
        var active = this.controls.getActive();
        var next = this.controls.getNext();
        var cached = next.isCached(src);
        var complete = this.proxy(function() {
            this.queue.stalled = false;
            this.toggleQuality(next.image, o.image_quality);
            this.setStyle( active.elem, { zIndex : 0 } );
            this.setStyle( next.elem, { zIndex : 1 } );
            this.trigger({
                type: G.IMAGE,
                index: index,
                imageTarget: next.image,
                thumbTarget: this.thumbnails[index].image
            });
            if (o.image_pan) {
                this.addPan(next.image);
            }
            this.controls.swap();
            this.moveOut( active.image );
            if (this.getData( index ).link) {
                this.setStyle( next.image, { cursor: 'pointer' } );
                this.listen( next.image, 'click', this.proxy(function() {
                    if (o.popup_links) {
                        var win = window.open(this.getData( index ).link, '_blank');
                    } else {
                        window.location.href = this.getData( index ).link;
                    }
                }));
            }
            Array.prototype.shift.call( this.queue );
            if (this.queue.length) {
                this.showImage();
            }
            this.playCheck();
        });
        if (typeof o.preload == 'number' && o.preload > 0) {
            var p,n = this.getNext();
            try {
                for (var i = o.preload; i>0; i--) {
                    p = new Picture();
                    p.add(this.getData(n).image);
                    n = this.getNext(n);
                }
            } catch(e) {}
        }
        this.trigger( {
            type: G.LOADSTART,
            cached: cached,
            index: index,
            imageTarget: next.image,
            thumbTarget: this.thumbnails[index].image
        } );
        
        jQuery(this.thumbnails[index].elem).addClass('active').siblings('.active').removeClass('active');
        
        next.load( src, this.proxy(function(e) {
            next.scale({
                width: this.stageWidth, 
                height: this.stageHeight, 
                crop: o.image_crop, 
                max: o.max_scale_ratio, 
                min: o.min_scale_ratio,
                margin: o.image_margin,
                position: o.image_position,
                complete: this.proxy(function() {
                    if (active.image) {
                        this.toggleQuality(active.image, false);
                    }
                    this.toggleQuality(next.image, false);
                    this.trigger({
                        type: G.LOADFINISH,
                        cached: cached,
                        index: index,
                        imageTarget: next.image,
                        thumbTarget: this.thumbnails[index].image
                    });
                    this.queue.stalled = true;
                    var transition = G.transitions[o.transition] || o.transition;
                    this.removePan();
                    this.setInfo(index);
                    this.setCounter(index);
                    if (typeof transition == 'function') {
                        transition.call(this, {
                            prev: active.image,
                            next: next.image,
                            rewind: rewind,
                            speed: o.transition_speed || 400
                        }, complete );
                    } else {
                        complete();
                    }
                })
            });
        }));
    },
    
    getNext : function(base) {
        base = typeof base == 'number' ? base : this.active;
        return base == this.data.length - 1 ? 0 : base + 1;
    },
    
    getPrev : function(base) {
        base = typeof base == 'number' ? base : this.active;
        return base === 0 ? this.data.length - 1 : base - 1;
    },
    
    next : function() {
        if (this.data.length > 1) {
            this.show(this.getNext(), false);
        }
        return this;
    },
    
    prev : function() {
        if (this.data.length > 1) {
            this.show(this.getPrev(), true);
        }
        return this;
    },
    
    get : function( elem ) {
        return elem in this.dom ? this.dom[ elem ] : null;
    },
    
    getData : function( index ) {
        return this.data[index] || this.data[this.active];
    },
    
    getIndex : function() {
        return typeof this.active === 'number' ? this.active : 0;
    },
    
    play : function(delay) {
        this.trigger( G.PLAY );
        this.playing = true;
        this.playtime = delay || this.playtime;
        this.playCheck();
        return this;
    },
    
    pause : function() {
        this.trigger( G.PAUSE );
        this.playing = false;
        return this;
    },
    
    playCheck : function() {
        var p = 0;
        var i = 20; // the interval
        var ts = function() {
            return new Date().getTime();
        }
        var now = ts();
        if (this.playing) {
            this.clearTimer('play');
            var fn = this.proxy(function() {
                p = ts() - now;
                if ( p >= this.playtime && this.playing ) {
                    this.clearTimer('play');
                    this.next();
                    return;
                }
                if ( this.playing ) {
                    this.trigger({
                        type: G.PROGRESS,
                        percent: Math.ceil(p / this.playtime * 100),
                        seconds: Math.floor(p/1000),
                        milliseconds: p
                    });
                    this.addTimer('play', fn, i);
                }
            });
            this.addTimer('play', fn, i);
        }
    },
    
    setActive: function(val) {
        this.active = val;
        return this;
    },
    
    setCounter: function(index) {
        index = index || this.active;
        this.current.innerHTML = index+1;
        return this;
    },
    
    setInfo : function(index) {
        var data = this.getData(index || this.active);
        this.loop(['title','description','author'], function(type) {
            var elem = this.get('info-'+type);
            var fn = data[type] && data[type].length ? 'reveal' : 'hide';
            this[fn](elem);
            if (data[type]) {
                elem.innerHTML = data[type];
            }
        });
        return this;
    },
    
    hasInfo : function(index) {
        var d = this.getData(index);
        var check = 'title description author'.split(' ');
        for ( var i=0; check[i]; i++ ) {
            if ( d[ check[i] ] && d[ check[i] ].length ) {
                return true;
            }
        }
        return false;
    },
    
    getDataObject : function(o) {
        var obj = {
            image: '',
            thumb: '',
            title: '',
            description: '',
            author: '',
            link: ''
        };
        return o ? this.mix(obj,o) : obj;
    },
    
    jQuery : function( str ) {
        var ret = [];
        this.loop(str.split(','), this.proxy(function(elem) {
            elem = elem.replace(/^\s\s*/, "").replace(/\s\s*$/, "");
            if (this.get(elem)) {
                ret.push(elem);
            }
        }));
        var jQ = jQuery(this.get(ret.shift()));
        this.loop(ret, this.proxy(function(elem) {
            jQ = jQ.add(this.get(elem));
        }));
        return jQ;
    },
    
    $ : function( str ) {
        return this.jQuery( str );
    },
    
    toggleQuality : function(img, force) {
        if (!G.IE7 || typeof img == 'undefined' || !img) {
            return this;
        }
        if (typeof force === 'undefined') {
            force = img.style.msInterpolationMode == 'nearest-neighbor';
        }
        img.style.msInterpolationMode = force ? 'bicubic' : 'nearest-neighbor';

        return this;
    },
    
    unload : function() {
        //TODO
    },
    
    load : function() {
        var loaded = 0;
        var o = this.options;
        if (
            (o.data_type == 'auto' && 
                typeof o.data_source == 'object' && 
                !(o.data_source instanceof jQuery) && 
                !o.data_source.tagName
            ) || o.data_type == 'json' || o.data_source.constructor == Array ) {
            this.data = o.data_source;
            this.trigger( G.DATA );
            
        } else { // assume selector
            var images = jQuery(o.data_source).find(o.data_image_selector);
            var getData = this.proxy(function( elem ) {
                var i,j,anchor = elem.parentNode;
                if (anchor && anchor.nodeName == 'A') {
                    if (anchor.href.match(/\.(png|gif|jpg|jpeg)/i)) {
                        i = anchor.href;
                    } else {
                        j = anchor.href;
                    }
                }
                var obj = this.getDataObject({
                    title: elem.title,
                    thumb: elem.src,
                    image: i || elem.src,
                    description: elem.alt,
                    link: j || elem.getAttribute('longdesc'),
                    elem: elem
                });
                return this.mix(obj, o.data_config( elem ) );
            });
            this.loop(images, function( elem ) {
                loaded++;
                this.push( getData( elem ), this.data );
                if (!o.keep_source && !Galleria.IE) {
                    elem.parentNode.removeChild(elem);
                }
                if ( loaded == images.length ) {
                    this.trigger( G.DATA );
                }
            });
        }
    }
});

G.log = function() {
    try { 
        console.log.apply( console, Array.prototype.slice.call(arguments) ); 
    } catch(e) {
        try {
            opera.postError.apply( opera, arguments ); 
        } catch(er) { 
              alert( Array.prototype.join.call( arguments, " " ) ); 
        } 
    }
};

var nav = navigator.userAgent.toLowerCase();
var hash = window.location.hash.replace(/#\//,'');

G.DATA = 'data';
G.READY = 'ready';
G.THUMBNAIL = 'thumbnail';
G.LOADSTART = 'loadstart';
G.LOADFINISH = 'loadfinish';
G.IMAGE = 'image';
G.THEMELOAD = 'themeload';
G.PLAY = 'play';
G.PAUSE = 'pause';
G.PROGRESS = 'progress';
G.FULLSCREEN_ENTER = 'fullscreen_enter';
G.FULLSCREEN_EXIT = 'fullscreen_exit';
G.IDLE_ENTER = 'idle_enter';
G.IDLE_EXIT = 'idle_exit';
G.RESCALE = 'rescale';
G.LIGHTBOX_OPEN = 'lightbox_open';
G.LIGHTBOX_CLOSE = 'lightbox_cloe';
G.LIGHTBOX_IMAGE = 'lightbox_image';

G.IE8 = (typeof(XDomainRequest) !== 'undefined')
G.IE7 = !!(window.XMLHttpRequest && document.expando);
G.IE6 = (!window.XMLHttpRequest);
G.IE = !!(G.IE6 || G.IE7);
G.WEBKIT = /webkit/.test( nav );
G.SAFARI = /safari/.test( nav );
G.CHROME = /chrome/.test( nav );
G.QUIRK = (G.IE && document.compatMode && document.compatMode == "BackCompat");
G.MAC = /mac/.test(navigator.platform.toLowerCase());
G.OPERA = !!window.opera

G.Picture = Picture;

G.addTheme = function(obj) {
    var theme = {};
    var orig = ['name','author','version','defaults','init'];
    var proto = G.prototype;
    proto.loop(orig, function(val) {
        if (!obj[ val ]) {
            G.raise(val+' not specified in theme.');
        }
        if (val != 'name' && val != 'init') {
            theme[val] = obj[val];
        }
    });
    theme.init = obj.init;
    
    if (obj.css) {
        var css;
        proto.loop(proto.getElements('script'), function(el) {
            var reg = new RegExp('galleria.' + obj.name.toLowerCase() + '.js');
            if(reg.test(el.src)) {
                css = el.src.replace(/[^\/]*$/, "") + obj.css;
                proto.loadCSS(css, function() {
                    G.theme = theme;
                    jQuery(document).trigger( G.THEMELOAD );
                });
            }
        });
        if (!css) {
            G.raise('No theme CSS loaded');
        }
    }
    return theme;
};

G.raise = function(msg) {
    if ( G.debug ) {
        throw new Error( msg );
    }
};

G.loadTheme = function(src) {
    G.prototype.loadScript(src);
};

G.galleries = [];
G.get = function(index) {
    if (G.galleries[index]) {
        return G.galleries[index];
    } else if (typeof index !== 'number') {
        return G.galleries;
    } else {
        G.raise('Gallery index not found');
    }
}

jQuery.easing.galleria = function (x, t, b, c, d) {
    if ((t/=d/2) < 1) { 
        return c/2*t*t*t*t + b;
    }
    return -c/2 * ((t-=2)*t*t*t - 2) + b;
};

G.transitions = {
    add: function(name, fn) {
        if (name != arguments.callee.name ) {
            this[name] = fn;
        }
    },
    fade: function(params, complete) {
        jQuery(params.next).show().css('opacity',0).animate({
            opacity: 1
        }, params.speed, complete);
        if (params.prev) {
            jQuery(params.prev).css('opacity',1).animate({
                opacity: 0
            }, params.speed);
        }
    },
    flash: function(params, complete) {
        jQuery(params.next).css('opacity',0);
        if (params.prev) {
            jQuery(params.prev).animate({
                opacity: 0
            }, (params.speed/2), function() {
                jQuery(params.next).animate({
                    opacity: 1
                }, params.speed, complete);
            });
        } else {
            jQuery(params.next).animate({
                opacity: 1
            }, params.speed, complete);
        }
    },
    pulse: function(params, complete) {
        if (params.prev) {
            jQuery(params.prev).css('opacity',0);
        }
        jQuery(params.next).css('opacity',0).animate({
            opacity:1
        }, params.speed, complete);
    },
    slide: function(params, complete) {
        var image = jQuery(params.next).parent();
        var images =  this.$('images');
        var width = this.stageWidth;
        image.css({
            left: width * ( params.rewind ? -1 : 1 )
        });
        images.animate({
            left: width * ( params.rewind ? 1 : -1 )
        }, {
            duration: params.speed,
            queue: false,
            easing: 'galleria',
            complete: function() {
                images.css('left',0);
                image.css('left',0);
                complete();
            }
        });
    },
    fadeslide: function(params, complete) {
        if (params.prev) {
            jQuery(params.prev).css({
                opacity: 1,
                left: 0
            }).animate({
                opacity: 0,
                left: 50 * ( params.rewind ? 1 : -1 )
            },{
                duration: params.speed,
                queue: false,
                easing: 'swing'
            });
        }
        jQuery(params.next).css({
            left: 50 * ( params.rewind ? -1 : 1 ), 
            opacity: 0
        }).animate({
            opacity: 1,
            left:0
        }, {
            duration: params.speed,
            complete: complete,
            queue: false,
            easing: 'swing'
        });
    }
};

G.addTransition = function() {
    G.transitions.add.apply(this, arguments);
}

jQuery.fn.galleria = function(options) {
    
    options = options || {};
    var selector = this.selector;
    
    return this.each(function() {
        if ( !options.keep_source ) {
            jQuery(this).children().hide();
        }
    
        options = G.prototype.mix(options, {target: this} );
        var height = G.prototype.height(this) || G.prototype.getStyle(this, 'height', true);
        if (!options.height && height) {
            options = G.prototype.mix( { height: height }, options );
        }
    
        G.debug = !!options.debug;
    
        var gallery = new G(options);
        
        Galleria.galleries.push(gallery);
    
        if (G.theme) {
            gallery.init();
        } else {
            jQuery(document).bind(G.THEMELOAD, function() {
                gallery.init();
            });
        }
    })
};


})();


/*
 * PrimeFaces Galleria Widget
 */
PrimeFaces.widget.Galleria = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);

    this.cfg.show_imagenav = false;

    jQuery(this.jqId).galleria(this.cfg);

    this.applyTheme();
}

PrimeFaces.widget.Galleria.prototype.applyTheme = function() {
    jQuery(this.jqId).children('.ui-galleria-container').addClass('ui-widget ui-widget-content ui-corner-all');

    jQuery(this.jqId + ' .ui-galleria-thumb-nav-right').addClass('ui-icon ui-icon-circle-triangle-e');
    jQuery(this.jqId + ' .ui-galleria-thumb-nav-left').addClass('ui-icon ui-icon-circle-triangle-w');
    jQuery(this.jqId + ' .ui-galleria-info-text').addClass('ui-widget-content ui-corner-all');
}