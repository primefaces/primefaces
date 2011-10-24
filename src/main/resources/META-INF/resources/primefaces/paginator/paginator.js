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
    
    this.cfg.pageCount = Math.ceil(this.cfg.totalRecords / this.cfg.rowsPerPage);
    this.cfg.pageLinks = this.cfg.pageLinks||10;
    this.cfg.pageLinks = this.cfg.pageLinks > this.cfg.pageCount ? this.cfg.pageCount : this.cfg.pageLinks;

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
    
    this.bindPageLinksEvents();
    
    //records per page selection
    this.rppSelect.change(function(e){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setRowsPerPage($(this).val());
        }
    });
    
    //jump to page
    this.jtpSelect.change(function(e){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage($(this).val());
        }
    });
    
    //First page link
    this.firstLink.click(function(){
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(1);
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
            _self.setPage(_self.cfg.pageCount);
        }
    });
}



PrimeFaces.widget.Paginator.prototype.bindPageLinksEvents = function(){
    var _self = this;
    
    this.pageLinks.click(function(e){
        var link = $(this);
        if(!link.hasClass('ui-state-active')){
            _self.setPage(parseInt(link.text()));
        }
    });
}

PrimeFaces.widget.Paginator.prototype.checkPageLinks = function(){
    var _self = this;
    
    this.pageLinks.removeClass('ui-state-active').each(function(index, item){
        
        if(index%_self.cfg.pageLinks == _self.cfg.page - 1)
            $(item).addClass('ui-state-active');
    });
}
PrimeFaces.widget.Paginator.prototype.checkLinks = function(){
    this.checkPageLinks();
    
    this.rppSelect.val(this.cfg.rowsPerPage);
    this.jtpSelect.val(this.cfg.page);
    
    if(this.cfg.page == 1){
        this.firstLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else{
        this.firstLink.removeClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-disabled');
    }
    
    if(this.cfg.page == this.cfg.pageCount){
        this.nextLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else{
        this.nextLink.removeClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-disabled');
    }
    
    this.currentReport.text('(' + this.cfg.page + ' of ' + this.cfg.pageCount + ')');
}

PrimeFaces.widget.Paginator.prototype.setPage = function(page){    
    if(page >= 0 && page < this.cfg.pageCount && this.cfg.page != page){
        this.cfg.page = page;
        this.setState();
    }
}

PrimeFaces.widget.Paginator.prototype.setRowsPerPage = function(rpp){
    if(rpp){
        var passed = this.cfg.rowsPerPage * (this.cfg.page - 1);
        var newPageCount = Math.ceil(this.cfg.totalRecords / rpp);
        this.cfg.page = passed / rpp + 1;
        this.cfg.pageCount = newPageCount;
        this.cfg.rowsPerPage = rpp;
        this.setState();
    }
}

PrimeFaces.widget.Paginator.prototype.setState = function(newState){
    if(!newState){
        
        var offset = this.cfg.rowsPerPage * (this.cfg.page - 1);
        newState = {
            recordOffset : offset,
            rowsPerPage : this.cfg.rowsPerPage,
            page : this.cfg.page,
            totalRecords : this.cfg.totalRecords,
            paginator : this,
            records : [offset, offset + this.cfg.rowsPerPage - 1]
        };
    }
    
    this.cfg.paginate.call(this.cfg.dataRoot, newState);
    
    this.checkLinks();
}