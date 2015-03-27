package net.algem.script.directory.models;

import java.io.File;
import java.util.List;

public class ScriptDirectory extends ScriptFile {
    private final File directory;
    private final List<ScriptFile> children;

    public ScriptDirectory(File directory, List<ScriptFile> children) {
        this.directory = directory;
        this.children = children;
    }

    public File getDirectory() {
        return directory;
    }

    public List<ScriptFile> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "ScriptDirectory{" +
                "directory=" + directory +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptDirectory that = (ScriptDirectory) o;

        if (children != null ? !children.equals(that.children) : that.children != null) return false;
        if (directory != null ? !directory.equals(that.directory) : that.directory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = directory != null ? directory.hashCode() : 0;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
