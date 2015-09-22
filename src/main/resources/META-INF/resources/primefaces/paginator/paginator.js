PrimeFaces.widget.Paginator = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
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
        this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPages})';

        //event bindings
        this.bindEvents();
    },
            
    bindEvents: function(){
        var $this = this;

        //visuals for first,prev,next,last buttons
        this.jq.children('span.ui-state-default').on('mouseover.paginator', function(){
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-focus');
            }
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });

        //page links
        this.bindPageLinkEvents();

        //records per page selection
        PrimeFaces.skinSelect(this.rppSelect);
        this.rppSelect.change(function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setRowsPerPage(parseInt($(this).val()));
            }
        });

        //jump to page
        PrimeFaces.skinSelect(this.jtpSelect);
        this.jtpSelect.change(function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(parseInt($(this).val()));
            }
        });

        //First page link
        this.firstLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(0);
            }
        });

        //Prev page link
        this.prevLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page - 1);
            }
        });

        //Next page link
        this.nextLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page + 1);
            }
        });

        //Last page link
        this.endLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.pageCount - 1);
            }
        });
    },
            
    bindPageLinkEvents: function(){
        var $this = this;

        this.pagesContainer.children('.ui-paginator-page').on('click.paginator', function(e) {
            var link = $(this);

            if(!link.hasClass('ui-state-disabled')&&!link.hasClass('ui-state-active')) {
                $this.setPage(parseInt(link.text()) - 1);
            }
        })
        .on('mouseover.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')&&!item.hasClass('ui-state-active')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });
    },
    
    updateUI: function() {  
        //boundaries
        if(this.cfg.page === 0) {
            this.disableElement(this.firstLink);
            this.disableElement(this.prevLink);
        }
        else {
            this.enableElement(this.firstLink);
            this.enableElement(this.prevLink);
        }

        if(this.cfg.page === (this.cfg.pageCount - 1)) {
            this.disableElement(this.nextLink);
            this.disableElement(this.endLink);
        }
        else {
            this.enableElement(this.nextLink);
            this.enableElement(this.endLink);
        }

        //current page report
        var startRecord = (this.cfg.rowCount === 0) ? 0 : (this.cfg.page * this.cfg.rows) + 1,
        endRecord = (this.cfg.page * this.cfg.rows) + this.cfg.rows;
        if(endRecord > this.cfg.rowCount) {
            endRecord = this.cfg.rowCount;
        }

        var text = this.cfg.currentPageTemplate
            .replace("{currentPage}", this.cfg.page + 1)
            .replace("{totalPages}", this.cfg.pageCount)
            .replace("{totalRecords}", this.cfg.rowCount)
            .replace("{startRecord}", startRecord)
            .replace("{endRecord}", endRecord);
        this.currentReport.text(text);

        //rows per page dropdown
        this.rppSelect.children('option').prop('selected', false).filter('option[value=' + this.cfg.rows + ']').prop('selected', true);

        //jump to page dropdown
        if(this.jtpSelect.length > 0) {
            this.jtpSelect.children().remove();

            for(var i=0; i < this.cfg.pageCount; i++) {
                this.jtpSelect.append("<option value=" + i + ">" + (i + 1) + "</option>");
            }
            this.jtpSelect.children('option[value=' + (this.cfg.page) + ']').prop('selected','selected');
        }

        //page links
        this.updatePageLinks();
    },
            
    updatePageLinks: function() {
        var start, end, delta,
        focusedElement = $(document.activeElement),
        focusContainer, tabindex;

        if(focusedElement.hasClass('ui-paginator-page')) {
            var pagesContainerIndex = this.pagesContainer.index(focusedElement.parent());
            if(pagesContainerIndex >= 0) {
                focusContainer = this.pagesContainer.eq(pagesContainerIndex);
                tabindex = focusedElement.index();
            }
        }

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

            this.pagesContainer.append('<span class="' + styleClass + '" tabindex="0">' + (i + 1) + '</span>')   
        }
        
        if(focusContainer) {
            focusContainer.children().eq(tabindex).trigger('focus');
        }

        this.bindPageLinkEvents();
    },
            
    setPage: function(p, silent) {
        if(p >= 0 && p < this.cfg.pageCount && this.cfg.page != p){        
            var newState = {
                first: this.cfg.rows * p,
                rows: this.cfg.rows,
                page: p
            };

            if(silent) {
                this.cfg.page = p;
                this.updateUI();
            }
            else {
                this.cfg.paginate.call(this, newState);
            }
        }
    },
            
    setRowsPerPage: function(rpp) {
        var first = this.cfg.rows * this.cfg.page,
        page = parseInt(first / rpp);

        this.cfg.rows = rpp;

        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);

        this.cfg.page = -1;
        this.setPage(page);
    },
            
    setTotalRecords: function(value) {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
        this.cfg.page = 0;
        this.updateUI();
    },
            
    getCurrentPage: function() {
        return this.cfg.page;
    },
    
    getFirst: function() {
        return (this.cfg.rows * this.cfg.page);
    },
    
    getRows: function() {
        return this.cfg.rows;
    },
            
    getContainerHeight: function(margin) {
        var height = 0;
        
        for(var i = 0; i < this.jq.length; i++) {
            height += this.jq.eq(i).outerHeight(margin);
        }
        
        return height;
    },
            
    disableElement: function(element) {
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
    },
            
    enableElement: function(element) {
        element.removeClass('ui-state-disabled').attr('tabindex', 0);
    },
    
    next: function() {
        this.setPage(this.cfg.page + 1);
    },
    
    prev: function() {
        this.setPage(this.cfg.page - 1);
    }
});
