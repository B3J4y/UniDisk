package de.unidisk.crawler.simple;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * implementiert das TreeNode Interface bezogen auf Crawling
 */
public class CarlsTreeNode extends DefaultMutableTreeNode {

    private Integer carlsDepth;

    public CarlsTreeNode(String seed) {
        super(seed);
    }

    @Override
    public boolean equals(Object obj) {
        return this.getUserObject().equals(((DefaultMutableTreeNode)obj).getUserObject());
    }

    public void setCarlsDepth(Integer depth) {
        this.carlsDepth = depth;
    }

    public Integer getCarlsDepth() {
        return carlsDepth;
    }
}
