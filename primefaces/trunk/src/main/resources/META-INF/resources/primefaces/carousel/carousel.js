/**
 * PrimeFaces Carousel Widget
 */
PrimeFaces.widget.Carousel = function(id, cfg) {
 
  this.id = id;
  this.cfg = cfg;
  this.jqId = PrimeFaces.escapeClientId(id);
  this.jq = $(this.jqId);
  this.container = this.jq.find('.ui-carousel-container');
  this.header = this.jq.find('.ui-carousel-header'),
  this.list = this.container.find('ol');
  this.items = this.container.find('ol li');
  this.prevButton = this.header.find('.ui-carousel-prev-button');
  this.nextButton = this.header.find('.ui-carousel-next-button');
    
  //get computed dimensions
  var item = document.defaultView.getComputedStyle(this.items.filter(':first').get(0), "");
  this.oneWidth = parseInt(item.getPropertyValue("width")) + parseInt(item.getPropertyValue("margin-left")) + parseInt(item.getPropertyValue("margin-right")) +  ((parseInt(item.getPropertyValue("border-left-width")) + parseInt(item.getPropertyValue("border-right-width"))));
  this.oneHeight= parseInt(item.getPropertyValue("height")) + Math.max(parseInt(item.getPropertyValue("margin-top")), parseInt(item.getPropertyValue("margin-bottom"))) + ((parseInt(item.getPropertyValue("border-top-width")) + parseInt(item.getPropertyValue("border-bottom-width"))));

  this.cfg.numVisible = this.cfg.numVisible || 3;
  this.cfg.speed *=1000;
  this.page = this.cfg.firstVisible || 0;
  this.plus = (((this.cfg.revealAmount || 0)/100)%this.items.length) * ( this.cfg.isVertical ? this.oneHeight : this.oneWidth);
  this.pageCount = this.items.length / (this.cfg.numVisible || 3 );
  this.pageCount = this.pageCount%1 > 0 ? parseInt(this.pageCount) : this.pageCount-1;

  //nav buttons
  if(this.pageCount < this.cfg.maxButton){
    this.header.append(this.getNavigator(this.pageCount+1));
    this.navButtons = this.header.find('.ui-carousel-nav-button');
  }
  else{
    this.header.append(this.getDropDown(this.pageCount+1));
    this.ddOptions = this.header.find('.ui-carousel-dropdown option');
  }

  //calculate container width/height
  if(this.cfg.isVertical){
    this.container.width(this.oneWidth);
    this.container.height((this.cfg.numVisible * this.oneHeight) + (this.plus*2));
  }
  else{
    this.container.width((this.cfg.numVisible * this.oneWidth) + (this.plus*2));
    this.container.height(this.oneHeight);
  }
    
  //first align
  this.setPosition(this.getPagePosition(this.page));
  this.jq.width(this.container.outerWidth(true));

  this.checkButtons();

  this.bindEvents();
  this.jq.css({
    visibility:'visible'
  });
}

/**
 * Creates dropdown navigation bar for given n
 */
PrimeFaces.widget.Carousel.prototype.getDropDown = function(n){
  var s = $('<select></select>').addClass('ui-widget ui-carousel-dropdown'),
  _self = this;

  for(var i=0; i<n; i++){
    var o = $('<option>'+ this.cfg.pagerFormat.replace(/{page}/i,(i+1)) +'</option>').attr('value', i);
    if(this.page == i)
      o.attr('selected', 'selected');
    s.append(o);
  }

  return s.change(function(e){
    _self.go(parseInt($(this).val()));
  });
}

/**
 * Creates navigation buttons for given n
 */
PrimeFaces.widget.Carousel.prototype.getNavigator = function(n){
  var nbc = $('<div></div>').addClass('ui-carousel-nav');

  for(var i=0; i<n ; i++)
    nbc.append($('<a></a>').addClass('ui-icon ui-carousel-nav-button ui-icon-radio-off'));

  return nbc;
}

/**
 * Autoplay startup.
 */
PrimeFaces.widget.Carousel.prototype.startAutoPlay = function(){
  var _self = this;
  if(this.cfg.autoPlayInterval){
    setInterval( function(){
      _self.next();
    }, this.cfg.autoPlayInterval);
  }
}

/**
 * Binds related mouse/key events.
 */
