package net.algem.script.ui;

import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptFile;
import net.algem.script.directory.models.ScriptImplFile;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ScriptDirectoryTreeModel implements TreeModel {
    private final ScriptDirectory scriptDir;

    public ScriptDirectoryTreeModel(ScriptDirectory scriptDir) {
        this.scriptDir = scriptDir;
    }

    @Override
    public Object getRoot() {
        return scriptDir;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof ScriptDirectory) {
            return ((ScriptDirectory) parent).getChildren().get(index);
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof ScriptDirectory) {
            return ((ScriptDirectory) parent).getChildren().size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof ScriptImplFile;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof ScriptDirectory && child instanceof ScriptFile) {
            return ((ScriptDirectory) parent).getChildren().indexOf(child);
        } else {
            return 0;
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}
