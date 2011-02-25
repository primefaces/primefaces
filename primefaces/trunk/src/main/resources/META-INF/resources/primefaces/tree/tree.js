/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.Tree = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    this.bindEvents();
}

PrimeFaces.widget.Tree.prototype.bindEvents = function() {
    var _self = this;
    
    this.jq.find('.ui-tree-node-content').mouseover(function() {
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
    iconEL.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');

    nodeEL.children('.ui-tree-nodes').show('fade', {}, 'fast');
}

PrimeFaces.widget.Tree.prototype.collapseNode = function(nodeEL, iconEL) {
    iconEL.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

    nodeEL.children('.ui-tree-nodes').hide('fade', {}, 'fast');
}