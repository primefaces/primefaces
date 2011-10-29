PrimeFaces.widget.Paginator = function(cfg){
    this.cfg = cfg;
    this.jq = $();
    
    var _self = this;
    $.each(this.cfg.id, function(index, id){
        _self.jq = _self.jq.add($(PrimeFaces.escapeClientId(id)));
    });
    
    //elements
    this.pagesContainer = this.jq.children('.ui-paginator-pages');
    this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
    this.rppSelect = this.jq.children('.ui-paginator-rpp-options');
    this.jtpSelect = this.jq.children('.ui-paginator-jtp-select');
    this.firstLink = this.jq.children('.ui-paginator-first');
    this.prevLink  = this.jq.children('.ui-paginator-prev');
    this.nextLink  = this.jq.children('.ui-paginator-next');
    this.endLink   = this.jq.children('.ui-paginator-last');
    this.currentReport = this.jq.children('.ui-paginator-current');
    
    //metadata
    this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
    this.cfg.pageLinks = this.cfg.pageLinks||10;
    this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPage})';
    
    if(this.pageLinks.length > 0) {
        this.calculatePageLinkRange();
    }
    
    //event bindings
    this.bindEvents();
}

PrimeFaces.widget.Paginator.prototype.bindEvents = function(){
    var _self = this;
    
    //visuals for first,prev,next,last buttons
    this.jq.children('span.ui-state-default').mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled')) {
            item.addClass('ui-state-hover');
        }
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
    
    //page links
    this.bindPageLinkEvents();
    
    //records per page selection
    this.rppSelect.change(function(e){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setRowsPerPage(parseInt($(this).val()));
        }
    });
    
    //jump to page
    this.jtpSelect.change(function(e){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(parseInt($(this).val()));
        }
    });
    
    //First page link
    this.firstLink.click(function(){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(0);
        }
    });
    
    //Prev page link
    this.prevLink.click(function(){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.page - 1);
        }
    });
    
    //Next page link
    this.nextLink.click(function(){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.page + 1);
        }
    });
    
    //Last page link
    this.endLink.click(function(){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.pageCount - 1);
        }
    });
}

PrimeFaces.widget.Paginator.prototype.bindPageLinkEvents = function(){
    var _self = this;
    
    this.pagesContainer.children('.ui-paginator-page').bind('click', function(e){
        var link = $(this);

        if(!link.hasClass('ui-state-disabled')&&!link.hasClass('ui-state-active')) {
            _self.setPage(parseInt(link.text()) - 1);
        }
    }).mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled')&&!item.hasClass('ui-state-active')) {
            item.addClass('ui-state-hover');
        }
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
}

PrimeFaces.widget.Paginator.prototype.updateUI = function() {  
    //boundaries
    if(this.cfg.page == 0) {
        this.firstLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else {
        this.firstLink.removeClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-disabled');
    }
    
    if(this.cfg.page == (this.cfg.pageCount - 1)){
        this.nextLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else {
        this.nextLink.removeClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-disabled');
    }
    
    //current page report
    var text = this.cfg.currentPageTemplate.replace('{currentPage}', this.cfg.page + 1).replace('{totalPage}', this.cfg.pageCount);
    this.currentReport.text(text);
    
    //dropdowns
    this.rppSelect.attr('value', this.cfg.rows);
    this.jtpSelect.attr('value', this.cfg.page);
    
    //page links
    this.updatePageLinks();
}

PrimeFaces.widget.Paginator.prototype.updatePageLinks = function() {
    var start, end, delta;
    
    //calculate visible page links
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
    var visiblePages = Math.min(this.cfg.pageLinks, this.cfg.pageCount);

    //calculate range, keep current in middle if necessary
    start = Math.max(0, Math.ceil(this.cfg.page - ((visiblePages) / 2)));
    end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);
    
    //check when approaching to last page
    delta = this.cfg.pageLinks - (end - start + 1);
    start = Math.max(0, start - delta);

    //update dom
    this.pagesContainer.children().remove();
    for(var i = start; i <= end; i++) {
        var styleClass = 'ui-paginator-page ui-state-default ui-corner-all';
        if(this.cfg.page == i) {
            styleClass += " ui-state-active";
        }
        
        this.pagesContainer.append('<span class="' + styleClass + '">' + (i + 1) + '</span>')   
    }
    
    this.bindPageLinkEvents();
}

PrimeFaces.widget.Paginator.prototype.setPage = function(page, silent) {    
    if(page >= 0 && page < this.cfg.pageCount && this.cfg.page != page){
        this.cfg.page = page;
        
        var newState = {
            first: this.cfg.rows * (this.cfg.page),
            rows: this.cfg.rows
        };

        if(!silent) {
            this.cfg.paginate.call(this, newState);
        }
    
        this.updateUI();
    }
}

PrimeFaces.widget.Paginator.prototype.setRowsPerPage = function(rpp) {
    var first = this.cfg.rows * this.cfg.page,
    page = parseInt(first / rpp);
    
    this.cfg.rows = rpp;
    
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
    
    this.cfg.page = -1;
    this.setPage(page);
}

PrimeFaces.widget.Paginator.prototype.setTotalRecords = function(value) {
    this.cfg.rowCount = value;
    this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
    this.cfg.page = 0;
    this.updateUI();
}

PrimeFaces.widget.Paginator.prototype.getCurrentPage = function() {
    return this.cfg.page;
}