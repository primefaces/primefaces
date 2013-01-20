/**
 * PrimeFaces DataGrid Widget
 */
PrimeFaces.widget.DataGrid = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
        this.content = this.jqId + '_content';

        if(this.cfg.paginator) {
            this.setupPaginator();
        }
    },
    
    setupPaginator: function() {
        var _self = this;
        this.cfg.paginator.paginate = function(newState) {
            _self.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    handlePagination: function(newState) {
        var _self = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            onsuccess: function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");

                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    content = update.text();

                    if(id == _self.id){
                        $(_self.content).html(content);
                    }
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
                
                return true;
            }
        };
        
        options.oncomplete = function() {
            //update paginator state
            _self.paginator.cfg.page = newState.page;
            
            _self.paginator.updateUI();
        };

        options.params = [
            {name: this.id + '_pagination', value: true},
            {name: this.id + '_first', value: newState.first},
            {name: this.id + '_rows', value: newState.rows}
        ];

        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    getPaginator: function() {
        return this.paginator;
    }
    
});