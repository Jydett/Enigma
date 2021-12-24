package cuchaz.enigma.gui.util;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

public class JTreeUtil {
    public static void setTreeExpandedState(JTree tree, boolean expanded) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getRoot();
        JTreeUtil.setNodeExpandedState(tree, node, expanded);
    }

    public static void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
        for (DefaultMutableTreeNode treeNode : JTreeUtil.children(node)) {
            JTreeUtil.setNodeExpandedState(tree, treeNode, expanded);
        }
        if (!expanded && node.isRoot()) {
            return;
        }
        TreePath path = new TreePath(node.getPath());
        if (expanded) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }

    public static DefaultMutableTreeNode copyNode(DefaultMutableTreeNode oldNode, Function<DefaultMutableTreeNode, DefaultMutableTreeNode> copy) {
        DefaultMutableTreeNode newNode = copy.apply(oldNode);
        for (DefaultMutableTreeNode oldChildNode : JTreeUtil.children(oldNode)) {
            DefaultMutableTreeNode newChildNode = copy.apply(oldChildNode);
            newNode.add(newChildNode);
            if (oldChildNode.isLeaf()) continue;
            JTreeUtil.copyChildrenTo(oldChildNode, newChildNode, copy);
        }
        return newNode;
    }

    public static void copyChildrenTo(DefaultMutableTreeNode from, DefaultMutableTreeNode to, Function<DefaultMutableTreeNode, DefaultMutableTreeNode> copy) {
        for (DefaultMutableTreeNode oldChildNode : JTreeUtil.children(from)) {
            DefaultMutableTreeNode newChildNode = copy.apply(oldChildNode);
            to.add(newChildNode);
            if (oldChildNode.isLeaf()) continue;
            JTreeUtil.copyChildrenTo(oldChildNode, newChildNode, copy);
        }
    }

    public static List<DefaultMutableTreeNode> children(DefaultMutableTreeNode node) {
        Enumeration<TreeNode> children = node.children();
        List<DefaultMutableTreeNode> res = new ArrayList<>(node.getChildCount());
        while (children.hasMoreElements()) {
            TreeNode treeNode = children.nextElement();
            if (treeNode instanceof DefaultMutableTreeNode t) {
                res.add(t);
            }
        }
        return res;
    }
}
