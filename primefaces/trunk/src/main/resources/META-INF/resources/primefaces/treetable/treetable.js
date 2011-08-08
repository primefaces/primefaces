/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
	this.cfg = cfg;
    
    this.bindEvents();
}

PrimeFaces.widget.TreeTable.prototype.bindEvents = function() {
    var _self = this;
    
    $(this.jqId + ' .ui-treetable-toggler').die('click.treetable')
        .live('click.treetable', function(e) {
            var toggler = $(this),
            node = toggler.parents('tr:first');
            
            if(toggler.hasClass('ui-icon-triangle-1-e'))
                _self.expandNode(e, node);
            else {
                _self.collapseNode(e, node);
            }
        });
}

PrimeFaces.widget.TreeTable.prototype.expandNode = function(e, node) {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    _self = this,
    nodeKey = node.attr('id').split('_node_')[1];
    
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                node.replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
            
            node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        return true;
    };
    
    var params = {};
    params[this.id + '_expand'] = nodeKey;
    
    options.params = params;
    
    if(this.hasBehavior('expand')) {
        var expandBehavior = this.cfg.behaviors['expand'];
        
        expandBehavior.call(this, e, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.TreeTable.prototype.collapseNode = function(e, node) {
    node.siblings('[id^="' + node.attr('id') + '"]').remove();

    node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
    
    if(this.hasBehavior('collapse')) {
        var collapseBehavior = this.cfg.behaviors['collapse'],
        nodeKey = node.attr('id').split('_node_')[1];
        
        var options = {
            params : {}
        };
        options.params[this.id + '_collapse'] = nodeKey;
        
        collapseBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}