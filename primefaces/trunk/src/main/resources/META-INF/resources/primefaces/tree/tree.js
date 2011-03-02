/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.Tree = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    if(this.cfg.selectionMode) {
        this.selectionHolder = jQuery(this.jqId + '_selection');
        this.selections = [];
    }

    this.bindEvents(this.jq.find(this.CONTENT_SELECTOR));

    if(!this.cfg.dynamic) {
        this.cookieName = this.id + '_state';
        this.restoreClientState();
    }
}

PrimeFaces.widget.Tree.prototype.HOVER_CLASS = 'ui-state-hover';
PrimeFaces.widget.Tree.prototype.SELECTED_CLASS = 'ui-state-highlight';
PrimeFaces.widget.Tree.prototype.EXPANDED_ICON_SELECTOR = 'ui-icon-triangle-1-s';
PrimeFaces.widget.Tree.prototype.COLLAPSED_ICON_SELECTOR = 'ui-icon-triangle-1-e';

PrimeFaces.widget.Tree.prototype.CONTENT_SELECTOR = '.ui-tree-node-content';
PrimeFaces.widget.Tree.prototype.CHILDREN_SELECTOR = '.ui-tree-nodes';
PrimeFaces.widget.Tree.prototype.ICON_SELECTOR = '.ui-tree-icon';
PrimeFaces.widget.Tree.prototype.SELECTED_SELECTOR = '.ui-tree-node-content.ui-state-highlight';

PrimeFaces.widget.Tree.prototype.bindEvents = function(elements) {
    var _self = this;
    
    elements.mouseover(function() {
        jQuery(this).addClass(_self.HOVER_CLASS);
    })
    .mouseout(function() {
        jQuery(this).removeClass(_self.HOVER_CLASS);
    })
    .click(function(e) {
        _self.onNodeClick(e, jQuery(this).parents('li:first'));
    });
}

PrimeFaces.widget.Tree.prototype.onNodeClick = function(e, nodeEL) {
    var target = jQuery(e.target),
    selectionMode = this.cfg.selectionMode;
    
    if(target.is(this.ICON_SELECTOR)) {
        if(target.hasClass(this.COLLAPSED_ICON_SELECTOR))
            this.expandNode(nodeEL);
        else
            this.collapseNode(nodeEL);
    }
    else if(selectionMode) {
        if(this.isNodeSelected(nodeEL))
            this.unselectNode(nodeEL);
        else
            this.selectNode(nodeEL);

        if(this.cfg.instantSelect) {
            this.fireNodeSelectEvent(nodeEL);
        }
    }
}

PrimeFaces.widget.Tree.prototype.expandNode = function(node) {
    var _self = this;

    if(this.cfg.dynamic) {

        if(this.cfg.cache && node.children(this.CHILDREN_SELECTOR).length > 0) {
            this.showNodeChildren(node, true);
            
            return;
        }

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
                    node.append(content);
                    _self.bindEvents(node.children(_self.CHILDREN_SELECTOR).find(_self.CONTENT_SELECTOR));
 
                    _self.showNodeChildren(node, true);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
                }
            }

            return false;
        };

        var params = {};
        params[this.id + '_loadNode'] = _self.getNodeId(node);

        PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
    }
    else {
        this.showNodeChildren(node, true);
        this.saveClientState();
    }
}

PrimeFaces.widget.Tree.prototype.collapseNode = function(node) {
    var _self = this,
    icon = node.find(this.ICON_SELECTOR + ':first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass(this.COLLAPSED_ICON_SELECTOR).removeClass(this.EXPANDED_ICON_SELECTOR);

    if(iconState) {
        nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
    }

    node.children(this.CHILDREN_SELECTOR).hide('fade', {}, 'fast', function() {
        if(_self.cfg.dynamic) {
            if(!_self.cfg.cache)
                jQuery(this).remove();
        }
        else {
            _self.saveClientState();1
        }
    });
}

PrimeFaces.widget.Tree.prototype.showNodeChildren = function(node, animate) {
    var icon = node.find(this.ICON_SELECTOR + ':first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass(this.EXPANDED_ICON_SELECTOR).removeClass(this.COLLAPSED_ICON_SELECTOR);

    if(iconState) {
        nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
    }

    if(animate)
        node.children(this.CHILDREN_SELECTOR).show('fade', {}, 'fast');
    else
        node.children(this.CHILDREN_SELECTOR).show();
}

PrimeFaces.widget.Tree.prototype.saveClientState = function() {
    var _self = this,
    expandedNodes = [];
    
    jQuery(this.jq).find('li').each(function() {
        var node = jQuery(this),
        icon = node.find(_self.ICON_SELECTOR + ':first');

        if(icon.hasClass(_self.EXPANDED_ICON_SELECTOR)) {
            expandedNodes.push(node.attr('id'));
        }
    });

    PrimeFaces.setCookie(this.cookieName, expandedNodes.join(','));
}

PrimeFaces.widget.Tree.prototype.restoreClientState = function() {
    var expandedNodes = PrimeFaces.getCookie(this.cookieName);
    
    if(expandedNodes) {
        expandedNodes = expandedNodes.split(',');

        for(var i in expandedNodes) {
            var node = jQuery(PrimeFaces.escapeClientId(expandedNodes[i]));

            this.showNodeChildren(node, false);
        }
    }
}

PrimeFaces.widget.Tree.prototype.selectNode = function(node) {

    if(this.isSingleSelection()) {
        //clean all selections
        this.selections = [];
        this.jq.find(this.SELECTED_SELECTOR).removeClass(this.SELECTED_CLASS);
    }

    //select node
    node.find(this.CONTENT_SELECTOR + ':first').addClass(this.SELECTED_CLASS);
    this.selections.push(this.getNodeId(node));

    this.writeSelections();
}

PrimeFaces.widget.Tree.prototype.unselectNode = function(node) {
    var nodeId = this.getNodeId(node);

    node.find(this.CONTENT_SELECTOR + ':first').removeClass(this.SELECTED_CLASS);
   
    //remove from selection
    this.selections = jQuery.grep(this.selections, function(r) {
        return r != nodeId;
    });

    this.writeSelections();
}

PrimeFaces.widget.Tree.prototype.writeSelections = function() {    
    this.selectionHolder.val(this.selections.join(','));
}

PrimeFaces.widget.Tree.prototype.fireNodeSelectEvent = function(node) {
    
}

PrimeFaces.widget.Tree.prototype.getNodeId = function(node) {
    return node.attr('id').split('_node_')[1];
}

PrimeFaces.widget.Tree.prototype.isNodeSelected = function(node) {
    return jQuery.inArray(this.getNodeId(node), this.selections) != -1;
}

PrimeFaces.widget.Tree.prototype.isSingleSelection = function(node) {
    return this.cfg.selectionMode == 'single';
}