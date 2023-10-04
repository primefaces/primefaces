package org.primefaces.component.treetable.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class TreeTableFilterFeatureTest {

    @Test
    public void testCloneTreeNodeWithCustomNodeConstructor() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode root = new DefaultTreeNode();
        DefaultTreeNode cloneRoot = new DefaultTreeNode();
        CustomNode<String> customNode = new CustomNode<>("Type", "Name", root);
        TreeTable treeTable = new TreeTable();
        treeTable.setCloneOnFilter(true);

        TreeNode cloneCustomNode = filterFeature.cloneTreeNode(treeTable, customNode, cloneRoot);

        Assertions.assertEquals(1, cloneRoot.getChildren().size());
        Assertions.assertEquals(cloneCustomNode, cloneRoot.getChildren().get(0));
    }
}
