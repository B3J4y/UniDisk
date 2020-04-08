package de.unidisk.crawler.simple;


import javax.swing.tree.DefaultMutableTreeNode;

/**
 * implementiert das TreeNode Interface bezogen auf Crawling
 */
public class CarlsTreeNode extends DefaultMutableTreeNode {

    private int carlsDepth;

    public CarlsTreeNode(String seed) {
        super(seed);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof DefaultMutableTreeNode)) {
            return false;
        }
        return this.getUserObject().equals(((DefaultMutableTreeNode)obj).getUserObject());
    }

    @Override
    public int hashCode() {
        return this.getUserObject().hashCode();
    }

    public void setCarlsDepth(int depth) {
        this.carlsDepth = depth;
    }

    public int getCarlsDepth() {
        return carlsDepth;
    }
}
