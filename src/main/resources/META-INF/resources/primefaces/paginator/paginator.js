/**
 * PrimeFaces Paginator Widget
 */
PrimeFaces.widget.Paginator = function(cfg){
    this.cfg = cfg;
    this.jq = $();
    
    var _self = this;
    $.each(this.cfg.id, function(index, id){
        _self.jq = _self.jq.add($(PrimeFaces.escapeClientId(id)));
    });
    
    this.pagesContainer = this.jq.children('.ui-paginator-pages');
    this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
    this.rppSelect = this.jq.children('.ui-paginator-rpp-options');
    this.jtpSelect = this.jq.children('.ui-paginator-jtp-select');
    this.firstLink = this.jq.children('.ui-paginator-first');
    this.prevLink  = this.jq.children('.ui-paginator-prev');
    this.nextLink  = this.jq.children('.ui-paginator-next');
    this.endLink   = this.jq.children('.ui-paginator-end');
    this.currentReport = this.jq.children('.ui-paginator-current');
    
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
    this.cfg.pageLinks = this.cfg.pageLinks||10;
    this.cfg.pageLinks = this.cfg.pageLinks > this.cfg.pageCount ? this.cfg.pageCount : this.cfg.pageLinks;
    this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPage})';
    this.bindEvents();
}

PrimeFaces.widget.Paginator.prototype.bindEvents = function(){
    var _self = this;
    
    //hover for buttons
    this.jq.find('span.ui-state-default').mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled'))
            item.addClass('ui-state-hover');
        
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
    
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
            _self.setPage(parseInt($(this).val()) - 1);
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
    
    this.pageLinks.click(function(e){
        var link = $(this);
        if(!link.hasClass('ui-state-active')){
            _self.setPage(parseInt(link.text()) - 1);
        }
    });
}

PrimeFaces.widget.Paginator.prototype.updatePageLinks = function(){
    var _self = this;
    
    //change page links dom
    
    
    this.pageLinks.removeClass('ui-state-active').each(function(index, item) {
        if(parseInt($(item).text()) - 1 == _self.cfg.page)
            $(item).addClass('ui-state-active');
    });
}

PrimeFaces.widget.Paginator.prototype.updateUI = function(){
    this.updatePageLinks();
    
    //sync dropdowns
    this.rppSelect.children('option[value=' + this.cfg.rows + ']').attr('selected', 'selected');
    this.jtpSelect.children('option[value=' + this.cfg.page + ']').attr('selected', 'selected');
    
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
    
    //TODO: Levent, use currentPageTemplate here
    var text = this.cfg.currentPageTemplate.replace('{currentPage}', this.cfg.page + 1).replace('{totalPage}', this.cfg.pageCount);
    this.currentReport.text(text);
}

PrimeFaces.widget.Paginator.prototype.setPage = function(page) {    
    if(page >= 0 && page < this.cfg.pageCount && this.cfg.page != page){
        this.cfg.page = page;
        
        var newState = {
            first: this.cfg.rows * (this.cfg.page),
            rows: this.cfg.rows
        };

        this.cfg.paginate.call(this, newState);
    
        this.updateUI();
    }
}

PrimeFaces.widget.Paginator.prototype.setRowsPerPage = function(rpp){
    var first = this.cfg.rows * this.cfg.page,
    page = parseInt(first / rpp);
    
    this.cfg.rows = rpp;
    
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
    
    this.cfg.page = -1;
    this.setPage(page);
}