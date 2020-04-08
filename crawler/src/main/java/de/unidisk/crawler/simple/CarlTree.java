package de.unidisk.crawler.simple;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class CarlTree extends DefaultTreeModel {
    public CarlTree(TreeNode root) {
        super(root);
    }

    public void insertCarlsNodes(CarlsTreeNode parentNode, CarlsTreeNode childNode) {
        if (parentNode.equals(childNode.getRoot())) {
            //urlTree.
            insertNodeInto((CarlsTreeNode) getRoot(), childNode, ((CarlsTreeNode) getRoot()).getChildCount());
        } else {
            insertNodeInto(parentNode, childNode, parentNode.getChildCount());
        }
    }
}
