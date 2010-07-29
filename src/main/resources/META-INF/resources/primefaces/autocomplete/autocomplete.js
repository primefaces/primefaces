PrimeFaces.widget.AutoComplete = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = this.jqId + '_input';
    this.jqh = this.jqId + '_hinput';
	
    this.setupDataSource();
		
    jQuery(this.jq).autocomplete(this.cfg);
	
    jQuery(this.jq).bind('autocompleteselect', {ac: this}, this.onSelect);
	
    if(this.cfg.forceSelection) {
        this.setupForceSelection();
    }

    //update hidden field value in pojo mode
    var _self = this;
    if(this.cfg.pojo) {
        jQuery(this.jq).keyup(function() {
            jQuery(_self.jqh).val(jQuery(this).val());
        });
    }
}

PrimeFaces.widget.AutoComplete.prototype.setupDataSource = function() {
    var _self = this;
	
    this.cfg.source = function(request, response) {
        //start callback
        if(_self.cfg.onstart) {
            _self.cfg.onstart.call(_self, request);
        }

        var options = {
            source: _self.id,
            process: _self.id,
            update: _self.id,
            formId: _self.cfg.id,
            onsuccess: function(responseXML) {
                var xmlDoc = responseXML.documentElement,
                updates = xmlDoc.getElementsByTagName("update");

                for(var i=0; i < updates.length; i++) {
                    var id = updates[i].attributes.getNamedItem("id").nodeValue,
                    data = updates[i].firstChild.data;

                    if(id == PrimeFaces.VIEW_STATE) {
                        PrimeFaces.ajax.AjaxUtils.updateState(data);
                    }
                    else if(id == _self.id){
                        var results = jQuery.parseJSON(data).results;

                        //complete callback
                        if(_self.cfg.oncomplete) {
                            _self.cfg.oncomplete.call(_self, results);
                        }

                        //max results
                        if(_self.cfg.maxResults < results.length) {
                            results = results.slice(0, _self.cfg.maxResults);
                        }

                        //force selection
                        if(_self.cfg.forceSelection) {
                            _self.cachedResults = results;
                        }

                        response(results);
                       
                    }
                    else {
                        jQuery(PrimeFaces.escapeClientId(id)).replaceWith(data);
                    }
                }

                return false;
            }
        };

        var params = {};
        params[_self.id + '_query'] = request.term;

        PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
        
    };
}

PrimeFaces.widget.AutoComplete.prototype.onSelect = function(event, ui) {
    var _self = event.data.ac;
	
    if(_self.cfg.pojo)
        jQuery(_self.jqh).val(ui.item.data);
    else
        jQuery(_self.jq).val(ui.item.label);
	
    //Fire instant selection event
    if(_self.cfg.ajaxSelect) {
        var options = {
            source: _self.id,
            process: _self.id,
            formId: _self.cfg.formId
        };

        if(_self.cfg.onSelectUpdate) {
            options.update = _self.cfg.onSelectUpdate;
        }
        
        var params = {};
        params[_self.id + "_ajaxSelect"] = true;

        PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
    }
}

PrimeFaces.widget.AutoComplete.prototype.setupForceSelection = function() {
    var _self = this;
	
    jQuery(this.jq).blur(function() {
        var value = jQuery(this).val(),
        valid = false;
		
        if(_self.cachedResults) {
            for(var i = 0; i < _self.cachedResults.length; i++) {
                if(_self.cachedResults[i].label == value) {
                    valid = true;
                    break;
                }
            }
        }
		
        if(!valid) {
            jQuery(this).val('');
        }
    });
}