/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.Tree = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    this.bindEvents(this.jq.find('.ui-tree-node-content'));
}

PrimeFaces.widget.Tree.prototype.bindEvents = function(elements) {
    var _self = this;
    
    elements.mouseover(function() {
        jQuery(this).addClass('ui-state-hover');
    })
    .mouseout(function() {
        jQuery(this).removeClass('ui-state-hover');
    }).click(function(e) {
        _self.onNodeClick(e, jQuery(this).parents('li:first'));
    });
}

PrimeFaces.widget.Tree.prototype.onNodeClick = function(e, nodeEL) {
    var target = jQuery(e.target);
    
    if(target.is("span.ui-tree-icon")) {
        if(target.hasClass('ui-icon-triangle-1-e'))
            this.expandNode(nodeEL, target);
        else
            this.collapseNode(nodeEL, target);
    }
    else {
        //todo selection
    }
}

PrimeFaces.widget.Tree.prototype.expandNode = function(nodeEL, iconEL) {
    var _self = this;

    if(this.cfg.dynamic && (!this.cfg.cache || nodeEL.children('.ui-tree-nodes').length == 0)) {

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        };

        options.onsuccess = function(responseXML) {
            var xmlDoc = responseXML.documentElement,
            updates = xmlDoc.getElementsByTagName("update");

            for(var i=0; i < updates.length; i++) {
                var id = updates[i].attributes.getNamedItem("id").nodeValue,
                content = updates[i].firstChild.data;

                if(id == _self.id){
                    nodeEL.append(content);
                    _self.bindEvents(nodeEL.children('.ui-tree-nodes').find('.ui-tree-node-content'));
 
                    iconEL.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
                    nodeEL.children('.ui-tree-nodes').show('fade', {}, 'fast');
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
                }
            }

            return false;
        };

        var params = {};
        params[this.id + '_loadNode'] = nodeEL.attr('id');

        PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
    }
    else {
        iconEL.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
        nodeEL.children('.ui-tree-nodes').show('fade', {}, 'fast');
    }
}

PrimeFaces.widget.Tree.prototype.collapseNode = function(nodeEL, iconEL) {
    var _self = this;

    iconEL.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

    nodeEL.children('.ui-tree-nodes').hide('fade', {}, 'fast', function() {
        if(_self.cfg.dynamic && !_self.cfg.cache) {
            nodeEL.children('.ui-tree-nodes').remove();
        }
    });
}