PrimeFaces.widget.Carousel.prototype.bindEvents = function(){
  
  var _self = this;
  
  this.navButtons ? this.navButtons.click(function(){
    if(_self.animating)
      return;
    else
      _self.animating = true;
    
    _self.go($(this).index());
  }) : false;
  
  this.prevButton.click(function(){
    
    if(_self.animating)
      return;
    else
      _self.animating = true;
    
    _self.prev();

  });
  
  this.nextButton.click(function(){
    if(_self.animating)
      return;
    else
      _self.animating = true;
    _self.next();
  });
}

/**
 * Calculates position of list for a page index.
 */
PrimeFaces.widget.Carousel.prototype.getPagePosition = function(index){
  return (-(index * (this.cfg.isVertical ? this.oneHeight : this.oneWidth) * this.cfg.numVisible) + this.plus);
}

/**
 * Returns instant position of list.
 */
PrimeFaces.widget.Carousel.prototype.getPosition = function(){
  return parseInt(this.list.css(this.cfg.isVertical ? 'top' : 'left'));
};

/**
 * Sets instant position of list.
 */
PrimeFaces.widget.Carousel.prototype.setPosition = function(val){
  this.list.css(this.cfg.isVertical ? {
    top : val
  } : {
    left : val
  });
};

/**
 * Transition of position for a given value and defined animation config.
 */
PrimeFaces.widget.Carousel.prototype.changePosition = function(val){
  if(this.getPosition() == val){
    this.animating = false;
    return;
  }
    
  if(this.cfg.animate)
    if(this.cfg.effect == 'fade')
      this.fade(val);
    else
      this.slide(val);
  else{
    this.setPosition(val);
    this.animating = false;
  }
};

/**
 * Fade animation for list transition.
 */
PrimeFaces.widget.Carousel.prototype.fade = function(val){
  var _self = this;
  this.list.animate(
  {
    opacity: 0
  }, 
  {
    duration: this.cfg.speed/2,
    specialEasing: {
      opacity : this.cfg.easing
    },
    complete: function() {
      _self.setPosition(val);
      $(this).animate( 
      {
        opacity: 1
      }, 
      {
        duration: _self.cfg.speed/2,
        specialEasing: {
          opacity : _self.cfg.easing
        },
        complete: function() {
          _self.animating = false;
        }
      });
    }
  });
}

/**
 * Slide animation for list transition.
 */
PrimeFaces.widget.Carousel.prototype.slide = function(val){
  var _self = this,
  ao = this.cfg.isVertical ? {
    top : val
  } : {
    left : val
  };
  
  this.list.animate( 
  ao, 
  {
    duration: this.cfg.speed,
    easing: this.cfg.easing,
    complete: function() {
      _self.animating = false;
    }
  });
}

/**
 * Next navigation of pageing.
 */
PrimeFaces.widget.Carousel.prototype.next = function(){
  return this.nextButton.disabled && !(this.animating = false) || this.go(this.page + 1);
}

/**
 * Previous navigation of pageing.
 */
PrimeFaces.widget.Carousel.prototype.prev = function(){
  return this.prevButton.disabled && !(this.animating = false) || this.go(this.page - 1);
}

/**
 * Navigation to a given page index.
 */
PrimeFaces.widget.Carousel.prototype.go = function( index ){
  if(this.cfg.isCircular)
    this.page = index > -1 ? index%(this.pageCount + 1) : this.pageCount + (index%(this.pageCount||1)) + 1;
  else{
    this.page = index;
  }
  
  this.checkButtons();
  
  //scroll
  return this.changePosition(this.getPagePosition(this.page));
}

/**
 * Look up navigation buttons for overflow constraints.
 */
PrimeFaces.widget.Carousel.prototype.checkButtons = function(){
  
  //update dropdown or nav-buttons
  if(this.navButtons){
    this.navButtons.removeClass('ui-icon-radio-on');
    this.navButtons.eq(this.page).addClass('ui-icon-radio-on');
  }
  else if(this.ddOptions){
    this.ddOptions.filter(':eq(' + this.page + ')').attr('selected', 'selected');
  }
  
  //no bound
  if(this.cfg.isCircular)
    return;
  
  //lower bound
  if(this.page == 0){
    this.prevButton.disabled = true;
    this.prevButton.addClass('ui-state-disabled');
  }
  else{
    this.prevButton.disabled = false;
    this.prevButton.removeClass('ui-state-disabled');
  }
  
  //upper bound
  if(this.page == this.pageCount){
    this.nextButton.disabled = true;
    this.nextButton.addClass('ui-state-disabled');
  }
  else{
    this.nextButton.disabled = false;
    this.nextButton.removeClass('ui-state-disabled');
  }
};