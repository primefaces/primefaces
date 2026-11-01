/**
 * __PrimeFaces Horizontal Tree Widget__
 * 
 * Tree is used for displaying hierarchical data and creating a site navigation. This implements a horizontal tree.
 * 
 * @interface {PrimeFaces.widget.HorizontalTreeCfg} cfg The configuration for the
 * {@link  HorizontalTree| HorizontalTree widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseTreeCfg} cfg
 */
PrimeFaces.widget.HorizontalTree = class HorizontalTree extends PrimeFaces.widget.BaseTree {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindEvents() {
        var $this = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = '.p-tree-toggler',
        nodeContentSelector = '.p-treenode-content.p-tree-selectable';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function() {
                        var icon = $(this),
                        node = icon.closest('td.p-treenode');

                        if(node.hasClass('p-treenode-collapsed'))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });

        if(selectionMode && this.cfg.highlight) {
            this.jq.off('mouseenter.tree mouseleave.tree', nodeContentSelector)
                        .on('mouseenter.tree', nodeContentSelector, null, function() {
                            $(this).addClass('p-state-hover');
                        })
                        .on('mouseleave.tree', nodeContentSelector, null, function() {
                            $(this).removeClass('p-state-hover');
                        });
        }

        if(this.isCheckboxSelection()) {
            var checkboxSelector = '.p-chkbox-box:not(.p-state-disabled)';

            this.jq.off('mouseleave.tree-checkbox mouseenter.tree-checkbox', checkboxSelector)
                        .on('mouseleave.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('p-state-hover');
                        })
                        .on('mouseenter.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('p-state-hover');
                        });
        }

        this.jq.off('click.tree-content', nodeContentSelector)
                .on('click.tree-content', nodeContentSelector, null, function(e) {
                    $this.nodeClick(e, $(this));
                });

    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
    showNodeChildren(node) {
        node.attr('aria-expanded', true);

        var childrenContainer = node.next(),
        toggleIcon = node.find('> .p-treenode-content > .p-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];

        if(iconState) {
            toggleIcon.nextAll('span.p-treenode-icon').removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        node.removeClass('p-treenode-collapsed');
        childrenContainer.show();
    }

    /**
     * Collapses the given node, as if the user had clicked on the `-` icon of the node. The children of the node will
     * now be visible. 
     * @param {JQuery} node Node to collapse. 
     */
    collapseNode(node) {
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .p-treenode-content > .p-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];

        //aria
        node.attr('aria-expanded', false);

        if(iconState) {
            toggleIcon.nextAll('span.p-treenode-icon').removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }

        node.addClass('p-treenode-collapsed');
        childrenContainer.hide();

        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.children('.p-treenode-children').empty();
        }

        if(!this.cfg.cache) {
            this.fireCollapseEvent(node);
        }
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     * @return {JQuery}
     */
    getNodeChildrenContainer(node) {
        return node.next('.p-treenode-children-container').children('.p-treenode-children');
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    selectNode(node, silent) {
        node.removeClass('p-treenode-unselected').addClass('p-treenode-selected').children('.p-treenode-content').addClass('p-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    unselectNode(node, silent) {
        var rowKey = this.getRowKey(node);

        node.removeClass('p-treenode-selected').addClass('p-treenode-unselected').children('.p-treenode-content').removeClass('p-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    }

    /**
     * @override
     * @inheritdoc
     */
    unselectAllNodes() {
        this.selections = [];
        this.jq.find('.p-treenode-content.p-state-highlight').each(function() {
            $(this).removeClass('p-state-highlight').closest('.p-treenode').attr('aria-selected', false);
        });
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    preselectCheckbox() {
        var _self = this;

        this.jq.find('.p-chkbox-icon').not('.p-checkbox-checked-icon').each(function() {
            var icon = $(this),
            node = icon.closest('.p-treenode'),
            childrenContainer = _self.getNodeChildrenContainer(node);

            if(childrenContainer.find('.p-chkbox-icon.p-checkbox-checked-icon').length > 0) {
                icon.addClass('p-checkbox-indeterminate-icon');
            }
        });
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
    toggleCheckboxNode(node) {
        var $this = this,
        checkbox = node.find('> .p-treenode-content > .p-chkbox'),
        checked = checkbox.find('> .p-chkbox-box > .p-chkbox-icon').hasClass('p-checkbox-checked-icon');

        this.toggleCheckboxState(checkbox, checked);

        if(this.cfg.propagateDown) {
            var skippedRowKeys = [];
            this.propagateCheckboxDown(this.getChildNodes(node), checked, skippedRowKeys);

            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'), skippedRowKeys);
            }
        }

        if(this.cfg.propagateUp) {
            node.parents('td.p-treenode-children-container').each(function() {
                var childrenContainer = $(this),
                parentNode = childrenContainer.prev('.p-treenode-parent'),
                parentsCheckbox = parentNode.find('> .p-treenode-content > .p-chkbox'),
                children = childrenContainer.find('> .p-treenode-children > table > tbody > tr > td.p-treenode').filter(function() {
                    // a non-selectable child can never be selected via its checkbox, so it must not keep its parent
                    // from being fully selected, see #14798
                    return $this.isSelectableNode($(this));
                });

                // a non-selectable node must never be selected by the propagation either, and without any selectable
                // child there is nothing to derive the state of the node from, see #14798
                if(!$this.isSelectableNode(parentNode) || !children.length) {
                    return;
                }

                if(checked) {
                    if(children.filter('.p-treenode-unselected').length === children.length)
                        $this.uncheck(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.p-treenode-selected').length === children.length)
                        $this.check(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
            });
        }

        this.writeSelections();

        if(checked)
            this.fireNodeUnselectEvent(node);
        else
            this.fireNodeSelectEvent(node);
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     * @return {JQuery}
     */
    getChildNodes(node) {
        return node.next('.p-treenode-children-container')
                .find('> .p-treenode-children > table > tbody > tr > td.p-treenode');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    check(checkbox) {
        super.check(checkbox);
        checkbox.parent('.p-treenode-content').addClass('p-state-highlight');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    uncheck(checkbox) {
        super.uncheck(checkbox);
        checkbox.parent('.p-treenode-content').removeClass('p-state-highlight');
    }

    /**
     * @override
     * @inheritdoc
     * @return {boolean}
     */
    isEmpty() {
        return this.jq.children('table').length === 0;
    }

    /**
     * This implementation does nothing, focus is not supported in horizontal mode.
     * @override
     * @inheritdoc
     * @protected
     * @param {JQuery} node
     */
    focusNode(node) {
        //focus not supported in horizontal mode
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    partialCheck(checkbox) {
        var box = checkbox.children('.p-chkbox-box'),
        icon = box.children('.p-chkbox-icon'),
        treeNode = checkbox.closest('.p-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('p-state-active');
        treeNode.find('> .p-treenode-content').removeClass('p-state-highlight');
        icon.removeClass('p-checkbox-checked-icon').addClass('p-checkbox-indeterminate-icon');
        treeNode.removeClass('p-treenode-selected p-treenode-unselected').addClass('p-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
     }

}