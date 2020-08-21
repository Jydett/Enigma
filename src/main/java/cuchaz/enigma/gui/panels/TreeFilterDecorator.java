package cuchaz.enigma.gui.panels;

import cuchaz.enigma.gui.node.ClassSelectorClassNode;
import cuchaz.enigma.gui.node.ClassSelectorPackageNode;
import cuchaz.enigma.gui.util.JTreeUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.BiPredicate;

public class TreeFilterDecorator {
    private final JTree tree;
    private DefaultMutableTreeNode originalRootNode;
    private BiPredicate<Object, String> userObjectMatcher;
    private JTextField filterField;

    public TreeFilterDecorator(JTree tree, BiPredicate<Object, String> userObjectMatcher, JTextField filterField) {
        this.tree = tree;
        this.filterField = filterField;
        this.originalRootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        this.userObjectMatcher = userObjectMatcher;
    }

    public static TreeFilterDecorator decorate(JTree tree, BiPredicate<Object, String> userObjectMatcher, JTextField filterField) {
        TreeFilterDecorator tfd = new TreeFilterDecorator(tree, userObjectMatcher, filterField);
        tfd.initFilterField();
        return tfd;
    }

    private void initFilterField() {
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTree();
            }
        });
        filterField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                filterField.selectAll();
            }
        });
    }

    private void filterTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        String text = filterField.getText().trim();
        if (text.equals("") && tree.getModel().getRoot() != originalRootNode) {
            model.setRoot(originalRootNode);
            JTreeUtil.setTreeExpandedState(tree, true);
        } else {
            DefaultMutableTreeNode newRootNode = matchAndBuildNode(text, originalRootNode);
            model.setRoot(newRootNode);
            JTreeUtil.setTreeExpandedState(tree, true);
        }
    }

    private DefaultMutableTreeNode matchAndBuildNode(final String text, DefaultMutableTreeNode oldNode) {
        if (!oldNode.isRoot() && userObjectMatcher.test(oldNode.getUserObject(), text)) {
            return JTreeUtil.copyNode(oldNode);
        }
        DefaultMutableTreeNode newMatchedNode;
        if (oldNode.isRoot()) {
            newMatchedNode = buildNewNode(oldNode);
        } else {
            newMatchedNode = null;
        }
        for (DefaultMutableTreeNode childOldNode : JTreeUtil.children(oldNode)) {
            DefaultMutableTreeNode newMatchedChildNode = matchAndBuildNode(text, childOldNode);
            if (newMatchedChildNode != null) {
                if (newMatchedNode == null) {
                    newMatchedNode = buildNewNode(oldNode);
                }
                newMatchedNode.add(newMatchedChildNode);
            }
        }
        return newMatchedNode;
    }

    private DefaultMutableTreeNode buildNewNode(DefaultMutableTreeNode oldNode) {
        DefaultMutableTreeNode newMatchedNode = null;
        if (oldNode instanceof ClassSelectorPackageNode) {
            newMatchedNode = new ClassSelectorPackageNode(((ClassSelectorPackageNode) oldNode).getPackageName());
        } else if (oldNode instanceof ClassSelectorClassNode) {
            newMatchedNode = new ClassSelectorClassNode(((ClassSelectorClassNode) oldNode).getObfEntry(), ((ClassSelectorClassNode) oldNode).getClassEntry());
        } else {
            newMatchedNode = new DefaultMutableTreeNode(oldNode.getUserObject());
        }
        return newMatchedNode;
    }
}