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
    this.endLink   = this.jq.children('.ui-paginator-last');
    this.currentReport = this.jq.children('.ui-paginator-current');
    
    this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
    this.cfg.pageLinks = this.cfg.pageLinks||10;
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
    
    this.pageLinks.unbind('click').bind('click', function(e){
        var link = $(this);
        if(!link.hasClass('ui-state-active')){
            _self.setPage(parseInt(link.text()) - 1);
        }
    }).mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled'))
            item.addClass('ui-state-hover');
        
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
}

PrimeFaces.widget.Paginator.prototype.updatePageLinks = function(){
    var pageCountToRender = this.cfg.pageCount < this.cfg.pageLinks ? this.cfg.pageCount : this.cfg.pageLinks,
    actualPageCount = this.pagesContainer.filter(':first').children().length,
    pageLinksCut = pageCountToRender < actualPageCount,
    pageLinksAdd = pageCountToRender > actualPageCount,
    actualJtpCount = this.jtpSelect.filter(':first').children().length,
    jtpCut = this.cfg.pageCount <= actualJtpCount,
    _self = this;
    
    //page links dom update
    if(actualPageCount != pageCountToRender ){
        if(pageLinksCut){
            this.pageLinks.each(function(index, item){
                var link = $(item),
                cursor = index%actualPageCount;

                if(cursor >= pageCountToRender)
                    link.remove();
            });
        }
        else if(pageLinksAdd){
            this.pagesContainer.each(function(index, item){
                for(var i = actualPageCount; i < pageCountToRender ; i++){
                    var newLink = $('<span class="ui-paginator-page ui-state-default ui-corner-all">'+ (i+1) +'</span>');
                    $(item).append(newLink);
                }
            });
        }
    }
    
    this.pageLinks.removeClass('ui-state-active');
    this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
    
    //shift
    if(pageCountToRender < this.cfg.pageCount||pageLinksCut||pageLinksAdd){
        var firstPageShown = parseInt(this.pageLinks.filter(':first').text()),
        lastPageShown = parseInt(this.pageLinks.filter(':last').text()),
        shiftCount = parseInt(pageCountToRender / 2),
        firstPageShould = this.cfg.page - shiftCount + 1,
        even = pageCountToRender%2 == 0,
        lastPageShould = this.cfg.page + shiftCount + 1 - (even ? 1 : 0);
    
        if((firstPageShown > 1 && firstPageShould < firstPageShown)||(lastPageShown < this.cfg.pageCount && lastPageShould > lastPageShown)){
            this.pageLinks.each(function(index, item){
                var link = $(item),
                cursor = index%pageCountToRender,
                actualPage = firstPageShould + cursor;

                if(lastPageShould > _self.cfg.pageCount)
                    actualPage = firstPageShould + cursor - shiftCount + (even ? 1 : 0);
                else if(firstPageShould < 1)
                    actualPage = cursor + 1;

                link.text(actualPage);
            });
        }
    }
    
    if(jtpCut){
        this.jtpSelect.children('option').each(function(index, item){
            var option = $(item);
            if(option.val() >= _self.cfg.pageCount)
                option.remove();
        });
    }
    else{
        this.jtpSelect.each(function(index, item){
            var parent = $(item);
            for(var i = actualJtpCount; i < _self.cfg.pageCount; i++){
                var newOption = $('<option value="' + i + '">' + (i+1) + "</option>");
                parent.append(newOption);
            }
        });
    }
    
    this.pageLinks.filter('[innerHTML="'+(this.cfg.page + 1)+'"]').addClass('ui-state-active');
    
    this.bindPageLinkEvents();
}

PrimeFaces.widget.Paginator.prototype.updateUI = function(){
    this.updatePageLinks();
    
    //update boundaries
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
    
    //update current page report
    var text = this.cfg.currentPageTemplate.replace('{currentPage}', this.cfg.page + 1).replace('{totalPage}', this.cfg.pageCount);
    this.currentReport.text(text);
    
    //sync dropdowns
    this.rppSelect.attr('value', this.cfg.rows);
    this.jtpSelect.attr('value', this.cfg.page);
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

PrimeFaces.widget.Paginator.prototype.setRowsPerPage = function(rpp){
    var first = this.cfg.rows * this.cfg.page,
    page = parseInt(first / rpp);
    
    this.cfg.rows = rpp;
    
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
    
    this.cfg.page = -1;
    this.setPage(page);
}

PrimeFaces.widget.Paginator.prototype.setTotalRecords = function(rc){
    this.cfg.rowCount = rc;
    this.cfg.pageCount = Math.ceil(rc / this.cfg.rows)||1;
    this.cfg.page = 0;
    this.updateUI();
}