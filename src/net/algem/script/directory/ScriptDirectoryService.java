package net.algem.script.directory;

import net.algem.script.common.Script;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptImplFile;

public interface ScriptDirectoryService {
    /**
     * Get the available scripts as a directory tree
     * @return the tree of available scripts
     */
    ScriptDirectory getAvailableScripts();

    /**
     * Loads a script from its script file
     * @param scriptFile the script file to load
     * @return a script object
     */
    Script loadScript(ScriptImplFile scriptFile) throws Exception;
}
