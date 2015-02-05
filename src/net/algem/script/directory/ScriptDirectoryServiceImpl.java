package net.algem.script.directory;

import net.algem.script.common.Script;
import net.algem.script.directory.ScriptManifestParser.ScriptManifest;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptFile;
import net.algem.script.directory.models.ScriptImplFile;
import net.algem.util.IOUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ScriptDirectoryServiceImpl implements ScriptDirectoryService {
    private final File rootScriptDirectory;
    private final IOUtil.FileReaderHelper fileReader;
    private final ScriptManifestParser manifestParser;


    public ScriptDirectoryServiceImpl(File rootScriptDirectory, IOUtil.FileReaderHelper fileReader, ScriptManifestParser manifestParser) {
        this.rootScriptDirectory = rootScriptDirectory;
        //TODO check directory
        this.fileReader = fileReader;
        this.manifestParser = manifestParser;
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
                ScriptFile file1 = getFile(f);
                if (file1 != null) children.add(file1);
            }
            return new ScriptDirectory(file, children);
        } else {
            assert file.getName().endsWith(".json");
            String implFileName = file.getName().replace(".json", ".js");
            File implFile = new File(file.getParent(), implFileName);
            if (implFile.exists()) {
                return new ScriptImplFile(file, implFile);
            } else {
                return null;
            }
        }
    }


    @Override
    public Script loadScript(ScriptImplFile scriptFile) throws Exception {
        String manifestData = fileReader.readFile(scriptFile.getManifestFile());
        String name = scriptFile.getManifestFile().getName().replace(".json", "");
        ScriptManifest manifest = manifestParser.parseManifest(name, manifestData);
        String codeData = fileReader.readFile(scriptFile.getCodeFile());
        return new Script(manifest.getName(), manifest.getArguments(), manifest.getDescription(), codeData);
    }
}
