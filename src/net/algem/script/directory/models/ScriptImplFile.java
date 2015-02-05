package net.algem.script.directory.models;

import java.io.File;

public class ScriptImplFile extends ScriptFile {
    private final File manifestFile;
    private final File codeFile;

    public ScriptImplFile(File manifestFile, File codeFile) {
        this.manifestFile = manifestFile;
        this.codeFile = codeFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptImplFile that = (ScriptImplFile) o;

        if (!codeFile.equals(that.codeFile)) return false;
        if (!manifestFile.equals(that.manifestFile)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = manifestFile.hashCode();
        result = 31 * result + codeFile.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ScriptImplFile{" +
                "manifestFile=" + manifestFile +
                ", codeFile=" + codeFile +
                '}';
    }


}
