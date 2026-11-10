/**
 * The configuration for the {@link BaseTree} widget.
 */
export interface BaseTreeCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** `true` is the tree is animated, or `false` otherwise. */
    animate: boolean;
    /**
     * `true` if the content of dynamically loaded nodes is cached for the next time the node is expanded, or `false`
     * to always fetch the content from the server.
     */
    cache: boolean;
    /** Optional scope for the dragging and dropping, of the draggables and droppables. */
    dragdropScope: string;
    /** `true` if nodes are draggable, or `false` otherwise. */
    draggable: boolean;
    /** Defines parent-child relationship when a node is dragged. */
    dragMode: PrimeFacesTypes.widget.BaseTree.DragMode;
    /** When enabled, the copy of the selected nodes can be dropped from a tree to another tree using Shift key. */
    dropCopyNode: boolean;
    /** `true` if nodes are droppable, or `false` otherwise. */
    droppable: boolean;
    /** `true` if the content of nodes is loaded dynamically as needed, or `false` otherwise. */
    dynamic: boolean;
    /** Event for the context menu. */
    event: string;
    /** `true` if filtering is enabled, `false` otherwise. */
    filter: boolean;
    /** Mode for filtering. */
    filterMode: PrimeFacesTypes.widget.BaseTree.FilterMode;
    /** `true` if selected nodes are highlighted, or `false` otherwise. */
    highlight: boolean;
    /** A map between the type of a node and the icons for that node. */
    iconStates: Record<string, PrimeFacesTypes.widget.BaseTree.NodeIconSet>;
    /** When enabled, the selected multiple nodes can be dragged from a tree to another tree. */
    multipleDrag: boolean;
    /** Node type of nodes for which the context menu is available. */
    nodeType: string;
    /** Callback that is invoked when a node is clicked. If it returns `false`, the click on the node is ignored. */
    onNodeClick: PrimeFacesTypes.widget.BaseTree.OnNodeClickCallback;
    /** Whether toggling a node checkbox is propagated downwards. */
    propagateDown: boolean;
    /** Whether toggling a node checkbox is propagated upwards. */
    propagateUp: boolean;
    /** How the node of this tree can be selected, if selection is enabled at all. */
    selectionMode: PrimeFacesTypes.widget.BaseTree.SelectionMode;
}

/**
 * The drag & drop methods called by {@link BaseTree.expandNode} for dynamically loaded nodes. They are only
 * implemented by the {@link VerticalTree}.
 */
export interface BaseTree<Cfg extends BaseTreeCfg = BaseTreeCfg> {
    /**
     * Sets up the draggable for the given elements, implemented by the vertical tree.
     * @param elements A list of draggable nodes to set up.
     */
    makeDraggable?(elements: JQuery): void;

    /**
     * Sets up the drop points for the given elements, implemented by the vertical tree.
     * @param elements A list of drop points to set up.
     */
    makeDropPoints?(elements: JQuery): void;

    /**
     * Sets up the droppables for the droppable nodes, implemented by the vertical tree.
     * @param elements List of elements to make droppable.
     */
    makeDropNodes?(elements: JQuery): void;
}

/**
 * __PrimeFaces Base Tree Widget__
 *
 * A tree is used for displaying hierarchical data and creating a site navigation.
 */
