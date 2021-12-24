package cuchaz.enigma.gui.panels;

import cuchaz.enigma.gui.node.ClassSelectorClassNode;
import cuchaz.enigma.gui.node.ClassSelectorPackageNode;
import cuchaz.enigma.gui.util.JTreeUtil;

import javax.swing.JTextField;
import javax.swing.JTree;
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
        this.originalRootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();
        this.userObjectMatcher = userObjectMatcher;
    }

    public static TreeFilterDecorator decorate(JTree tree, BiPredicate<Object, String> userObjectMatcher, JTextField filterField) {
        TreeFilterDecorator tfd = new TreeFilterDecorator(tree, userObjectMatcher, filterField);
        tfd.initFilterField();
        return tfd;
    }

    private void initFilterField() {
        this.filterField.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                TreeFilterDecorator.this.filterTree();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TreeFilterDecorator.this.filterTree();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TreeFilterDecorator.this.filterTree();
            }
        });
        this.filterField.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e) {
                TreeFilterDecorator.this.filterField.selectAll();
            }
        });
    }

    private void filterTree() {
        DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
        String text = this.filterField.getText().trim();
        DefaultMutableTreeNode newRootNode;
        if (text.equals("") && this.tree.getModel().getRoot() != this.originalRootNode) {
            newRootNode = this.originalRootNode;
        } else {
            newRootNode = this.matchAndBuildNode(text, this.originalRootNode);
        }
        model.setRoot(newRootNode);
        JTreeUtil.setTreeExpandedState(this.tree, true);
    }

    private DefaultMutableTreeNode matchAndBuildNode(String text, DefaultMutableTreeNode oldNode) {
        if (!oldNode.isRoot() && this.userObjectMatcher.test(oldNode.getUserObject(), text)) {
            return JTreeUtil.copyNode(oldNode, TreeFilterDecorator::buildNewNode);
        }
        DefaultMutableTreeNode newMatchedNode = oldNode.isRoot() ? buildNewNode(oldNode) : null;
        for (DefaultMutableTreeNode childOldNode : JTreeUtil.children(oldNode)) {
            DefaultMutableTreeNode newMatchedChildNode = this.matchAndBuildNode(text, childOldNode);
            if (newMatchedChildNode == null) continue;
            if (newMatchedNode == null) {
                newMatchedNode = buildNewNode(oldNode);
            }
            newMatchedNode.add(newMatchedChildNode);
        }
        return newMatchedNode;
    }

    public static DefaultMutableTreeNode buildNewNode(DefaultMutableTreeNode oldNode) {
        DefaultMutableTreeNode newMatchedNode = null;
        if (oldNode instanceof ClassSelectorPackageNode packageNode) {
            newMatchedNode = new ClassSelectorPackageNode(packageNode.getPackageName());
        } else {
            if (oldNode instanceof ClassSelectorClassNode classSelectorClassNode) {
                newMatchedNode = new ClassSelectorClassNode(classSelectorClassNode.getObfEntry(), classSelectorClassNode.getClassEntry());
            } else {
                newMatchedNode = new DefaultMutableTreeNode(oldNode.getUserObject());
            }
        }
        return newMatchedNode;
    }
}
