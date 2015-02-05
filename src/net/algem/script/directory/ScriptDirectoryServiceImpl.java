package net.algem.script.directory;

import net.algem.script.Script;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptFile;
import net.algem.script.directory.models.ScriptImplFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptDirectoryServiceImpl implements ScriptDirectoryService {
    private final File rootScriptDirectory;

    public ScriptDirectoryServiceImpl(File rootScriptDirectory) {
        this.rootScriptDirectory = rootScriptDirectory;
        //TODO check directory

    }

    @Override
    public ScriptDirectory getAvailableScripts() {
        return (ScriptDirectory) getFile(rootScriptDirectory);
    }

    ScriptFile getFile(File file) {
        if (file.isDirectory()) {
            List<ScriptFile> children = new ArrayList<>();
            File[] subFiles = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".json");
                }
            });
            for (File f : subFiles) {
                children.add(getFile(f));
            }
            return new ScriptDirectory(file, children);
        } else {
            assert file.getName().endsWith(".json");
            String implFileName = file.getName().replace(".json", ".js");
            File implFile = new File(file.getParent(), implFileName);
            return new ScriptImplFile(file, implFile);
        }
    }


    @Override
    public Script loadScript(ScriptFile scriptFile) {

        return null;
    }
}