export class BaseTree<Cfg extends BaseTreeCfg = BaseTreeCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** When multiple nodes are selected, the selected node on which the user clicked most recently. */
    cursorNode?: JQuery;
    /** DOM element of the node which is currently focused, if any. */
    focusedNode: JQuery | null = null;
    /** Target of the context menu, when a context menu is used. */
    jqTargetId?: string;
    /** List of nodes which are currently selected. Each item is the row key of a selected node. */
    selections: string[] = [];
    /** DOM element of the hidden form element that holds the list of selected nodes. */
    selectionHolder: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.highlight = (this.cfg.highlight === false) ? false : true;
        this.focusedNode = null;

        if (!this.cfg.disabled) {
            if (this.cfg.selectionMode) {
                this.initSelection();
            }

            this.bindEvents();

            this.jq.data('widget', cfg.widgetVar);
        }
    }

    /**
     * Called when this tree is initialized. Performs any setup required for enabling the selection of node.
     */
    initSelection(): void {
        this.selectionHolder = $(this.jqId + '_selection');
        const selectionsValue = this.selectionHolder.val() as string;
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if (this.cursorNode) {
            this.cursorNode = this.jq.find('.p-treenode[data-rowkey="' + CSS.escape(this.cursorNode.data('rowkey')) + '"]');
        }

        if (this.isCheckboxSelection() && this.cfg.propagateUp) {
            this.preselectCheckbox();
        }
    }

    override bindContextMenu(
        menuWidget: PrimeFacesTypes.widget.ContextMenuLikeWidget,
        targetWidget: this,
        targetId: string,
        cfg: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.ContextMenuLikeWidgetCfg>
    ): void {
        const nodeContentSelector = targetId + ' .p-tree-selectable',
            containerEvent = cfg.event + '.tree' + this.id;
        let nodeEvent = cfg.nodeType ? cfg.event + '.treenode.' + cfg.nodeType : cfg.event + '.treenode';
        nodeEvent = nodeEvent + this.id;

        $(document).off(nodeEvent, nodeContentSelector).on(nodeEvent, nodeContentSelector, null, (e) => {
            const nodeContent = $(e.currentTarget);

            if ($(e.target).is(':not(.p-tree-toggler)') && (cfg.nodeType === undefined || nodeContent.parent().data('nodetype') === cfg.nodeType)) {
                const isContextMenuDelayed = targetWidget.nodeRightClick(e, nodeContent, () => {
                    menuWidget.show(e);
                });

                if (isContextMenuDelayed) {
                    e.preventDefault();
                    e.stopPropagation();
                }
            }
        });

        $(document).off(containerEvent, targetId).on(containerEvent, targetId, null, (e) => {
            if (e.target.id == targetWidget.id && targetWidget.isEmpty()) {
                menuWidget.show(e);
            }
        });
        this.addDestroyListener(() => {
            $(document).off(nodeEvent + ' ' + containerEvent);
        });
    }

    /**
     * Expands the given node, as if the user had clicked on the `+` icon of the node. The children of the node will now
     * be visible.
     * @param node Node to expand.
     */
    expandNode(node: JQuery): void {
        if (this.cfg.dynamic) {
            if (this.cfg.cache && this.getNodeChildrenContainer(node).children().length > 0) {
                this.showNodeChildren(node);

                return;
            }

            if (node.data('processing')) {
                PrimeFaces.debug('Node is already being expanded, ignoring expand event.');
                return;
            }

            node.data('processing', true);

            const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.getId(),
                process: this.getId(),
                update: this.getId(),
                formId: this.getParentFormId(),
                params: [
                    { name: this.id + '_expandNode', value: this.getRowKey(node) }
                ],
                onsuccess: (responseXML, status, xhr) => {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            const nodeChildrenContainer = this.getNodeChildrenContainer(node);
                            nodeChildrenContainer.append(content);

                            this.showNodeChildren(node);

                            if (this.cfg.draggable) {
                                this.makeDraggable!(nodeChildrenContainer.find('.p-treenode-content'));
                            }

                            if (this.cfg.droppable) {
                                this.makeDropPoints!(nodeChildrenContainer.find('li.p-tree-droppoint'));
                                this.makeDropNodes!(nodeChildrenContainer.find('div.p-treenode-droppable'));
                            }
                        }
                    });

                    return true;
                },
                oncomplete: () => {
                    node.removeData('processing');
                }
            };

            if (this.hasBehavior('expand')) {
                this.callBehavior('expand', options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            this.showNodeChildren(node);
            this.fireExpandEvent(node);
        }
    }

    /**
     * Called when a node was expanded. Fire the appropriate event.
     * @param node The node for which to fire the event.
     */
    fireExpandEvent(node: JQuery): void {
        if (this.hasBehavior('expand')) {
            const ext = {
                params: [
                    { name: this.id + '_expandNode', value: this.getRowKey(node) }
                ]
            };

            this.callBehavior('expand', ext);
        }
    }

    /**
     * Called when a node was collapsed. Fire the appropriate event.
     * @param node The node for which to fire the event.
     */
    fireCollapseEvent(node: JQuery): void {
        if (this.hasBehavior('collapse')) {
            const ext = {
                params: [
                    { name: this.id + '_collapseNode', value: this.getRowKey(node) }
                ]
            };

            this.callBehavior('collapse', ext);
        }
    }

    /**
     * Finds the DOM element for the container which contains the child nodes of the given node.
     * @param node A node for which to get the children container.
     * @return The container with the children of the given node.
     */
    getNodeChildrenContainer(node: JQuery): JQuery {
        throw "Unsupported Operation";
    }

    /**
     * Makes the children of the given node visible. Called when a node is expanded.
     * @param node Node with children to display.
     */
    showNodeChildren(node: JQuery): void {
        throw "Unsupported Operation";
    }

    /**
     * Saves the list of currently selected nodes in a hidden form element.
     */
    writeSelections(): void {
        this.selectionHolder.val(this.selections.join(','));
    }

    /**
     * Called when a node was selected. Fire the appropriate event.
     * @param node The node for which to fire the event.
     */
    fireNodeSelectEvent(node: JQuery): void {
        if (this.isCheckboxSelection() && this.cfg.dynamic) {
            const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.getId(),
                process: this.getId()
            };

            options.params = [
                { name: this.id + '_instantSelection', value: this.getRowKey(node) }
            ];

            options.oncomplete = (_xhr, _status, args) => {
                if (args!.descendantRowKeys && args!.descendantRowKeys !== '') {
                    const rowKeys = (args!.descendantRowKeys as string).split(',');
                    for (let i = 0; i < rowKeys.length; i++) {
                        this.addToSelection(rowKeys[i]!);
                    }
                    this.writeSelections();
                }
            };

            if (this.hasBehavior('select')) {
                this.callBehavior('select', options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            if (this.hasBehavior('select')) {
                const ext = {
                    params: [
                        { name: this.id + '_instantSelection', value: this.getRowKey(node) }
                    ]
                };

                this.callBehavior('select', ext);
            }
        }
    }

    /**
     * Called when a node was unselected. Fire the appropriate event.
     * @param node The node for which to fire the event.
     */
    fireNodeUnselectEvent(node: JQuery): void {
        if (this.hasBehavior('unselect')) {
            const ext = {
                params: [
                    { name: this.id + '_instantUnselection', value: this.getRowKey(node) }
                ]
            };

            this.callBehavior('unselect', ext);
        }
    }

    /**
     * Called when a right click was performed on a node. Fire the appropriate event.
     * @param node The node for which to fire the event.
     * @param fnShowMenu Callback that is invoked once the context menu is shown.
     */
    fireContextMenuEvent(node: JQuery, fnShowMenu: () => void): void {
        if (this.hasBehavior('contextMenu')) {
            const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                params: [
                    { name: this.id + '_contextMenuNode', value: this.getRowKey(node) }
                ],
                oncomplete: () => {
                    fnShowMenu();
                }
            };

            this.callBehavior('contextMenu', ext);
        } else {
            fnShowMenu();
        }
    }

    /**
     * Finds the row key (unique ID) of the given node.
     * @param node A node for which to find the row key.
     * @return The key of the given node.
     */
    getRowKey(node: JQuery): string {
        return node.attr('data-rowkey') as string;
    }

    /**
     * Checks whether the given node is currently selected, irrespective of the current selection mode.
     * @param node A node to check.
     * @return `true` if the given node is selected, or `false` otherwise.
     */
    isNodeSelected(node: JQuery): boolean {
        return $.inArray(this.getRowKey(node), this.selections) != -1;
    }

    /**
     * Checks whether the selection mode of this tree is set to `single`.
     * @return `true` if the current selection mode is `single`, or `false` otherwise.
     */
    isSingleSelection(): boolean {
        return this.cfg.selectionMode == 'single';
    }

    /**
     * Checks whether the selection mode of this tree is set to `multiple`.
     * @return `true` if the current selection mode is `multiple`, or `false` otherwise.
     */
    isMultipleSelection(): boolean {
        return this.cfg.selectionMode == 'multiple';
    }

    /**
     * Checks whether the selection mode of this tree is set to `checkbox`.
     * @return `true` if the current selection mode is `checkbox`, or `false` otherwise.
     */
    isCheckboxSelection(): boolean {
        return this.cfg.selectionMode == 'checkbox';
    }

    /**
     * Adds the given node to the list of selected nodes.
     * @param rowKey Row key of the node to add to the selected nodes.
     */
    addToSelection(rowKey: string): void {
        if (!PrimeFaces.inArray(this.selections, rowKey)) {
            this.selections.push(rowKey);
        }
    }

    /**
     * Removes the given node from the list of currently selected nodes.
     * @param rowKey Row key of a node to to remove from the current selection.
     */
    removeFromSelection(rowKey: string): void {
        this.selections = $.grep(this.selections, (r) => {
            return r !== rowKey;
        });
    }

    /**
     * Removes all chilren of the given node from the list of currently selected nodes.
     * @param rowKey Row key of a node to process.
     * @param keptRowKeys Row keys of non-selectable descendants which, together with their own descendants, keep
     * their selection state.
     */
    removeDescendantsFromSelection(rowKey: string, keptRowKeys?: string[]): void {
        keptRowKeys = keptRowKeys || [];
        const newSelections: string[] = [];
        for (let i = 0; i < this.selections.length; i++) {
            const selection = this.selections[i]!;
            let kept = selection.indexOf(rowKey + '_') !== 0;

            for (let j = 0; !kept && j < keptRowKeys.length; j++) {
                kept = selection === keptRowKeys[j] || selection.indexOf(keptRowKeys[j] + '_') === 0;
            }

            if (kept)
                newSelections.push(selection);
        }
        this.selections = newSelections;
    }

    /**
     * Checks whether the given node can be selected, i.e. whether its `TreeNode` is selectable.
     * @param node A node to check.
     * @return `true` if the node is selectable, `false` otherwise.
     */
    isSelectableNode(node: JQuery): boolean {
        return node.children('.p-treenode-content').hasClass('p-tree-selectable');
    }

    /**
     * Toggles the checkbox of the given child nodes and their descendants when the selection propagates down. A
     * non-selectable node and its whole subtree are left untouched, just like
     * `CheckboxTreeNode#propagateSelectionDown` does on the server side, see #14798.
     * @param children Child nodes to toggle.
     * @param checked `true` if the parent node was checked before it was toggled, `false` otherwise.
     * @param skippedRowKeys Collects the row keys of the skipped non-selectable nodes.
     */
    propagateCheckboxDown(children: JQuery, checked: boolean, skippedRowKeys: string[]): void {
        children.each((_i, el) => {
            const child = $(el);
            if (!this.isSelectableNode(child)) {
                skippedRowKeys.push(this.getRowKey(child));
                return;
            }

            const checkbox = child.find('> .p-treenode-content > .p-chkbox');
            if (checkbox.length) {
                this.toggleCheckboxState(checkbox, checked);
            }
            this.propagateCheckboxDown(this.getChildNodes(child), checked, skippedRowKeys);
        });
    }

    /**
     * Finds the rendered child nodes of the given node.
     * @param node A node for which to find the children.
     * @return The child nodes of the given node.
     */
    getChildNodes(node: JQuery): JQuery {
        throw "Unsupported Operation";
    }

    /**
     * Invoked in response to a normal click on a node.
     * @param event Event of the click.
     * @param nodeContent Content of the clicked node.
     */
    nodeClick(event: JQuery.TriggeredEvent, nodeContent: JQuery): void {
        if ($(event.target).is(':not(.p-tree-toggler)')) {
            const node = nodeContent.parent(),
                selectable = nodeContent.hasClass('p-tree-selectable');

            if (this.cfg.onNodeClick) {
                const retVal = this.cfg.onNodeClick.call(this, node, event);
                if (retVal === false) {
                    return;
                }
            }

            if (selectable && this.cfg.selectionMode) {
                const selected = this.isNodeSelected(node),
                    metaKey = event.metaKey || event.ctrlKey,
                    shiftKey = event.shiftKey;

                if (this.isCheckboxSelection()) {
                    this.toggleCheckboxNode(node);
                }
                else {
                    if (selected && (metaKey)) {
                        this.unselectNode(node);
                    }
                    else {
                        if (this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
                            this.unselectAllNodes();
                        }

                        if (this.isMultipleSelection() && shiftKey && this.cursorNode && (this.cursorNode.parent().is(node.parent()))) {
                            const parentList = node.parent(),
                                treenodes = parentList.children('li.p-treenode'),
                                currentNodeIndex = treenodes.index(node),
                                cursorNodeIndex = treenodes.index(this.cursorNode),
                                startIndex = (currentNodeIndex > cursorNodeIndex) ? cursorNodeIndex : currentNodeIndex,
                                endIndex = (currentNodeIndex > cursorNodeIndex) ? (currentNodeIndex + 1) : (cursorNodeIndex + 1);

                            for (let i = startIndex; i < endIndex; i++) {
                                const treenode = treenodes.eq(i);
                                if (treenode.is(':visible')) {
                                    if (i === (endIndex - 1))
                                        this.selectNode(treenode);
                                    else
                                        this.selectNode(treenode, true);
                                }
                            }
                        }
                        else {
                            this.selectNode(node);
                            this.cursorNode = node;
                        }
                    }
                }

                if ($(event.target).is(':not(:input:enabled)')) {
                    PrimeFaces.clearSelection();
                    this.focusNode(node);
                }
            }
        }
    }

    /**
     * Invoked in response to a right click on a node.
     * @param event Event of the right click.
     * @param nodeContent Content of the clicked node.
     * @param fnShowMenu Callback that is invoked when the context menu is shown.
     * @return `true` if the context menu was opened, or `false` otherwise.
     */
    nodeRightClick(event: JQuery.TriggeredEvent, nodeContent: JQuery, fnShowMenu: () => void): boolean {
        PrimeFaces.clearSelection();

        if ($(event.target).is(':not(.p-tree-toggler)')) {
            const node = nodeContent.parent(),
                selectable = nodeContent.hasClass('p-tree-selectable');

            if (selectable && this.cfg.selectionMode) {
                const selected = this.isNodeSelected(node);
                if (!selected) {
                    if (this.isCheckboxSelection()) {
                        this.toggleCheckboxNode(node);
                    }
                    else {
                        this.unselectAllNodes();
                        this.selectNode(node, true);
                        this.cursorNode = node;
                    }
                }

                this.fireContextMenuEvent(node, fnShowMenu);
                return true;
            }
        }
        return false;
    }

    /**
     * A sub class may perform any setup related to registering event handlers in this method, such as listening to
     * mouse clicks or keyboard presses.
     */
    bindEvents(): void {
        throw "Unsupported Operation";
    }

    /**
     * This method must select the given node. When `silent` is set to `true`, no events should be triggered in response
     * to this action.
     * @param node A node of this tree to select.
     * @param silent `true` if no events should be triggered, or `false` otherwise.
     */
    selectNode(node: JQuery, silent?: boolean): void {
        throw "Unsupported Operation";
    }

    /**
     * This method must unselect the given node. When `silent` is set to `true`, no events should be triggered in
     * response to this action.
     * @param node A node of this tree to unselect.
     * @param silent `true` if no events should be triggered, or `false` otherwise.
     */
    unselectNode(node: JQuery, silent?: boolean): void {
        throw "Unsupported Operation";
    }

    /**
     * This method must unselect all nodes of this tree that are selected.
     */
    unselectAllNodes(): void {
        throw "Unsupported Operation";
    }

    /**
     * Called once during widget initialization if this tree has got nodes with selectable checkboxes.
     */
    preselectCheckbox(): void {
        throw "Unsupported Operation";
    }

    /**
     * Called when the nodes of this tree are selected via checkboxes. Must select the checkbox of the given node.
     * @param node Node with a checkbox to toggle.
     */
    toggleCheckboxNode(node: JQuery): void {
        throw "Unsupported Operation";
    }

    /**
     * Checks whether this tree is empty, that is, whether it contains any nodes.
     * @return `true` if this tree has got no nodes, or `false` otherwise.
     */
    isEmpty(): boolean {
        throw "Unsupported Operation";
    }

    /**
     * When this tree has got selectable nodes with checkboxes, checks or unchecks the given checkbox.
     * @param checkbox A checkbox of a node to check or uncheck.
     * @param checked `true` to check the given node, `false` to uncheck it.
     */
    toggleCheckboxState(checkbox: JQuery, checked: boolean): void {
        if (checked)
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    }

    /**
     * When this tree has got selectable nodes with checkboxes, partially selects the given checkbox. Does nothing
     * otherwise.
     * @param checkbox Checkbox of a node to check partially.
     */
    partialCheck(checkbox: JQuery): void {
        const box = checkbox.children('.p-chkbox-box'),
            icon = box.children('.p-chkbox-icon'),
            treeNode = checkbox.closest('.p-treenode'),
            rowKey = this.getRowKey(treeNode);

        box.removeClass('p-state-active');
        treeNode.find('> .p-treenode-content').removeClass('p-state-highlight')
            .find('> .p-treenode-label').removeClass('p-state-highlight');
        icon.removeClass('p-checkbox-checked-icon').addClass('p-checkbox-indeterminate-icon');
        treeNode.removeClass('p-treenode-selected p-treenode-unselected').addClass('p-treenode-hasselected').attr('aria-checked', 'false').attr('aria-selected', 'false');

        this.removeFromSelection(rowKey);
    }

    /**
     * When this tree has got selectable nodes with checkboxes, selects the given checkbox. Does nothing otherwise.
     * @param checkbox Checkbox of a node to check.
     */
    check(checkbox: JQuery): void {
        const box = checkbox.children('.p-chkbox-box'),
            icon = box.children('.p-chkbox-icon'),
            treeNode = checkbox.closest('.p-treenode'),
            rowKey = this.getRowKey(treeNode);

        box.addClass('p-state-active');
        icon.removeClass('p-checkbox-indeterminate-icon').addClass('p-checkbox-checked-icon');
        treeNode.removeClass('p-treenode-hasselected p-treenode-unselected').addClass('p-treenode-selected').attr('aria-checked', 'true').attr('aria-selected', 'true');

        this.addToSelection(rowKey);
    }

    /**
     * When this tree has got selectable nodes with checkboxes, unselects the given checkbox. Does nothing otherwise.
     * @param checkbox Checkbox of a node to uncheck.
     */
    uncheck(checkbox: JQuery): void {
        const box = checkbox.children('.p-chkbox-box'),
            icon = box.children('.p-chkbox-icon'),
            treeNode = checkbox.closest('.p-treenode'),
            rowKey = this.getRowKey(treeNode);

        box.removeClass('p-state-active');
        icon.removeClass('p-checkbox-indeterminate-icon p-checkbox-checked-icon');
        treeNode.removeClass('p-treenode-hasselected p-treenode-selected').addClass('p-treenode-unselected').attr('aria-checked', 'false').attr('aria-selected', 'false');

        this.removeFromSelection(rowKey);
    }

    /**
     * Checks whether the given node is currently expanded, that is, whether its children are visible.
     * @param node Node to check.
     * @return `true` if the node is expanded, or `false` otherwise.
     */
    isExpanded(node: JQuery): boolean {
        return this.getNodeChildrenContainer(node).is(':visible');
    }

    /**
     * Puts focus on the given node.
     * @param node A node on which to put focus.
     */
    focusNode(node: JQuery): void {
        throw "Unsupported Operation";
    }

}
