PrimeFaces.widget.AutoComplete = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = jQuery(this.jqId + '_input');
    this.jqh = jQuery(this.jqId + '_hinput');
    this.cfg.formId = this.jq.parents('form:first').attr('id');
	
    this.setupDataSource();
		
    this.jq.autocomplete(this.cfg);

    var _self = this;

    //Item select handler
    this.jq.bind('autocompleteselect', function(event, ui) {
        _self.onItemSelect(event, ui);
    });
	
    if(this.cfg.forceSelection) {
        this.setupForceSelection();
    }

    //Update hidden field value in pojo mode
    if(this.cfg.pojo) {
        this.jq.keyup(function(e) {
            if(e.keyCode != 13) {
                jQuery(_self.jqh).val(jQuery(this).val());
            }
        });
    }

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
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
            formId: _self.cfg.formId,
            onsuccess: function(responseXML) {
                var xmlDoc = responseXML.documentElement,
                updates = xmlDoc.getElementsByTagName("update");

                for(var i=0; i < updates.length; i++) {
                    var id = updates[i].attributes.getNamedItem("id").nodeValue,
                    data = updates[i].firstChild.data;

                    if(id == _self.id){
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

                    } else {
                        PrimeFaces.ajax.AjaxUtils.updateElement(id, data);
                    }
                }

                return false;
            }
        };

        var params = {};
        params[_self.id + '_query'] = request.term;

        options.params = params;

        PrimeFaces.ajax.AjaxRequest(options);
        
    };
}

PrimeFaces.widget.AutoComplete.prototype.onItemSelect = function(event, ui) {
    if(this.cfg.pojo)
        this.jqh.val(ui.item.data);
    else
        this.jq.val(ui.item.label);
	
    //Fire instant selection event
    if(this.cfg.ajaxSelect) {
        var options = {
            source: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        if(this.cfg.onSelectUpdate) {
            options.update = this.cfg.onSelectUpdate;
        }
        
        var params = {};
        params[this.id + "_ajaxSelect"] = true;

        options.params = params;

        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.AutoComplete.prototype.setupForceSelection = function() {
    var _self = this;
	
    this.jq.blur(function() {
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

PrimeFaces.widget.AutoComplete.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled').attr('disabled', 'disabled').autocomplete('disable');
}

PrimeFaces.widget.AutoComplete.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled').removeAttr('disabled').autocomplete('enable');
}

PrimeFaces.widget.AutoComplete.prototype.search = function(value) {
    this.jq.autocomplete('search', value);
}

PrimeFaces.widget.AutoComplete.prototype.close = function() {
    this.jq.autocomplete('close');
}

PrimeFaces.widget.AutoComplete.prototype.deactivate = function() {
    this.jq.autocomplete('disable');
}

PrimeFaces.widget.AutoComplete.prototype.activate = function() {
    this.jq.autocomplete('enable');
}